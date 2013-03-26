/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application.encryption_demo;

import java.util.ArrayList;
import security_layer.Profile;

/**
 *
 * @author Yi
 */
public class HAuthFunctionality {
    String myIdent = "0";
    String theirIdent = "1";
    
    CommunicationInterface myCommunicator;
    CommunicationInterface theirCommunicator;
    
    static String password_0 = "pass0000pass0000";
    static String password_1 = "pass1111pass1111";
    
    public void setUp() throws Exception {
        Profile.deleteProfile(myIdent);
        Profile p1 = Profile.writeProfile(myIdent, password_0, 4000, "localhost");
        
        Profile.deleteProfile(theirIdent);
        Profile p2 = Profile.writeProfile(theirIdent, password_1, 4001, "localhost");
        
        p1.save(password_0);
        p2.save(password_1);

        myCommunicator = new Communication(p1, password_0);
        theirCommunicator = new Communication(p2, password_1);
        
        myCommunicator.updatePeers(theirIdent, "localhost", 4001, new ArrayList<String>(), true);
        theirCommunicator.updatePeers(myIdent, "localhost", 4000, new ArrayList<String>(), true);
    }
        
    public void testHumanAuthentication() {
        myCommunicator.authenticateHuman(theirIdent);
    }  
    
    public void updatePIN(String PIN){
        myCommunicator.updatePin(theirIdent, PIN);
    }
        
}
