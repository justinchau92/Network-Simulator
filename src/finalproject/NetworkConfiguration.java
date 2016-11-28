package finalproject;
/*---------------------------------------------------------------------------------------
--	SOURCE FILE:		NetworkConfiguration.java 
--
--	PROGRAM:		NetworkConfiguration.java
--
--	
--
--	DATE:			November 26th 2016
--
--	REVISIONS:		(Date and Description)
--
--				November 26 2016
				NetworkConfiguration class that configures the network
				Create this as an object in the runner and use this to create the Network
--				
--
--	DESIGNERS:		Justin Chau & Paul Cabanez
--
--	PROGRAMMERS:		Justin Chau & Paul Cabanez
--
--	
---------------------------------------------------------------------------------------*/
import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * NetworkConfiguration file
 *
 */
public class NetworkConfiguration {
	private InetAddress sender; // Sender IP
	private int senderPort; 	// Sender Port
	
	private InetAddress receiver; // Receiver IP
	private int receiverPort;	// Receiver Port
	
	private int bitErrorRate; //bit Error Rate
	private int averageDelayPerPacket; // average delay in packets
	
	private int networkPort; //network Port
	
	/**
	 * DEFAULT CONSTRUCTOR
	 */
	public NetworkConfiguration(){
		
		try
		{
			this.setSender(InetAddress.getByName("127.0.0.1"));
			this.setReceiver(InetAddress.getByName("127.0.0.1"));
		}
		catch(Exception e)
		{
			
		}
		
		this.setSenderPort(7005);
		this.setNetworkPort(7008);
		this.setReceiverPort(7004);
		this.setBitErrorRate(50);
		this.setAverageDelayPerPacket(100);
	}

	
	/**
	 * Network Constructor
	 * @param sender senderIP
	 * @param senderPort sender Port
	 * @param receiver receiver IP
	 * @param receiverPort receiver Port
	 * @param bitErrorRate bit error rate
	 * @param averageDelayPerPacket average delay per packet
	 * @param networkPort network port
	 */
	public NetworkConfiguration(InetAddress sender, int senderPort, InetAddress receiver, int receiverPort,
			int bitErrorRate, int averageDelayPerPacket, int networkPort) {
		super();
		this.sender = sender;
		this.senderPort = senderPort;
		this.receiver = receiver;
		this.receiverPort = receiverPort;
		this.bitErrorRate = bitErrorRate;
		this.averageDelayPerPacket = averageDelayPerPacket;
		this.networkPort = networkPort;
	}

	/**
	 * GETTERS AND SETTERS + TO STRING
	 *
	 */
	public InetAddress getSender() {
		return sender;
	}

	public void setSender(InetAddress sender) {
		this.sender = sender;
	}

	public int getSenderPort() {
		return senderPort;
	}

	public void setSenderPort(int senderPort) {
		this.senderPort = senderPort;
	}

	public InetAddress getReceiver() {
		return receiver;
	}

	public void setReceiver(InetAddress receiver) {
		this.receiver = receiver;
	}

	public int getReceiverPort() {
		return receiverPort;
	}

	public void setReceiverPort(int receiverPort) {
		this.receiverPort = receiverPort;
	}

	public int getBitErrorRate() {
		return bitErrorRate;
	}

	public void setBitErrorRate(int bitErrorRate) {
		this.bitErrorRate = bitErrorRate;
	}

	public int getAverageDelayPerPacket() {
		return averageDelayPerPacket;
	}

	public void setAverageDelayPerPacket(int averageDelayPerPacket) {
		this.averageDelayPerPacket = averageDelayPerPacket;
	}

	public int getNetworkPort() {
		return networkPort;
	}

	public void setNetworkPort(int networkPort) {
		this.networkPort = networkPort;
	}

	@Override
	public String toString() {
		return "NetworkConfiguration [sender=" + sender + ", senderPort=" + senderPort + ", receiver=" + receiver
				+ ", receiverPort=" + receiverPort + ", bitErrorRate=" + bitErrorRate + ", averageDelayPerPacket="
				+ averageDelayPerPacket + ", networkPort=" + networkPort + "]";
	}
	
	
}
