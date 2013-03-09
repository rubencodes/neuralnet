**********************************************************************
* Read Me: Neural.java                                               *
* By Tyler Higgins, Marissa Rosenthal, and Ruben Martinez Jr.        *
* Date: 8 March 2013                                                 *
**********************************************************************
This program utilizes a neural networks made of perceptrons to 
facilitate handwriting recognition. 

To Compile: $ javac Neural.java

To Execute: $ java Neural [Training File] [Test File] [Epochs #] 
[Learning Rate #] [Input Representation] [Output Representation #]

More Info:
[Training File]         Ex. training.tra
- Name of file to train perceptrons on.

[Test File]             Ex. testing.tes
- Name of file to test perceptrons on.

[Epochs #]              Ex. 1, 10, 100
- Number of epochs to train on training file.

[Learning Rate]         Ex. 1, 0.1, 0.01
- Rate of learning for updating weights.

[Input Representation]  Choose: bmp, ndsi, dsi
- Format of input file.
  - bmp:  32x32 bitmap
  - dsi:  8x8 
  - ndsi: normalized version of dsi.

[Output Representation] Choose: 1, 4, 10
- Number of nodes to use.
  - 1:  Trains one node.
  - 4:  Trains four nodes binary representations.
  - 10: Trains ten nodes, each to recognize one target.

Example Run: $ java Neural training.tra testing.tes 10 0.01 ndsi 10