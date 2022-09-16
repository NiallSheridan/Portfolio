package project.lyit.hotel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import javafx.application.*;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.*;

public class HotelApp extends Application {
	
	private Scene scene;
	private BorderPane sceneLayout;
	
	//LEFT PANE
	private VBox leftPane;
	private RadioButton rbRoom, rbCustomer, rbExtra;
	private Button btAdd, btEdit, btDelete;
	private ToggleGroup group;

	//RIGHT PANE
	private VBox rightPane;

	//BOTTOM PANE
	private HBox bottomPane;
	private ComboBox<String> cbAvailable;
	private Button btMakeBooking;
	private ComboBox<String> checkInBox;
	private Button btCheckIn, btGenerateBill;
	private ComboBox<String> cbReadyForCheckOut;
	
	//DIALOG + DATABASE CONNECTOR
	private Dialog<ButtonType> dialog;
	private Room room;
	private Customer customer;
	private Extra extra;
	private Booking booking;

	//ROOM GRID FIELDS
	private ComboBox<Integer> cbRoomNos; 
	private ComboBox<String> cbRoomType; 
	private ComboBox<Boolean> cbDecomm;

	//CUSTOMER GRID FIELDS
	private TextField custNo, first, last, addr, phone, email; 
	private ComboBox<Integer> cbCustomers;

	//EXTRA GRID FIELDS
	private TextField extraNo, cost, total, bookingNo;
	private ComboBox<Integer> cbExtras;
	private ComboBox<String> cbExtraType;
	private ComboBox<Integer> cbQty;
	private ComboBox<String> cbBooking;

	//Booking 
	private DatePicker checkInDate;
    private DatePicker checkOutDate;
	private CheckBox chkCustomer;
	private TextField txtBookingNo, txtCheckInDate, txtCheckOutDate, txtRoomDetails;
	private ComboBox<String> cbCustomerOptions;
	
	@Override
	public void start(Stage primaryStage){
		
		sceneLayout = new BorderPane();
		leftPane = getLeftPane();
		rightPane = getRightPane();
		bottomPane = getBottomPane();
		
		sceneLayout.setPrefSize(700,225);
		BorderPane.setMargin(leftPane, new Insets(15,15,15,15));
		BorderPane.setMargin(rightPane, new Insets(15,15,15,15));
		sceneLayout.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
		Image image = new Image(getClass().getResourceAsStream("hotelIcon.png"));

		sceneLayout.setTop(new Label("Hotel Reservation System"));
		sceneLayout.setLeft(leftPane);
		sceneLayout.setRight(rightPane);
		sceneLayout.setBottom(bottomPane);
		
		scene = new Scene(sceneLayout);
		primaryStage.getIcons().add(image);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Hotel Reservation System");
		primaryStage.show();
	}

	private VBox getRightPane() {
		VBox vbox = new VBox(5);
		vbox.getChildren().addAll(getFirstRow(), getSecondRow());
		return vbox;
	}

