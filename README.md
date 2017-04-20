## Welcome to the JCompiler site

This is a project that started from a simple 8-bit MMLogic computer. This 8-bit computer was increased to 16-bit along with increasing the register count to 8 and adding features such as a stack and busses to control data flow. The increased complexity of the computer demanded a more complex compiler to create code it could run.

The first attempt at a compiler is in [jeffASM](https://github.com/Grexel/JCompiler/tree/master/src/jeffasm). The compiler had bad design(checking individual characters to complete the code generation), but worked. I came across [How to implement a programming language in JavaScript](http://lisperator.net/pltut/) which showed a better way to write a compiler.
Loosely following the tutorial I came up with these steps:
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
  --A variable node simply places it's hex value into it's alotted line. 
  --An instruction node gets run through a separate compile function based on its command. So if the instruction node's command is LOD, it gets sent to the compileLOD function.
  --each compile function creates a set of high high, high low, low high, and low low nibbles. These are set based on the function  and the instruction node's arguments. An address value is also created, though some functions do not use it. If the argument is a variable, it goes through each node looking for a label matching the variable's name and uses that node's address.
  --each instruction places its high-high,high-low,low-high, and low-low nibbles on its first line, and the address on the second.
 5. save the "machine code" to a file for MMLogic to load. I say "machine code" because text files using hex are valid in MMLogic.
  -since MMLogic's RAM arrays are 8-bit wide with 16-bit selectors, I use two side by side to get 16-bit width. This requires separating the machine code into a high byte and low byte file.
 6. load the files into MMLogic and run the program.

### Markdown

Markdown is a lightweight and easy-to-use syntax for styling your writing. It includes conventions for

```markdown
Syntax highlighted code block

# Header 1
## Header 2
### Header 3

- Bulleted
- List

1. Numbered
2. List

**Bold** and _Italic_ and `Code` text

[Link](url) and ![Image](src)
```

For more details see [GitHub Flavored Markdown](https://guides.github.com/features/mastering-markdown/).

### Jekyll Themes

Your Pages site will use the layout and styles from the Jekyll theme you have selected in your [repository settings](https://github.com/Grexel/JCompiler/settings). The name of this theme is saved in the Jekyll `_config.yml` configuration file.

### Support or Contact

Having trouble with Pages? Check out our [documentation](https://help.github.com/categories/github-pages-basics/) or [contact support](https://github.com/contact) and we’ll help you sort it out.
