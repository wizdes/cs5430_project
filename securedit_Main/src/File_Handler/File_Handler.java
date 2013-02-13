/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package File_Handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    public static byte[] readFile (File file) throws IOException {
        // Open file
        RandomAccessFile f = new RandomAccessFile(file, "r");

        try {
            // Get and check length
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength) throw new IOException("File size >= 2 GB");

            // Read file and return data
            byte[] data = new byte[length];
            f.readFully(data);
            return data;
        }
        finally {
            f.close();
        }
    }

    @Override
    public byte[] readByteFile(String filename) {
        FileInputStream fis = null;
        try {
            RandomAccessFile f = new RandomAccessFile(new File(filename), "r");
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength){
                throw new IOException("File size is too big");
            }

            // Read file and return data
            byte[] data = new byte[length];
            f.readFully(data);
            return data;
        } catch (IOException ex) {
            Logger.getLogger(File_Handler.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(File_Handler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    @Override
    public String readStringFile(String filename) {
        byte[] file = readByteFile(filename);
        String file_string = new String(file);
        return file_string;
    }
    
}
