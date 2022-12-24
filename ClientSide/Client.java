/*
* EE422C Final Project submission by
* Hamza Shaikh
* hms2659
* 17835
* Spring 2021
* Slip Days Used: 0
*/

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

public class Client extends Application { 
	// I/O streams 
	ObjectOutputStream toServer = null; 
	ObjectInputStream fromServer = null;
	
	Stage primaryStage;
	BorderPane mainPane;
	GridPane center;
	
	ArrayList<Item> history;
	
	ArrayList<Text> priceLedger;
	ArrayList<Button> bidLedger;
	ArrayList<Button> buyLedger;

	String user = "Guest";
	boolean isHistory;
	
	@Override
	public void start(Stage primaryStage) { 
		this.primaryStage = primaryStage;
		this.mainPane = new BorderPane();
		this.center = new GridPane();
		
		findHost();
	}
	
	private void findHost() {
		Text hostText = new Text("Please enter the IPv4 Address of the server you are trying to connect to:");
		Text invalidText = new Text();
		TextField hostInput = new TextField();
		Button hostBtn = new Button("Enter");
		Button localBtn = new Button("Connect to Local Host");
		hostBtn.setPrefWidth(250);
		localBtn.setPrefWidth(250);
		
		GridPane ipBtns = new GridPane();
		ipBtns.addColumn(0, hostBtn);
		ipBtns.addColumn(1, localBtn);
		ipBtns.setHgap(10);
		
		GridPane ipConnect = new GridPane();		
		ipConnect.setPadding(new Insets(50, 50, 50, 50));
		GridPane.setHalignment(hostText, HPos.CENTER);
		ipConnect.setVgap(10);
		
		ipConnect.add(hostText, 0, 0);
		ipConnect.add(hostInput, 0, 1);
		ipConnect.add(ipBtns, 0, 2);
		ipConnect.add(invalidText, 0, 4);
		
		mainPane.setCenter(ipConnect);
		
		// Create a scene and place it in the stage  
		primaryStage.setTitle("IP Connect");		// Set the stage title 
		primaryStage.setScene(new Scene(mainPane));	// Place the scene in the stage
		primaryStage.show();						// Display the stage
		
		hostBtn.setOnAction(e -> { 
			String input = hostInput.getText();
			if (input.equals("")) {
				input = "invalid";
			}
			connect(input, invalidText);
		});
		localBtn.setOnAction(e -> { 
			String input = "localhost";
			connect(input, invalidText);
		});
	}
	
	private void connect(String input, Text invalidText) {
		try { 
			// Create a socket to connect to the server 
			@SuppressWarnings("resource")
			Socket socket = new Socket(input, 8000);
					
			// Create an input stream to receive data from the server
			fromServer = new ObjectInputStream(socket.getInputStream()); 

			// Create an output stream to send data to the server 
			toServer = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException ex) {
			invalidText.setText("Invalid IP Address");
			invalidText.setFill(Color.RED);
			return;
		}
		
		Text title = new Text("Welcome");
		title.setStyle("-fx-font-size: 40");
		
		Text usernameText = new Text("Username");
		Text passwordText = new Text("Password");
		Text failedText = new Text("");
		
		TextField username = new TextField();
		TextField password = new TextField();
		username.setPrefWidth(250);
		password.setPrefWidth(250);
		
		Button start = new Button("Login");
		Button guestStart = new Button("Browse as Guest");
		
		start.setPrefWidth(150);
		guestStart.setPrefWidth(150);
		
		GridPane loginBtns = new GridPane();
		loginBtns.addColumn(0, start);
		loginBtns.addColumn(1, guestStart);
		loginBtns.setHgap(10);
		
		GridPane login = new GridPane();		
		login.setPadding(new Insets(50, 50, 50, 50));
		GridPane.setHalignment(title, HPos.CENTER);
		login.setVgap(10);
		
		login.add(title, 0, 0);
		login.add(usernameText, 0, 1);
		login.add(username, 0, 2);
		login.add(passwordText, 0, 3);
		login.add(password, 0, 4);
		login.add(loginBtns, 0, 5);
		login.add(failedText, 0, 6);
		
		mainPane.setCenter(login);
		
		// Create a scene and place it in the stage  
		primaryStage.setWidth(410);
		primaryStage.setHeight(375);
		primaryStage.setTitle("Client Login"); // Set the stage title 
		primaryStage.show(); // Display the stage
		
		start.setOnAction(e -> { 
			userLogin(username.getText(), password.getText(), failedText);
		});
		
		guestStart.setOnAction(e -> {
			userLogin("Guest", null, failedText);
		});
	}
	
