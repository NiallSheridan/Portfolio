package project.lyit.hotel;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.application.*;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.*;
import javafx.stage.*;

public class HotelApp extends Application {
	
	private Scene scene;
	private BorderPane sceneLayout;
	
	//LEFT PANE
	private VBox leftPane;
	private RadioButton rbRoom, rbCustomer, rbExtra;
	private Button btAdd, btEdit, btDelete;
	private ToggleGroup group;
	
	//DIALOG + DATABASE CONNECTOR
	private Dialog<String> dialog;
	private Room room;
	private Customer customer;
	private Extra extra;

	//ROOM GRID FIELDS
	private ComboBox<Integer> cbRoomNos; 
	private ComboBox<String> cbRoomType; 
	private ComboBox<Boolean> cbDecomm;

	//CUSTOMER GRID FIELDS
	private TextField custNo, first, last, addr, phone, email; 
	private ComboBox<Integer> cbCustomers;

	//EXTRA GRID FIELDS
	TextField extraNo, cost, total, bookingNo;
	ComboBox<Integer> cbExtras;
	ComboBox<String> cbExtraType;
	ComboBox<Integer> cbQty;
	
	@Override
	public void start(Stage primaryStage){
		
		sceneLayout = new BorderPane();
		leftPane = getLeftPane();
		
		sceneLayout.setPrefSize(200,125);
		sceneLayout.setTop(new Label("Hotel Reservation System"));
		sceneLayout.setLeft(leftPane);
		
		scene = new Scene(sceneLayout);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Hotel Reservation System");
		primaryStage.show();
	}
	
	private VBox getLeftPane() {
		
		VBox vbox = new VBox(5);
		HBox hbox = new HBox(5);
		
		rbRoom = new RadioButton("Room");
		rbCustomer = new RadioButton("Customer");
		rbExtra = new RadioButton("Extra");
		
		group = new ToggleGroup();
		rbRoom.setToggleGroup(group);
		rbCustomer.setToggleGroup(group);
		rbExtra.setToggleGroup(group);
		rbRoom.setSelected(true);
		
		btAdd = new Button("Add");
		btEdit = new Button("Edit");
		btDelete = new Button("Delete");

		btAdd.setOnAction(e -> {
	    	handleAdd();
	    });
		
		btEdit.setOnAction(e -> {
			handleEdit();
	    });
		
		btDelete.setOnAction(e -> {
			handleDelete();
	    });
		
		hbox.getChildren().addAll(btAdd, btEdit, btDelete);
		vbox.getChildren().addAll(rbRoom, rbCustomer, rbExtra, hbox);
		
		return vbox;
	}
	
