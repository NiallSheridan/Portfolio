package project.lyit.hotel;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import javafx.application.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;

import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.*;

public class HotelApp extends Application {
	
	private Scene scene;
	private BorderPane sceneLayout;

	//formatter for decimal places
	private final DecimalFormat df = new DecimalFormat("0.00");
	
	//LEFT PANE
	private VBox leftPane;
	private RadioButton rbRoom, rbCustomer, rbExtra;
	private Button btAdd, btEdit, btDelete;
	private ToggleGroup group;

	//RIGHT PANE
	private VBox rightPane;
	private ComboBox<String> cbAvailable, cbRoomChoice;
	private Button btMakeBooking;
	private DatePicker checkInDate;
    private DatePicker checkOutDate;
	private CheckBox chkCustomer;

	//BOTTOM PANE
	private VBox bottomPane;
	private ComboBox<String> checkInBox;
	private Button btCheckIn, btGenerateBill;
	private ComboBox<String> cbReadyForCheckOut;
	
	//DIALOG + OBJECTS NEEDED
	private Dialog<ButtonType> dialog;
	private Room room;
	private Customer customer;
	private Extra extra;
	private Booking booking;

	//ROOM
	private ComboBox<Integer> cbRoomNos; 
	private ComboBox<String> cbRoomType;
	private ComboBox<Boolean> cbDecomm;

	//CUSTOMER
	private TextField custNo, first, last, addr, phone, email; 
	private ComboBox<Integer> cbCustomers;

	//EXTRA
	private TextField extraNo, cost, total, bookingNo;
	private ComboBox<Integer> cbExtras;
	private ComboBox<String> cbExtraType;
	private ComboBox<Integer> cbQty;
	private ComboBox<String> cbBooking;

	//BOOKING
	private TextField txtBookingNo, txtCheckInDate, txtCheckOutDate, txtRoomDetails;
	private ComboBox<String> cbCustomerOptions;
	
