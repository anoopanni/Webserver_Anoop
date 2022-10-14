# Simple Makefile to Build and Start the webserver that returns Santa Clara University homepage. 
# 1) Build command -> make | make build
# 2) Start command -> make start | make run

build:  
	javac Server.java

start:
	java Server -document_root / -port 9000

run:
	java Server -document_root / -port 9000