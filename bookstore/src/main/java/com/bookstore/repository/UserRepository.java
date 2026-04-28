package com.bookstore.repository;

import com.bookstore.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbc;

    private final RowMapper<User> userMapper = (rs, rowNum) -> {
        User u = new User();
        u.setId(rs.getLong("id"));
        u.setUserId(rs.getString("user_id"));
        u.setName(rs.getString("name"));
        u.setPassword(rs.getString("password"));
        u.setRole(rs.getString("role"));
        return u;
    };

    public Optional<User> findByUserId(String userId) {
        List<User> results = jdbc.query("SELECT * FROM users WHERE user_id = ?", userMapper, userId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public List<User> findAll() {
        return jdbc.query("SELECT * FROM users ORDER BY role, name", userMapper);
    }

    public boolean existsByUserId(String userId) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM users WHERE user_id=?", Integer.class, userId);
        return count != null && count > 0;
    }

    public void save(User user) {
        jdbc.update("INSERT INTO users (user_id, name, password, role) VALUES (?,?,?,?)",
            user.getUserId(), user.getName(), user.getPassword(), user.getRole());
    }
}
