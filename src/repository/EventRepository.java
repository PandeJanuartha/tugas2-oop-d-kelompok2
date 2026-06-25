package repository;

import database.DatabaseManager;
import model.Event;
import model.Concert;
import model.Seminar;
import model.SportMatch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * repositori data access untuk persistensi entitas Event menggunakan JDBC standar.
 */
public class EventRepository {

    public EventRepository() {}

    public void save(Event event) throws SQLException {
        if (event == null) { return; }

        Connection conn = null;
        PreparedStatement psEvent = null;
        PreparedStatement psCapacity = null;

        String queryInsertEvent = "INSERT INTO events (id, type, name, venue_id, organizer_id, date, base_price) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String queryInsertCapacity = "INSERT INTO capacities (event_id, category, total) VALUES (?, ?, ?)";

        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false);

            psEvent = conn.prepareStatement(queryInsertEvent);
            this.bindEventStatement(psEvent, event);
            psEvent.executeUpdate();

            Map<String, Integer> capacities = event.getCapacities();
            if (capacities != null && !capacities.isEmpty()) {
                psCapacity = conn.prepareStatement(queryInsertCapacity);
                this.executeBatchCapacityInsert(psCapacity, event.getId(), capacities);
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) { conn.rollback(); }
            throw e;
        } finally {
            this.closeStatementResource(psCapacity);
            this.closeStatementResource(psEvent);
            this.closeConnectionResource(conn);
        }
    }

    public Event findById(String id) throws SQLException {
        if (id == null) { return null; }

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Event event = null;

        String queryFind = "SELECT * FROM events WHERE id = ?";

        try {
            conn = DatabaseManager.getConnection();
            ps = conn.prepareStatement(queryFind);
            ps.setString(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                event = this.createPolymorphicEventInstance(rs.getString("type"));
                if (event != null) {
                    this.populateEventFromResultSet(event, rs);
                    event.setCapacities(this.findCapacitiesByEventId(event.getId()));
                }
            }
        } finally {
            this.closeResultSetResource(rs);
            this.closeStatementResource(ps);
            this.closeConnectionResource(conn);
        }
        return event;
    }

    public List<Event> findAll() throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Event> loadedEvents = new ArrayList<Event>();

        String queryFindAll = "SELECT * FROM events";

        try {
            conn = DatabaseManager.getConnection();
            ps = conn.prepareStatement(queryFindAll);
            rs = ps.executeQuery();

            while (rs.next()) {
                Event event = this.createPolymorphicEventInstance(rs.getString("type"));
                if (event != null) {
                    this.populateEventFromResultSet(event, rs);
                    loadedEvents.add(event);
                }
            }
        } finally {
            this.closeResultSetResource(rs);
            this.closeStatementResource(ps);
            this.closeConnectionResource(conn);
        }

        for (Event e : loadedEvents) {
            e.setCapacities(this.findCapacitiesByEventId(e.getId()));
        }
        return loadedEvents;
    }

    public boolean hasConflictingEvent(String venueId, String date) throws SQLException {
        if (venueId == null || date == null) { return false; }

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean hasConflict = false;

        String queryCheckConflict = "SELECT COUNT(*) FROM events WHERE venue_id = ? AND date = ?";

        try {
            conn = DatabaseManager.getConnection();
            ps = conn.prepareStatement(queryCheckConflict);
            ps.setString(1, venueId);
            ps.setString(2, date);
            rs = ps.executeQuery();

            if (rs.next()) {
                hasConflict = rs.getInt(1) > 0;
            }
        } finally {
            this.closeResultSetResource(rs);
            this.closeStatementResource(ps);
            this.closeConnectionResource(conn);
        }
        return hasConflict;
    }


    private void bindEventStatement(PreparedStatement ps, Event event) throws SQLException {
        ps.setString(1, event.getId());
        ps.setString(2, event.getType());
        ps.setString(3, event.getName());
        ps.setString(4, event.getVenueId());
        ps.setString(5, event.getOrganizerId());
        ps.setString(6, event.getDate());
        ps.setDouble(7, event.getBasePrice());
    }

    private void executeBatchCapacityInsert(PreparedStatement ps, String eventId, Map<String, Integer> capacities) throws SQLException {
        for (Map.Entry<String, Integer> entry : capacities.entrySet()) {
            ps.setString(1, eventId);
            ps.setString(2, entry.getKey());
            ps.setInt(3, entry.getValue());
            ps.addBatch();
        }
        ps.executeBatch();
    }

    private Event createPolymorphicEventInstance(String type) {
        if (type == null) { return null; }
        String typeKey = type.toLowerCase();
        if (typeKey.equals("concert")) { return new Concert(); }
        if (typeKey.equals("seminar")) { return new Seminar(); }
        if (typeKey.equals("sport_match")) { return new SportMatch(); }
        return null;
    }

    private void populateEventFromResultSet(Event event, ResultSet rs) throws SQLException {
        event.setId(rs.getString("id"));
        event.setName(rs.getString("name"));
        event.setVenueId(rs.getString("venue_id"));
        event.setOrganizerId(rs.getString("organizer_id"));
        event.setDate(rs.getString("date"));
        event.setBasePrice(rs.getDouble("base_price"));
    }

    private Map<String, Integer> findCapacitiesByEventId(String eventId) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<String, Integer> parsedCapacities = new HashMap<String, Integer>();

        String queryLoadCapacity = "SELECT category, total FROM capacities WHERE event_id = ?";

        try {
            conn = DatabaseManager.getConnection();
            ps = conn.prepareStatement(queryLoadCapacity);
            ps.setString(1, eventId);
            rs = ps.executeQuery();

            while (rs.next()) {
                parsedCapacities.put(rs.getString("category"), rs.getInt("total"));
            }
        } finally {
            this.closeResultSetResource(rs);
            this.closeStatementResource(ps);
            this.closeConnectionResource(conn);
        }
        return parsedCapacities;
    }

    public void update(Event event) throws SQLException {
        if (event == null) { return; }
        String sql = "UPDATE events SET name = ?, date = ?, base_price = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, event.getName());
            ps.setString(2, event.getDate());
            ps.setDouble(3, event.getBasePrice());
            ps.setString(4, event.getId());
            ps.executeUpdate();
        }
    }

    public Map<String, Integer> findRemainingCapacities(String eventId) throws SQLException {
        Map<String, Integer> remaining = new HashMap<String, Integer>();
        String sql = "SELECT category, total, filled FROM capacities WHERE event_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int total = rs.getInt("total");
                    int filled = rs.getInt("filled");
                    remaining.put(rs.getString("category"), total - filled);
                }
            }
        }
        return remaining;
    }

    private void closeConnectionResource(Connection connection) {
        if (connection != null) {
            try { connection.close(); } catch (SQLException e) { /* Logging Suppressed */ }
        }
    }

    private void closeStatementResource(PreparedStatement statement) {
        if (statement != null) {
            try { statement.close(); } catch (SQLException e) { /* Logging Suppressed */ }
        }
    }

    private void closeResultSetResource(ResultSet resultSet) {
        if (resultSet != null) {
            try { resultSet.close(); } catch (SQLException e) { /* Logging Suppressed */ }
        }
    }
}