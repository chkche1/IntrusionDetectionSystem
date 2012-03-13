import net.sourceforge.jpcap.capture.CaptureFileOpenException;
import net.sourceforge.jpcap.capture.CapturePacketException;
import net.sourceforge.jpcap.capture.PacketCapture;

public class IDS {

	public IDS(String ruleFile, String pcapFile){
		// Create PacketCapture object and initialize it
		PacketCapture pc = new PacketCapture();
		try {
			pc.openOffline(pcapFile);
		} catch (CaptureFileOpenException e) {
			e.printStackTrace();
		}
		
		// Add listener to handle packets
		pc.addPacketListener(new IDSListener(ruleFile)); // IDSListener takes the rule file as input
		
		try {
			pc.capture(-1); // block forever until an exception is thrown
		} catch (CapturePacketException e) {
			//end of capture file
			System.out.println("Capture throws an exception");
		}
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args){
		if(args.length<2) System.out.println("IDS takes two arguments: java IDS rule_fule pcap_file");
		new IDS(args[0], args[1]);
	}

}
