# 🏛️ Jejak Karya

![Jejak Karya Banner](https://img.shields.io/badge/Status-Active-success)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue?logo=kotlin)
![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-Material_3-4285F4?logo=jetpackcompose)
![Supabase](https://img.shields.io/badge/Supabase-Backend-3ECF8E?logo=supabase)

**Jejak Karya** adalah sebuah aplikasi *mobile* yang didedikasikan untuk menyusuri jejak sejarah dan melestarikan budaya peninggalan masa lalu Nusantara. Aplikasi ini memungkinkan Anda untuk menemukan, membaca, dan menyimpan koleksi situs-situs bersejarah serta karya seni warisan leluhur.

---

## ✨ Fitur Utama

- **Modern & Smooth UI/UX**: Dibangun sepenuhnya dengan **Jetpack Compose** (Material Design 3) dan animasi *slide* & *crossfade* berdurasi 600ms (*Cinematic* & *Snappy*).
- **Autentikasi Aman**: Didukung oleh sistem Login & Register berbasis [Supabase Auth](https://supabase.com/docs/guides/auth).
- **Login Biometrik**: Mendukung *Fingerprint / Face Unlock* menggunakan `androidx.biometric` dan `security-crypto` untuk penyimpanan kredensial tingkat militer.
- **Koleksi & Bookmark**: Simpan situs bersejarah dan karya favorit Anda (tersinkronisasi secara *real-time* dengan *StateFlow*).
- **Pencarian Cepat**: Cari peninggalan budaya berdasarkan nama atau deskripsi.
- **Kinerja Optimal**: Pemuatan gambar besar yang ringan dan *asynchronous* berkat integrasi **Coil**.

## 🛠️ Teknologi yang Digunakan

- **Bahasa**: Kotlin
- **UI Framework**: Jetpack Compose
- **Arsitektur**: MVVM (Model-View-ViewModel), Clean Architecture
- **Backend / Database**: Supabase (PostgreSQL), Supabase-KT
- **Networking**: Ktor Client
- **Image Loading**: Coil (`coil-compose`)
- **Navigasi**: Jetpack Navigation Compose

## 🚀 Cara Menjalankan (Run Locally)

1. *Clone* repositori ini:
   ```bash
   git clone https://github.com/qillexia/Jejak-Karuhun.git
   ```
2. Buka proyek ini menggunakan **Android Studio**.
3. Buat file `Secrets.kt` di direktori yang sesuai dan konfigurasikan kunci API Supabase Anda (karena file ini diabaikan oleh `.gitignore`).
4. Tunggu *Gradle* selesai melakukan sinkronisasi (*build*).
5. Klik **Run** atau tekan `Shift + F10` untuk menjalankan aplikasi di Emulator atau perangkat fisik Anda.

## 📦 Rilis (Release)

Untuk mem-*build* APK versi rilis, jalankan perintah berikut di *Terminal*:
```powershell
.\gradlew.bat assembleRelease
```
*Catatan: Pastikan Anda telah menata konfigurasi Keystore (signingConfig) Anda jika ingin mengunggahnya ke Play Store.*
