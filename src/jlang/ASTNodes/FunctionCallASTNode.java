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
public class FunctionCallASTNode extends ASTNode {
    String functionName;
    ArrayList<ASTNode> arguments;

    public FunctionCallASTNode(String functionName, ArrayList<ASTNode> arguments) {
        this.functionName = functionName;
        this.arguments = arguments;
    }

    @Override
    public String getNodeType() {
        return "call";
    }    
}
