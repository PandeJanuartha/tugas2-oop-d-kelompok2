# Tugas 2 Pemrograman Berorientasi Objek - REST API Manajemen Event & Ticketing

## 1. Deskripsi Tugas

### 1.1 Ringkasan

Kalian diminta untuk membangun sebuah REST API menggunakan Java untuk sistem Manajemen Event dan Ticketing. Sistem ini menangani pengelolaan user, venue, event, dan penjualan tiket. API harus mengimplementasikan konsep-konsep OOP yang telah dipelajari selama perkuliahan.

| Item | Detail |
|---|---|
| Topik | Sistem Manajemen Event & Ticketing |
| Bahasa | Java (JDK 11) |
| Database | SQLite (`sqlite-jdbc` oleh Xerial) |
| JSON Library | Jackson (`jackson-databind`) |
| HTTP Server | Template sudah disediakan |
| Repository | GitHub (sebagai portofolio kalian) |

### 1.2 Aturan Library dan Framework

Kalian **HANYA** boleh menggunakan library berikut (sudah termasuk di template API server):

1. `com.sun.net.httpserver` -- bawaan Java 
2. `sqlite-jdbc` (org.xerial) -- driver SQLite untuk Java
3. `jackson-databind` (com.fasterxml.jackson.core) -- serialisasi/deserialisasi JSON

**TIDAK DIPERBOLEHKAN** menggunakan library atau framework tambahan di luar ketiga library di atas. Ini termasuk namun tidak terbatas pada:

- Spring Boot, Spring MVC, atau framework Spring lainnya
- Jakarta EE / Java EE (Servlet, JAX-RS, dll.)
- Micronaut, Quarkus, Javalin, Spark Java, atau micro-framework lainnya
- Hibernate, MyBatis, atau ORM lainnya
- Gson, JSON-P, atau library JSON lainnya (gunakan Jackson)
- Lombok

Tujuan pembatasan ini agar kalian benar-benar memahami fundamental Java: menulis getter/setter sendiri, menulis SQL query secara manual, dan mengelola koneksi database via JDBC.

### 1.3 Template yang Disediakan

Template HTTP server yang diberikan sudah menangani:

- Setup server dan routing (`server/Server.java`)
- Parsing request: body JSON, path parameter, query parameter (`server/Request.java`)
- Pengiriman response JSON dengan format standar (`server/Response.java`)
- Interface handler untuk routing (`server/RouteHandler.java`)
- Konfigurasi koneksi SQLite dasar (`database/DatabaseManager.java`)
- Contoh penggunaan endpoint di `App.java`

Kalian **tidak perlu mengubah** file-file dalam package `server/`. Fokus kalian adalah membangun **model, service, repository, handler, dan exception** di belakang setiap endpoint.

### 1.4 Dependency dan Cara Menjalankan

Pastikan file JAR berikut ada di folder `lib/`:

```
lib/
|-- jackson-annotations-2.13.3.jar
|-- jackson-core-2.13.3.jar
|-- jackson-databind-2.13.3.jar
+-- sqlite-jdbc-3.49.1.0.jar
```

Kompilasi dan jalankan dari folder `src/`:

```bash
# Linux / Mac
javac -cp ".:../lib/*" App.java model/*.java service/*.java repository/*.java handler/*.java exception/*.java database/*.java server/*.java
java -cp ".:../lib/*" App

# Windows
javac -cp ".;../lib/*" App.java model/*.java service/*.java repository/*.java handler/*.java exception/*.java database/*.java server/*.java
java -cp ".;../lib/*" App
```

Server akan berjalan di `http://localhost:8080`. Gunakan **Postman** untuk menguji endpoint.

---

## 2. Struktur Proyek

### 2.1 Struktur Direktori

