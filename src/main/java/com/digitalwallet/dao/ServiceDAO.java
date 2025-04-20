package com.digitalwallet.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.digitalwallet.model.Service;
import com.digitalwallet.util.DatabaseUtil;

public class ServiceDAO {
    public List<Service> getAllServices() throws SQLException {
        List<Service> services = new ArrayList<>();
        String query = "SELECT * FROM services";

        try (Connection connection = DatabaseUtil.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                services.add(mapResultSetToService(rs));
            }
        }

        return services;
    }

    public List<String> getServiceTypes() throws SQLException {
        List<String> serviceTypes = new ArrayList<>();
        String query = "SELECT DISTINCT service_name FROM services";

        try (Connection connection = DatabaseUtil.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                serviceTypes.add(rs.getString("service_name"));
            }
        }

        return serviceTypes;
    }

    public List<String> getServiceProviders(String serviceName) throws SQLException {
        List<String> providers = new ArrayList<>();
        String query = "SELECT DISTINCT service_provider FROM services WHERE service_name = ?";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, serviceName);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                providers.add(rs.getString("service_provider"));
            }
        }

        return providers;
    }

    public Service getServiceByNameAndProvider(String serviceName, String serviceProvider) throws SQLException {
        String query = "SELECT * FROM services WHERE service_name = ? AND service_provider = ?";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, serviceName);
            pstmt.setString(2, serviceProvider);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToService(rs);
            }
        }

        return null;
    }

    public List<Service> getServicesByName(String serviceName) throws SQLException {
        List<Service> services = new ArrayList<>();
        String query = "SELECT * FROM services WHERE service_name = ?";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, serviceName);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                services.add(mapResultSetToService(rs));
            }
        }

        return services;
    }

    private Service mapResultSetToService(ResultSet rs) throws SQLException {
        return new Service(
            rs.getInt("service_id"),
            rs.getString("service_name"),
            rs.getString("service_provider")
        );
    }
} 