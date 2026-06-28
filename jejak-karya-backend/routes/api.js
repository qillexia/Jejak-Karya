const express = require('express');
const router = express.Router();
const axios = require('axios');

const metApiBaseUrl = process.env.MET_API_BASE_URL || "https://collectionapi.metmuseum.org/public/collection/v1";

// Simple in-memory cache
const searchCache = new Map();
const objectCache = new Map();
const CACHE_TTL = 1000 * 60 * 60 * 24; // 24 jam

// Global Semaphore Queue untuk mencegah 403 sama sekali
class RequestQueue {
    constructor(concurrency = 1) {
        this.concurrency = concurrency;
        this.running = 0;
        this.queue = [];
    }
    
    async enqueue(task) {
        if (this.running >= this.concurrency) {
            await new Promise(resolve => this.queue.push(resolve));
        }
        this.running++;
        try {
            return await task();
        } finally {
            this.running--;
            if (this.queue.length > 0) {
                const next = this.queue.shift();
                next();
            }
        }
    }
}

// Batasi 3 request ke The Met API pada satu waktu
const metQueue = new RequestQueue(3);

// Helper function untuk fetch dengan mekanisme Queue, Retry & Delay wajib
const fetchWithRetry = async (url, retries = 3, baseDelay = 1500) => {
    return metQueue.enqueue(async () => {
        for (let i = 0; i < retries; i++) {
            try {
                const response = await axios.get(url, {
                    headers: { 'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36' }
                });
                return response;
            } catch (error) {
                if (i === retries - 1) throw error;
                if (error.response && (error.response.status === 403 || error.response.status === 429)) {
                    const delay = baseDelay * (i + 1);
                    console.warn(`[RETRY] ${url} => ke-${i + 1} dalam ${delay}ms`);
                    await new Promise(res => setTimeout(res, delay));
                } else {
                    throw error;
                }
            }
        }
    });
};

// Fetch TANPA queue (langsung) — khusus untuk endpoint /api/artworks yang sudah kontrol batch sendiri
const fetchDirect = async (url, retries = 2) => {
    for (let i = 0; i < retries; i++) {
        try {
            return await axios.get(url, {
                headers: { 'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36' },
                timeout: 5000
            });
        } catch (error) {
            if (i === retries - 1) throw error;
            if (error.response && (error.response.status === 403 || error.response.status === 429)) {
                await new Promise(res => setTimeout(res, 1000 * (i + 1)));
            } else {
                throw error;
            }
        }
    }
};

/* GET /api (Sekadar menarik data dari The Met API) */
router.get('/', async (req, res) => {
    try {
        const query = req.query.search || "indonesia";
        
        // Cek Cache
        if (searchCache.has(query)) {
            const cached = searchCache.get(query);
            if (Date.now() - cached.timestamp < CACHE_TTL) {
                return res.status(200).json({ success: true, data: cached.data });
            }
        }
        
        const response = await fetchWithRetry(`${metApiBaseUrl}/search?hasImages=true&isPublicDomain=true&q=${query}`);
        
        // Simpan ke Cache
        searchCache.set(query, { data: response.data, timestamp: Date.now() });

        res.status(200).json({ success: true, data: response.data });
    } catch (error) {
        console.error("[ERROR] Gagal memuat data dari API:", error.message);
        res.status(500).json({ success: false, error: error.message });
    }
});

/* GET /api/object/:id (Menarik detail dan gambar spesifik dari sebuah ID) */
router.get('/object/:id', async (req, res) => {
    try {
        const objectId = req.params.id;
        
        // Cek Cache
        if (objectCache.has(objectId)) {
            const cached = objectCache.get(objectId);
            if (Date.now() - cached.timestamp < CACHE_TTL) {
                return res.status(200).json({ success: true, data: cached.data });
            }
        }
        
        const response = await fetchWithRetry(`${metApiBaseUrl}/objects/${objectId}`);
        
        // Simpan ke Cache
        objectCache.set(objectId, { data: response.data, timestamp: Date.now() });

        res.status(200).json({ success: true, data: response.data });
    } catch (error) {
        console.error(`[ERROR] Gagal memuat detail untuk ID ${req.params.id}:`, error.message);
        res.status(500).json({ success: false, error: error.message });
    }
});

