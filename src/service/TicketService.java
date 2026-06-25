package service;

import database.DatabaseManager;
import exception.EventNotFoundException;
import exception.RefundNotAllowedException;
import exception.TicketSoldOutException;
import model.Event;
import model.Refundable;
import model.Ticket;
import repository.EventRepository;
import repository.TicketRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/**
 * Service layer untuk logika transaksi tiket.
 * Menangani pembelian tiket (cek kuota, hitung harga polimorfis)
 * dan proses refund (validasi Refundable, hitung nominal, kembalikan kuota).
 *
 * @author Grevi
 */
public class TicketService {

    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;

    public TicketService(TicketRepository ticketRepository, EventRepository eventRepository) {
        this.ticketRepository = ticketRepository;
        this.eventRepository = eventRepository;
    }

    // =============================================
    // GET ALL TICKETS (dengan filter opsional)
    // =============================================

    /**
     * Mengambil daftar tiket dengan filter opsional eventId / userId / status.
     * Semua parameter boleh null (berarti tidak difilter).
     */
    public List<Ticket> getAllTickets(String eventId, String userId, String status) {
        return ticketRepository.findAll(eventId, userId, status);
    }

    // =============================================
    // GET TICKET BY ID
    // =============================================

    /**
     * Mencari tiket berdasarkan ID.
     *
     * @throws EventNotFoundException jika tiket tidak ditemukan
     */
    public Ticket getTicketById(String id) throws EventNotFoundException {
        Ticket ticket = ticketRepository.findById(id);
        if (ticket == null) {
            throw new EventNotFoundException("Tiket dengan id '" + id + "' tidak ditemukan.");
        }
        return ticket;
    }

    // =============================================
    // BUY TICKET
    // =============================================

    /**
     * Memproses pembelian tiket.
     *
     * Alur:
     * 1. Validasi event ada
     * 2. Cek ketersediaan kuota kategori di tabel capacities
     * 3. Hitung unit_price via polimorfisme (event.calculateTicketPrice)
     * 4. Simpan record tiket baru
     * 5. Kurangi filled di tabel capacities (atomic via transaction)
     *
     * @param eventId  ID event yang dituju
     * @param userId   ID user pembeli
     * @param category Kategori tiket (vip, regular, festival, dll.)
     * @param quantity Jumlah tiket yang dibeli
     * @return Objek Ticket yang berhasil dibuat
     */
    public Ticket buyTicket(String eventId, String userId, String category, int quantity)
            throws EventNotFoundException, TicketSoldOutException, SQLException {

        // 1. Validasi event
        Event event = eventRepository.findById(eventId);
        if (event == null) {
            throw new EventNotFoundException(eventId);
        }

        // 2. Cek dan kurangi kapasitas (atomic dalam satu transaksi)
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Cek sisa kapasitas untuk kategori ini
                int available = getAvailableCapacity(conn, eventId, category);
                if (available < quantity) {
                    throw new TicketSoldOutException(category);
                }

                // 3. Hitung harga via polimorfisme
                double unitPrice = event.calculateTicketPrice(category);
                double totalPrice = unitPrice * quantity;

                // 4. Buat objek tiket
                String ticketId = "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                String purchaseDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

                Ticket ticket = new Ticket(
                        ticketId,
                        eventId,
                        userId,
                        category,
                        quantity,
                        unitPrice,
                        totalPrice,
                        purchaseDate,
                        "active",
                        0.0
                );

                // 5. Simpan tiket
                ticketRepository.save(ticket);

                // 6. Update filled di tabel capacities
                updateFilledCapacity(conn, eventId, category, quantity);

                conn.commit();
                return ticket;

            } catch (TicketSoldOutException e) {
                conn.rollback();
                throw e;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    // =============================================
    // REFUND TICKET
    // =============================================

    /**
     * Memproses refund tiket.
     *
     * Alur:
     * 1. Cari tiket berdasarkan ID
     * 2. Validasi tiket masih aktif (belum direfund)
     * 3. Cari event terkait
     * 4. Cek apakah event implements Refundable (instanceof)
     * 5. Hitung selisih hari ke tanggal event
     * 6. Hitung nominal refund = rasio * totalPrice
     * 7. Update status tiket menjadi "refunded" + simpan refund_amount
     * 8. Kembalikan kuota kapasitas (kurangi filled)
     *
     * @param ticketId ID tiket yang akan direfund
     * @return Objek Ticket setelah direfund
     * @throws RefundNotAllowedException jika event tidak support refund (mis. SportMatch)
     */
    public Ticket refundTicket(String ticketId)
            throws EventNotFoundException, RefundNotAllowedException, SQLException {

        // 1. Cari tiket
        Ticket ticket = ticketRepository.findById(ticketId);
        if (ticket == null) {
            throw new EventNotFoundException("Tiket dengan id '" + ticketId + "' tidak ditemukan.");
        }

        // 2. Validasi status
        if (!"active".equalsIgnoreCase(ticket.getStatus())) {
            throw new RefundNotAllowedException("Tiket sudah berstatus '" + ticket.getStatus() + "', tidak dapat direfund.");
        }

        // 3. Cari event
        Event event = eventRepository.findById(ticket.getEventId());
        if (event == null) {
            throw new EventNotFoundException(ticket.getEventId());
        }

        // 4. Cek Refundable via instanceof (downcasting aman)
        if (!(event instanceof Refundable)) {
            throw new RefundNotAllowedException(
                    "Event bertipe '" + event.getType() + "' tidak mendukung refund."
            );
        }

        Refundable refundableEvent = (Refundable) event;

        // Cek flag isRefundable (untuk keamanan ekstra)
        if (!refundableEvent.isRefundable()) {
            throw new RefundNotAllowedException("Event ini tidak menerima refund.");
        }

        // 5. Hitung selisih hari
        LocalDate today = LocalDate.now();
        LocalDate eventDate = LocalDate.parse(event.getDate(), DateTimeFormatter.ISO_LOCAL_DATE);
        long daysBeforeEvent = ChronoUnit.DAYS.between(today, eventDate);

        // 6. Hitung nominal refund
        double refundRatio = refundableEvent.calculateRefund((int) daysBeforeEvent);
        double refundAmount = refundRatio * ticket.getTotalPrice();

        // 7 & 8. Update status tiket dan kembalikan kapasitas (atomic)
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Update tiket ke status refunded
                ticketRepository.updateStatus(ticketId, "refunded", refundAmount);

                // Kembalikan kuota (kurangi filled)
                restoreCapacity(conn, ticket.getEventId(), ticket.getCategory(), ticket.getQuantity());

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }

        // Kembalikan tiket dengan data terbaru
        ticket.setStatus("refunded");
        ticket.setRefundAmount(refundAmount);
        return ticket;
    }

