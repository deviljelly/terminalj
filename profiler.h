
/*
 * profiler.h
 *
 *  Created on: 08.09.2015
 *      Author: F600308
 */

#ifndef PROFILER_H_
#define PROFILER_H_

#include <stdbool.h>
#include "util.h"
#include "jvmti.h"


#define GLOBAL_BUFFER_LENGTH 131072
#define THREAD_BUFFER_LENGTH 131072

#define EVENT_BEGIN_BURST 101
#define EVENT_END_BURST 102
#define EVENT_END_FILE 100
#define EVENT_CLASS_DEFINE 4
#define EVENT_EXTENDED_EXTENSIVE_CLASS_LOAD 110
#define EVENT_OBJECT_DEFINE 20
#define EVENT_WIDE_METHOD_ENTER 91
#define EVENT_METHOD_LEAVE 31
#define EVENT_THREAD_DEFINE 10

typedef struct Option_struct Option;

#ifdef __WIN32__
typedef struct EventCleanupStruct_struct EventCleanupStruct;
struct EventCleanupStruct_struct {
	HANDLE startEvent;
	HANDLE stopEvent;
	HANDLE rollEvent;
};
#endif

void* networkController(void *arg);

struct Option_struct {
    uint8_t *rawOption;
    uint8_t *optionName;
    uint8_t *optionValue;
};

#endif /* PROFILER_H_ */
