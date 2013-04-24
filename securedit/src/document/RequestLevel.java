/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package document;

/**
 *
 */
public class RequestLevel extends DocumentCommand {
    
    private int level;
    
    public RequestLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
    
    @Override
    public String toString() {
        return "RequestLevel(" + this.getLevel() + ")";
    }   
    
}
