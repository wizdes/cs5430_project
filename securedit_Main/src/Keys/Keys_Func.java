/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Keys;
import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;

/**
 *
 * @author yjli_000
 */
public class Keys_Func implements Serializable{
    public Map<String, PublicKey> id_pub_key;
    public Map<String, String> id_session_key;
    public Map<String, String> salt;
    public PrivateKey my_priv_key;
}
