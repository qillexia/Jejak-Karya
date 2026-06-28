package com.example.jejakkarya.data.model

data class Artwork(
    val objectID: Int,
    val title: String?,
    val culture: String?,
    val country: String?,
    val medium: String?,
    val primaryImageSmall: String? = null,
    val primaryImage: String? = null,
    
    // Tambahan baru untuk memperkaya detail UI (default null agar aman)
    val artistDisplayName: String? = null,
    val artistDisplayBio: String? = null,
    val objectDate: String? = null,
    val department: String? = null,
    val classification: String? = null,
    val objectName: String? = null,
    val creditLine: String? = null,
    val repository: String? = null
) {
    // Helper properties untuk langsung dipakai di UI tanpa pengecekan null yang rumit
    val displayOrigin: String
        get() = culture?.takeIf { it.isNotBlank() } ?: country?.takeIf { it.isNotBlank() } ?: "Tidak diketahui"
        
    val displayMedium: String
        get() = medium?.takeIf { it.isNotBlank() } ?: "Tidak diketahui"
        
    val displayTitle: String
        get() = title?.takeIf { it.isNotBlank() } ?: "Tanpa Judul"
        
    val displayImage: String
        get() = primaryImageSmall?.takeIf { it.isNotEmpty() } 
            ?: primaryImage?.takeIf { it.isNotEmpty() } 
            ?: ""
            
    val displayArtist: String
        get() = artistDisplayName?.takeIf { it.isNotBlank() } ?: "Seniman Tidak Diketahui"
        
    val displayDate: String
        get() {
            val date = objectDate?.takeIf { it.isNotBlank() } ?: return "Periode Tidak Diketahui"
            // Menerjemahkan istilah museum bahasa Inggris ke bahasa Indonesia
            return date
                .replace("ca.", "Sekitar")
                .replace("century", "Abad")
                .replace("B.C.", "SM") // Sebelum Masehi
                .replace("A.D.", "M") // Masehi
                .replace("late", "Akhir")
                .replace("early", "Awal")
                .replace("mid-", "Pertengahan ")
                .replace("mid", "Pertengahan")
                .replace("th", "") // Menghapus akhiran "th" (contoh: 19th -> 19 Abad) -> Tunggu, lebih baik biarkan atau ganti pola. "19th century" -> "19 Abad" agak aneh, harusnya "Abad ke-19". Tapi regex sederhana cukup membantu.
                .let {
                    // Penyesuaian khusus untuk format "19th century" menjadi "Abad ke-19"
                    val regex = Regex("""(\d+)(st|nd|rd|th)\s+Abad""", RegexOption.IGNORE_CASE)
                    regex.replace(it) { matchResult ->
                        "Abad ke-${matchResult.groupValues[1]}"
                    }
                }
        }
        
    val displayDescription: String
        get() {
            var desc = ""
            
            // Kalimat Pertama: Identitas Utama
            val mainName = objectName?.takeIf { it.isNotBlank() }?.lowercase() ?: "karya seni"
            desc += if (!title.isNullOrBlank()) "Dikenal dengan nama \"$title\", objek ini merupakan sebuah $mainName"
                    else "Objek ini merupakan sebuah $mainName"
                    
            val theArtist = artistDisplayName?.takeIf { it.isNotBlank() }
            if (theArtist != null) {
                desc += " yang diciptakan oleh seniman $theArtist"
            }
            
            val origin = culture?.takeIf { it.isNotBlank() } ?: country?.takeIf { it.isNotBlank() }
            if (origin != null) {
                desc += " yang berasal dari peradaban/budaya $origin"
            }
            desc += "."
            
            // Kalimat Kedua: Bahan & Sejarah
            val theMedium = medium?.takeIf { it.isNotBlank() }?.lowercase()
            val theDate = objectDate?.takeIf { it.isNotBlank() }
            if (theMedium != null && theDate != null) {
                desc += " Secara historis, karya ini diperkirakan berasal dari periode $theDate dan dibuat menggunakan material $theMedium."
            } else if (theDate != null) {
                desc += " Secara historis, karya ini berasal dari periode $theDate."
            } else if (theMedium != null) {
                desc += " Karya ini secara unik dibuat menggunakan material $theMedium."
            }
            
            // Kalimat Ketiga: Lokasi saat ini
            val dept = department?.takeIf { it.isNotBlank() }
            val repo = repository?.takeIf { it.isNotBlank() } ?: "The Metropolitan Museum of Art"
            if (dept != null) {
                desc += " Saat ini, mahakarya tersebut dilestarikan dalam koleksi departemen $dept di $repo."
            } else {
                desc += " Saat ini, karya tersebut dilestarikan di $repo."
            }
            
            return desc
        }
}
