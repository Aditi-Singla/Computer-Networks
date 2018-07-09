#!/bin/bash
if [ "$#" == 1 ]; then
    java Receiver "$1"
else
    echo "Illegal number of parameters ($#), should be 1"
fi