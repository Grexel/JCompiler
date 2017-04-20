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
public class VariableNode extends Node{
    Token value;
    @Override
    
    
    public int getLength() {
        return 1;
    }

    public VariableNode(Token label, Token value) {
        super(label);
        this.value = value;
    }

    @Override
    public String toString() {
        return super.toString() + value.toString();
    }
    
}
