#!/bin/bash
service lighttpd status

if [ $1 = "start" ]; then
	sudo service lighttpd start
fi
if [ $1 = "stop" ]; then
	sudo service lighttpd stop
fi
if [ $1 = "restart" ]; then
	sudo service lighttpd restart
fi

service lighttpd status
