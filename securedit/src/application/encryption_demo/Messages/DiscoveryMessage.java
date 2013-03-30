/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package application.encryption_demo.Messages;

/**
 *
 * @author Patrick C. Berens
 */
public class DiscoveryMessage implements Message {
    public String sourceID;
    public String sourceIP;
    public Integer sourcePort;

    public DiscoveryMessage(String myID, String myIP, Integer myPort) {
        this.sourceID = myID;
        this.sourceIP = myIP;
        this.sourcePort = myPort;
    }
    
    @Override
    public String toString(){
        return sourceID + ", " + sourceIP + ", " + sourcePort;
    }
}
