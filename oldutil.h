#ifndef UTIL_H_
#define UTIL_H_

#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <stdbool.h>
#ifdef __win32__
#include <winsock2.h>
#include "windows.h"
#include "winbase.h"
#else
#include <pthread.h>
#endif
#ifdef __AIX__
#include <sys/timers.h>
#include <sys/timer.h>
#include <sys/atomic_op.h>
#include <sys/systemcfg.h>
#endif
#ifdef __SOLARIS__
#include <atomic.h>
#endif

#include <time.h>
#include <sys/time.h>
#include <unistd.h>
#include "debug.h"
#include "classfile.h"

#define UNLOCKED 0
#define LOCKED 1


#ifdef __win32__

//struct timespec
//  {
//    int tv_sec; /* Seconds.  */
//    long int tv_nsec; /* Nanoseconds.  */
//  };

#endif

#ifdef __MVS__
#  define pthread_getspecific pthread_getspecific_d8_np
#endif

#ifdef __linux__
#define JVMStringToPlatform(STR) (STR)
#define platformStringToJVM(STR) (STR)
#elif __AIX__
#define JVMStringToPlatform(STR) (STR)
#define platformStringToJVM(STR) (STR)
#elif __SOLARIS__
#define JVMStringToPlatform(STR) (STR)
#define platformStringToJVM(STR) (STR)
#elif defined(__win32__)
#define JVMStringToPlatform(STR) (STR)
#define platformStringToJVM(STR) (STR)
#elif defined(__MVS__)
#define JVMStringToPlatform(STR) ({         \
             char *str=(STR);           \
             size_t len = strlen(str);  \
             char *buf=alloca(len+1);           \
             memcpy(buf, str, len+1);   \
             __a2e_l(buf, len);         \
             buf;                       \
          })

#define platformStringToJVM(STR) ({         \
             char *str=(STR);           \
             size_t len = strlen(str);  \
             char *buf=alloca(len+1);           \
             memcpy(buf, str, len+1);   \
             __e2a_l(buf, len);         \
             buf;                       \
          })
#endif

typedef uint32_t lockStructure;

//static uint32_t * mallocs;


#define MALLOC_BUFFERS 0
#define MALLOC_CLASS 1
#define MALLOC_BYTECODE 2

#ifdef __linux__
# if defined(__s390x__)
#  define LINUX_S390
#  define JINSIGHT_PLATFORM 55
#  define barrier() __asm__ __volatile__("BR 0": : :"memory")
#  define CS32  "cs"
#  define CS64  "csg"
#  define CSPTR "csg"
# elif defined(__s390__)
#  define LINUX_S390
#  define JINSIGHT_PLATFORM 55
#  define CS32  "cs"
#  define CS64  "cds"
#  define CSPTR "cs"
# elif defined(__386__)
#  define LINUX_X86
#  define JINSIGHT_PLATFORM 44
//#  define barrier() __asm__ __volatile__("": : :"memory")
#  define barrier() ;
#  define CS32 "cmpxchgl"
#  define CS64 "cmpxchgq"
#  define CSPTR "cmpxchgl"
# elif defined(__x86_64__)
#  define barrier() __asm__ __volatile__("": : :"memory")
#  define LINUX_X86
#  define JINSIGHT_PLATFORM 44
#  define CS32 "cmpxchgl"
#  define CS64 "cmpxchgq"
#  define CSPTR "cmpxchgq"
# else
//# error
# endif
#elif defined(__win32__)
# if defined(__386__)
#  define JINSIGHT_PLATFORM 11
#  define barrier() ;
#  define CS32 "cmpxchgl"
#  define CS64 "cmpxchgq"
#  define CSPTR "cmpxchgl"
# elif defined(__x86_64__)
#  define barrier() __asm__ __volatile__("": : :"memory")
#  define LINUX_X86
#  define JINSIGHT_PLATFORM 44
#  define CS32 "cmpxchgl"
#  define CS64 "cmpxchgq"
#  define CSPTR "cmpxchgq"
# else
//# error
# endif
#elif defined(__MVS__)
#  define JINSIGHT_PLATFORM 33
# define __builtin_expect(X,Y) X
# define barrier() ((void)getTicks())
# define CS32  __cs1
# ifdef _LP64
#  define CS64  __csg
#  define CSPTR __csg
# else
#  define CS64  __cds1
#  define CSPTR __cs1
# endif
#elif defined(__AIX__)
# define JINSIGHT_PLATFORM 22
# define __builtin_expect(X,Y) X
//# define barrier() ((void)getTicks())
//# define CS32  __cs1
//# ifdef _LP64
//#  define CS64  __csg
//#  define CSPTR __csg
//# else
//#  define CS64  __cds1
//#  define CSPTR __cs1
//# endif
#elif defined(__SOLARIS__)
#  define JINSIGHT_PLATFORM 66
# ifdef _LP64
# else
# endif
#endif

