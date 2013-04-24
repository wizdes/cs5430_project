/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package document;

/**
 *
 */
public class UpdateLevel extends DocumentCommand {
    private int level;
    private String userId;
    
    public UpdateLevel(String userId, int level) {
        this.level = level;
        this.userId = userId;
    }
    
    @Override
    public String toString() {
        return "UpdateLevel(user " + this.getUserId() + " ~> " + this.getLevel() + ")";
    }    

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