	private HBox getBottomPane() {
		HBox hbox = new HBox(5);
		hbox.setPadding(new Insets(10, 20, 10, 10));
		booking = new Booking();

		//left side dealing with check in
		btCheckIn = new Button("Check In");
		btGenerateBill =  new Button("Generate Bill");
		checkInBox = new ComboBox<String>();
		ArrayList<String> checkIns = booking.getBookingsToCheckIn();
		if (checkIns.size() > 0) {
			checkInBox.getItems().addAll(checkIns);
			checkInBox.setValue(checkIns.get(0));
		} else {
			checkInBox.getItems().addAll("No check ins for today");
			checkInBox.setValue("No check ins for today");
		}

		cbReadyForCheckOut = new ComboBox<String>();
		//right side dealing with generate bill
		ArrayList<String> availableToBill = booking.getBookingsToBill();
		if (availableToBill.size() > 0) {
			cbReadyForCheckOut.getItems().addAll(availableToBill);
			cbReadyForCheckOut.setValue(availableToBill.get(0));
		} else {
			//do something here
		}

		hbox.getChildren().addAll(checkInBox, btCheckIn, cbReadyForCheckOut, btGenerateBill);

		return hbox;
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

	private VBox getFirstRow() {
		VBox vbox = new VBox(5);
		HBox h1 = new HBox(5);
		HBox h2 = new HBox(5);
	
		Button btCheck = new Button("Check Availability");
		checkInDate = new DatePicker();
		checkOutDate = new DatePicker();
		cbRoomType = new ComboBox<>();
	 
		room = new Room();
		HashMap<String, Double> roomOptions = room.getRoomOpts();
		checkInDate.setValue(LocalDate.now());
		checkOutDate.setValue(checkInDate.getValue().plusDays(1));
		cbRoomType.getItems().addAll(roomOptions.keySet());
		cbRoomType.setValue("Single");
		
		h1.getChildren().add(new Label("Check In Date:"));
		h1.getChildren().add(checkInDate);
		h2.getChildren().add(new Label("Check Out Date:"));
		h2.getChildren().add(checkOutDate);
		h1.getChildren().add(cbRoomType);
		h2.getChildren().add(btCheck);
		
		btCheck.setOnAction(e -> {
			checkAvailibility();
		});

		checkInDate.setDayCellFactory(picker -> new DateCell() {
	        public void updateItem(LocalDate date, boolean empty) {
	            super.updateItem(date, empty);
	            LocalDate today = LocalDate.now();

	            setDisable(empty || date.compareTo(today) < 0 );
	        }
	    });

		checkOutDate.setDayCellFactory(picker -> new DateCell() {
	        public void updateItem(LocalDate date, boolean empty) {
	            super.updateItem(date, empty);
	            LocalDate today = LocalDate.now();

	            setDisable(empty || date.compareTo(today) < 0 );
	        }
	    });

		vbox.getChildren().addAll(h1, h2);

		return vbox;
	}

	public HBox getSecondRow() {
		HBox hbox = new HBox(5);
		btMakeBooking = new Button("Book a Room");
		cbAvailable = new ComboBox<>();
		chkCustomer = new CheckBox();
		hbox.getChildren().addAll(cbAvailable, new Label("Existing Customer? "), chkCustomer, btMakeBooking);

		btMakeBooking.setOnAction(e -> {
			addABooking();
			cbAvailable.getItems().clear();
			cbAvailable.getItems().add("Check Availability...");
			cbAvailable.setValue("Check Availability...");
			checkInBox.getItems().clear();
			checkInBox.getItems().addAll(booking.getBookingsToCheckIn());
			checkInBox.setValue(booking.getBookingsToCheckIn().get(0));
		});

		return hbox;
	}
	
	public void checkAvailibility( ) {
		cbAvailable.getItems().clear();
		LocalDate checkDate = checkInDate.getValue();
		LocalDate checkoutDate = checkOutDate.getValue();
		String roomType = cbRoomType.getValue();
		
		Booking booking = new Booking();
		ArrayList<String> availableToBook = booking.getBookingAvailability(checkDate, checkoutDate, roomType);
		
		if (availableToBook.size() > 0) {
			cbAvailable.getItems().addAll(availableToBook);
			cbAvailable.setValue(availableToBook.get(0));
		} else {
			cbAvailable.getItems().add("No Rooms Available!");
			cbAvailable.setValue("No Rooms Available!");
		}
	}

	public void addABooking() {
		if (!(chkCustomer.isSelected())) {
			addACustomer();
		} 

		if (!(cbAvailable.getValue().equals("No Rooms Available!"))) {
			dialog = new Dialog<>();
			dialog.setTitle("Making a Booking");
			dialog.setHeaderText("Select customer to make the booking");
			dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

			GridPane grid = getBookingGrid();
			dialog.getDialogPane().setContent(grid);

			Optional<ButtonType> buttonClicked = dialog.showAndWait();
			if (buttonClicked.get() == ButtonType.OK) {
				String[] roomDetails = cbAvailable.getValue().split(" ");
				String[] custDetails = cbCustomerOptions.getValue().split(" ");
				booking.addBooking(Integer.parseInt(txtBookingNo.getText()), txtCheckInDate.getText(), 
									txtCheckOutDate.getText(), Integer.parseInt(custDetails[1]), 
									Integer.parseInt(roomDetails[1]));
			}
		}
	}

	public GridPane getBookingGrid() {
		GridPane bookingGrid = new GridPane();
		bookingGrid.setHgap(10);
		bookingGrid.setVgap(10);
		bookingGrid.setPadding(new Insets(20, 150, 10, 10));
		booking = new Booking();
		customer = new Customer();

		txtBookingNo = new TextField();
		txtBookingNo.setText("" + booking.getNextNo());
		txtBookingNo.setEditable(false);
		
		txtRoomDetails = new TextField();
		txtRoomDetails.setText(cbAvailable.getValue());
		txtRoomDetails.setEditable(false);

		txtCheckInDate = new TextField();
		txtCheckInDate.setText(checkInDate.getValue().toString());
		txtCheckInDate.setEditable(false);

		txtCheckOutDate = new TextField();
		txtCheckOutDate.setText(checkOutDate.getValue().toString());
		txtCheckOutDate.setEditable(false);

		cbCustomerOptions = new ComboBox<>();
		cbCustomerOptions.getItems().addAll(customer.getExistingDetails());
		cbCustomerOptions.setValue(customer.getExistingDetails().get(0));

		bookingGrid.add(new Label("Booking No: "), 0, 0);
		bookingGrid.add(txtBookingNo, 1, 0);
		bookingGrid.add(new Label("Room Details: "), 0, 1);
		bookingGrid.add(txtRoomDetails, 1, 1);
		bookingGrid.add(new Label("Check In Date: "), 0, 2);
		bookingGrid.add(txtCheckInDate, 1, 2);
		bookingGrid.add(new Label("Check Out Date: "), 0, 3);
		bookingGrid.add(txtCheckOutDate, 1, 3);
		bookingGrid.add(new Label("Select a Customer: "), 0, 4);
		bookingGrid.add(cbCustomerOptions, 1, 4);

		return bookingGrid;
	}

	private void handleAdd() {
		dialog = new Dialog<>();
		dialog.setTitle("Add Details");
		dialog.setHeaderText("Enter Details to add to Database");
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
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
		HashMap<String, Double> roomOptions = room.getRoomOpts();
		cbRoomNos = new ComboBox<>();
		cbRoomType = new ComboBox<>();
		cbRoomType.getItems().addAll(roomOptions.keySet());
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
		Optional<ButtonType> buttonClicked = dialog.showAndWait();
		if (buttonClicked.get() == ButtonType.OK) {
			room.addRoom(cbRoomNos.getValue(), cbRoomType.getValue(), cbDecomm.getValue());
		}
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
		
		Optional<ButtonType> buttonClicked = dialog.showAndWait();
		if (buttonClicked.get() == ButtonType.OK) {
			customer.addCustomer(Integer.parseInt(custNo.getText()), first.getText(), last.getText(), 
								addr.getText(), phone.getText(), email.getText());
		}
	}

	private GridPane getExtraGrid() {
		GridPane extraGrid = new GridPane();
		extraGrid.setHgap(10);
		extraGrid.setVgap(10);
		extraGrid.setPadding(new Insets(20, 150, 10, 10));
		extra = new Extra();
		booking = new Booking();
		
		//Using map to store key:value pairs of extra and cost and filling 
		//combobox with the keys from map.
		HashMap<String, Double> extras = extra.getExtraOptions();
		cbExtraType = new ComboBox<>();
		cbExtraType.getItems().addAll(extras.keySet());
		cbQty = new ComboBox<>();
		cbQty.getItems().addAll(1, 2, 3, 4, 5);
		cost = new TextField();
		total = new TextField();

		cbBooking = new ComboBox<>();
		cbBooking.getItems().addAll(booking.getExistingBookings());
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
		extraGrid.add(new Label("Booking: "), 0, 5);
		extraGrid.add(cbBooking, 1, 5);

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

		Optional<ButtonType> buttonClicked = dialog.showAndWait();
		if (buttonClicked.get() == ButtonType.OK) {
			String[] bookingNo = cbBooking.getValue().split(" ");
			extra.addExtra(Integer.parseInt(extraNo.getText()), cbExtraType.getValue(), 
						cbQty.getValue(), Double.parseDouble(cost.getText()), 
						Double.parseDouble(total.getText()), 
						Integer.parseInt(bookingNo[5]));
		}
	}
	
	private void handleEdit() {
		dialog = new Dialog<>();
		dialog.setTitle("Edit Details");
		dialog.setHeaderText("Enter details for the item you want to edit.");
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
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
		Optional<ButtonType> buttonClicked = dialog.showAndWait();
		if (buttonClicked.get() == ButtonType.OK) {
			room.editRoom(cbRoomNos.getValue(), cbRoomType.getValue(), cbDecomm.getValue());
		}
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
		Optional<ButtonType> buttonClicked = dialog.showAndWait();
		if (buttonClicked.get() == ButtonType.OK) {
			customer.editCustomer(cbCustomers.getValue(), first.getText(), last.getText(), 
								addr.getText(), phone.getText(), email.getText());
		}
	}
		
	private void editAnExtra() {
		GridPane grid = getExtraGrid();
		ArrayList<Integer> extras = extra.getExistingExtras();
		cbExtras = new ComboBox<>();
		bookingNo = new TextField();
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

		grid.add(new Label("Booking: "), 0, 5);
		grid.add(bookingNo, 1, 5);
		
		dialog.getDialogPane().setContent(grid);
		Optional<ButtonType> buttonClicked = dialog.showAndWait();
		if (buttonClicked.get() == ButtonType.OK) {
			extra.editExtra(cbExtras.getValue(), cbExtraType.getValue(), cbQty.getValue(), 
						Double.parseDouble(cost.getText()), Double.parseDouble(total.getText()), 
						Integer.parseInt(bookingNo.getText()));
		}
	}

	private void handleDelete() {
		dialog = new Dialog<>();
		dialog.setTitle("Delete an item");
		dialog.setHeaderText("Select item to delete.");
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
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
		Optional<ButtonType> buttonClicked = dialog.showAndWait();
		if (buttonClicked.get() == ButtonType.OK) {
			room.deleteRoom(cbRooms.getValue());
		}
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
		Optional<ButtonType> buttonClicked = dialog.showAndWait();
		if (buttonClicked.get() == ButtonType.OK) {
			customer.deleteCustomer(cbCustomers.getValue());
		}
	}
	
	private void deleteAnExtra() {
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));
		extra = new Extra();
		booking = new Booking();