	private Text topLabel;
	
	
	@Override
	public void start(Stage primaryStage){
	
		StackPane topPane = new StackPane(topLabel = new Text("Hotel Reservation"));
		topLabel.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 20));
		topPane.setStyle("-fx-border-color: black");
	
		extra = new Extra();
		booking = new Booking();
		customer = new Customer();
		room = new Room();

		sceneLayout = new BorderPane();
		leftPane = getLeftPane();
		rightPane = getRightPane();
		bottomPane = getBottomPane();
		
		sceneLayout.setPrefSize(700,225);
		BorderPane.setMargin(leftPane, new Insets(15,15,15,15));
		BorderPane.setMargin(rightPane, new Insets(15,15,15,15));
		BorderPane.setMargin(bottomPane, new Insets(0,0,10,0));
		sceneLayout.setBackground(new Background(new BackgroundFill(Color.CADETBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
		Image image = new Image(getClass().getResourceAsStream("hotelIcon.png"));

		sceneLayout.setTop(topPane);
		
		sceneLayout.setLeft(leftPane);
		sceneLayout.setRight(rightPane);
		sceneLayout.setBottom(bottomPane);
		
		scene = new Scene(sceneLayout);
		primaryStage.getIcons().add(image);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Hotel Reservation System");
		primaryStage.setResizable(false);
		primaryStage.show();
	}

	//gets the right pane
	private VBox getRightPane() {
		VBox vbox = new VBox(5);
		Text rightText = new Text("Check Availibility");
		BorderPane.setAlignment(rightText, Pos.TOP_RIGHT);
		rightText.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
		
		vbox.getChildren().addAll(rightText, getFirstRow(), getSecondRow());
		return vbox;
	}

	//gets the bottom pane
	private VBox getBottomPane() {
		HBox hbox = new HBox(10);
		VBox vbox = new VBox(5);
		
		Text bottomText = new Text("Check-In/Check-Out");
		BorderPane.setAlignment(bottomText, Pos.BOTTOM_CENTER);
		bottomText.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
		
		btCheckIn = new Button("Check In");
		btGenerateBill =  new Button("Generate Bill");
		checkInBox = new ComboBox<String>();
		cbReadyForCheckOut = new ComboBox<String>();

		updateCheckIns();
		updateBookingsToBill();
	
		//handles checkin and error if no booking is selected
		btCheckIn.setOnAction(e -> {
			if (!(checkInBox.getValue().equals("No check ins for today"))) {
				String[] checkInBoxSplit = checkInBox.getValue().split(" ");
				booking.checkIn(checkInBoxSplit[6]);
				updateCheckIns();
				updateBookingsToBill();
			} else {
				Alert errorAlert = new Alert(AlertType.INFORMATION);
				errorAlert.setTitle("No booking selected for check in!");
				errorAlert.setHeaderText("REQUEST NOT COMPLETE");
				errorAlert.setContentText("There is no booking selected to check in!");
				errorAlert.showAndWait();
			}
		});

		//handles generate bill and error if no booking is selected
		btGenerateBill.setOnAction(e -> {
			if (!(cbReadyForCheckOut.getValue().equals("No bookings to bill"))) {
				String[] billToCheckOut = cbReadyForCheckOut.getValue().split(" ");
				generateBill(Integer.parseInt(billToCheckOut[5]));
				updateBookingsToBill();
			} else {
				Alert errorAlert = new Alert(AlertType.INFORMATION);
				errorAlert.setTitle("No booking selected too bill!");
				errorAlert.setHeaderText("REQUEST NOT COMPLETE");
				errorAlert.setContentText("There is no booking selected to bill!");
				errorAlert.showAndWait();
			}
		});
		
		hbox.getChildren().addAll(checkInBox, btCheckIn, cbReadyForCheckOut, btGenerateBill);
		vbox.getChildren().addAll(bottomText,hbox);
		hbox.setAlignment(Pos.BOTTOM_CENTER);
		vbox.setAlignment(Pos.BOTTOM_CENTER);
		
		return vbox;
	}

	//Generates the bill in a dialog box with all the info, once ok is clicked the cost 
	//is added to the database, if cancel is clicked the booking is still checked in
	private void generateBill(int noToBill) {
		//getting any info needed from booking and customer
		String[] bookingInfo = booking.getBookingToBill(noToBill);
		String[] customerInfo = customer.getCustomerDetails(Integer.parseInt(bookingInfo[2]));
		ArrayList<String> extrasAdded = extra.getExtrasForBill(noToBill);

		//declaring cost variables
		double totalExtraCost = 0;
		double overallCost = 0;

		//getting dates of stay and calculates how many nights in total
		LocalDate chkIn = LocalDate.parse(bookingInfo[0]);
		LocalDate chkOut = LocalDate.parse(bookingInfo[1]);
		Period period = Period.between(chkIn, chkOut);
		int nights = Math.abs(period.getDays());

		//room info
		String[] roomInfo = room.getRoomDetails(Integer.parseInt(bookingInfo[3]));
		double roomCost = room.getRoomCost(roomInfo[0]);
		
		//dialog creation begins
		dialog = new Dialog<>();
		dialog.setTitle("Hotel Reservation System Bill Total");
		dialog.setHeaderText("Thank you for your stay :)");
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 30, 20, 30));

		//TEXTFIELDS!
		TextField txtBooking = new TextField();
		txtBooking.setText("" + noToBill);
		txtBooking.setEditable(false);

		TextField txtCustomer = new TextField();
		txtCustomer.setText("" + customerInfo[0] + " " + customerInfo[1]);
		txtCustomer.setEditable(false);
		
		TextField txtCheckIn = new TextField();
		txtCheckIn.setText("" + chkIn);
		txtCheckIn.setEditable(false);
		
		TextField txtCheckOut = new TextField();
		txtCheckOut.setText("" + chkOut);
		txtCheckOut.setEditable(false);

		TextField nightsStayed = new TextField();
		nightsStayed.setText("" + nights);
		nightsStayed.setEditable(false);

		TextField txtType = new TextField();
		txtType.setText("" + roomInfo[0]);
		txtType.setEditable(false);

		TextField txtCostPerNight = new TextField();
		txtCostPerNight.setText("€" + df.format(roomCost));
		txtCostPerNight.setEditable(false);

		TextField txtTotalForRoom = new TextField();
		txtTotalForRoom.setText("€" + df.format(nights * roomCost));
		txtTotalForRoom.setEditable(false);

		//Adding to the grid
		grid.add(new Label("Booking No: "), 0, 0);
		grid.add(txtBooking, 1, 0);
		grid.add(new Label("Customer Name: "), 0, 1);
		grid.add(txtCustomer, 1, 1);
		grid.add(new Label("Check In Date: "), 0, 2);
		grid.add(txtCheckIn, 1, 2);
		grid.add(new Label("Check Out Date: "), 0, 3);
		grid.add(txtCheckOut, 1, 3);
		grid.add(new Label("Nights Stayed: "), 0, 4);
		grid.add(nightsStayed, 1, 4);
		grid.add(new Label(roomInfo[0] + " Room Cost Per Night: "), 0, 5);
		grid.add(txtCostPerNight, 1, 5);
		grid.add(new Label("Total for " + nights + " nights: "), 0, 6);
		grid.add(txtTotalForRoom, 1, 6);
		
		//Checks if there are any extras present for the booking and if so adds them
		//to the grid
		if (extrasAdded.size() > 0) {
			totalExtraCost = extra.getExtraCostForBill(noToBill);
			TextArea taExtras = new TextArea();
			String list = "";
			for (String item : extrasAdded) {
				list += item + "\n";
			}
			taExtras.setText(list);
			taExtras.setEditable(false);
			taExtras.setPrefColumnCount(20);
			taExtras.setPrefRowCount(4);
			grid.add(new Label("Extras: "), 0, 7);
			grid.add(taExtras, 1, 7);
			grid.add(new Label("Extra Total: "), 0, 8);
			grid.add(new TextField("€" + df.format(totalExtraCost)), 1, 8);
		}

		//Adds overall cost to the grid
		overallCost = (nights * roomCost) + totalExtraCost;
		TextField txtOverallTotal = new TextField();
		txtOverallTotal.setText("€" + df.format(overallCost));

		grid.add(new Label("Total Cost of Stay: "), 0, 9);
		grid.add(txtOverallTotal, 1, 9);

		//displays dialog and charges the bill if ok is clicked
		dialog.getDialogPane().setContent(grid);
		Optional<ButtonType> buttonClicked = dialog.showAndWait();
		if (buttonClicked.get() == ButtonType.OK) {
			booking.chargeBill(noToBill, overallCost);
		}
	}

	//Updates the check in combo box in the bottom pane
	private void updateCheckIns() {
		checkInBox.getItems().clear();
		ArrayList<String> newCheckIns = booking.getBookingsToCheckIn();
		if (newCheckIns.size() > 0) {
			checkInBox.getItems().addAll(newCheckIns);
			checkInBox.setValue(newCheckIns.get(0));
		} else {
			checkInBox.getItems().addAll("No check ins for today");
			checkInBox.setValue("No check ins for today");
		}
	}

	//Updates the check outs combo box in the bottom pane
	private void updateBookingsToBill() {
		cbReadyForCheckOut.getItems().clear();
		ArrayList<String> availableToBill = booking.getBookingsToBill();
		if (availableToBill.size() > 0) {
			cbReadyForCheckOut.getItems().addAll(availableToBill);
			cbReadyForCheckOut.setValue(availableToBill.get(0));
		} else {
			cbReadyForCheckOut.getItems().add("No bookings to bill");
			cbReadyForCheckOut.setValue("No bookings to bill");
		}
	}
	
	//gets the left pane
	private VBox getLeftPane() {
		VBox vbox = new VBox(5);
		HBox hbox = new HBox(5);
		
		Text leftText = new Text("Admin");
		BorderPane.setAlignment(leftText, Pos.TOP_LEFT);
		leftText.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
		
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
		vbox.getChildren().addAll(leftText,rbRoom, rbCustomer, rbExtra, hbox);
		
		VBox.setMargin(hbox, new Insets(10,0,0,0));
		
		return vbox;
	}

	//gets the first row of the right pane
	private GridPane getFirstRow() {
		GridPane rowGrid = new GridPane();
		rowGrid.setHgap(10);
		rowGrid.setVgap(10);
	
		Button btCheck = new Button("Check Availability");
		checkInDate = new DatePicker();
		checkOutDate = new DatePicker();
		cbRoomChoice = new ComboBox<>();
	 
		HashMap<String, Double> roomOptions = room.getRoomOpts();
		checkInDate.setValue(LocalDate.now());
		checkOutDate.setValue(checkInDate.getValue().plusDays(1));
		cbRoomChoice.getItems().addAll(roomOptions.keySet());
		cbRoomChoice.setValue("Single");
		
		rowGrid.add(new Label("Check In Date:"), 0, 0);
		rowGrid.add(checkInDate, 1, 0);
		rowGrid.add(cbRoomChoice, 2, 0);
		rowGrid.add(new Label("Check Out Date:"), 0, 1);
		rowGrid.add(checkOutDate, 1, 1);
		rowGrid.add(btCheck, 2, 1);
		
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

		//changes check out date picker to prevent check out before the check in date
		checkInDate.setOnAction(e -> {
			LocalDate chkInDate = checkInDate.getValue();
			checkOutDate.setValue(chkInDate.plusDays(1));

			checkOutDate.setDayCellFactory(picker -> new DateCell() {
				public void updateItem(LocalDate date, boolean empty) {
					super.updateItem(date, empty);
					
					setDisable(empty || date.compareTo(chkInDate.plusDays(1)) < 0 );
					
				}
			});
		});
	
		return rowGrid;
	}

	//gets the second row of the right pane
	public HBox getSecondRow() {
		HBox hbox = new HBox(5);
		btMakeBooking = new Button("Book a Room");
		cbAvailable = new ComboBox<>();
		chkCustomer = new CheckBox();
		Label existCust;
		
		hbox.getChildren().addAll(cbAvailable, existCust = new Label("Existing Customer? "), chkCustomer, btMakeBooking);
		
		HBox.setMargin(cbAvailable, new Insets(10,0,0,0));
		HBox.setMargin(existCust, new Insets(10,0,0,0));
		HBox.setMargin(chkCustomer, new Insets(10,0,0,0));
		HBox.setMargin(btMakeBooking, new Insets(10,0,0,0));
		
		cbAvailable.getItems().add("Check Availability...");
		cbAvailable.setValue("Check Availability...");

		btMakeBooking.setOnAction(e -> {
			addABooking();
			cbAvailable.getItems().clear();
			cbAvailable.getItems().add("Check Availability...");
			cbAvailable.setValue("Check Availability...");
			updateCheckIns();
		});
		
		return hbox;
	}
	
	//Gets the available room to book on a specified date and type
	public void checkAvailibility() {
		cbAvailable.getItems().clear();
		LocalDate checkDate = checkInDate.getValue();
		LocalDate checkoutDate = checkOutDate.getValue();
		String roomType = cbRoomChoice.getValue();
		
		ArrayList<String> availableToBook = booking.getBookingAvailability(checkDate, checkoutDate, roomType);
		if (availableToBook.size() > 0) {
			for (String available : availableToBook) {
				cbAvailable.getItems().add(available + " Cost: €" + room.getRoomCost(roomType));
			}
//			cbAvailable.getItems().addAll(availableToBook);
			cbAvailable.setValue(availableToBook.get(0) + " Cost: €" + room.getRoomCost(roomType));
		} else {
			cbAvailable.getItems().add("No Rooms Available!");
			cbAvailable.setValue("No Rooms Available!");
		}
	}

	//Checks a room is actually selected to book, then checks for an existing customer,
	//if not an existing customer the the add a customer dialog box is displayed otherwise
	//booking dialog is displayed and you can select the customer from a combo box of 
	//available customers
	public void addABooking() {
		if (!(cbAvailable.getValue().equals("No Rooms Available!")) 
			&& !(cbAvailable.getValue().equals("Check Availability..."))) {

			if (!(chkCustomer.isSelected())) {
				dialog = new Dialog<>();
				dialog.setTitle("Add Details");
				dialog.setHeaderText("Add customer details before booking.");
				dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
				addACustomer();
			} 
			
			if (customer.getExistingCustomers().size() > 0) {
				dialog = new Dialog<>();
				dialog.setTitle("Making a Booking");
				dialog.setHeaderText("Select customer to make the booking");
				dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

				GridPane grid = getBookingGrid();
				dialog.getDialogPane().setContent(grid);

				//Booking only added if ok is clicked
				Optional<ButtonType> buttonClicked = dialog.showAndWait();
				if (buttonClicked.get() == ButtonType.OK) {
					String[] roomDetails = cbAvailable.getValue().split(" ");
					String[] custDetails = cbCustomerOptions.getValue().split(" ");
					booking.addBooking(Integer.parseInt(txtBookingNo.getText()), txtCheckInDate.getText(), 
										txtCheckOutDate.getText(), Integer.parseInt(custDetails[1]), 
										Integer.parseInt(roomDetails[1]));
				}
			} else {
				Alert errorAlert = new Alert(AlertType.INFORMATION);
		 		errorAlert.setTitle("No Customers To Book!");
		 		errorAlert.setHeaderText("REQUEST NOT COMPLETE");
		 		errorAlert.setContentText("There is no existing customers to book!");
		 		errorAlert.showAndWait();
			}
		} else {
			Alert errorAlert = new Alert(AlertType.INFORMATION);
	 		errorAlert.setTitle("No Room Selected To Book!");
	 		errorAlert.setHeaderText("REQUEST NOT COMPLETE");
	 		errorAlert.setContentText("There is no room selected to book!");
	 		errorAlert.showAndWait();
		}
	}

	//gets a grid for making a booking which can be added to the dialog box
	public GridPane getBookingGrid() {
		GridPane bookingGrid = new GridPane();
		bookingGrid.setHgap(10);
		bookingGrid.setVgap(10);
		bookingGrid.setPadding(new Insets(20, 30, 20, 30));

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

	//When the add button is clicked in the left pane this function determines which
	//radio is selected and acts accordingly
	private void handleAdd() {
		dialog = new Dialog<>();
		dialog.setTitle("Add Details");
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		RadioButton selected = (RadioButton) group.getSelectedToggle();
		String selectedOpt = selected.getText();
		
		switch (selectedOpt) {
		case "Room":
			dialog.setHeaderText("Enter Room Details to add to Database");
			addARoom();
			break;
		case "Customer":
			dialog.setHeaderText("Enter Customer Details to add to Database");
			addACustomer();
			break;
		case "Extra":
			dialog.setHeaderText("Enter Extra Details to add to Database");
			addAnExtra();
			break;
		}
	}

	//gets a room grid which can be added to the dialog box
	private GridPane getRoomGrid() {
		GridPane roomGrid = new GridPane();
		roomGrid.setHgap(10);
		roomGrid.setVgap(10);
		roomGrid.setPadding(new Insets(20, 30, 20, 30));

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
	
	//gets the values to add the room and calls the addRoom method from the room class
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

	//gets the cusomter grid which can be added to the combobox
	private GridPane getCustomerGrid() {
		GridPane custGrid = new GridPane();
		custGrid.setHgap(10);
		custGrid.setVgap(10);
		custGrid.setPadding(new Insets(20, 30, 20, 30));
		
		//TEXT FIELDS
		first = new TextField();
		first.setPromptText("'Jane'");
		last = new TextField();
		last.setPromptText("'Doe'");
		addr = new TextField();
		addr.setPromptText("'Donegal'");
		phone = new TextField();
		phone.setPromptText("'0896548895'");
		email = new TextField();
		email.setPromptText("'janedoe99@hotmail.com'");
		
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
	
	//gets the values from the customer dialog and calls the addCustomer method
	//from the customer class
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

	//gets a grid for extras which can be added to the dialog box
	private GridPane getExtraGrid() {
		GridPane extraGrid = new GridPane();
		extraGrid.setHgap(10);
		extraGrid.setVgap(10);
		extraGrid.setPadding(new Insets(20, 30, 20, 30));
		
		//Using map to store key:value pairs of extra and cost and filling 
		//combobox with the keys from map.
		HashMap<String, Double> extras = extra.getExtraOptions();
		cbExtraType = new ComboBox<>();
		cbExtraType.getItems().addAll(extras.keySet());
		cbQty = new ComboBox<>();
		cbQty.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		cost = new TextField();
		total = new TextField();

		//setting initial values
		cbExtraType.setValue("Coffee");
		cbQty.setValue(1);
		cost.setText("" + extras.get(cbExtraType.getValue()));
		cost.setEditable(false);
		double totalAmt = cbQty.getValue() * extras.get(cbExtraType.getValue());
		total.setText("" + totalAmt);
		total.setEditable(false);

		//event handles when the type is changed so that all values associated
		//are also changed
		cbExtraType.setOnAction(e -> {
			cost.setText("" + extras.get(cbExtraType.getValue()));
			double newTotal = cbQty.getValue() * extras.get(cbExtraType.getValue());
			total.setText("" + newTotal);
		});

		//event handles when the qty is changed so that all values associated
		//are also changed
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

		return extraGrid;
	}
	
	//Addds any extra fields needed to the grid and gets bookings that are currently checked
	//in to choose from. displays the dialog and passes the values as parameters to the addExtra
	//method in the extra class
	private void addAnExtra() {
		ArrayList<String> bookings = booking.getBookingsToBill();
		if (bookings.size() > 0) {
			GridPane grid = getExtraGrid();
			extraNo = new TextField();
			extraNo.setText("" + extra.getNextNo());
			extraNo.setEditable(false);
			cbBooking = new ComboBox<>();
			cbBooking.getItems().addAll(bookings);
			cbBooking.setValue(bookings.get(0));

			grid.add(new Label("Extra No: "), 0, 0);
			grid.add(extraNo, 1, 0);
			grid.add(new Label("Booking: "), 0, 5);
			grid.add(cbBooking, 1, 5);

			dialog.getDialogPane().setContent(grid);

			Optional<ButtonType> buttonClicked = dialog.showAndWait();
			if (buttonClicked.get() == ButtonType.OK) {
				String[] bookingNo = cbBooking.getValue().split(" ");
				extra.addExtra(Integer.parseInt(extraNo.getText()), cbExtraType.getValue(), 
							cbQty.getValue(), Double.parseDouble(cost.getText()), 
							Double.parseDouble(total.getText()), 
							Integer.parseInt(bookingNo[5]));
			}
		//If there are no bookings in which to add extras to then a warning will display and
		//there will be no dialog
		} else {
			Alert errorAlert = new Alert(AlertType.INFORMATION);
	 		errorAlert.setTitle("No bookings!");
	 		errorAlert.setHeaderText("REQUEST NOT COMPLETE");
	 		errorAlert.setContentText("There are no bookings to add extras to!");
	 		errorAlert.showAndWait();
		}
	}
	
	//Once the edit button is clicked in the left pane, this method is called and 
	//determines which radio button is selected and acts accordingly
	private void handleEdit() {
		dialog = new Dialog<>();
		dialog.setTitle("Edit Details");
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		RadioButton selected = (RadioButton) group.getSelectedToggle();
		String selectedOpt = selected.getText();
		
		switch (selectedOpt) {
		case "Room":
			dialog.setHeaderText("Enter detail changes for the room you wish to edit.");
			editARoom();
			break;
		case "Customer":
			dialog.setHeaderText("Enter detail changes for the customer you wish to edit.");
			editACustomer();
			break;
		case "Extra":
			dialog.setHeaderText("Enter detail changes for the extra you wish to edit.");
			editAnExtra();
			break;
		}
	}
	
	//Brings up the edit a room dialog box and passes the values as parameters
	//once the ok button is clicked
	private void editARoom() {
		ArrayList<Integer> existingRooms = room.getExistingRooms();
		if (existingRooms.size() > 0) {
			GridPane grid = getRoomGrid();
			//SETS INITIAL VALUES
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
		//Dialog will not display if there are no existing rooms to edit and a warning will appear
		} else {
			Alert errorAlert = new Alert(AlertType.INFORMATION);
	 		errorAlert.setTitle("No rooms!");
	 		errorAlert.setHeaderText("REQUEST NOT COMPLETE");
	 		errorAlert.setContentText("There are no rooms to edit at this time!");
	 		errorAlert.showAndWait();
		}
	}
	
	//Brings up the edit a customer dialog box and passes the values as parameters
	//once the ok button is clicked
	private void editACustomer() {
		ArrayList<Integer> customersNos = customer.getExistingCustomers();
		if (customersNos.size() > 0) {
			GridPane grid = getCustomerGrid();
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
		//Dialog will not display if there are no existing rooms to edit and a warning will appear
		} else {
			Alert errorAlert = new Alert(AlertType.INFORMATION);
	 		errorAlert.setTitle("No customers!");
	 		errorAlert.setHeaderText("REQUEST NOT COMPLETE");
	 		errorAlert.setContentText("There are no customers to edit at this time!");
	 		errorAlert.showAndWait();
		}
	}
	
	//Brings up the edit a customer dialog box and passes the values as parameters
	//once the ok button is clicked
	private void editAnExtra() {
		ArrayList<Integer> extras = extra.getActiveExtraList();
		if(extras.size() > 0) {
			GridPane grid = getExtraGrid();
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
			bookingNo.setText(booking.getBookingDetails(Integer.parseInt(details[4])));
			bookingNo.setEditable(false);

			//CHANGES VALUES WHEN COMBOBOX SELECTION CHANGES
			cbExtras.setOnAction(e -> {
				String[] detailsChanged = extra.getExtraDetails(cbExtras.getValue());
				cbExtraType.setValue(detailsChanged[0]);
				cbQty.setValue(Integer.parseInt(detailsChanged[1]));
				cost.setText(detailsChanged[2]);
				total.setText(detailsChanged[3]);
				bookingNo.setText(booking.getBookingDetails(Integer.parseInt(detailsChanged[4])));
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
				String[] booking = bookingNo.getText().split(" ");
				extra.editExtra(cbExtras.getValue(), cbExtraType.getValue(), cbQty.getValue(), 
							Double.parseDouble(cost.getText()), Double.parseDouble(total.getText()), 
							Integer.parseInt(booking[3]));
			}
		//Dialog will not display if there are no existing rooms to edit and a warning will appear
		} else {
			Alert errorAlert = new Alert(AlertType.INFORMATION);
	 		errorAlert.setTitle("No extras!");
	 		errorAlert.setHeaderText("REQUEST NOT COMPLETE");
	 		errorAlert.setContentText("There are no extras to edit at this time!");
	 		errorAlert.showAndWait();
		}
	}

	//When the delete button is clicked in the left pane this method is called and 
	//determines which radio button is selected and acts accordingly
	private void handleDelete() {
		dialog = new Dialog<>();
		dialog.setTitle("Delete an item");
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		RadioButton selected = (RadioButton) group.getSelectedToggle();
		String selectedOpt = selected.getText();
		
		switch (selectedOpt) {
		case "Room":
			dialog.setHeaderText("Select the room you wish to delete.");
			deleteARoom();
			break;
		case "Customer":
			dialog.setHeaderText("Select the customer you wish to delete.");
			deleteACustomer();
			break;
		case "Extra":
			dialog.setHeaderText("Select the extra you wish to delete from booking.");
			deleteAnExtra();
			break;
		}
	}
	
	//Brings up the delete a room dialog box and calls the delete room method from the room 
	//class with the roomNo passed as a parameter
	private void deleteARoom() {
		ArrayList<Integer> existingRooms = room.getExistingRooms();
		if (existingRooms.size() > 0) {
			GridPane grid = new GridPane();
			grid.setHgap(10);
			grid.setVgap(10);
			grid.setPadding(new Insets(20, 30, 20, 30));

			//COMBOBOX WITH EXISTING ROOM NUMBERS
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

			//Changes values based on combo box selection
			cbRooms.setOnAction(e -> {
				String[] detailsChanged = room.getRoomDetails(cbRooms.getValue());
				roomType.setText(detailsChanged[0]);
				roomDecommed.setText("" + Boolean.parseBoolean(detailsChanged[1]));
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
		//Dialog will not display if there are no rooms to delete
		} else {
			Alert errorAlert = new Alert(AlertType.INFORMATION);
	 		errorAlert.setTitle("No rooms!");
	 		errorAlert.setHeaderText("REQUEST NOT COMPLETE");
	 		errorAlert.setContentText("There are no rooms to delete at this time!");
	 		errorAlert.showAndWait();
		}
	}
	
	//Brings up the delete a customer dialog box and calls the delete customer method from the customer
	//class with the customerNo passed as a parameter
	private void deleteACustomer() {
		ArrayList<Integer> existingCustomers = customer.getExistingCustomers();
		if (existingCustomers.size() > 0) {
			GridPane grid = getCustomerGrid();
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
		//Dialog will not display if there are no customers to delete
		} else {
			Alert errorAlert = new Alert(AlertType.INFORMATION);
	 		errorAlert.setTitle("No customers!");
	 		errorAlert.setHeaderText("REQUEST NOT COMPLETE");
	 		errorAlert.setContentText("There are no customers to delete at this time!");
	 		errorAlert.showAndWait();
		}
	}
	
	//Brings up the delete an extra dialog box and calls the delete extra method from the extra
	//class with the extraNo passed as a parameter
	private void deleteAnExtra() {
		ArrayList<Integer> existingExtras = extra.getActiveExtraList();
		if (existingExtras.size() > 0) {
			GridPane grid = new GridPane();
			grid.setHgap(10);
			grid.setVgap(10);
			grid.setPadding(new Insets(20, 40, 20, 30));
			
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
			
			cbExtras.getItems().addAll(existingExtras);
			cbExtras.setValue(existingExtras.get(0));
			String[] extraDetails = extra.getExtraDetails(cbExtras.getValue());
			txtType.setText(extraDetails[0]);
			txtQty.setText(extraDetails[1]);
			txtCost.setText(extraDetails[2]);
			txtTotal.setText(extraDetails[3]);
			txtBookingInfo.setText(booking.getBookingDetails(Integer.parseInt(extraDetails[4])));
			
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
				String[] extraDetailsChange = extra.getExtraDetails(cbExtras.getValue());
				txtType.setText(extraDetailsChange[0]);
				txtQty.setText(extraDetailsChange[1]);
				txtCost.setText(extraDetailsChange[2]);
				txtTotal.setText(extraDetailsChange[3]);
				txtBookingInfo.setText(booking.getBookingDetails(Integer.parseInt(extraDetailsChange[4])));
			});
			
			dialog.getDialogPane().setContent(grid);
			Optional<ButtonType> buttonClicked = dialog.showAndWait();
			if (buttonClicked.get() == ButtonType.OK) {
				extra.deleteExtra(cbExtras.getValue());
			}
		//Dialog will not display if there are no customers to delete
		} else {
			Alert errorAlert = new Alert(AlertType.INFORMATION);
	 		errorAlert.setTitle("No extras!");
	 		errorAlert.setHeaderText("REQUEST NOT COMPLETE");
	 		errorAlert.setContentText("There are no extras to delete at this time!");
	 		errorAlert.showAndWait();
		}
	}
	
	public static void main(String[] args) {	
		launch(args);
	}
}