import java.net.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

public class ServerThread extends Thread{
	private Socket socket = null;
	
	private int chtrmNumber;

	public ServerThread(Socket socket) {
		super("ServerThread");
		this.socket = socket;
	}

	public void run(){

		try (
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				BufferedReader in = new BufferedReader(
						new InputStreamReader(
								socket.getInputStream()));
				) {
			String inputLine, outputLine;
			outputLine = null;
			int state = 0;

			while ((inputLine = in.readLine()) != null) {
				if(inputLine.contains("KILL_SERVICE\n")){
					break;
				}

				if(inputLine.contains("HELO ") && state == 0){
					outputLine = inputLine + "\n";
					outputLine += "IP:134.226.50.57\n";
					outputLine += "Port:1234\n";
					outputLine += "StudentID:14310841\n";
				}
				else if(inputLine.contains("JOIN_CHATROOM: ") && state == 0){

					String chatroomName = actualData(inputLine);
					String roomRef = "";
					chtrmNumber = chatroomExists(chatroomName);
					if(chtrmNumber > 0)
						roomRef = "" + chtrmNumber;
					else{
						Chatroom new1 = new Chatroom(chatroomName);
						Server.chatrooms.put(new1.roomRef, new1);
					}
					outputLine = "JOINED_CHATROOM: " + chatroomName + "\n";
					outputLine += "SERVER_IP: 134.226.50.33\n";
					outputLine += "PORT: 1234\n";
					outputLine += "ROOM_REF: " + roomRef + "\n";
					state = 1;
				}
				else if(inputLine.contains("CLIENT_IP: ") && state == 1)state = 2;
				else if(inputLine.contains("PORT: ") && state == 2)state = 3;
				else if(inputLine.contains("CLIENT_NAME: ") && state == 3){

					String clientName = actualData(inputLine);
					String joinID = "";
					int clntExists = clientExists(clientName);
					if(clntExists > 0){
						joinID = "" + clntExists;
					}
					outputLine += "JOIN_ID: " + joinID + "\n";
					
					state = 0;
				}
				
				if(outputLine != null && state == 0) {
					out.write(outputLine.getBytes());
				}
			}
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public String actualData(String a){

		char[] b = a.toCharArray();
		boolean active = false;
		String res = "";
		for(int i = 0; i < a.length(); i ++){

			if(b[i] == '\\'){
				break;
			}
			if(active && Character.isLetterOrDigit(b[i])){
				res += b[i];
			}
			if(b[i] == ' '){
				active = true;
			}
		}
		return res;
	}
	public int chatroomExists(String a){
		int res = 0;
		Set set = Server.chatrooms.entrySet();
		Iterator iterator = set.iterator();
		while(iterator.hasNext()){
			Map.Entry mentry = (Map.Entry)iterator.next();
			if(a.equalsIgnoreCase((String) mentry.getValue()))
				res = (int) mentry.getKey();
		}
		return res;
	}
	public int clientExists(String a){
		int res = 0;
		Set set = Server.allClients.entrySet();
		Iterator iterator = set.iterator();
		while(iterator.hasNext()){
			Map.Entry mentry = (Map.Entry)iterator.next();
			if(a.equalsIgnoreCase((String) mentry.getValue()))
				res = (int) mentry.getKey();
		}
		return res;
	}
}
