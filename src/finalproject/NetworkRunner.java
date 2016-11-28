package finalproject;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
import java.util.Properties;
import java.util.Scanner;

/**
 * Network Runner class
 *
 */
public class NetworkRunner {
	//Main
	public static void main(String[] args)
	{
		
		NetworkConfiguration config = loadNetworkConfiguration(); //create the config
		System.out.println(config);
		Network network = new Network(config);
		network.run();
			
	}
	
	/**
	 * loads/reads the properties file into a NetworkConfiguration class
	 * @return NetworkConfiguration object fully configured
	 */
	public static NetworkConfiguration loadNetworkConfiguration()
	            
	    {
	        Properties networkProperties = new Properties();
	        InputStream fileInputStream = null;
	        NetworkConfiguration configuration = new NetworkConfiguration();

	        try
	        {
	            // create a stream to the properties file
	            fileInputStream = new FileInputStream("NetworkConfiguration.properties");

	            // load the configuration file.
	            networkProperties.load(fileInputStream);

	            // set configuration properties
	            configuration.setReceiver(InetAddress.getByName(networkProperties
	                    .getProperty("receiverAddress")));
	            configuration.setReceiverPort(Integer.parseInt(networkProperties
	                    .getProperty("receiverPort")));
	            configuration.setSender(InetAddress.getByName(networkProperties
	                    .getProperty("senderAddress")));
	            configuration.setSenderPort(Integer.parseInt(networkProperties
	                    .getProperty("senderPort")));
	            configuration.setBitErrorRate(Integer.parseInt(networkProperties.getProperty("bitErrorRate")));
	            configuration.setAverageDelayPerPacket(Integer.parseInt(networkProperties
	                    .getProperty("averageDelayPerPacket")));
	            configuration.setNetworkPort(Integer.parseInt(networkProperties
	                    .getProperty("networkPort")));

	        }
	        catch (FileNotFoundException e)
	        {
	        	System.out.println("Couldn't read the configuration file.");
	        }
	        catch (UnknownHostException e)
	        {
	            System.out.println(e.getMessage());
	        }
	        catch (IOException e)
	        {
	            System.out.println("Couldn't load the configuration file.");
	        }
	        finally
	        {
	            if (fileInputStream != null)
	            {
	                try
	                {
	                    fileInputStream.close();
	                }
	                catch (IOException e)
	                {
	                    e.printStackTrace();

	                    System.out.println("Couldn't close the configuration file.");
	                }
	            }
	        }

	        return configuration;
	    }

}
