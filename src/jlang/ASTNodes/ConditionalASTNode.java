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
public class ConditionalASTNode extends ASTNode {
    ComparisonASTNode ifNode;
    CodeBlockASTNode trueNode;
    CodeBlockASTNode falseNode;

    public ConditionalASTNode(ComparisonASTNode ifNode, CodeBlockASTNode trueNode, CodeBlockASTNode falseNode) {
        this.ifNode = ifNode;
        this.trueNode = trueNode;
        this.falseNode = falseNode;
    }
   
    @Override
    public String getNodeType() {
        return "conditional";
    }    
}

