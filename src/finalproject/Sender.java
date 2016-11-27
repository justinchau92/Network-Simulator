package finalproject;

import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/*---------------------------------------------------------------------------------------
--	SOURCE FILE:		Sender.java -   Sender Class
--
--	PROGRAM:		Sender.java
--
--	
--
--	DATE:			November 26th 2016
--
--	REVISIONS:		(Date and Description)
--
--				November 26 2016
				Sender Class
--				
--
--	DESIGNERS:		Justin Chau & Paul Cabanez
--
--	PROGRAMMERS:		Justin Chau & Paul Cabanez
--
--	NOTES:
--	This is just a class that needs to be created with a SRConfiguration object in SenderRunner
---------------------------------------------------------------------------------------*/

/**
 * Sender Class 
 *
 */
/**
 * @author Chau
 *
 */
public class Sender {

	private int seqNum;
	private ArrayList<Packet> packetWindow;
	private ArrayList<Packet> packetArray;
	private Timer timer;
	private boolean waitForAcks;
	private DatagramSocket socket;
	private DatagramPacket datagramPacket;
	private SRConfiguration config;
	private byte[] data;
	
	private Packet EOT;
	private Packet SOT;
	private Packet packet;
	
	private int packetsSent;
	PrintWriter writer;
	DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy-HH.mm.ss");
	
	
	/**
	 * Sender constructor
	 * @param config Configuration object of the SRConfiguration class
	 */
	public Sender(SRConfiguration config)
	{
		this.seqNum = 1;
		this.packetWindow = new ArrayList<Packet>();
		this.config = config;
	}
	
	/**
	 * THe Sender Runnable method
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void run() throws IOException, ClassNotFoundException
	{
		Date date = new Date();
		String dateLog = dateFormat.format(date);
		writer = new PrintWriter(dateLog +"_Sender_Log.txt", "UTF-8");	
		System.out.println("Sender is running");
		writer.write("Sender is running");
		try
		{
			//create socket connection to sender port
			socket = new DatagramSocket(config.getSenderPort());	
		}
		catch(Exception e)
		{

		}
			//start the transmission
			//make SOT packet
			SOT = new Packet(1, this.seqNum, this.config.getWindowSize(), this.seqNum,  
					 this.config.getReceiverAddress().getHostAddress(),this.config.getSenderAddress().getHostAddress(),
					this.config.getSenderPort(),this.config.getReceiverPort());
			
			//send the SOT packet to the network with information
			sendPacket(SOT);
			
			date = new Date();
			dateLog = dateFormat.format(date);
			
			//print that the SOT has been sent
			System.out.println(dateLog +" Start of Transmission Sent : " + SOT );
			writer.write(dateLog +" Start of Transmission Sent : " + SOT );
			//get SOT Response
			try
			{
				Packet responsePacket = getPacket();
				
				if(responsePacket.getPacketType() == 1)
				{
					
					System.out.println(dateLog  + " SOT ACK received : " + responsePacket);
					writer.write(dateLog  + " SOT ACK received : " + responsePacket);
				}
				
				Thread.sleep(2000);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				
			}
			
			packetsSent = 0;
			
			//generate an array of packets
			createPackets();
			
			//if packetarray is not empty and prepped
			while(packetArray.size() > 0)
			{
				//prepare the window size
				this.prepareWindow();
				int l = 0;
				
				//send out the packets
				for(int i=0;i < packetWindow.size();i++)
				{
					if(packetArray.get(i).getPacketType() == 4)
					{
						if(packetArray.size() == 1)
						{
							sendPacket(packetArray.get(i));
							date = new Date();
							dateLog = dateFormat.format(date);
							
							System.out.println(dateLog + " End of Transmission Packet Sent : " + packetArray.get(i));
							writer.write(dateLog + " End of Transmission Packet Sent : " + packetArray.get(i));
						}
					}
					else 
					{
						sendPacket(packetArray.get(l));
						
						date = new Date();
						dateLog = dateFormat.format(date);
						
						System.out.println(dateLog + " Packet Sent : " + packetArray.get(l));
						writer.write(dateLog + " Packet Sent : " + packetArray.get(l));
						l++;
					}
				}
				l=0;

				waitAndResend();

			}	
	}
	
	/**
	 * Waits for the timeout and Resends data if timeout 
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public void waitAndResend() throws ClassNotFoundException, IOException
	{
		this.timer = new Timer();
		this.timer.schedule(new TimerTask(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				timer.cancel();
				timer.purge();
				
				timer = null;
				
				//if window still has items
				if(packetWindow.size() > 0)
				{
					for(int i = 0; i < packetWindow.size(); i++)
					{
						Packet packet = packetWindow.get(i);
						
						try {
							//resend
							sendPacket(packet);
							
							Date date = new Date();
							String dateLog = dateFormat.format(date);
							
							System.out.println(dateLog +" Resending Packet : " + packet);
							writer.write(dateLog +" Resending Packet : " + packet);
							
						} 
						catch (IOException e) 
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					try {
						waitAndResend();
					} catch (ClassNotFoundException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
			
		}, this.config.getMaxTimeout());
		
		//socket.setSoTimeout(2000);
		
		//check for the ACKS
		for(int i=0; i < this.config.getWindowSize(); i++)
		{

			Packet packet = getPacket();
			
			Date date = new Date();
			String dateLog = dateFormat.format(date);
					
			if(packet != null && packet.getPacketType() == 4)
			{
				
				System.out.println(dateLog + " Packet EOT ACK Received " + packet);
				writer.write(dateLog + " Packet EOT ACK Received " + packet);
				System.out.println("Terminating");
				System.exit(0);
			}
			else if(packet != null)
			{
				System.out.println(dateLog + " Packet ACK Received " + packet);
				writer.write(dateLog + " Packet ACK Received " + packet);
				
				this.removePacketFromWindow(packet.getAckNum());
				this.removeFromPacketArray(packet);
			}
			
			

		}
	}
	
	
	/**
	 * Removes packet from packetArray list
	 * @param packet
	 */
	public void removeFromPacketArray(Packet packet)
	{
		for(int i = 0; i < packetArray.size(); i++)
		{
			if(packetArray.get(i).getSeqNum() == packet.getSeqNum())
			{
				packetArray.remove(i);
			}
		}
		
		packetArray.remove(packet);
	}
	
