package project.lyit.hotel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Customer {
	
	DatabaseConnector dbConnect;
	Connection conn;
	Statement stmt;
	
	public Customer() {
		dbConnect = new DatabaseConnector();
	}
	
	public void addCustomer(int custNo, String fName, String lName, String addr, String phone, String email) {
		conn = dbConnect.connectToDatabase();
	 	String sql = "INSERT INTO customer VALUES (" + custNo + ", '"
	 			+ fName + "', '" + lName + "', '" + addr + "', '" + phone + "', '" + email + "')";
	 	try {
	 		stmt = conn.createStatement();
	 		stmt.executeUpdate(sql);
	 		System.out.println("SUCCESS!!!");
	 	} catch (SQLException e) {
	 		e.printStackTrace();
	 	} finally {
	 		try {
				stmt.close();
				dbConnect.closeDatabaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
	 	}
	}
	
	public void editCustomer(int custNo, String fName, String lName, String addr, String phone, String email) {
		conn = dbConnect.connectToDatabase();
		String sql = "UPDATE customer SET FirstName='" + fName + "', LastName='" + lName + 
					"', Address= '" + addr + "', Phone='" + phone + "', Email='" + email + "' WHERE CustomerNo=" + custNo;
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			System.out.println("Customer UPDATED!!!");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stmt.close();
				dbConnect.closeDatabaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void deleteCustomer(int custNo) {
		conn = dbConnect.connectToDatabase();
		String sql = "DELETE FROM customer WHERE CustomerNo=" + custNo;
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			System.out.println("Customer DELETED!!!");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stmt.close();
				dbConnect.closeDatabaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public int getNextNo() {
		conn = dbConnect.connectToDatabase();
		String sql = "SELECT * FROM customer";
		int lastNo = 1000;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				lastNo = rs.getInt("CustomerNo");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stmt.close();
				dbConnect.closeDatabaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return ++lastNo;
	}

	public ArrayList<Integer> getExistingCustomers() {
		ArrayList<Integer> existingCustomers = new ArrayList<>();
		conn = dbConnect.connectToDatabase();
		String sql = "SELECT * FROM customer";
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				existingCustomers.add(rs.getInt("CustomerNo"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stmt.close();
				dbConnect.closeDatabaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return existingCustomers;
	}

	public String[] getCustomerDetails(int custNo) {
		String[] details = new String[5];
		conn = dbConnect.connectToDatabase();
		String sql = "SELECT * FROM customer WHERE CustomerNo=" + custNo;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				details[0] = rs.getString("FirstName");
				details[1] = rs.getString("LastName");
				details[2] = rs.getString("Address");
				details[3] = rs.getString("Phone");
				details[4] = rs.getString("Email");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stmt.close();
				dbConnect.closeDatabaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return details;
	}
}
