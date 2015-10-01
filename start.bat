@echo off

set MYCLASSPATH=bin/gmail_notifier.jar
REM set MYCLASSPATH=../bin
set MYCLASSPATH=%MYCLASSPATH%;config
set MYCLASSPATH=%MYCLASSPATH%;lib/log4j-1.2.15.jar
set MYCLASSPATH=%MYCLASSPATH%;lib/skype_full.jar

set MAINCLASS=org.abelson.gmailnotifier.Main

java -classpath %MYCLASSPATH% %MAINCLASS%
