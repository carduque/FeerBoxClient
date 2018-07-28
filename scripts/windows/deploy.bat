cd C:\Software\HCB\FeerBoxClient
copy target\classes\config.properties config\config.properties
copy target\classes\log4j.properties config\log4j.properties
git fetch origin
git reset --hard origin/master
call mvn clean install -Dmaven.test.skip=true
copy /Y config\config.properties target\classes\config.properties
copy /Y config\log4j.properties target\classes\log4j.properties