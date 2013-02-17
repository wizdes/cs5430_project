/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package network;

import java.util.Collection;
import messages.Message;

/**
 *
 */
public class Network implements NetworkInterface {
    
    private Server server;
    private Client client;
    
    public Network(Node host) {
        client = new Client();
        server = new Server(host);
        server.listen();
    }
    
    @Override
    public void sendMessage(Message m) {
        client.send(m);
    }
    
    @Override
    public Collection<Message> waitForMessages() {
        return server.waitForMessages();
    }
    
    public static void log(String msg) {
        System.out.println(msg);
    }
    
    public static void logError(String msg) {
        System.err.println(msg);
    }
}
