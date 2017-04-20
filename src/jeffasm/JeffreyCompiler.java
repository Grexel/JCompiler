/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeffasm;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 *
 * @author Jeff
 */
public class JeffreyCompiler {

    final int maxRecords = 65536;

    // holds the original source code with blank lines and comment lines stripped
    ArrayList<String> source = new ArrayList<>();

    // holds source with remaining comments stripped
    ArrayList<String> sourceStripped = new ArrayList<>();

    // holds the final machine code
    ArrayList<String> mcHi = new ArrayList<>();
    ArrayList<String> mcLo = new ArrayList<>();

    //Holds list of commands;
    ArrayList<ASSCommand> commands = new ArrayList<>();
    ArrayList<String> errorList = new ArrayList<>();

    File sourceFile;

    public JeffreyCompiler() {
        setErrorList();
    }

    public void compile(File f) {
        sourceFile = f;
        //clear arrays
        source.clear();
        sourceStripped.clear();
        mcHi.clear();
        mcLo.clear();
        commands.clear();
        errorList.clear();

        loadData(f);
        stripData();
        parseLines();
        dereference();
        output();
    }

    private void loadData(File f) {
        try {
            Scanner sc = new Scanner(f);
            while (sc.hasNextLine()) {
                source.add(sc.nextLine().toUpperCase());
            }
        } catch (FileNotFoundException fnf) {
            addError("Source file not found.");
        }
    }

    private void stripData() {

        for (String line : source) {
            System.out.println(line);
            if (line.matches("#.*") || line.matches("\\s*")) {;
            } else {
                sourceStripped.add(line);
            }
        }
    }

    // parse each stripped line	into a label, command, and operands
    private void parseLines() {
        for (String line : sourceStripped) {
            commands.add(new ASSCommand(line));
        }
    }

    private void dereference() {
        int address = 0;
        for (ASSCommand cmd : commands) {
            System.out.println(cmd.line + " address: " + address);
            cmd.setAddress(address);
            address += cmd.getSize();
        }//have addresses for every label now.

        //go through operations and see if they need dereferencing
        for (ASSCommand cmd : commands) {
            for (ASSOperation op : cmd.operations) {
                if (op.dereference) {
                    //operation needs dereferencing, look through all commands
                    //for appropriate label
                    for (ASSCommand cmdLabel : commands) {
                        if (op.address.equalsIgnoreCase(cmdLabel.label)) {
                            op.address = cmdLabel.getAddress();
                        }
                    }
                }
            }
        }
        //dereferencing done
    }

    private void output() {
        //only save compiled program if there are no errors
        if(!hasErrors()){
            //File writer for hi, filewriter for lo
            String hiByteString = sourceFile.getParent() + File.separator
                    + sourceFile.getName().substring(0, sourceFile.getName().length() - 4)
                    + "Hi.txt";
            String loByteString = sourceFile.getParent() + File.separator
                    + sourceFile.getName().substring(0, sourceFile.getName().length() - 4)
                    + "Lo.txt";
            File hiByte = new File(hiByteString);
            File loByte = new File(loByteString);

            //necessary for encoding to the ascii format MMLogic requires
            String outString;
            byte[] encoded;

            try (BufferedWriter bwHi = new BufferedWriter(new FileWriter(hiByte));
                    BufferedWriter bwLo = new BufferedWriter(new FileWriter(loByte))) {
                for (ASSCommand cmd : commands) {
                    for (ASSOperation op : cmd.operations) {
                        if (op.addressOnly) {
                            outString = op.address.substring(0, 2) + "\n";
                            encoded = outString.getBytes(StandardCharsets.US_ASCII);
                            bwHi.append(new String(encoded,StandardCharsets.US_ASCII));

                            outString = op.address.substring(2) + "\n";
                            encoded = outString.getBytes(StandardCharsets.US_ASCII);
                            bwLo.append(new String(encoded,StandardCharsets.US_ASCII));
                        } else {
                            //Instruction
                            outString = op.instruction.substring(0, 2) + "\n";
                            encoded = outString.getBytes(StandardCharsets.US_ASCII);
                            bwHi.append(new String(encoded,StandardCharsets.US_ASCII));

                            outString = op.instruction.substring(2) + "\n";
                            encoded = outString.getBytes(StandardCharsets.US_ASCII);
                            bwLo.append(new String(encoded,StandardCharsets.US_ASCII));
                            //Address
                            outString = op.address.substring(0, 2) + "\n";
                            encoded = outString.getBytes(StandardCharsets.US_ASCII);
                            bwHi.append(new String(encoded,StandardCharsets.US_ASCII));

                            outString = op.address.substring(2) + "\n";
                            encoded = outString.getBytes(StandardCharsets.US_ASCII);
                            bwLo.append(new String(encoded,StandardCharsets.US_ASCII));
                        }
                    }
                }
            } catch (IOException ioe) {
                addError("Problem saving files:\n");
                addError("HiByte: " + hiByte.getAbsolutePath() + "\n");
                addError("LoByte: " + loByte.getAbsolutePath() + "\n");
            }
        }
        else{
            addError("Could not compile and save, program has errors.\n");
            
        }
    }

