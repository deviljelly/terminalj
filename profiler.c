#pragma clang diagnostic push
#pragma ide diagnostic ignored "EmptyDeclOrStmt"
/*
 * profiler.c
 *
 *  Created on: 08.09.2015
 *      Author: F600308
 */

#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <unistd.h>
#include <string.h>
#include <strings.h>
#include <stdbool.h>
#include <malloc.h>
#include <pthread.h>
#include <time.h>
#include <errno.h>
#include <error.h>
#ifdef __WIN32__
#include <windows.h>
#endif
#ifdef __linux
#include <arpa/inet.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#endif
#include <inttypes.h>
#include "jvmti.h"
#include "classfile_constants.h"
#include "profiler.h"
#include "util.h"
#include "tables.h"
#include "semaphore.h"

uint32_t uniqueClassID = 1;
uint32_t uniqueObjectID = 1;
uint32_t uniqueThreadID = 1;
uint32_t traceFileNumber = 0;

#ifdef __WIN32__
//
#endif
#ifdef __linux
//
#endif

pid_t pid;

pthread_t controllerThread;
pthread_mutex_t fileLock;
pthread_mutexattr_t fileLockAttr;

jvmtiEnv *globalJVMTIInterface;
static JavaVM *jvm;
static FILE *traceFile;
static bool tagObjects;
static char *traceDirectory;
Buffer *globalBuffer;

uint8_t headerBinary = 'b';
uint32_t headerVersion = 8;
#ifdef __WIN32__
uint32_t headerPlatform = 11;
#endif
#ifdef __linux
uint32_t headerPlatform = 44;
#endif
uint32_t headerNumberOfEvents = 0;
uint32_t headerMaxThreads = 1024;
uint32_t headerMaxClasses = 16384;
uint32_t headerTicksPerMicrosecond = 2400;
uint64_t headerStartTicks = 0;
uint32_t headerVMStartTime = 0;
uint32_t headerConnectionStartTime = 0;
uint32_t headerOverhead = 0;

bool agentLoaded = true;

LockStructure profiling = UNLOCKED;
static Option *options = NULL;
static uint32_t optionCount = 0;


static uint64_t tagObjectsCost = 0;
static uint64_t startProfilingTime = 0;
static uint64_t stopProfilingTime = 0;


void getAllThreads(jvmtiEnv *jvmtiInterface, jint *numberOfThreads, jthread **threads);
void startProfiling(jvmtiEnv *jvmtiInterface, JNIEnv *jni_env);
void stopProfiling(jvmtiEnv *jvmtiInterface, JNIEnv *jni_env);
void rollTraceFile(jvmtiEnv *jvmtiInterface, JNIEnv *jni_env);
ClassNode* discoverClass(jvmtiEnv *jvmtiInterface, JNIEnv *jni_env, jclass class, jmethodID requestedMethod, MethodIDNode **returnedMethodIDNode);
void JNICALL MethodEntry(jvmtiEnv *jvmti_env, JNIEnv* jni_env, jthread thread, jmethodID method);
void JNICALL MethodExit(jvmtiEnv *jvmti_env, JNIEnv* jni_env, jthread thread, jmethodID method, jboolean was_popped_by_exception, jvalue return_value);
ThreadNode* discoverThread(jvmtiEnv *jvmtiInterface, jthread jvmtiThread);




void parseOptions(char *optionString) {

    if(optionString<=0) return;

    uint32_t length = strlen(optionString);

    if (length <= 0)
        return;

    uint32_t lastDelimiter = 0;

    for (int i = 0; i < length; i++) {
        if (optionString[i] == ';') {
            optionCount++;
            lastDelimiter = i;
        }
    }

    if (lastDelimiter <= length) {
        optionCount++;
    }

    options = calloc(optionCount, sizeof(Option));

    uint32_t from = 0;
    uint32_t to = 0;
    uint32_t optionNumber = 0;

    for (int i = 0; i < length; i++) {

        if (optionString[i] == ',') {
            to = i;
            uint32_t rawOptionLength = (to - from);
            uint8_t *rawOption = calloc(1, rawOptionLength + 1);
            memcpy(rawOption, optionString + from, rawOptionLength);
            options[optionNumber++].rawOption = rawOption;
            from = to + 1;

        } else if (i == (length - 1)) {
            to = i;
            uint32_t rawOptionLength = (to - from) + 1;
            uint8_t *rawOption = calloc(1, rawOptionLength + 1);
            memcpy(rawOption, optionString + from, rawOptionLength);
            options[optionNumber++].rawOption = rawOption;

        }

    }

    for (int i = 0; i < optionCount; i++) {

        bool hasValue = false;

        uint8_t *rawOption = options[i].rawOption;

        uint32_t rawOptionLength = strlen((char*) rawOption);

        for (uint32_t j = 0; j < rawOptionLength; j++) {

            if (rawOption[j] == '=') {

                hasValue = true;

                uint32_t nameFrom = 0;
                uint32_t nameTo = j;
                uint32_t valueFrom = j + 1;
                uint32_t valueTo = rawOptionLength;

                uint32_t nameLength = nameTo - nameFrom;
                uint32_t valueLength = valueTo - valueFrom;

                uint8_t *optionName = calloc(1, nameLength + 1);
                memcpy(optionName, rawOption + nameFrom, nameLength);
                options[i].optionName = optionName;

                uint8_t *optionValue = calloc(1, valueLength + 1);
                memcpy(optionValue, rawOption + valueFrom, valueLength);
                options[i].optionValue = optionValue;

                j = rawOptionLength;
            }

        }

        if (!hasValue) {
            options[i].optionName = options[i].rawOption;
        }

    }

    for (int i = 0; i < optionCount; i++) {
        debug("Option: %d: raw:%s name:%s value:%s\n", i+1, options[i].rawOption, options[i].optionName, options[i].optionValue);
    }

}

Option* getOption(char *optionName) {

    debug("looking for %s\n", optionName);

    for (int i = 0; i < optionCount; i++) {

        if(options[i].optionName) {

            debug("comparing  %s\n", options[i].optionName);

            if (strcasecmp((const char*) options[i].optionName, (const char*) optionName) == 0) {
                debug("Returning Option: raw:%s name:%s value:%s\n", options[i].rawOption, options[i].optionName, options[i].optionValue);
                return &options[i];
            }

        }

    }

    return NULL;

}

Buffer *allocateBuffer(uint32_t bufferLength, bool shared) {

    Buffer *buffer = calloc(1, sizeof(Buffer));
    if (buffer <= 0) {
        error("Unable to allocate buffer\n");
        exit(-1);
    }

    buffer->buffer = calloc(1, bufferLength);
    if (buffer->buffer <= 0) {
        error("Unable to allocate buffer\n");
        exit(-1);
    }

    buffer->bufferOffset = 0;
    buffer->bufferLength = bufferLength;
    buffer->shared = shared;
    return buffer;

}

#ifdef __linux
void networkCleanup(void *arg) {
    int *serverSocket = (int*) arg;
    if(serverSocket) {
        if(jvm) {
            (*jvm)->DetachCurrentThread(jvm);
        }
        close(*serverSocket);
        *serverSocket=0;
    }

}
#endif

