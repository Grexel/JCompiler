/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jasm;

import java.util.ArrayList;

/**
 *
 * @author Jeff
 */
public class InstructionNode extends Node{
    Token command;
    ArrayList<Token> arguments;
    

    public InstructionNode(Token label, Token command, ArrayList<Token> arguments) {
        super(label);
        this.command = command;
        this.arguments = arguments;
    }

    @Override
    public int getLength() {
        return 2;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(command.toString());
        for(Token arg : arguments){
            sb.append(arg.toString());
        }
        return sb.toString();
    }
    
}
