package project.lyit.hotel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.time.LocalDate;

public class Booking {
	
	private DatabaseConnector dbConnect;
	private Connection conn;
	private Statement stmt;
	
	public Booking() {
		dbConnect = new DatabaseConnector();
	}

	public void addBooking(int bookingNo, String checkInDate, String checkOutDate, int custNo, int roomNo) {
		conn = dbConnect.connectToDatabase();
		String sql = "INSERT INTO booking VALUES (" + bookingNo + ", '" + checkInDate  + "', '" +  checkOutDate +  "', "  
													+ custNo + ", " + roomNo + ", false, false)";

		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			System.out.println("Booking ADDED!!!");
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

	public ArrayList<String> getBookingAvailability(LocalDate checkDate, LocalDate checkoutDate, String type) {	
		conn = dbConnect.connectToDatabase();
		//ArrayList<Integer> existingNums = room.getExistingRooms();
		ArrayList<Integer> unavailableNums = new ArrayList<>();
		ArrayList<String> availableToBook = new ArrayList<>();
		ArrayList<String> availability = new ArrayList<>();

		String sqlUnavailable = "SELECT RoomNo from booking WHERE (CheckInDate <= '" + checkDate.toString()
					+ "' AND CheckOutDate >= '" + checkDate.toString() + "') AND (CheckInDate <= '" + checkoutDate.toString()
					+ "' AND CheckOutDate >= '" + checkoutDate.toString() + "')";
		
		//SELECT RoomNo from booking WHERE (CheckIn <= '2022/04/01' AND CheckOut >= '2022/04/01') AND (CheckIn <= '2022/04/03' AND CheckOut >= '2022/04/03')
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sqlUnavailable);
			while(rs.next()) {
				unavailableNums.add(rs.getInt("RoomNo"));
			}
			String sqlAvailable = "SELECT * FROM room WHERE RoomType= '" + type + "' AND Decommissioned = false";
			rs = stmt.executeQuery(sqlAvailable);
			while (rs.next()) {
				availability.add("Room: " + rs.getInt("RoomNo") + " Room Type: " + rs.getString("RoomType"));
			}
			//LOGIC IS WRONG HERE!!!!!!!
			if (unavailableNums.size() > 0) {
				for (int no : unavailableNums) {
					for (String available : availability) {
						if (!(available.contains("" + no))) {
							availableToBook.add(available);
						}
					}
				}
			} else {
				for (String available : availability) {
					availableToBook.add(available);
				}
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
		return availableToBook;
	}
	
	public ArrayList<String> getExistingBookings() {
		ArrayList<String> existingBookings = new ArrayList<>();
		conn = dbConnect.connectToDatabase();
		String sql = "SELECT * FROM booking";
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				existingBookings.add("Room No: " + rs.getInt("RoomNo") + ", Booking No: " + rs.getInt("BookingNo"));
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
		return existingBookings;
	}

	public ArrayList<String> getBookingsToCheckIn() {
		ArrayList<String> existingBookings = new ArrayList<>();
		conn = dbConnect.connectToDatabase();
		System.out.println(LocalDate.now());
		String sql = "SELECT * FROM booking WHERE CheckIn = false AND CheckInDate = '" + LocalDate.now() + "'";
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				existingBookings.add("Room No: " + rs.getInt("RoomNo") + ", Booking No: " + rs.getInt("BookingNo"));
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
		return existingBookings;
	}

	public ArrayList<String> getBookingsToBill() {
		ArrayList<String> bookingsToBill = new ArrayList<>();
		conn = dbConnect.connectToDatabase();
		String sql = "SELECT * FROM booking WHERE CheckIn = true AND CheckOutTotal = NULL";
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				bookingsToBill.add("Room No: " + rs.getInt("RoomNo") + ", Booking No: " + rs.getInt("BookingNo"));
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
		return bookingsToBill;
	}

	public int getNextNo() {
		conn = dbConnect.connectToDatabase();
		String sql = "SELECT * FROM booking";
		int lastNo = 20000;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				lastNo = rs.getInt("BookingNo");
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

	public String getBookingDetails(int bookingNo) {
		String details = "";
		conn = dbConnect.connectToDatabase();
		String sql = "SELECT * FROM booking WHERE BookingNo=" + bookingNo;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				details += "Booking: " + rs.getString("BookingNo") + ", Room Number: " 
				+ rs.getInt("RoomNo");
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
