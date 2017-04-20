/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jlang;

import java.util.ArrayList;

/**
 *
 * @author Jeff
 */
public class TokenizerStream {
    Token currentToken;
    ArrayList<String> keywordList;
    CharacterInputStream characterStream;
    
    public TokenizerStream(CharacterInputStream characterStream) {
        this.characterStream = characterStream;
        loadDefinitions();
    }
    public Token readNextToken(){
        skipWhiteSpace();
        if(characterStream.endOfText()){
            return null;
        }
        
        char ch = characterStream.peek();
        //Comment
        if(ch == '#'){
            skipComment();
            return readNextToken();
        }
        if(isDigit(ch)){
            return readNumber();
        }
        if(isIDStart(ch)){
            return readID();
        }
        if(isPunctuation(ch)){
            return new Token("punctuation", "" + characterStream.next());
        }
        if(isOperator(ch)){
            return readOperator();
        }
        characterStream.throwError("Can't handle this character");
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
        while(isHex(ch) && characterStream.hasNext()){ // true for decimal and hex
            number += characterStream.next();
            ch = characterStream.peek();
        }
        
        return new Token(type, number);
    }
    public Token readID(){
        String id = "";
        char ch = characterStream.peek();
        while(isIDChar(ch) && characterStream.hasNext()){
            id += characterStream.next();
            ch = characterStream.peek();
        }
        if(keywordList.contains(id)){
            return new Token("keyword", id);
        }
        else{
            return new Token("variable", id);            
        }
    }
    public Token readOperator(){
        String operate = "";
        char ch = characterStream.peek();
        while(isOperator(ch) && characterStream.hasNext()){
            operate += characterStream.next();
            ch = characterStream.peek();
        }
        return new Token("operator", operate);
    }
    
    public boolean isKeyword(String s){
        return keywordList.contains(s);
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
        if(Character.isAlphabetic(ch) || Character.isDigit(ch) ||
                ch == '_'){
            return true;
        }
        return false;
    }
    public boolean isOperator(char ch){
        switch(ch){
            case '+': return true;
            case '-': return true;
            case '*': return true;
            case '/': return true;
            case '=': return true;
            case '>': return true;
            case '<': return true;
            case '&': return true;
            case '|': return true;
            case '!': return true;
            default: return false;
        }
    }  
    public boolean isPunctuation(char ch){
        switch(ch){
            case ',': return true;
            case ';': return true;
            case '(': return true;
            case ')': return true;
            case '{': return true;
            case '}': return true;
            default: return false;
        }
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
            char nextChar = characterStream.peek();
            whiteSpace = Character.isWhitespace(nextChar);
            if(whiteSpace){
                characterStream.next();
            }
        }while(whiteSpace && characterStream.hasNext());
    } 
    public void skipComment(){
        //did not dispose of the first #, so take 2.
        //comment is #comment goes here #
        int numberOfHashSymbols = 0;
        while(numberOfHashSymbols < 2 && characterStream.hasNext()){
            char ch = characterStream.next();
            if(ch == '#')
                numberOfHashSymbols++;
        }
    }
    private void loadDefinitions(){
        keywordList = new ArrayList<>();
        keywordList.add("function");
        keywordList.add("if");
        keywordList.add("else");
        keywordList.add("while");
        keywordList.add("void");
        keywordList.add("short");
        keywordList.add("int");
        keywordList.add("long");
        keywordList.add("bool");
        keywordList.add("char");
        keywordList.add("import");
    }
    
    
    public Token peek() {
        if(currentToken == null){
            currentToken = readNextToken();
        }
        return currentToken;
    }
    public Token next() {
        Token tok = currentToken;
        currentToken = null;
        if(tok == null){
            return readNextToken();
        }
        else{
            return tok;
        }
    }
    public static void main(String[] args) {
        String s = " function {void  \n morse(short poop, long right) * >= )}";
        TokenizerStream tk = new TokenizerStream(new CharacterInputStream(s));
        while(!tk.isEmpty()){
            System.out.println(tk.next().toString());
        }
    }
}