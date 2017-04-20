/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jlang.ASTNodes;

import java.util.ArrayList;
import jlang.ASTNode;

/**
 *
 * @author Jeff
 */
public class CodeBlockASTNode extends ASTNode{
    ArrayList<ASTNode> sequence;

    public CodeBlockASTNode(ArrayList<ASTNode> sequence) {
        this.sequence = sequence;
    }
    
    @Override
    public String getNodeType() {
        return "codeBlock";
    }
}