```
project/
|-- lib/                           # JAR library (Jackson + SQLite JDBC)
+-- src/
    |-- App.java                   # Entry point -- start server + init database
    |
    |-- server/                    # [TEMPLATE - JANGAN DIUBAH]
    |   |-- Server.java            #   HTTP server + routing engine
    |   |-- Request.java           #   Wrapper request (path param, query param, JSON body)
    |   |-- Response.java          #   Wrapper response (sendSuccess, sendError, dll.)
    |   +-- RouteHandler.java      #   Interface untuk handler
    |
    |-- database/                  # [TEMPLATE - JANGAN DIUBAH kecuali method initialize]
    |   +-- DatabaseManager.java   #   Koneksi SQLite + inisialisasi tabel
    |
    |-- model/                     # [TUGAS MAHASISWA] Entity classes + OOP
    |   |-- Event.java             #   Abstract class
    |   |-- Concert.java           #   extends Event, implements Refundable
    |   |-- Seminar.java           #   extends Event, implements Refundable
    |   |-- SportMatch.java        #   extends Event (TIDAK impl. Refundable)
    |   |-- Ticket.java
    |   |-- User.java
    |   |-- Venue.java
    |   +-- Refundable.java        #   Interface
    |
    |-- service/                   # [TUGAS MAHASISWA] Business logic
    |   |-- EventService.java
    |   |-- TicketService.java
    |   |-- UserService.java
    |   +-- VenueService.java
    |
    |-- repository/                # [TUGAS MAHASISWA] Data access (JDBC + SQLite)
    |   |-- EventRepository.java
    |   |-- TicketRepository.java
    |   |-- UserRepository.java
    |   +-- VenueRepository.java
    |
    |-- handler/                   # [TUGAS MAHASISWA] Daftarkan route di App.java
    |   |-- EventHandler.java
    |   |-- TicketHandler.java
    |   |-- UserHandler.java
    |   +-- VenueHandler.java
    |
    +-- exception/                 # [TUGAS MAHASISWA] Custom exceptions
        |-- EventNotFoundException.java
        |-- TicketSoldOutException.java
        +-- RefundNotAllowedException.java
```

### 2.2 Penjelasan Package

| Package | Tanggung Jawab | Contoh |
|---|---|---|
| `server/` | Infrastruktur HTTP server. **Jangan diubah.** | `Server.java`, `Request.java`, `Response.java` |
| `database/` | Koneksi database. Kalian hanya perlu **menambahkan `CREATE TABLE` di `DatabaseManager.initialize()`**. | `DatabaseManager.java` |
| `model/` | Entity class yang merepresentasikan data. Di sinilah abstract class, interface, inheritance, dan encapsulation diterapkan. | `Event.java`, `Concert.java`, `Refundable.java` |
| `service/` | Business logic: validasi, kalkulasi harga, pengecekan kapasitas, logika refund. Di sinilah polymorphism terjadi. | `EventService.java`, `TicketService.java` |
| `repository/` | Akses database: query SQL menggunakan `PreparedStatement`, konversi `ResultSet` ke object Java. | `EventRepository.java` |
| `handler/` | Menerima request, memanggil service, dan mengirim response. Mendaftarkan route di `App.java`. | `EventHandler.java` |
| `exception/` | Custom exception yang dipetakan ke HTTP status code. | `TicketSoldOutException.java` |

### 2.3 Alur Request

Setiap HTTP request yang masuk akan melewati alur berikut:

```
Client (Postman)
    |
    v
[server/Server.java]  -- Routing: cocokkan URL + method dengan handler terdaftar
    |
    v
[handler/]             -- Parsing request, panggil service yang sesuai
    |
    v
[service/]             -- Business logic, validasi, polymorphism
    |
    v
[repository/]          -- Query SQL ke database (JDBC + PreparedStatement)
    |
    v
[SQLite database.db]   -- Simpan/ambil data
    |
    v
(response JSON dikembalikan ke client)
```

### 2.4 Cara Mendaftarkan Route

Di `App.java`, daftarkan route menggunakan method `server.get()`, `server.post()`, dan `server.put()`. Contoh:

```java
Server server = new Server(port);

// Daftarkan route yang lebih SPESIFIK terlebih dahulu
server.get("/api/events/price-summary", (req, res) -> {
    // handler untuk ringkasan harga
});

// Baru daftarkan route dengan path parameter
server.get("/api/events/{id}", (req, res) -> {
    String id = req.getPathParam("id");
    // handler untuk detail event
});

server.post("/api/events", (req, res) -> {
    Map<String, Object> body = req.getJSON();
    // handler untuk buat event baru
});

server.start();
```

**PENTING:** Route dengan path yang lebih spesifik (seperti `/api/events/price-summary`) harus didaftarkan **sebelum** route dengan path parameter (seperti `/api/events/{id}`). Jika dibalik, `/api/events/{id}` akan menangkap `"price-summary"` sebagai nilai `{id}`.

---

## 3. Persyaratan OOP

### 3.1 Konsep Wajib

API yang kalian buat **wajib** mengimplementasikan konsep-konsep berikut:

