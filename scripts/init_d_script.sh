#! /bin/sh
# /etc/init.d/feerbox.sh 

### BEGIN INIT INFO
# Provides:          feerbox.sh
# Required-Start:    $remote_fs $syslog
# Required-Stop:     $remote_fs $syslog
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
# Short-Description: Simple script to start a program at boot
# Description:       A simple script from www.stuffaboutcode.com which will start / stop a program a boot / shutdown.
### END INIT INFO

# If you want a command to always run, put it here

# Carry out specific functions when asked to by the system
case "$1" in
  start)
    echo "Starting feerbox"
    # run application you want to start
    /opt/FeerBoxClient/FeerBoxClient/scripts/feerbox-start-mvn.sh
    ;;
  stop)
    echo "Stopping feerbox"
    # kill application you want to stop
    /opt/FeerBoxClient/FeerBoxClient/scripts/feerbox-stop.sh
    ;;
  *)
    echo "Usage: /etc/init.d/feerbox.sh {start|stop}"
    exit 1
    ;;
esac

exit 0