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
        
        String ch = characterStream.peek();
        //Comment
        if(ch.equalsIgnoreCase("#")){
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
            return new Token("punctuation", characterStream.next());
        }
        if(isOperator(ch)){
            return readOperator();
        }
        characterStream.throwError("Can't handle this character");
        return null;
    }
    public Token readNumber(){
        String number = "";
        String s = characterStream.peek();
        while(isDigit(s)){
            number += characterStream.next();
            if(characterStream.endOfText()) break;
            s = characterStream.peek();
        }
        return new Token("number", number);
    }
    public Token readID(){
        String id = "";
        String s = characterStream.peek();
        while(isIDChar(s)){
            id += characterStream.next();
            if(characterStream.endOfText()) break;
            s = characterStream.peek();
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
        String s = characterStream.peek();
        while(isOperator(s)){
            operate += characterStream.next();
            if(characterStream.endOfText()) break;
            s = characterStream.peek();
        }
        return new Token("operator", operate);
    }
    
    public boolean isKeyword(String s){
        return keywordList.contains(s);
    }
    public boolean isDigit(String s){
        return Character.isDigit(s.charAt(0));
    }
    public boolean isIDStart(String s){
        return Character.isAlphabetic(s.charAt(0));
    }
    public boolean isIDChar(String s){
        char ch = s.charAt(0);
        if(Character.isAlphabetic(ch) || Character.isDigit(ch) ||
                ch == '_'){
            return true;
        }
        return false;
    }
    public boolean isOperator(String s){
        char ch = s.charAt(0);
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
    public boolean isPunctuation(String s){
        char ch = s.charAt(0);
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
    
    public boolean isEmpty(){
        return peek() == null;
    }
    public void skipWhiteSpace(){
        boolean whiteSpace;
        do{
            if(characterStream.endOfText()) break;
            String nextChar = characterStream.peek();
            whiteSpace = Character.isWhitespace(nextChar.charAt(0));
            if(whiteSpace){
                characterStream.next();
            }
        }while(whiteSpace);
    } 
    public void skipComment(){
        int numberOfHashSymbols = 0;
        while(numberOfHashSymbols < 2){
            String s = characterStream.next();
            if(s.equalsIgnoreCase("#"))
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