# SEISMOVA — Android prototype

Aplikasi edukasi kesiapsiagaan tsunami dengan visual ocean premium (navy–teal).

## Fitur yang tersedia
- Beranda dan navigasi bawah
- Materi belajar mitigasi dengan kartu yang dapat dibuka
- Simulasi keputusan sederhana beserta umpan balik
- Checklist tas siaga yang tersimpan lokal
- Panduan umum saat gempa dan ketika berada di pesisir
- Edukasi khusus konteks pesisir Tulungagung
- Progres belajar dan checklist tersimpan pada perangkat

## Cara membuka
1. Instal Android Studio versi stabil.
2. Ekstrak ZIP ini.
3. Di Android Studio, pilih **Open** dan pilih folder `SEISMOVA_Android`.
4. Tunggu Gradle Sync selesai. Jika diminta, instal Android SDK Platform 35 dan gunakan JDK 17.
5. Jalankan pada emulator atau perangkat Android dengan USB debugging aktif.

## Catatan penting
- Ini prototipe edukasi offline, bukan aplikasi peringatan dini real-time.
- Belum ada integrasi sensor seismik, relai LoRa, backend, notifikasi BMKG, atau peta evakuasi tervalidasi.
- Jangan menampilkan status “aman” atau rute evakuasi tertentu tanpa data resmi yang diverifikasi.
- Materi keselamatan perlu ditinjau dengan panduan terbaru BMKG, BNPB, BPBD, dan otoritas setempat sebelum dipakai untuk edukasi publik.
- Build APK belum disertakan; Android Studio perlu melakukan Gradle Sync dan build pada perangkat pengembang.
