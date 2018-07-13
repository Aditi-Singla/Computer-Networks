#!/bin/bash
if [ "$#" == 3 ]; then
    java Sender "$1" "$2" "$3"
else
    echo "Illegal number of parameters ($#), should be 3"
fi