package project.lyit.tester;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnectorTester {
	
	Connection conn = null;
	Statement stmt = null;
	
	public static void main(String[] args) {
		DatabaseConnectorTester dbTest = new DatabaseConnectorTester();

		//ADD, EDIT, DELETE ROOM!!!!!!!!!
		//dbTest.addRoom();
		dbTest.editRoom();
		//dbTest.deleteRoom();
		
		//ADD, EDIT, DELETE CUSTOMER!!!!!!!!!
		//dbTest.addCustomer();
		//dbTest.editCustomer();
		//dbTest.deleteCustomer();
	}

	private void addRoom() {
		conn = connectToDatabase();
		String sql = "INSERT INTO room VALUES (10, 'single', false)";
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			System.out.println("Room ADDED!!!");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeDatabaseConnection();
		}
	}

	private void editRoom() {
		conn = connectToDatabase();
		String sql = "UPDATE room SET RoomType='double' WHERE RoomNo=10";
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			System.out.println("Room UPDATED!!!");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeDatabaseConnection();
		}
	}

	private void deleteRoom() {
		conn = connectToDatabase();
		String sql = "DELETE FROM room WHERE RoomNo=10";
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			System.out.println("Room DELETED!!!");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeDatabaseConnection();
		}
	}

	private void addCustomer() {
		conn = connectToDatabase();
	 	String sql = "INSERT INTO customer VALUES (1000, "
	 			+ "'Vanessa', 'Aiken', 'Letterkenny', '0898765678', 'va@hotmail.com')";
	 	try {
	 		stmt = conn.createStatement();
	 		stmt.executeUpdate(sql);
	 		System.out.println("SUCCESS!!!");
	 	} catch (SQLException e) {
	 		e.printStackTrace();
	 	} finally {
			closeDatabaseConnection();
	 	}
	}
	
	private void editCustomer() {
		conn = connectToDatabase();
		String sql = "UPDATE customer SET email='Vanessa@lyit.ie' WHERE CustomerNo=1000";
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			System.out.println("Customer UPDATED!!!");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeDatabaseConnection();
		}
	}

	private void deleteCustomer() {
		conn = connectToDatabase();
		String sql = "DELETE FROM customer WHERE CustomerNo=1000";
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			System.out.println("Customer DELETED!!!");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeDatabaseConnection();
		}
	}
	
	private Connection connectToDatabase() {
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
	
	private void closeDatabaseConnection() {
		try {
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