#ifdef __linux
void* networkController(void *arg) {

    debug("Network Controller Thread\n");

    int previousState = 0;
    pthread_setcancelstate(PTHREAD_CANCEL_DISABLE, &previousState);

    JavaVM *vm = (JavaVM*) arg;

    JNIEnv *JNIInterface;

    jint jniReturnCode;
    jniReturnCode = (*vm)->AttachCurrentThreadAsDaemon(vm, (void **) &JNIInterface, NULL);
    if (jniReturnCode != JNI_OK) {
        error("Unable to attach to the JVM, network controller unavailable (%d)\n", jniReturnCode);
        return NULL;
    }

    jvmtiError returnCode;
    jthread *currentThread = NULL;

    returnCode = (*globalJVMTIInterface)->GetCurrentThread(globalJVMTIInterface, currentThread);
    if (jniReturnCode != JNI_OK) {
        error("Error getting the current thread, network controller unavailable (%d)\n", returnCode);
        return NULL;
    }

    ThreadNode *threadNode = calloc(1, sizeof(ThreadNode));

    threadNode->threadID = -1;

    returnCode = (*globalJVMTIInterface)->SetThreadLocalStorage(globalJVMTIInterface, *currentThread, threadNode);
    if (jniReturnCode != JNI_OK) {
        error("Error setting thread local storage, network controller unavailable (%d)\n", returnCode);
        return NULL;
    }

    int serverSocket;
    int clientSocket;

    struct sockaddr_in serverAddress;
    struct sockaddr_in clientAddress;

    int ENABLE = 1;
    int BUFFER_SIZE = 16;

    serverSocket = socket(AF_INET, SOCK_STREAM, 0);

    if (serverSocket == -1) {
        error("Error creating server socket, network controller unavailable (%s)\n", strerror(errno));
        return NULL;
    }

    returnCode = setsockopt(serverSocket, SOL_SOCKET, SO_REUSEADDR, (char *) &ENABLE, sizeof(int));

    if (returnCode == -1) {
        error("Error creating server socket, network controller unavailable (%s)\n", strerror(errno));
        return NULL;
    }

    serverAddress.sin_family = AF_INET;
    serverAddress.sin_port = htons(12345);
    serverAddress.sin_addr.s_addr = INADDR_ANY;
    memset(&(serverAddress.sin_zero), 0, 8);

    debug("Binding Socket\n");

    returnCode = bind(serverSocket, (struct sockaddr*) &serverAddress, sizeof(serverAddress));
    if (returnCode == -1) {
        error("Error binding server socket, network controller unavailable (%s)\n", strerror(errno));
        return NULL;
    }

    debug("Listening on Socket\n");

    returnCode = listen(serverSocket, 2);

    if (returnCode == -1) {
        error("Error listening on server socket, network controller unavailable (%s)\n", strerror(errno));
        return NULL;
    }

    pthread_setcancelstate(PTHREAD_CANCEL_ENABLE, &previousState);

    pthread_cleanup_push((networkCleanup), (void*) &serverSocket);

    while (agentLoaded) {

        uint32_t structSize = sizeof(struct sockaddr_in);

        debug("Acceping Connection\n");

        clientSocket = accept(serverSocket, (struct sockaddr*) &clientAddress, &structSize);

        if (clientSocket != -1) {

            char *buffer = calloc(1, BUFFER_SIZE);
            ssize_t bytesReceived = recv(clientSocket, buffer, BUFFER_SIZE, 0);
            if (bytesReceived != -1) {

                int command = (int) buffer[0];

                switch (command) {

                    case 1:
                        startProfiling(globalJVMTIInterface, JNIInterface);
                        break;
                    case 2:
                        stopProfiling(globalJVMTIInterface, JNIInterface);
                        break;
                    case 3:
                        rollTraceFile(globalJVMTIInterface, JNIInterface);
                        break;
                    default:
                        break;
                }

                char message[1];
                message[0] = 9;
                send(clientSocket, message, 1, 0);
            } else {
                error("Error receiving data from the client (%s)\n", strerror(errno));
            }

            } else {
                error("Error accepting connection from the client (%s)\n", strerror(errno));
            }

            pthread_testcancel();
        }
        pthread_cleanup_pop(1);
        return 0;
    }
#endif

#ifdef __WIN32__
void eventCleanup(void *arg) {

	EventCleanupStruct *eventCleanupStruct = arg;

	if (eventCleanupStruct) {

		(*jvm)->DetachCurrentThread(jvm);
		CloseHandle(eventCleanupStruct->startEvent);
		CloseHandle(eventCleanupStruct->stopEvent);
		CloseHandle(eventCleanupStruct->rollEvent);
	}

}
#endif

#ifdef __linux
void pipeCleanup(void *arg) {

    if(arg) {

        int *pipe = (int*) arg;

        if(*pipe) {
            close(*pipe);
        }

        char *pipeName = calloc(1, 128);
        sprintf((char*) pipeName, "/tmp/prfctl-%d", pid);
        unlink(pipeName);

    }

}
#endif

#ifdef __linux
void* pipeController(void *arg) {

    info("Starting the Controller Thread\n");

    debug("1\n");

    int previousState = 0;
    pthread_setcancelstate(PTHREAD_CANCEL_DISABLE, &previousState);

    JavaVM *vm = (JavaVM*) arg;

    JNIEnv *JNIInterface;

    jint jniReturnCode;
    jniReturnCode = (*vm)->AttachCurrentThreadAsDaemon(vm, (void **) &JNIInterface, NULL);
    if (jniReturnCode != JNI_OK) {
        error("Unable to attach to the JVM, network controller unavailable (%d)\n", jniReturnCode);
        return NULL;
    }
    debug("2\n");

    jvmtiError returnCode;
    jthread currentThread = calloc(1, sizeof(jthread));;

    returnCode = (*globalJVMTIInterface)->GetCurrentThread(globalJVMTIInterface, &currentThread);
    if (jniReturnCode != JNI_OK) {
        error("Error getting the current thread, network controller unavailable (%d)\n", returnCode);
        return NULL;
    }


    ThreadNode *threadNode = calloc(1, sizeof(ThreadNode));

    threadNode->threadID = -1;
    debug("3\n");

    debug("%p %p %p\n", globalJVMTIInterface, currentThread, threadNode);

    returnCode = (*globalJVMTIInterface)->SetThreadLocalStorage(globalJVMTIInterface, currentThread, threadNode);
    if (jniReturnCode != JNI_OK) {
        error("Error setting thread local storage, network controller unavailable (%d)\n", returnCode);
        return NULL;
    }

    pthread_setcancelstate(PTHREAD_CANCEL_ENABLE, &previousState);
    debug("4\n");

    char *pipeName = calloc(1, 128);
    sprintf((char*) pipeName, "/tmp/prfctl-%d", pid);

    int *pipe = calloc(1, sizeof(int));

    mkfifo(pipeName, 0666);
    debug("5\n");

    pthread_cleanup_push((pipeCleanup), (void*) pipe);

            while(agentLoaded) {

                *pipe = open(pipeName, O_RDONLY);

                char buf[1];
                buf[0]=0;

                uint64_t ret = read(*pipe, &buf, 1);
                if(ret==1) {
                    int command = (int)buf[0];

                    switch (command) {
                        case 1:
                            startProfiling(globalJVMTIInterface, JNIInterface);
                            break;
                        case 2:
                            stopProfiling(globalJVMTIInterface, JNIInterface);
                            break;
                        case 3:
                            rollTraceFile(globalJVMTIInterface, JNIInterface);
                            break;
                        default:
                            break;
                    }
                }
                close(*pipe);
                *pipe = 0;
                pthread_testcancel();
            }

    pthread_cleanup_pop(1);
    return 0;
}
#endif

#ifdef __WIN32__
void* eventController(void *arg) {

	debug("Starting the Controller Thread\n");

	int previousState = 0;
	pthread_setcancelstate(PTHREAD_CANCEL_DISABLE, &previousState);

	JavaVM *vm = (JavaVM*) arg;

	JNIEnv *JNIInterface;

	jint jniReturnCode;
	jniReturnCode = (*vm)->AttachCurrentThreadAsDaemon(vm, (void **) &JNIInterface, NULL);
	if (jniReturnCode != JNI_OK) {
		error("Unable to attach to the JVM, network controller unavailable (%d)\n", (uint32_t )jniReturnCode);
		return NULL;
	}

	jvmtiError returnCode;
	jthread currentThread;

	returnCode = (*globalJVMTIInterface)->GetCurrentThread(globalJVMTIInterface, &currentThread);
	if (jniReturnCode != JNI_OK) {
		error("Error getting the current thread, network controller unavailable (%d)\n", returnCode);
		return NULL;
	}

	ThreadNode *threadNode = calloc(1, sizeof(ThreadNode));
	if (threadNode <= 0) {
		error("Unable to allocate ThreadNode\n");
		exit(-1);
	}

	threadNode->threadID = -1;

	returnCode = (*globalJVMTIInterface)->SetThreadLocalStorage(globalJVMTIInterface, currentThread, threadNode);
	if (jniReturnCode != JNI_OK) {
		error("Error setting thread local storage, network controller unavailable (%d)\n", returnCode);
		return NULL;
	}

	pthread_setcancelstate(PTHREAD_CANCEL_ENABLE, &previousState);

	char *startEventName = calloc(1, 128);
	char *stopEventName = calloc(1, 128);
	char *rollEventName = calloc(1, 128);
	if (startEventName <= 0 || stopEventName <= 0|| rollEventName <= 0) {
		error("Unable to allocate EventNames\n");
		exit(-1);
	}

	sprintf((char*) startEventName, "Global\\profilerStart-%d", (uint32_t) pid);
	sprintf((char*) stopEventName, "Global\\profilerStop-%d", (uint32_t) pid);
	sprintf((char*) rollEventName, "Global\\profilerRoll-%d", (uint32_t) pid);

	debug("%s\n", startEventName);
	debug("%s\n", stopEventName);
	debug("%s\n", rollEventName);

	HANDLE startEvent = CreateEvent(NULL, TRUE, FALSE, startEventName);

	HANDLE stopEvent = CreateEvent(NULL, TRUE, FALSE, stopEventName);

	HANDLE rollEvent = CreateEvent(NULL, TRUE, FALSE, rollEventName);

	EventCleanupStruct eventCleanupStruct;

	eventCleanupStruct.startEvent = startEvent;
	eventCleanupStruct.stopEvent = stopEvent;
	eventCleanupStruct.rollEvent = rollEvent;

	pthread_cleanup_push((eventCleanup), (void*) &eventCleanupStruct)
		;

//		bool profiling = false;


		HANDLE events[3] = {startEvent, stopEvent, rollEvent};

		while (1) {

			DWORD dwEvent;

			dwEvent  = WaitForMultipleObjects(3, events, FALSE, INFINITE);

			 switch (dwEvent)
			    {
			        case WAIT_OBJECT_0 + 0:
					    ResetEvent(startEvent);
					    startProfiling(globalJVMTIInterface, JNIInterface, NULL);
			            break;

			        case WAIT_OBJECT_0 + 1:
						ResetEvent(stopEvent);
						stopProfiling(globalJVMTIInterface, JNIInterface);
			            break;

			        case WAIT_OBJECT_0 + 2:
						ResetEvent(rollEvent);
						rollTraceFile(globalJVMTIInterface, JNIInterface);
			            break;

			        default:
			            printf("Wait error: %ld\n", GetLastError());
			    }


			pthread_testcancel();
		}

		pthread_cleanup_pop(1);
}
#endif

