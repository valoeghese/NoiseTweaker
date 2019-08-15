# NoiseTweaker
Java program for messing with noise via simple script files, making testing noise manipulation somewhat easier

Seeds for noise gen are unique per file in which the function is executed
All transformers are located in the directory "transformers", which should be located in the same directory as the built jar

## Program Arguments:
`--randomseed`
- Use random seed offset when running the script

`--main <name of main transformer>`
- Change the main transformer which is executed. The default is "main" (the file ./transformer/main.tfm)

## Script Rules:
- All variables/values are double precision float values.
- The next script line is specified by a new line

Each script file takes in values and outputs values. These are specified at the head of the file (before `start`).

The main script file takes in two values (x, y) and outputs three values (r, g, b)

the line `start` specifies the end of the head and the beginning of the body

Comments are specified with a hash followed by a space, at the beginning of a line only

`# this is a comment`

## Header Commands
`in <variable name>`
- takes the next avaliable input value and stores it as a local variable under the name specified

`out <variable name>`
- adds the name of a variable to be outputed at the end of program
- the script calling this file will have their local variable of this name set to this scripts final variable value of this name
- for the main script file the outputs specify red, green, and blue values

## Body Commands

`set <variable name> <value>`
- sets the local variable of this name to this value

`add <variable name> <value>`
- adds this value to the local variable of this name

`sub <variable name> <value>`
- subtracts this value from the local variable of this name

`mul <variable name> <value>`
- multiplies the local variable of this name by this value

`div <variable name> <value>`
- divides the local variable of this name by this value

(TODO! complete this list)
