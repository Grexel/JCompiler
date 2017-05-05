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
public class TokenizerStream {
    Token currentToken;
    ArrayList<String> commandList;
    CharacterInputStream characterStream;
    
    public TokenizerStream(CharacterInputStream characterStream) {
        this.characterStream = characterStream;
        loadDefinitions();
    }
    public Token readNextToken(){
        skipWhiteSpace();
        if(characterStream.isEmpty()){
            return null;
        }
        
        char ch = characterStream.peek();
        //Comment
        if(ch == '#'){
            skipComment();
            return readNextToken();
        }
        if(ch == '['){
            return readVariable();
        }
        if(isDigit(ch)){
            return readNumber();
        }
        if(isIDStart(ch)){
            return readID();
        }
        if(isPunctuation(ch)){
            //skip over, no need for special token
            characterStream.next();
            return readNextToken();
        }
        throwError("Can't handle this character");
        return null;
    }
    //
    //format 9999 for deximal, 0xFFFF for hex
    public Token readNumber(){
        
        String type = "decimal";
        String number = "";
        number += characterStream.next();
        
        //check for 0x
        char ch = characterStream.peek();
        if(ch == 'x'){
            number = "";
            type = "hex";
            characterStream.next(); // get rid of the x to read hex
            ch = characterStream.peek();
        }
        while(isHex(ch)){ // true for decimal and hex
            number += characterStream.next();
            ch = characterStream.peek();
        }
        
        return new Token(type, number);
    }
    public Token readID(){
        String id = "";
        char ch = characterStream.peek();
        while(isIDChar(ch)){
            id += characterStream.next();
            if(characterStream.isEmpty()) break;
            ch = characterStream.peek();
        }
        if(commandList.contains(id)){
            return new Token("command", id);
        }
        else if(id.endsWith(":")){
            return new Token("label", id.substring(0, id.length()-1));            
        }
        else if(isRegister(id)){
            return new Token("register", id.substring(1,2));
        }else{
            return new Token("variable", id); //no brackets for CALL and JMP
        }
    }
    public Token readVariable(){
        characterStream.next(); //clear [ from the stream
        String varName = "";
        char ch = characterStream.peek();
        while(ch != ']'){
            varName += characterStream.next();
            if(characterStream.isEmpty()) break;
            ch = characterStream.peek();
        }
        characterStream.next(); // clear the remaing ]
        return new Token("variable", varName);
    }
    
    public boolean isKeyword(char ch){
        return commandList.contains(ch);
    }
    public boolean isDigit(char ch){
        return Character.isDigit(ch);
    }
    public boolean isHex(char ch){
        if(Character.isDigit(ch)) return true;
        switch(ch){
            case 'a':return true;
            case 'A':return true;
            case 'b':return true;
            case 'B':return true;
            case 'c':return true;
            case 'C':return true;
            case 'd':return true;
            case 'D':return true;
            case 'e':return true;
            case 'E':return true;
            case 'f':return true;
            case 'F':return true;
            default: return false;
        }
    }
    public boolean isIDStart(char ch){
        return Character.isAlphabetic(ch);
    }
    public boolean isIDChar(char ch){
        return Character.isAlphabetic(ch) 
                || Character.isDigit(ch) 
                || ch == '_' 
                || ch == ':';
    }
    public boolean isPunctuation(char ch){
        switch(ch){
            case ',': return true;
            case ';': return true;
            case '.': return true;
            default: return false;
        }
    }
    public boolean isRegister(String id) { //format = r# or R#
      if(id.startsWith("r") || id.startsWith("R")){
          if(isDigit(id.charAt(1))){
              return true;
          }
      } 
      return false;
    }
    
    public boolean hasNext(){
        return !isEmpty();
    }
    public boolean isEmpty(){
        return peek() == null;
    }
    public void skipWhiteSpace(){
        boolean whiteSpace;
        do{
            if(characterStream.isEmpty()) break;
            char nextChar = characterStream.peek();
            whiteSpace = Character.isWhitespace(nextChar) 
                    || isPunctuation(nextChar);
            
            if(whiteSpace){
                characterStream.next();
            }
        }while(whiteSpace);
    }  
    public void skipComment(){
        while(true){
            if(characterStream.isEmpty()) break;
            char s = characterStream.next();
            if(s == '\n') break;
        }
    }
    private void loadDefinitions(){
        commandList = new ArrayList<>();
        commandList.add("LOD");
        commandList.add("MOV");
        commandList.add("STO");
        commandList.add("IN");
        commandList.add("OUT");
        commandList.add("ADD");
        commandList.add("ADC");
        commandList.add("SUB");
        commandList.add("SBB");
        commandList.add("CMP");
        commandList.add("JMP");
        commandList.add("JZ");
        commandList.add("JNZ");
        commandList.add("JC");
        commandList.add("JNC");
        commandList.add("PUSH");
        commandList.add("POP");
        commandList.add("CALL");
        commandList.add("RET");
        commandList.add("HLT");
        commandList.add("VAR");
    }
    
    
    public Token peek() {
        if(currentToken == null){
            currentToken = readNextToken();
        }
        System.out.println("Token.peek = " + currentToken);
        return currentToken;
    }
    public Token next() {
        Token tok = currentToken;
        currentToken = null;
        if(tok == null){
            tok = readNextToken();
            System.out.println(tok);
            return tok;
        }
        else{
            System.out.println(tok);
            return tok;
        }
    }
    public static void main(String[] args) {
        String s = "#I HAVE A COMMENT \n"
                 + "Main:   \n"
                 + "LOD R0 , [var1] \n"
                 + "var1:   VAR 1 \n"
                 + "var2:   VAR 0xFF \n"
                +  "LOD R1 , 0x23";
        TokenizerStream tk = new TokenizerStream(new CharacterInputStream(s));
        while(!tk.isEmpty()){
            System.out.println(tk.next().toString());
        }
    }

    private void throwError(String msg) {
        characterStream.throwError(msg);
    }

}