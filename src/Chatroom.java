import java.util.ArrayList;
import java.util.HashMap;

public class Chatroom {
	public int roomRef;
	public String name;
	public ArrayList<Client> clients;
	public Chatroom(String a){
		name = a;
		roomRef = assignNumber();
		clients = new ArrayList<Client>();
	}
	
	private int assignNumber(){
		Server.roomRefGlobal ++;
		return Server.roomRefGlobal;
	}
	public void addClientToChatroom(Client a){
		
		clients.add(a);
	}
}
