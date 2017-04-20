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
public class ComparisonASTNode extends ASTNode{
    ASTNode leftSide;
    ASTNode rightSide;
    String comparator;

    public ComparisonASTNode(ASTNode leftSide, ASTNode rightSide, String comparator) {
        this.leftSide = leftSide;
        this.rightSide = rightSide;
        this.comparator = comparator;
    }
    
    @Override
    public String getNodeType() {
        return "comparison";
    }
    
}
