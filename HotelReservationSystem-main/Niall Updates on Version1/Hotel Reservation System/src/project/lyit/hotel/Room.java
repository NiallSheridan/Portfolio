package project.lyit.hotel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Room {
	
	private DatabaseConnector dbConnect;
	private Connection conn;
	private Statement stmt;
	private String[] roomType = {"Single", "Double", "Triple", "Family"};
	
	public Room() {
		dbConnect = new DatabaseConnector();
	}

	public String[] getRoomType() {
		return roomType;
	}

	public void addRoom(int roomNo, String roomType, boolean decomissioned ) {
		conn = dbConnect.connectToDatabase();
		String sql = "INSERT INTO room VALUES (" + roomNo + ", '" + roomType  + "', " +  decomissioned +  ")";
 
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			System.out.println("Room ADDED!!!");
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
	
	public void editRoom(int roomNo, String roomType, boolean decomm) {
		conn = dbConnect.connectToDatabase();
		String sql = "UPDATE room SET RoomType='" + roomType + "', Decommissioned= " + decomm
				+ " WHERE RoomNo=" + roomNo;
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			System.out.println("Room UPDATED!!!");
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

	public void deleteRoom(int roomNo) {
		conn = dbConnect.connectToDatabase();
		String sql = "DELETE FROM room WHERE RoomNo=" + roomNo;
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			System.out.println("Room DELETED!!!");
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

	public ArrayList<Integer> getAvailableRooms() {
		ArrayList<Integer> usedRooms = new ArrayList<>();
		ArrayList<Integer> availableRooms = new ArrayList<>();;
		conn = dbConnect.connectToDatabase();
		String sql = "SELECT * FROM room";
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				usedRooms.add(rs.getInt("RoomNo"));
			}
			for (int i = 1; i <= 30; i++) {
				if (!usedRooms.contains(i)) {
					availableRooms.add(i);
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
		return availableRooms;
	}

	public ArrayList<Integer> getExistingRooms() {
		ArrayList<Integer> existingRooms = new ArrayList<>();
		conn = dbConnect.connectToDatabase();
		String sql = "SELECT * FROM room";
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				existingRooms.add(rs.getInt("RoomNo"));
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
		return existingRooms;
	}

	public String[] getRoomDetails(int roomNo) {
		String[] details = new String[2];
		conn = dbConnect.connectToDatabase();
		String sql = "SELECT * FROM room WHERE RoomNo=" + roomNo;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				details[0] = rs.getString("RoomType");
				details[1] = "" + rs.getBoolean("Decommissioned");
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
