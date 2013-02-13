/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package File_Handler;

/**
 *
 * @author yjli_000
 */
public interface File_Handler_Interface {
    public boolean writeToFile(String filename, byte[] data);
    public boolean writeToFile(String filename, String data);
    public byte[]  readByteFile(String filename);
    public String  readStringFile(String filename);
    
}
