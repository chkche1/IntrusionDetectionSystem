import net.sourceforge.jpcap.capture.PacketListener;
import net.sourceforge.jpcap.net.EthernetPacket;
import net.sourceforge.jpcap.net.Packet;
import net.sourceforge.jpcap.net.TCPPacket;
import net.sourceforge.jpcap.net.UDPPacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;

public class IDSListener implements PacketListener{
	RuleParser rp;
	HashMap<Rule, List<Pattern>> ruleMap;
	HashMap<String, StringBuilder> tcpStreams; // <src_port, packet_content>
	
	public IDSListener(String fileName){
		rp = new RuleParser(fileName);
		
		// Compile all regex into pattern and associate all patterns with the rule
		for(Rule rule : rp.rules){
			ArrayList<Pattern> patterns = new ArrayList<Pattern>();
			
			for(String pat : rule.subRules.recv_send){
				try{
					Pattern p = Pattern.compile(pat);
					patterns.add(p);
				}catch(PatternSyntaxException e){
					System.out.println("Invalid regex syntax!");
				}
				
			}// end of subrules loop
			
			ruleMap.put(rule, patterns);	
			
		} // end of rp.rules loop
		
	} // end of IDSListener(String fileName)


	@Override
	public void packetArrived(Packet packet) {
		System.out.println(packet.getData());
		
		// Check if the packet is an EthernetPacket
		if(packet instanceof EthernetPacket){
			//System.out.println("ethernetpacket");
			if(packet instanceof TCPPacket){
				handleTCPPackets(packet);
			}else if(packet instanceof UDPPacket){
				handleUDPPackets(packet);
			}else return;
			
		}
		
		return;
	} // end of packetArrived

	private void handleTCPPackets(Packet p){
		/* Process TCP packets */
		TCPPacket tcp = (TCPPacket) p;
		
		for(Rule r: rp.rules){
			// Match rules with the packet info
			if(!r.src_port.equals(tcp.getSourcePort())) continue;
			
			if(!r.dst_port.equals(tcp.getDestinationPort())) continue;
			
			// Add packet to tcpStreams map
			if(r.type.equals("tcp_stream") && !tcpStreams.containsKey(tcp.getSourcePort())){
				StringBuilder sb = new StringBuilder(new String(tcp.getData()));
				tcpStreams.put(""+tcp.getSourcePort(), sb);
			}
						
			TCPRegexMatch(tcp, r);
			
		} // end of for rp.rules loop
		
	}
	
	private void TCPRegexMatch(TCPPacket p, Rule r){
		List<Pattern> patterns = ruleMap.get(r);
		List<String> tomatch = r.subRules.recv_send;
		List<Boolean> recv = r.subRules.receive;
		boolean isStream = false;
		
		if(r.type.equals("tcp_stream")){
			StringBuilder sb = tcpStreams.get(p.getSourcePort());
			sb.append(new String(p.getData()));
			
			if(p.isFin()){
				tomatch = new ArrayList<String>();
				tomatch.add(sb.toString());
				recv = new ArrayList<Boolean>(); // let it be an empty list since tcp_streamsd don't have subrules
				isStream = true;
			}else{
				// Don't have to match rules yet.
				return;
			}
			
		}
		
		if(patterns.size()==1){
			
			// Just apply the match to  all strings
			for(String s: tomatch){
				Matcher m = patterns.get(0).matcher(s);
				List<Flag> flags = r.subRules.flags;
				if(m.find() && MatchFlags(p, flags)){
					printTCPWarning(p, r, isStream);
				}
			} // end of tomatch loop
			
		}else if(patterns.size()>1){
			
			// Apply the match in order. My interpretation is we need to match to see if any continuous subsequence matches in order.
			for(int i=0; i<tomatch.size(); i++){
				
				
				
			}
			
		}
			
			
			
	} // end of TCPRegexMatch
	
	private boolean MatchFlags(TCPPacket p, List<Flag> flags){
		for(Flag f : flags){
			switch(f){
			case S:
				if(!p.isFin()) return false;
				break;
			case A:
				if(!p.isAck()) return false;
				break;
			case F:
				if(!p.isFin()) return false;
				break;
			case R:
				if(!p.isRst()) return false;
				break;
			case P:
				if(!p.isPsh()) return false;
				break;
			case U:
				if(!p.isUrg()) return false;
				break;
			}
		}
		
		return true;
	}
	
	private void printTCPWarning(TCPPacket p, Rule r, boolean isStream){
		
	}
	
	private void registerTCPStream(String srcPort){
		
	}
	
	private void handleUDPPackets(Packet p){
		/* Process UDP packets */
		
	}
	
}
