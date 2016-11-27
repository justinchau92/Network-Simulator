package finalproject;

import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sender Class 
 *
 */
public class Sender3 {

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
	
	DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy-HH.mm.ss");
	
	/**
	 * Sender constructor
	 * @param config Configuration object of the SRConfiguration class
	 */
	public Sender3(SRConfiguration config)
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
		System.out.println("Sender is running");
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
			
			Date date = new Date();
			String dateLog = dateFormat.format(date);
			
			//print that the SOT has been sent
			System.out.println(dateLog +" Start of Transmission Sent : " + SOT );
			
			//get SOT Response
			try
			{
				Packet responsePacket = getPacket();
				
				if(responsePacket.getPacketType() == 1)
				{
					
					System.out.println(dateLog  + " SOT ACK received : " + responsePacket);
				}
				
				Thread.sleep(2000);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				
			}
			
			packetsSent = 0;
			
			while(packetsSent < this.config.getMaxPacketsToSend())
			{
				this.generateWindowAndSend();
				
				this.waitForAcks = true;
				
				this.setACKTimers();
				
				while(!this.packetWindow.isEmpty())
				{
					if(!this.waitForAcks)
					{
						System.out.println("Window Status : " + packetWindow.size() + " packets left in the current window!");
					}
				}
				
				packetsSent += this.config.getMaxPacketsToSend();
				
				System.out.println("Sent Packets : " + packetsSent);
				System.out.println("Remaining Packets : " + (this.config.getMaxPacketsToSend() - packetsSent));
			
			}
			
			this.sendEndOfTransmissionPacket();
			
			System.out.println("DONE");
			System.exit(0);
			

		
	}
	
	private void sendEndOfTransmissionPacket() {
		// TODO Auto-generated method stub
		Packet packet = new Packet(4, this.seqNum, this.config.getWindowSize(), this.seqNum,  
				 this.config.getReceiverAddress().getHostAddress(),this.config.getSenderAddress().getHostAddress(),
				this.config.getSenderPort(),this.config.getReceiverPort());
		
		try {
			this.sendPacket(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("LAST PACKET SENT");
		
		
		
	}

	public void waitAndResend() throws SocketException 
	{
		this.timer = new Timer();
		this.timer.schedule(new TimerTask(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				timer.cancel();
				timer.purge();
				
				timer = null;
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
							
						} 
						catch (IOException e) 
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				
			}
			
		}, this.config.getMaxTimeout());
		
		socket.setSoTimeout(2000);
		
		//check for the ACKS
		for(int i=0; i < this.config.getWindowSize(); i++)
		{

			Packet packet = getPacket();
			
			Date date = new Date();
			String dateLog = dateFormat.format(date);
					
			System.out.println(dateLog + " Packet ACK Received " + packet);

			this.removePacketFromWindow(packet.getAckNum());
			this.removeFromPacketArray(packet);

		}
	}
	
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
	 * Setting timers for acks and check
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public void setACKTimers() 
	{
		//Set timer and wait for the ACKS
		this.timer = new Timer();
		this.timer.schedule(new TimerTask(){

			@Override
			public void run() {
			
				Sender3.this.ackTimeout();

			}
			
		}, this.config.getMaxTimeout());
		
		try {
			this.receiveACKs();
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	
	private void receiveACKs() throws ClassNotFoundException, IOException {
		// TODO Auto-generated method stub
		try
		{
			this.socket.setSoTimeout(2000);
			
			while(this.packetWindow.size() != 0 && this.waitForAcks)
			{
				Packet packet = getPacket();
				
				if(packet.getPacketType() == 3)
				{
					this.removePacketFromWindow(packet.getAckNum());
				}
			}
		}
		catch(SocketException e)
		{
			
		}
	}

	protected void ackTimeout()  {
		// TODO Auto-generated method stub
		this.stopTimerAndAckReceiverThread();
		
		if(!this.packetWindow.isEmpty())
		{
			this.waitForAcks = true;
			
			for(int i = 0; i < this.packetWindow.size(); i++)
			{
				Packet packet = this.packetWindow.get(i);
				try {
					this.sendPacket(packet);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Date date = new Date();
				String dateLog = dateFormat.format(date);
				System.out.println(date + " Resending Packet : " + packet);
			}
			
			this.setACKTimers();
		}
	}

	private void stopTimerAndAckReceiverThread() {
		// TODO Auto-generated method stub
		this.timer.cancel();
		this.timer.purge();
		this.timer = null;
		
		this.waitForAcks = false;
		
	}

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
	
	private Packet getPacket() 
	{

			data = new byte[1024];
			datagramPacket = new DatagramPacket(data, data.length);
			try {
				socket.receive(datagramPacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			data = datagramPacket.getData();
			
			ByteArrayInputStream baIn = new ByteArrayInputStream(data);
			ObjectInputStream oIn;
			try {
				oIn = new ObjectInputStream(baIn);
				Packet temp = (Packet) oIn.readObject();
				return temp;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			return null;
			
			
			
		
	}
	
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
	
	public void generateWindowAndSend() 
	{
		for(int i =1; i<=this.config.getWindowSize();i++)
		{
			Packet packet = new Packet(2, this.seqNum, this.config.getWindowSize(), this.seqNum,  
					 this.config.getReceiverAddress().getHostAddress(),this.config.getSenderAddress().getHostAddress(),
					this.config.getSenderPort(),this.config.getReceiverPort());
			
			this.packetWindow.add(packet);
			
			try {
				this.sendPacket(packet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			this.seqNum++;
		}
	}
	
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
