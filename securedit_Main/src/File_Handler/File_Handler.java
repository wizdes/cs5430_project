/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package File_Handler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.misc.IOUtils;

/**
 *
 * @author yjli_000
 */
public class File_Handler implements File_Handler_Interface{

    @Override
    public boolean writeToFile(String filename, byte[] data) {
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(new File(filename));
            output.write(data);
        } catch (IOException ex) {
            Logger.getLogger(File_Handler.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                output.close();
            } catch (IOException ex) {
                Logger.getLogger(File_Handler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return true;
    }

    @Override
    public boolean writeToFile(String filename, String data) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(filename);
            out.print(data);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(File_Handler.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            out.close();
        }
        return true;
    }

    @Override
    public byte[] readByteFile(String filename) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String readStringFile(String filename) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
