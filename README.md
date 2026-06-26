# REST API Manajemen Event dan Ticketing

Sistem backend RESTful API untuk manajemen penyelenggaraan acara dan transaksi penjualan tiket. Proyek ini mengelola empat entitas utama: pengguna (*user*), lokasi penyelenggaraan (*venue*), acara (*event*), dan tiket (*ticket*), serta dibangun murni menggunakan Java Standard Edition (JDK 11+) dengan *database* SQLite.

---

## Daftar Isi

- [1. Deskripsi Singkat Proyek](#1-deskripsi-singkat-proyek)
- [2. Cara Menjalankan Server](#2-cara-menjalankan-server)
  - [Pengujian dengan Postman](#pengujian-dengan-postman)
- [3. Daftar Endpoint API Lengkap](#3-daftar-endpoint-api-lengkap)
  - [Endpoint User](#endpoint-user)
  - [Endpoint Venue](#endpoint-venue)
  - [Endpoint Event](#endpoint-event)
  - [Endpoint Ticket dan Refund](#endpoint-ticket-dan-refund)
  - [Endpoint Laporan](#endpoint-laporan)
- [4. Struktur Proyek](#4-struktur-proyek)
- [5. Pembagian Tugas Anggota](#5-pembagian-tugas-anggota)

---

## 1. Deskripsi Singkat Proyek

Proyek ini merupakan implementasi REST API untuk manajemen tiket acara. Seluruh arsitekturnya dibangun tanpa bantuan *framework* pihak ketiga, sehingga mewajibkan pemahaman mendalam terkait pemrosesan HTTP secara *native* dan manajemen koneksi basis data menggunakan JDBC. Proyek ini mendemonstrasikan penerapan konsep Object-Oriented Programming (OOP) secara langsung, seperti polimorfisme pada kalkulasi harga tiket dan abstraksi antarmuka pada kebijakan *refund* (pembatalan).

---

## 2. Cara Menjalankan Server

**Prasyarat:**
- Java Development Kit (JDK) 11 atau lebih baru.
- Aplikasi pengujian API (seperti Postman atau `curl`).

**Langkah-langkah Eksekusi:**

1. **Kloning Repositori**
   ```bash
   git clone https://github.com/PandeJanuartha/tugas2-oop-d-kelompok2.git
   cd tugas2-oop-d-kelompok2/src
   ```

2. **Kompilasi Kode Sumber**
   Jalankan perintah berikut di dalam direktori `src/`:
   
   *Linux / macOS:*
   ```bash
   javac -cp ".:../lib/*" App.java model/*.java service/*.java repository/*.java handler/*.java exception/*.java database/*.java server/*.java
   ```
   
   *Windows:*
   ```bash
   javac -cp ".;../lib/*" App.java model/*.java service/*.java repository/*.java handler/*.java exception/*.java database/*.java server/*.java
   ```

3. **Menjalankan Server**
   *Linux / macOS:*
   ```bash
   java -cp ".:../lib/*" App
   ```
   
   *Windows:*
   ```bash
   java -cp ".;../lib/*" App
   ```
   Server akan berjalan secara *default* pada `http://localhost:8080`. (Gunakan argumen angka untuk mengubah port, contoh: `java -cp ".;../lib/*" App 3000`).

### Pengujian dengan Postman
Seluruh endpoint API dapat diuji secara komprehensif menggunakan **Postman**. Koleksi API beserta dokumen hasil pengujian telah disediakan.
- Buka aplikasi Postman, pilih menu **Import**.
- Arahkan ke direktori `postman/collections/Testing API` di repositori ini.
- Hasil pengujian terperinci dari seluruh *endpoint* dapat dilihat pada berkas `postman/collections/Testing API/HasilPengujian.md`.

---

## 3. Daftar Endpoint API Lengkap

Semua API mengembalikan standar format JSON berupa `status` ("success" atau "error") beserta `data` atau `message`.

### Endpoint User
1. **GET `/api/users`** - Daftar seluruh pengguna.
2. **GET `/api/users/{id}`** - Detail pengguna.
3. **POST `/api/users`** - Registrasi pengguna.
4. **PUT `/api/users/{id}`** - Update data pengguna.

**Contoh Request (POST `/api/users`):**
```json
{
  "name": "Kadek Surya",
  "email": "kadek.surya@email.com",
  "phone": "081234567890",
  "role": "buyer"
}
```
**Contoh Response:**
```json
{
  "status": "success",
  "data": {
    "id": "USR-003",
    "name": "Kadek Surya",
    "email": "kadek.surya@email.com",
    "phone": "081234567890",
    "role": "buyer",
    "summary": {
      "totalTicketsPurchased": 0,
      "totalSpending": 0.0
    }
  }
}
```

### Endpoint Venue
5. **GET `/api/venues`** - Daftar seluruh *venue*.
6. **GET `/api/venues/{id}`** - Detail *venue*.
7. **POST `/api/venues`** - Penambahan *venue* baru.
8. **PUT `/api/venues/{id}`** - Update *venue*.

**Contoh Request (POST `/api/venues`):**
```json
{
  "name": "GWK Cultural Park",
  "address": "Ungasan, Bali",
  "maxCapacity": 8000
}
```
**Contoh Response:**
```json
{
  "status": "success",
  "data": {
    "id": "VNU-001",
    "name": "GWK Cultural Park",
    "address": "Ungasan, Bali",
    "maxCapacity": 8000,
    "events": []
  }
}
```

### Endpoint Event
9. **GET `/api/events`** - Daftar seluruh acara.
10. **GET `/api/events/{id}`** - Detail acara beserta daftar harga polimorfik.
11. **POST `/api/events`** - Penambahan acara baru.
12. **PUT `/api/events/{id}`** - Update acara.

**Contoh Request (POST `/api/events`):**
```json
{
  "type": "concert",
  "name": "Bali Music Festival",
  "venueId": "VNU-001",
  "organizerId": "USR-001",
  "date": "2026-08-15",
  "basePrice": 250000,
  "capacity": { "vip": 100, "regular": 500, "festival": 1000 }
}
```
**Contoh Response (GET `/api/events/{id}`):**
```json
{
  "status": "success",
  "data": {
    "id": "EVT-005",
    "type": "concert",
    "name": "Bali Music Festival",
    "date": "2026-08-15",
    "basePrice": 250000,
    "priceList": {
      "vip": 750000.0,
      "regular": 250000.0,
      "festival": 175000.0
    },
    "refundable": true
  }
}
```

### Endpoint Ticket dan Refund
13. **GET `/api/tickets`** - Daftar seluruh tiket.
14. **GET `/api/tickets/{id}`** - Detail tiket.
15. **POST `/api/tickets`** - Pembelian tiket.
16. **PUT `/api/tickets/{id}/refund`** - Pembatalan/Refund tiket.

**Contoh Request (POST `/api/tickets`):**
```json
{
  "eventId": "EVT-005",
  "userId": "USR-003",
  "category": "vip",
  "quantity": 2
}
```
**Contoh Response:**
```json
{
  "status": "success",
  "data": {
    "id": "TKT-042",
    "category": "vip",
    "quantity": 2,
    "unitPrice": 750000.0,
    "totalPrice": 1500000.0,
    "status": "active"
  }
}
```

### Endpoint Laporan
17. **GET `/api/events/price-summary`** - Agregasi perhitungan dinamis daftar harga seluruh acara.
18. **GET `/api/reports/sales?eventId={id}`** - Statistik performa penjualan spesifik suatu acara.

**Contoh Response (GET `/api/events/price-summary`):**
```json
{
  "status": "success",
  "data": [
    {
      "id": "EVT-005",
      "name": "Bali Music Festival",
      "type": "concert",
      "prices": {
        "vip": 750000.0,
        "regular": 250000.0,
        "festival": 175000.0
      }
    }
  ]
}
```

---

## 4. Struktur Proyek

Arsitektur aplikasi menerapkan struktur berlapis (*layered*) untuk menjaga prinsip *Separation of Concerns*.

```
tugas2-oop-d-kelompok2/
├── lib/                                  # Dependensi (SQLite JDBC & Jackson)
├── docs/                                 # Dokumentasi pendukung 
├── postman/
│   └── collections/
│       └── Testing API/                  # File koleksi Postman
│           ├── HasilPengujian.md         # Laporan logis/pengujian Postman
│           └── [Folder endpoint]         # Definisi request Postman
└── src/
    ├── App.java                          # Entry point aplikasi
    ├── server/                           # Core HTTP engine & routing
    ├── handler/                          # Request handlers (API Controllers)
    ├── service/                          # Business logic & validasi
    ├── repository/                       # Data access layer (Query SQL SQLite)
    ├── model/                            # Model entitas & struktur polimorfik
    ├── exception/                        # Custom exception handling
    └── database/                         # Modul inisialisasi SQLite
```

---

## 5. Pembagian Tugas Anggota

Tabel berikut menjabarkan pendelegasian tugas secara mendetail di antara seluruh anggota kelompok:

| Anggota | NIM | Tanggung Jawab |
|---|---|---|
| Oka | 2505551101 | Inisialisasi proyek dan template server, Database DDL (`DatabaseManager.java`), Custom exception classes, Model, Repository, Service, dan Handler untuk entitas Venue, Wiring seluruh handler di `App.java`, QA dan refactor. |
| Veda | 2505551109 | Model `User.java`, Repository `UserRepository.java`, Service `UserService.java`, Handler `UserHandler.java`. |
| Pande | 250555074 | Interface `Refundable.java`, Abstract class `Event.java`, Subclass `Concert.java` / `Seminar.java` / `SportMatch.java`, Repository `EventRepository.java`, Service `EventService.java`, Handler `EventHandler.java`. |
| Grevi | 2505551070 | Model `Ticket.java`, Repository `TicketRepository.java`, Service `TicketService.java`, Handler `TicketHandler.java` termasuk laporan endpoint penjualan. |