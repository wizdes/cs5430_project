/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package document;

/**
 *
 * @author yjli_000
 */
public class DeleteUser extends DocumentCommand{
    private String userID;

    public DeleteUser(String userID){
        this.userID = userID;
    }
    
    public void setUserID(String userID){
        this.userID = userID;
    }
    
    public String getUserID(){
        return this.userID;
    }
    
    @Override
    public String toString() {
        return "UpdateLevel(user " + this.getUserID() + " ~> )";
    }        
}
