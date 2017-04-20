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
public class FunctionASTNode extends ASTNode {
    String functionName;
    String returnType;
    ArrayList<ASTNode> arguments;
    CodeBlockASTNode    code;

    public FunctionASTNode(String functionName, String returnType, ArrayList<ASTNode> arguments, CodeBlockASTNode code) {
        this.functionName = functionName;
        this.returnType = returnType;
        this.arguments = arguments;
        this.code = code;
    }
    
    @Override
    public String getNodeType() {
        return "function";
    }
    
}
