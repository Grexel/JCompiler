#MMLogic 16-bit ALU

##Ripple Adder
![alt text][adderLow]
![alt text][adderHigh]
The ALU is a 16 bit ripple adder.

##Flags and Pass through
![alt text][flags]
Contains the carry, zero, and negative flags. Also holds the carry in, used for add with carry, subtract, and subtract with borrow.

##Subtraction Complement circuit
![alt text][complement]
2's Complements the input when using subtraction. 

[adderLow]: https://grexel.github.io/JCompiler/MML_16bit/ALU/AdderLow.jpeg "Adder Low Byte"
[adderHigh]: https://grexel.github.io/JCompiler/MML_16bit/ALU/AdderHigh.jpeg "Adder High Byte"
[flags]: https://grexel.github.io/JCompiler/MML_16bit/ALU/AdderFlagsandPass.jpeg "Flags"
[complement]: https://grexel.github.io/JCompiler/MML_16bit/ALU/AdderSubComplement.jpeg "Subtraction Complement"