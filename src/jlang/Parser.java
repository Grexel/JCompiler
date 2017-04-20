/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jlang;

import java.util.ArrayList;
import jlang.ASTNodes.CodeBlockASTNode;
import jlang.ASTNodes.FunctionASTNode;
import jlang.ASTNodes.VariableASTNode;

/**
 *
 * @author Jeff
 */
public class Parser {
    public TokenizerStream tokenStream;
    public ArrayList<ASTNode> nodes;
    public Parser(TokenizerStream tokenStream) {
        this.tokenStream = tokenStream;
    }

    public Parser(String text) {
        tokenStream = new TokenizerStream(new CharacterInputStream(text));
    }
    
    public ArrayList<ASTNode> parse(){
        nodes.clear();
        while(tokenStream.hasNext()){
            nodes.add(parseNext());
        }
        return nodes;
    }
    public ASTNode parseNext(){
        Token peekToken = tokenStream.peek();
        if(isKeyword(peekToken)){
            if(isFunction(peekToken)){
               return parseFunction();
            }
        }
        return null;
    }
    public ASTNode parseFunction(){
        String name = "Noname";
        String returnType = "Notype";
        
        //get type, get name, get parameters, get codeblock
        Token tk = tokenStream.next(); // keyword, function
        
        tk = tokenStream.next(); //return type;
        returnType = tk.getValue();
        
        tk = tokenStream.next(); // method name
        name = tk.getValue();
        ArrayList<ASTNode> args = parseArguments();
        CodeBlockASTNode block = parseCodeBlock();
        return new FunctionASTNode(name,returnType,args,block);
    }
    
    private CodeBlockASTNode parseCodeBlock() {
        Token tk = tokenStream.next(); // { is in stream
        ArrayList<ASTNode> sequence = new ArrayList<>();
        if(isOpenBrace(tk)){
            while(!isCloseBrace(tk)){
                sequence.add(parseNext());
                tk = tokenStream.peek();
            }
            return new CodeBlockASTNode(sequence);
        }
        return null;
    }
    //parsing a functions arguments
    public ArrayList<ASTNode> parseArguments(){
        //get the (
        Token tk = tokenStream.next();
        if(tk.getValue().equalsIgnoreCase("(")){
            ArrayList<ASTNode> arguments = new ArrayList<>();
            //get the data type e.g short varName1
            tk = tokenStream.peek();
            while(!tk.getValue().equalsIgnoreCase(")")){
                if(isKeyword(tk)){
                    //data type
                    tk = tokenStream.next();
                    String varType = tk.getValue();
                    
                    //data name
                    tk = tokenStream.next();
                    String varName = tk.getValue();
                    
                    //data type, data value
                    VariableASTNode var = new VariableASTNode(varType, varName);
                    arguments.add(var);
                    //comma or )
                    tk = tokenStream.next();
                    if(isComma(tk)){
                        //clear comma, look at next token
                        tk = tokenStream.peek();
                    }
                }
            }
            return arguments;
        }
        else{
            //throw error
        }
        return null;
    }
    private boolean isKeyword(Token tk){
        return tk.getType().equalsIgnoreCase("keyword");
    }
    private boolean isFunction(Token tk) {
        return tk.getValue().equalsIgnoreCase("function");
    }
    private boolean isComma(Token tk) {
        return tk.getValue().equalsIgnoreCase(",");
    }
    private boolean isOpenBrace(Token tk) {
        return tk.getValue().equalsIgnoreCase("{");
    }
    private boolean isCloseBrace(Token tk) {
        return tk.getValue().equalsIgnoreCase("}");
    }

    
    
}