| Konsep | Minimum Persyaratan |
|---|---|
| Abstract Class | 1 abstract class dengan minimal 1 abstract method |
| Interface | 1 interface yang diimplementasikan oleh **sebagian** (bukan semua) subclass |
| Inheritance | Minimal 3 subclass dari 1 abstract class |
| Polymorphism | Minimal 1 endpoint yang memproses objek berbeda secara polimorfik via late binding |
| Encapsulation | Semua atribut model `private` dengan getter/setter |
| Exception Handling | Minimal 2 custom exception class, dipetakan ke HTTP status code |

### 3.2 Hierarki Class

```
Event (abstract class)
|-- Concert             (implements Refundable)
|-- Seminar             (implements Refundable)
+-- SportMatch          (TIDAK implement Refundable)

Interface Refundable
|-- calculateRefund(int daysBeforeEvent) : double
+-- isRefundable() : boolean
```

Penjelasan:

- `Event` adalah **abstract class** dengan abstract method `calculateTicketPrice(String category)`. Class ini tidak bisa di-instantiate langsung.
- `Concert`, `Seminar`, dan `SportMatch` masing-masing meng-extends `Event` dan meng-override `calculateTicketPrice()` dengan rumus yang berbeda.
- `Refundable` adalah **interface** yang hanya diimplementasikan oleh `Concert` dan `Seminar`. `SportMatch` **tidak** mengimplementasikan `Refundable` karena tiket pertandingan tidak bisa di-refund.

### 3.3 Rumus Bisnis

**Concert:**

```
Harga tiket:
  VIP      = basePrice x 3.0
  Regular  = basePrice x 1.0
  Festival = basePrice x 0.7

Kebijakan refund:
  > 14 hari sebelum event  => refund 100%
  7-14 hari sebelum event  => refund 50%
  < 7 hari sebelum event   => refund 0% (tidak bisa refund)
```

**Seminar:**

```
Harga tiket:
  Semua kategori = basePrice (flat price, tidak ada pembedaan)

Kebijakan refund:
  > 1 hari sebelum event   => refund 100%
  <= 1 hari sebelum event  => refund 0%
```

**SportMatch:**

```
Harga tiket:
  Tribune  = basePrice x 1.0
  VIP      = basePrice x 2.5
  VVIP     = basePrice x 5.0

Kebijakan refund:
  TIDAK BISA -- class ini tidak implement interface Refundable
```

---

## 4. Skema Database

### 4.1 Tabel-tabel

Tambahkan `CREATE TABLE` berikut di method `DatabaseManager.initialize()`:

