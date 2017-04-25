/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jasm;

import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author Jeff
 */
public abstract class Compiler {
    
    public abstract void compile(File file);
    public abstract void compile(String text);

    public abstract boolean hasErrors();
    public abstract ArrayList<String> getErrors();
}
