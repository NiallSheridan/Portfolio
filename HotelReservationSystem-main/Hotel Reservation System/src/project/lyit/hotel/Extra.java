package project.lyit.hotel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class Extra {
	
	private DatabaseConnector dbConnect;
	private Connection conn;
	private Statement stmt;
	private HashMap<String, Double> extraOpts = new HashMap<String, Double>();
	private final DecimalFormat df = new DecimalFormat("0.00");
	
	public Extra() {
		dbConnect = new DatabaseConnector();
		extraOpts.put("Coffee", 2.5);
		extraOpts.put("Tea", 2.0);
		extraOpts.put("Breakfast", 7.5);
	}

	public HashMap<String, Double> getExtraOptions() {
		return extraOpts;
	}

	//Adds an extra to the database - all info is filled in or selected through controlled fields
	//so no need for further validation
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
	
	//Edits an extra in the database
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

	//Deletes an extra from the database
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

	//gets next number available to set as unique primary key in database
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

	//Gets all existing extraNo in the database
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
	
	//Gets list of extras on active bookings
	public ArrayList<Integer> getActiveExtraList() {
		ArrayList<Integer> activeBookings = new ArrayList<>();
		ArrayList<Integer> activeExtras = new ArrayList<>();
		conn = dbConnect.connectToDatabase();
		String sqlBookings = "SELECT * FROM booking WHERE CheckIn = true AND CheckOutTotal = 0";
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sqlBookings);
			while(rs.next()) {
				activeBookings.add(rs.getInt("BookingNo"));
			}
			for (int booking : activeBookings) {
				String sqlExtras = "SELECT * FROM extra WHERE BookingNo = " + booking;
				stmt = conn.createStatement();
				ResultSet rs2 = stmt.executeQuery(sqlExtras);
				while(rs2.next()) {
					activeExtras.add(rs2.getInt("ExtraNo"));
				}
			}
		} catch (SQLException e) {
			System.out.println("Database error");
		} finally {
			try {
				stmt.close();
				dbConnect.closeDatabaseConnection(conn);
			} catch (SQLException e) {
				System.out.println("Database error!");
			}
		}
		return activeExtras;
	}

	//Gets extra details of a specific extra
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

	//Gets all extras for a specific booking for the bill
	public ArrayList<String> getExtrasForBill(int no) {
		conn = dbConnect.connectToDatabase();
		ArrayList<String> extras = new ArrayList<>();
		String sql = "SELECT * FROM extra WHERE BookingNo = " + no;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				extras.add(rs.getString("Qty") + " x " + rs.getString("Type") 
							+ " @ €" + df.format(rs.getDouble("Cost")) 
							+ " Total: €" + df.format(rs.getDouble("Total")));
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
		return extras;
	}

	//Gets overall cost of extras for specific bill
	public double getExtraCostForBill(int no) {
		conn = dbConnect.connectToDatabase();
		double totalCost = 0;
		String sql = "SELECT * FROM extra WHERE BookingNo = " + no;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				totalCost += rs.getDouble("Total");
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
		return totalCost;
	}
}