void getAllThreads(jvmtiEnv *jvmtiInterface, jint *numberOfThreads, jthread **threads) {
    jvmtiError returnCode;

    returnCode = (*jvmtiInterface)->GetAllThreads(jvmtiInterface, numberOfThreads, threads);
    if (returnCode != JNI_OK) {
        error("Error in GetAllThreads (%d)\n", returnCode);
    }
}

void reportThreadStatistics(jvmtiEnv *jvmtiInterface, jint numberOfThreads, jthread *threads) {

    uint64_t cacheHits = 0;
    uint64_t cacheMisses = 0;

    for (int i = 0; i < numberOfThreads; i++) {

        jvmtiError returnCode;
        ThreadNode *threadNode;

        returnCode = (*jvmtiInterface)->GetThreadLocalStorage(jvmtiInterface, threads[i], (void **) &threadNode);
        if (returnCode != JNI_OK) {
            error("Unable to GetThreadLocalStorage (%d)\n", returnCode);
        }

        if (threadNode) {

            cacheHits += threadNode->cacheHits;
            cacheMisses += threadNode->cacheMisses;

        }

    }

    info("Threads: %d, Cache Hits: %" PRIu64 ", Cache Misses: %" PRIu64 "\n", (uint32_t )numberOfThreads, cacheHits, cacheMisses);

}


void openTraceFile(const char *fileName) {

    traceFile = fopen(fileName, "wb");

    if (traceFile <= 0) {
        error("Unable to open trace file (%s) (%s)\n", fileName, strerror(errno));
    }

}


void flushGlobalBuffer(bool mustLock) {

    if (mustLock)
        lock(&globalBuffer->lock);

    if (globalBuffer->buffer) {

        uint32_t returnCode;

        returnCode = pthread_mutex_lock(&fileLock);

        if (returnCode) {
            if (mustLock)
                unlock(&globalBuffer->lock);
            return;
        }

        size_t written = fwrite(globalBuffer->buffer, 1, globalBuffer->bufferOffset, traceFile);

        if (written != globalBuffer->bufferOffset) {

        }

        returnCode = pthread_mutex_unlock(&fileLock);

        if (returnCode) {
            if (mustLock)
                unlock(&globalBuffer->lock);
            return;
        }

        globalBuffer->bufferOffset = 0;

    }

    if (mustLock)
        unlock(&globalBuffer->lock);

}

void flushBuffer(Buffer *buffer) {

    pthread_mutex_lock(&fileLock);

    size_t written = fwrite(buffer->buffer, 1, buffer->bufferOffset, traceFile);

    if (written != buffer->bufferOffset) {

    }

    pthread_mutex_unlock(&fileLock);

    buffer->bufferOffset = 0;

}

void flushBuffers(jvmtiEnv *jvmtiInterface, jint numberOfThreads, jthread *threads) {

    debug("flushBuffers");

    flushBuffer(globalBuffer);

    for (int i = 0; i < numberOfThreads; i++) {

        jvmtiError returnCode;
        ThreadNode *threadNode;

        returnCode = (*jvmtiInterface)->GetThreadLocalStorage(jvmtiInterface, threads[i], (void **) &threadNode);
        if (returnCode != JNI_OK) {
            error("Unable to GetThreadLocalStorage (%d)\n", returnCode);
        }

        if (threadNode) {
            if (threadNode->threadBuffer) {
                flushBuffer(threadNode->threadBuffer);
            }
        }

    }

}


void writeUint8_t(Buffer *buffer, uint8_t value) {

    buffer->buffer[buffer->bufferOffset++] = value;

}


void writeUint16_t(Buffer *buffer, uint16_t value) {

    uint16_t* castBuffer = (uint16_t*)(buffer->buffer+buffer->bufferOffset);

    *castBuffer = value;

    buffer->bufferOffset+=sizeof(uint16_t);

}

void writeUint32_t(Buffer *buffer, uint32_t value) {

    uint32_t* castBuffer = (uint32_t*)(buffer->buffer+buffer->bufferOffset);

    *castBuffer = value;

    buffer->bufferOffset+=sizeof(uint32_t);
}


void writeUint64_t(Buffer *buffer, uint64_t value) {

    uint64_t* castBuffer = (uint64_t*)(buffer->buffer+buffer->bufferOffset);

    *castBuffer = value;

    buffer->bufferOffset+=sizeof(uint64_t);

}


void writeString(Buffer *buffer, char *string) {

    if (string) {
        debug("Write String (%s)\n", string);
        uint32_t length = strlen(string);
        writeUint16_t(buffer, (uint16_t) length);
        if (length) {
            memcpy(buffer->buffer + buffer->bufferOffset, string, length);
            buffer->bufferOffset += length;
        }
    }

}

void writeDefaultHeader(Buffer *buffer) {

    writeUint8_t(buffer, headerBinary);
    writeUint32_t(buffer, headerVersion);
    writeUint32_t(buffer, headerPlatform);
    writeUint32_t(buffer, headerNumberOfEvents);
    writeUint32_t(buffer, headerMaxThreads);
    writeUint32_t(buffer, headerMaxClasses);
    writeUint32_t(buffer, headerTicksPerMicrosecond);
    writeUint64_t(buffer, getTicks());
    writeUint32_t(buffer, time(NULL));
    writeUint32_t(buffer, time(NULL));
    writeUint32_t(buffer, 0);

}

void writeBeginBurst(Buffer *buffer) {
    debug("Write Begin Burst\n");
    if (buffer->shared) {
        lock(&buffer->lock);
    }

    uint32_t length = sizeof(uint8_t) + sizeof(uint32_t);

    if (buffer->bufferOffset + length >= buffer->bufferLength) {
        flushGlobalBuffer(false);
        flushBuffer(buffer);
    }

    writeUint8_t(buffer, EVENT_BEGIN_BURST);
    writeUint64_t(buffer, getTicks());

    if (buffer->shared) {
        unlock(&buffer->lock);
    }

}

void writeEndBurst(Buffer *buffer) {
    debug("Write End Burst\n");

    if (buffer->shared) {
        lock(&buffer->lock);
    }

    uint32_t length = sizeof(uint8_t) + sizeof(uint32_t);

    if (buffer->bufferOffset + length >= buffer->bufferLength) {
        flushGlobalBuffer(false);
        flushBuffer(buffer);
    }

    writeUint8_t(buffer, EVENT_END_BURST);
    writeUint64_t(buffer, getTicks());

    if (buffer->shared) {
        unlock(&buffer->lock);
    }

}

void writeEndFile(Buffer *buffer) {
    debug("Write End File\n");

    if (buffer->shared) {
        lock(&buffer->lock);
    }

    uint32_t length = sizeof(uint8_t);

    if (buffer->bufferOffset + length >= buffer->bufferLength) {
        flushGlobalBuffer(false);
        flushBuffer(buffer);
    }

    writeUint8_t(buffer, EVENT_END_FILE);
    writeUint32_t(buffer, 5705);
    writeUint32_t(buffer, 0xadde0000);

    if (buffer->shared) {
        unlock(&buffer->lock);
    }

}

