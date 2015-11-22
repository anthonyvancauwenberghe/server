@echo off
:build
cls
javac -classpath libs/commons-codec-1.10.jar;libs/commons-compress-1.0.jar;libs/jsoup-1.8.3.jar;libs/junit-4.6.jar;libs/jython.jar;libs/mina-core-2.0.0-M6.jar;libs/mysql-connector.jar;libs/slf4j-api-1.5.8.jar;libs/slf4j-jdk14-1.5.8.jar;libs/sqlite-jdbc-3.7.2.jar;libs/worldmap2010.jar;libs/xpp.jar;libs/xstream.jar; -d bin .\org\hyperion\*.java .\org\hyperion\abuse\*.java .\org\hyperion\cache\*.java .\org\hyperion\cache\index\*.java .\org\hyperion\cache\index\impl\*.java .\org\hyperion\cache\map\*.java .\org\hyperion\cache\obj\*.java .\org\hyperion\cache\util\*.java .\org\hyperion\data\*.java .\org\hyperion\fileserver\*.java .\org\hyperion\ls\*.java .\org\hyperion\map\*.java .\org\hyperion\map\pathfinding\*.java .\org\hyperion\rs2\*.java .\org\hyperion\rs2\action\*.java .\org\hyperion\rs2\action\impl\*.java .\org\hyperion\rs2\commands\*.java .\org\hyperion\rs2\commands\impl\*.java .\org\hyperion\rs2\event\*.java .\org\hyperion\rs2\event\impl\*.java .\org\hyperion\rs2\login\*.java .\org\hyperion\rs2\model\*.java .\org\hyperion\rs2\model\achievements\*.java .\org\hyperion\rs2\model\challenge\*.java .\org\hyperion\rs2\model\challenge\cmd\*.java .\org\hyperion\rs2\model\cluescroll\*.java .\org\hyperion\rs2\model\cluescroll\requirement\*.java .\org\hyperion\rs2\model\cluescroll\reward\*.java .\org\hyperion\rs2\model\cluescroll\util\*.java .\org\hyperion\rs2\model\color\*.java .\org\hyperion\rs2\model\combat\*.java .\org\hyperion\rs2\model\combat\attack\*.java .\org\hyperion\rs2\model\combat\npclogs\*.java .\org\hyperion\rs2\model\combat\pvp\*.java .\org\hyperion\rs2\model\combat\summoning\*.java .\org\hyperion\rs2\model\combat\summoning\impl\*.java .\org\hyperion\rs2\model\combat\weapons\*.java .\org\hyperion\rs2\model\combat\weapons\impl\*.java .\org\hyperion\rs2\model\container\*.java .\org\hyperion\rs2\model\container\bank\*.java .\org\hyperion\rs2\model\container\duel\*.java .\org\hyperion\rs2\model\container\impl\*.java .\org\hyperion\rs2\model\content\*.java .\org\hyperion\rs2\model\content\bounty\*.java .\org\hyperion\rs2\model\content\bounty\place\*.java .\org\hyperion\rs2\model\content\bounty\rewards\*.java .\org\hyperion\rs2\model\content\checkers\*.java .\org\hyperion\rs2\model\content\clan\*.java .\org\hyperion\rs2\model\content\cluescroll\*.java .\org\hyperion\rs2\model\content\EP\*.java .\org\hyperion\rs2\model\content\ge\*.java .\org\hyperion\rs2\model\content\grandexchange\*.java .\org\hyperion\rs2\model\content\itfactivation\*.java .\org\hyperion\rs2\model\content\jge\*.java .\org\hyperion\rs2\model\content\jge\entry\*.java .\org\hyperion\rs2\model\content\jge\entry\claim\*.java .\org\hyperion\rs2\model\content\jge\entry\progress\*.java .\org\hyperion\rs2\model\content\jge\event\*.java .\org\hyperion\rs2\model\content\jge\itf\*.java .\org\hyperion\rs2\model\content\jge\tracker\*.java .\org\hyperion\rs2\model\content\minigame\*.java .\org\hyperion\rs2\model\content\minigame\barrowsffa\*.java .\org\hyperion\rs2\model\content\minigame\poker\*.java .\org\hyperion\rs2\model\content\minigame\poker\card\*.java .\org\hyperion\rs2\model\content\misc\*.java .\org\hyperion\rs2\model\content\misc2\*.java .\org\hyperion\rs2\model\content\misc2\teamboss\*.java .\org\hyperion\rs2\model\content\misc2\teamboss\impl\*.java .\org\hyperion\rs2\model\content\polls\*.java .\org\hyperion\rs2\model\content\pvptasks\*.java .\org\hyperion\rs2\model\content\pvptasks\impl\*.java .\org\hyperion\rs2\model\content\quest\*.java .\org\hyperion\rs2\model\content\randomgame\*.java .\org\hyperion\rs2\model\content\skill\*.java .\org\hyperion\rs2\model\content\skill\agility\*.java .\org\hyperion\rs2\model\content\skill\agility\courses\*.java .\org\hyperion\rs2\model\content\skill\agility\obstacles\*.java .\org\hyperion\rs2\model\content\skill\crafting\*.java .\org\hyperion\rs2\model\content\skill\dungoneering\*.java .\org\hyperion\rs2\model\content\skill\dungoneering\reward\*.java .\org\hyperion\rs2\model\content\skill\fletching\*.java .\org\hyperion\rs2\model\content\skill\slayer\*.java .\org\hyperion\rs2\model\content\specialareas\*.java .\org\hyperion\rs2\model\content\specialareas\impl\*.java .\org\hyperion\rs2\model\content\ticket\*.java .\org\hyperion\rs2\model\content\transport\*.java .\org\hyperion\rs2\model\customtrivia\*.java .\org\hyperion\rs2\model\customtrivia\cmd\*.java .\org\hyperion\rs2\model\iteminfo\*.java .\org\hyperion\rs2\model\itf\*.java .\org\hyperion\rs2\model\itf\impl\*.java .\org\hyperion\rs2\model\joshyachievementsv2\*.java .\org\hyperion\rs2\model\joshyachievementsv2\constraint\*.java .\org\hyperion\rs2\model\joshyachievementsv2\constraint\impl\*.java .\org\hyperion\rs2\model\joshyachievementsv2\io\*.java .\org\hyperion\rs2\model\joshyachievementsv2\reward\*.java .\org\hyperion\rs2\model\joshyachievementsv2\reward\impl\*.java .\org\hyperion\rs2\model\joshyachievementsv2\sql\*.java .\org\hyperion\rs2\model\joshyachievementsv2\task\*.java .\org\hyperion\rs2\model\joshyachievementsv2\task\impl\*.java .\org\hyperion\rs2\model\joshyachievementsv2\tracker\*.java .\org\hyperion\rs2\model\joshyachievementsv2\utils\*.java .\org\hyperion\rs2\model\log\*.java .\org\hyperion\rs2\model\log\cmd\*.java .\org\hyperion\rs2\model\log\util\*.java .\org\hyperion\rs2\model\newcombat\*.java .\org\hyperion\rs2\model\possiblehacks\*.java .\org\hyperion\rs2\model\punishment\*.java .\org\hyperion\rs2\model\punishment\cmd\*.java .\org\hyperion\rs2\model\punishment\event\*.java .\org\hyperion\rs2\model\punishment\holder\*.java .\org\hyperion\rs2\model\punishment\manager\*.java .\org\hyperion\rs2\model\recolor\*.java .\org\hyperion\rs2\model\recolor\cmd\*.java .\org\hyperion\rs2\model\recolor\pattern\*.java .\org\hyperion\rs2\model\recolor\save\*.java .\org\hyperion\rs2\model\region\*.java .\org\hyperion\rs2\model\sets\*.java .\org\hyperion\rs2\model\shops\*.java .\org\hyperion\rs2\net\*.java .\org\hyperion\rs2\net\ondemand\*.java .\org\hyperion\rs2\net\security\*.java .\org\hyperion\rs2\packet\*.java .\org\hyperion\rs2\pf\*.java .\org\hyperion\rs2\saving\*.java .\org\hyperion\rs2\saving\impl\*.java .\org\hyperion\rs2\saving\instant\*.java .\org\hyperion\rs2\saving\instant\impl\*.java .\org\hyperion\rs2\sql\*.java .\org\hyperion\rs2\sql\event\*.java .\org\hyperion\rs2\sql\event\impl\*.java .\org\hyperion\rs2\sql\requests\*.java .\org\hyperion\rs2\task\*.java .\org\hyperion\rs2\task\impl\*.java .\org\hyperion\rs2\util\*.java .\org\hyperion\rs2\util\rssfeed\*.java .\org\hyperion\util\*.java .\org\hyperion\util\login\*.java 
pause
goto :build
