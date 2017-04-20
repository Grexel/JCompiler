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
public class ImportASTNode extends ASTNode {
    String fileToImport;

    public ImportASTNode(String fileToImport) {
        this.fileToImport = fileToImport;
    }
    
    @Override
    public String getNodeType() {
        return "import";
    }
    
}
