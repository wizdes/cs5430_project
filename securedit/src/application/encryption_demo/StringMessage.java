/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package application.encryption_demo;


/**
 *
 */
class StringMessage implements Message {
    String contents;
    
    StringMessage(String contents) {
        this.contents = contents;
    }
}
