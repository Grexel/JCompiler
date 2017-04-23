## Welcome to the JCompiler site

This is a project that started from a simple 8-bit MMLogic computer. This 8-bit computer was increased to 16-bit along with increasing the register count to 8 and adding features such as a stack and busses to control data flow. The increased complexity of the computer demanded a more complex compiler to create code it could run.

### The 16-bit MMLogic Computer
Going from 8-bit to 16-bit created a very large computer(around 50 pages), but many pages go to modeling 8 registers, inputs, outputs, computer registers, and busses. The magic happens in a few Instruction pages that control the data flow.
The Tutorial can be found [here](https://grexel.github.io/JCompiler/MMLogicTutorial).
### The Assembly Compiler
The first attempt at a compiler is in [jeffASM](https://github.com/Grexel/JCompiler/tree/master/src/jeffasm). The compiler had bad design(checking individual characters to complete the code generation), but worked. I came across [How to implement a programming language in JavaScript](http://lisperator.net/pltut/) which showed a better way to write a compiler.
Loosely following the tutorial I came up with these steps to create the next compiler [jasm](https://github.com/Grexel/JCompiler/tree/master/src/jeffasm):
1. create a character stream that reads in single characters from the file.
2. create a token stream that reads in a group of characters separated by whitespace.
  - give these tokens a type (such as keyword, number, id) and a value (such as LOD, 45, var1)
3. create a parser that reads in tokens and outputs Nodes.
  - for assembly, I have two node types, instruction nodes and variable nodes.
  - all nodes can have a label attribute
  - instruction nodes may take a label, but need one command (e.g. LOD,ADD,PUSH) and 0+ arguments (e.g. register 0 and number 45 for LOD).
  - variable nodes require a label and are made when a command is VAR. They take one argument, which is their value.
 4. create a compiler that takes the nodes generated from a parser and turns them into "machine code."
  - The first step I do with the nodes is give them each an address. Starting from 0, instruction nodes get 2 lines, and variable nodes get 1 line. Then I run each Node through the compile process. 
  - The compile process looks like this:
  - - A variable node simply places it's hex value into it's alotted line. 
  - - An instruction node gets run through a separate compile function based on its command. So if the instruction node's command is LOD, it gets sent to the compileLOD function.
  - - each compile function creates a set of high high, high low, low high, and low low nibbles. These are set based on the function  and the instruction node's arguments. An address value is also created, though some functions do not use it. If the argument is a variable, it goes through each node looking for a label matching the variable's name and uses that node's address.
  - - each instruction places its high-high,high-low,low-high, and low-low nibbles on its first line, and the address on the second.
 5. save the "machine code" to a file for MMLogic to load. I say "machine code" because text files using hex are valid in MMLogic.
  - since MMLogic's RAM arrays are 8-bit wide with 16-bit selectors, I use two side by side to get 16-bit width. This requires separating the machine code into a high byte and low byte file.
 6. load the files into MMLogic and run the program.

The Assembly compiler currently supports the following commands:
  - LOD / MOV
  - STO
  - IN
  - OUT
  - ADD / ADC
  - SUB / SBB
  - JMP / JZ / JNZ / JC / JNC 
  - PUSH / CALL
  - POP / RET
  - HLT
  - VAR for defining variables.
More commands are planned such as: JG- jump greater than, JLE- jump less than or equal, JN- jump negative, CMP- compare, INC- increment, DEC- decrement.

Each command may take multiple paramaters. Here is a list of all usages.
  - LOD R#, [var]
  - LOD R#, 0xFF
  - LOD R#, 45
  - MOV R#, R# (dest, src)
  - STO [var], R#
  - IN R#, #(port)
  - IN [var], #(port)
  - OUT #(port), R#
  - OUT #(port), 0xFF
  - OUT #(port), 99
  - ADD/ADC R#, R# (dest, src)
  - ADD/ADC R#, 0xFF
  - ADD/ADC R#, 99 
  - ADD/ADC R#, [var]
  - SUB/SBB R#, R# (dest, src)
  - SUB/SBB R#, 0xFF
  - SUB/SBB R#, 99 
  - SUB/SBB R#, [var]
  - JMP/JZ/JNZ/JC/JNC label
  - PUSH R#
  - POP R#
  - CALL label
  - RET
  - HLT
  
A sample program might look like
```
#This program outputs hello world on port 0 (default display)
#then does 0xFF - 90 + 7 and outputs to port1
#then reads in a keyboard press (default port 0)
#which saves to a variable
Start:  CALL sayHello
        CALL sayWorld
        LOD R0, 0xFF
        LOD R1, 90
        SUB R0, R1
        ADD R0, [num7]
        OUT 1, R0
        CALL getKeyPress
        STO [keyPress], R0
        LOD [keyPress], R5
        OUT 0, R5
        HLT
sayHello: OUT 0, 0x48
          OUT 0, 0X65
          OUT 0, 0X6C
          OUT 0, 0X6C
          OUT 0, 0X6F
          RET
sayWorld: OUT 0, 0x57
          OUT 0, 0X6F
          OUT 0, 0X72
          OUT 0, 0X6C
          OUT 0, 0X64
          RET
getKeyPress:  IN R0, 0
        SUB R0, 0
        JZ getKeyPress
        RET
        
num7:   VAR 7
keyPress: VAR 0
```
