/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import java.io.File;
import java.util.Collection;
import messages.Message;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author goggin
 */
public class NetworkTest {
    
    public NetworkTest() {
    }
    
    @Test
    public void testReadNeighbors() {
        Node me = new Node("1", "localhost", 4001);
        Network network = new Network(me);
        
        File f = new File("test/network/test_hosts.txt");
        Collection<Node> neighbors = network.readNeighbors(f);
        
        assertEquals(4, neighbors.size());
        assertTrue(neighbors.contains(new Node("2", "localhost", 4002)));
        assertTrue(neighbors.contains(new Node("3", "localhost", 4003)));
        assertTrue(neighbors.contains(new Node("4", "localhost", 4004)));
        assertTrue(neighbors.contains(new Node("5", "localhost", 4005)));
        
        assertFalse(neighbors.contains(new Node("1", "localhost", 4001)));
    }

}
