@echo off
rem
rem Command invocation is: 
rem
rem       jinsightlive.bat <tracefile_name>
rem 
rem       where tracefile_name is the name of a trace file previously 
rem       captured and you wish to view the existing information.
rem 
rem Edit the following three environment variables if changes are needed:
rem
rem  Set the location of the java executable
set  "JAVA2_HOME=c:\Program Files\IBM\Java142\jre"
rem 
rem  Set the location of jinsightLive.jar  
set  "JINSIGHT_HOME=c:\zJinsight\jinsightLive"
rem
rem  Set JINSIGHT_HEAP to about 80% of your actual memory, if loading large traces:
set  JINSIGHT_HEAP=768M
rem
rem  Optional system variables.
rem  The following variables can be used to set connect to the jvm and save the data:
rem     The host name where the application is to be profiled (LIVE_SERVER)
rem     The port number being used by the profiling agent  (LIVE_PORT)
rem     The name of the trace file to be used for this invocation (LIVE_TO_FILE)
rem  USAGE NOTES:  
rem      1) The LIVE_SERVER and LIVE_PORT must both be specified to connect 
rem         automatically to the JVM.  If you want to save the trace then you must
rem         also specify LIVE_TO_FILE, it cannot be set once the visualizer is connected.
rem      2) Do NOT use these system properties if you want to view an existing trace file.
rem         Use the file name to be read as an argument to the visualizer.
rem
rem set "LIVE_SERVER=yourhostname"
rem set "LIVE_PORT=yoursocketportnumber"
rem set "LIVE_TO_FILE=yourtrace.trc"
rem
rem
rem Build the LIVE_VALUES string if any of above are set.
set LIVE_VALUES=
IF "%LIVE_SERVER%X" == "X" GOTO ONE
set "LIVE_VALUES=%LIVE_VALUES% -DLIVE_SERVER=%LIVE_SERVER%"
set LIVE_SERVER=
:ONE
IF "%LIVE_PORT%X" == "X" GOTO TWO
set "LIVE_VALUES=%LIVE_VALUES% -DLIVE_PORT=%LIVE_PORT%"
set LIVE_PORT=
:TWO
if "%LIVE_TO_FILE%X" == "X" GOTO THREE
set "LIVE_VALUES=%LIVE_VALUES% -DLIVE_TO_FILE=%LIVE_TO_FILE%"
set LIVE_TO_FILE=
:THREE
rem echo %LIVE_VALUES%
rem
rem Now invoke the java launcher to start the visualizer.
@echo on
"%JAVA2_HOME%\bin\java"  -Xmx%JINSIGHT_HEAP%  -classpath "%JINSIGHT_HOME%\jinsightLive.jar"  -DJINSIGHT_MAXHEAP=%JINSIGHT_HEAP% %LIVE_VALUES%   jinsight.main.Jinsight  %1