static inline uint64_t
getTicks()
{
#ifdef LINUX_X86
  uint32_t lo, hi;
  __asm__ __volatile__("rdtsc"
      : "=a" (lo), "=d" (hi));
  return (uint64_t) hi << 32 | lo;
#elif defined(LINUX_S390)
  unsigned long long time;
  __asm__("stck %[time]"
      : [time] "=Q" (time)
      : : "memory", "cc");
  return time;
#elif defined(__win32__)
  uint32_t lo, hi;
  __asm__ __volatile__("rdtsc"
      : "=a" (lo), "=d" (hi));
  return (uint64_t) hi << 32 | lo;
#elif defined(__MVS__)
  unsigned long long time;
  __stck(&time);
  return time;
#elif defined(__AIX__)
  uint64_t time;
  timebasestruct_t t;
  int rc = read_wall_time(&t, sizeof(timebasestruct_t));

  //   if(t.flag==RTC_POWER_PC) {

  time = t.tb_high;
  time = time << 32;
  time |= t.tb_low;
  //       fprintf(stderr, "this is the correct type of timer %d\n", t.flag);

  //  } else {


  //       fprintf(stderr, "this is the other type of timer\n");


  // }

  return time;
#elif defined(__SOLARIS__)
  uint64_t time;
  time = gethrtime();
  return time;

#endif
}

#ifdef __MVS__
// no nanosleep on z/OS; fake it
static inline int nanosleep(const struct timespec *rqtp, struct timespec *rmtp)
  {
    struct timeval req =
      { .tv_sec = rqtp->tv_sec,
        .tv_usec = rqtp->tv_nsec/1000};

    return select(0, NULL, NULL, NULL, &req);
  }
#endif

#ifdef __AIX__

static inline int nanosleep(const struct timespec *rqtp, struct timespec *rmtp)
  {

    struct timestruc_t ts;

    ts.tv_sec=rqtp->tv_sec;
    ts.tv_nsec=rqtp->tv_nsec;

    //  fprintf(stderr,"%d\n", ts.tv_nsec);
    return nsleep(&ts, rmtp);

  }

#endif

#ifdef __win32__
// no nanosleep on mingw; fake it as above... maybe usleep is better
static inline int nanosleep(struct timespec *rqtp, struct timespec *rmtp)
  {
    return usleep(rqtp->tv_nsec/1000);

  }
#endif

#ifdef LINUX_S390
// CS CC is 0 on success
static inline int compareAndSwap(uint32_t *ptr, uint32_t exp, uint32_t new)
  {
    int ret;
    __asm__ (CS32 " %[exp],%[new],%[ptr]\n\t"
        "ipm %[ret]\n\t"
        "srl %[ret],28"
        : [exp] "+?d" (exp), [ptr] "+?Q" (*ptr), [ret] "=d" (ret)
        : [new] "d" (new)
        : "memory", "cc");
    return ret;
  }

static inline int compareAndSwapPtr(void *ptr, void *exp, void *new)
  {
    int ret;
    __asm__ (
        CSPTR " %[exp],%[new],%[ptr]\n\t"
        "ipm %[ret]\n\t"
        "srl %[ret],28"
        : [exp] "+?d" (exp), [ptr] "+?Q" (*(void**)ptr), [ret] "=d" (ret)
        : [new] "d" (new)
        : "memory", "cc");
    return ret;
  }
#elif defined(LINUX_X86)
static inline int
compareAndSwap(lockStructure *ptr, lockStructure exp, lockStructure new)
{

  lockStructure prev = exp;

  __asm__ __volatile__(
      "lock; " CS32 " %1,%2"
      : "=a"(prev)
      : "r"(new), "m"(*ptr), "0"(prev)
      : "memory");
  return exp != prev; // return zero on success
}

