#!/bin/bash
service lighttpd status

OPTS=`getopt -o ed: --long enable,disable: -n 'parse-options' -- "$@"`
if [ $? != 0 ] ; then echo "Failed parsing options." >&2 ; exit 1 ; fi
ENABLE_WEB_ADMIN=false
NOTHING=false
if [ $? == 0 ] ; then NOTHING=true; fi
eval set -- "$OPTS"
while true; do
  case "$1" in
    -e | --enable ) ENABLE_WEB_ADMIN=true; shift ;;
    -d | --disable ) ENABLE_WEB_ADMIN=false; shift ;;
    -- ) shift; break ;;
    * ) break ;;
  esac
done

if $NOTHING; then
	if $ENABLE_WEB_ADMIN; then
		sudo service lighttpd start
	else
		sudo service lighttpd stop
	fi
	service lighttpd status
fi
