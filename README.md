#Mindfunc

Mindfunc is a functional extension to the brainfuck. This page assumes knowledge of brainfuck, so you should totally read up on it if you haven't already.

##Details

Mindfunc ignores whitespace, so formatting mindfunc code is easy.

Values in mindfunc can be stored in variables. Variable identifiers can only be one character, and cannot be one of the reserved characters [, ], (, ), {, }, and ;.

Every value in mindfunc is technically a consumer, a function that doesn't return a value. These consumers can specify how many arguments to accept, and can only accept that many.

##Consumers

A consumer is called simply using its variable identifier. For example, if we'd like to call the *+* consumer, simply:

```
+
```

To pass arguments to consumers, we use a *;* separator. If a consumer *2* exists that accepts 1 argument, we could call it like:

```
2;+
```

Consumers can also be composed, but only as arguments to other consumers. This is done by enclosing a call in parenthesis in the argument list. For example, if a consumer *r* exists that moves the pointer right twice and then calls it's first argument, we can use our previous *2;+* as a composition to specify that we want to increment that memory slot twice.

```
r;(2;+)
```

###Built-ins

Mindfunc provides consumers for all brainfuck operations, plus the *#* consumer.

- **>** |Move the pointer to the right
- **<** |Move the pointer to the left
- **+** |Increment the memory cell under the pointer
- **-** |Decrement the memory cell under the pointer
- **#** |Output the number stored in the cell at the pointer
- **.** |Output the character signified by the cell at the pointer
- **,** |Input a number and store it in the cell at the pointer

While *[* and *]* behave the same in mindfunc as in brainfuck, they are not considered consumers. The implementation can decide how to handle these.

###Declaration

Consumers can be declared in mindfunc with the following syntax:

```
((args)namebody)
```

OR (much prettier and easier to see):

```
( (args)name
  body
)
```

where *args* is every argument identifier with no separation (note that the arg list is optional, if a consumer accepts no arguments the *()* can be omitted altogether), *name* is the single-character identifier for this consumer , and *body* is the body of the consumer . As an example, let's write the consumer *r* we mentioned earlier.

```
( (f)r
  >>f
)
```

There! We've defined a consumer *r* which takes one argument *f*. The consumer moves the memory pointer right twice and calls the argument consumer *f*.

##Anonymous Consumers

Mindfunc also allows anonymous consumers, or *lambdas*. A lambda is not given a name and must be used in a call.

Lambdas are declared inside of curly braces *{}*:

```
{(args)body}
```

OR:

```
{ (args)
  body
}
```

This only differs from normal consumer declaration where curly braces are used and the identifier name is omitted.

Now lets use a lambda to tell *r* what to do after it moves us right twice:

```
r;{++}
```

Now, when *r* calls it's *f* parameter, it's calling our lambda that will increment the memory cell twice.

##Other Examples

Here's a program that prints "500 005", figure it out through the poor explanation.

```
Code:           Pseudo code:
( (f)2          Create consumer *2* that accepts one argument *f*
ff            Call *f* twice.
)               End this consumer.

( (lr)m         Create consumer *m* that accepts arguments *l* and *r*.
r             Call *r*.
[-]           Set the current cell to 0.
l             Call *l*.
[-r+l]        Move the value at the current location to wherever *r* tells us to go (let's hope they only have pointer movements).
)

( p             Create consumer *p* that accepts no arguments.
#>#>#>        Print three cells as numbers.
<<<           Move back to the cell *p* started at.
)

>               Move right one cell.
{(f)fff};(2;+)- Set this cell to 5.
p               Print this cell and the two to its right.
<.>             Display a space character.
m;{<<};{>>}     Call *m*, passing lambdas that move left twice and right twice respectively. Our cell position is the same.
p               Print this cell and the two to its right.
```