void writeObject(Buffer *buffer, uint32_t objectID, uint16_t classID) {
    debug("Write Object\n");
    if (buffer->shared) {
        lock(&buffer->lock);
    }

    uint32_t length = sizeof(uint8_t) + sizeof(uint64_t) + sizeof(uint32_t) + sizeof(uint16_t);

    if (buffer->bufferOffset + length >= buffer->bufferLength) {
        flushGlobalBuffer(false);
    }

    writeUint8_t(buffer, EVENT_OBJECT_DEFINE);
    writeUint64_t(buffer, getTicks());
    writeUint32_t(buffer, objectID);
    writeUint16_t(buffer, classID);

    if (buffer->shared) {
        unlock(&buffer->lock);
    }

}

void writeThread(Buffer *buffer, ThreadNode *threadNode) {
    debug("Write Thread\n");

    if (buffer->shared) {
        lock(&buffer->lock);
    }

    debug("Write Thread 2\n");
    ClassNode *threadClassNode = getClassNode("Ljava/lang/Thread;");
    debug("Write Thread 2.1 %p\n", threadNode->name);
    debug("Write Thread 2.1.1 %s\n", threadNode->name);
    uint32_t length = sizeof(uint8_t) + sizeof(uint64_t) + sizeof(uint32_t) + sizeof(uint32_t) + sizeof(uint16_t) + strlen((const char*) threadNode->name);

    debug("Write Thread 2.2\n");
    if (buffer->bufferOffset + length >= buffer->bufferLength) {
        debug("Write Thread 2.3\n");
        flushGlobalBuffer(false);
    }

    debug("Write Thread 3\n");
    writeUint8_t(buffer, EVENT_THREAD_DEFINE);
    writeUint64_t(buffer, getTicks());
    writeUint32_t(buffer, threadNode->threadID);
    writeUint32_t(buffer, 0);
    writeUint16_t(buffer, threadClassNode->classID);
    writeString(buffer, (char*) threadNode->name);

    debug("Write Thread 4\n");
    if (buffer->shared) {
        unlock(&buffer->lock);
    }

    debug("Write Thread 5\n");
}

void writeMethodEntry(Buffer *buffer, uint32_t threadID, uint16_t classID, uint16_t methodID, uint32_t objectID, uint64_t ticks) {

    debug("Write Method Entry\n");

    uint32_t length = sizeof(uint8_t) + sizeof(uint64_t) + sizeof(uint32_t) + sizeof(uint16_t) + sizeof(uint16_t) + sizeof(uint32_t) + sizeof(uint16_t);

    if (buffer->bufferOffset + length >= buffer->bufferLength) {
        flushGlobalBuffer(true);
        flushBuffer(buffer);
    }

    writeUint8_t(buffer, EVENT_WIDE_METHOD_ENTER);
    writeUint64_t(buffer, ticks);
    writeUint32_t(buffer, threadID);
    writeUint16_t(buffer, classID);
    writeUint16_t(buffer, methodID);
    writeUint32_t(buffer, objectID);
    writeUint16_t(buffer, 0);

}

void writeMethodExit(Buffer *buffer, uint32_t threadID, uint64_t exitStart, uint64_t entryOverhead) {

    debug("Write Method Exit\n");

    uint32_t length = sizeof(uint8_t) + sizeof(uint64_t) + sizeof(uint64_t) + sizeof(uint32_t);

    if (buffer->bufferOffset + length >= buffer->bufferLength) {
        flushGlobalBuffer(true);
        flushBuffer(buffer);
    }

    writeUint8_t(buffer, EVENT_METHOD_LEAVE);
    writeUint64_t(buffer, exitStart);
    writeUint64_t(buffer, ((getTicks() - exitStart) + entryOverhead));
    writeUint32_t(buffer, threadID);

}

void writeClass(Buffer *buffer, ClassNode *classNode) {

    if (classNode == NULL) {
        return;
    }

    if (alreadyWritten(&classNode->written)) {
        return;
    }

    if (buffer->shared) {
        lock(&buffer->lock);
    }

    uint32_t length = sizeof(uint8_t) + sizeof(uint64_t) + sizeof(uint16_t) + sizeof(uint32_t);
    length += sizeof(uint8_t) + sizeof(uint64_t) + sizeof(uint16_t) + sizeof(uint16_t);
    length += sizeof(uint16_t) + strlen((const char*) classNode->profilerName);

    for (int i = 0; i < classNode->numberOfMethods; i++) {

        MethodInfo methodInfo = classNode->methods[i];
        length += sizeof(uint16_t) + strlen((const char*) methodInfo.name);
        length += sizeof(uint16_t) + strlen((const char*) methodInfo.signature);
        length += sizeof(uint16_t);

    }

    for (int i = 0; i < classNode->numberOfFields; i++) {

        FieldInfo fieldInfo = classNode->fields[i];
        length += sizeof(uint16_t) + strlen((const char*) fieldInfo.name);
        length += sizeof(uint16_t) + strlen((const char*) fieldInfo.signature);
        length += sizeof(uint16_t);

    }

    length += sizeof(uint16_t) * classNode->numberOfInterfaces;

    if (buffer->bufferOffset + length >= buffer->bufferLength) {
        flushGlobalBuffer(false);
    }

    bool hugeClass = false;

    if (length > buffer->bufferLength)
        hugeClass = true;

    uint64_t ticks = getTicks();

    debug("Write Class Define\n");
    writeUint8_t(buffer, EVENT_CLASS_DEFINE);
    writeUint64_t(buffer, ticks);
    writeUint16_t(buffer, classNode->classID);
    writeUint32_t(buffer, 0);

    debug("Write Class Load\n");
    writeUint8_t(buffer, EVENT_EXTENDED_EXTENSIVE_CLASS_LOAD);
    writeUint64_t(buffer, ticks);
    writeUint16_t(buffer, classNode->classID);
    writeUint16_t(buffer, 0);

    writeString(buffer, (char*) classNode->profilerName);

    writeUint16_t(buffer, classNode->numberOfMethods);

    for (int i = 0; i < classNode->numberOfMethods; i++) {

        MethodInfo methodInfo = classNode->methods[i];
        writeString(buffer, (char*) methodInfo.name);
        writeString(buffer, (char*) methodInfo.signature);
        writeUint16_t(buffer, methodInfo.modifiers);

        if (hugeClass) {
            if (buffer->bufferOffset + 1024 >= buffer->bufferLength) {
                flushGlobalBuffer(false);
            }
        }
    }

    writeUint16_t(buffer, classNode->numberOfFields);

    for (int i = 0; i < classNode->numberOfFields; i++) {

        FieldInfo fieldInfo = classNode->fields[i];
        writeString(buffer, (char*) fieldInfo.name);
        writeString(buffer, (char*) fieldInfo.signature);
        writeUint16_t(buffer, fieldInfo.modifiers);

        if (hugeClass) {
            if (buffer->bufferOffset + 1024 >= buffer->bufferLength) {
                flushGlobalBuffer(false);
            }
        }

    }

    writeUint16_t(buffer, classNode->superClassID);

    writeUint16_t(buffer, classNode->numberOfInterfaces);

    for (int i = 0; i < classNode->numberOfInterfaces; i++) {

        InterfaceInfo interfaceInfo = classNode->interfaces[i];
        writeUint16_t(buffer, interfaceInfo.classID);

    }

    if (buffer->shared) {
        unlock(&buffer->lock);
    }

}

void enableMainProfilingEvents(jvmtiEnv *jvmtiInterface) {

    jvmtiError returnCode;

    returnCode = (*jvmtiInterface)->SetEventNotificationMode(jvmtiInterface, JVMTI_ENABLE, JVMTI_EVENT_METHOD_ENTRY, (jthread) NULL);
    if (returnCode != JNI_OK) {
        error("Unable to enable event notification, JVMTI_EVENT_METHOD_ENTRY (%d)\n", returnCode);
    }

    returnCode = (*jvmtiInterface)->SetEventNotificationMode(jvmtiInterface, JVMTI_ENABLE, JVMTI_EVENT_METHOD_EXIT, (jthread) NULL);
    if (returnCode != JNI_OK) {
        error("Unable to enable event notification, JVMTI_EVENT_METHOD_EXIT (%d)\n", returnCode);
    }

}

void disableMainProfilingEvents(jvmtiEnv *jvmtiInterface) {

    jvmtiError returnCode;

    returnCode = (*jvmtiInterface)->SetEventNotificationMode(jvmtiInterface, JVMTI_DISABLE, JVMTI_EVENT_METHOD_ENTRY, (jthread) NULL);
    if (returnCode != JNI_OK) {
        error("Unable to disable event notification, JVMTI_EVENT_METHOD_ENTRY (%d)\n", returnCode);
    }

    returnCode = (*jvmtiInterface)->SetEventNotificationMode(jvmtiInterface, JVMTI_DISABLE, JVMTI_EVENT_METHOD_EXIT, (jthread) NULL);
    if (returnCode != JNI_OK) {
        error("Unable to disable event notification, JVMTI_EVENT_METHOD_EXIT (%d)\n", returnCode);
    }

}

