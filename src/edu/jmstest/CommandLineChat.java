package edu.jmstest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Properties;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;


public class CommandLineChat implements MessageListener{

    /**
     * @param args the command line arguments
     * @throws javax.jms.JMSException
     * @throws javax.naming.NamingException
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws JMSException, NamingException, IOException {
        if(args.length!=3) System.out.println("usar: Usuario suscribe-to-queue-name publish-to-queue-name");
        else{
            String username = args[0];
            System.out.println("Usuario: "+ username+ "| Suscrito a: "+args[1]+"| Publica en: "+args[2]);
            Context initialContext = CommandLineChat.getInitialContext();
            CommandLineChat commandLineChat = new CommandLineChat();
            Queue queue1 = (Queue)initialContext.lookup(args[1]);
            Queue queue2 = (Queue)initialContext.lookup(args[2]);
            JMSContext jmsContext = ((ConnectionFactory)initialContext.lookup("java:comp/DefaultJMSConnectionFactory")).createContext();
            jmsContext.createConsumer(queue1).setMessageListener(commandLineChat);
            JMSProducer jmsProducer = jmsContext.createProducer();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            String  messageToSend = null;
            while(true){
                messageToSend = bufferedReader.readLine();
                if(messageToSend.equalsIgnoreCase("exit")){
                    jmsContext.close();
                    System.exit(0);
                }else{
                    jmsProducer.send(queue2,"["+username+": "+messageToSend+"]");
                }
            }
        }
        
    }

    @Override
    public void onMessage(Message message) {
        try{
            System.out.println(message.getBody(String.class));
        } catch (JMSException ex) {
           ex.printStackTrace();
        }
    }
    
    public static Context getInitialContext() throws JMSException, NamingException {
//        Properties properties = new Properties();
//        properties.setProperty("java.naming.factory.initial", "com.sun.enterprise.naming.SerialInitContextFactory");
//        properties.setProperty("java.naming.factory.url.pkgs", "com.sun.enterprise.naming");
//        properties.setProperty("java.naming.provider.url", "iiop://localhost:3700");
//        return new InitialContext(properties);
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY,  
        "com.sun.enterprise.naming.SerialInitContextFactory");

        Context ctx = new InitialContext(env);
        return ctx;
    }
    
//     public static Context getInitialContext() throws JMSException, NamingException {
//        Properties properties = new Properties();
//        properties.setProperty("java.naming.factory.initial", "com.sun.enterprise.naming.SerialInitContextFactory");
//        properties.setProperty("java.naming.factory.url.pkgs", "com.sun.enterprise.naming");
//        properties.setProperty("java.naming.provider.url", "iiop://localhost:3700");
//        return (Context) new InitialContext(properties);
//    }
    
}













