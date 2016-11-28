package finalproject;
/*---------------------------------------------------------------------------------------
--	SOURCE FILE:		Receiver.java - 
--
--	PROGRAM:		Receiver.java
--
--	
--
--	DATE:			November 26th 2016
--
--	REVISIONS:		(Date and Description)
--
--				November 26 2016
				Receiver Class - Receiver class that sends ACKs back to the Sender
				SRConfiguration as object to create the receiver
--				
--
--	DESIGNERS:		Justin Chau & Paul Cabanez
--
--	PROGRAMMERS:		Justin Chau & Paul Cabanez
--

---------------------------------------------------------------------------------------*/
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;

/**
 *Receiver class
 *
 */
public class Receiver {
	private int seqNum;	//sequence number
	private ArrayList<Packet> ackPackets; //list of ack packets

	private DatagramSocket socket;		//socket connection
	private DatagramPacket datagramPacket; //datagrampacket
	private SRConfiguration config; //configuration object
	private byte[] data; //data
	
	private Packet SOT; //start of transmission
	private Packet packet; //packet
	
	PrintWriter writer;
	
	DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy-HH.mm.ss"); //date format
	
	/**
	 * Constructor needs config file
	 * @param config
	 */
	public Receiver(SRConfiguration config)
	{
		this.seqNum = 1;
		this.config = config;
		ackPackets = new ArrayList<Packet>();
	}
	
	/**
	 * Runnable
	 * @throws UnsupportedEncodingException 
	 * @throws FileNotFoundException 
	 */
	public void run() throws FileNotFoundException, UnsupportedEncodingException
	{
		Date date = new Date();
		String dateLog = dateFormat.format(date);
		writer = new PrintWriter(dateLog + "_Receiver_Log.txt", "UTF-8");
		writer.println(dateLog + " Receiver started");
		//connect socket
		try
		{
			socket = new DatagramSocket(config.getReceiverPort());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		//wait for sender
		waitForSender();
		
		boolean keepReceiving = true;
		
		int totalPackets = 0;
		int totalDupAcks = 0;
		
		while(keepReceiving)
		{
			try
			{
				Packet scanPacket = getPacket();
				
				switch(scanPacket.getPacketType())
				{
					//datapacket
					case (2):
						this.seqNum = scanPacket.getSeqNum();
					
						//create an ack packet
						Packet ackPacket = new Packet(3, this.seqNum, this.config.getWindowSize(), this.seqNum,  
							 this.config.getSenderAddress().getHostAddress(),this.config.getReceiverAddress().getHostAddress(),
								this.config.getReceiverPort(),this.config.getSenderPort()); 
						this.sendPacket(ackPacket);
						
						date = new Date();
						dateLog = dateFormat.format(date);
						
						System.out.println(dateLog + " Sent ACK : " + ackPacket); //print out log
						writer.println(dateLog + " Sent ACK : " + ackPacket);
						
						if(!ACKChecker(scanPacket.getSeqNum()))
						{
							totalPackets++;
						}
						else
						{
							totalDupAcks++;
						}
						
						this.ackPackets.add(packet);
						
						break;
					
					//end of transmission
					case(4):
						this.seqNum = scanPacket.getSeqNum();
						//creates an ack packet
						ackPacket = new Packet(4, this.seqNum, this.config.getWindowSize(), this.seqNum,  
								this.config.getSenderAddress().getHostAddress(),this.config.getReceiverAddress().getHostAddress(),
								this.config.getReceiverPort(),this.config.getSenderPort());
						this.sendPacket(ackPacket);
					
						date = new Date();
						dateLog = dateFormat.format(date);
						
						System.out.println(dateLog + " Sent EOT ACK : " + ackPacket);
						writer.println(dateLog + " Sent EOT ACK : " + ackPacket);
						
						writer.println("Total Received Packets : " + totalPackets);
						writer.println("Total Dup Acks : " + totalDupAcks);
						System.out.println("Total Received Packets : " + totalPackets);
						System.out.println("Total Dup Acks : " + totalDupAcks);
						
						keepReceiving = false;
						
						break;
						
				}
			}
			catch(Exception e)
			{
				
			}
		}
		
		writer.close();
		
	}
	
	/**
	 * Checks if we 
	 * @param seqNum
	 * @return
	 */
	private boolean ACKChecker(int seqNum)
	{
		for(int i = 0; i < this.ackPackets.size();i++)
		{
			if(this.ackPackets.get(i).getSeqNum() == seqNum)
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Wait for Sender and then replies
	 */
	private void waitForSender()
	{
		try
		{
			SOT = getPacket();
			//if it is a SOT packet
			if(SOT.getPacketType() == 1)
			{
				packet = new Packet(1, this.seqNum, this.config.getWindowSize(), this.seqNum,  
						 this.config.getSenderAddress().getHostAddress(),this.config.getReceiverAddress().getHostAddress(),
							this.config.getReceiverPort(),this.config.getSenderPort());
				
				this.sendPacket(packet);
				
				Date date = new Date();
				String dateLog = dateFormat.format(date);
				
				System.out.println(dateLog + " Sent back SOT ACK " + packet);
				writer.println(dateLog + " Sent back SOT ACK " + packet);
			}
			else
			{
				this.waitForSender();
			}
		}
		catch(Exception e)
		{}
		
	}
	
	/**
	 * Gets packet from the socket
	 * @return Packet
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private Packet getPacket() throws IOException, ClassNotFoundException
	{
		try
		{
			data = new byte[1024];
			datagramPacket = new DatagramPacket(data, data.length);
			socket.receive(datagramPacket);
			
			data = datagramPacket.getData();
			
			ByteArrayInputStream baIn = new ByteArrayInputStream(data);
			ObjectInputStream oIn = new ObjectInputStream(baIn);
			
			Packet temp = (Packet) oIn.readObject();
			return temp;
			
		}
		catch (SocketException e)
		{}
		return null;
		
	}
	
	/**
	 * Sends packet to the connection
	 * @param packet
	 * @throws IOException
	 */
	private void sendPacket(Packet packet) throws IOException
	{
		try
		{
			data = new byte[1024];
			
			ByteArrayOutputStream baOut = new ByteArrayOutputStream();
			ObjectOutputStream oOut = new ObjectOutputStream(baOut);
			
			oOut.writeObject(packet);
			oOut.close();
			
			data = baOut.toByteArray();
			
			datagramPacket = new DatagramPacket(data,data.length, config.getNetworkAddress(), config.getNetworkPort());
			
			socket.send(datagramPacket);
			
		}
		catch(SocketException e)
		{}

	}
}