void unwindStacks(jvmtiEnv *jvmtiInterface, JNIEnv *jni_env, jint numberOfThreads, jthread *threads) {

    debug("Start UnindStacks\n");

    jvmtiError returnCode;
    jint maxNumberOfFrames = 2048;
    jvmtiStackInfo *stackInfo = NULL;

    returnCode = (*jvmtiInterface)->GetThreadListStackTraces(jvmtiInterface, numberOfThreads, threads, maxNumberOfFrames, &stackInfo);
    if (returnCode != JNI_OK) {
        error("Unable to get Thread Stack traces (%d)\n", returnCode);
    }

    for (int i = 0; i < numberOfThreads; i++) {

        jthread thread = stackInfo[i].thread;
        jint numberOfFrames = stackInfo[i].frame_count;
        jvmtiFrameInfo *frameInfo = stackInfo[i].frame_buffer;

        for (int j = 0; j < numberOfFrames; j++) {
            jmethodID method = frameInfo[j].method;
            jvalue value;
            MethodExit(jvmtiInterface, jni_env, thread, method, JNI_FALSE, value);

        }

    }

    debug("End UnindStacks\n");

}

void windStacks(jvmtiEnv *jvmtiInterface, JNIEnv *jni_env, jint numberOfThreads, jthread *threads) {

    debug("XStart WindStacks\n");

    jvmtiError returnCode;
    jint maxNumberOfFrames = 2048;
    jvmtiStackInfo *stackInfo = NULL;

    debug("Getting stack traces\n");

    returnCode = (*jvmtiInterface)->GetThreadListStackTraces(jvmtiInterface, numberOfThreads, threads, maxNumberOfFrames, &stackInfo);

    debug("got stack traces\n");

    if (returnCode != JNI_OK) {
        debug("Unable to get Thread Stack traces (%d)\n", returnCode);
    }

    debug("Number of Threads %d\n", numberOfThreads);


    for (int i = 0; i < numberOfThreads; i++) {

        jthread thread = stackInfo[i].thread;
        jint numberOfFrames = stackInfo[i].frame_count;
        jvmtiFrameInfo *frameInfo = stackInfo[i].frame_buffer;

        debug("Thread index %d\n", i);

        for (int j = numberOfFrames - 1; j >= 0; j--) {
            jmethodID method = frameInfo[j].method;
            MethodEntry(jvmtiInterface, jni_env, thread, method);

        }

    }

    debug("End WindStacks\n");

}

uint8_t* generateTraceFileName() {

    uint8_t *traceFileName = NULL;

    Option *traceFileNameOption = getOption("traceFileName");

    if (traceFileNameOption != NULL) {

        if (traceFileNameOption->optionValue != NULL) {

            traceFileName = calloc(1, 512);
            sprintf((char*) traceFileName, "%s-%d.trc",
                    traceFileNameOption->optionValue,
                    atomicIncrement(&traceFileNumber));

            traceFileName = traceFileNameOption->optionValue;

        }
    }

    if (traceFileName == NULL) {

        traceFileName = calloc(1, 512);

        if (traceFileName <= 0) {
            error("cannot allocate traceFileNAme");
            return NULL;
        }

#ifdef __WIN32__

        char cwd[MAX_PATH];
		char tempPath[MAX_PATH];

		getcwd(cwd, PATH_MAX);

		DWORD dwRetVal = 0;
		dwRetVal = GetTempPath(MAX_PATH, tempPath);

		if (dwRetVal > MAX_PATH || (dwRetVal == 0)) {
			error("unable to get temp path\n")
			sprintf((char*) traceFileName, "%s\\trace-%d-%d.trc", cwd, getpid(), atomicIncrement(&traceFileNumber));
		} else {
			sprintf((char*) traceFileName, "%strace-%d-%d.trc", tempPath, getpid(), atomicIncrement(&traceFileNumber));
		}

#endif
#ifdef __linux

        sprintf((char*) traceFileName, "%s/trace-%d-%d.trc", traceDirectory, getpid(), atomicIncrement(&traceFileNumber));
#endif

    }

    return traceFileName;

}

void clearThreadLocalStorage(jvmtiEnv *jvmtiInterface, jint numberOfThreads, jthread *threads) {

    for (int i = 0; i < numberOfThreads; i++) {

        ThreadNode *threadNode;

        (*jvmtiInterface)->GetThreadLocalStorage(jvmtiInterface, threads[i], (void **) &threadNode);

        if(threadNode) {

            if (threadNode->threadID != -1) {

                if(threadNode->name) free(threadNode->name);
                if(threadNode->threadBuffer) free(threadNode->threadBuffer);

                free(threadNode);

                (*jvmtiInterface)->SetThreadLocalStorage(jvmtiInterface, threads[i], (const void*) NULL);

            }

        }

    }

}


void rollTraceFile(jvmtiEnv *jvmtiInterface, JNIEnv *jni_env) {



    if (__sync_bool_compare_and_swap(&profiling, LOCKED, UNLOCKED)) {
        info("Stopping Profiling\n");
        jint numberOfThreads;
        jthread *threads;

        getAllThreads(jvmtiInterface, &numberOfThreads, &threads);

        unwindStacks(jvmtiInterface, jni_env, numberOfThreads, threads);

        flushBuffers(jvmtiInterface, numberOfThreads, threads);

        writeEndBurst(globalBuffer);
        flushBuffer(globalBuffer);

        reportStatistics();
        reportThreadStatistics(jvmtiInterface, numberOfThreads, threads);

    }

    if (__sync_bool_compare_and_swap(&profiling, UNLOCKED, UNLOCKED)) {



        writeEndFile(globalBuffer);
        flushBuffer(globalBuffer);

        fclose(traceFile);

        uint8_t *traceFileName = generateTraceFileName();

        openTraceFile((char*) traceFileName);

        info("New Trace File: %s\n", traceFileName);

        if (traceFile <= 0) {
            error("Unable to open trace file %s\n", traceFileName);
            return;
        }


        clearMethodIDHashtable();
        clearClassHashtable();
        clearThreadHashtable();
        clearThreadIDHashtable();

        jint numberOfThreads;
        jthread *threads;

        getAllThreads(jvmtiInterface, &numberOfThreads, &threads);
        clearThreadLocalStorage(jvmtiInterface, numberOfThreads, threads);

        uniqueClassID = 1;
        uniqueObjectID = 1;
        uniqueThreadID = 1;

        free(globalBuffer);

        globalBuffer = allocateBuffer(GLOBAL_BUFFER_LENGTH, true);

        writeDefaultHeader(globalBuffer);

        if (getClassNode("java/lang/Thread") == NULL) {
            discoverClass(jvmtiInterface, jni_env, (*jni_env)->FindClass(jni_env, "java/lang/Thread"), NULL, NULL);

        }

    }

    if (__sync_bool_compare_and_swap(&profiling, UNLOCKED, LOCKED)) {
        info("Starting Profiling\n");

        jint numberOfThreads;
        jthread *threads;

        getAllThreads(jvmtiInterface, &numberOfThreads, &threads);

        writeBeginBurst(globalBuffer);

        windStacks(jvmtiInterface, jni_env, numberOfThreads, threads);

    }

}




void startProfiling(jvmtiEnv *jvmtiInterface, JNIEnv *jni_env) {

    if (__sync_bool_compare_and_swap(&profiling, UNLOCKED, LOCKED)) {
        info("Starting Profiling\n");

        startProfilingTime = getTicks();

        jint numberOfThreads;
        jthread *threads;
        getAllThreads(jvmtiInterface, &numberOfThreads, &threads);

        writeBeginBurst(globalBuffer);

        windStacks(jvmtiInterface, jni_env, numberOfThreads, threads);

        enableMainProfilingEvents(jvmtiInterface);

    }

}



void stopProfiling(jvmtiEnv *jvmtiInterface, JNIEnv *jni_env) {

    if (__sync_bool_compare_and_swap(&profiling, LOCKED, UNLOCKED)) {

        stopProfilingTime = getTicks();

        info("Stopping Profiling\n");
        jint numberOfThreads;
        jthread *threads;

        getAllThreads(jvmtiInterface, &numberOfThreads, &threads);

        unwindStacks(jvmtiInterface, jni_env, numberOfThreads, threads);

        disableMainProfilingEvents(jvmtiInterface);

        flushBuffers(jvmtiInterface, numberOfThreads, threads);

        writeEndBurst(globalBuffer);
        flushBuffer(globalBuffer);

        reportStatistics();
        reportThreadStatistics(jvmtiInterface, numberOfThreads, threads);

        uint64_t profilingTime = (stopProfilingTime-startProfilingTime);

        uint64_t tagObjectsPercent = tagObjectsCost/(profilingTime/100);

        info("Profiling Time %" PRIu64 "\n", profilingTime);

        info("TagObjects Cost %" PRIu64 "\n", tagObjectsCost);

        info("TagObjects percent %" PRIu64 "\n", tagObjectsPercent);

    }

}

