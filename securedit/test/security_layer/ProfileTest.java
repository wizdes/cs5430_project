/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

import java.io.File;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author goggin
 */
public class ProfileTest {
    
    private static String username = "goggin13";
    private static String password = "0000pass0000pass";
    private static int port = 5000;
    private static String host = "localhost";
    
    @Before
    public void tearDownTest() {
        Profile.deleteProfile(username);
    }
    
    @Test
    public void testReadAndWriteProfileSuccess() {
        Profile.writeProfile(username, password, port, host); 
        Profile obj = Profile.readProfile(username, password);
        assertEquals(obj.ident, username);
        assertEquals(obj.port, port);
        assertEquals(obj.host, host);
    }
    
    @Test
    public void testReadAndWriteProfileFileNotFound() {
        Profile obj = Profile.readProfile(username, password);
        assertEquals(null, obj);
    }
    
    @Test
    public void testReadAndWriteProfileBadPassword() {
        Profile.writeProfile(username, password, port, host); 
        Profile obj = Profile.readProfile(username, "bad password");
        assertEquals(null, obj);
    }    
}
