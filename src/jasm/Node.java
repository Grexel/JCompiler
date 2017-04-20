/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jasm;

/**
 *
 * @author Jeff
 */
public abstract class Node {
    int address;
    Token label;

    public Node(Token label) {
        this.label = label;
        address = 0;
    }
    
    public abstract int getLength();
    
    public boolean hasLabel(){
        return label != null;
    }
    @Override
    public String toString(){
        if(label != null)
        return label.toString();
        else return "No Label";
    }
}
