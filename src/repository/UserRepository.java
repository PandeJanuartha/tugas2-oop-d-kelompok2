package repository;

import database.DatabaseManager;
import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    public void save(User user) throws SQLException {
        String sql = "INSERT INTO users (id, name, email, phone, role, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getId());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPhone());
            stmt.setString(5, user.getRole());
            // Simpan sebagai string ISO agar SQLite bisa baca balik tanpa error parsing
            String createdAtStr = user.getCreatedAt() != null
                ? new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(user.getCreatedAt())
                : new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(new java.util.Date());
            stmt.setString(6, createdAtStr);
            stmt.executeUpdate();
        }
    }

    public User findById(String id) throws SQLException {
        String sql = "SELECT id, name, email, phone, role, created_at FROM users WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        }
    }

    public List<User> findAll(String roleFilter) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT id, name, email, phone, role, created_at FROM users");
        if (roleFilter != null && !roleFilter.isEmpty()) {
            sql.append(" WHERE role = ?");
        }

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            if (roleFilter != null && !roleFilter.isEmpty()) {
                stmt.setString(1, roleFilter);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                List<User> users = new ArrayList<>();
                while (rs.next()) {
                    users.add(mapRow(rs));
                }
                return users;
            }
        }
    }

    public void update(User user) throws SQLException {
        String sql = "UPDATE users SET name = ?, email = ?, phone = ?, role = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPhone());
            stmt.setString(4, user.getRole());
            stmt.setString(5, user.getId());
            stmt.executeUpdate();
        }
    }

    public String generateNextId() throws SQLException {
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            int count = rs.next() ? rs.getInt(1) : 0;
            return String.format("USR-%03d", count + 1);
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        String phoneVal = rs.getString("phone");
        if (phoneVal != null && phoneVal.startsWith("'") && phoneVal.endsWith("'") && phoneVal.length() >= 2) {
            phoneVal = phoneVal.substring(1, phoneVal.length() - 1);
        }

        return new User(
            rs.getString("id"),
            rs.getString("name"),
            rs.getString("email"),
            phoneVal,
            rs.getString("role"),
            parseTimestamp(rs.getString("created_at"))
        );
    }

    private Timestamp parseTimestamp(String val) {
        if (val == null || val.trim().isEmpty()) {
            return null;
        }
        val = val.trim();
        if (val.startsWith("'") && val.endsWith("'") && val.length() >= 2) {
            val = val.substring(1, val.length() - 1);
        }
        if (val.matches("\\d+")) {
            try {
                return new Timestamp(Long.parseLong(val));
            } catch (NumberFormatException e) {
                // fall through
            }
        }
        try {
            return Timestamp.valueOf(val);
        } catch (IllegalArgumentException e) {
            try {
                String normalized = val.replace("T", " ");
                if (val.contains("Z") || val.contains("+") || (val.lastIndexOf("-") > 10)) {
                    java.time.Instant instant = java.time.Instant.parse(val);
                    return Timestamp.from(instant);
                }
                return Timestamp.valueOf(normalized);
            } catch (Exception ex) {
                return new Timestamp(System.currentTimeMillis());
            }
        }
    }
}
