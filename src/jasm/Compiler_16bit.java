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
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jeff
 */
public class Compiler_16bit extends Compiler{
    ArrayList<String> compiledInstructions;
    ArrayList<String> errors;
    
    Parser parser;
    ArrayList<Node> nodes;
    
    File textFile;

    public Compiler_16bit() {
        compiledInstructions = new ArrayList<>();
        errors = new ArrayList<>();
    }
    
    public void compile(File file){
        compiledInstructions.clear();
        errors.clear();
        
        this.textFile = file;
        compile(loadTextFromFile(textFile));
    }
    public void compile(String text){
        compiledInstructions.clear();
        errors.clear();
        
        parser = new Parser(text);
        nodes = parser.parse();
        
        setAddresses(nodes);
        compileInstructions(nodes);
        
        //break into high and low.
        if(errors.isEmpty()){
            saveCompiled(compiledInstructions);
        }
    }

    private void setAddresses(ArrayList<Node> nodes) {
        //set address for each node. 
        int address = 0;
        for(Node n : nodes){
            n.address = address;
            address += n.getLength();
        }
        //turn 
    }
    private void compileInstructions(ArrayList<Node> nodes) {
        for(Node n : nodes){
            if(n instanceof VariableNode){
                VariableNode var = (VariableNode)n;
                if(var.value.getType().equalsIgnoreCase("hex")){
                    compiledInstructions.add(bufferSizeHex(var.value.getValue()));
                }
                else if(var.value.getType().equalsIgnoreCase("decimal")){
                    compiledInstructions.add(decimalToHex(var.value.getValue()));
                }
            }
            else if(n instanceof InstructionNode){
                InstructionNode instruct = (InstructionNode)n;
                String command = instruct.command.getValue();
                if(command.equalsIgnoreCase("LOD")){
                    compileLOD(instruct);
                }
                if(command.equalsIgnoreCase("MOV")){
                    compileMOV(instruct);
                }
                if(command.equalsIgnoreCase("STO")){
                    compileSTO(instruct);
                }
                if(command.equalsIgnoreCase("OUT")){
                    compileOUT(instruct);
                }
                if(command.equalsIgnoreCase("IN")){
                    compileIN(instruct);
                }
                if(command.equalsIgnoreCase("ADD")){
                    compileADD(instruct);
                }
                if(command.equalsIgnoreCase("ADC")){
                    compileADC(instruct);
                }
                if(command.equalsIgnoreCase("SUB")){
                    compileSUB(instruct);
                }
                if(command.equalsIgnoreCase("SBB")){
                    compileSBB(instruct);
                }
                if(command.equalsIgnoreCase("JMP")){
                    compileJMP(instruct, 0);
                }
                if(command.equalsIgnoreCase("JZ")){
                    compileJMP(instruct, 1);
                }
                if(command.equalsIgnoreCase("JNZ")){
                    compileJMP(instruct, 2);
                }
                if(command.equalsIgnoreCase("JC")){
                    compileJMP(instruct, 3);
                }
                if(command.equalsIgnoreCase("JNC")){
                    compileJMP(instruct, 4);
                }
                if(command.equalsIgnoreCase("PUSH")){
                    compilePUSH(instruct);
                }
                if(command.equalsIgnoreCase("POP")){
                    compilePOP(instruct);
                }
                if(command.equalsIgnoreCase("CALL")){
                    compileCALL(instruct);
                }
                if(command.equalsIgnoreCase("RET")){
                    compileRET(instruct);
                }
                if(command.equalsIgnoreCase("HLT")){
                    compileHLT(instruct);
                }
            }
        }
    }
    private void compileLOD(InstructionNode instruct){
        
        Token register = instruct.arguments.get(0);
        Token value = instruct.arguments.get(1);
        
        //Instruction
        String HHNibble = "1";
        String HLNibble = register.getValue();
        String LHNibble = "0";
        String LLNibble = "0";
        String address = "FFFF"; //can be an address or a constant
        
        //value
        if(value.getType().equalsIgnoreCase("hex")){
            LLNibble = "8"; //8 is for constant
            address = bufferSizeHex(value.getValue());
        }
        if(value.getType().equalsIgnoreCase("decimal")){
            LLNibble = "8"; //8 is for constant
            address = decimalToHex(value.getValue());
        }
        if(value.getType().equalsIgnoreCase("variable")){
            address = searchLabelAddress(value.getValue());
        }
        compiledInstructions.add(HHNibble + HLNibble + LHNibble + LLNibble);
        compiledInstructions.add(address);
    }
    private void compileMOV(InstructionNode instruct){
        
        Token registerInto = instruct.arguments.get(0);
        Token registerFrom = instruct.arguments.get(1);
        
        //Instruction
        String HHNibble = "1";
        String HLNibble = registerInto.getValue();
        String LHNibble = addToHex(registerFrom.getValue(),"8");
        String LLNibble = "0";
        String address = "FFFF"; 
        
        compiledInstructions.add(HHNibble + HLNibble + LHNibble + LLNibble);
        compiledInstructions.add(address);
    }
    private void compileSTO(InstructionNode instruct){
        Token value = instruct.arguments.get(0);
        Token register = instruct.arguments.get(1);
        
        //Instruction
        String HHNibble = "2";
        String HLNibble = register.getValue();
        String LHNibble = "0";
        String LLNibble = "0";
        String address = "FFFF"; //can be an address or a constant
        
        if(value.getType().equalsIgnoreCase("variable")){
            address = searchLabelAddress(value.getValue());
        }
        compiledInstructions.add(HHNibble + HLNibble + LHNibble + LLNibble);
        compiledInstructions.add(address);
    }  
    private void compileIN(InstructionNode instruct){
        
        Token port = instruct.arguments.get(0); //decimal
        Token destination = instruct.arguments.get(1);
        
        //Instruction
        String HHNibble = "4";
        String HLNibble = destination.getValue();
        String LHNibble = "0";
        String LLNibble = port.getValue();
        String address = "FFFF"; //can be an address or a constant
        
        //variable not register
        if(destination.getType().equalsIgnoreCase("variable")){
            HLNibble = "8"; // save to variable
            address = searchLabelAddress(destination.getValue());
        }
        compiledInstructions.add(HHNibble + HLNibble + LHNibble + LLNibble);
        compiledInstructions.add(address);
    }
    private void compileOUT(InstructionNode instruct){
        
        Token port = instruct.arguments.get(0); //decimal
        Token src = instruct.arguments.get(1);
        
        //Instruction
        String HHNibble = "3";
        String HLNibble = src.getValue();
        String LHNibble = "0";
        String LLNibble = port.getValue();
        String address = "FFFF"; //can be an address or a constant
        
        //constant not register
        if(src.getType().equalsIgnoreCase("hex")){
            HLNibble = "8"; //8 is for constant
            address = bufferSizeHex(src.getValue());
        }
        if(src.getType().equalsIgnoreCase("decimal")){
            HLNibble = "8"; //8 is for constant
            address = decimalToHex(src.getValue());
        }
        compiledInstructions.add(HHNibble + HLNibble + LHNibble + LLNibble);
        compiledInstructions.add(address);
    }
    private void compileADD(InstructionNode instruct){
        
        Token registerToSave = instruct.arguments.get(0); //decimal
        Token addend = instruct.arguments.get(1);
        
        //Instruction
        String HHNibble = "5";
        String HLNibble = registerToSave.getValue();
        String LHNibble = "0";
        String LLNibble = "0"; // 1 for ADC
        String address = "FFFF"; //can be an address or a constant
        
        //constant not register
        if(addend.getType().equalsIgnoreCase("register")){
            LHNibble = addToHex(addend.getValue(), "8"); 
            address = "FFFF"; //adding registers together, no need for this
        }
        if(addend.getType().equalsIgnoreCase("variable")){
            LHNibble = "0"; //8 is for constant
            address = searchLabelAddress(addend.getValue());
        }
        if(addend.getType().equalsIgnoreCase("hex")){
            LLNibble = "8"; //8 is for constant
            address = bufferSizeHex(addend.getValue());
        }
        if(addend.getType().equalsIgnoreCase("decimal")){
            LLNibble = "8"; //8 is for constant
            address = decimalToHex(addend.getValue());
        }
        compiledInstructions.add(HHNibble + HLNibble + LHNibble + LLNibble);
        compiledInstructions.add(address);
    }
    private void compileADC(InstructionNode instruct){
        
        Token registerToSave = instruct.arguments.get(0); //decimal
        Token addend = instruct.arguments.get(1);
        
        //Instruction
        String HHNibble = "5";
        String HLNibble = registerToSave.getValue();
        String LHNibble = "0";
        String LLNibble = "1"; // 1 for ADC
        String address = "FFFF"; //can be an address or a constant
        
        //constant not register
        if(addend.getType().equalsIgnoreCase("register")){
            LHNibble = addToHex(addend.getValue(), "8"); 
            address = "FFFF"; //adding registers together, no need for this
        }
        if(addend.getType().equalsIgnoreCase("variable")){
            LHNibble = "0"; //8 is for constant
            address = searchLabelAddress(addend.getValue());
        }
        if(addend.getType().equalsIgnoreCase("hex")){
            LLNibble = "8"; //8 is for constant
            address = bufferSizeHex(addend.getValue());
        }
        if(addend.getType().equalsIgnoreCase("decimal")){
            LLNibble = "8"; //8 is for constant
            address = decimalToHex(addend.getValue());
        }
        compiledInstructions.add(HHNibble + HLNibble + LHNibble + LLNibble);
        compiledInstructions.add(address);
    }
    private void compileSUB(InstructionNode instruct){
        
        Token registerToSave = instruct.arguments.get(0); //decimal
        Token addend = instruct.arguments.get(1);
        
        //Instruction
        String HHNibble = "6";
        String HLNibble = registerToSave.getValue();
        String LHNibble = "0";
        String LLNibble = "0"; // 1 for ADC
        String address = "FFFF"; //can be an address or a constant
        
        //constant not register
        if(addend.getType().equalsIgnoreCase("register")){
            LHNibble = addToHex(addend.getValue(), "8"); 
            address = "FFFF"; //adding registers together, no need for this
        }
        if(addend.getType().equalsIgnoreCase("variable")){
            LHNibble = "0"; //8 is for constant
            address = searchLabelAddress(addend.getValue());
        }
        if(addend.getType().equalsIgnoreCase("hex")){
            LLNibble = "8"; //8 is for constant
            address = bufferSizeHex(addend.getValue());
        }
        if(addend.getType().equalsIgnoreCase("decimal")){
            LLNibble = "8"; //8 is for constant
            address = decimalToHex(addend.getValue());
        }
        compiledInstructions.add(HHNibble + HLNibble + LHNibble + LLNibble);
        compiledInstructions.add(address);
    }
    private void compileSBB(InstructionNode instruct){
        
        Token registerToSave = instruct.arguments.get(0); //decimal
        Token addend = instruct.arguments.get(1);
        
        //Instruction
        String HHNibble = "6";
        String HLNibble = registerToSave.getValue();
        String LHNibble = "0";
        String LLNibble = "1"; // 1 for ADC
        String address = "FFFF"; //can be an address or a constant
        
        //constant not register
        if(addend.getType().equalsIgnoreCase("register")){
            LHNibble = addToHex(addend.getValue(), "8"); 
            address = "FFFF"; //adding registers together, no need for this
        }
        if(addend.getType().equalsIgnoreCase("variable")){
            LHNibble = "0"; //8 is for constant
            address = searchLabelAddress(addend.getValue());
        }
        if(addend.getType().equalsIgnoreCase("hex")){
            LLNibble = "8"; //8 is for constant
            address = bufferSizeHex(addend.getValue());
        }
        if(addend.getType().equalsIgnoreCase("decimal")){
            LLNibble = "8"; //8 is for constant
            address = decimalToHex(addend.getValue());
        }
        compiledInstructions.add(HHNibble + HLNibble + LHNibble + LLNibble);
        compiledInstructions.add(address);
    }
    private void compileJMP(InstructionNode instruct, int type){
                
        Token destination = instruct.arguments.get(0); //label
        
        //Instruction
        String HHNibble = "7";
        String HLNibble = "0";
        String LHNibble = toHex(type);
        String LLNibble = "0";
        String address = "FFFF"; //label to jump to
        
        //destination
        if(destination.getType().equalsIgnoreCase("variable")){
            address = searchLabelAddress(destination.getValue());
        }
        compiledInstructions.add(HHNibble + HLNibble + LHNibble + LLNibble);
        compiledInstructions.add(address);
    }
    private void compilePUSH(InstructionNode instruct){
                
        Token src = instruct.arguments.get(0); //register to push
        
        //Instruction
        String HHNibble = "8";
        String HLNibble = src.getValue();
        String LHNibble = "0";
        String LLNibble = "0";
        String address = "FFFF"; //label to jump to
        
        compiledInstructions.add(HHNibble + HLNibble + LHNibble + LLNibble);
        compiledInstructions.add(address);
    }
    private void compilePOP(InstructionNode instruct){
                
        Token dest = instruct.arguments.get(0); //register to pop to
        
        //Instruction
        String HHNibble = "9";
        String HLNibble = dest.getValue();
        String LHNibble = "0";
        String LLNibble = "0";
        String address = "FFFF"; //unnecessary
        
        compiledInstructions.add(HHNibble + HLNibble + LHNibble + LLNibble);
        compiledInstructions.add(address);
    }
    private void compileCALL(InstructionNode instruct){
                
        Token destination = instruct.arguments.get(0); //label to jump to
        
        //Instruction
        String HHNibble = "8";
        String HLNibble = "0";
        String LHNibble = "0";
        String LLNibble = "1";
        String address = "FFFF"; //label to jump to
        
        //destination
        if(destination.getType().equalsIgnoreCase("variable")){
            address = searchLabelAddress(destination.getValue());
        }
        
        compiledInstructions.add(HHNibble + HLNibble + LHNibble + LLNibble);
        compiledInstructions.add(address);
    }
    private void compileRET(InstructionNode instruct){
        
        //Instruction
        String HHNibble = "9";
        String HLNibble = "0";
        String LHNibble = "0";
        String LLNibble = "1";
        String address = "FFFF"; //
        
        compiledInstructions.add(HHNibble + HLNibble + LHNibble + LLNibble);
        compiledInstructions.add(address);
    }
    private void compileHLT(InstructionNode instruct){
        compiledInstructions.add("FFFF");
        compiledInstructions.add("FFFF");
    }
    
