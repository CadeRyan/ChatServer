
import java.net.*;
import java.io.*;
import java.util.HashMap;

public class Server {
	
	public static HashMap<Integer, Chatroom> chatrooms = new HashMap<Integer, Chatroom>();
	public static HashMap<Integer, Client> allClients = new HashMap<Integer, Client>();
	public static int roomRefGlobal = 1;
	public static int clientJoinIDGlobal = 1;
	public static void main(String[] args) throws IOException {

	        int portNumber = 1234;
	        boolean listening = true;
	        
	        try (ServerSocket serverSocket = new ServerSocket(portNumber)) { 
	            while (listening) {
		            new ServerThread(serverSocket.accept()).start();
		        }
		    } catch (IOException e) {
	            System.err.println("Could not listen on port " + portNumber);
	            System.exit(0);
	        }
	    }
}
