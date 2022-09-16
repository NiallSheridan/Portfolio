package project.lyit.hotel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class Extra {
	
	private DatabaseConnector dbConnect;
	private Connection conn;
	private Statement stmt;
	private HashMap<String, Double> extraOpts = new HashMap<String, Double>();
	
	public Extra() {
		dbConnect = new DatabaseConnector();
		extraOpts.put("Coffee", 2.5);
		extraOpts.put("Tea", 2.0);
		extraOpts.put("Breakfast", 7.5);
	}

	public HashMap<String, Double> getExtraOptions() {
		return extraOpts;
	}

	public void addExtra(int extraNo, String type, int qty, double cost, double total, int bookingNo) {
		conn = dbConnect.connectToDatabase();
	 	String sql = "INSERT INTO extra VALUES (" + extraNo + ", '"
	 			+ type + "', " + qty + ", " + cost + ", " + total + ", " + bookingNo + ")";
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
	
	public void editExtra(int extraNo, String type, int qty, double cost, double total, int bookingNo) {
		conn = dbConnect.connectToDatabase();
		String sql = "UPDATE extra SET Type='" + type + "', Qty=" + qty + 
					", Cost=" + cost + ", Total=" + total + ", BookingNo=" + bookingNo + " WHERE ExtraNo=" + extraNo;
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			System.out.println("Extra UPDATED!!!");
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

	public void deleteExtra(int extraNo) {
		conn = dbConnect.connectToDatabase();
		String sql = "DELETE FROM extra WHERE ExtraNo=" + extraNo;
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			System.out.println("Extra DELETED!!!");
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
		String sql = "SELECT * FROM extra";
		int lastNo = 10000;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				lastNo = rs.getInt("ExtraNo");
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

	public ArrayList<Integer> getExistingExtras() {
		ArrayList<Integer> existingExtras = new ArrayList<>();
		conn = dbConnect.connectToDatabase();
		String sql = "SELECT * FROM extra";
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				existingExtras.add(rs.getInt("ExtraNo"));
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
		return existingExtras;
	}

	public String[] getExtraDetails(int extraNo) {
		String[] details = new String[5];
		conn = dbConnect.connectToDatabase();
		String sql = "SELECT * FROM extra WHERE ExtraNo=" + extraNo;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				details[0] = rs.getString("Type");
				details[1] = "" + rs.getInt("Qty");
				details[2] = "" + rs.getDouble("Cost");
				details[3] = "" + rs.getDouble("Total");
				details[4] = "" + rs.getInt("BookingNo");
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
