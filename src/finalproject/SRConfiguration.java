package finalproject;
/*---------------------------------------------------------------------------------------
--	SOURCE FILE:		SRConfiguration.java 
--
--	PROGRAM:		SRConfiguration.java
--
--	
--
--	DATE:			November 26th 2016
--
--	REVISIONS:		(Date and Description)
--
--				November 26 2016
				SRConfiguration file that is needed in order to start either Sender or Receiver class
				It help makes the client
--				
--
--	DESIGNERS:		Justin Chau & Paul Cabanez
--
--	PROGRAMMERS:		Justin Chau & Paul Cabanez
--

---------------------------------------------------------------------------------------*/
import java.net.InetAddress;

/**
 * SRConfiguration Class
 *
 */

public class SRConfiguration {
	
	private InetAddress networkAddress; 		//Network IP
	private int networkPort;					//Network Port
	private InetAddress senderAddress;			//Sender IP
	private int senderPort;						//Sender Port
	private InetAddress receiverAddress;		//Receiver IP
	private int receiverPort;					//Receiver Port
	private int maxPacketsToSend;				//How many packets to send
	private int windowSize;						//Window size
	private int maxTimeout;						//timeout in milliseconds
	
	/**
	 * Default constructor for SRconfiguration
	 */
	public SRConfiguration()
	{}
	
	/**
	 * SR Constructor
	 * 
	 * @param networkAddress networkIP
	 * @param networkPort network Port
	 * @param senderAddress Sender IP
	 * @param senderPort Sender Port
	 * @param receiverAddress Receiver IP
	 * @param receiverPort Receiver Port
	 * @param maxPacketsToSend Max Packets to send
	 * @param windowSize Window Size
	 * @param maxTimeout Timeout in miliseconds
	 */
	public SRConfiguration(InetAddress networkAddress, int networkPort, InetAddress senderAddress, int senderPort,
			InetAddress receiverAddress, int receiverPort, int maxPacketsToSend, int windowSize, int maxTimeout) {
		super();
		this.networkAddress = networkAddress;
		this.networkPort = networkPort;
		this.senderAddress = senderAddress;
		this.senderPort = senderPort;
		this.receiverAddress = receiverAddress;
		this.receiverPort = receiverPort;
		this.maxPacketsToSend = maxPacketsToSend;
		this.windowSize = windowSize;
		this.maxTimeout = maxTimeout;
	}


	/**
	 * 
	 * GETTERS AND SETTERS
	 */
	
	public InetAddress getNetworkAddress() {
		return networkAddress;
	}


	public void setNetworkAddress(InetAddress networkAddress) {
		this.networkAddress = networkAddress;
	}

	public int getNetworkPort() {
		return networkPort;
	}

	public void setNetworkPort(int networkPort) {
		this.networkPort = networkPort;
	}

	public InetAddress getSenderAddress() {
		return senderAddress;
	}

	public void setSenderAddress(InetAddress senderAddress) {
		this.senderAddress = senderAddress;
	}

	public int getSenderPort() {
		return senderPort;
	}

	public void setSenderPort(int senderPort) {
		this.senderPort = senderPort;
	}

	public InetAddress getReceiverAddress() {
		return receiverAddress;
	}

	public void setReceiverAddress(InetAddress receiverAddress) {
		this.receiverAddress = receiverAddress;
	}

	public int getReceiverPort() {
		return receiverPort;
	}

	public void setReceiverPort(int receiverPort) {
		this.receiverPort = receiverPort;
	}

	public int getMaxPacketsToSend() {
		return maxPacketsToSend;
	}

	public void setMaxPacketsToSend(int maxPacketsToSend) {
		this.maxPacketsToSend = maxPacketsToSend;
	}

	public int getWindowSize() {
		return windowSize;
	}

	public void setWindowSize(int windowSize) {
		this.windowSize = windowSize;
	}

	public int getMaxTimeout() {
		return maxTimeout;
	}

	public void setMaxTimeout(int maxTimeout) {
		this.maxTimeout = maxTimeout;
	}

	@Override
	public String toString() {
		return "SRConfiguration [networkAddress=" + networkAddress + ", networkPort=" + networkPort + ", senderAddress="
				+ senderAddress + ", senderPort=" + senderPort + ", receiverAddress=" + receiverAddress
				+ ", receiverPort=" + receiverPort + ", maxPacketsToSend=" + maxPacketsToSend + ", windowSize="
				+ windowSize + ", maxTimeout=" + maxTimeout + "]";
	}
	
}
