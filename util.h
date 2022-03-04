/*
 * util.h
 *
 *  Created on: 08.09.2015
 *      Author: F600308
 */

#ifndef UTIL_H_
#define UTIL_H_
#include <stdint.h>
#include <stdbool.h>
#ifdef __WIN32__
#include <windows.h>
#else
#ifdef __linux
#include <pthread.h>
#endif
#endif
#include <stddef.h>
#include <stdio.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/time.h>
#include <time.h>

#define UNLOCKED 0
#define LOCKED 1
#define NOT_WRITTEN 0
#define WRITTEN 1
#define MIN_LOCK_WAIT_NS 1000
#define MAX_LOCK_WAIT_NS 100000
#define LOCK_WAIT_STEP_NS 1000

//#define TRACE
//#define DEBUG
#define INFO
#define WARN
#define _ERROR

#ifdef TRACE
#define trace(...) \
{fprintf(stderr, "TRACE: ");fprintf(stderr, __PRETTY_FUNCTION__);fprintf(stderr,": ");fprintf(stderr,__VA_ARGS__);fflush(stderr);}
#else
#define trace(...)
#endif

#ifdef DEBUG
#define debug(...) \
{fprintf(stderr, "DEBUG: ");fprintf(stderr, __PRETTY_FUNCTION__);fprintf(stderr,": ");fprintf(stderr,__VA_ARGS__);fflush(stderr);}
#else
#define debug(...)
#endif

#ifdef INFO
#define info(...) \
{fprintf(stderr, "INFO: ");fprintf(stderr,__VA_ARGS__);fflush(stderr);}
#else
#define info(...)
#endif

#ifdef WARN
#define warn(...) \
{fprintf(stderr, "WARN: ");fprintf(stderr,__VA_ARGS__);fflush(stderr);}
#else
#define warn(...)
#endif

#ifdef _ERROR
#define error(...) \
{fprintf(stderr, "ERROR: ");fprintf(stderr, __PRETTY_FUNCTION__);fprintf(stderr,": ");fprintf(stderr,__VA_ARGS__);fflush(stderr);}
#else
#define error(...)
#endif

typedef uint32_t LockStructure ;

typedef struct Buffer_struct Buffer;

struct Buffer_struct {
    uint32_t bufferLength;
    uint32_t bufferOffset;
    bool shared;
    LockStructure lock;
    uint8_t *buffer;
};





#if defined (__WIN32__)

static inline int winnanosleep (const struct timespec *req, struct timespec *rem) {
	HANDLE timer = NULL;
	LARGE_INTEGER sleepTime;

	sleepTime.QuadPart = req->tv_sec * 1000000000 + req->tv_nsec / 100;

	timer = CreateWaitableTimer (NULL, TRUE, NULL);
	if (timer == NULL)
	{
		LPVOID buffer = NULL;
		FormatMessage (FORMAT_MESSAGE_ALLOCATE_BUFFER | FORMAT_MESSAGE_FROM_SYSTEM, NULL,
						GetLastError(), 0, (LPTSTR) &buffer, 0, NULL);
		error("nanosleep: CreateWaitableTimer failed: (%" PRIu64 ") %s\n", (uint64_t) GetLastError (), (LPTSTR) buffer);
		LocalFree (buffer);
		CloseHandle(timer);
		return -1;
	}

	if (!SetWaitableTimer (timer, &sleepTime, 0, NULL, NULL, 0))
	{
		LPVOID buffer = NULL;
		FormatMessage (FORMAT_MESSAGE_ALLOCATE_BUFFER | FORMAT_MESSAGE_FROM_SYSTEM, NULL,
						GetLastError(), 0, (LPTSTR) &buffer, 0, NULL);
		error("nanosleep: SetWaitableTimer failed: (%" PRIu64 ") %s\n", (uint64_t) GetLastError (), (LPTSTR) buffer);
		LocalFree (buffer);
		CloseHandle(timer);
		return -1;
	}

	if (WaitForSingleObject (timer, INFINITE) != WAIT_OBJECT_0)
	{
		LPVOID buffer = NULL;
		FormatMessage (FORMAT_MESSAGE_ALLOCATE_BUFFER | FORMAT_MESSAGE_FROM_SYSTEM, NULL,
						GetLastError(), 0, (LPTSTR) &buffer, 0, NULL);
		error("nanosleep: WaitForSingleObject failed: (%" PRIu64 ") %s\n", (uint64_t) GetLastError (), (LPTSTR) buffer);
		LocalFree (buffer);
		CloseHandle(timer);
		return -1;
	}

	CloseHandle(timer);

	return 0;

}