```sql
CREATE TABLE IF NOT EXISTS users (
    id          TEXT PRIMARY KEY,
    name        TEXT NOT NULL,
    email       TEXT NOT NULL UNIQUE,
    phone       TEXT,
    role        TEXT DEFAULT 'buyer',       -- 'buyer' atau 'organizer'
    created_at  TEXT DEFAULT (datetime('now'))
);

CREATE TABLE IF NOT EXISTS venues (
    id              TEXT PRIMARY KEY,
    name            TEXT NOT NULL,
    address         TEXT NOT NULL,
    max_capacity    INTEGER NOT NULL,
    created_at      TEXT DEFAULT (datetime('now'))
);

CREATE TABLE IF NOT EXISTS events (
    id              TEXT PRIMARY KEY,
    type            TEXT NOT NULL,           -- 'concert', 'seminar', 'sport_match'
    name            TEXT NOT NULL,
    venue_id        TEXT NOT NULL,
    organizer_id    TEXT NOT NULL,           -- FK ke users (role = 'organizer')
    date            TEXT NOT NULL,           -- format: YYYY-MM-DD
    base_price      REAL NOT NULL,
    created_at      TEXT DEFAULT (datetime('now')),
    FOREIGN KEY (venue_id) REFERENCES venues(id),
    FOREIGN KEY (organizer_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS capacities (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    event_id    TEXT NOT NULL,
    category    TEXT NOT NULL,           -- 'vip', 'regular', 'festival', 'tribune', 'vvip'
    total       INTEGER NOT NULL,
    filled      INTEGER DEFAULT 0,
    FOREIGN KEY (event_id) REFERENCES events(id)
);

CREATE TABLE IF NOT EXISTS tickets (
    id              TEXT PRIMARY KEY,
    event_id        TEXT NOT NULL,
    user_id         TEXT NOT NULL,           -- FK ke users
    category        TEXT NOT NULL,
    quantity        INTEGER NOT NULL,
    unit_price      REAL NOT NULL,
    total_price     REAL NOT NULL,
    purchase_date   TEXT DEFAULT (date('now')),
    status          TEXT DEFAULT 'active',   -- 'active', 'refunded'
    refund_amount   REAL DEFAULT 0,
    FOREIGN KEY (event_id) REFERENCES events(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### 4.2 Relasi Antar Tabel

```
users ------< events       Seorang user (organizer) bisa membuat banyak event
venues -----< events       Setiap event diadakan di satu venue
events -----< capacities   Satu event memiliki beberapa kategori kapasitas
events -----< tickets      Satu event bisa memiliki banyak tiket terjual
users ------< tickets      Seorang user (buyer) bisa memiliki banyak tiket
```

Catatan desain: Tabel `events` menyimpan semua tipe event dalam satu tabel dengan kolom `type` sebagai diskriminator. Di layer Java, `EventRepository` membaca kolom `type` dan meng-instantiate subclass yang sesuai (`new Concert(...)`, `new Seminar(...)`, atau `new SportMatch(...)`).

---

## 5. Spesifikasi API

### 5.1 Format Response Standar

Semua response menggunakan format JSON yang konsisten:

```json
{
  "status": "success",
  "data": { ... }
}
```

```json
{
  "status": "error",
  "message": "Deskripsi error yang informatif"
}
```

Template `Response.java` sudah menyediakan helper method untuk ini:

```java
res.sendSuccess(data);             // 200 OK + {"status":"success","data":...}
res.sendCreated(data);             // 201 Created + {"status":"success","data":...}
res.sendError(400, "Pesan error"); // 400 Bad Request + {"status":"error","message":...}
res.sendError(404, "Pesan error"); // 404 Not Found + {"status":"error","message":...}
```

### 5.2 HTTP Status Codes

| Code | Nama | Kapan Digunakan |
|---|---|---|
| `200` | OK | Request berhasil (GET, PUT) |
| `201` | Created | Resource berhasil dibuat (POST) |
| `400` | Bad Request | Input tidak valid, kapasitas penuh, refund tidak diizinkan |
| `404` | Not Found | Resource tidak ditemukan berdasarkan ID |
| `500` | Internal Server Error | Error tak terduga di server |

### 5.3 Endpoint User

| Method | Route | Deskripsi |
|---|---|---|
| GET | `/api/users` | Daftar semua user. Query param opsional: `?role=organizer` |
| GET | `/api/users/{id}` | Detail user beserta ringkasan aktivitas |
| POST | `/api/users` | Registrasi user baru |
| PUT | `/api/users/{id}` | Update data user |

**Request -- POST `/api/users`:**

```json
{
  "name": "Kadek Surya",
  "email": "kadek.surya@email.com",
  "phone": "081234567890",
  "role": "buyer"
}
```

**Response -- GET `/api/users/{id}` (role: buyer):**

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
      "totalTicketsPurchased": 5,
      "totalSpending": 3750000
    }
  }
}
```

**Response -- GET `/api/users/{id}` (role: organizer):**

```json
{
  "status": "success",
  "data": {
    "id": "USR-001",
    "name": "Bali Event Organizer",
    "email": "info@balievent.id",
    "phone": "081987654321",
    "role": "organizer",
    "summary": {
      "totalEventsCreated": 3,
      "totalRevenue": 45000000
    }
  }
}
```

### 5.4 Endpoint Venue

| Method | Route | Deskripsi |
|---|---|---|
| GET | `/api/venues` | Daftar semua venue |
| GET | `/api/venues/{id}` | Detail venue termasuk daftar event di venue tersebut |
| POST | `/api/venues` | Tambah venue baru |
| PUT | `/api/venues/{id}` | Update data venue |

**Request -- POST `/api/venues`:**

```json
{
  "name": "GWK Cultural Park",
  "address": "Jl. Raya Uluwatu, Ungasan, Kuta Selatan, Badung, Bali",
  "maxCapacity": 8000
}
```

**Response -- GET `/api/venues/{id}`:**

```json
{
  "status": "success",
  "data": {
    "id": "VNU-001",
    "name": "GWK Cultural Park",
    "address": "Jl. Raya Uluwatu, Ungasan, Kuta Selatan, Badung, Bali",
    "maxCapacity": 8000,
    "events": [
      {
        "id": "EVT-005",
        "name": "Bali Music Festival 2026",
        "date": "2026-08-15"
      }
    ]
  }
}
```

