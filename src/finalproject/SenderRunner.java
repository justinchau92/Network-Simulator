package finalproject;
/*---------------------------------------------------------------------------------------
--	SOURCE FILE:		SenderRunner.java 
--
--	PROGRAM:		SenderRunner.java
--
--	
--
--	DATE:			November 26th 2016
--
--	REVISIONS:		(Date and Description)
--
--				November 26 2016
				Runner class for Sender
				Needs SRConfiguration file to create Sender class and then it runs from method run
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
 * Sender Runner
 *
 */
public class SenderRunner {
	public static void main(String[]args) {
		

		SRConfiguration config = new SRConfiguration();

		System.out.println(config);
		Sender sender = new Sender(config);
		
		try {
			sender.run();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
}