	/**
	 * Sends packet
	 * @param packet
	 * @throws IOException
	 */
	private void sendPacket(Packet packet) throws IOException
	{

			data = new byte[1024];
			
			ByteArrayOutputStream baOut = new ByteArrayOutputStream();
			ObjectOutputStream oOut = new ObjectOutputStream(baOut);
			
			oOut.writeObject(packet);
			oOut.close();
			
			data = baOut.toByteArray();
			
			datagramPacket = new DatagramPacket(data,data.length,this.config.getNetworkAddress(), this.config.getNetworkPort());
			
			socket.send(datagramPacket);

	}

	/**
	 * Gets packet from socket
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private Packet getPacket() throws IOException, ClassNotFoundException
	{

			data = new byte[1024];
			datagramPacket = new DatagramPacket(data, data.length);
			socket.receive(datagramPacket);
			
			data = datagramPacket.getData();
			
			ByteArrayInputStream baIn = new ByteArrayInputStream(data);
			try{
				ObjectInputStream oIn = new ObjectInputStream(baIn);
				Packet temp = (Packet) oIn.readObject();
				return temp;
			}
			catch(Exception e)
			{
				
			}
			return null;

	}
	
	/**
	 * Create the packets into an arraylist
	 */
	public void createPackets()
	{
		packetArray = new ArrayList<Packet>();
		//generate an array of packets
		for(int i = 1; i <= this.config.getMaxPacketsToSend(); i ++)
		{
			packet = new Packet(2, this.seqNum, this.config.getWindowSize(), this.seqNum,  
					 this.config.getReceiverAddress().getHostAddress(),this.config.getSenderAddress().getHostAddress(),
					this.config.getSenderPort(),this.config.getReceiverPort());
			
			this.packetArray.add(packet);
			this.seqNum ++ ;
		}
		
		EOT = new Packet(4, this.seqNum, this.config.getWindowSize(), this.seqNum,  
				 this.config.getReceiverAddress().getHostAddress(),this.config.getSenderAddress().getHostAddress(),
				this.config.getSenderPort(),this.config.getReceiverPort());
		
		this.packetArray.add(EOT);
		
	}
	
	/**
	 * Creates the window with window size from configuration
	 */
	public void prepareWindow()
	{
		packetWindow = new ArrayList(this.config.getWindowSize());
		// if we have less packets to send than windowsize , fill remaining packets
		if(packetArray.size() < this.config.getWindowSize())
		{
			for(int i=0;i<packetArray.size();i++)
			{
				packetWindow.add(packetArray.get(i));
			}
		}
		else
		{
			for(int i = 0; i < this.config.getWindowSize(); i++)
			{
				packetWindow.add(packetArray.get(i));
			}
		}
	}
	
	/**
	 * Removes the packet from the window
	 * @param ackNum
	 */
	public void removePacketFromWindow(int ackNum)
	{
		for(int i = 0 ; i < this.packetWindow.size();i++)
		{
			if(this.packetWindow.get(i).getAckNum() == ackNum)
			{
				this.packetWindow.remove(i);
			}// end if
		}// end for
	}
	
}
