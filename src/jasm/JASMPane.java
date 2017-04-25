/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jasm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Scanner;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Allows the user to sort single dimensional arrays of doubles and save to file
 * @author Jeff
 */
public class JASMPane extends Pane {
    Stage parentStage;
    
    Label lbData;
    Label lbError;
    TextArea taData;
    TextArea taError;
    Compiler compiler;
    File dataFile;

    /**
     * Sets up the Assembler Panel
     * @param parentStage the stage that is creating this Pane. necessary for the FileChooser
     */
    public JASMPane(Stage parentStage) {
        this.parentStage = parentStage;
        compiler = new Compiler_16bit();
        
        File directory = new File(".");
        dataFile = new File(directory.getAbsoluteFile() + File.separator + "New File.txt");
        
        //Instantiate all components
        lbData = new Label("Data");
        lbError = new Label("Compiler Output");
        lbData.setAlignment(Pos.CENTER);
        lbError.setAlignment(Pos.CENTER);
        
        taData = new TextArea();
        taError = new TextArea();
        taError.setEditable(false);
               
        VBox bpData = new VBox(5);
        
        bpData.getChildren().add(lbData);
        bpData.getChildren().add(taData);
        taData.setPrefRowCount(18);
        VBox bpError = new VBox(5);
        bpError.getChildren().add(lbError);
        bpError.getChildren().add(taError);
        taError.setPrefRowCount(8);
        
        
        GridPane bpInformation = new GridPane();
        bpInformation.setPadding(new Insets(10,10,10,10));
        bpInformation.add(bpData, 0, 0,4,6);
        bpInformation.add(bpError, 0, 7,4,2);
        //BorderPane.setMargin(bpData, new Insets(10,10,10,10));
        //BorderPane.setMargin(bpError, new Insets(10,10,10,10));
        this.getChildren().add(bpInformation);
    }

    public void setCompiler(Compiler comp){
        compiler = comp;
    }
    public void newFile(){
        dataFile = new File("temp.txt");
        clearTextAreas();
    }
    public void openFile(File f){
        dataFile = f;
        clearTextAreas();
        try{
            Scanner sc = new Scanner(f);
            while(sc.hasNextLine()){
                taData.appendText(sc.nextLine() + "\n");
            }
            taData.deletePreviousChar();//delete extra newline character
        }catch(FileNotFoundException fnf){
            taError.appendText("Could not open File.\n");
        }
    }
    public void saveAsFile(File f){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(f))){
            Scanner sc = new Scanner(taData.getText());
            while(sc.hasNextLine()){
                bw.append(sc.nextLine() + "\n");
            }
        }catch(Exception e){
            taError.appendText("Writer failed to initialize");
        }
        dataFile = f;
    }
    public void saveFile(){
        saveAsFile(dataFile);
    }
    public void compile() {
        saveFile();
        compiler.compile(dataFile);
        if(compiler.hasErrors()){
            for(String s : compiler.getErrors()){
                taError.appendText(s + "\n");
            }
        }else{
            taError.appendText("Compiled successfully.\n");
            taError.appendText("Hi and Lo byte files saved in: " + dataFile.getParent() + "\n");
        }
    }

    private void clearTextAreas() {
        taData.clear();
        taError.clear();
    }
}
