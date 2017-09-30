#!/bin/bash
if [ $(dpkg-query -W -f='${Status}' realvnc-vnc-server 2>/dev/null | grep -c "ok installed") -eq 0 ];
then
        echo "installing realvnc-vnc-server"
        sudo apt-get update
        sudo apt-get install realvnc-vnc-server
        echo "installed realvnc-vnc-server"
        sudo systemctl start vncserver-x11-serviced.service
        echo "realvnc-vnc-server service started on"
        ifconfig | sed -En 's/127.0.0.1//;s/.*inet (addr:)?(([0-9]*\.){3}[0-9]*).*/\2/p'
else
        if ps ax | grep -v grep | grep vncserver > /dev/null
        then
            echo "realvnc-vnc-server service already running on"
            ifconfig | sed -En 's/127.0.0.1//;s/.*inet (addr:)?(([0-9]*\.){3}[0-9]*).*/\2/p'
        else
            echo "realvnc-vnc-server is not running, starting"
            sudo systemctl start vncserver-x11-serviced.service
            echo "realvnc-vnc-server service started on"
            ifconfig | sed -En 's/127.0.0.1//;s/.*inet (addr:)?(([0-9]*\.){3}[0-9]*).*/\2/p'
        fi
fi
