package com.example.trust.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbc;

    public UserRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public boolean existsByEmail(String email) {
        Integer c = jdbc.queryForObject("SELECT COUNT(*) FROM users WHERE email = ?", Integer.class, email);
        return c != null && c > 0;
    }

    public int createUser(String email, String passwordHash, String fullName) {
        String sql = "INSERT INTO users(email,password_hash,full_name) VALUES(?,?,?)";
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, email);
            ps.setString(2, passwordHash);
            ps.setString(3, fullName);
            return ps;
        }, kh);

        Number key = kh.getKey();
        if (key == null) throw new IllegalStateException("User id üretilemedi");
        return key.intValue();
    }

    public void addRole(int userId, String roleName) {
        Integer roleId = jdbc.queryForObject(
                "SELECT id FROM roles WHERE name = ?",
                Integer.class,
                roleName
        );

        if (roleId == null) throw new IllegalStateException("Role bulunamadı: " + roleName);

        jdbc.update(
                "INSERT INTO user_roles(user_id, role_id) VALUES(?,?)",
                userId, roleId
        );
    }


    public Optional<UserAuth> findAuthByEmail(String email) {
        List<UserAuth> list = jdbc.query(
                "SELECT id, email, password_hash, full_name, enabled FROM users WHERE email = ?",
                (rs, n) -> new UserAuth(
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("password_hash"),
                        rs.getString("full_name"),
                        rs.getBoolean("enabled"),
                        List.of()
                ),
                email
        );
        if (list.isEmpty()) return Optional.empty();

        UserAuth u = list.get(0);
        List<String> roles = jdbc.queryForList(
                "SELECT r.name FROM roles r JOIN user_roles ur ON ur.role_id=r.id WHERE ur.user_id=?",
                String.class,
                u.id()
        );

        return Optional.of(new UserAuth(u.id(), u.email(), u.passwordHash(), u.fullName(), u.enabled(), roles));
    }

    public Optional<UserProfile> findProfileById(int userId) {
        List<UserProfile> list = jdbc.query(
                "SELECT id, email, full_name FROM users WHERE id = ?",
                (rs, rn) -> new UserProfile(rs.getInt("id"), rs.getString("email"), rs.getString("full_name")),
                userId
        );
        if (list.isEmpty()) return Optional.empty();

        List<String> roles = jdbc.queryForList(
                "SELECT r.name FROM roles r JOIN user_roles ur ON ur.role_id=r.id WHERE ur.user_id=?",
                String.class,
                userId
        );
        UserProfile p = list.get(0);
        return Optional.of(new UserProfile(p.id(), p.email(), p.fullName(), roles));
    }

    public record UserAuth(int id, String email, String passwordHash, String fullName, boolean enabled,
                           List<String> roles) {
    }

    public record UserProfile(int id, String email, String fullName, List<String> roles) {
        public UserProfile(int id, String email, String fullName) {
            this(id, email, fullName, List.of());
        }

    }

}

