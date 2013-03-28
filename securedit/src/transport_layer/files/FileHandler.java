/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package transport_layer.files;

import configuration.Constants;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.Serializable;

/**
 *
 * @author Patrick C. Berens
 */
public class FileHandler implements FileTransportInterface{
    /***********************************************
     * Low Level File operations similar to network
     * -Patrick's
     ***********************************************/
    @Override
    public Serializable readFile(String filename) {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        Serializable objectFile = null;
        try {
            fis = new FileInputStream(filename);
            ois = new ObjectInputStream(fis);
            objectFile = (Serializable)ois.readObject();
        } catch (ClassNotFoundException | IOException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(FileHandler.class.getName()).log(Level.SEVERE, "Reading file with filename: " + filename + ".", ex);
            }
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                if(Constants.DEBUG_ON){
                    Logger.getLogger(FileHandler.class.getName()).log(Level.SEVERE, "Reading file with filename: " + filename + ".", ex);
                }
            }
            try {
                ois.close();
            } catch (IOException ex) {
                if(Constants.DEBUG_ON){
                    Logger.getLogger(FileHandler.class.getName()).log(Level.SEVERE, "Reading file with filename: " + filename + ".", ex);
                }
            }
        }
        return objectFile;
    }

    @Override
    public boolean writeFile(String filename, Serializable content) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream(filename);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(content);
        } catch (IOException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(FileHandler.class.getName()).log(Level.SEVERE, "Writing file with filename: " + filename + ".", ex);
            }
        } finally {
            try {
                fos.close();
            } catch (IOException ex) {
                if(Constants.DEBUG_ON){
                    Logger.getLogger(FileHandler.class.getName()).log(Level.SEVERE, "Writing file with filename: " + filename + ".", ex);
                }
            }
            try {
                oos.close();
                return true;
            } catch (IOException ex) {
                if(Constants.DEBUG_ON){
                    Logger.getLogger(FileHandler.class.getName()).log(Level.SEVERE, "Writing file with filename: " + filename + ".", ex);
                }
            }
        }
        return false;
    }

    @Override
    public String openUnserializedFile(String filename) {
        try {
            Path path = FileSystems.getDefault().getPath(".", filename);
            byte[] fileArray = Files.readAllBytes(path);
            return new String(fileArray);
        } catch (IOException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(FileHandler.class.getName()).log(Level.SEVERE, "Opening unserialized file with filename: " + filename + ".", ex);
            }
            return null;
        }
        
    }
}
