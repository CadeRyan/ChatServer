public class Client {

	public Client(String a){
		String name = a;
		int ClientRef = assignNumber();
	}
	
	private int assignNumber(){
		Server.clientJoinIDGlobal ++;
		return Server.clientJoinIDGlobal;
	}
}