// #define __xg(x) ((struct __xchg_dummy *)(x))
// (*__xg(ptr))
static inline int
compareAndSwapPtr(void *ptr, void *exp, void *new)
{

  uintptr_t *prev = (uintptr_t*) exp; //TODO WARNING .... Test this!!
  uintptr_t* _new = (uintptr_t*) new;
  uintptr_t* _ptr = (uintptr_t*) ptr;

  __asm__ __volatile__(
      "lock; " CSPTR " %1,%2"
      : "=a"(prev)
      : "r"(_new), "m"(*_ptr), "0"(prev)
      : "memory");
  return exp != prev; // return zero on success
}
#elif defined(__win32__)
static inline int compareAndSwap(lockStructure *ptr, lockStructure exp, lockStructure new)
  {

    lockStructure prev = exp;

    __asm__ __volatile__(
        "lock; " CS32 " %1,%2"
        : "=a"(prev)
        : "r"(new), "m"(*ptr), "0"(prev)
        : "memory");
    return exp != prev; // return zero on success
  }


static inline int compareAndSwapPtr(void *ptr, void *exp, void *new)
  {

    void *prev = exp;

    __asm__ __volatile__(
        "lock; " CSPTR " %1,%2"
        : "=a"(prev)
        : "r"(new), "m"(*ptr), "0"(prev)
        : "memory");
    return exp != prev; // return zero on success
  }
#elif defined(__MVS__)
// Try to compare and swap; __cs1 returns zero on success
#define compareAndSwap(ptr, exp, new) ({ \
          uint32_t exp1=(exp), new1=(new); \
          CS32((void*)&exp1, (ptr), &new1); \
})
#define compareAndSwapPtr(ptr, exp, new) ({ \
          void *exp1=(exp), *new1=(new); \
          CSPTR((void*)&exp1, (ptr), &new1); \
})

#elif defined (__AIX__)

static inline int compareAndSwap(uint32_t *ptr, uint32_t exp, uint32_t new)
  {
    //      debugHigh("(AIX _check_lock)", "locking %s\n");

    //was
    //__sync();
    //__isync();

    // maybe
    //__isync();
    //__eieio

    // trying first
    __lwsync();

    if(_check_lock((atomic_p)ptr,(int) exp, (int) new))
      {
        //   if(compare_and_swap((atomic_p)ptr,(int*) &exp, (int)new)) {
        return 1;
      }
    else
      {
        return 0;
      }

  }

static inline int compareAndSwapPtr(void *ptr, void *exp, void *new)
  {

#ifdef _LP64
    //was
    //__sync();
    //__isync();

    // maybe
    //__isync();
    //__eieio

    // trying first
    __lwsync();

    //void** ptr2 = (void**) ptr;

    if(compare_and_swaplp((atomic_l)(ptr), (long*)exp, (long)new))
      {
        return 0;
      }
    else
      {
        return 1;
      }

#else
    //was
    //__sync();
    //__isync();

    // maybe
    //__isync();
    //__eieio

    // trying first
    __lwsync();
    //void** ptr2 = (void**) ptr;
    if(compare_and_swap((atomic_p)(ptr), (int*)exp, (int)new))
      {
        return 0;
      }
    else
      {
        return 1;
      }

#endif

  }
#elif defined(__SOLARIS__)

static inline int compareAndSwap(lockStructure *ptr, lockStructure exp, lockStructure new)
  {
    lockStructure prev = exp;
    lockStructure ret;
    ret = atomic_cas_32(ptr, exp, new);
    return(ret!=prev);
  }

static inline int compareAndSwapPtr(void *ptr, void *exp, void *new)
  {
    void *prev = exp;
    void *ret;
    ret = atomic_cas_ptr(ptr, exp, new);
    return (ret!=prev);
  }

#endif

#define MIN_WAIT 1024
#define MAX_WAIT 1048576
static inline void
lock(lockStructure *pointer)
{ // this seems to be better
  int sleepTime = MIN_WAIT;
  while (compareAndSwap(pointer, UNLOCKED, LOCKED))
    {


      struct timespec ts;


      ts.tv_sec = 0;
      ts.tv_nsec = sleepTime;

      nanosleep(&ts, NULL);

      sleepTime *= 2;
      if (sleepTime >= MAX_WAIT)
        sleepTime = MAX_WAIT;
#ifdef __win32__
      //                        SwitchToThread();
#else
      //                        sched_yield();
#endif
    }
}
#ifdef __AIX__

