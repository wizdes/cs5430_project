/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package transport_layer.files;

/**
 *
 * @author Patrick C. Berens
 */
public interface FileTransportInterface {
    //Patrick
    public String openUnserializedFile(String filename);
    public java.io.Serializable readFile(String filename);
    public boolean writeFile(String filename, java.io.Serializable content);
}
