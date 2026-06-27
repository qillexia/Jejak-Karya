const express = require('express');
const router = express.Router();
const axios = require('axios');

const metApiBaseUrl = process.env.MET_API_BASE_URL;

/* GET /api (Sekadar menarik data dari The Met API) */
router.get('/', async (req, res) => {
    try {
        // Cukup ambil query dari parameter atau gunakan 'indonesia' sebagai tes standar
        const query = req.query.search || "indonesia";
        
        // Proxy request langsung ke The Met API (dengan filter Public Domain agar gambar dijamin 100% ada)
        const response = await axios.get(`${metApiBaseUrl}/search?hasImages=true&isPublicDomain=true&q=${query}`);
        
        // Langsung kembalikan respons mentah (raw) ke frontend/browser
        res.status(200).json({
            success: true,
            data: response.data
        });

    } catch (error) {
        console.error("[ERROR] Gagal memuat data dari API:", error.message);
        res.status(500).json({ success: false, error: error.message });
    }
});
/* GET /api/object/:id (Menarik detail dan gambar spesifik dari sebuah ID) */
router.get('/object/:id', async (req, res) => {
    try {
        const objectId = req.params.id;
        
        // Proxy request untuk detail objek ke The Met API
        const response = await axios.get(`${metApiBaseUrl}/objects/${objectId}`);
        
        // Langsung kembalikan respons mentah (raw) ke frontend/browser
        res.status(200).json({
            success: true,
            data: response.data
        });

    } catch (error) {
        console.error(`[ERROR] Gagal memuat detail untuk ID ${req.params.id}:`, error.message);
        res.status(500).json({ success: false, error: error.message });
    }
});

module.exports = router;
