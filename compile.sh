#!/bin/sh

if [ ! -d bin/ ] ; then
	mkdir bin
fi

find . -name "*.java" > javafiles
javac -d bin -cp "libs/*" @javafiles
rm javafiles
