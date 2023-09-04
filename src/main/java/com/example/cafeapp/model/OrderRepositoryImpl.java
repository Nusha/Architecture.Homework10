package com.example.cafeapp.model;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class OrderRepositoryImpl implements OrderRepository {
    private final LoadingCache<String, Order> cache;

    public OrderRepositoryImpl() {
        cache = Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build(this::loadOrderFromDb);
    }

    private Order loadOrderFromDb(String id) {
        Order order = null;
        DbCredentials db = new DbCredentials();
        String sql = "SELECT * FROM Orders WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(db.getDbUrl(), db.getDbUser(), db.getDbPassword());
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                order = new Order();

                order.setId(rs.getString("id"));
                // Set other properties of order from the ResultSet
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle exceptions as appropriate for your application
        }

        return order;
    }


    @Override
    public Order save(Order order) {
        DbCredentials db = new DbCredentials();
        String sql = "INSERT INTO Orders (id, ...) VALUES (?, ...)";

        try (Connection conn = DriverManager.getConnection(db.getDbUrl(), db.getDbUser(), db.getDbPassword());
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, order.getId());


            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                cache.put(order.getId(), order);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();

        }

        return order;
    }


    @Override
    public Order findById(String id) {
        Order order = null;
        DbCredentials db = new DbCredentials();
        String sql = "SELECT * FROM Orders WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(db.getDbUrl(), db.getDbUser(), db.getDbPassword());
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                order = new Order();

                order.setId(rs.getString("id"));

            }
        } catch (SQLException ex) {
            ex.printStackTrace();

        }

        return order;
    }

    @Override
    public List<Order> findAll(int page, int size) {
        List<Order> orders = new ArrayList<>();
        DbCredentials db = new DbCredentials();
        String sql = "SELECT * FROM Orders LIMIT ? OFFSET ?";

        try (Connection conn = DriverManager.getConnection(db.getDbUrl(), db.getDbUser(), db.getDbPassword());
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, size);
            ps.setInt(2, (page - 1) * size);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getString("id"));

                orders.add(order);
                cache.put(order.getId(), order);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();

        }

        return orders;
    }


    @Override
    public void deleteById(String id) {
        DbCredentials db = new DbCredentials();
        String sql = "DELETE FROM Orders WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(db.getDbUrl(), db.getDbUser(), db.getDbPassword());
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            int rowsDeleted = ps.executeUpdate();
            cache.invalidate(id);

            if (rowsDeleted > 0) {
                System.out.println("Record with ID " + id + " deleted successfully.");
            } else {
                System.out.println("No record found with ID " + id + ".");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();

        }
    }


}
