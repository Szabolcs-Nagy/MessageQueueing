package ds.jms.examples.client;

import java.util.logging.Logger;
import javax.jms.MessageListener;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.NamingException;

/**
 * MessageQueue example based on wildfly-10
 * 
 */
public class ReceiveMessage implements MessageListener {
    
    private static final Logger log = Logger.getLogger(ReceiveMessage.class.getName());

    // Set up all the default values
    private static final String DEFAULT_CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
    private static final String DEFAULT_DESTINATION = "jms/queue/myQueue4";
    private static final String DEFAULT_MESSAGE_COUNT = "10";  
    private static final String DEFAULT_USERNAME = "JMSuser1000";
    private static final String DEFAULT_PASSWORD = "Password1";      
    public static String user;

    public static void main(String[] args) {

        Context namingContext = null;

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
            
            int count = Integer.parseInt(System.getProperty("message.count", DEFAULT_MESSAGE_COUNT));

            try ( JMSContext context = connectionFactory.createContext(userName, password) ) {
                // Create the JMS consumer
                JMSConsumer consumer = context.createConsumer(destination);
                // Then receive the same number of messages that were sent
//                    for (int i = 0; i < count; i++) {
                        String text = consumer.receiveBody(String.class, 5000);
                        System.out.println("Received message with content " + text);
//                    }
            }
        } catch (Exception e) {     
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

    @Override
    public void onMessage(Message message) {
                TextMessage textMessage = (TextMessage) message;
        try {
            System.out.println(user + " received "
                    + textMessage.getText());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    
}



