package repository;

import database.DatabaseManager;
import model.Ticket;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketRepository {

    // Cari tiket berdasarkan ID
    public Ticket findById(String id) {
        String sql = "SELECT * FROM tickets WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Ambil semua tiket dengan filter opsional
    public List<Ticket> findAll(String eventId, String userId, String status) {

        List<Ticket> tickets = new ArrayList<>();

        StringBuilder sql = new StringBuilder("SELECT * FROM tickets WHERE 1=1");

        if (eventId != null)
            sql.append(" AND event_id = ?");

        if (userId != null)
            sql.append(" AND user_id = ?");

        if (status != null)
            sql.append(" AND status = ?");

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int index = 1;

            if (eventId != null)
                ps.setString(index++, eventId);

            if (userId != null)
                ps.setString(index++, userId);

            if (status != null)
                ps.setString(index++, status);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tickets.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tickets;
    }

    // Simpan transaksi tiket
    public boolean save(Ticket ticket) {

        String sql = "INSERT INTO tickets "
                + "(id, event_id, user_id, category, quantity, "
                + " unit_price, total_price, purchase_date, "
                + " status, refund_amount) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ticket.getId());
            ps.setString(2, ticket.getEventId());
            ps.setString(3, ticket.getUserId());
            ps.setString(4, ticket.getCategory());
            ps.setInt(5, ticket.getQuantity());
            ps.setDouble(6, ticket.getUnitPrice());
            ps.setDouble(7, ticket.getTotalPrice());
            ps.setString(8, ticket.getPurchaseDate());
            ps.setString(9, ticket.getStatus());
            ps.setDouble(10, ticket.getRefundAmount());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Update status refund
    public boolean updateStatus(String id, String status, double refundAmount) {

        String sql = "UPDATE tickets "
                + "SET status = ?, refund_amount = ? "
                + "WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setDouble(2, refundAmount);
            ps.setString(3, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Mapping ResultSet ke Object Ticket (pola mapRow)
    private Ticket mapRow(ResultSet rs) throws SQLException {

        return new Ticket(
                rs.getString("id"),
                rs.getString("event_id"),
                rs.getString("user_id"),
                rs.getString("category"),
                rs.getInt("quantity"),
                rs.getDouble("unit_price"),
                rs.getDouble("total_price"),
                rs.getString("purchase_date"),
                rs.getString("status"),
                rs.getDouble("refund_amount")
        );
    }
}
