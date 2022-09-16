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

	//Adds a booking to the database - all info is filled in or selected through controlled fields
	//so no need for further validation
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

	//Checks in a booking by setting value to true. 
	public void checkIn(String no) {
		conn = dbConnect.connectToDatabase();
		String sql = "UPDATE booking SET CheckIn = true WHERE BookingNo = " + Integer.parseInt(no);
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			System.out.println("Checked in the booking!!!");
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

	//Gets a list of rooms available to book by first getting a list of unavailable room numbers 
	//on the specified dates, then gets all rooms available of the specified type and removes any
	//room numbers included in the unavailable list.
	public ArrayList<String> getBookingAvailability(LocalDate checkDate, LocalDate checkoutDate, String type) {	
		conn = dbConnect.connectToDatabase();
		ArrayList<Integer> unavailableNums = new ArrayList<>();
		ArrayList<String> availability = new ArrayList<>();

		String sqlUnavailable = "SELECT * from booking WHERE (CheckInDate BETWEEN '" + checkDate.toString() 
								+ "' AND '" + checkoutDate.toString() + "') OR (CheckOutDate BETWEEN '" 
								+ checkDate.toString() + "' AND '" + checkoutDate.toString() + "')";
		
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sqlUnavailable);
			while(rs.next()) {
				unavailableNums.add(rs.getInt("RoomNo"));
			}
			String sqlAvailable = "SELECT * FROM room WHERE RoomType= '" + type + "' AND Decommissioned = false";
			rs = stmt.executeQuery(sqlAvailable);
			while (rs.next()) {
				availability.add("Room: " + rs.getInt("RoomNo") + " Type: " + rs.getString("RoomType"));
			}

			if (unavailableNums.size() > 0) {
				for (int no : unavailableNums) {
					int counter = 0;
					while (counter < availability.size()) {
						if (availability.get(counter).contains(" " + no + " ")) {
							availability.remove(counter);
						} else {
							counter++;
						}
					}
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
		return availability;
	}
	
	//Gets a list of all existing bookings
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

	//Gets a list of available booking that have not yet been checked in for today's date
	public ArrayList<String> getBookingsToCheckIn() {
		ArrayList<String> existingBookings = new ArrayList<>();
		conn = dbConnect.connectToDatabase();
		String sql = "SELECT * FROM booking WHERE CheckIn = false AND CheckInDate = '" + LocalDate.now() + "'";
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				existingBookings.add("Room No: " + rs.getInt("RoomNo") + " , Booking No: " + rs.getInt("BookingNo"));
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

	//Gets a list of bookings that are checked in that have not yet been charged for check out
	public ArrayList<String> getBookingsToBill() {
		ArrayList<String> bookingsToBill = new ArrayList<>();
		conn = dbConnect.connectToDatabase();

		String sql = "SELECT * FROM booking WHERE CheckIn = true AND CheckOutTotal = 0";
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

	//gets next number available to set as unique primary key in database
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

	//Gets booking information for a specific booking
	public String getBookingDetails(int bookingNo) {
		String details = "";
		conn = dbConnect.connectToDatabase();
		String sql = "SELECT * FROM booking WHERE BookingNo=" + bookingNo;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				details += "Room: " + rs.getString("RoomNo") + ", Booking: " 
				+ rs.getInt("BookingNo");
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

	//Gets booking information to display on bill
	public String[] getBookingToBill(int bookingNo) {
		String[] details = new String[4];
		conn = dbConnect.connectToDatabase();
		String sql = "SELECT * FROM booking WHERE BookingNo=" + bookingNo;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				details[0] = rs.getString("CheckInDate");
				details[1] = rs.getString("CheckOutDate");
				details[2] = rs.getString("CustomerNo");
				details[3] = rs.getString("RoomNo");
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

	//Sets the CheckOutTotal of the bill amount in the database meaning the booking is now checked out
	public void chargeBill(int no, double cost) {
		conn = dbConnect.connectToDatabase();
		String sql = "UPDATE booking SET CheckOutTotal = " + cost + ", CheckOutDate = '" 
					+ LocalDate.now() + "' WHERE BookingNo = " + no;
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			System.out.println("Check Out Complete!!");
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
}
