package project.lyit.hotel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Room {
	
	private DatabaseConnector dbConnect;
	private Connection conn;
	private Statement stmt;
	private HashMap<String, Double> roomOpts = new HashMap<String, Double>();
	
	public Room() {
		dbConnect = new DatabaseConnector();
		roomOpts.put("Single", 50.0);
		roomOpts.put("Double", 100.0);
		roomOpts.put("Triple", 150.0);
		roomOpts.put("Family", 200.0);
	}

	public HashMap<String, Double> getRoomOpts() {
		return roomOpts;
	}

	//Adds a room to the database - all info is filled in or selected through controlled fields
	//so no need for further validation
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
	
	//Edits a room in the database
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

	//Deletes a room in the database
	public void deleteRoom(int roomNo) {
		conn = dbConnect.connectToDatabase();
		String sql = "DELETE FROM room WHERE RoomNo=" + roomNo;
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			System.out.println("Room DELETED!!!");
		} catch(SQLIntegrityConstraintViolationException e) {
			System.out.println("Cannot delete a room that is booked!");
			Alert errorAlert = new Alert(AlertType.ERROR);
	 		errorAlert.setTitle("Error");
	 		errorAlert.setHeaderText("REQUEST NOT COMPLETE");
	 		errorAlert.setContentText("Cannot delete a room that is booked!");
	 		errorAlert.showAndWait();
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

	//Gets list of available rooms to add to the database between 1 and 30.
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

	//Gets a list of existing rooms in the database
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

	//Gets details for a specific room
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

	//Returns cost of room depending on type
	public double getRoomCost(String type) {
		return roomOpts.get(type);
	}
}