### 5.5 Endpoint Event

| Method | Route | Deskripsi |
|---|---|---|
| GET | `/api/events` | Daftar semua event. Query param opsional: `?type=concert`, `?dateFrom=2026-07-01` |
| GET | `/api/events/{id}` | Detail event termasuk info venue, organizer, kapasitas tersisa, dan daftar harga |
| POST | `/api/events` | Buat event baru |
| PUT | `/api/events/{id}` | Update data event (name, date, base price) |

**Request -- POST `/api/events`:**

```json
{
  "type": "concert",
  "name": "Bali Music Festival 2026",
  "venueId": "VNU-001",
  "organizerId": "USR-001",
  "date": "2026-08-15",
  "basePrice": 250000,
  "capacity": {
    "vip": 100,
    "regular": 500,
    "festival": 1000
  }
}
```

Validasi pada POST:

- Field `type` menentukan subclass yang di-instantiate (`concert`, `seminar`, atau `sport_match`)
- `organizerId` harus merujuk ke user dengan role `organizer`
- Total kapasitas semua kategori tidak boleh melebihi `maxCapacity` venue
- Tidak boleh ada event lain di venue yang sama pada tanggal yang sama

**Response -- GET `/api/events/{id}`:**

```json
{
  "status": "success",
  "data": {
    "id": "EVT-005",
    "type": "concert",
    "name": "Bali Music Festival 2026",
    "venue": {
      "id": "VNU-001",
      "name": "GWK Cultural Park"
    },
    "organizer": {
      "id": "USR-001",
      "name": "Bali Event Organizer"
    },
    "date": "2026-08-15",
    "basePrice": 250000,
    "priceList": {
      "vip": 750000,
      "regular": 250000,
      "festival": 175000
    },
    "remainingCapacity": {
      "vip": 82,
      "regular": 341,
      "festival": 967
    },
    "refundable": true,
    "refundPolicy": "100% if >14 days, 50% if 7-14 days, 0% if <7 days"
  }
}
```

Catatan: Field `priceList` dihitung oleh method `calculateTicketPrice()` yang di-override di setiap subclass. Field `refundable` dan `refundPolicy` hanya muncul jika objek event `instanceof Refundable`.

### 5.6 Endpoint Ticket

| Method | Route | Deskripsi |
|---|---|---|
| GET | `/api/tickets` | Daftar semua tiket. Query param opsional: `?eventId=EVT-005`, `?userId=USR-003`, `?status=active` |
| GET | `/api/tickets/{id}` | Detail tiket |
| POST | `/api/tickets` | Beli tiket |
| PUT | `/api/tickets/{id}/refund` | Proses refund tiket |

**Request -- POST `/api/tickets`:**

```json
{
  "eventId": "EVT-005",
  "userId": "USR-003",
  "category": "vip",
  "quantity": 2
}
```

Saat memproses pembelian, server menghitung harga via **polymorphism**: memanggil `event.calculateTicketPrice("vip")` di mana `event` bertipe abstract `Event` namun objek sebenarnya adalah subclass konkret (misal `Concert`). Late binding memilih implementasi yang tepat.

**Response -- POST `/api/tickets`:**

```json
{
  "status": "success",
  "data": {
    "id": "TKT-042",
    "event": "Bali Music Festival 2026",
    "eventType": "concert",
    "buyer": {
      "id": "USR-003",
      "name": "Kadek Surya"
    },
    "category": "vip",
    "quantity": 2,
    "unitPrice": 750000,
    "totalPrice": 1500000,
    "purchaseDate": "2026-06-10",
    "status": "active"
  }
}
```

**Response -- PUT `/api/tickets/{id}/refund` (berhasil, Concert, >14 hari):**

```json
{
  "status": "success",
  "data": {
    "id": "TKT-042",
    "event": "Bali Music Festival 2026",
    "totalPaid": 1500000,
    "refundPercentage": 100,
    "refundAmount": 1500000,
    "status": "refunded"
  }
}
```

**Response -- PUT `/api/tickets/{id}/refund` (gagal, SportMatch):**

```json
{
  "status": "error",
  "message": "SportMatch events do not support refunds"
}
```

