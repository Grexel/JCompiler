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
public class Token{
    private String type;
    private String value;


    public Token(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Token{" + "type= " + type + ", value= " + value + '}';
    }
}