    private String searchLabelAddress(String value) {
        
        for(Node n : nodes){
            
            if(n.hasLabel() && n.label.getValue().equalsIgnoreCase(value)){
                return decimalToHex(n.address);
            }
        }
        return "0000";
    }
    private String decimalToHex(int i){
        int accumulator = 0;
        int highhigh = i / 4096;  //first char = i / 4096;
        accumulator += highhigh * 4096;
        int highlow = (i - accumulator) / 256; //second char = i / 256;
        accumulator += highlow * 256;
        int lowhigh = (i - accumulator) / 16; //third char = i / 16;
        accumulator += lowhigh * 16;
        int lowlow = i - accumulator;
        
        String address = toHex(highhigh) + toHex(highlow)
                + toHex(lowhigh) + toHex(lowlow);
        
        return bufferSizeHex(address);
    }
    private String decimalToHex(String s){
        return decimalToHex(Integer.parseInt(s));
    }  
    private String toHex(int i){
        switch (i) {
            case 0: return "0";
            case 1: return "1";
            case 2: return "2";
            case 3: return "3";
            case 4: return "4";
            case 5: return "5";
            case 6: return "6";
            case 7: return "7";
            case 8: return "8";
            case 9: return "9";
            case 10: return "A";
            case 11: return "B";
            case 12: return "C";
            case 13: return "D";
            case 14: return "E";
            case 15: return "F";
            default: return "0";
        }
        
    }
    private String bufferSizeHex(String s) {
        if (s.length() == 4) {
            return s;
        }
        if (s.length() > 4) {
            return s.substring(0, 4);
        } else {
            while (s.length() < 4) {
                s = "0" + s;
            }
            return s;
        }
    }
    private String addToHex(String n1, String n2){
        int num1 = Integer.parseInt(n1);
        int num2 = Integer.parseInt(n2);
        return toHex(num1+num2);
    }
    public static void main(String[] args) {
       // String text = "#Stufff \n"
        //        + "LOD: r0, hFF";
        String st = "#I HAVE A COMMENT \n"
                 + "   \n"
                 + "LOD R0, 0xFFFF \n"
                 + "var1:   VAR 12 \n"
                 + "var2:   VAR 0xFE \n"
                 + "LOD R1, [var1] \n"
                 + "ADD R0, 0xFFF \n"
                 + "CALL Mult \n"
                 + "Mult: OUT 0, 0x45 \n"
                 + "    OUT 0, 0x46 \n"
                 + "     OUT 0, 0x47 \n"
                 +  "RET \n"
                 + "";
        Compiler_16bit comp = new Compiler_16bit();
        comp.compile(st);
        for(Node s : comp.parser.nodes){
            System.out.println(s);
        }
        for(String s : comp.compiledInstructions){
            System.out.println(s);
        }
    }

