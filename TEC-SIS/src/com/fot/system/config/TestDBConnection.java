package com.fot.system.config;

import java.sql.Connection;

public class TestDBConnection {

    public static void main(String[] args) {

        try {
            Connection conn = DBConnection.getInstance().getConnection();

            if (conn != null && !conn.isClosed()) {
                System.out.println(" Database connected successfully!");
            } else {
                System.out.println(" Failed to connect to database.");
            }

        } catch (Exception e) {
            System.out.println("❌ Error occurred while connecting:");
            e.printStackTrace();
        }
    }
}