    private void setErrorList() {
        ASSCommand.compiler = this;
    }

    public ArrayList<String> getErrors() {
        return errorList;
    }

    public void addError(String s) {
        errorList.add(s);
    }

    public boolean hasErrors() {
        return !errorList.isEmpty();
    }
}

class ASSCommand {

    public static JeffreyCompiler compiler;
    private static Pattern p = Pattern.compile("((\\w+):)?\\s*(\\w+)\\s*(.*)",
            Pattern.CASE_INSENSITIVE);
    private static Matcher m = p.matcher("");

    private String address; //set during the derefencing call
    String label;
    String line;
    ArrayList<ASSOperation> operations;

    public ASSCommand(String line) {
        address = "";
        operations = new ArrayList<>();
        label = "";
        this.line = line;
        parseLine();
    }

    private void parseLine() {
        m = m.reset(line);
        if (m.matches()) {
            String label = m.group(2);
            String command = m.group(3);
            String operand = m.group(4);
            System.out.println("label: " + label + " com: "
                    + command + " operand: " + operand);
            if (label != null) {
                this.label = label;
            }
            if (command != null && !operand.equalsIgnoreCase(":")) {
                parseCommand(command, operand);
            }
            else{
                throwError("Missing command: \n\t" + line);
            }
        }
        else{
            throwError("Line is not a statement. A label may be missing its"
                    + " operation.\n\t" + line);
        }
    }
    public void parseCommand(String command, String operand) {
        //iHH = High byte, high nibble. used for Opcodes.
        //iHL = High byte, low nibble. mostly used for register selection
        //iLH = Low byte, high nibble. mostly used for register in selection
        //  as well as jump type
        //iLL = Low byte, low nibble. used for miscellaneous
        int iHH = 0, iHL = 0, iLH = 0, iLL = 0;

        //instruction and address.
        String IR = "0000";
        String AR = "0000";
        boolean dereference = false;

        //OUT 5, R0 == output register 0 at port 5
        //OUT 4, cXXXX == output cXXXX at port 4
        /*
            IR-11 = constant IR- 10,9,8 = register selector if not constant
            IR- 2,1,0 = output port to send to
         */
        if (command.equalsIgnoreCase("OUT")) {
            iHH = 3;
            String[] split = operand.split(",");
            String port = split[0].trim(); //port number
            String value = split[1].trim(); //Register or constant
            
            //Get the port
            iLL = Character.getNumericValue(port.charAt(0));
            
            if (iLL < 0 || iLL > 7) {
                throwError("OUT must specify a port number from 0-7:\n" 
                        + "\t" + line);
            }
            
            //Get the value
            if (value.startsWith("R")) { //register
                iHL = Character.getNumericValue(value.charAt(1));
                if(iHL < 0 || iHL > 7){
                    throwError("Register must be 0-7:\n" 
                        + "\t" + line);
                }
            } else if (value.startsWith("H")) { //hex constant
                iHL = 8; // 8 for constant out
                AR = bufferSizeHexAddress(value.substring(1));
            } else if (value.startsWith("D")) { //decimal constant
                iHL = 8; // 8 for constant out
                int decimalValue = Integer.parseInt(split[1].substring(1));
                AR = intToHexAddress(decimalValue);
            } else {
                throwError("OUT must specify a register or value(Hex/Decimal) to output"
                + "\n\t" + line);
            }
        }
        //OUT 5, R0 == output register 0 at port 5
        //OUT 4, cXXXX == output cXXXX at port 4
        /*
            IR-11 = constant IR- 10,9,8 = register selector if not constant
            IR- 2,1,0 = output port to send to
         */
        if (command.equalsIgnoreCase("IN")) {
            iHH = 4;
            String[] split = operand.split(",");
            String value = split[0].trim(); //Register or constant
            String port = split[1].trim(); //port number
            
            //Get the port
            iLL = Character.getNumericValue(port.charAt(0));
            if (iLL < 0 || iLL > 7) {
                throwError("IN must specify a port number from 0-7:\n" 
                        + "\t" + line);
            }
            
            //Get the value
            if (value.startsWith("R")) { //register
                iHL = Character.getNumericValue(value.charAt(1));
                if(iHL < 0 || iHL > 7){
                    throwError("Register must be 0-7:\n" 
                        + "\t" + line);
                }
            } 
            else if (value.startsWith("[") && value.endsWith("]")) {
                iHL = 8; // use constant to note to put this in memory
                //will need to dereference, cut out the []
                AR = value.substring(1, value.length() - 1);
                dereference = true;
            }
            else {
                throwError("IN must specify a (R)egister or ([label]) to input"
                + "\n\t" + line);
            }
        }
        //LOD R(0-7), [label]
        /*
            IR-10,9,8 = register selector, IR-3 = constant value
         */ 
        else if (command.equalsIgnoreCase("LOD")) {
            iHH = 1;
            String[] split = operand.split(",");
            String register = split[0].trim(); //register to load to
            String value = split[1].trim(); //name of address to load
            
            //Register to load into
            if (register.startsWith("R")) {
                iHL = Character.getNumericValue(split[0].charAt(1));
                if(iHL < 0 || iHL > 7){
                    throwError("Register must be 0-7:\n" 
                        + "\t" + line);
                }
            }
            else{
                throwError("Operand must be a (R)egister:\n" 
                    + "\t" + line);
            }
            
            //Value to load
            if (value.startsWith("H")) { // Hex constant
                iLL = 8; // 8 for constant load
                AR = bufferSizeHexAddress(value.substring(1));
            } else if (value.startsWith("D")) { //decimal constant
                iLL = 8; // 8 for constant load
                int decimalValue = Integer.parseInt(value.substring(1));
                AR = intToHexAddress(decimalValue);
            } 
            else if (value.startsWith("[") && value.endsWith("]")) {
                //will need to dereference, cut out the []
                AR = value.substring(1, value.length() - 1);
                dereference = true;
            }
            else{
                throwError("Value must be a (H)ex, (D)ecimal, or ([label]):\n" 
                        + "\t" + line);
            }
        } 
        //MOV r(0-7), r(0-7)
        /*
            LOD Operation, IR-10,9,8 = register to get value
            IR-7 = use Register IN  IR-4,5,6 = register to get value from
         */ 
        else if (command.equalsIgnoreCase("MOV")) {
            iHH = 1;
            String[] split = operand.split(",");
            String registerSave = split[0].trim(); //register to load to
            String registerValue = split[1].trim(); //register to get data from
            if (registerSave.startsWith("R")) {
                iHL = Character.getNumericValue(registerSave.charAt(1));
                if(iHL < 0 || iHL > 7){
                    throwError("Register to move value to must be 0-7:\n" 
                        + "\t" + line);
                }
            } 
            else {
                throwError("Register to MOV into must begin with R.\n\t" + line);
            }

            if (registerValue.startsWith("R")) {
                //add 8 to this value to get RIN flag
                iLH = Character.getNumericValue(registerValue.charAt(1));
                if(iLH < 0 || iLH > 7){
                    throwError("Register to get value from must be 0-7:\n" 
                        + "\t" + line);
                }
                iLH += 8;
            }
            else {
                throwError("Register to MOV from must begin with R.\n\t" + line);
            }
        } //STO [label], R(0-7)
        /*
            IR-10,9,8 = register selection
         */ 
        else if (command.equalsIgnoreCase("STO")) {
            iHH = 2;
            String[] split = operand.split(",");
            String address = split[0].trim(); //name of address to load
            String register = split[1].trim(); //register to take data from
            if (register.startsWith("R")) {
                iHL = Character.getNumericValue(register.charAt(1));
                if(iHL < 0 || iHL > 7){
                    throwError("Register to get value from must be 0-7:\n" 
                        + "\t" + line);
                }
            }
            else{
                throwError("Second operand must be a (R)egister:\n" 
                    + "\t" + line);
            }
            if (address.startsWith("[") && address.endsWith("]")) {
                //will need to dereference, cut out the []
                AR =  address.substring(1, address.length() - 1);
                dereference = true;
            }
            else{
                throwError("First operand must be a ([)label(])" 
                    + "\t" + line);                
            }
        } 
        //3(0-7)register(0-1)mem/constant(0-1)(1=adc)
        //ADD R(0-7), [label]/c
        /*
            IR-10,9,8 = register add with and to save the addition
            IR-7 = add using value from another register, RIN flag
            IR-6,5,4 = Register in selector if RIN
            IR-3 = constant
            IR-0 = add with carry
         */ 
        else if (command.equalsIgnoreCase("ADD")
                || command.equalsIgnoreCase("ADC")) {
            iHH = 5;
            //0 for add, 1 for adc
            iLL = (command.equalsIgnoreCase("ADD") ? 0 : 1);
            String[] split = operand.split(",");
            String register = split[0].trim(); //register to load to
            String addend = split[1].trim(); //what to add with
            if (register.startsWith("R")) {
                iHL = Character.getNumericValue(register.charAt(1));
                if(iHL < 0 || iHL > 7){
                    throwError("Register must be 0-7:\n" 
                        + "\t" + line);
                }
            }
            else{
                throwError("There must be a (R)egister to save addition" 
                    + "\t" + line);      
            }
            
                
            if (addend.startsWith("R")) {
                //2nd operand is a register, use RIN by adding 8
                iLH = Character.getNumericValue(addend.charAt(1));
                if(iLH < 0 || iLH > 7){
                    throwError("Register must be 0-7:\n" 
                        + "\t" + line);
                }
                iLH += 8;
                
            } else if (addend.startsWith("[") && addend.endsWith("]")) {
                iLH = 0; //memory 
                //will need to dereference, cut out the []
                AR = addend.substring(1, addend.length() - 1);
                dereference = true;
            } else if (addend.startsWith("H")) { // hex constant
                iLL += 8; // constant is IR-3
                AR = bufferSizeHexAddress(addend.substring(1));
            } else if (addend.startsWith("D")) { //decimal constant
                iLL += 8; // constant is IR-3
                int decimalValue = 0;
                try{
                    decimalValue = Integer.parseInt(addend.substring(1));
                }catch( NumberFormatException nfe){
                    throwError("Not a (D)ecimal\n\t"+ line);
                }
                AR = intToHexAddress(decimalValue);
            }
            else{
                throwError("Value must be a (R)egister, (H)ex, (D)ecimal,"
                        + " or ([label]):\n" 
                        + "\t" + line);
            }
        } 
        //3(0-7)register(0-1)mem/constant(0-1)(1=adc)
        //ADD R(0-7), [label]/c
        /*
            IR-10,9,8 = register sub with and to save the subtraction
            IR-7 = sub using value from another register, RIN flag
            IR-6,5,4 = Register in selector if RIN
            IR-3 = constant
            IR-0 = sub with borrow. not implemented yet
         */ 
        else if (command.equalsIgnoreCase("SUB")
                || command.equalsIgnoreCase("SBB")) {
            iHH = 6;
            //0 for SUB, 1 for SBB
            iLL = (command.equalsIgnoreCase("SUB") ? 0 : 1);
            String[] split = operand.split(",");
            String register = split[0].trim(); //register to load to
            String subend = split[1].trim(); //what to add with
            if (register.startsWith("R")) {
                iHL = Character.getNumericValue(register.charAt(1));
                if(iHL < 0 || iHL > 7){
                    throwError("Register must be 0-7:\n" 
                        + "\t" + line);
                }
            }
            else{
                throwError("There must be a (R)egister to save subtraction" 
                    + "\t" + line);      
            }
            
                
            if (subend.startsWith("R")) {
                //2nd operand is a register, use RIN by adding 8
                iLH = Character.getNumericValue(subend.charAt(1));
                if(iLH < 0 || iLH > 7){
                    throwError("Register must be 0-7:\n" 
                        + "\t" + line);
                }
                iLH += 8;
                
            } else if (subend.startsWith("[") && subend.endsWith("]")) {
                iLH = 0; //memory 
                //will need to dereference, cut out the []
                AR = subend.substring(1, subend.length() - 1);
                dereference = true;
            } else if (subend.startsWith("H")) { // hex constant
                iLL += 8; // constant is IR-3
                AR = bufferSizeHexAddress(subend.substring(1));
            } else if (subend.startsWith("D")) { //decimal constant
                iLL += 8; // constant is IR-3
                int decimalValue = 0;
                try{
                    decimalValue = Integer.parseInt(subend.substring(1));
                }catch( NumberFormatException nfe){
                    throwError("Not a (D)ecimal\n\t"+ line);
                }
                AR = intToHexAddress(decimalValue);
            }
            else{
                throwError("Value must be a (R)egister, (H)ex, (D)ecimal,"
                        + " or ([label]):\n" 
                        + "\t" + line);
            }
        } 
        //JMP label, JN/JNN R0-7, label
        /*
            IR-10,9,8 = register selector. used for JN and JNN, maybe create a flag instead
            IR-6,5,4 = jump type
            000-JMP 001-JZ 010-JNZ 011-JC 100-JNC 101-JN 110-JNN
         */ 
        else if (command.equalsIgnoreCase("JMP")
                || command.equalsIgnoreCase("JZ")
                || command.equalsIgnoreCase("JNZ")
                || command.equalsIgnoreCase("JC")
                || command.equalsIgnoreCase("JNC")
                || command.equalsIgnoreCase("JN")
                || command.equalsIgnoreCase("JNN")) {
            iHH = 7;
            iLH = (command.equalsIgnoreCase("JMP") ? 0 : iLH);
            iLH = (command.equalsIgnoreCase("JZ") ? 1 : iLH);
            iLH = (command.equalsIgnoreCase("JNZ") ? 2 : iLH);
            iLH = (command.equalsIgnoreCase("JC") ? 3 : iLH);
            iLH = (command.equalsIgnoreCase("JNC") ? 4 : iLH);
            iLH = (command.equalsIgnoreCase("JN") ? 5 : iLH);
            iLH = (command.equalsIgnoreCase("JNN") ? 6 : iLH);
            //for JMP,JZ,JNZ,JC,JNC
            if (iLH == 0 || iLH == 1 || iLH == 2 || iLH == 3 || iLH == 4) {
                AR = operand.trim();
                dereference = true;

            } //for JN, JNN
            else {
                String[] split = operand.split(",");
                split[0] = split[0].trim(); //name of register to check
                split[1] = split[1].trim(); //label to jump to
                if (split[0].startsWith("R")) {
                    iHL = Character.getNumericValue(split[0].charAt(1));
                    if(iHL < 0 || iHL > 7){
                        throwError("Register must be 0-7:\n" 
                            + "\t" + line);
                    }
                }
                else{
                    throwError("There must be a (R)egister to check for negative.\n" 
                        + "\t" + line);      
                }
                AR = split[0];
                dereference = true;
            }
        } // Nothing changes HLT's functioning
        else if (command.equalsIgnoreCase("HLT")) {
            iHH = 15;
            AR = "FFFF"; // not necessary
        } //IR-10,9,8 register selection
        else if (command.equalsIgnoreCase("PUSH")) {
            iHH = 8;
            operand.trim();
            if (operand.startsWith("R")) {
                iHL = Character.getNumericValue(operand.charAt(1));
                if(iHL < 0 || iHL > 7){
                    throwError("Register must be 0-7:\n" 
                        + "\t" + line);
                }
            }
            else{
                throwError("There must be a (R)egister to PUSH.\n" 
                    + "\t" + line);      
            }
        } //IR-10,9,8 register selection
        else if (command.equalsIgnoreCase("POP")) {
            iHH = 9;
            operand.trim();
            if (operand.startsWith("R")) {
                iHL = Character.getNumericValue(operand.charAt(1));
                if(iHL < 0 || iHL > 7){
                    throwError("Register must be 0-7:\n" 
                        + "\t" + line);
                }
            }
            else{
                throwError("There must be a (R)egister to POP to.\n" 
                    + "\t" + line);      
            }
        }  
        //IR-10,9,8 register selection
        //PUSH COMMAND, IR 0 MAKES IT A CALL FUNCTION
        else if (command.equalsIgnoreCase("CALL")) {
            iHH = 8;
            iLL = 1;
            AR = operand.trim();
            dereference = true;
            System.out.println("We are calling" + line);
        } //IR-10,9,8 register selection
        else if (command.equalsIgnoreCase("RET")) {
            iHH = 9;
            iLL = 1;
                System.out.println("We are returning" + line);
        } 
        else if (command.equalsIgnoreCase("DB")) {
            if (operand.startsWith("H")) {
                AR = bufferSizeHexAddress(operand.substring(1));
            } 
            else if (operand.startsWith("D")) { //decimal constant
                int decimalValue = Integer.parseInt(operand.substring(1));
                AR = intToHexAddress(decimalValue);
            }
            else{
                throwError("DB missing type value (H/D)");
            }
            operations.add(new ASSOperation(AR));
            return;
        }
        IR = intToHex(iHH) + intToHex(iHL) + intToHex(iLH) + intToHex(iLL);
        operations.add(new ASSOperation(IR, AR, dereference));
    }
    public void throwError(String s) {
        compiler.addError(s);
        System.out.println(s);
    }

