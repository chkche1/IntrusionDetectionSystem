/**This class does the parsing of the rule files
  *It creates an object with all the attributes of the
  *specific rule file given
  **/
import java.util.*;
import java.io.*;

public class RuleParser {

	public String fileName; //Rule file to be parsed
	public String host; //The host in the rule file
	public ArrayList<Rule> rules = new ArrayList<Rule>(); //An arraylist of the rules
	
	public RuleParser(String fileName){
		this.fileName = fileName;
		parseFile();
	}

	public void parseFile(){
		try{
				FileInputStream fstream = new FileInputStream(fileName);
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String strLine;
				int count = 0; //Flag that keeps track of the rules. If there are more than one, it resets the Rule attributes

				//The following are temp variables to form Rule objects with
				String name = "";
				String type = "";
				String src_port = "";
				String dst_port = "";
				String ip = "";
				String proto = "";
				boolean receive = false; //Just a default value that will be overwritten by the one in the rules
				String recv_send = "";
				ArrayList<Boolean> subrule_receive = new ArrayList<Boolean>(); //These are specifit for protocol rules since you can have more than one send|recv
				ArrayList<String> receive_send = new ArrayList<String>(); 
				ArrayList<Flag> flags = new ArrayList<Flag>();
				//Loop that reads the file line by line
				while ((strLine = br.readLine()) != null){
					strLine = strLine.trim(); //Gets rid of leading and trailing whitespaces
						if ((strLine.length() >= 4) && strLine.substring(0, 4).equals("host")){
							 host = getRightSide(strLine);
						}
						else if ((strLine.length() >=4) && strLine.substring(0, 4).equals("name")){
							 if (count == 0){ 
						 		name = getRightSide(strLine);
								count++;
							 }
							 else {
								if (type.equals("tcp_stream")){
									rules.add(new Rule(name, type, src_port, dst_port, ip, recv_send, receive, proto));
								}
								else {
									Rule proto_rule = new Rule(name, type, src_port, dst_port, ip, recv_send, receive, proto);
									SubRule r = new SubRule(subrule_receive, receive_send);
									r.setFlags(flags);
									proto_rule.setSubRules(r);
									rules.add(proto_rule); //If protocol stream you need to take into account subrules as well
									
								}
								name = getRightSide(strLine);
								//Reset everything else to get ready for the next rule
								type = "";
								src_port = "";
								dst_port = "";
								ip = "";
								proto = "";
								receive = false; //Just a default value that will be overwritten by the one in the rules
								recv_send = "";
								subrule_receive = new ArrayList<Boolean>(); //These are specifit for protocol rules since you can have more than one send|recv
								receive_send = new ArrayList<String>(); 
								flags = new ArrayList<Flag>();
								
							 }
						}
						else if ((strLine.length() >=4) && strLine.substring(0, 4).equals("type")){
							type = getRightSide(strLine);
						}
						else if ((strLine.length() >=8) && strLine.substring(0, 8).equals("src_port")){
							 src_port = getRightSide(strLine);
						}
						else if ((strLine.length() >=8) && strLine.substring(0, 8).equals("dst_port")){
							 dst_port = getRightSide(strLine);
						}
						else if ((strLine.length() >=2) && strLine.substring(0, 2).equals("ip")){
							 ip = getRightSide(strLine);
						}
						else if ((strLine.length() >=5) && strLine.substring(0, 5).equals("proto")){
							 proto = getRightSide(strLine);
						}

						else if ((strLine.length() >=4) && strLine.substring(0, 4).equals("recv")
											&& type.equals("tcp_stream")){
							 recv_send = getRightSide(strLine);
							 receive = true;
						}
						else if ((strLine.length() >=4) && strLine.substring(0, 4).equals("send")
										  && type.equals("tcp_stream")){
							 recv_send = getRightSide(strLine);
							 receive = false;
						}

						else if ((strLine.length() >=4) && strLine.substring(0, 4).equals("recv")
										&& type.equals("protocol")){
							if (strLine.contains("with")){
								String temp[] = strLine.split("with");
								recv_send = getRightSide(temp[0]);
								String Flags = getRightSide(temp[1]);
								for (int i =0 ; i< Flags.length(); i++){
									char f = Flags.charAt(i);
									flags.add(getFlagRepresentation(f));
									
								}
							}
							else {
								recv_send = getRightSide(strLine);
							}
							subrule_receive.add(true);
							receive_send.add(recv_send);
						}
						
						else if ((strLine.length() >=4) && strLine.substring(0, 4).equals("send")
									&& type.equals("protocol")){
							if (strLine.contains("with")){
								String temp[] = strLine.split("with");
								recv_send = getRightSide(temp[0]);
								String Flags = getRightSide(temp[1]);
								for (int i =0 ; i< Flags.length(); i++){
									char f = Flags.charAt(i);
									flags.add(getFlagRepresentation(f));
							
								}
							}
							else {
								recv_send = getRightSide(strLine);
							}
							subrule_receive.add(false);
							receive_send.add(recv_send);
						}

				}
				
				if (count == 1){
					if (type.equals("tcp_stream")){
						rules.add(new Rule(name, type, src_port, dst_port, ip, recv_send, receive, proto));
					}
					else {
						Rule proto_rule = new Rule(name, type, src_port, dst_port, ip, recv_send, receive, proto);
						SubRule r = new SubRule(subrule_receive, receive_send);
						r.setFlags(flags);
						proto_rule.setSubRules(r);
						rules.add(proto_rule); //If protocol stream you need to take into account subrules as well
						
					}
					count = 0; //Reset count to get ready for next Rule loop
				}

				in.close();

		} catch (Exception e){
					System.err.println("Error: " + e.getMessage());
		}
	}

	//This function returns what's on the right side of the = sign
	public String getRightSide(String eq){
		if (eq.charAt(eq.length() - 1) == '='){
			return "";
		}
		else{
			String temp[] = eq.split("=");
			return (temp[1]).trim();
		}
	}
	
	public Flag getFlagRepresentation(char f){
		if (f == 'S'){
			return Flag.S;
		}
		else if (f == 'A'){
			return Flag.A;
		}
		else if (f == 'F'){
			return Flag.F;
		}
		else if (f == 'R'){
			return Flag.R;
		}
		else if (f == 'P'){
			return Flag.P;
		}
		else if (f == 'U'){
			return Flag.U;
		}
		else{
			return null;
		}
	}
}
