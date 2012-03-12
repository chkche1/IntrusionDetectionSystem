import net.sourceforge.jpcap.capture.CapturePacketException;
import net.sourceforge.jpcap.capture.PacketListener;
import net.sourceforge.jpcap.net.EthernetPacket;
import net.sourceforge.jpcap.net.IPPacket;
import net.sourceforge.jpcap.net.Packet;
import net.sourceforge.jpcap.net.TCPPacket;
import net.sourceforge.jpcap.net.UDPPacket;


public class IDSListener implements PacketListener{

	@Override
	public void packetArrived(Packet packet) {
		System.out.println(packet.getData());
		
		// Check if the packet is an EthernetPacket
		if(packet instanceof EthernetPacket){
			System.out.println("ethernetpacket");
			if(packet instanceof TCPPacket){
				handleTCPPackets(packet);
			}else if(packet instanceof UDPPacket){
				handleUDPPackets(packet);
			}else return;
			
		}
		
		return;
	} // end of packetArrived

	private void handleTCPPackets(Packet p){
		// Process TCP packets
		System.out.println("I'm tcp");
		
		
	}
	
	private void handleUDPPackets(Packet p){
		// Process UDP packets
		System.out.println("I'm udp");
		
	}
	
}
