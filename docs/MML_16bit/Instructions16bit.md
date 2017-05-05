# MMLogic 16-bit Clock, Instructions, etc.

## Clock 
![Clock][clock]
The clock holds a timer which is set to the maximum 1 ms. Slower speeds yield more stable operation.
There are 4 stages to each CPU cycle. The first loads an instruction, the second loads an address, the third executes most instructions, and the fourth is necessary for some operations like CALL/RET and achieving stable output device clocking.

## Instruction decoder
![Instructions][instruction]
This is where the magic happens. The top 4 bits of an instruction are demultiplexed into 1 of 16 possible commands.
The rest of the instruction bits control how the command operates, with some commands needing none, such as PUSH, and some having many, like LOD with its ability to take data in from another register, a constant from the address register, or a variable from the RAM memory data out.

## Jumping
![Jumping][jump]
Jumping is demultiplexed from 3 instruction bits. Jumping includes options such as jump if zero, not zero, carry, not carry, negative, or not negative.

## RAM Array
![RAM Array][ram]
The RAM array uses two 64kb 8bit RAM modules side by side to allow 16 bit width of each stage. 
This poses a few problems such as necessitating two files to be created from the compiler, and having the "byte width" be 16 bit instead of 8 bit.

[clock]: https://github.com/Grexel/JCompiler/MML_16bit/Clock.JPG "Clock"
[instruction]: https://github.com/Grexel/JCompiler/MML_16bit/Instructions.JPG "Instruction Decoder"
[jump]: https://github.com/Grexel/JCompiler/MML_16bit/Jump.JPG "Jumping"
[ram]: https://github.com/Grexel/JCompiler/MML_16bit/RAM/RAMArray.JPG "RAM"
