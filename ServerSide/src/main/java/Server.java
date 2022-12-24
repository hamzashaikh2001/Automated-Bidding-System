/*
* EE422C Final Project submission by
* Hamza Shaikh
* hms2659
* 17835
* Spring 2021
* Slip Days Used: 0
*/

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;
import com.google.gson.*;

import org.bson.BasicBSONObject;
import org.bson.Document;
import org.json.JSONObject;

import com.mongodb.*;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

import java.util.*;

public class Server extends Observable {

    static Server server;
    ArrayList<Item> itemList = new ArrayList<Item>();
    ArrayList<Item> history = new ArrayList<Item>();
    Object lock = new Object();
    
    public static void main (String [] args) {
    	ConnectionString cs = new ConnectionString("mongodb+srv://hamzashaikh2001:2VTFKmdrtzU5abB@ee422c-server.4w0vs.mongodb.net/final?retryWrites=true&w=majority");
		MongoClientSettings settings = MongoClientSettings.builder()
    	        .applyConnectionString(cs)
    	        .build();
		MongoClient mongoClient = MongoClients.create(settings);
		MongoDatabase db = mongoClient.getDatabase("final");
		MongoCollection<Document> users = db.getCollection("users");
		//printCollection(users);
		
        server = new Server();
        server.populateItems();
        server.SetupNetworking(users);
    }

    private void populateItems() {
    	try {
			BufferedReader input = new BufferedReader(new FileReader("Input.txt"));
			String line = input.readLine();
			while(line != null) {
				Item item = new Item(line.split("_"));
				itemList.add(item);
				line = input.readLine();
			}
			input.close();
		} catch (IOException e) {
			System.out.println("Input.txt file issue!");
		}
    }
    
    private void SetupNetworking(MongoCollection<Document> users) {
        int port = 8000;//5000;
        try {
            ServerSocket ss = new ServerSocket(port);
            while (true) {
                Socket clientSocket = ss.accept();
                ClientObserver writer = new ClientObserver(clientSocket.getOutputStream());                
                Thread t = new Thread(new ClientHandler(clientSocket, writer, users));
                t.start();   
                addObserver(writer);
                System.out.println("got a connection");
            }
        } catch (IOException e) {}
    }

    class ClientHandler implements Runnable {
        private  ObjectInputStream reader;
        private  ClientObserver writer; // See Canvas. Extends ObjectOutputStream, implements Observer
        Socket clientSocket;
        boolean flag;

        public ClientHandler(Socket clientSocket, ClientObserver writer, MongoCollection<Document> users) {
        	this.clientSocket = clientSocket;
        	this.writer = writer;
        	try {
				reader = new ObjectInputStream(clientSocket.getInputStream());
				flag = false;
				while (!flag) {
					String username = new Gson().fromJson((String) reader.readObject(), String.class);
                	String password = new Gson().fromJson((String) reader.readObject(), String.class);
                	System.out.println(username);
                	System.out.println(password);
                	if (username.equals("exit")) {
                		writer.writeObject(new Gson().toJson(true));
                		System.exit(0);
                	}
                	if (findUser(users, username, password)) {
                		flag = true;
                	}
                	writer.writeObject(new Gson().toJson(flag));
				}
	        	writer.writeObject(new Gson().toJson(itemList));
	        	writer.writeObject(new Gson().toJson(history));
			} catch (IOException e) {}
        	catch (JsonSyntaxException e) {}
        	catch (ClassNotFoundException e) {}
        }

        public void run() { // Why does this not end after one run?
			Item item;
			System.out.println("Check");
        	try {
	        	while ((item = new Gson().fromJson((String) reader.readObject(), Item.class)) != null) {
					System.out.println("server read "+ item);
					synchronized(lock) {
						if ((Double.parseDouble(item.getPrice())) > (Double.parseDouble((itemList.get(itemList.indexOf(item))).getPrice()))) {
							if (Double.parseDouble(item.getPrice()) >= Double.parseDouble(item.getMaxPrice())) {
								item.setSold(true);
							}
							itemList.set(itemList.indexOf(item), item);
							history.add(item);
							setChanged();
							notifyObservers(item);
						}
	        		}
	        	}
			} catch (Exception e) {}
        }
    } // end of class ClientHandler
    
  	private static boolean findUser(MongoCollection<Document> coll, String username, String password) {
  		FindIterable<Document> documents = coll.find();
  		Iterator<Document> it = documents.iterator();
  		if (username.equals("Guest")) {
  			return true;
  		}
  		while(it.hasNext()) {
  			Document doc = it.next();
  			JSONObject docJSON = new JSONObject(doc.toJson());
  			String docUsername = docJSON.getString("username");
			String docPassword = docJSON.getString("password");
  			if (docUsername.equals(username) && docPassword.equals(password)) {
  				return true;
  			}
  		}
  		return false;
  	}
}