// Cache khusus untuk endpoint /api/artworks (menyimpan hasil akhir yang sudah difilter)
const artworksCache = new Map();
const ARTWORKS_CACHE_TTL = 1000 * 60 * 60; // 1 jam (lebih pendek agar data segar)

/* GET /api/artworks?search=Painting&count=8
   Endpoint PINTAR: Backend yang mengurus SEMUA pekerjaan berat.
   - Cari ID berdasarkan query
   - Ambil detail SATU PER SATU (anti 403!)
   - Filter hanya yang punya gambar
   - Kembalikan tepat sejumlah 'count' yang diminta
*/
router.get('/artworks', async (req, res) => {
    try {
        const query = req.query.search || "Sunflowers";
        const count = parseInt(req.query.count) || 8;
        const forceRefresh = req.query.refresh === 'true';
        const cacheKey = `${query}_${count}`;
        
        // Cek Cache (kecuali jika force refresh)
        if (!forceRefresh && artworksCache.has(cacheKey)) {
            const cached = artworksCache.get(cacheKey);
            if (Date.now() - cached.timestamp < ARTWORKS_CACHE_TTL) {
                console.log(`[CACHE HIT] /api/artworks?search=${query} => ${cached.data.length} karya seni`);
                return res.status(200).json({ success: true, data: cached.data });
            }
        }
        
        console.log(`[FETCH] Mencari ${count} karya seni untuk query "${query}"...`);
        
        // 1. Cari semua ID yang cocok
        const searchResponse = await fetchWithRetry(
            `${metApiBaseUrl}/search?hasImages=true&isPublicDomain=true&q=${query}`
        );
        
        const allIds = searchResponse.data.objectIDs;
        if (!allIds || allIds.length === 0) {
            return res.status(200).json({ success: true, data: [] });
        }
        
        // 2. Acak dan ambil kandidat (pool besar agar pasti dapat 8)
        const shuffled = allIds.sort(() => Math.random() - 0.5);
        const candidates = shuffled.slice(0, count * 8);
        
        // 3. Ambil detail dalam BATCH PARALEL (5 sekaligus) — TANPA queue agar super cepat!
        const BATCH_SIZE = 5;
        const results = [];
        
        for (let i = 0; i < candidates.length && results.length < count; i += BATCH_SIZE) {
            const batch = candidates.slice(i, i + BATCH_SIZE);
            
            const batchResults = await Promise.allSettled(
                batch.map(async (id) => {
                    // Cek object cache dulu
                    if (objectCache.has(String(id))) {
                        const cached = objectCache.get(String(id));
                        if (Date.now() - cached.timestamp < CACHE_TTL) {
                            return cached.data;
                        }
                    }
                    const detail = await fetchDirect(`${metApiBaseUrl}/objects/${id}`);
                    objectCache.set(String(id), { data: detail.data, timestamp: Date.now() });
                    return detail.data;
                })
            );
            
            for (const result of batchResults) {
                if (results.length >= count) break;
                if (result.status === 'fulfilled' && result.value) {
                    const data = result.value;
                    if (data.primaryImageSmall || data.primaryImage) {
                        results.push(data);
                    }
                }
            }
        }
        
        console.log(`[DONE] ${results.length}/${count} karya seni untuk "${query}"`);
        
        // 4. Simpan ke cache
        artworksCache.set(cacheKey, { data: results, timestamp: Date.now() });
        
        res.status(200).json({ success: true, data: results });
    } catch (error) {
        console.error(`[ERROR] /api/artworks gagal:`, error.message);
        res.status(500).json({ success: false, error: error.message });
    }
});

module.exports = router;