		ComboBox<Integer> cbExtras = new ComboBox<>();
		TextField txtType = new TextField();
		txtType.setEditable(false);
		TextField txtQty = new TextField();
		txtQty.setEditable(false);
		TextField txtCost = new TextField();
		txtCost.setEditable(false);
		TextField txtTotal = new TextField();
		txtTotal.setEditable(false);
		TextField txtBookingInfo = new TextField();
		txtBookingInfo.setEditable(false);

		ArrayList<Integer> existingExtras = extra.getExistingExtras();
		if (existingExtras.size() == 0) {
			//DO SOMETHING HERE
		} else {
			cbExtras.getItems().addAll(existingExtras);
			cbExtras.setValue(existingExtras.get(0));
			String[] extraDetails = extra.getExtraDetails(cbExtras.getValue());
			txtType.setText(extraDetails[0]);
			txtQty.setText(extraDetails[1]);
			txtCost.setText(extraDetails[2]);
			txtTotal.setText(extraDetails[3]);
			txtBookingInfo.setText(booking.getBookingDetails(Integer.parseInt(extraDetails[4])));
		}
		
		grid.add(new Label("Extra No: "), 0, 0);
		grid.add(cbExtras, 1, 0);
		grid.add(new Label("Type: "), 0, 1);
		grid.add(txtType, 1, 1);
		grid.add(new Label("Qty: "), 0, 2);
		grid.add(txtQty, 1, 2);
		grid.add(new Label("Cost: "), 0, 3);
		grid.add(txtCost, 1, 3);
		grid.add(new Label("Total: "), 0, 4);
		grid.add(txtTotal, 1, 4);
		grid.add(new Label("Booking: "), 0, 5);
		grid.add(txtBookingInfo, 1, 5);

		cbExtras.setOnAction(e -> {
			String[] extraDetails = extra.getExtraDetails(cbExtras.getValue());
			txtType.setText(extraDetails[0]);
			txtQty.setText(extraDetails[1]);
			txtCost.setText(extraDetails[2]);
			txtTotal.setText(extraDetails[3]);
			txtBookingInfo.setText(booking.getBookingDetails(Integer.parseInt(extraDetails[4])));
		});
		
		dialog.getDialogPane().setContent(grid);
		Optional<ButtonType> buttonClicked = dialog.showAndWait();
		if (buttonClicked.get() == ButtonType.OK) {
			extra.deleteExtra(cbExtras.getValue());
		}
	}
	
	public static void main(String[] args) {	
		launch(args);
	}
}
