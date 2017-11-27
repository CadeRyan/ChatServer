import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
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
								socket.getInputStream()));) 
		{
			String inputLine, outputLine;
			outputLine = null;
			int state = 0;
			Client thisClient = new Client();
			String message = "";
			boolean notification = false;
			Chatroom currentChatroom = null;
			boolean leave = false;
			int clientJoinIDChat = 0;
			boolean output = true;

			while ((inputLine = in.readLine()) != null) {

				if(inputLine.contains("KILL_SERVICE")){
					state = 12;
					break;
				}

				else if(inputLine.contains("HELO ") && state == 0){
					
					outputLine = inputLine + "\n";
					outputLine += "IP:" + InetAddress.getLocalHost().toString().substring(8) + "\n";
					outputLine += "Port:1234\n";
					outputLine += "StudentID:14310841\n";
				}
				//_________________________________________________________________
				// here lies the logic for joining a chatroom

				else if(inputLine.contains("JOIN_CHATROOM: ") && state == 0){

					String chatroomName = actualData(inputLine);
					String roomRef = "";
					chtrmNumber = chatroomExists(chatroomName);
					if(chtrmNumber > 0)
						roomRef = "" + chtrmNumber;
					else{
						Chatroom new1 = new Chatroom(chatroomName);
						Server.chatrooms.put(new1.roomRef, new1);
						roomRef = "" + new1.roomRef;
						chtrmNumber = new1.roomRef;
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
					int clientJoinID = clntExists;
					if(clntExists > 0)
						joinID = "" + clntExists;
					else{
						thisClient.name = clientName;
						thisClient.socket = socket;
						Server.allClients.put(thisClient.joinID, thisClient);
						clientJoinID = thisClient.joinID;
						joinID = "" + thisClient.joinID;
					}
					Chatroom tmp = Server.chatrooms.get(chtrmNumber);
					thisClient = Server.allClients.get(clientJoinID); 

					if(!(clientIsInChatroomAlready(tmp, thisClient))){
						tmp.addClientToChatroom(thisClient);
					}
					outputLine += "JOIN_ID: " + joinID + "\n";
					notification = true;
					currentChatroom = tmp;
					message = "CHAT: " + currentChatroom.roomRef + "\n"
							+ "CLIENT_NAME: " + thisClient.name + "\n"
							+ "MESSAGE: " + thisClient.name + " has just joined the chatroom\n\n";
					state = 0;
				}

				//___________________________________________________________________
				// here lies the logic for leaving the chatroom

				else if(inputLine.contains("LEAVE_CHATROOM: ")&& state == 0){

					int roomReference = Integer.parseInt(actualData(inputLine));
					currentChatroom = Server.chatrooms.get(roomReference);
					outputLine = "LEFT_CHATROOM: " + roomReference + "\n";
					state = 4;
				}
				else if(inputLine.contains("JOIN_ID: ")&& state == 4){
					int joinID = Integer.parseInt(actualData(inputLine));
					thisClient = Server.allClients.get(joinID);
					outputLine += inputLine + "\n";
					leave = true;
					state = 5;
				}
				else if(inputLine.contains("CLIENT_NAME: ")&& state == 5){
					notification = true;
					message = "CHAT: " + currentChatroom.roomRef + "\n"
							+ "CLIENT_NAME: " + thisClient.name + "\n"
							+ "MESSAGE: " + thisClient.name + " has just left the chatroom\n\n";
					state = 0;
				}

				//___________________________________________________________________
				//here lies the logic for handling chat messages

				else if(inputLine.contains("CHAT: ")&& state == 0){
					state = 6;
					currentChatroom = Server.chatrooms.get(Integer.parseInt(actualData(inputLine)));
					outputLine = inputLine + "\n";
				}
				else if(inputLine.contains("JOIN_ID: ")&& state == 6){
					state = 7;
					clientJoinIDChat = Integer.parseInt(actualData(inputLine));
				}
				else if(inputLine.contains("CLIENT_NAME: ")&& state == 7){
					state = 8;
					outputLine += inputLine + "\n";
				}
				else if(inputLine.contains("MESSAGE: ")&& state == 8){
					state = 0;
					if(clientIsInChatroomAlready(currentChatroom, Server.allClients.get(clientJoinIDChat)))
						notification = true;
					outputLine += inputLine + "\n\n";
					message = outputLine;
					output = false;
				}
				//___________________________________________________________________
				//here lies the logic for the disconnect

				else if(inputLine.contains("DISCONNECT: ")&& state == 0){
					state = 9;
				}
				else if(inputLine.contains("PORT: ")&& state == 9){
					state = 10;
				}
				else if(inputLine.contains("CLIENT_NAME: ")&& state == 10){
					state = 11;
					removeFromAllChatrooms(actualData(inputLine));
					break;
				}


				//___________________________________________________________________

				else outputLine = null;
				//___________________________________________________________________
				if(outputLine != null && state == 0) {
					if(output)
						out.write(outputLine.getBytes());
					output = true;
					if(state == 11){

					}
					if(notification && currentChatroom != null){
						notifyOtherClients(currentChatroom.clients, thisClient, message);
						if(leave){
							removeClientFromChatroom(currentChatroom, thisClient);
						}
						notification = false;
						leave = false;
						currentChatroom = null;
						message = "";
					}
					outputLine = "this should not be showing";
				}
			}
			if(state == 11)
				socket.close();
			else if(state == 12){
				socket.close();
				System.exit(0);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void removeFromAllChatrooms(String name){

		int thisClientID = 0;
		Map<Integer, Client> map = Server.allClients;
		for(Map.Entry<Integer, Client> entry : map.entrySet()){
			if(name.equalsIgnoreCase(entry.getValue().name)){
				thisClientID = entry.getKey();
			}
		}
		Client thisHereClient = Server.allClients.get(thisClientID);
		if(thisClientID != 0){
			Map<Integer, Chatroom> map2 = Server.chatrooms;
			for(Map.Entry<Integer, Chatroom> entry2 : map2.entrySet()){
				for(int i = 0; i < entry2.getValue().clients.size(); i ++){
					if(entry2.getValue().clients.get(i).joinID == thisClientID){
						String message = "CHAT: " + entry2.getValue().roomRef + "\n"
								+ "CLIENT_NAME: " + thisHereClient.name + "\n"
								+ "MESSAGE: " + thisHereClient.name + " has just left the chatroom\n\n";
						notifyOtherClients(entry2.getValue().clients, thisHereClient, message);
						entry2.getValue().clients.remove(i);
					}
				}
			}
		}
		Server.allClients.remove(thisClientID);
	}
	public void removeClientFromChatroom(Chatroom a, Client b){

		for(int i = 0; i < a.clients.size(); i++){
			if(a.clients.get(i).joinID == b.joinID){
				a.clients.remove(i);
			}
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
	public void notifyOtherClients(ArrayList<Client> clientsInChatroom, Client b, String message){

		for(int i = 0; i < clientsInChatroom.size(); i ++){
			try {
				DataOutputStream out = new DataOutputStream(clientsInChatroom.get(i).socket.getOutputStream());
				out.write(message.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public int chatroomExists(String a){
		int res = 0;
		Map<Integer, Chatroom> map = Server.chatrooms;
		for(Map.Entry<Integer, Chatroom> entry : map.entrySet()){
			if(a.equalsIgnoreCase(entry.getValue().name)){
				res = (int) entry.getKey();
			}
		}
		return res;
	}
	public int clientExists(String a){
		int res = 0;
		Map<Integer, Client> map = Server.allClients;
		for(Map.Entry<Integer, Client> entry : map.entrySet()){
			if(a.equalsIgnoreCase(entry.getValue().name))
				res = (int) entry.getKey();
		}
		return res;
	}
	public boolean clientIsInChatroomAlready(Chatroom a, Client b){

		for(int i = 0; i < a.clients.size(); i ++){
			if(a.clients.get(i) != null && a.clients.get(i).joinID == b.joinID){
				return true;
			}
		}
		return false;
	}
}