ThreadNode* discoverThread(jvmtiEnv *jvmtiInterface, jthread jvmtiThread) {

    jvmtiError returnCode;
    jvmtiThreadInfo threadInfo;

    returnCode = (*jvmtiInterface)->GetThreadInfo(jvmtiInterface, jvmtiThread, &threadInfo);

    if (returnCode != JNI_OK) {
        error("Unable to get Thread Info (%d)\n", returnCode);
        return NULL;
    }

    ThreadNode *threadNode = calloc(1, sizeof(ThreadNode));

    if (threadNode <= 0) {
        error("Unable to allocate ThreadNode\n");
        exit(-1);
    }

    threadNode->threadID = atomicIncrement(&uniqueThreadID);
    if(threadInfo.name) {
        threadNode->name = (uint8_t*) strdup((const char*) threadInfo.name);
    } else {
        threadNode->name = (uint8_t*) "Unknown";
    }

    threadNode->threadBuffer = allocateBuffer(THREAD_BUFFER_LENGTH, false);

    returnCode = (*jvmtiInterface)->SetThreadLocalStorage(jvmtiInterface, jvmtiThread, (const void*) threadNode);

    debug("DiscoverThread returnCode %d threadId %d threadNode %p %s %d\n", returnCode, threadNode->threadID, threadNode, threadInfo.name,  threadInfo.is_daemon);


    //TODO add to hashtable? not really needed

    writeThread(globalBuffer, threadNode);

    return threadNode;

}

void JNICALL VMStart(jvmtiEnv *jvmti_env, JNIEnv *jni_env) {
    debug("VMStart\n");
}

void JNICALL VMInit(jvmtiEnv *jvmti_env, JNIEnv *jni_env, jthread thread) {

    debug("VMInit\n");

    if (getClassNode("java/lang/Thread") == NULL) {
        discoverClass(jvmti_env, jni_env, (*jni_env)->FindClass(jni_env, "java/lang/Thread"), NULL, NULL);

    }

    Option *startProfilingOption = getOption("startProfiling");

    if (startProfilingOption) {
        startProfiling(jvmti_env, jni_env);
    }


#ifdef __linux
    //pthread_create(&controllerThread, NULL, networkController, (void*) jvm);
    pthread_create(&controllerThread, NULL, pipeController, (void*) jvm);
#endif
#ifdef __WIN32__
    pthread_create(&controllerThread, NULL, eventController, (void*) jvm);
#endif

}

void JNICALL VMDeath(jvmtiEnv *jvmti_env, JNIEnv *jni_env) {

    debug("VMDeath\n");
    debug("VMDeath\n");
    debug("StopProfiling\n");
    stopProfiling(jvmti_env, jni_env);
    jint numberOfThreads;
    jthread *threads;
    debug("getAllThreads\n");
    getAllThreads(jvmti_env, &numberOfThreads, &threads);
    debug("flushBuffers\n");
    flushBuffers(jvmti_env, numberOfThreads, threads);
    debug("writeEndFile\n");
    writeEndFile(globalBuffer);
    debug("flushGlobalBuffer\n");
    flushBuffer(globalBuffer);
    debug("fclose\n");
    fclose(traceFile);

    pthread_cancel(controllerThread);
}

uint32_t discoverObject(jvmtiEnv *jvmtiInterface, jobject object, uint16_t classID) {

    jvmtiError returnCode;

    uint32_t tag = atomicIncrement(&uniqueObjectID);

    returnCode = (*jvmtiInterface)->SetTag(jvmtiInterface, object, (jlong) tag);
    if (returnCode == JNI_OK) {
        writeObject(globalBuffer, (uint32_t) tag, classID);
        return tag;
    } else {
        warn("could not tag object (%d)\n", returnCode);
        return -1;
    }

}

uint8_t* fixClassName(char *name) {

    if (name >= 0) {

        uint32_t length = strlen(name);

        uint8_t *newName;

        if (name[0] == 'L') {

            newName = calloc(1, length);

            if (newName <= 0) {
                error("Unable to allocate fixed class name\n");
                exit(-1);
            }

            uint32_t newLength = length - 1;

            for (int i = 0; i < newLength; i++) {
                newName[i] = name[i + 1];
            }


            name = (char*) newName;
            length = newLength;

        }

        if(name[length-1] == ';') {
            name[length-1] = 0;
        }

        return (uint8_t*) name;

    }

    return NULL;

}



static inline uint8_t* copyString(const char * original) {

    if (original == NULL)
        return NULL;

    uint32_t length = 0;

    while (original[length] != 0) {
        length++;
    }

    if (length == 0)
        return NULL;

    uint8_t *copy = malloc(length + 1);

    for (int i = 0; i < length; i++) {
        copy[i] = original[i];
    }

    copy[length] = 0;

    return copy;

}

