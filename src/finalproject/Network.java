package finalproject;

/*---------------------------------------------------------------------------------------
--	SOURCE FILE:		Network.java 
--
--	PROGRAM:		Network.java
--
--	
--
--	DATE:			November 26th 2016
--
--	REVISIONS:		(Date and Description)
--
--				November 26 2016
				Create this as an object in the network runner using the network configuration object
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
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

/**
 * Network Class
 *
 */
public class Network {

		private NetworkConfiguration config; // configuration file with set config
		
		private DatagramSocket networkReceivePacket; //ACK to Host
		private DatagramSocket networkSendSocket; // Data to Host
		
		PrintWriter writer;
		
		byte[] data = new byte[1024]; //data
		
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy-HH.mm.ss"); // date
		
		
		/**
		 * Constructor
		 * @param config NetworkConfiguration
		 */
		public Network(NetworkConfiguration config)
		{
			this.config = config;
		}
		
		/**
		 * Main runnable of the class
		 */
		public void run()
		{
			Date date = new Date();
			String dateLog = dateFormat.format(date);
			try {
				writer = new PrintWriter(dateLog +"_Network_Log.txt", "UTF-8");
			} catch (FileNotFoundException | UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}	
			writer.write("Network is now running");

			System.out.println("Network is now running");
			
			int totalPackets = 0;
			int totalPacketsDropped = 0;
			int totalPacketsTransmitted = 0;
			
			DatagramSocket socket = null;
			
			try
			{
				socket = new DatagramSocket(config.getNetworkPort()); // create network socket
			}
			catch (SocketException e)
			{
				e.printStackTrace();
				System.exit(0);
			}
			
			//run forever
			while(true)
			{
				try
				{
					DatagramPacket datagramPacket = new DatagramPacket(data, data.length);
					socket.receive(datagramPacket);	//receive data
					data = datagramPacket.getData();
					
					ByteArrayInputStream ba = new ByteArrayInputStream(data);
					ObjectInputStream o = new ObjectInputStream(ba);
					
					Packet packet = (Packet) o.readObject(); // read the packet
					
					totalPackets ++;
					
					//if its a control packet SOT or EOT
					if(packet.getPacketType() ==1 || packet.getPacketType() == 4)
					{
						data = new byte[1024];
						
						ByteArrayOutputStream baOut = new ByteArrayOutputStream();
						ObjectOutputStream oOut = new ObjectOutputStream(baOut);
						
						oOut.writeObject(packet);
						oOut.close();
						
						data = baOut.toByteArray();
						
						datagramPacket = new DatagramPacket(data,data.length, InetAddress.getByName(packet.getDestinationAddress()), packet.getDestinationPort());
						
						//send the data through
						socket.send(datagramPacket);
						
						totalPacketsTransmitted ++;
						date = new Date();
						dateLog = dateFormat.format(date);
						System.out.println(dateLog +" Sent : " +  packet);
						writer.write(dateLog +" Sent : " +  packet);
					}
					//any other packet you can drop
					else
					{
						//if packet drop rate is lower than the threshold drop the packet
						if(this.getDropRateThreshold() <= this.config.getBitErrorRate())
						{
							date = new Date();
							dateLog = dateFormat.format(date);
							
							System.out.println(dateLog + " Dropped : " + packet);
							writer.write(dateLog + " Dropped : " + packet);
							
							totalPacketsDropped++;
							
						}
						else
						{
							// if packet drop rate is greater than threshold, transmit it
							Thread.sleep(this.config.getAverageDelayPerPacket());
							
							data = new byte[1024];
							
							ByteArrayOutputStream baOut = new ByteArrayOutputStream();
							ObjectOutputStream oOut = new ObjectOutputStream(baOut);
							
							oOut.writeObject(packet);
							oOut.close();
							
							data = baOut.toByteArray();
							
							datagramPacket = new DatagramPacket(data,data.length, InetAddress.getByName(packet.getDestinationAddress()), packet.getDestinationPort());
							
							socket.send(datagramPacket); //sends
							
							totalPacketsTransmitted ++;
							date = new Date();
							dateLog = dateFormat.format(date);
							System.out.println(dateLog + " Sent : " + packet);
							writer.write(dateLog + " Sent : " + packet );
							
						}
					}
					
				} catch(Exception e){
					
				}
			}
		}
		

		
		/**
		 * Randomly generates a number to see if its below the bit rate
		 * @return random number
		 * @throws IOException
		 */
		private int getDropRateThreshold() throws IOException{
			Random random = new Random();
			int threshold = random.nextInt((100 - 1) + 1) + 1;
			return threshold;
		}
		
}
