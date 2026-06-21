package repository;

import database.DatabaseManager;
import model.Venue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * repositori JDBC venue.
 *
 * @author Oka
 */
public class VenueRepository {

    /** Pemetaan ResultSet (Rows). */
    private Venue mapRow(ResultSet rs) throws SQLException {
        return new Venue(
            rs.getString("id"),
            rs.getString("name"),
            rs.getString("address"),
            rs.getInt("max_capacity"),
            rs.getString("created_at")
        );
    }

    public List<Venue> findAll() throws SQLException {
        List<Venue> venues = new ArrayList<>();
        String sql = "SELECT * FROM venues ORDER BY created_at DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) venues.add(mapRow(rs));
        }
        return venues;
    }

    public Venue findById(String id) throws SQLException {
        String sql = "SELECT * FROM venues WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public void save(Venue venue) throws SQLException {
        String sql = "INSERT INTO venues (id, name, address, max_capacity) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, venue.getId());
            ps.setString(2, venue.getName());
            ps.setString(3, venue.getAddress());
            ps.setInt(4, venue.getMaxCapacity());
            ps.executeUpdate();
        }
    }

    public void update(Venue venue) throws SQLException {
        String sql = "UPDATE venues SET name = ?, address = ?, max_capacity = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, venue.getName());
            ps.setString(2, venue.getAddress());
            ps.setInt(3, venue.getMaxCapacity());
            ps.setString(4, venue.getId());
            ps.executeUpdate();
        }
    }

    /** Mengembalikan ringkasan (id, name, date) dari venue tertentu. */
    public List<java.util.Map<String, Object>> findEventsByVenueId(String venueId) throws SQLException {
        List<java.util.Map<String, Object>> events = new ArrayList<>();
        String sql = "SELECT id, name, date FROM events WHERE venue_id = ? ORDER BY date ASC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, venueId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    java.util.Map<String, Object> e = new java.util.HashMap<>();
                    e.put("id", rs.getString("id"));
                    e.put("name", rs.getString("name"));
                    e.put("date", rs.getString("date"));
                    events.add(e);
                }
            }
        }
        return events;
    }

    /** Generate venue ID (VNU-001, VNU-002, VNU-00x). */
    public String generateNextId() throws SQLException {
        String sql = "SELECT COUNT(*) FROM venues";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            int count = rs.next() ? rs.getInt(1) : 0;
            return String.format("VNU-%03d", count + 1);
        }
    }
}