ClassNode* discoverClass(jvmtiEnv *jvmtiInterface, JNIEnv *jni_env, jclass class, jmethodID requestedMethod, MethodIDNode **returnedMethodIDNode) {

    if (class <= 0)
        return NULL;

    jvmtiError returnCode;

    ClassNode *classNode = calloc(1, sizeof(ClassNode));

    if (classNode <= 0) {
        error("cannot allocate ClassNode");
        return NULL;
    }

    char *classSignature;
    char *classGeneric;

    returnCode = (*jvmtiInterface)->GetClassSignature(jvmtiInterface, class, &classSignature, &classGeneric);

    if (returnCode != JNI_OK) {
        return NULL;
    }

    classNode->name = copyString(classSignature);
    classNode->profilerName = fixClassName(classSignature);

    debug("Discovering Class %s\n", classNode->name);

    if (classSignature) {
        (*jvmtiInterface)->Deallocate(jvmtiInterface, (unsigned char*) classSignature);
    }
    if (classGeneric) {
        (*jvmtiInterface)->Deallocate(jvmtiInterface, (unsigned char*) classGeneric);
    }

    jint methodCount;
    jmethodID *methods;

    returnCode = (*jvmtiInterface)->GetClassMethods(jvmtiInterface, class, &methodCount, &methods);
    if (returnCode != JNI_OK) {
        error("Failed getting Class methods (%d)\n", returnCode);
        return NULL;
    }

    if (methodCount) {

        MethodInfo *methodInfo = calloc(1, (sizeof(MethodInfo) * methodCount));
        if (methodInfo <= 0) {
            error("cannot allocate MethodInfo");
            return NULL;
        }


        classNode->numberOfMethods = methodCount;
        classNode->methods = methodInfo;

        for (int i = 0; i < methodCount; i++) {

            jint modifiers;
            returnCode = (*jvmtiInterface)->GetMethodModifiers(jvmtiInterface, methods[i], &modifiers);
            if (returnCode != JNI_OK) {
                error("Failed getting method modifiers (%d)\n", returnCode);
                return NULL;
            }

            methodInfo[i].modifiers = (uint16_t) modifiers;

            char* methodName;
            char* methodSignature;
            char* methodGeneric;

            returnCode = (*jvmtiInterface)->GetMethodName(jvmtiInterface, methods[i], &methodName, &methodSignature, &methodGeneric);
            if (returnCode != JNI_OK) {
                error("Failed getting method name (%d)\n", returnCode);
                return NULL;
            }

            if (methodName)
                methodInfo[i].name = copyString(methodName);
            if (methodSignature)
                methodInfo[i].signature = copyString(methodSignature);
            if (methodGeneric)
                methodInfo[i].generic = copyString(methodGeneric);

            if (methodName)
                (*jvmtiInterface)->Deallocate(jvmtiInterface, (unsigned char*) methodName);
            if (methodSignature)
                (*jvmtiInterface)->Deallocate(jvmtiInterface, (unsigned char*) methodSignature);
            if (methodGeneric)
                (*jvmtiInterface)->Deallocate(jvmtiInterface, (unsigned char*) methodGeneric);

        }

    }

    jint fieldCount;
    jfieldID *fields;

    returnCode = (*jvmtiInterface)->GetClassFields(jvmtiInterface, class, &fieldCount, &fields);
    if (returnCode != JNI_OK) {
        error("Failed getting Class fields (%d)\n", returnCode);
        return NULL;
    }

    if (fieldCount) {

        FieldInfo *fieldInfo = calloc(fieldCount, sizeof(FieldInfo));
        if (fieldInfo <= 0) {
            error("cannot allocate FieldInfo");
            return NULL;
        }

        classNode->numberOfFields = fieldCount;
        classNode->fields = fieldInfo;

        for (int i = 0; i < fieldCount; ++i) {

            jint modifiers;
            returnCode = (*jvmtiInterface)->GetFieldModifiers(jvmtiInterface, class, fields[i], &modifiers);
            if (returnCode != JNI_OK) {
                error("Failed getting field modifiers (%d)\n", returnCode);
                return NULL;
            }

            fieldInfo[i].modifiers = (uint16_t) modifiers;

            char* name;
            char* signature;
            char* generic;

            returnCode = (*jvmtiInterface)->GetFieldName(jvmtiInterface, class, fields[i], &name, &signature, &generic);
            if (returnCode != JNI_OK) {
                error("Failed getting field name (%d)\n", returnCode);
                return NULL;
            }

            if (name)
                fieldInfo[i].name = copyString(name);
            if (signature)
                fieldInfo[i].signature = copyString(signature);
            if (generic)
                fieldInfo[i].generic = copyString(generic);

            if (name)
                (*jvmtiInterface)->Deallocate(jvmtiInterface, (unsigned char*) name);
            if (signature)
                (*jvmtiInterface)->Deallocate(jvmtiInterface, (unsigned char*) signature);
            if (generic)
                (*jvmtiInterface)->Deallocate(jvmtiInterface, (unsigned char*) generic);

        }

        if (fields)
            (*jvmtiInterface)->Deallocate(jvmtiInterface, (unsigned char*) fields);

    }

    jclass superClass = (*jni_env)->GetSuperclass(jni_env, class);

    if (superClass) {

        char *superClassSignature;
        char *superClassGeneric;

        returnCode = (*jvmtiInterface)->GetClassSignature(jvmtiInterface, superClass, &superClassSignature, &superClassGeneric);
        if (returnCode != JNI_OK) {
            error("Failed getting superclass signature (%d)\n", returnCode);
            return NULL;
        }

        ClassNode *superClassNode = getClassNode(superClassSignature);

        if (superClassNode <= 0) {
            superClassNode = discoverClass(jvmtiInterface, jni_env, superClass, NULL, NULL);
        }

        if (superClassNode)
            classNode->superClassID = superClassNode->classID;

        if (superClassSignature)
            (*jvmtiInterface)->Deallocate(jvmtiInterface, (unsigned char*) superClassSignature);
        if (superClassGeneric)
            (*jvmtiInterface)->Deallocate(jvmtiInterface, (unsigned char*) superClassGeneric);

    }

    jint interfaceCount;
    jclass *interfaces;

    returnCode = (*jvmtiInterface)->GetImplementedInterfaces(jvmtiInterface, class, &interfaceCount, &interfaces);
    if (returnCode != JNI_OK) {
        error("Failed getting Class interfaces (%d)\n", returnCode);
        return NULL;
    }

    if (interfaceCount) {

        InterfaceInfo *interfaceInfo = calloc(interfaceCount, sizeof(InterfaceInfo));
        if (interfaceInfo <= 0) {
            error("cannot allocate InterfaceInfo");
            return NULL;
        }

        classNode->numberOfInterfaces = interfaceCount;
        classNode->interfaces = interfaceInfo;

        for (int i = 0; i < interfaceCount; i++) {

            char *interfaceSignature;
            char *interfaceGeneric;

            returnCode = (*jvmtiInterface)->GetClassSignature(jvmtiInterface, interfaces[i], &interfaceSignature, &interfaceGeneric);
            if (returnCode != JNI_OK) {
                error("Failed getting interface signature (%d)\n", returnCode);
                return NULL;
            }

            if (interfaceSignature) {

                ClassNode *interfaceClassNode = getClassNode(interfaceSignature);

                if (interfaceClassNode <= 0) {
                    interfaceClassNode = discoverClass(jvmtiInterface, jni_env, interfaces[i], NULL, NULL);
                }

                interfaceInfo[i].classID = interfaceClassNode->classID;

            }

            if (interfaceSignature)
                (*jvmtiInterface)->Deallocate(jvmtiInterface, (unsigned char*) interfaceSignature);
            if (interfaceGeneric)
                (*jvmtiInterface)->Deallocate(jvmtiInterface, (unsigned char*) interfaceGeneric);

        }

    }

    if (interfaces)
        (*jvmtiInterface)->Deallocate(jvmtiInterface, (unsigned char*) interfaces);

    classNode->classID = atomicIncrement(&uniqueClassID);

    uint32_t numberOfMethods = (uint32_t) methodCount;

    MethodIDNode *methodIDNodeList = calloc(1, (sizeof(MethodIDNode) * numberOfMethods));
    for (int i = 0; i < numberOfMethods; i++) {
        methodIDNodeList[i].jvmtiMethodID = methods[i];
        methodIDNodeList[i].jvmtiClass = class;
        methodIDNodeList[i].classID = (uint16_t) classNode->classID;
        methodIDNodeList[i].methodID = (uint16_t) i;
        methodIDNodeList[i].staticMethod = (classNode->methods[i].modifiers&ACC_STATIC);
        if(i==0) methodIDNodeList[i].allocationType = LIST_ALLOCATION;

        methodIDNodeList[i].next = NULL;
    }

    addListToMethodIDHashtable(numberOfMethods, methodIDNodeList, jvmtiInterface);

    if (methods)
        (*jvmtiInterface)->Deallocate(jvmtiInterface, (unsigned char*) methods);

    addToClassHashtable(classNode);
    writeClass(globalBuffer, classNode);
    return classNode;

}

void JNICALL MethodEntry(jvmtiEnv *jvmti_env, JNIEnv* jni_env, jthread thread, jmethodID method) {

    uint64_t start = getTicks();

    jvmtiEnv *jvmtiInterface = jvmti_env;

    jvmtiError returnCode;

    ThreadNode *threadNode;

    returnCode = (*jvmtiInterface)->GetThreadLocalStorage(jvmtiInterface, thread, (void **) &threadNode);

    if (threadNode <= 0) {
        threadNode = discoverThread(jvmtiInterface, thread);
    }

    uintptr_t hashCode = intHash((uintptr_t) method);
    uint32_t cacheEntry = (uint32_t) (hashCode & METHOD_CACHE_MASK);

    MethodIDNode *methodIDNode = threadNode->methodCache[cacheEntry];

    if ((methodIDNode <= 0) || (methodIDNode->jvmtiMethodID != method)) {
        methodIDNode = getMethodIDNode(method);
        if (methodIDNode > 0) {
            threadNode->methodCache[cacheEntry] = methodIDNode;
        } else {
            jclass declaringClass;
            returnCode = (*jvmtiInterface)->GetMethodDeclaringClass(jvmtiInterface, method, &declaringClass);
            discoverClass(jvmtiInterface, jni_env, declaringClass, NULL, NULL);
            methodIDNode = getMethodIDNode(method);
            threadNode->methodCache[cacheEntry] = methodIDNode;
        }
        threadNode->cacheMisses++;
    } else {
        threadNode->cacheHits++;
    }

    if (methodIDNode <= 0) {
        error("MethodEntry: methodIDNode still NULL after discovery %p %p\n", methodIDNode, method);
        return;
    }

    jlong tag = -1;

    if(tagObjects && !methodIDNode->staticMethod) {

        jobject this = NULL;

        returnCode = (*jvmtiInterface)->GetLocalObject(jvmtiInterface, thread, 0, 0, &this);

        if(returnCode==JVMTI_ERROR_OPAQUE_FRAME) {
            returnCode = (*jvmtiInterface)->GetLocalInstance(jvmtiInterface, thread, 0, &this);
        }

        if ((returnCode == JNI_OK) && (this)) {

            returnCode = (*jvmtiInterface)->GetTag(jvmtiInterface, this, &tag);

            if (tag == 0) {
                tag = discoverObject(jvmtiInterface, this, methodIDNode->classID);
            } else if (returnCode != JNI_OK) {
                warn("unable to tag object (%d)\n", returnCode);
            }

        }
    }

    writeMethodEntry(threadNode->threadBuffer, threadNode->threadID, methodIDNode->classID, methodIDNode->methodID, (uint32_t) tag, start);

    //threadNode->overhead[threadNode->overheadPointer++] = (getTicks() - start);

}

