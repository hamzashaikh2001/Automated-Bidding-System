/*
* EE422C Final Project submission by
* Hamza Shaikh
* hms2659
* 17835
* Spring 2021
* Slip Days Used: 0
*/

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Observable;
import java.util.Observer;

import com.google.gson.Gson;

public class ClientObserver extends ObjectOutputStream implements Observer {
	public ClientObserver(OutputStream out) throws IOException {
		super(out);
	}
	@Override
	public void update(Observable o, Object arg) {
		try {
			this.writeObject(new Gson().toJson(arg));
			this.flush();
		} catch (IOException e) {}
	}

}
