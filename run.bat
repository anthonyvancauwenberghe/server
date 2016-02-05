@echo off
title ArteroPk
:her5
java -Xmx5900m -cp bin;libs/jdbi-2.70.jar;libs/sqlite-jdbc-3.7.2.jar;libs/worldmap2010.jar;libs/mysql-connector.jar;libs/highscores.jar;libs/jython.jar;libs/xstream.jar;libs/xpp.jar;libs/slf4j-api-1.5.8.jar;libs/mina-core-2.0.0-M6.jar;libs/slf4j-jdk14-1.5.8.jar;libs/commons-compress-1.0.jar;libs/junit-4.6.jar org.hyperion.Server local
pause