    public boolean hasErrors(){
        return !errors.isEmpty();
    }
    public ArrayList<String> getErrors(){
        return errors;
    }
    private String loadTextFromFile(File textFile) {
        StringBuilder sb = new StringBuilder();
        try{
            Scanner sc = new Scanner(textFile);
            while(sc.hasNextLine()){
                sb.append(sc.nextLine());
                sb.append("\n");
            }
            return sb.toString();
        }catch(FileNotFoundException fnf){
            errors.add("Could not open file" + textFile.getName());
        }
        return "";
    }
    private void saveCompiled(ArrayList<String> compiledInstructions) {
        File hiByte;
        File loByte;
        
        if(textFile != null){
            String hiByteString = textFile.getParent() + File.separator
                    + textFile.getName().substring(0, textFile.getName().length() - 4)
                    + "Hi.txt";
            String loByteString = textFile.getParent() + File.separator
                    + textFile.getName().substring(0, textFile.getName().length() - 4)
                    + "Lo.txt";
            hiByte = new File(hiByteString);
            loByte = new File(loByteString);
        }
        else{
            hiByte = new File("tempHi.txt");
            loByte = new File("tempLo.txt");
        }
        
        byte[] encoded;
        try (BufferedWriter bwHi = new BufferedWriter(new FileWriter(hiByte));
                BufferedWriter bwLo = new BufferedWriter(new FileWriter(loByte))) {
            for(String bytecode : compiledInstructions){
                encoded = bytecode.substring(0,2).getBytes(StandardCharsets.US_ASCII);
                bwHi.append(new String(encoded,StandardCharsets.US_ASCII)).append("\n");
                
                encoded = bytecode.substring(2).getBytes(StandardCharsets.US_ASCII);
                bwLo.append(new String(encoded,StandardCharsets.US_ASCII)).append("\n");
            }
        } catch (IOException ex) {
        Logger.getLogger(Compiler_16bit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
