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
public class VariableASTNode extends ASTNode {
    String dataType;
    String value;

    public VariableASTNode(String dataType, String value) {
        this.dataType = dataType;
        this.value = value;
    }
    @Override
    public String getNodeType() {
        return "variable";
    }    
}
