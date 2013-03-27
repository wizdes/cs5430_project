/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package _extension_lib;

import java.util.Objects;

/**
 * Horray for Pair
 * -Generic, Immutable
 * @author Patrick
 */
public class Pair<Left, Right> {
  public final Left left; 
  public final Right right; 
  public Pair(Left left, Right right) { 
    this.left = left; 
    this.right = right; 
  } 

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.left);
        hash = 37 * hash + Objects.hashCode(this.right);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Pair<Left, Right> other = (Pair<Left, Right>) obj;
        if (!Objects.equals(this.left, other.left)) {
            return false;
        }
        if (!Objects.equals(this.right, other.right)) {
            return false;
        }
        return true;
    }
}
