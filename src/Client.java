public class Client {

	public int joinID;
	public String name;
	public Client(String a){
		name = a;
		joinID = assignNumber();
	}
	
	private int assignNumber(){
		Server.clientJoinIDGlobal ++;
		return Server.clientJoinIDGlobal;
	}
}