static inline void unlock(lockStructure *pointer)
  {
    //was
    //__sync();
    //__isync();

    // maybe
    //__isync();
    //__eieio

    // trying first
    __lwsync();
    _clear_lock((atomic_p)pointer, UNLOCKED);
  }
#else
static inline void
unlock(lockStructure *pointer)
{
  *pointer = UNLOCKED;
}
#endif

static inline void
spinALittlelock(lockStructure *pointer)
{
  uint32_t countDown = 1000;
  while (compareAndSwap(pointer, UNLOCKED, LOCKED))
    {
      countDown--;
      if (!countDown)
        {
#ifdef __win32__
          SwitchToThread();
#else
          sched_yield();
#endif
          countDown = 1000;
        }
    }
}

static inline uint32_t
accountingLock(lockStructure *pointer)
{
  uint32_t lockContends = 0;
  while (compareAndSwap(pointer, UNLOCKED, LOCKED))
    {
      lockContends++;
    }
  if (lockContends > 1000)
    debugHigh("(accountingLock)", "lock contends %d\n", lockContends);
  return lockContends;
}

static inline uint32_t
namedLock(lockStructure *pointer, const char* lockName)
{
  debugHigh("(namedLock)", "locking %s\n", lockName);
  return accountingLock(pointer);
}

static inline void
namedUnlock(lockStructure *pointer, const char* lockName)
{
  debugHigh("(namedUnlock)", "locking %s\n", lockName);
  unlock(pointer);
}

static inline uint8_t
readuint8(const unsigned char* pointer)
{

  return (uint8_t) *pointer & 0xff;

}

static inline uint16_t
readuint16(const unsigned char* pointer)
{

  uint16_t value = 0;

  value |= (uint16_t) (*(pointer++) & 0xff) << 8;
  value |= (uint16_t) *(pointer) & 0xff;

  return value;

}

static inline int16_t
readint16(const unsigned char* pointer)
{

  int16_t value = 0;

  value |= (int16_t) (*(pointer++) & 0xff) << 8;
  value |= (int16_t) *(pointer) & 0xff;

  return value;

}
static inline int32_t
readint32(const unsigned char* pointer)
{

  int32_t value = 0;

  value |= (int32_t) (*(pointer++) & 0xff) << 24;
  value |= (int32_t) (*(pointer++) & 0xff) << 16;
  value |= (int32_t) (*(pointer++) & 0xff) << 8;
  value |= (int32_t) *(pointer) & 0xff;

  return value;

}

static inline int64_t
readint64(const unsigned char* pointer)
{

  int64_t value = 0;

  value |= (int64_t) (*(pointer++) & 0xff) << 56;
  value |= (int64_t) (*(pointer++) & 0xff) << 48;
  value |= (int64_t) (*(pointer++) & 0xff) << 40;
  value |= (int64_t) (*(pointer++) & 0xff) << 32;
  value |= (int64_t) (*(pointer++) & 0xff) << 24;
  value |= (int64_t) (*(pointer++) & 0xff) << 16;
  value |= (int64_t) (*(pointer++) & 0xff) << 8;
  value |= (int64_t) *(pointer) & 0xff;

  return value;

}

static inline uint32_t
readuint32(const unsigned char* pointer)
{

  uint32_t value = 0;

  value |= (int32_t) (*(pointer++) & 0xff) << 24;
  value |= (int32_t) (*(pointer++) & 0xff) << 16;
  value |= (int32_t) (*(pointer++) & 0xff) << 8;
  value |= (int32_t) *(pointer) & 0xff;

  return value;

}

static inline uint32_t
writeuint8(uint8_t value, unsigned char* pointer)
{

  *(pointer++) = value;
  return sizeof(int8_t);

}

static inline uint32_t
writeint8(int8_t value, unsigned char* pointer)
{

  *(pointer++) = value;
  return sizeof(int8_t);

}

static inline uint32_t
writeuint32(uint32_t value, unsigned char* pointer)
{

  *(pointer++) = (value >> 24) & 0xff;
  *(pointer++) = (value >> 16) & 0xff;
  *(pointer++) = (value >> 8) & 0xff;
  *(pointer++) = (value) & 0xff;

  return sizeof(uint32_t);
}

