#!/bin/sh

PROCESSING=$(./config.sh)

if [ -z $PROCESSING ]; then
	echo "Did you read the README file?"
	echo "You need to edit config.sh before compiling the code"
	exit 1
fi

if [ ! -d $PROCESSING ]; then
	echo "Did not find the folder: $PROCESSING"
	echo "Make sure to edit config.sh to point to the right" \
	     "location of the Processing Libraries"
	exit 1
fi

if [ ! -d bin/ ] ; then
	mkdir bin
fi

find . -name "*.java" > javafiles
javac -d bin -cp "$PROCESSING/*" @javafiles
rm javafiles
