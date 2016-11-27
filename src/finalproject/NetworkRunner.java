package finalproject;
/*---------------------------------------------------------------------------------------
--	SOURCE FILE:		NetworkRunner.java - 
--
--	PROGRAM:		NetworkRunner.java
--
--	
--
--	DATE:			November 26th 2016
--
--	REVISIONS:		(Date and Description)
--
--				November 26 2016
				Network Runner - Uses Network.java and NetworkConfiguration.java to work
--				
--
--	DESIGNERS:		Justin Chau & Paul Cabanez
--
--	PROGRAMMERS:		Justin Chau & Paul Cabanez
--

---------------------------------------------------------------------------------------*/
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Network Runner class
 *
 */
public class NetworkRunner {
	//Main
	public static void main(String[] args)
	{
		
		NetworkConfiguration config = new NetworkConfiguration(); //create the config
		System.out.println(config);
		Network network = new Network(config);
		network.run();
		
		
		
		
	}
}
