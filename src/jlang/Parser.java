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
public class Parser {
    public TokenizerStream tokenStream;

    public Parser(TokenizerStream tokenStream) {
        this.tokenStream = tokenStream;
    }

    public Parser(String text) {
        tokenStream = new TokenizerStream(new CharacterInputStream(text));
    }
    
    
}
