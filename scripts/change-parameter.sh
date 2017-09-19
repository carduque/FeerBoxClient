#!/bin/bash
NEW=$1    
sed -i 's:^[ \t]*file.input[ \t]*=\([ \t]*.*\)$:file.input = '${NEW}':' /opt/FeerBoxClient/FeerBoxClient/target/classes/config.properties