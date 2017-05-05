# MMLogic 16-bit ALU

## Ripple Adder
![Adder Low Byte][adderLow]
![Adder High Byte][adderHigh]
The ALU is a 16 bit ripple adder.

## Flags and Pass through
![ALU Flags][flags]
Contains the carry, zero, and negative flags. Also holds the carry in, used for add with carry, subtract, and subtract with borrow.

## Subtraction Complement circuit
![Subtraction Complementation][complement]
2's Complements the input when using subtraction. 

[adderLow]: https://grexel.github.io/JCompiler/MML_16bit/ALU/AdderLow.JPG "Adder Low Byte"
[adderHigh]: https://grexel.github.io/JCompiler/MML_16bit/ALU/AdderHigh.JPG "Adder High Byte"
[flags]: https://grexel.github.io/JCompiler/MML_16bit/ALU/AdderFlagsandPass.JPG "Flags"
[complement]: https://grexel.github.io/JCompiler/MML_16bit/ALU/AdderSubComplement.JPG "Subtraction Complement"
