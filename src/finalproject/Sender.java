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

public class Sender {

	private int seqNum;
	private ArrayList<Packet> packetWindow;
	private ArrayList<Packet> packetArray;
	private ArrayList<Packet> ackList;

	private DatagramSocket socket;
	private DatagramPacket datagramPacket;
	private SRConfiguration config;
	private byte[] data;
	
	private Packet EOT;
	private Packet SOT;
	private Packet packet;
	

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
		ackList = new ArrayList<Packet>();
		Date date = new Date();
		String dateLog = dateFormat.format(date);
		writer = new PrintWriter(dateLog +"_Sender_Log.txt", "UTF-8");	
		System.out.println("Sender is running");
		writer.println("Sender is running");
	
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
			writer.println(dateLog +" Start of Transmission Sent : " + SOT );
			
			//get SOT Response
			try
			{
				Packet responsePacket = getPacket();
				
				if(responsePacket.getPacketType() == 1)
				{
					
					System.out.println(dateLog  + " SOT ACK received : " + responsePacket);
					writer.println(dateLog  + " SOT ACK received : " + responsePacket);
				}
				
				Thread.sleep(2000);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				
			}
			

			//generate an array of packets
			createPackets();
			socket.setSoTimeout(this.config.getMaxTimeout());
			
			while(!packetArray.isEmpty())
			{
				if(packetWindow.isEmpty())
				{
					prepareWindow();
				}
				
				System.out.println();
				System.out.println("Enter Send Mode");
				
				send();
			}
			
			EOT = new Packet(4, this.seqNum, this.config.getWindowSize(), this.seqNum,  
					 this.config.getReceiverAddress().getHostAddress(),this.config.getSenderAddress().getHostAddress(),
					this.config.getSenderPort(),this.config.getReceiverPort());
			
			System.out.println();
			System.out.println("Enter EOT Mode");

			
			packetArray.add(EOT);
			packetWindow.add(EOT);
			while(!packetArray.isEmpty())
			{
				send();
			}
	
			
			System.out.println("END OF TRANSMISSION");
			writer.println("END OF TRANSMISSION");
			
			writer.close();
	}
	
	
	/**
	 * Sender mode
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void send() throws IOException, ClassNotFoundException
	{
		Date date;
		String dateLog;
		
		//for every packet in the window send it
		for(int i = 0; i < packetWindow.size();i++)
		{
			
			date = new Date();
			dateLog = dateFormat.format(date);
			
			//if the packet is an EOT 
			if(packetWindow.get(i).getPacketType() == 4)
			{
				writer.println(dateLog + " EOT Sent " + packetWindow.get(i));
				System.out.println(dateLog + " EOT Sent " + packetWindow.get(i));
				
				sendPacket(packetWindow.get(i));
			}
			else
			{
				writer.println(dateLog + " Sent " + packetWindow.get(i));
				System.out.println(dateLog + " Sent " + packetWindow.get(i));
				
				sendPacket(packetWindow.get(i));
			}
		}
		
		System.out.println();
		System.out.println("Entering Receive mode");
		
		//goes into receive mode
		receive();
		
		//removes received packet from window and array list
		for(int i = 0; i < ackList.size();i++)
		{
			//removing from packetWindow
			removePacketFromWindow(ackList.get(i).getAckNum());
			
			//removing from packetArray
			removeFromPacketArray(ackList.get(i));
		}
	}
	
	/**
	 * Receives packets
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private void receive() throws ClassNotFoundException, IOException{
		Packet returnPacket;
		Date date;
		String dateLog;
		
		//goes through the window array to see if we received a packet then removes it from window and array list
		for(int i = 0; i < packetWindow.size();i++)
		{	
			
			returnPacket = getPacket();
			
			//makes sure that theres something
			if(returnPacket != null)
			{
				ackList.add(returnPacket);
				date = new Date();
				dateLog = dateFormat.format(date);
				writer.println(dateLog + " ACK Received " + returnPacket);
				System.out.println(dateLog + " ACK Received " + returnPacket);
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
	private Packet getPacket() throws IOException 
	{
		int bytes = 0;
		do{
			data = new byte[1024];
			datagramPacket = new DatagramPacket(data, data.length);
			
			try {
				socket.receive(datagramPacket);
			} catch (SocketTimeoutException e1) {
				// TODO Auto-generated catch block
				break;
			}
			data = datagramPacket.getData();
			

			ByteArrayInputStream baIn = new ByteArrayInputStream(data);
			try{
				ObjectInputStream oIn = new ObjectInputStream(baIn);
				Packet temp = (Packet) oIn.readObject();
				
				return temp;
			}
			catch(Exception e)
			{}
		} while(bytes == data.length );
		
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