	//****NEED TO SORT THE CANCEL BUTTON IN DIALOG ****/
	private void handleAdd() {
		dialog = new Dialog<>();
		dialog.setTitle("Add Details");
		dialog.setHeaderText("Enter Details to add to Database");
		// Set the button types.
		ButtonType btOk = new ButtonType("Ok", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(btOk, ButtonType.CANCEL);
		
		RadioButton selected = (RadioButton) group.getSelectedToggle();
		String selectedOpt = selected.getText();
		
		switch (selectedOpt) {
		case "Room":
			System.out.println("You choose room!!");
			addARoom();
			break;
		case "Customer":
			System.out.println("You choose customer!!");
			addACustomer();
			break;
		case "Extra":
			System.out.println("You choose extra!!");
			addAnExtra();
			break;
		}
	}

	private GridPane getRoomGrid() {
		GridPane roomGrid = new GridPane();
		roomGrid.setHgap(10);
		roomGrid.setVgap(10);
		roomGrid.setPadding(new Insets(20, 150, 10, 10));
		room = new Room();

		//COMBO BOXES - Gets avalable room using the method from the room class
		cbRoomNos = new ComboBox<>();
		cbRoomType = new ComboBox<>();
		cbRoomType.getItems().addAll(room.getRoomType());
		cbDecomm = new ComboBox<>();
		cbDecomm.getItems().addAll(true, false);

		roomGrid.add(new Label("Room No: "), 0, 0);
		roomGrid.add(cbRoomNos, 1, 0);
		roomGrid.add(new Label("Room Type: "), 0, 1);
		roomGrid.add(cbRoomType, 1, 1);
		roomGrid.add(new Label("Decommissioned: "), 0, 2);
		roomGrid.add(cbDecomm, 1, 2);

		return roomGrid;
	}
	
	private void addARoom() {
		GridPane grid = getRoomGrid();

		ArrayList<Integer> roomNos = room.getAvailableRooms();
		cbRoomNos.getItems().addAll(roomNos);
		cbRoomNos.setValue(roomNos.get(0));
		cbRoomType.setValue("Single");
		cbDecomm.setValue(false);

		dialog.getDialogPane().setContent(grid);
		dialog.showAndWait();
		room.addRoom(cbRoomNos.getValue(), cbRoomType.getValue(), cbDecomm.getValue());
	}

	private GridPane getCustomerGrid() {
		GridPane custGrid = new GridPane();
		custGrid.setHgap(10);
		custGrid.setVgap(10);
		custGrid.setPadding(new Insets(20, 150, 10, 10));
		customer = new Customer();
		
		//TEXT FIELDS
		first = new TextField();
		first.setPromptText("First Name");
		last = new TextField();
		last.setPromptText("Last Name");
		addr = new TextField();
		addr.setPromptText("Address");
		phone = new TextField();
		phone.setPromptText("Phone");
		email = new TextField();
		email.setPromptText("Email");
		
		custGrid.add(new Label("First Name: "), 0, 1);
		custGrid.add(first, 1, 1);
		custGrid.add(new Label("Last Name: "), 0, 2);
		custGrid.add(last, 1, 2);
		custGrid.add(new Label("Address: "), 0, 3);
		custGrid.add(addr, 1, 3);
		custGrid.add(new Label("Phone: "), 0, 4);
		custGrid.add(phone, 1, 4);
		custGrid.add(new Label("Email: "), 0, 5);
		custGrid.add(email, 1, 5);

		return custGrid;
	}
	
	private void addACustomer() {
		GridPane grid = getCustomerGrid();
		//Text field set to uneditable and method used from the customer class to get the
		//next available number.
		custNo = new TextField();
		custNo.setText("" + customer.getNextNo());
		custNo.setEditable(false);

		grid.add(new Label("Customer No: "), 0, 0);
		grid.add(custNo, 1, 0);

		dialog.getDialogPane().setContent(grid);
		dialog.showAndWait();

		customer.addCustomer(Integer.parseInt(custNo.getText()), first.getText(), last.getText(), 
								addr.getText(), phone.getText(), email.getText());
	}

	private GridPane getExtraGrid() {
		GridPane extraGrid = new GridPane();
		extraGrid.setHgap(10);
		extraGrid.setVgap(10);
		extraGrid.setPadding(new Insets(20, 150, 10, 10));
		extra = new Extra();
		
		//Using map to store key:value pairs of extra and cost and filling 
		//combobox with the keys from map.
		HashMap<String, Double> extras = extra.getExtraOptions();
		cbExtraType = new ComboBox<>();
		cbExtraType.getItems().addAll(extras.keySet());
		cbQty = new ComboBox<>();
		cbQty.getItems().addAll(1, 2, 3, 4, 5);
		cost = new TextField();
		total = new TextField();

		//******Need to change booking no to get available bookings******
		bookingNo = new TextField();
		bookingNo.setPromptText("Booking No");
		//setting initial values
		cbExtraType.setValue("Coffee");
		cbQty.setValue(1);
		cost.setText("" + extras.get(cbExtraType.getValue()));
		cost.setEditable(false);
		double totalAmt = cbQty.getValue() * extras.get(cbExtraType.getValue());
		total.setText("" + totalAmt);
		total.setEditable(false);

		cbExtraType.setOnAction(e -> {
			cost.setText("" + extras.get(cbExtraType.getValue()));
			double newTotal = cbQty.getValue() * extras.get(cbExtraType.getValue());
			total.setText("" + newTotal);
		});

		cbQty.setOnAction(e -> {
			double newTotal = cbQty.getValue() * extras.get(cbExtraType.getValue());
			total.setText("" + newTotal);
		});
		
		extraGrid.add(new Label("Type: "), 0, 1);
		extraGrid.add(cbExtraType, 1, 1);
		extraGrid.add(new Label("Qty: "), 0, 2);
		extraGrid.add(cbQty, 1, 2);
		extraGrid.add(new Label("Cost: "), 0, 3);
		extraGrid.add(cost, 1, 3);
		extraGrid.add(new Label("Total: "), 0, 4);
		extraGrid.add(total, 1, 4);
		extraGrid.add(new Label("Booking No: "), 0, 5);
		extraGrid.add(bookingNo, 1, 5);

		return extraGrid;
	}
	
	private void addAnExtra() {
		GridPane grid = getExtraGrid();

		extraNo = new TextField();
		extraNo.setText("" + extra.getNextNo());
		extraNo.setEditable(false);
		grid.add(new Label("Extra No: "), 0, 0);
		grid.add(extraNo, 1, 0);

		dialog.getDialogPane().setContent(grid);
		dialog.showAndWait();

		extra.addExtra(Integer.parseInt(extraNo.getText()), cbExtraType.getValue(), 
						cbQty.getValue(), Double.parseDouble(cost.getText()), 
						Double.parseDouble(total.getText()), 
						Integer.parseInt(bookingNo.getText()));
	}
	
	//****NEED TO FIX CANCEL BUTTON****/
	private void handleEdit() {
		dialog = new Dialog<>();
		dialog.setTitle("Edit Details");
		dialog.setHeaderText("Enter details for the item you want to edit.");
		ButtonType btOk = new ButtonType("Ok", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(btOk, ButtonType.CANCEL);
		
		RadioButton selected = (RadioButton) group.getSelectedToggle();
		String selectedOpt = selected.getText();
		
		switch (selectedOpt) {
		case "Room":
			System.out.println("You choose room!!");
			editARoom();
			break;
		case "Customer":
			System.out.println("You choose customer!!");
			editACustomer();
			break;
		case "Extra":
			System.out.println("You choose extra!!");
			editAnExtra();
			break;
		}
	}
	
	private void editARoom() {
		GridPane grid = getRoomGrid();
		//SETS INITIAL VALUES
		ArrayList<Integer> existingRooms = room.getExistingRooms();
		cbRoomNos.getItems().addAll(existingRooms);
		cbRoomNos.setValue(existingRooms.get(0));
		String[] details = room.getRoomDetails(cbRoomNos.getValue());
		cbRoomType.setValue(details[0]);
		cbDecomm.setValue(Boolean.parseBoolean(details[1]));

		//CHANGES VALUES AS COMBO BOX SELECTION CHANGES
		cbRoomNos.setOnAction(e -> {
			String[] detailsChanged = room.getRoomDetails(cbRoomNos.getValue());
			cbRoomType.setValue(detailsChanged[0]);
			cbDecomm.setValue(Boolean.parseBoolean(detailsChanged[1]));
		});
		
		dialog.getDialogPane().setContent(grid);
		dialog.showAndWait();
		
		room.editRoom(cbRoomNos.getValue(), cbRoomType.getValue(), cbDecomm.getValue());
	}
	
	private void editACustomer() {
		GridPane grid = getCustomerGrid();
		ArrayList<Integer> customersNos = customer.getExistingCustomers();
		cbCustomers = new ComboBox<>();
		cbCustomers.getItems().addAll(customersNos);
		cbCustomers.setValue(customersNos.get(0));

		//SETS INITIAL VALUES
		String[] details = customer.getCustomerDetails(cbCustomers.getValue());
		first.setText(details[0]);
		last.setText(details[1]);
		addr.setText(details[2]);
		phone.setText(details[3]);
		email.setText(details[4]);

		//CHANGES VALUES WHEN COMBO BOX SELECTION CHANGES
		cbCustomers.setOnAction(e -> {
			String[] detailsChanged = customer.getCustomerDetails(cbCustomers.getValue());
			first.setText(detailsChanged[0]);
			last.setText(detailsChanged[1]);
			addr.setText(detailsChanged[2]);
			phone.setText(detailsChanged[3]);
			email.setText(detailsChanged[4]);
		});
		
		//FILLS THE GRID
		grid.add(new Label("Customer No: "), 0, 0);
		grid.add(cbCustomers, 1, 0);
		
		dialog.getDialogPane().setContent(grid);
		dialog.showAndWait();
		
		customer.editCustomer(cbCustomers.getValue(), first.getText(), last.getText(), 
								addr.getText(), phone.getText(), email.getText());
	}
		
	private void editAnExtra() {
		GridPane grid = getExtraGrid();
		ArrayList<Integer> extras = extra.getExistingExtras();
		cbExtras = new ComboBox<>();
		cbExtras.getItems().addAll(extras);
		cbExtras.setValue(extras.get(0));

		grid.add(new Label("Extra No: "), 0, 0);
		grid.add(cbExtras, 1, 0);

		//SETS INITIAL VALUES BASED ON INITIAL COMBOBOX SELECTION
		String[] details = extra.getExtraDetails(cbExtras.getValue());
		cbExtraType.setValue(details[0]);
		cbQty.setValue(Integer.parseInt(details[1]));
		cost.setText(details[2]);
		cost.setEditable(false);
		total.setText(details[3]);
		total.setEditable(false);
		bookingNo.setText(details[4]);
		bookingNo.setEditable(false);

		//CHANGES VALUES WHEN COMBOBOX SELECTION CHANGES
		cbExtras.setOnAction(e -> {
			String[] detailsChanged = extra.getExtraDetails(cbExtras.getValue());
			cbExtraType.setValue(detailsChanged[0]);
			cbQty.setValue(Integer.parseInt(detailsChanged[1]));
			cost.setText(detailsChanged[2]);
			total.setText(detailsChanged[3]);
			bookingNo.setText(detailsChanged[4]);
		});

		//WHEN TYPE SELECTION CHANGES SO DOES COST AND TOTAL
		HashMap<String, Double> extrasMap = extra.getExtraOptions();
		cbExtraType.setOnAction(e -> {
			cost.setText("" + extrasMap.get(cbExtraType.getValue()));
			double newTotal = cbQty.getValue() * extrasMap.get(cbExtraType.getValue());
			total.setText("" + newTotal);
		});

		//WHEN QTY CHANGES SO DOES TOTAL
		cbQty.setOnAction(e -> {
			double newTotal = cbQty.getValue() * Double.parseDouble(cost.getText());
			total.setText("" + newTotal);
		});
		
		dialog.getDialogPane().setContent(grid);
		dialog.showAndWait();
		
		extra.editExtra(cbExtras.getValue(), cbExtraType.getValue(), cbQty.getValue(), 
						Double.parseDouble(cost.getText()), Double.parseDouble(total.getText()), Integer.parseInt(bookingNo.getText()));
	}

	private void handleDelete() {
		dialog = new Dialog<>();
		dialog.setTitle("Delete an item");
		dialog.setHeaderText("Select id to delete.");
		ButtonType btOk = new ButtonType("Ok", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(btOk, ButtonType.CANCEL);
		
		RadioButton selected = (RadioButton) group.getSelectedToggle();
		String selectedOpt = selected.getText();
		
		switch (selectedOpt) {
		case "Room":
			System.out.println("You choose room!!");
			deleteARoom();
			break;
		case "Customer":
			System.out.println("You choose customer!!");
			deleteACustomer();
			break;
		case "Extra":
			System.out.println("You choose extra!!");
			deleteAnExtra();
			break;
		}
	}
	
	private void deleteARoom() {
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));
		room = new Room();

		//COMBOBOX WITH EXISTING ROOM NUMBERS
		ArrayList<Integer> existingRooms = room.getExistingRooms();
		ComboBox<Integer> cbRooms = new ComboBox<>();
		cbRooms.getItems().addAll(existingRooms);

		//UNEDITABLE TEXTFIELDS TO SHOW DETAILS YOU ARE ABOUT TO DELETE
		TextField roomType = new TextField();
		TextField roomDecommed = new TextField();

		cbRooms.setValue(existingRooms.get(0));
		String[] details = room.getRoomDetails(cbRooms.getValue());
		roomType.setText(details[0]);
		roomType.setEditable(false);
		roomDecommed.setText("" + Boolean.parseBoolean(details[1]));
		roomDecommed.setEditable(false);

		cbRooms.setOnAction(e -> {
			String[] detailsChanged = room.getRoomDetails(cbRooms.getValue());
			roomType.setText(detailsChanged[0]);
			roomType.setEditable(false);
			roomDecommed.setText("" + Boolean.parseBoolean(detailsChanged[1]));
			roomDecommed.setEditable(false);
		});

		grid.add(new Label("Room No: "), 0, 0);
		grid.add(cbRooms, 1, 0);
		grid.add(new Label("Room Type: "), 0, 1);
		grid.add(roomType, 1, 1);
		grid.add(new Label("Decommissioned: "), 0, 2);
		grid.add(roomDecommed, 1, 2);
		
		dialog.getDialogPane().setContent(grid);
		dialog.showAndWait();
		
		room.deleteRoom(cbRooms.getValue());
	}
	
	private void deleteACustomer() {
		GridPane grid = getCustomerGrid();

		ArrayList<Integer> existingCustomers = customer.getExistingCustomers();
		ComboBox<Integer> cbCustomers = new ComboBox<>();
		cbCustomers.getItems().addAll(existingCustomers);
		cbCustomers.setValue(existingCustomers.get(0));

		//SETS INITIAL VALUES
		String[] details = customer.getCustomerDetails(cbCustomers.getValue());
		first.setText(details[0]);
		first.setEditable(false);
		last.setText(details[1]);
		last.setEditable(false);
		addr.setText(details[2]);
		addr.setEditable(false);
		phone.setText(details[3]);
		phone.setEditable(false);
		email.setText(details[4]);
		email.setEditable(false);

		//CHANGES VALUES WHEN COMBO BOX SELECTION CHANGES
		cbCustomers.setOnAction(e -> {
			String[] detailsChanged = customer.getCustomerDetails(cbCustomers.getValue());
			first.setText(detailsChanged[0]);
			last.setText(detailsChanged[1]);
			addr.setText(detailsChanged[2]);
			phone.setText(detailsChanged[3]);
			email.setText(detailsChanged[4]);
		});
		
		//FILLS THE GRID
		grid.add(new Label("Customer No: "), 0, 0);
		grid.add(cbCustomers, 1, 0);
		
		dialog.getDialogPane().setContent(grid);
		dialog.showAndWait();
		
		customer.deleteCustomer(cbCustomers.getValue());
	}
	
	//*NEED TO UPDATE THIS TO DISPLAY NON EDITABLE VALUES BEFORE DELETE */
	private void deleteAnExtra() {
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));
		extra = new Extra();

		ArrayList<Integer> existingExtras = extra.getExistingExtras();
		ComboBox<Integer> cbExtras = new ComboBox<>();
		cbExtras.getItems().addAll(existingExtras);
		
		grid.add(new Label("Extra No: "), 0, 0);
		grid.add(cbExtras, 1, 0);

		cbExtras.setValue(existingExtras.get(0));
		
		dialog.getDialogPane().setContent(grid);
		dialog.showAndWait();
		
		extra.deleteExtra(cbExtras.getValue());
	}
	
	public static void main(String[] args) {	
		launch(args);
	}
}
