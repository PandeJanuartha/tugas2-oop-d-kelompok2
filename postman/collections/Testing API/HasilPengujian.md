# ENDPOINT USER
## REGISTRASI BUYER
``` json
{
    "status": "success",
    "data": {
        "id": "USR-001",
        "name": "Kadek Surya",
        "email": "kadek.surya@email.com",
        "phone": "081234567890",
        "role": "buyer",
        "createdAt": 1782417667875
    }
}
```
## REGISTRASI ORGANIZER
``` json 
{
    "status": "success",
    "data": {
        "id": "USR-002",
        "name": "Bali Event Organizer",
        "email": "info@balievent.id",
        "phone": "081987654321",
        "role": "organizer",
        "createdAt": 1782417722000
    }
}
```
## AMBIL SEMUA USER
``` json
{
    "status": "success",
    "data": [
        {
            "id": "USR-001",
            "name": "Kadek Surya",
            "email": "kadek.surya@email.com",
            "phone": "081234567890",
            "role": "buyer",
            "createdAt": 1782417667000
        },
        {
            "id": "USR-002",
            "name": "Bali Event Organizer",
            "email": "info@balievent.id",
            "phone": "081987654321",
            "role": "organizer",
            "createdAt": 1782417722000
        }
    ]
}
```
## AMBIL DETAIL USR-001
``` json
{
    "status": "success",
    "data": {
        "id": "USR-001",
        "name": "Kadek Surya",
        "email": "kadek.surya@email.com",
        "phone": "081234567890",
        "role": "buyer",
        "created_at": 1782417667000,
        "activity_summary": {
            "total_tickets_bought": 0,
            "total_spending": 0
        }
    }
}
```
## UPDATE USR-001
``` json
{
    "status": "success",
    "data": {
        "id": "USR-001",
        "name": "Kadek Surya Wibawa",
        "email": "kadek.surya.new@email.com",
        "phone": "081234567890",
        "role": "buyer",
        "createdAt": 1782417667000
    }
}
```

# ENDPOINT VENUE
## REGISTRASI VENUE
``` json
{
    "status": "success",
    "data": {
        "address": "Jl. Raya Uluwatu, Ungasan, Bali",
        "name": "GWK Cultural Park",
        "maxCapacity": 8000,
        "id": "VNU-001"
    }
}
```
## AMBIL SEMUA VENUE
``` json 
{
    "status": "success",
    "data": [
        {
            "address": "Jl. Raya Uluwatu, Ungasan, Bali",
            "name": "GWK Cultural Park",
            "maxCapacity": 8000,
            "id": "VNU-001"
        }
    ]
}
```
## AMBIL DETAIL VENUE VNU-001
``` json
{
    "status": "success",
    "data": {
        "address": "Jl. Raya Uluwatu, Ungasan, Bali",
        "name": "GWK Cultural Park",
        "maxCapacity": 8000,
        "id": "VNU-001",
        "events": []
    }
}
```
## UPDATE VENUE VNU-001
``` json
{
    "status": "success",
    "data": {
        "address": "Jl. Raya Uluwatu, Ungasan, Bali",
        "name": "GWK Cultural Park - Main Amphitheatre",
        "maxCapacity": 10000,
        "id": "VNU-001"
    }
}
```