void JNICALL MethodExit(jvmtiEnv *jvmti_env, JNIEnv* jni_env, jthread thread, jmethodID method, jboolean was_popped_by_exception, jvalue return_value) {

    uint64_t start = getTicks();

    jvmtiEnv *jvmtiInterface = jvmti_env;

    jvmtiError returnCode;

    ThreadNode *threadNode;

    returnCode = (*jvmtiInterface)->GetThreadLocalStorage(jvmtiInterface, thread, (void **) &threadNode);

    if (returnCode != JNI_OK) {
        error("Unable to GetThreadLocalStorage (%d)\n", returnCode);
    }

    if (threadNode <= 0) {
        threadNode = discoverThread(jvmtiInterface, thread);
    }

//      uint64_t entryOverhead = threadNode->overhead[--threadNode->overheadPointer];
    writeMethodExit(threadNode->threadBuffer, threadNode->threadID, start, 0);

}

JNIEXPORT jint JNICALL
Agent_Unload(JavaVM *vm, char *options, void *reserved) {
    agentLoaded = false;
    return JNI_OK;
}

JNIEXPORT jint JNICALL
    Agent_OnLoad(JavaVM *vm, char *options, void *reserved) {

//	if(time(NULL)>1499593293) return JNI_ERR;

    parseOptions(options);

    pid = getpid();

    info("Profiler, pid: %d\n", (uint32_t )pid);

    tagObjects = false;

    Option *tagObjectsOption = getOption("tagObjects");

    if (tagObjectsOption) {
        tagObjects = true;
        warn("Tagging Objects\n");
    }

    Option *traceDirectoryOption = getOption("traceDirectory");

    debug("%p\n", traceDirectoryOption);

    if (traceDirectoryOption) {

        debug("%s\n", traceDirectoryOption->optionValue);
        traceDirectory = (char*) traceDirectoryOption->optionValue;
        warn("Trace Directory: %s\n", traceDirectory);

    } else {
        traceDirectory = "/tmp/";
    }


    jvm = vm;
    createMethodIDHashtable();
    createClassHashtable();
    createThreadHashtable();
    createThreadIDHashtable();

    jvmtiEnv *jvmtiInterface;
    jvmtiError returnCode;

    returnCode = (*vm)->GetEnv(vm, (void **) &jvmtiInterface, JVMTI_VERSION_1_1);
    if (returnCode != JNI_OK) {
        error("Unable to obtain the correct version of the JVMTI interface (%d)\n", returnCode);
        return JNI_ERR;
    }

    globalJVMTIInterface = jvmtiInterface;
    jvmtiCapabilities *requiredCapabilities;

    requiredCapabilities = calloc(1, sizeof(jvmtiCapabilities));
    if (requiredCapabilities <= 0) {
        error("Error allocating memory (%d)\n", (uint32_t )sizeof(jvmtiCapabilities));
        return JNI_ERR;
    }

    requiredCapabilities->can_generate_method_entry_events = 1;
    requiredCapabilities->can_generate_method_exit_events = 1;
    requiredCapabilities->can_generate_all_class_hook_events = 1;
    requiredCapabilities->can_access_local_variables = 1;
    requiredCapabilities->can_tag_objects = 1;
    requiredCapabilities->can_suspend = 1;

    returnCode = (*jvmtiInterface)->AddCapabilities(jvmtiInterface, requiredCapabilities);
    if (returnCode != JNI_OK) {
        error("Unable to obtain the required capabilities (%d)\n", returnCode);
        return JNI_ERR;
    }

    jvmtiEventCallbacks *eventCallbacks = calloc(1, sizeof(jvmtiEventCallbacks));
    if (eventCallbacks <= 0) {
        error("Error allocating memory (%d)\n", (uint32_t )sizeof(jvmtiEventCallbacks));
        return JNI_ERR;
    }


    eventCallbacks->VMInit = &VMInit;
    eventCallbacks->VMStart = &VMStart;
    eventCallbacks->VMDeath = &VMDeath;
    eventCallbacks->MethodEntry = &MethodEntry;
    eventCallbacks->MethodExit = &MethodExit;
//    eventCallbacks->ClassPrepare = &ClassPrepare;

    returnCode = (*jvmtiInterface)->SetEventCallbacks(jvmtiInterface, eventCallbacks, sizeof(jvmtiEventCallbacks));
    if (returnCode != JNI_OK) {
        error("Unable to set the event callbacks (%d)\n", returnCode);
        return JNI_ERR;
    }

    returnCode = (*jvmtiInterface)->SetEventNotificationMode(jvmtiInterface, JVMTI_ENABLE, JVMTI_EVENT_VM_INIT, (jthread) NULL);
    if (returnCode != JNI_OK) {
        error("Unable to enable JVMTI_EVENT_VM_INIT (%d)\n", returnCode);
        return JNI_ERR;
    }

    returnCode = (*jvmtiInterface)->SetEventNotificationMode(jvmtiInterface, JVMTI_ENABLE, JVMTI_EVENT_VM_START, (jthread) NULL);
    if (returnCode != JNI_OK) {
        error("Unable to enable JVMTI_EVENT_VM_START (%d)\n", returnCode);
        return JNI_ERR;
    }

    returnCode = (*jvmtiInterface)->SetEventNotificationMode(jvmtiInterface, JVMTI_ENABLE, JVMTI_EVENT_VM_DEATH, (jthread) NULL);
    if (returnCode != JNI_OK) {
        error("Unable to enable JVMTI_EVENT_VM_DEATH (%d)\n", returnCode);
        return JNI_ERR;
    }

    returnCode = (*jvmtiInterface)->SetEventNotificationMode(jvmtiInterface, JVMTI_DISABLE, JVMTI_EVENT_METHOD_ENTRY, (jthread) NULL);
    if (returnCode != JNI_OK) {
        error("Unable to enable JVMTI_EVENT_METHOD_ENTRY (%d)\n", returnCode);
        return JNI_ERR;
    }

    returnCode = (*jvmtiInterface)->SetEventNotificationMode(jvmtiInterface, JVMTI_DISABLE, JVMTI_EVENT_METHOD_EXIT, (jthread) NULL);
    if (returnCode != JNI_OK) {
        error("Unable to enable JVMTI_EVENT_METHOD_EXIT (%d)\n", returnCode);
        return JNI_ERR;
    }

    returnCode = (*jvmtiInterface)->SetEventNotificationMode(jvmtiInterface, JVMTI_DISABLE, JVMTI_EVENT_CLASS_PREPARE, (jthread) NULL);
    if (returnCode != JNI_OK) {
        error("Unable to enable JVMTI_EVENT_CLASS_PREPARE (%d)\n", returnCode);
        return JNI_ERR;
    }

    uint8_t *traceFileName = generateTraceFileName();

    openTraceFile((char*) traceFileName);

    info("Trace File: %s\n", traceFileName);

    if (traceFile <= 0) {
        error("Unable to open trace file %s\n", traceFileName);
        return JNI_ERR;
    }

    returnCode = pthread_mutexattr_init(&fileLockAttr);

    if (returnCode != 0) {
        error("Unable initialise file lock mutex attr (%d)\n", returnCode);
        return JNI_ERR;
    }

    returnCode = pthread_mutexattr_setpshared(&fileLockAttr, PTHREAD_PROCESS_PRIVATE);

    if (returnCode != 0) {
        error("Unable initialise file lock mutex attr (%d)\n", returnCode);
        return JNI_ERR;
    }

    returnCode = pthread_mutex_init(&fileLock, &fileLockAttr);

    if (returnCode != 0) {
        error("Unable initialise file lock mutex (%d)\n", returnCode);
        return JNI_ERR;
    }

    globalBuffer = allocateBuffer(GLOBAL_BUFFER_LENGTH, true);

#ifdef __WIN32__

    uint64_t qpcFrequency;
	QueryPerformanceFrequency((LARGE_INTEGER *) &qpcFrequency);
	headerTicksPerMicrosecond = (uint32_t) (qpcFrequency / 1000);

#endif
#ifdef __linux


    uint32_t loops = 10;
    uint32_t total = 0;

    for(int i=0;i<loops;i++) {

        uint64_t start = getTicks();

        struct timespec ts;
        ts.tv_sec = 0;
        ts.tv_nsec = 10000000;
        nanosleep(&ts, NULL);

        uint64_t stop= getTicks();
        total+=(stop-start);
    }

    headerTicksPerMicrosecond = ((total/loops)/10000);

#endif

    headerVMStartTime = time(NULL);
    headerConnectionStartTime = headerVMStartTime;
    headerOverhead = 0;
    writeDefaultHeader(globalBuffer);

    return JNI_OK;
}

void JNICALL ClassPrepare(jvmtiEnv *jvmti_env, JNIEnv* jni_env, jthread thread, jclass klass) {

    discoverClass(jvmti_env, jni_env, klass, NULL, NULL);

}


#pragma clang diagnostic pop