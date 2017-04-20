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
public class AssignmentASTNode extends ASTNode {
    VariableASTNode variable;
    ASTNode         assignment;

    public AssignmentASTNode(VariableASTNode variable, ASTNode assignment) {
        this.variable = variable;
        this.assignment = assignment;
    }
    
    @Override
    public String getNodeType() {
        return "assignment";
    }    
}
