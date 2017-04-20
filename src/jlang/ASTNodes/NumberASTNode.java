/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jlang.ASTNodes;

import jlang.ASTNode;

/**
 *
 * @author Jeff
 */
public class NumberASTNode extends ASTNode {
    String value;

    public NumberASTNode(String value) {
        this.value = value;
    }
    
    @Override
    public String getNodeType() {
        return "number";
    }    
}
