/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jlang;

/**
 *
 * @author Jeff
 */
public class CharacterInputStream {
    String ch;
    String text;
    int position;
    int lineNumber, columnNumber;
    
    public CharacterInputStream(String text) {
        this.text = text;
        position = 0;
        lineNumber = 1;
        columnNumber = 1;
    }
    public String next(){
        ch = text.substring(position, position+1);
        if(ch.equalsIgnoreCase("\n")){
            lineNumber++;
            columnNumber = 1;
        }else{
            columnNumber++;
        }
        position++;
        return ch;
    }
    public String peek(){
        if(position != text.length())
            return text.substring(position, position+1);
        else
            return null;
    }
    public boolean endOfText(){
        return position == text.length();
    }
    public void throwError(String msg){
        System.out.println(msg + "Line:"+lineNumber + " Col: " + columnNumber);
    }
    
}