Saat memproses refund, service layer mengecek `if (event instanceof Refundable)`. Jika ya, cast ke `Refundable` dan panggil `calculateRefund(daysBeforeEvent)`. Jika tidak, lempar `RefundNotAllowedException` yang dipetakan ke HTTP 400.

### 5.7 Endpoint Laporan

| Method | Route | Deskripsi |
|---|---|---|
| GET | `/api/events/price-summary` | Daftar harga tiket semua event per kategori |
| GET | `/api/reports/sales?eventId={id}` | Statistik penjualan per event |

**Response -- GET `/api/events/price-summary`:**

```json
{
  "status": "success",
  "data": [
    {
      "id": "EVT-005",
      "name": "Bali Music Festival 2026",
      "type": "concert",
      "prices": {
        "vip": 750000,
        "regular": 250000,
        "festival": 175000
      }
    },
    {
      "id": "EVT-008",
      "name": "Tech Talk: AI in Education",
      "type": "seminar",
      "prices": {
        "general": 150000
      }
    },
    {
      "id": "EVT-012",
      "name": "Bali United vs Persib",
      "type": "sport_match",
      "prices": {
        "tribune": 75000,
        "vip": 187500,
        "vvip": 375000
      }
    }
  ]
}
```

Endpoint ini adalah demonstrasi **polymorphism** paling jelas: service layer memuat semua event dari database sebagai `List<Event>`. Meskipun list bertipe `Event` (abstract), setiap elemen adalah instance dari subclass konkret. Pemanggilan `calculateTicketPrice()` dalam loop menghasilkan harga yang berbeda per tipe event melalui late binding.

### 5.8 Ringkasan Seluruh Endpoint

| # | Method | Route | Entitas |
|---|---|---|---|
| 1 | GET | `/api/users` | User |
| 2 | GET | `/api/users/{id}` | User |
| 3 | POST | `/api/users` | User |
| 4 | PUT | `/api/users/{id}` | User |
| 5 | GET | `/api/venues` | Venue |
| 6 | GET | `/api/venues/{id}` | Venue |
| 7 | POST | `/api/venues` | Venue |
| 8 | PUT | `/api/venues/{id}` | Venue |
| 9 | GET | `/api/events` | Event |
| 10 | GET | `/api/events/{id}` | Event |
| 11 | POST | `/api/events` | Event |
| 12 | PUT | `/api/events/{id}` | Event |
| 13 | GET | `/api/tickets` | Ticket |
| 14 | GET | `/api/tickets/{id}` | Ticket |
| 15 | POST | `/api/tickets` | Ticket |
| 16 | PUT | `/api/tickets/{id}/refund` | Ticket |
| 17 | GET | `/api/events/price-summary` | Report |
| 18 | GET | `/api/reports/sales?eventId={id}` | Report |

**Total: 18 endpoint.**

---

## 6. Ketentuan Git dan Dokumentasi

### 6.1 Aturan Repository

- Repository di GitHub **wajib private** ketika pengerjaan dan **wajib public** di hari pengumpulan.
- Minimum **20 commit yang bermakna** (bukan `"fix typo"` berulang-ulang).
- Commit harus **tersebar selama 4 minggu** -- bukan dump semua commit di hari terakhir.
- **Semua anggota harus memiliki commit** dengan kontribusi yang proporsional.
- Pesan commit harus **deskriptif**, contoh: `Implement calculateTicketPrice for Concert class`, bukan hanya `update code`.

### 6.2 Pembagian Tugas di README

Setiap kelompok **wajib** mencantumkan tabel pembagian tugas di `README.md`:

```markdown
## Contoh Pembagian Tugas

| Anggota | NIM | Tanggung Jawab |
|---|---|---|
| Ani | 2501001 | Model: Concert.java, Seminar.java |
| Budi | 2501002 | Model: SportMatch.java, User.java, Venue.java |
| Citra | 2501003 | Service: EventService.java, TicketService.java |
| Dewa | 2501004 | Repository: EventRepository.java, TicketRepository.java, DatabaseManager |
| Eka | 2501005 | Handler: semua handler. Exception classes. UserService, VenueService |
```

### 6.3 Format README

`README.md` harus mencakup:

1. Deskripsi singkat proyek
2. Cara menjalankan server (langkah-langkah dari clone sampai server berjalan)
3. Daftar endpoint API lengkap beserta contoh request dan response
4. Struktur proyek
5. Tabel pembagian tugas anggota

---


