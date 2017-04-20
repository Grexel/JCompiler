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
public class BinaryASTNode extends ASTNode{
    String operator;
    ASTNode leftSide;
    ASTNode rightSide;

    public BinaryASTNode(String operator, ASTNode leftSide, ASTNode rightSide) {
        this.operator = operator;
        this.leftSide = leftSide;
        this.rightSide = rightSide;
    }
    
    @Override
    public String getNodeType() {
        return "binary";
    }   
}
