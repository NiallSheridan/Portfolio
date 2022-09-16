package project.lyit.hotel;

import java.sql.*;

public class DatabaseConnector {

	Connection conn = null;
	
	public Connection connectToDatabase() {
		// database URL and credentials
		final String DB_URL = "jdbc:mysql://localhost/hotel_reservation_system";
		final String USER = "root";
		final String PASS = "";
		
		try {
			// STEP 2: Register JDBC driver
			Class.forName("com.mysql.cj.jdbc.Driver");

			// STEP 3: Open a connection
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		}
		return conn;
	}
	
	public void closeDatabaseConnection(Connection conn) {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