	private void userLogin(String username, String password, Text failedText) {
		try { 
			toServer.writeObject(new Gson().toJson(username, String.class));
			toServer.writeObject(new Gson().toJson(password, String.class));
			boolean flag = new Gson().fromJson((String) fromServer.readObject(), boolean.class);
			if (username.equals("exit")) {
				System.exit(0);
			}
			if (flag) {
				user = username;
				primaryStage.setTitle("Client Auction");
				primaryStage.setWidth(1150);
				primaryStage.setHeight(510);
				Thread readerThread = new Thread(new IncomingReader());
				readerThread.start();
			}
			else {
				failedText.setText("Invalid username or password");
				failedText.setFill(Color.RED);
				return;
			}
		} catch (IOException ex) {
		} catch (JsonSyntaxException e) {
		} catch (ClassNotFoundException e) {}
	}
	
	private void sessionInit(ArrayList<Item> itemList) {
		isHistory = false;
		Text Error = new Text();
		Button exitBtn = new Button("Quit");
		Button historyBtn = new Button("History");
		exitBtn.setStyle("-fx-base: red;");
		exitBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// How to synchronize
				System.exit(0);
			}});
		historyBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (!isHistory) {
					showHistory();
					isHistory = true;
					historyBtn.setText("Auction");
				}
				else {
					mainPane.setCenter(center);
					primaryStage.show();
					isHistory = false;
					historyBtn.setText("History");
				}
			}});
		
		exitBtn.setPrefWidth(100);
		historyBtn.setPrefWidth(100);
		
		GridPane bottomBtns = new GridPane();
		bottomBtns.addColumn(0, exitBtn);
		bottomBtns.addColumn(1, historyBtn);
		bottomBtns.setHgap(5);
		
		GridPane bottom = new GridPane();
		bottom.addColumn(0, Error);
		bottom.addColumn(0, bottomBtns);
		bottom.setVgap(5);
		//bottom.addColumn(0, historyBtn);
		//bottom.addColumn(0, exitBtn);
		
		priceLedger = new ArrayList<Text>();
		bidLedger = new ArrayList<Button>();
		buyLedger = new ArrayList<Button>();
		Text nameTitle = new Text("Name");
		Text descTitle = new Text("Description");
		Text priceTitle = new Text(" Price ");//     ");
		Text inputTitle = new Text("");
		Text bidTitle = new Text("Bid for It!");
		Text buyTitle = new Text("Buy it Now!");
		
		nameTitle.setStyle("-fx-font-weight: bold");
		descTitle.setStyle("-fx-font-weight: bold");
		priceTitle.setStyle("-fx-font-weight: bold");
		inputTitle.setStyle("-fx-font-weight: bold");
		bidTitle.setStyle("-fx-font-weight: bold");
		buyTitle.setStyle("-fx-font-weight: bold");
		
		
		
		center.addColumn(0, nameTitle);
		center.addColumn(1, descTitle);
		center.addColumn(2, priceTitle);
		center.addColumn(3, inputTitle);
		center.addColumn(4, bidTitle);
		center.addColumn(5, buyTitle);
		
		GridPane.setHalignment(nameTitle, HPos.CENTER);
		GridPane.setHalignment(descTitle, HPos.CENTER);
		GridPane.setHalignment(priceTitle, HPos.CENTER);
		GridPane.setHalignment(bidTitle, HPos.CENTER);
		GridPane.setHalignment(buyTitle, HPos.CENTER);
		
		int i = 0;
		for (Item item : itemList) {
			Text name = new Text(" " + item.getName() + " ");// + "  ");
			Text description = new Text(" " + item.getDescription() + " ");// + "  ");
			Text price = new Text("$" + item.getPrice());
			Button bid = new Button("Bid");
			Button buy = new Button("Buy for $" + item.getMaxPrice());
			TextField bidInput = new TextField();
			
			bid.setPrefWidth(100);
			//buy.setPrefHeight(100);
			buy.setPrefWidth(200);
			GridPane.setHalignment(name, HPos.CENTER);
			GridPane.setHalignment(description, HPos.CENTER);
			GridPane.setHalignment(price, HPos.CENTER);
			
			priceLedger.add(price);
			bidLedger.add(bid);
			buyLedger.add(buy);
			bid.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					try {
						synchronized(toServer) {
							if (Double.parseDouble(bidInput.getText()) > Double.parseDouble(item.getPrice())) {
								Error.setText("Bid Placed!");
								Error.setFill(Color.GREEN);
								Item biddedItem = new Item(item);
								biddedItem.setPrice(bidInput.getText());
								biddedItem.setBidder(user);
								toServer.writeObject(new Gson().toJson(biddedItem));
							}
							else {
								Error.setText("Your bid is too low!");
								Error.setFill(Color.RED);
							}
						}
					} catch (Exception e) {
						Error.setText("Please enter a number for your bid");
						Error.setFill(Color.RED);
					}
				}});
			buy.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
						synchronized(toServer) {
							Error.setText("Purchased!");
							Error.setFill(Color.GREEN);
							Item biddedItem = new Item(item);
							biddedItem.setPrice(item.getMaxPrice());
							biddedItem.setBidder(user);
							try {
								toServer.writeObject(new Gson().toJson(biddedItem));
							} catch (IOException e) {}
						}
				}});
			if (item.isSold()) {
				terminateAspect(i, item.getBidder());
			}
			center.addColumn(0, name);
			center.addColumn(1, description);
			center.addColumn(2, price);
			center.addColumn(3, bidInput);
			center.addColumn(4, bid);
			center.addColumn(5, buy);
			i++;
		}
		
		center.setHgap(5);
		center.setVgap(5);
		
		Text title = new Text("E-PURCHASES");		
		title.setStyle("-fx-font-weight: bold");
		
		StackPane top = new StackPane();
		top.setPrefHeight(50);
		top.getChildren().add(title);
		
		StackPane left = new StackPane();
		left.setPrefWidth(50);
		
		StackPane right = new StackPane();
		right.setPrefWidth(50);
		
		center.setStyle("-fx-background-color: white; -fx-border-color: black;");
		
		mainPane.setTop(top);
		mainPane.setBottom(bottom);
		mainPane.setCenter(center);
		
		mainPane.setLeft(left);
		mainPane.setRight(right);
		
		Scene scene2 = new Scene(mainPane);//, 1000, 1000);
		
		primaryStage.setScene(scene2); // Place the scene in the stage
		//primaryStage.hide();
		primaryStage.show(); // Display the stage
	}
	
	private void updateAspect(String biddedPrice, int row) {
		Text price = new Text("$" + biddedPrice);
		Text oldPrice = priceLedger.get(row);
		priceLedger.set(row, price);
		center.getChildren().remove(oldPrice);
		center.add(price, 2, row + 1); // Shifted to accommodate for titles
		GridPane.setHalignment(price, HPos.CENTER);
		mainPane.setCenter(center); 
		primaryStage.show(); // Display the stage
	}
	
	private void terminateAspect(int row, String bidder) {
		Button soldBtn = bidLedger.get(row);
		Button whomBtn = buyLedger.get(row);
		soldBtn.setDisable(true);
		whomBtn.setDisable(true);
		soldBtn.setStyle("-fx-base: red;");
		whomBtn.setStyle("-fx-base: red;");
		soldBtn.setText("SOLD");
		whomBtn.setText("Congrats " + bidder);
		//whomBtn.setText("SOLD");
	}
	
	private void showHistory() {
		GridPane histPane = new GridPane();
		for (Item item : history) {
			//Text pastBid = new Text(item.toString());
			Text pastBid = new Text("Bidder: " + item.getBidder() + ". Bidded Price: $" + item.getPrice() + ". Item: " + item.getName() + ". Description: " + item.getDescription());
			if (item.isSold()) {
				pastBid.setFill(Color.GREEN);
			}
			histPane.addColumn(0, pastBid);
		}
		histPane.setStyle("-fx-background-color: white; -fx-border-color: black;");
		histPane.setVgap(5);
		mainPane.setCenter(histPane);
		primaryStage.show();
	}
		
	public static void main(String[] args) {
		launch(args);
	}
	
	class IncomingReader implements Runnable {
		ArrayList<Item> itemList;
		int priceLoc = 0;
		Item item;
		public IncomingReader() {
			try {
				itemList = new Gson().fromJson((String) fromServer.readObject(), new TypeToken<ArrayList<Item>>(){}.getType());
				history = new Gson().fromJson((String) fromServer.readObject(), new TypeToken<ArrayList<Item>>(){}.getType());
				sessionInit(itemList);
			} catch (Exception e) {}	
		}
		
		public void run() { // Why does this not end after one run?
			System.out.println("Obtained");
        	try {
        		while ((item = new Gson().fromJson((String) fromServer.readObject(), Item.class)) != null) {
        			System.out.println("server read "+ item);
        			history.add(item);
					int i = 0;
        			for (Item listedItem : itemList) {
						if (item.equals(listedItem)) {
							priceLoc = i;
							System.out.println("Found at " + priceLoc);
							listedItem.setPrice(item.getPrice());
							System.out.println("New Price: $" + listedItem.getPrice());
							Platform.runLater(new Runnable() {
					            @Override public void run() {
									updateAspect(listedItem.getPrice(), priceLoc);
					            }
					        });
							if (item.isSold()) {
								listedItem.setSold(true);
								Platform.runLater(new Runnable() {
						            @Override public void run() {
										terminateAspect(priceLoc, item.getBidder());
						            }
						        });
							}
						}
						i++;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}  	
        }
	}
}
