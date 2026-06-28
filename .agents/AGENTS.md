# Project-Specific Agent Rules

This file (`AGENTS.md`) contains custom rules and behavioral constraints for the AI agent specific to this workspace (Jejak Karya). 
You can add your own rules below to customize how the agent behaves, writes code, or communicates with you in this project.

## User Rules
Berdasarkan riwayat dan preferensi pengerjaan (*prompting*), berikut adalah aturan yang wajib dipatuhi agent untuk proyek ini:

- **Bahasa & Komunikasi**: Selalu gunakan Bahasa Indonesia yang ramah, komunikatif, dan teknis. Berikan penjelasan yang jelas atas setiap perombakan UI atau logika yang dilakukan tapi jangan terlalu bertele-tele.
- **Smooth UX & Desain**: 
  - Utamakan antarmuka yang bersih (*clean*), padat informasi, namun tetap memanjakan mata (*eye-catching*).
  - Terapkan animasi transisi dan mikro-interaksi (*micro-animations*) yang terasa cepat dan responsif (durasi 150ms - 300ms) untuk komponen-komponen UI (seperti getaran tombol hati, transisi ukuran, dll).
  - Hindari kedipan (*flickering*) atau pemuatan ganda (*double loading*). 
  - **PENTING**: Jika data sudah tersedia di dalam memori sementara (*cache*), JANGAN gunakan *Skeleton Loading* ketika berpindah kategori. Tampilkan data secara instan agar tidak mengganggu pengalaman pengguna.
- **Optimasi Aplikasi & Performa (Jetpack Compose)**: 
  - Gunakan `rememberSaveable` untuk mempertahankan *state* antarmuka (seperti kueri pencarian, posisi scroll, atau tab aktif) agar tidak hilang saat pengguna kembali (Navigasi Back).
  - Terapkan `Shared ViewModel` atau memori global di level *Navigation* jika dua layar membutuhkan data yang sama agar terhindar dari *network fetch* atau *database fetch* yang berlebihan dan mubazir.
  - Terapkan "Race to 11" atau pemanggilan API secara paralel (menggunakan *Coroutines*, *async-await*) untuk menghindari *loading* yang terlalu lama saat melakukan *fetch* banyak data.
- **Konsistensi UI/UX**: 
  - Saat membangun fitur (seperti Multi-select, Pull-to-Refresh), pastikan terasa konsisten di semua layar (contoh: *ExploreTab* dan *CollectionTab* memiliki logika *refresh* yang selaras).
  - Gunakan penamaan file dan ikon yang koheren.
- **Clean Code**: 
  - Usahakan kode yang *compact* dan terstruktur. Gunakan *Grid* atau tata letak modern (seperti Parallax) untuk menghemat ruang jika diperlukan.
  - Pisahkan logika dari UI dengan menempatkannya di *ViewModel* atau *Repository*.
- **Sinkronisasi Data & Keamanan**: 
  - Komponen UI (seperti inisial profil, nama) harus selalu tersinkronisasi secara dinamis dari *StateFlow* agar tidak ada data basi (stale).
  - Jika ada perubahan data kredensial atau *state* krusial (seperti penggantian sandi), pastikan untuk selalu menyelaraskannya dengan *cache* keamanan lokal (misalnya pada Biometrik/EncryptedSharedPreferences) supaya terhindar dari galat "Invalid Credential".
- **Detail Layout & Proporsi**: 
  - Selalu perhatikan atribut detail di Jetpack Compose seperti `fillMaxWidth`, `padding`, dan `RoundedCornerShape` di setiap form dan komponen agar tetap proporsional dan tidak terlihat *out-of-place* dibandingkan dengan elemen di sekelilingnya.
