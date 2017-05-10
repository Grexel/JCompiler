# MMLogic 16-bit CPU Buses and Selectors

## Selectors
![Selector Page 1][selector1]
![Selector Page 2][selector2]
These pages select which buses are active during each cycle.


## ALU Bus
![ALU Bus][aluBus]
This Bus can switch the second input into the ALU to be either the address register when using a constant, the memory data from RAM if using a variable, or the secondary register if adding two registers together. The first input will always be the main register.

## PC Jumping Bus
![PC Bus][pcBus]
This bus is necessary to allow jumping from either the address register for normal jumps, or from memory data when jumping based on a RET return call. 

## RAM Address Bus
![RAM Address Bus][ramAddressBus]
This bus allows getting the memory data in RAM from either: the pc (program counter) register to set the instruction and address registers, the address register (when using variables), and the stack pointer when using stack functions including CALL and RET.

## RAM Data In Bus
![RAM Data In Bus][ramDataInBus]
Allows saving to RAM from an input (I'm not sure if this is good design), the pc (program counter) register needed to use the CALL and RET functions, and by default the main register

## Register Main Data Out Bus
![Main Register Data Out Low][regMainOutLow]
![Main Register Data Out High][regMainOutHigh]
Allows the computer to choose which main register to use for commands. 

## Register Secondary Data Out Bus
![Secondary Register Data Out Low][regSecondaryOutLow]
![Secondary Register Data Out High][regSecondaryOutHigh]
Allows the computer to choose which secondary register to use for commands. Used for commands such as MOV (moving data from on register to another) and ADD when adding two registers together


## Register Data in Bus
![Register Data In Low][regInLow]
![Register Data In High][regInHigh]
Allows the Register to get data from an INput device, another register when MOVing, the ALU when doing arithmetic, memory Data when loading a variable, and the address register when getting a constant.

## Output Bus
![Output Data Bus][outputBus]
Allows the data being sent to the output devices to come from either the address register when outputting a constant, or outputting from the register.

## Input Bus
Similar to the main register data out bus, unfinished with only one working input that has been set up to use the keyboard.


[selector1]: https://grexel.github.io/JCompiler/MML_16bit/BUSES/Selector1.JPG "Selector Page 1"
[selector2]: https://grexel.github.io/JCompiler/MML_16bit/BUSES/Selector2.JPG "Selector Page 2"
[aluBus]: https://grexel.github.io/JCompiler/MML_16bit/BUSES/ALUAddend2BUS.JPG "ALU Bus"
[pcBus]: https://grexel.github.io/JCompiler/MML_16bit/BUSES/PCJumpBUS.JPG "PC Jump Bus"
[ramAddressBus]: https://grexel.github.io/JCompiler/MML_16bit/BUSES/RAMAddressBUS.JPG "RAM Address Bus"
[ramDataInBus]: https://grexel.github.io/JCompiler/MML_16bit/BUSES/RAMDataInBUS.JPG "RAM Data In Bus"
[regMainOutLow]: https://grexel.github.io/JCompiler/MML_16bit/BUSES/RegisterMainDataOutBUSLow.JPG "Main Register Data Out Low"
[regMainOutHigh]: https://grexel.github.io/JCompiler/MML_16bit/BUSES/RegisterMainDataOutBUSHigh.JPG "Main Register Data Out High"
[regSecondaryOutLow]: https://grexel.github.io/JCompiler/MML_16bit/BUSES/RegisterSecondaryDataOutBUSLow.JPG "Secondary Register Data Out Low"
[regSecondaryOutHigh]: https://grexel.github.io/JCompiler/MML_16bit/BUSES/RegisterSecondaryDataOutBUSHigh.JPG "Secondary Register Data Out High"
[regInLow]: https://grexel.github.io/JCompiler/MML_16bit/BUSES/RegisterDataInBUSLow.JPG "Register Data In Low"
[regInHigh]: https://grexel.github.io/JCompiler/MML_16bit/BUSES/RegisterDataInBUSHigh.JPG "Register Data In High"
[outputBus]: https://grexel.github.io/JCompiler/MML_16bit/BUSES/OutputDataInBUS.JPG "Output Data In"