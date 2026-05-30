# PAPAN INFO SEMENTARA GUYS!

## Panduan
Jangan lupa pake template di docs/baseline.java yaa biar sesuai best practice :D
Kalo kalian ada buat context file / dokumen tertentu taruh di docs ya

## JOBDESK
Silahkan tambah manual klo ada ya
### Oka
- [x] **Inisialisasi Proyek**: Pindahin template_server ke folder root
- [x] **Database DDL**: Inisialisasi database
- [ ] **Exceptions**: Buat custom exceptions (`UserNotFoundException`, `VenueNotFoundException`, `EventNotFoundException`, `TicketSoldOutException`, `RefundNotAllowedException`)
- [ ] **Model**: Buat `model/Venue.java`
- [ ] **Repository**: Buat `repository/VenueRepository.java` (CRUD SQLite)
- [ ] **Service**: Buat `service/VenueService.java` (Logic list event per venue)
- [ ] **Handler**: Buat `handler/VenueHandler.java` (Routing `/api/venues/*`)
- [ ] **Wiring & Integration**: Daftarin seluruh handler di `App.java` (Atur urutan route dengan benar)
- [ ] **QA & Refactor**: Tes endpoint & refactor try-with-resources pada database connections

---

### Veda
- [ ] **Model**: Buat `model/User.java`
- [ ] **Repository**: Buat `repository/UserRepository.java` (CRUD SQLite)
- [ ] **Service**: Buat `service/UserService.java` (Logic summary total spending/revenue)
- [ ] **Handler**: Buat `handler/UserHandler.java` (Routing `/api/users/*`)

---

### Pande
- [ ] **Interface**: Buat `model/Refundable.java`
- [ ] **Abstract Class**: Buat `model/Event.java`
- [ ] **Subclass**: Buat `model/Concert.java` (Polymorphic price & refund policy)
- [ ] **Subclass**: Buat `model/Seminar.java` (Polymorphic price & refund policy)
- [ ] **Subclass**: Buat `model/SportMatch.java` (Polymorphic price, non-refundable)
- [ ] **Repository**: Buat `repository/EventRepository.java` (Discriminator type mapping & capacities table CRUD)
- [ ] **Service**: Buat `service/EventService.java` (Overlap date/venue check & polymorphic price-summary)
- [ ] **Handler**: Buat `handler/EventHandler.java` (Routing `/api/events/*`)

---

### Grevi
- [ ] **Model**: Buat `model/Ticket.java`
- [ ] **Repository**: Buat `repository/TicketRepository.java` (CRUD SQLite & status update)
- [ ] **Service**: Buat `service/TicketService.java` (Logic booking, stock reduction, & polymorphic refund engine)
- [ ] **Handler**: Buat `handler/TicketHandler.java` (Routing `/api/tickets/*` & sales report)
