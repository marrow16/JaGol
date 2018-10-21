# JaGol (Conway's Game Of Life)
[![Apache Version 2 Licence](http://img.shields.io/:license-Apache%20v2-blue.svg)](LICENSE)
[![Latest Build](https://img.shields.io/badge/download-jagol--2.6.1.jar-blue.svg)](./built/jagol-2.6.1.jar)

_Written for fun in Java using JavaFX_

#### To run
Dowload the latest [build](./built/jagol-2.6.1.jar) and execute from command line:-
```sbtshell
java -jar jagol-2.6.jar
```

#### Features:
+ Adjustable board size, generation speed and board drawing options
+ Supports different life rules (e.g. Standard, [HiLife](http://www.conwaylife.com/wiki/HighLife) or custom) 
+ Animation (gif) recording
+ Pattern loading/saving (using [.rle](http://www.conwaylife.com/wiki/Rle) files)
+ Drag patterns (rle and plaintext) from websites (e.g. [Life Lexicon](http://www.conwaylife.com/ref/lexicon/lex_1.htm))
+ Board drawing - the board is focusable and accepts keys...
  + move about with arrow keys
    + hold `Ctrl` down to set cell alive
    + hold `Alt+Ctrl` down to invert cell alive states
  + type something!
+ Additional command line interface
+ Cell ageing (cells can optionally die of old age)

![Screen Shot](https://user-images.githubusercontent.com/13290974/31861754-801105a8-b72a-11e7-829a-0e0fa7b29f9a.png)

Example animation output...

![golanimation](https://user-images.githubusercontent.com/13290974/31552671-11ea1296-b030-11e7-8e7f-f537ec1f9bad.gif)

_Enjoy!_
