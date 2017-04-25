/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jasm;

import java.io.File;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author Jeff
 */
public class JASM_APP extends Application {
    
    Stage primaryStage;
    JASMPane assemblyPane;
    FileChooser fileChooser;
    CheckMenuItem _8bitCompilerMenu;
    CheckMenuItem _16bitCompilerMenu;
    
    String title;
    boolean newFile;
    @Override
    public void start(Stage primaryStage) {
        
        newFile = true;
        title = "Let's Compile! Jeffrey MMLogic CPU Compiler";
        fileChooser = setupFileChooser();
        assemblyPane = new JASMPane(primaryStage);
        
        
        this.primaryStage = primaryStage;
        VBox root = new VBox(5);
        root.getChildren().add(myMenuBar());
        root.getChildren().add(assemblyPane);
        
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.sizeToScene();
    }
    
    /**
     * Displays a menu for this application.
     *
     * FYI: menu accelerator key codes are listed at:
     * https://docs.oracle.com/javase/8/javafx/api/javafx/scene/input/KeyCode.html
     */
    public MenuBar myMenuBar() {
        MenuBar myBar = new MenuBar();
        final Menu fileMenu = new Menu("File");
        final Menu optionsMenu = new Menu("Options");
        final Menu helpMenu = new Menu("Help");

        myBar.getMenus().addAll(fileMenu, optionsMenu, helpMenu);

        /**
         * *********************************************************************
         * File Menu Section
         */
        MenuItem newCanvas = new MenuItem("New");
        newCanvas.setOnAction((ActionEvent e) -> {
            newFile();
        });
        fileMenu.getItems().add(newCanvas);

        MenuItem open = new MenuItem("Open");
        open.setOnAction((ActionEvent e) -> {
            openFile();
        });
        fileMenu.getItems().add(open);

        MenuItem save = new MenuItem("Save");
        save.setOnAction((ActionEvent e) -> {
            saveFile();
        });
        fileMenu.getItems().add(save);
        
        
        MenuItem saveAs = new MenuItem("Save as");
        saveAs.setOnAction((ActionEvent e) -> {
            saveAsFile();
        });
        fileMenu.getItems().add(saveAs);
        
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(e -> System.exit(0));
        fileMenu.getItems().add(exit);

        /**
         * *********************************************************************
         * Options Menu Section
         */
        _8bitCompilerMenu = new CheckMenuItem("Select 8bit Compiler");
        _8bitCompilerMenu.setOnAction(e -> select8BitCompiler());
        optionsMenu.getItems().add(_8bitCompilerMenu);
        
        _16bitCompilerMenu= new CheckMenuItem("Select 16bit Compiler");
        _16bitCompilerMenu.setOnAction(e -> select16BitCompiler());
        optionsMenu.getItems().add(_16bitCompilerMenu);
        
//        MenuItem compile = new MenuItem("Compile program");
//        compile.setAccelerator(KeyCombination.keyCombination("Ctrl+Shift+C"));
//        compile.setOnAction(e -> compile());
//        optionsMenu.getItems().add(compile);


        
        /**
         * *********************************************************************
         * Help Menu Section
         */
        
        MenuItem opCodes = new MenuItem("Op Codes");
        opCodes.setOnAction(e -> {
            String message = "Operation Codes for Jeff CPU:\n"
                    + "    NOP - does nothing\n"
                    + "    LOD - loads a value\n"
                    + "    MOV - moves a value from one register to another\n"
                    + "    STO - stores a variable\n"
                    + "    OUT - output to a port\n"
                    + "    IN - input from a port\n"
                    + "    ADD - adds values\n"
                    + "    ADC - adds values with a carry\n"
                    + "    SUB - subtracts values\n"
                    + "    SBB - subtracts values with a borrow\n"
                    + "    JMP - jumps to a label\n"
                    + "    PUSH - adds a value to the stack\n"
                    + "    POP - removes a value from the stack\n"
                    + "    HLT - stops the processor\n";
            Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
            alert.setTitle("Op Codes");
            alert.setHeaderText("Details of available opcodes");
            alert.showAndWait();
        });
        helpMenu.getItems().add(opCodes);        
        
        MenuItem about = new MenuItem("About");
        about.setOnAction((ActionEvent e) -> {
            String message = "Written for Proffesor John Phillip's\n"
                    + " Computer Organization class.\n"
                    + " by Jeffrey Miller\n"
                    + " April 7, 2017.\n"
                    + " Targeting a 16 bit computer with 65k ram\n";
            Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
            alert.setTitle("About");
            alert.setHeaderText("MMLogic Assembly compiler v1.0 by Jeffrey Miller");
            alert.showAndWait();
        });
        helpMenu.getItems().add(about);

        return myBar;
    }
    
    /**
     * Initializes a FileChoosers starting directory and allowable file
     * formats.
     * @return FileChooser at the current directory.
     */
    public FileChooser setupFileChooser(){
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(
            new File(System.getProperty("user.dir"))
        ); 
        fc.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Text", "*.txt"),
            new FileChooser.ExtensionFilter("Data", "*.dat"),
            new FileChooser.ExtensionFilter("All", "*.*")
        );  
        return fc;
    }

    public void saveFile(){
        if(newFile){
            File file = fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                assemblyPane.saveAsFile(file);
                newFile = false;
            }
        }
        else{
            assemblyPane.saveFile();
        }
    }
    /**
     * 
     * @return a boolean of whether the user saved to a valid file. 
     */
    public boolean saveAsFile(){
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            assemblyPane.saveAsFile(file);
            newFile = false;
            return true;
        }
        return false;
    }
    public void newFile(){
        assemblyPane.newFile();
        newFile = true;
    }
    public void openFile(){
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            assemblyPane.openFile(file);
            newFile = false;
        }        
    }
    
    public void compile(){
        if(newFile){
            boolean saved = saveAsFile();
            if(saved){
                assemblyPane.compile();
            }
        }
        else{
            assemblyPane.compile();            
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    private void select8BitCompiler() {
        assemblyPane.setCompiler(new Compiler_8bit());
        _16bitCompilerMenu.setSelected(false);
    }

    private void select16BitCompiler() {
        assemblyPane.setCompiler(new Compiler_16bit());
        _8bitCompilerMenu.setSelected(false);   
    }
    
}
