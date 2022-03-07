#!/bin/sh
#
#
#  Edit the following three environment variables if changes are needed:
#
#  Set the location of the java executable
JAVA2_HOME="/opt/openjdk-bin-11"
#JAVA2_HOME="/root/jdk-16.0.2+7"
# 
#  Set the location of jinsightLive.jar  
JINSIGHT_HOME="/root/ToBeOrganised"
#
#  Set JINSIGHT_HEAP to about 80% of your actual memory, if loading large traces:
JINSIGHT_HEAP=4768M
#
#  Optional system variables.
#  The following variables can be used to set connect to the jvm and save the data:
#     The host name where the application is to be profiled (LIVE_SERVER)
#     The port number being used by the profiling agent  (LIVE_PORT)
#     The name of the trace file to be used for this invocation (LIVE_TO_FILE)
#  USAGE NOTES:  
#      1) The LIVE_SERVER and LIVE_PORT must both be specified to connect 
#         automatically to the JVM.  If you want to save the trace then you must
#         also specify LIVE_TO_FILE, it cannot be set once the visualizer is connected.
#      2) Do NOT use these system properties if you want to view an existing trace file.
#         Use the file name to be read as an argument to the visualizer.
#
# LIVE_SERVER="yourhostname"
# LIVE_PORT="yoursocketportnumber"
# LIVE_TO_FILE="yourtrace.trc"
#
#
# Build the LIVE_VALUES string if any of above are set.
#
if  [[ ! -z $LIVE_SERVER ]] 
then
   LIVE_VALUES="${LIVE_VALUES} -DLIVE_SERVER=${LIVE_SERVER}"
fi
#
if [[ ! -z $LIVE_PORT ]]
then
   LIVE_VALUES="${LIVE_VALUES} -DLIVE_PORT=${LIVE_PORT}"
fi
#
if [[ ! -z $LIVE_TO_FILE ]]
then
   LIVE_VALUES="${LIVE_VALUES} -DLIVE_TO_FILE=${LIVE_TO_FILE}"
fi
#
echo ${LIVE_VALUES}
#
# Now invoke the java launcher to start the visualizer.
#
${JAVA2_HOME}/bin/java  -Xmx${JINSIGHT_HEAP}  -classpath ${JINSIGHT_HOME}/newJinsight.jar -DJINSIGHT_MAXHEAP=${JINSIGHT_HEAP} ${LIVE_VALUES} jinsight.main.Jinsight  "$@"
#