    private String intToHexAddress(int i) {
        int accumulator = 0;
        int highhigh = i / 4096;  //first char = i / 4096;
        accumulator += highhigh * 4096;
        int highlow = (i - accumulator) / 256; //second char = i / 256;
        accumulator += highlow * 256;
        int lowhigh = (i - accumulator) / 16; //third char = i / 16;
        accumulator += lowhigh * 16;
        int lowlow = i - accumulator;
        //fourst char  = leftover
        if(highhigh > 15){
            throwError("Integer given is greater than 65536: "
                    + i + "\n\t" + line);
        }
        String address = intToHex(highhigh) + intToHex(highlow)
                + intToHex(lowhigh) + intToHex(lowlow);
        return address;
    }
    private String intToHex(int i) {
        switch (i) {
            case 0:
                return "0";
            case 1:
                return "1";
            case 2:
                return "2";
            case 3:
                return "3";
            case 4:
                return "4";
            case 5:
                return "5";
            case 6:
                return "6";
            case 7:
                return "7";
            case 8:
                return "8";
            case 9:
                return "9";
            case 10:
                return "A";
            case 11:
                return "B";
            case 12:
                return "C";
            case 13:
                return "D";
            case 14:
                return "E";
            case 15:
                return "F";
            default:
                throwError("Could not convert int to hex: " + i + "\n\t" + line);
                return "0";
        }
    }
    private String bufferSizeHexAddress(String s) {
        if (s.length() == 4) {
            return s;
        }
        if (s.length() > 4) {
            throwError("Given Hex is larger than 16 bits: " 
                    + s + "\n\t" + line);
            return s.substring(0, 4);
        } else {
            while (s.length() < 4) {
                s = "0" + s;
            }
            return s;
        }
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(int address) {
        this.address = intToHexAddress(address);
    }
    public int getSize() {
        int size = 0;
        for (ASSOperation op : operations) {
            if (op.addressOnly) {
                size++;
            } else {
                size += 2;
            }
        }
        return size;
    }
}

class ASSOperation {

    String instruction;
    String address;
    boolean dereference;
    boolean addressOnly;

    public ASSOperation(String in, String ad, boolean deref) {
        instruction = in;
        address = ad;
        dereference = deref;
    }
    public ASSOperation(String ad) {
        address = ad;
        dereference = false;
        addressOnly = true;
    }
}