    // =============================================
    // SALES REPORT
    // =============================================

    /**
     * Mengambil laporan penjualan tiket untuk event tertentu.
     * Hanya mengembalikan tiket yang berstatus "active" (tidak termasuk refunded).
     *
     * @param eventId ID event (wajib diisi)
     * @return List tiket aktif untuk event tersebut
     */
    public List<Ticket> getSalesReport(String eventId) throws EventNotFoundException, SQLException {
        Event event = eventRepository.findById(eventId);
        if (event == null) {
            throw new EventNotFoundException(eventId);
        }
        return ticketRepository.findAll(eventId, null, "active");
    }

    // =============================================
    // PRIVATE HELPER METHODS
    // =============================================

    /**
     * Membaca sisa kapasitas yang tersedia (total - filled) untuk kategori tiket.
     * Menggunakan kolom 'total' sesuai skema database asli di tabel capacities.
     */
    private int getAvailableCapacity(Connection conn, String eventId, String category) throws SQLException {
        String sql = "SELECT total, filled FROM capacities WHERE event_id = ? AND category = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, eventId);
            ps.setString(2, category);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("total");
                    int filled = rs.getInt("filled");
                    return total - filled;
                }
            }
        }
        // Jika kategori tidak ditemukan di capacities, anggap habis
        return 0;
    }

    /**
     * Menambah nilai filled di tabel capacities saat tiket dibeli.
     */
    private void updateFilledCapacity(Connection conn, String eventId, String category, int quantity)
            throws SQLException {
        String sql = "UPDATE capacities SET filled = filled + ? WHERE event_id = ? AND category = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setString(2, eventId);
            ps.setString(3, category);
            ps.executeUpdate();
        }
    }

    /**
     * Mengurangi nilai filled di tabel capacities saat tiket direfund
     * (mengembalikan kuota agar bisa dijual kembali).
     */
    private void restoreCapacity(Connection conn, String eventId, String category, int quantity)
            throws SQLException {
        String sql = "UPDATE capacities SET filled = MAX(0, filled - ?) WHERE event_id = ? AND category = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setString(2, eventId);
            ps.setString(3, category);
            ps.executeUpdate();
        }
    }
}
