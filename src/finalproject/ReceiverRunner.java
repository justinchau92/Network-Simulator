package finalproject;

/*---------------------------------------------------------------------------------------
--	SOURCE FILE:		ReceiverRunner.java - 
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
				Receiver Runner -  Uses Receiver class that sends ACKs back to the Sender
				SRConfiguration as object to create the receiver
--				
--
--	DESIGNERS:		Justin Chau & Paul Cabanez
--
--	PROGRAMMERS:		Justin Chau & Paul Cabanez
--

---------------------------------------------------------------------------------------*/

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Receiver Runner
 *
 */
public class ReceiverRunner {
	public static void main(String[]args) throws IOException{

		SRConfiguration config = new SRConfiguration(); //create configuration file
		
		System.out.println(config);
		Receiver receiver = new Receiver(config);
		
		receiver.run();
		
		
		
	}
}
