package finalproject;

import java.io.Serializable;

/*---------------------------------------------------------------------------------------
--	SOURCE FILE:		Packet.java 
--
--	PROGRAM:		Packet.java
--
--	
--
--	DATE:			November 26th 2016
--
--	REVISIONS:		(Date and Description)
--
--				November 26 2016
				Packet Class that has all the attribute of a packet
--				
--
--	DESIGNERS:		Justin Chau & Paul Cabanez
--
--	PROGRAMMERS:		Justin Chau & Paul Cabanez
--
--
---------------------------------------------------------------------------------------*/
/**
 * Packet class
 */

public class Packet implements Serializable {
	
	private static final long serialVersionUID = 4530874967285330444L;
	
	private int packetType;// 1 = Start 2 = Data 3 = ACK 4 = EOT 
	private int seqNum; // sequence number
	private int windowSize; //window size
	private int ackNum; //ackNUm
	private String data; // data
	private String destinationAddress; //destination IP
	private String sourceAddress; // source IP
	private int sourcePort; //source port
	private int destinationPort; // destination port
	
	
	/**
	 * Default constructor
	 */
	public Packet()
	{}

	
	/**
	 * Constructor with data
	 * @param packetType
	 * @param seqNum
	 * @param windowSize
	 * @param ackNum
	 * @param data
	 * @param destinationAddress
	 * @param sourceAddress
	 * @param sourcePort
	 * @param destinationPort
	 */
	public Packet(int packetType, int seqNum, int windowSize, int ackNum, String data, String destinationAddress,
			String sourceAddress, int sourcePort, int destinationPort) {
		super();
		this.packetType = packetType;
		this.seqNum = seqNum;
		this.windowSize = windowSize;
		this.ackNum = ackNum;
		this.data = data;
		this.destinationAddress = destinationAddress;
		this.sourceAddress = sourceAddress;
		this.sourcePort = sourcePort;
		this.destinationPort = destinationPort;
	}

	/**
	 * 
	 * Constructor without data
	 * @param packetType
	 * @param seqNum
	 * @param windowSize
	 * @param ackNum
	 * @param destinationAddress
	 * @param sourceAddress
	 * @param sourcePort
	 * @param destinationPort
	 */
	public Packet(int packetType, int seqNum, int windowSize, int ackNum, String destinationAddress,
			String sourceAddress, int sourcePort, int destinationPort) {
		super();
		this.packetType = packetType;
		this.seqNum = seqNum;
		this.windowSize = windowSize;
		this.ackNum = ackNum;
		this.destinationAddress = destinationAddress;
		this.sourceAddress = sourceAddress;
		this.sourcePort = sourcePort;
		this.destinationPort = destinationPort;
	}


	/**========================================
	 * GETTERS AND SETTERS
	 */
	
	/**
	 * @return
	 */
	public int getPacketType(){
		return packetType;
	}

	/**
	 * @return
	 */
	public int getSeqNum() {
		return seqNum;
	}

	/**
	 * @param seqNum
	 */
	public void setSeqNum(int seqNum) {
		this.seqNum = seqNum;
	}

	/**
	 * @return
	 */
	public int getWindowSize() {
		return windowSize;
	}

	/**
	 * @param windowSize
	 */
	public void setWindowSize(int windowSize) {
		this.windowSize = windowSize;
	}

	/**
	 * @return
	 */
	public int getAckNum() {
		return ackNum;
	}

	/**
	 * @param ackNum
	 */
	public void setAckNum(int ackNum) {
		this.ackNum = ackNum;
	}

	/**
	 * @return
	 */
	public String getData() {
		return data;
	}

	/**
	 * @param data
	 */
	public void setData(String data) {
		this.data = data;
	}

	/**
	 * @return
	 */
	public String getDestinationAddress() {
		return destinationAddress;
	}

	/**
	 * @param destinationAddress
	 */
	public void setDestinationAddress(String destinationAddress) {
		this.destinationAddress = destinationAddress;
	}

	/**
	 * @return
	 */
	public String getSourceAddress() {
		return sourceAddress;
	}

	/**
	 * @param sourceAddress
	 */
	public void setSourceAddress(String sourceAddress) {
		this.sourceAddress = sourceAddress;
	}

	/**
	 * @return
	 */
	public int getSourcePort() {
		return sourcePort;
	}

	/**
	 * @param sourcePort
	 */
	public void setSourcePort(int sourcePort) {
		this.sourcePort = sourcePort;
	}

	/**
	 * @return
	 */
	public int getDestinationPort() {
		return destinationPort;
	}

	/**
	 * @param destinationPort
	 */
	public void setDestinationPort(int destinationPort) {
		this.destinationPort = destinationPort;
	}

	/**
	 * @param packetType
	 */
	public void setPacketType(int packetType) {
		this.packetType = packetType;
	}
	
	@Override
	public String toString()
	{
		return "Packet [Source IP : " + sourceAddress + ":" + sourcePort +  ", Destination IP : "
				+ destinationAddress + ":" + destinationPort + ", Sequence Number : " + seqNum + ", Ack Number : "
				+ ackNum + ", Window Size :" + windowSize + ", Data : " + data;
	}
	
}
