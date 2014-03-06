#!/bin/sh

PROCESSING=$(./config.sh)

if [ -z $PROCESSING ]; then
	echo "Did you read the README file?"
	echo "You need to edit config.sh before compiling the code"
	exit 1
fi

java -cp "bin:$PROCESSING/*" main.Main
