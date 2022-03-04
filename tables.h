
/*
 * tables.h
 *
 *  Created on: 08.09.2015
 *      Author: F600308
 */

#ifndef TABLES_H_
#define TABLES_H_

#include <stdint.h>
#ifdef __linux
#include <pthread.h>
#endif
#include "util.h"
#include "profiler.h"
#include "jvmti.h"

#define METHOD_ID_HASHTABLE_BUCKETS 16384
#define METHOD_ID_HASHTABLE_MASK 16383

#define CLASS_HASHTABLE_BUCKETS 4096
#define CLASS_HASHTABLE_MASK 4095

#define THREAD_HASHTABLE_BUCKETS 256
#define THREAD_HASHTABLE_MASK 255

#define THREAD_ID_HASHTABLE_BUCKETS 256
#define THREAD_ID_HASHTABLE_MASK 255

#define METHOD_CACHE_ENTRIES 1024
#define METHOD_CACHE_MASK 1023

#define LIST_ALLOCATION 1
#define SINGLE_ALLOCATION 2

#define ACC_STATIC 0x0008



typedef struct MethodIDHashtable_struct MethodIDHashtable;
typedef struct MethodIDBucket_struct MethodIDBucket;
typedef struct MethodIDNode_struct MethodIDNode;

typedef struct ClassHashtable_struct ClassHashtable;
typedef struct ClassBucket_struct ClassBucket;
typedef struct ClassNode_struct ClassNode;
typedef struct MethodInfo_struct MethodInfo;
typedef struct FieldInfo_struct FieldInfo;
typedef struct InterfaceInfo_struct InterfaceInfo;

typedef struct ThreadHashtable_struct ThreadHashtable;
typedef struct ThreadBucket_struct ThreadBucket;
typedef struct ThreadNode_struct ThreadNode;

typedef struct ThreadIDHashtable_struct ThreadIDHashtable;
typedef struct ThreadIDBucket_struct ThreadIDBucket;
typedef struct ThreadIDNode_struct ThreadIDNode;

#ifdef __WIN32__
typedef uint32_t NativeThreadID;
#elif __linux
typedef pthread_t NativeThreadID;
#endif


void createMethodIDHashtable();
void createClassHashtable();
void createThreadHashtable();
void createThreadIDHashtable();

void clearMethodIDHashtable();
void clearClassHashtable();
void clearThreadHashtable();
void clearThreadIDHashtable();


MethodIDNode* getMethodIDNode(jmethodID jvmtiMethodID);
ClassNode* getClassNode(char *name);
ThreadNode* getThreadNode(uint32_t threadID);
ThreadIDNode* getThreadIDNode(NativeThreadID nativeThreadID);

void addListToMethodIDHashtable(uint32_t numberOfMethods, MethodIDNode *methodIDNodeList, jvmtiEnv *jvmtiInterface);
void addToMethodIDHashtable(jmethodID jvmtiMethodID, jclass jvmtiClass, uint16_t classID, uint16_t methodID);
void addToClassHashtable(ClassNode *classNode);
void addToThreadHashtable(ThreadNode *threadNode);
void addToThreadIDHashtable(ThreadIDNode *threadNode);

void reportStatistics();


struct MethodIDHashtable_struct {
    volatile LockStructure lock;
    uint32_t entries;
    uint32_t collisions;
    uint64_t lockedFor;
    MethodIDBucket *buckets[METHOD_ID_HASHTABLE_BUCKETS];
};


struct MethodIDBucket_struct {
    volatile LockStructure lock;
    uint32_t chainLength;
//	volatile MethodIDNode *rootNode;
    MethodIDNode *rootNode;
};

struct MethodIDNode_struct {
    jmethodID jvmtiMethodID;
    jclass jvmtiClass;
    uint16_t classID;
    uint16_t methodID;
    uint8_t allocationType;
    uint8_t staticMethod;
//	volatile MethodIDNode *next;
    MethodIDNode *next;
};


struct ClassHashtable_struct {
    volatile LockStructure lock;
    uint32_t entries;
    uint32_t collisions;
    uint64_t lockedFor;
    ClassBucket *buckets[CLASS_HASHTABLE_BUCKETS];
};

struct ClassBucket_struct {
    volatile LockStructure lock;
    uint32_t chainLength;
    ClassNode *rootNode;
};

struct ClassNode_struct {
    uint32_t hashCode;
    volatile LockStructure written;
    volatile LockStructure lock;
    uint8_t *name;
    uint8_t *profilerName;
    uint32_t classID;
    uint32_t superClassID;
    uint32_t numberOfMethods;
    MethodInfo *methods;
    uint32_t numberOfFields;
    FieldInfo *fields;
    uint32_t numberOfInterfaces;
    InterfaceInfo *interfaces;
    ClassNode *next;
};

struct MethodInfo_struct {
    uint16_t modifiers;
    uint8_t *name;
    uint8_t *signature;
    uint8_t *generic;
};


struct FieldInfo_struct {
    uint16_t modifiers;
    uint8_t *name;
    uint8_t *signature;
    uint8_t *generic;
};


struct InterfaceInfo_struct {
    uint32_t classID;
};


struct ThreadHashtable_struct {
    volatile LockStructure lock;
    uint32_t entries;
    uint32_t collisions;
    uint64_t lockedFor;
    ThreadBucket *buckets[THREAD_HASHTABLE_BUCKETS];
};

struct ThreadBucket_struct {
    volatile LockStructure lock;
    uint32_t chainLength;
    ThreadNode *rootNode;
};


struct ThreadNode_struct {
    uint32_t threadID;
    uint8_t* name;
    Buffer *threadBuffer;
    ThreadNode *next;
//	uint32_t overheadPointer;
//	uint64_t overhead[4096];
    uint32_t cacheHits;
    uint32_t cacheMisses;
    MethodIDNode *methodCache[METHOD_CACHE_ENTRIES];
};


struct ThreadIDHashtable_struct {
    volatile LockStructure lock;
    uint32_t entries;
    uint32_t collisions;
    uint64_t lockedFor;
    ThreadIDBucket *buckets[THREAD_ID_HASHTABLE_BUCKETS];
};

struct ThreadIDBucket_struct {
    volatile LockStructure lock;
    uint32_t chainLength;
    ThreadIDNode *rootNode;
};


struct ThreadIDNode_struct {
    NativeThreadID nativeThreadID;
    uint32_t threadID;
    ThreadIDNode *next;
};



#endif /* TABLES_H_ */
