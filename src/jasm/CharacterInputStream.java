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
public class CharacterInputStream {
    char ch;
    String text;
    int position;
    int lineNumber, columnNumber;
    
    public CharacterInputStream(String text) {
        this.text = text;
        position = 0;
        lineNumber = 1;
        columnNumber = 1;
    }
    public char next(){
        ch = text.substring(position, position+1).charAt(0);
        if(ch == '\n'){
            lineNumber++;
            columnNumber = 1;
        }else{
            columnNumber++;
        }
        position++;
        return ch;
    }
    public char peek(){
        if(position != text.length())
            return text.substring(position, position+1).charAt(0);
        else
            return 0;
    }
    public boolean endOfText(){
        return position == text.length();
    }
    public void throwError(String msg){
        System.out.println(msg + "Line:"+lineNumber + " Col: " + columnNumber);
    }
    
}
