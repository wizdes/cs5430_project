/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package transport_layer.files;

import java.io.Serializable;

/**
 *
 * @author Patrick C. Berens
 */
public class FileHandler implements FileTransportInterface{
    /******************************************
     * Low Level File operations similar to network
     * -Patrick's
     ******************************************/
    @Override
    public Serializable readFile(String filename) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Serializable writeFile(String filename, Serializable content) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
}
