/**This class represents a given subrule
**/

import java.util.*;

public class SubRule{

	public ArrayList<Boolean> receive = new ArrayList<Boolean>(); //True if it's recv=<regexp>, false otherwise
	public ArrayList<String> recv_send = new ArrayList<String>();
	public ArrayList<Flag> flags = new ArrayList<Flag>();

	public SubRule(ArrayList<Boolean> receive, ArrayList<String> recv_send){
		this.receive = receive;
		this.recv_send = recv_send;
	}

	public void setFlags(ArrayList<Flag> flags){
		this.flags = flags;
	}
}
