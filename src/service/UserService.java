package service;

import database.DatabaseManager;
import exception.UserNotFoundException;
import model.User;
import repository.UserRepository;

import java.sql.*;
import java.util.*;

public class UserService {

    private final UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepository();
    }

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(User user) throws SQLException, IllegalArgumentException {
        validateUserFields(user);

        if (user.getId() == null || user.getId().isEmpty()) {
            user.setId(UUID.randomUUID().toString());
        }
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        }

        userRepository.save(user);
        return user;
    }

    public User findById(String id) throws SQLException, UserNotFoundException {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new UserNotFoundException("User with id '" + id + "' not found.");
        }
        return user;
    }

    public List<User> findAll(String roleFilter) throws SQLException {
        return userRepository.findAll(roleFilter);
    }

    public User update(String id, User updatedData) throws SQLException, UserNotFoundException {
        User existing = findById(id);

        if (updatedData.getName() != null) existing.setName(updatedData.getName());
        if (updatedData.getEmail() != null) existing.setEmail(updatedData.getEmail());
        if (updatedData.getPhone() != null) existing.setPhone(updatedData.getPhone());
        if (updatedData.getRole() != null) existing.setRole(updatedData.getRole());

        userRepository.update(existing);
        return existing;
    }

    public Map<String, Object> getActivitySummary(User user) throws SQLException {
        if ("buyer".equals(user.getRole())) {
            return getBuyerSummary(user.getId());
        }
        return getOrganizerSummary(user.getId());
    }

    private Map<String, Object> getBuyerSummary(String buyerId) throws SQLException {
        String sql = "SELECT COUNT(*) AS total_tickets, COALESCE(SUM(t.price), 0) AS total_spending "
                   + "FROM tickets t "
                   + "WHERE t.buyer_id = ? AND t.status != 'refunded'";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, buyerId);
            try (ResultSet rs = stmt.executeQuery()) {
                Map<String, Object> summary = new LinkedHashMap<>();
                if (rs.next()) {
                    summary.put("total_tickets_bought", rs.getInt("total_tickets"));
                    summary.put("total_spending", rs.getDouble("total_spending"));
                } else {
                    summary.put("total_tickets_bought", 0);
                    summary.put("total_spending", 0.0);
                }
                return summary;
            }
        }
    }

    private Map<String, Object> getOrganizerSummary(String organizerId) throws SQLException {
        String sqlEvents = "SELECT COUNT(*) AS total_events FROM events WHERE organizer_id = ?";
        String sqlRevenue = "SELECT COALESCE(SUM(t.price), 0) AS total_revenue "
                          + "FROM tickets t "
                          + "JOIN events e ON t.event_id = e.id "
                          + "WHERE e.organizer_id = ? AND t.status != 'refunded'";

        Map<String, Object> summary = new LinkedHashMap<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmtEvents = conn.prepareStatement(sqlEvents)) {
            stmtEvents.setString(1, organizerId);
            try (ResultSet rs = stmtEvents.executeQuery()) {
                summary.put("total_events_created", rs.next() ? rs.getInt("total_events") : 0);
            }
        }

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmtRevenue = conn.prepareStatement(sqlRevenue)) {
            stmtRevenue.setString(1, organizerId);
            try (ResultSet rs = stmtRevenue.executeQuery()) {
                summary.put("total_revenue", rs.next() ? rs.getDouble("total_revenue") : 0.0);
            }
        }

        return summary;
    }

    private void validateUserFields(User user) throws IllegalArgumentException {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required.");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required.");
        }
        if (user.getRole() == null || (!user.getRole().equals("buyer") && !user.getRole().equals("organizer"))) {
            throw new IllegalArgumentException("Role must be 'buyer' or 'organizer'.");
        }
    }
}