static inline uint32_t
writeint16(int16_t value, unsigned char* pointer)
{

  *(pointer++) = (value & 0xff00) >> 8;
  *(pointer++) = (value & 0xff);

  return sizeof(int16_t);

}

static inline uint32_t
writeuint16(uint16_t value, unsigned char* pointer)
{

  *(pointer++) = (value & 0xff00) >> 8;
  *(pointer++) = (value & 0xff);

  return sizeof(uint16_t);

}

static inline uint32_t
writeint32(int32_t value, unsigned char* pointer)
{

  *(pointer++) = (value >> 24) & 0xff;
  *(pointer++) = (value >> 16) & 0xff;
  *(pointer++) = (value >> 8) & 0xff;
  *(pointer++) = (value) & 0xff;

  return sizeof(int32_t);
}
static inline uint32_t
writeint64(uint64_t value, unsigned char* pointer)
{

  *(pointer++) = (value >> 56) & 0xff;
  *(pointer++) = (value >> 48) & 0xff;
  *(pointer++) = (value >> 40) & 0xff;
  *(pointer++) = (value >> 32) & 0xff;
  *(pointer++) = (value >> 24) & 0xff;
  *(pointer++) = (value >> 16) & 0xff;
  *(pointer++) = (value >> 8) & 0xff;
  *(pointer++) = (value) & 0xff;

  return sizeof(int64_t);
}

static inline uint32_t
writeuint32LittleEndian(uint32_t value, unsigned char* pointer)
{

#ifdef LINUX_X86

  memcpy(pointer, &value, sizeof(uint32_t));

#else

  *(pointer++) = (value)&0xff;
  *(pointer++) = (value>>8)&0xff;
  *(pointer++) = (value>>16)&0xff;
  *(pointer) = (value>>24)&0xff;

#endif

  return sizeof(uint32_t);

}

void*
myCalloc(int site, int len, int size);
void*
myMalloc(int site, int size);
void
myFree(int site, void *pointer);

static inline uint32_t
writeuint64LittleEndian(uint64_t value, unsigned char* pointer)
{

#ifdef LINUX_X86

  memcpy(pointer, &value, sizeof(uint64_t));

#else

  *(pointer++) = (value)&0xff;
  *(pointer++) = (value>>8)&0xff;
  *(pointer++) = (value>>16)&0xff;
  *(pointer++) = (value>>24)&0xff;
  *(pointer++) = (value>>32)&0xff;
  *(pointer++) = (value>>40)&0xff;
  *(pointer++) = (value>>48)&0xff;
  *(pointer) = (value>>56)&0xff;

#endif

  return sizeof(uint64_t);
}

uintptr_t
getTranslatedNativeThreadID();

// Function prototypes

void
processOptions(char *options);
bool
nullSafeCompare(const char *string1, char *string2);
bool
nullSafeCompareWildCard(const char *string, char *wildCardString);
uint32_t
fetchAndIncrement(uint32_t *number);
char*
getConstantPoolString(ClassFile* classFile, uint32_t index);
void *
getConstantPoolStringPointer(ClassFile *classFile, uint32_t index);
uint32_t
getConstantPoolStringLength(ClassFile* classFile, uint32_t index);
uint32_t
compareConstantPoolString(char* stringToCompare, ClassFile* classFile, int index);
uint32_t
compareRawConstantPoolString(char *stringToCompare, ClassFile *classFile, int index);
uint32_t
compareRawConstantPoolStringWithLength(char *stringToCompare, int stringToCompareLength, ClassFile *classFile, int index);
uint32_t
compareConstantPoolStringWithLength(char *stringToCompare, int stringToCompareLength, ClassFile *classFile, int index);
uint32_t
compareConstantPoolStringWildCard(char* stringToCompare, ClassFile* classFile, int index);
uint32_t
compareJVMString(const char *string, char *jvmString);
void
createLUTs();
//inline uintptr_t getTranslatedNativeThreadID();

//void* myCalloc(int site, int len, int size);
//void * xcalloc(size_t a, size_t b);
void
e2aBuffer(uint8_t *buffer, uint32_t length);
void
a2eBuffer(uint8_t *buffer, uint32_t length);

#endif /*UTIL_H_*/