#endif


static inline uintptr_t intHash(uintptr_t x) {

    x = ((x >> 16) ^x) * 0x45d9f3b;
    x = ((x >> 16) ^x) * 0x45d9f3b;
    x = ((x >> 16) ^x);
    return x;

}

static inline uint32_t jenkins_one_at_a_time_hash(const char *key, size_t len) {

    uint32_t hash, i;
    for(hash = i = 0; i < len; ++i) {
        hash += key[i];
        hash += (hash << 10);
        hash ^= (hash >> 6);
    }

    hash += (hash << 3);
    hash ^= (hash >> 11);
    hash += (hash << 15);
    return hash;

}

static inline uint64_t getTicks() {

    uint32_t lower;
    uint32_t upper;

#ifdef __x86_64__

    __asm__ __volatile__ ("rdtsc": "=a" (lower), "=d"  (upper));

#elif __i386__

    __asm__ __volatile__ ("rdtsc": "=a" (lower), "=d"  (upper));

#elif __powerpc64__

	uint32_t upper1;

	do{

		__asm__ __volatile__ ("mftbu %0" : "=r"(upper));
		__asm__ __volatile__ ("mftb %0" : "=r"(lower));
		__asm__ __volatile__ ("mftbu %0" : "=r"(upper1));

	} while(upper!=upper1);


#endif

    uint64_t ticks = ((uint64_t) upper << 32) + lower;


    return ticks;

}

static inline uint32_t atomicIncrement(volatile uint32_t *value) {
    return __sync_add_and_fetch(value, 1);
}

static inline uint64_t lock(volatile LockStructure *lock) {

    uint64_t sleepNS = MIN_LOCK_WAIT_NS;
    uint64_t sleptForNS = 0;

    while(__sync_val_compare_and_swap(lock, UNLOCKED, LOCKED)) {

        struct timespec ts;
        ts.tv_sec = 0;
        ts.tv_nsec = sleepNS;

#ifdef __linux
        nanosleep(&ts, NULL);
#elif __WIN32__
        winnanosleep((const struct timespec*)&ts, NULL);
#endif
        sleptForNS += sleepNS;
        sleepNS+=LOCK_WAIT_STEP_NS;
        if(sleepNS>MAX_LOCK_WAIT_NS) sleepNS = MAX_LOCK_WAIT_NS;
    }

    return sleptForNS;

}

static inline void unlock(volatile LockStructure *lock) {
    __sync_lock_test_and_set(lock, UNLOCKED);
    //*lock = UNLOCKED;
}


static inline uint64_t spinWait(volatile LockStructure *lock) {

    uint64_t start = getTicks();
    uint64_t waitTime = 0;

    while(__sync_val_compare_and_swap(lock, UNLOCKED, UNLOCKED)) {

        uint64_t now = getTicks();
        waitTime += (now-start);
        start = now;
    }

    return waitTime;
}


static inline uint64_t spinLock(volatile LockStructure *lock) {

    uint64_t start = getTicks();
    uint64_t waitTime = 0;

    while(__sync_val_compare_and_swap(lock, UNLOCKED, LOCKED)) {

        uint64_t now = getTicks();
        waitTime += (now-start);
        start = now;
    }

    return waitTime;
}



//static inline void* compareAndSwapPtr(volatile uintptr_t *targetPtr, void *oldPtr, void *newPtr) {
//    return (void*) __sync_val_compare_and_swap(targetPtr, &oldPtr, &newPtr);
//}


static inline bool compareAndSwapPtrBool(volatile uintptr_t *targetPtr, volatile void* oldPtr, volatile void* newPtr) {
    return  __sync_bool_compare_and_swap(targetPtr, (uintptr_t) oldPtr, (uintptr_t) newPtr);
}


static inline bool alreadyWritten(volatile uint32_t *writtenFlag) {

    uint32_t prev = __sync_val_compare_and_swap(writtenFlag, NOT_WRITTEN, WRITTEN);

    if(prev==NOT_WRITTEN) {
        return false;
    } else {
        return true;
    }

}

#endif /* UTIL_H_ */

