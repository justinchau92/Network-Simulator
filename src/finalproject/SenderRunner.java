package finalproject;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.Scanner;

/**
 * Sender Runner
 *
 */
public class SenderRunner {
	public static void main(String[]args) {
		

		SRConfiguration config = loadSRConfiguration();

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
	
	/**
	 * loads/reads the properties file into a SRConfiguration class
	 * @return SRConfiguration object fully configured
	 */
	public static SRConfiguration loadSRConfiguration()
	{
        Properties clientProperties = new Properties();
        InputStream fileInputStream = null;
        SRConfiguration configuration = new SRConfiguration();

        try
        {
            // create a stream to the properties file
            fileInputStream = new FileInputStream("SRConfiguration.properties");

            // load the configuration file
            clientProperties.load(fileInputStream);

            configuration.setNetworkAddress(InetAddress.getByName(clientProperties
                    .getProperty("networkAddress")));
            configuration.setNetworkPort(Integer.parseInt(clientProperties
                    .getProperty("networkPort")));
            configuration.setSenderAddress(InetAddress.getByName(clientProperties
                    .getProperty("senderAddress")));
            configuration.setSenderPort(Integer.parseInt(clientProperties
                    .getProperty("senderPort")));
            configuration.setReceiverAddress(InetAddress.getByName(clientProperties
                    .getProperty("receiverAddress")));
            configuration.setReceiverPort(Integer.parseInt(clientProperties
                    .getProperty("receiverPort")));
            configuration
                    .setWindowSize(Integer.parseInt(clientProperties.getProperty("windowSize")));
            configuration.setMaxPacketsToSend(Integer.parseInt(clientProperties
                    .getProperty("maxPackets")));
            configuration
                    .setMaxTimeout(Integer.parseInt(clientProperties.getProperty("maxTimeout")));
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
                    System.out.println("Couldn't close the configuration file.");
                }
            }
        }

        return configuration;
    }

}
