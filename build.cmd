@echo off

echo "Clean Project ..."
call mvn clean -f pom.xml

echo "Build Project ..."
call mvn package -f pom.xml -D"maven.test.skip=true"

:exit
pause