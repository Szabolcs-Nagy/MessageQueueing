package ds.jms.examples.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.naming.Context;
import javax.naming.NamingException;

/**
 * MessageQueue example based on wildfly-10
 * 
 */
public class SendMessage {
    private static final Logger log = Logger.getLogger(SendMessage.class.getName());

    // Set up all the default values
//    private static final String DEFAULT_MESSAGE = "Hello, World!";
    private static final String DEFAULT_CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
    private static final String DEFAULT_DESTINATION = "jms/queue/myQueue4";
    private static final String DEFAULT_MESSAGE_COUNT = "10";  
    private static final String DEFAULT_USERNAME = "JMSuser1000";
    private static final String DEFAULT_PASSWORD = "Password1";      


    public static void main(String[] args) throws IOException {

        Context namingContext = null;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        try {
            String userName = System.getProperty("username", DEFAULT_USERNAME);
            String password = System.getProperty("password", DEFAULT_PASSWORD);

            // Set up the namingContext for the JNDI lookup
            namingContext = BuilderFactory.createContext(userName, password);
            
            // Perform the JNDI lookups
            String connectionFactoryString = System.getProperty("connection.factory", DEFAULT_CONNECTION_FACTORY);
            log.info("Attempting to acquire connection factory \"" + connectionFactoryString + "\"");
            
            ConnectionFactory connectionFactory = (ConnectionFactory) namingContext.lookup(connectionFactoryString);
            log.info("Found connection factory \"" + connectionFactoryString + "\" in JNDI");

            String destinationString = System.getProperty("destination", DEFAULT_DESTINATION);
            log.info("Attempting to acquire destination \"" + destinationString + "\"");
            
            Destination destination = (Destination) namingContext.lookup(destinationString);
            log.info("Found destination \"" + destinationString + "\" in JNDI");


            try ( JMSContext context = connectionFactory.createContext(userName, password) ) {
                // Send the specified number of messages
            System.out.println("Enter the name of the item you wish to purchase");
            String item = bufferedReader.readLine();
            
            System.out.println("Enter the price of the item you wish to purchase");
            int price = Integer.parseInt(bufferedReader.readLine());
            
            System.out.println("Enter the amount of the item you wish to purchase");
            String amount = bufferedReader.readLine();
            
            int points =  Integer.parseInt(amount) * price/10;
            
            int count = Integer.parseInt(System.getProperty("message.count", amount));
            String content = System.getProperty("message.content", item);

                for (int i = 1; i <= count; i++) {
                    context.createProducer().send(destination, content);
                    System.out.println("Sending purchase number " + i + " by user: " + userName + " item purchased " + content);
                }
                System.out.println("Total points earned: " + points);
            }
        } catch (NamingException e) {
            log.severe(e.getMessage());
            e.printStackTrace();
        } finally {
            if (namingContext != null) {
                try {
                    namingContext.close();
                } catch (NamingException e) {
                    log.severe(e.getMessage());
                }
            }
        }
    }

    
}