# ENDPOINT EVENT
## REGISTRASI EVENT
``` json
{
    "status": "success",
    "data": {
        "id": "EVT-001",
        "type": "concert",
        "name": "Bali Music Festival 2026",
        "venueId": "VNU-001",
        "organizerId": "USR-002",
        "date": "2026-08-15",
        "basePrice": 250000,
        "capacities": {
            "festival": 1000,
            "vip": 100,
            "regular": 500
        },
        "refundable": true,
        "published": false
    }
}
```
## AMBIL SEMUA EVENT
``` json
{
    "status": "success",
    "data": [
        {
            "id": "EVT-001",
            "type": "concert",
            "name": "Bali Music Fest 2026 - Day 1",
            "venue": {
                "id": "VNU-001",
                "name": "GWK Cultural Park - Main Amphitheatre"
            },
            "organizer": {
                "id": "USR-002",
                "name": "Bali Event Organizer"
            },
            "date": "2026-08-15",
            "basePrice": 300000,
            "priceList": {
                "vip": 900000,
                "regular": 300000,
                "festival": 210000
            },
            "remainingCapacity": {
                "festival": 1000,
                "vip": 98,
                "regular": 500
            },
            "refundable": true
        },
        {
            "id": "EVT-002",
            "type": "seminar",
            "name": "Tech Talk: Future of AI",
            "venue": {
                "id": "VNU-001",
                "name": "GWK Cultural Park - Main Amphitheatre"
            },
            "organizer": {
                "id": "USR-002",
                "name": "Bali Event Organizer"
            },
            "date": "2026-09-10",
            "basePrice": 100000,
            "priceList": {
                "general": 100000
            },
            "remainingCapacity": {
                "vip": 50,
                "regular": 200
            },
            "refundable": true
        },
        {
            "id": "EVT-003",
            "type": "sport_match",
            "name": "Bali United vs Persija",
            "venue": {
                "id": "VNU-001",
                "name": "GWK Cultural Park - Main Amphitheatre"
            },
            "organizer": {
                "id": "USR-002",
                "name": "Bali Event Organizer"
            },
            "date": "2026-07-20",
            "basePrice": 50000,
            "priceList": {
                "tribune": 50000,
                "vip": 125000,
                "vvip": 250000
            },
            "remainingCapacity": {
                "vvip": 49,
                "tribune": 5000,
                "vip": 200
            },
            "refundable": false
        }
    ]
}
## AMBIL DETAIL EVENT EVT-001
``` json 
{
    "status": "success",
    "data": {
        "id": "EVT-001",
        "type": "concert",
        "name": "Bali Music Festival 2026",
        "venue": {
            "id": "VNU-001",
            "name": "GWK Cultural Park - Main Amphitheatre"
        },
        "organizer": {
            "id": "USR-002",
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
            "festival": 1000,
            "vip": 100,
            "regular": 500
        },
        "refundable": true,
        "refundPolicy": "100% if >14 days, 50% if 7-14 days, 0% if <7 days"
    }
}
```
## UPDATE EVENT EVT-001
``` json
{
    "status": "success",
    "data": {
        "id": "EVT-001",
        "type": "concert",
        "name": "Bali Music Fest 2026 - Day 1",
        "venueId": "VNU-001",
        "organizerId": "USR-002",
        "date": "2026-08-15",
        "basePrice": 300000,
        "capacities": {
            "festival": 1000,
            "vip": 100,
            "regular": 500
        },
        "refundable": true,
        "published": false
    }
}
```
## RINGKASAN DAFTAR HARGA
``` json
{
    "status": "success",
    "data": [
        {
            "id": "EVT-001",
            "name": "Bali Music Fest 2026 - Day 1",
            "type": "concert",
            "prices": {
                "vip": 900000,
                "regular": 300000,
                "festival": 210000
            }
        },
        {
            "id": "EVT-002",
            "name": "Tech Talk: Future of AI",
            "type": "seminar",
            "prices": {
                "general": 100000
            }
        },
        {
            "id": "EVT-003",
            "name": "Bali United vs Persija",
            "type": "sport_match",
            "prices": {
                "tribune": 50000,
                "vip": 125000,
                "vvip": 250000
            }
        }
    ]
}
```
## REGISTRASI EVENT SEMINAR
``` json
{
    "status": "success",
    "data": {
        "id": "EVT-002",
        "type": "seminar",
        "name": "Tech Talk: Future of AI",
        "venueId": "VNU-001",
        "organizerId": "USR-002",
        "date": "2026-09-10",
        "basePrice": 100000,
        "capacities": {
            "vip": 50,
            "regular": 200
        },
        "refundable": true,
        "published": false
    }
}
```
## REGISTRASI EVENT SPORTMATCH
``` json
{
    "status": "success",
    "data": {
        "id": "EVT-003",
        "type": "sport_match",
        "name": "Bali United vs Persija",
        "venueId": "VNU-001",
        "organizerId": "USR-002",
        "date": "2026-07-20",
        "basePrice": 50000,
        "capacities": {
            "vvip": 50,
            "tribune": 5000,
            "vip": 200
        },
        "published": false
    }
}
```

# ENDPOINT TIKET & LAPORAN PENJUALAN
## BELI TIKET CONCERT 
``` json
{
    "status": "success",
    "data": {
        "id": "TKT-001",
        "eventId": "EVT-001",
        "userId": "USR-001",
        "category": "vip",
        "quantity": 2,
        "unitPrice": 900000,
        "totalPrice": 1800000,
        "purchaseDate": "2026-06-26",
        "status": "active",
        "refundAmount": 0
    }
}
```
## BELI TIKET EVT-002
``` json
{
    "status": "success",
    "data": {
        "id": "TKT-003",
        "eventId": "EVT-002",
        "userId": "USR-001",
        "category": "vip",
        "quantity": 3,
        "unitPrice": 100000,
        "totalPrice": 300000,
        "purchaseDate": "2026-06-26",
        "status": "active",
        "refundAmount": 0
    }
}
```
## BELI TIKET EVT-003
``` json
{
    "status": "success",
    "data": {
        "id": "TKT-002",
        "eventId": "EVT-003",
        "userId": "USR-001",
        "category": "vvip",
        "quantity": 1,
        "unitPrice": 250000,
        "totalPrice": 250000,
        "purchaseDate": "2026-06-26",
        "status": "active",
        "refundAmount": 0
    }
}
```
## BELI TIKET EVT-002 MELEBIHI KUOTA
``` json
{
    "status": "error",
    "message": "Tiket untuk kategori 'vip' telah habis."
}
```
## AMBIL SEMUA PEMBELIAN TIKET
``` json
{
    "status": "success",
    "data": [
        {
            "id": "TKT-001",
            "eventId": "EVT-001",
            "userId": "USR-001",
            "category": "vip",
            "quantity": 2,
            "unitPrice": 900000,
            "totalPrice": 1800000,
            "purchaseDate": "2026-06-26",
            "status": "refunded",
            "refundAmount": 1800000
        },
        {
            "id": "TKT-002",
            "eventId": "EVT-003",
            "userId": "USR-001",
            "category": "vvip",
            "quantity": 1,
            "unitPrice": 250000,
            "totalPrice": 250000,
            "purchaseDate": "2026-06-26",
            "status": "active",
            "refundAmount": 0
        },
        {
            "id": "TKT-003",
            "eventId": "EVT-002",
            "userId": "USR-001",
            "category": "vip",
            "quantity": 3,
            "unitPrice": 100000,
            "totalPrice": 300000,
            "purchaseDate": "2026-06-26",
            "status": "refunded",
            "refundAmount": 300000
        }
    ]
}
```
## AMBIL DETAIL TKT-001
``` json
{
    "status": "success",
    "data": {
        "id": "TKT-001",
        "eventId": "EVT-001",
        "userId": "USR-001",
        "category": "vip",
        "quantity": 2,
        "unitPrice": 900000,
        "totalPrice": 1800000,
        "purchaseDate": "2026-06-26",
        "status": "active",
        "refundAmount": 0
    }
}
``` 
## REFUND TKT-001
``` json
{
    "status": "success",
    "data": {
        "ticket": {
            "id": "TKT-001",
            "eventId": "EVT-001",
            "userId": "USR-001",
            "category": "vip",
            "quantity": 2,
            "unitPrice": 900000,
            "totalPrice": 1800000,
            "purchaseDate": "2026-06-26",
            "status": "refunded",
            "refundAmount": 1800000
        },
        "message": "Refund berhasil diproses.",
        "refundAmount": 1800000
    }
}
```
## REFUND TKT-002
```  json
{
    "status": "error",
    "message": "Event bertipe 'sport_match' tidak mendukung refund."
}
```
## REFUND TKT-003
```
{
    "status": "success",
    "data": {
        "ticket": {
            "id": "TKT-003",
            "eventId": "EVT-002",
            "userId": "USR-001",
            "category": "vip",
            "quantity": 3,
            "unitPrice": 100000,
            "totalPrice": 300000,
            "purchaseDate": "2026-06-26",
            "status": "refunded",
            "refundAmount": 300000
        },
        "message": "Refund berhasil diproses.",
        "refundAmount": 300000
    }
}
```
## LAPORAN PENJUALAN EVT-001
``` json
{
    "status": "success",
    "data": {
        "eventId": "EVT-001",
        "totalTicketsSold": 0,
        "tickets": [],
        "totalRevenue": 0
    }
}
```

