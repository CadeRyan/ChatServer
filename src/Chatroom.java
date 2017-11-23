public class Chatroom {
	public int roomRef;
	public Chatroom(String a){
		String name = a;
		roomRef = assignNumber();
	}
	
	private int assignNumber(){
		Server.roomRefGlobal ++;
		return Server.roomRefGlobal;
	}
}
