import java.net.Socket;

public class Client {

	public int joinID;
	public String name;
	public Socket socket;
	public Client(){
		joinID = assignNumber();
		name = "";
	}
	
	private int assignNumber(){
		Server.clientJoinIDGlobal ++;
		return Server.clientJoinIDGlobal;
	}
}
