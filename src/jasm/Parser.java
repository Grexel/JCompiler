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
public class Parser {
    TokenizerStream tkStream;
    ArrayList<Node> nodes;
    public Parser(TokenizerStream tkStream) {
        this.tkStream = tkStream;
        nodes = new ArrayList<>();
    }
    
    public Parser(String textFile){
        this.tkStream = new TokenizerStream(new CharacterInputStream(textFile));
        nodes = new ArrayList<>();
    }
    
    public ArrayList<Node> parse(){
        nodes.clear();
        while(tkStream.hasNext()){
            Token currentToken = tkStream.peek();
            if(isLabel(currentToken)){
                Token label = tkStream.next();
                
                if(tkStream.hasNext()){
                    Token nextToken = tkStream.peek();
                    if(isVariable(nextToken)){
                        System.out.println("labeled VAR command");
                        nodes.add(readVariable(label));
                    }
                    else if(isCommand(nextToken)){
                        System.out.println("labeled command");
                        nodes.add(readCommand(label));
                    }
                    //maybe variable, maybe command
                }
            }
            else if(isCommand(currentToken)){
                nodes.add(readCommand());
            }
            else{
                //Error
            }
        }
        return nodes;
    }
    public Node readCommand(){
        return readCommand(null);
    }
    public Node readCommand(Token label){
        Token command = tkStream.next();
        ArrayList<Token> arguments = new ArrayList<>();
        
        while(tkStream.hasNext()){
            Token peekToken = tkStream.peek();
            if(!isLabel(peekToken) && !isCommand(peekToken)){
                arguments.add(tkStream.next());
            }
            else
                break;
        }
        
        InstructionNode instructNode= new InstructionNode(label,command, arguments);
        return (Node) instructNode;
    }
    private Node readVariable(Token label) {
        //VAR is token in tkStream, throw it to get the value;
        tkStream.next();
        Token val = tkStream.next();
        VariableNode varNode = new VariableNode(label,val);
        return (Node) varNode;
    }
        
    public boolean isLabel(Token tk){
        return tk.getType().equalsIgnoreCase("label");
    }
    public boolean isVariable(Token tk){
        return tk.getType().equalsIgnoreCase("command")
                &&  tk.getValue().equalsIgnoreCase("VAR");
    }
    public boolean isCommand(Token tk){
        return tk.getType().equalsIgnoreCase("command");        
    }

}
