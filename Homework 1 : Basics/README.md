# Homework 1 : Basic Socket Programming

Netem:

`sudo tc qdisc add dev <name of ethernet adapter> root netem delay <mean> ms <std-dev> ms distribution normal loss <percent value>%`

1. On the client side, run
	
	`make
	java client <IP Address of the system on which echo.java is run>`

2. On the server side, run
	
	`make
	java echo`

* The readings have been taken on two Ubuntu systems connected to Ethernet.
* The files are saved as "Output_with_netem\_&lt;T&gt;\_&lt;P&gt;.txt" or "Output_wo_netem\_&lt;T&gt;\_&lt;P&gt;.txt"		
