### Dots Remake

This a remake of the game [Dots: A Game About
Connecting](http://weplaydots.com/) written in Java using
[Processing](http://processing.org/).

### How to compile

These instructions assume you are on a Linux machine (I think this may
also work on a Mac) and have the JDK installed.

If you already have Processing installed in your machine, skip to step
3.

1. Download the right Processing version for your operating system
   [here](https://www.processing.org/download/). 

1. Uncompress the downloaded file

   	$ tar -xvzf <file_name>

   A folder will be uncompressed. We will refer to this folder as the
   *processing* folder.

1. Inside the *processing* folder there is a core/library folder.
   You must assign the full path to *processing*/core/library to the
   PROCESSING variable inside *config.sh*
   In may case this was:

   	PROCESSING=$HOME/processing-2.1.1/core/library

1. Make sure all \*.sh files are executable

   	$ chmod 755 \*.sh

1. Run *./compile.sh*
1. Run *./run.sh*
1. Have fun!
