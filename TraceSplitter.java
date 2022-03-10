package jinsight.util;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;

public class TraceSplitter {

	
	
	static final int JINSIGHT_V2_BEEF = 0xBEEF;
	static final int JINSIGHT_V2_VERSION_1 = 1;
	static final int JINSIGHT_V2_VERSION_2 = 2;
	static final int JINSIGHT_V2_VERSION_3 = 3;
	static final int JINSIGHT_V2_VERSION_4 = 4;
	static final int JINSIGHT_V2_VERSION_5 = 5;
	static final int JINSIGHT_V2_VERSION_6 = 6;
	static final int JINSIGHT_V2_VERSION_7 = 7;
	static final int JINSIGHT_V2_VERSION_8 = 8;
	public static final int JINSIGHT_V2_UNKNOWN_PLATFORM = 0;
	public static final int JINSIGHT_V2_Win32x86 = 11;
	public static final int JINSIGHT_V2_AIXPPC = 22;
	public static final int JINSIGHT_V2_S390 = 33;
	public static final int JINSIGHT_V2_LINUXX86 = 44;
	public static final int JINSIGHT_V2_LINUX390 = 55;
	public static final int JINSIGHT_V2_SOLARISSPARC = 66;
	public static final int JINSIGHT_V2_NULL_OID = 0xffffffff;
	public static final int JINSIGHT_V2_NULL_CID = 0xffff;
	public static final int JINSIGHT_V2_NULL_MID = 0xffff;
	public static final int JINSIGHT_V2_NULL_FID = 0xffff;
	public static final int JINSIGHT_V2_NULL_TID = 0xffff;
	public static final int JINSIGHT_V2_ClassLoad = 1;
	public static final int JINSIGHT_V2_ClassUnload = 2;
	public static final int JINSIGHT_V2_ClassEvolved = 3;
	public static final int JINSIGHT_V2_ClassDefine = 4;
	public static final int JINSIGHT_V2_ArrayClassDefine = 5;
	public static final int JINSIGHT_V2_ThreadDefine = 10;
	public static final int JINSIGHT_V2_ThreadStart = 11;
	public static final int JINSIGHT_V2_ThreadExit = 12;
	public static final int JINSIGHT_V2_ThreadNameChange = 13;
	public static final int JINSIGHT_V2_ObjectDefined = 20;
	public static final int JINSIGHT_V2_ObjectCreated = 21;
	public static final int JINSIGHT_V2_ArrayDefined = 22;
	public static final int JINSIGHT_V2_ArrayCreated = 23;
	public static final int JINSIGHT_V2_ObjectReclaimed = 24;
	public static final int JINSIGHT_V2_ObjectRefs = 25;
	public static final int JINSIGHT_V2_ArrayRefs = 26;
	public static final int JINSIGHT_V2_JavaMethodEnter = 30;
	public static final int JINSIGHT_V2_JavaMethodLeave = 31;
	public static final int JINSIGHT_V2_NativeMethodEnter = 32;
	public static final int JINSIGHT_V2_NativeMethodLeave = 33;
	public static final int JINSIGHT_V2_ThreadNonBlockingWait = 40;
	public static final int JINSIGHT_V2_ThreadBlockingWait = 41;
	public static final int JINSIGHT_V2_ThreadNotified = 42;
	public static final int JINSIGHT_V2_ThreadNotify = 43;
	public static final int JINSIGHT_V2_ThreadNotifyAll = 44;
	public static final int JINSIGHT_V2_ThreadSuspend = 50;
	public static final int JINSIGHT_V2_ThreadResume = 51;
	public static final int JINSIGHT_V2_ThreadSetPriority = 52;
	public static final int JINSIGHT_V2_ThreadYield = 53;
	public static final int JINSIGHT_V2_ThreadSleep = 54;
	public static final int JINSIGHT_V2_ThreadInterrupt = 55;
	public static final int JINSIGHT_V2_MonitorEnter = 60;
	public static final int JINSIGHT_V2_MonitorBlock = 61;
	public static final int JINSIGHT_V2_MonitorLeave = 62;
	public static final int JINSIGHT_V2_GCStart = 70;
	public static final int JINSIGHT_V2_GCStop = 71;
	public static final int JINSIGHT_V2_GC_JavaStackRoot = 72;
	public static final int JINSIGHT_V2_GC_JNI_LocalRoot = 74;
	public static final int JINSIGHT_V2_GC_JNI_GlobalRoot = 75;
	public static final int JINSIGHT_V2_GC_StaticRoot = 76;
	public static final int JINSIGHT_V2_GC_UnknownRoot = 85;
	public static final int JINSIGHT_V2_GC_StickyClassRoot = 86;
	public static final int JINSIGHT_V2_GC_ThreadRoot = 87;
	public static final int JINSIGHT_V2_GC_MonitorRoot = 88;
	public static final int JINSIGHT_V2_GC_NativeStackRoot = 89;
	public static final int JINSIGHT_V2_Generation = 77;
	public static final int JINSIGHT_V2_UserEvent = 78;
	public static final int JINSIGHT_V2_ExtUserEvent = 79;
	public static final int JINSIGHT_V2_ExtUserEvent_RMI = 0;
	public static final int JINSIGHT_V2_ExtUserEvent_value = 1;
	public static final int JINSIGHT_V2_ObjectProfiling = 80;
	public static final int JINSIGHT_V2_ExecProfiling = 81;
	public static final int JINSIGHT_V2_MonitorProfiling = 82;
	public static final int JINSIGHT_V2_WideClassLoad = 90;
	public static final int JINSIGHT_V2_WideJavaMethodEnter = 91;
	public static final int JINSIGHT_V2_WideNativeMethodEnter = 92;
	public static final int JINSIGHT_V2_ExtensiveClassLoad = 93;
	public static final int JINSIGHT_V2_TraceEnd = 100;
	public static final int JINSIGHT_V2_BOBEvent = 101;
	public static final int JINSIGHT_V2_EOBEvent = 102;
	public static final int JINSIGHT_V2_ArgValueEvent = 103;
	public static final int JINSIGHT_V2_FieldValueAtEnterEvent = 104;
	public static final int JINSIGHT_V2_ReturnValueEvent = 105;
	public static final int JINSIGHT_V2_PropertyValueEvent = 106;
	public static final int JINSIGHT_V2_FieldValueAtExitEvent = 107;
	public static final int JINSIGHT_V2_RetractedEvent = 108;
	public static final int JINSIGHT_V2_ExtendedThreadDefine = 109;
	public static final int JINSIGHT_V2_ExtendedExtensiveClassLoad = 110;
	public static final int JINSIGHT_V2_ClassDefine2 = 111;
	public static final int JINSIGHT_V2_MethodProfileEvent = 153;
	public static final int JINSIGHT_V2_GlobalProfileEvent = 154;
	public static final int JINSIGHT_V2_ValueEvent = 101;
	public static final int JINSIGHT_V2_EndReportEvent = 160;
	public static final int JINSIGHT_V2_MonitorContention = 161;
	public static final int JINSIGHT_V2_EndOfFile = 255;


	public static final int ACC_NATIVE = 0x0100;
	
	
	
	private static final int BUFFER_LENGTH = 1048576;// 1024*1024;//131072;

	private static final int EXPANSION_FACTOR = 2;

	private static final int SIZEOF_INT8 = 1;
	private static final int SIZEOF_INT16 = 2;
	private static final int SIZEOF_INT32 = 4;
	private static final int SIZEOF_INT64 = 8;

	private static final int NULL = -1;
	private static final int NOT_WRITTEN = 0;
	private static final int WRITTEN = 1;

	private final static int OBJECT_NODE_LENGTH = 5;
	private final static int OBJECT_NODE_OBJECTID_OFFSET = 0;
	private final static int OBJECT_NODE_CLASSID_OFFSET = 1;
	private final static int OBJECT_NODE_THREADID_OFFSET = 2;
	private final static int OBJECT_NODE_ARRAY_TYPE_OFFSET = 3;
	private final static int OBJECT_NODE_SIZE_OFFSET = 4;

	private final static int OBJECT_HASHTABLE_SIZE = 1048576;
	private final static int OBJECT_HASHTABLE_MASK = OBJECT_HASHTABLE_SIZE - 1;

	private final static int WRITTEN_OBJECT_HASHTABLE_SIZE = 1048576;
	private final static int WRITTEN_OBJECT_HASHTABLE_MASK = WRITTEN_OBJECT_HASHTABLE_SIZE - 1;
	private final static int WRITTEN_OBJECT_INITIAL_CHAIN_LENGTH = 2;
	private final static int WRITTEN_OBJECT_NODE_LENGTH = 1;

	private final static int OBJECT_HASHTABLE_INITIAL_CHAIN_NODE_COUNT = 1;
	private final static int OBJECT_HASHTABLE_INITIAL_CHAIN_LENGTH = OBJECT_HASHTABLE_INITIAL_CHAIN_NODE_COUNT * OBJECT_NODE_LENGTH;

	private final static int THREAD_NODE_LENGTH = 4;
	private final static int THREAD_NODE_THREADID_OFFSET = 0;
//	private final static int THREAD_NODE_INTERNALID_OFFSET = 1;
	private final static int THREAD_NODE_CLASSID_OFFSET = 1;
	private final static int THREAD_NODE_OBJECTID_OFFSET = 2;
	private final static int THREAD_NODE_NAME_POINTER_OFFSET = 3;

	private final static int THREAD_HASHTABLE_SIZE = 1024;
	private final static int THREAD_HASHTABLE_MASK = THREAD_HASHTABLE_SIZE - 1;

	private final static int THREAD_HASHTABLE_INITIAL_CHAIN_NODE_COUNT = 1;
	private final static int THREAD_HASHTABLE_INITIAL_CHAIN_LENGTH = THREAD_HASHTABLE_INITIAL_CHAIN_NODE_COUNT * THREAD_NODE_LENGTH;

	private final static int CLASS_NODE_LENGTH = 7;
	private final static int CLASS_NODE_CLASSID_OFFSET = 0;
	private final static int CLASS_NODE_NAME_POINTER_OFFSET = 1;
	private final static int CLASS_NODE_SUPERCLASSID_OFFSET = 2;
	private final static int CLASS_NODE_OBJECTID_OFFSET = 3;
	private final static int CLASS_NODE_METHODS_POINTER_OFFSET = 4;
	private final static int CLASS_NODE_FIELDS_POINTER_OFFSET = 5;
	private final static int CLASS_NODE_INTERFACES_POINTER_OFFSET = 6;

	private final static int METHOD_NODE_LENGTH = 3;
	private final static int METHOD_NODE_NAME_POINTER_OFFSET = 0;
	private final static int METHOD_NODE_DESCRIPTOR_POINTER_OFFSET = 1;
	private final static int METHOD_NODE_ACCESS_FLAGS_OFFSET = 2;
	//private final static int METHOD_NODE_BANNED_OFFSET = 3;

	private final static int FIELD_NODE_LENGTH = 3;
	private final static int FIELD_NODE_NAME_POINTER_OFFSET = 0;
	private final static int FIELD_NODE_DESCRIPTOR_POINTER_OFFSET = 1;
	private final static int FIELD_NODE_ACCESS_FLAGS_OFFSET = 2;
	
	private final static int INTERFACE_NODE_LENGTH = 1;
	private final static int INTERFACE_NODE_CLASSID_OFFSET = 0;

	private final static int CLASS_HASHTABLE_SIZE = 4096;
	private final static int CLASS_HASHTABLE_MASK = CLASS_HASHTABLE_SIZE - 1;

	private final static int CLASS_HASHTABLE_INITIAL_CHAIN_NODE_COUNT = 1;
	private final static int CLASS_HASHTABLE_INITIAL_CHAIN_LENGTH = CLASS_HASHTABLE_INITIAL_CHAIN_NODE_COUNT * CLASS_NODE_LENGTH;

	private final static int CLASS_DATA_INITIAL_SIZE = 1048576;

//	private final static int THREAD_STACKS_INITIAL_SIZE = 1024;
	//private final static int THREAD_STACK_DATA_NODE_SIZE = 22;
	private static final int THREAD_STACK_DATA_MAX_STACK = 1024;

	private final static int THREAD_STACK_DATA_NODE_LENGTH = 4;
	private final static int THREAD_STACK_DATA_NODE_THREADID_OFFSET = 0;
	private final static int THREAD_STACK_DATA_NODE_CLASSID_OFFSET= 1;
	private final static int THREAD_STACK_DATA_NODE_METHODID_OFFSET= 2;
	private final static int THREAD_STACK_DATA_NODE_OBJECTID_OFFSET= 3;
	
	private int[] eventsCounts;
	
	
	private static final int STRING_DATA_INITIAL_SIZE = 1024;

	private int javaLangObject;

	
	private int headerVersion;
	private int headerPlatform;
//	private int headerNumberOfEvents;
//	private int headerMaxThreads;
//	private int headerMaxClasses;
	private int headerTicksPerMicrosecond;
	private long headerStartTicks;
	private int headerJvmStartSeconds;
	private int headerConnectionStartSeconds;
	private int headerJvmOverhead;

	private int numberOfParts = 10;
	private int eventsPerPart = 0;

	private int numberOfObjects = 0;
	private int numberOfObjectsCreated = 0;
	private int existingObjects = 0;
	private int numberOfClasses = 0;
	private int numberOfThreads = 0;
	private int numberOfEvents = 0;
	private int numberOfMethodCalls = 0;

	private int[][] objectHashtable;

	private int[][] classHashtable;

	private int[][] threadHashtable;

//	private int internalThreadIDs = 0;
	private int[] threadStackPointers;
	private int[] threadBannedStackPointers;
	private int[] threadMethodCountStackPointers;
	private long[] threadBannedEnter;
	private long[] threadToRemove;

	private int[] threadMethodCounts;
	private int[] threadObjectCounts;

	//	private byte[][] threadStackData;

	private int[][] threadStackData2;

	
	
	//	private byte[][] classData;

	private int methodsPointer = 0;
	private int[][] methods;
	private int interfacesPointer = 0;
	private int[][] interfaces;
	private int fieldsPointer = 0;
	private int[][] fields;
	
	
	private int stringsPointer = 0;
	private String[] strings;


	private int splitCharactersWritten = 0;
	
	private String currentFileName;
	private FileOutputStream fos;
	private int partsWritten = 0;
	private static String fileName;
	private boolean splitOnBursts;
	
	// use a bit mask and arrays of length/8;
	private byte[] writtenThreads;
	private byte[] writtenClasses;
	private int[][] writtenObjects; 
//	byte[] writtenObjects;
		
	//com/hcsc/eas/framework/services/logging/Logger debug (Ljava/lang/Object;)V
	//com/ibm/ejs/jms/listener/WS390ServerSession onMessage (Lcom/ibm/ejs/container/MessageDrivenBeanO;)V

	//com/ibm/ws/sib/api/jmsra/impl/JmsJcaEndpointInvokerImpl invokeEndpoint (Ljavax/resource/spi/endpoint/MessageEndpoint;Lcom/ibm/wsspi/sib/core/SIBusMessage;Lcom/ibm/wsspi/sib/core/AbstractConsumerSession;Lcom/ibm/wsspi/sib/core/SITransaction;)Z
	
//	private final String[] methodCountClasses = {"com/ibm/ejs/jms/listener/WS390ServerSession", "com/ibm/ejs/jms/listener/MDBCppUtilitiesInterfaceImpl", "com/ibm/ws/sib/api/jmsra/impl/JmsJcaEndpointInvokerImpl"};//,"com/ibm/ejs/container/MessageEndpointHandler"};
//	private final String[] methodCountMethods = {"onMessage", "onMessageReference", "invokeEndpoint"};//, "invoke"};
//	private final String[] methodCountDescriptors = {"(Lcom/ibm/ejs/container/MessageDrivenBeanO;)V", "([B[B)V", "(Ljavax/resource/spi/endpoint/MessageEndpoint;Lcom/ibm/wsspi/sib/core/SIBusMessage;Lcom/ibm/wsspi/sib/core/AbstractConsumerSession;Lcom/ibm/wsspi/sib/core/SITransaction;)Z"};//, "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;"};

	
//	private final String[] methodCountClasses = {"com/ibm/ejs/jms/listener/MDBCppUtilitiesInterfaceImpl"};
//	private final String[] methodCountMethods = {"onMessageReference"};
//	private final String[] methodCountDescriptors = {"([B[B)V"};

//	private final int[] methodCountClassIDs= {-1, -1, -1, -1};
//	private final int[] methodCountMethodIDs= {-1, -1, -1, -1};
	
//	private final String[] bannedClasses = {"com/hcsc/ccsp/eep/common/util/ToStringBuilderUtil", "com/hcsc/ccsp/eep/integration/rulesimpl/util/RuleTracerTool", "com/hcsc/eas/framework/services/logging/Logger"};
//	private final String[] bannedMethods = {"reflectionToString", "reflectionToString", "debug"};
//	private final String[] bannedDescriptors = {"(Ljava/lang/Object;)Ljava/lang/String;", "(Ljava/lang/Object;)Ljava/lang/String;", "(Ljava/lang/Object;)V"};
//	private final int[] bannedClassIDs= {-1, -1, -1};
//	private final int[] bannedMethodIDs= {-1, -1, -1};

//	private final String[] bannedClasses = {"com/ibm/ws/Transaction/JTA/TransactionImpl", "com/hcsc/bluechip/preadj/common/exception/PreAdjException", "com/hcsc/ccsp/prap/mdb/PrapOthrMDBBean"};
//	private final String[] bannedMethods = {"commit", "<init>", "processException"};
//	private final String[] bannedDescriptors = {"()V", "(Lcom/hcsc/bluechip/preadj/domaindto/ExceptionInfo;Ljava/lang/Exception;)V", "(Ljava/lang/Exception;Lcom/hcsc/bluechip/preadj/dto/claim/Claim;ILjava/lang/String;)V"};

	
//	private final int[] bannedClassIDs= {-1, -1, -1};
//	private final int[] bannedMethodIDs= {-1, -1, -1};

	private final String[] bannedClasses = {"java/lang/StringBuffer"};
	private final String[] bannedMethods = {"append"};
	private final String[] bannedDescriptors = {"*"};

	private final int[] bannedClassIDs= {-1, -1, -1};
	private final int[] bannedMethodIDs= {-1, -1, -1};
	
	private static final int BANNED = 1;
	
	private final void createFastLookupArrays() {
		// use a bit mask and arrays of length/8;
		writtenThreads = new byte[threadWaterMark+10];
		writtenClasses = new byte[classWaterMark+10];
		writtenObjects = new int[WRITTEN_OBJECT_HASHTABLE_SIZE][];
		
		threadStackPointers = new int[threadWaterMark+10];
		threadBannedStackPointers = new int[threadWaterMark+10];
		threadMethodCountStackPointers = new int[threadWaterMark+10];
		threadBannedEnter = new long[threadWaterMark+10];
		threadToRemove = new long[threadWaterMark+10];
		threadMethodCounts = new int[threadWaterMark+10];
		threadObjectCounts = new int[threadWaterMark+10];

		//threadStackData = new byte[THREAD_STACKS_INITIAL_SIZE][];
		threadStackData2 = new int[threadWaterMark+10][];


	}
	
	private final void createObjectHashtable() {

		objectHashtable = new int[OBJECT_HASHTABLE_SIZE][];

	}

	private final void createClassHashtable() {

		classHashtable = new int[CLASS_HASHTABLE_SIZE][];
//		classData = new byte[CLASS_DATA_INITIAL_SIZE][];

	}

	private final void createThreadHashtable() {

		threadHashtable = new int[THREAD_HASHTABLE_SIZE][];


	}

	private final void createStrings() {

		strings = new String[STRING_DATA_INITIAL_SIZE];

	}
	
	private final void createMethods() {

		methods = new int[CLASS_DATA_INITIAL_SIZE][];

	}

	private final void createFields() {

		fields = new int[CLASS_DATA_INITIAL_SIZE][];

	}
	
	private final void createInterfaces() {

		interfaces = new int[CLASS_DATA_INITIAL_SIZE][];

	}

	private final int addToStringData(String data) {

		int pointer = stringsPointer++;

		if (stringsPointer >= strings.length) {
			
			expandStrings();
		}

		strings[pointer] = data;

		return pointer;
	}

	private final void addObject(int objectID, int classID, int threadID, int arrayType, int size) {

		if(objectID>objectWaterMark) {
			objectWaterMark=objectID;
		}


		
		int existingObjectID = getObject(objectID);

		if (existingObjectID == NULL) {

			numberOfObjects++;

			int bucket = objectID & OBJECT_HASHTABLE_MASK;

			int[] chain = objectHashtable[bucket];

			if (chain == null) {

				objectHashtable[bucket] = new int[OBJECT_HASHTABLE_INITIAL_CHAIN_LENGTH];
				chain = objectHashtable[bucket];

			}

			int chainLength = chain.length;

			int i = 0;

			while (chain[i] != 0) {
				
				if(chain[i+OBJECT_NODE_OBJECTID_OFFSET]==objectID)
					break;

				i += OBJECT_NODE_LENGTH;
				if (i == chainLength)
					break;

			}

			if (i == chainLength) {

				int newChainLength = chainLength * 2;
				int[] tempChain = new int[newChainLength];
				System.arraycopy(chain, 0, tempChain, 0, chainLength);
				chain = tempChain;
				objectHashtable[bucket] = chain;
				chainLength = newChainLength;

			}

			chain[i + OBJECT_NODE_OBJECTID_OFFSET] = objectID;
			chain[i + OBJECT_NODE_CLASSID_OFFSET] = classID;
			chain[i + OBJECT_NODE_THREADID_OFFSET] = threadID;
			chain[i + OBJECT_NODE_ARRAY_TYPE_OFFSET] = arrayType;
			chain[i + OBJECT_NODE_SIZE_OFFSET] = size;

		} else {

			existingObjects++;

			int bucket = objectID & OBJECT_HASHTABLE_MASK;

			int[] chain = objectHashtable[bucket];

			chain[existingObjectID + OBJECT_NODE_THREADID_OFFSET] = threadID;

		}

	}

	private final int getObject(int objectID) {

		int bucket = objectID & OBJECT_HASHTABLE_MASK;

		int[] chain = objectHashtable[bucket];

		if (chain == null) {

			return -1;
		}

		int chainLength = chain.length;

		int i = 0;

		while (i < chainLength) {

			if (chain[i + OBJECT_NODE_OBJECTID_OFFSET] == objectID) {
				return i;
			}
			i += OBJECT_NODE_LENGTH;

		}

		if (i >= chainLength) {

			return -1;

		}

		return -1;

	}

/*	public int getClassLength(int classID) {

		int bucket = classID & CLASS_HASHTABLE_MASK;

		int[] chain = classHashtable[bucket];

		if (chain == null) {

			return -1;
		}

		int chainLength = chain.length;

		int i = 0;

		while (i < chainLength) {

			if (chain[i + CLASS_NODE_CLASSID_OFFSET] == classID) {
				return chain[i + CLASS_NODE_CLASS_DATA_LENGTH];
			}
			i += CLASS_NODE_LENGTH;

		}

		if (i >= chainLength) {

			return -1;

		}

		return -1;

	}
*/

	private final int[] getClassChain(int classID) {

		
		int bucket = classID & CLASS_HASHTABLE_MASK;

		int[] chain = classHashtable[bucket];

			return chain;

	}

	private final int[] getObjectChain(int objectID) {

		int bucket = objectID & OBJECT_HASHTABLE_MASK;

		int[] chain = objectHashtable[bucket];

			return chain;

	}

	private final int getObjectNode(int objectID) {

		int bucket = objectID & OBJECT_HASHTABLE_MASK;

		int[] chain = objectHashtable[bucket];

		if (chain == null) {

			return -1;
		}

		int chainLength = chain.length;

		int i = 0;

		while (i < chainLength) {

			if (chain[i + OBJECT_NODE_OBJECTID_OFFSET] == objectID) {
				return i;
			}
			i += OBJECT_NODE_LENGTH;

		}

		if (i >= chainLength) {

			return -1;

		}

		return -1;

	}
	
	private final int getClassNode(int classID) {

		int bucket = classID & CLASS_HASHTABLE_MASK;

		int[] chain = classHashtable[bucket];

		if (chain == null) {

			return -1;
		}

		int chainLength = chain.length;

		int i = 0;

		while (i < chainLength) {

			if (chain[i + CLASS_NODE_CLASSID_OFFSET] == classID) {
				return i;
			}
			i += CLASS_NODE_LENGTH;

		}

		if (i >= chainLength) {

			return -1;

		}

		return -1;

	}
	
	
	
	
	private final void addClass(int classID, int namePointer, int superClassID, int objectID, int methodsPointer, int fieldsPointer, int interfacesPointer) {

		if(classID>classWaterMark) {
			classWaterMark=classID;
		}


//		System.out.println(strings[namePointer]);
		
		numberOfClasses++;
		
		int bucket = classID & CLASS_HASHTABLE_MASK;

		int[] chain = classHashtable[bucket];

		if (chain == null) {

			classHashtable[bucket] = new int[CLASS_HASHTABLE_INITIAL_CHAIN_LENGTH];
			chain = classHashtable[bucket];

		}

		int chainLength = chain.length;

		int i = 0;

		while (chain[i] != 0) {

			i += CLASS_NODE_LENGTH;
			if (i == chainLength)
				break;

		}

		if (i == chainLength) {

			int newChainLength = chainLength * 2;
			int[] tempChain = new int[newChainLength];
			System.arraycopy(chain, 0, tempChain, 0, chainLength);
			chain = tempChain;
			classHashtable[bucket] = chain;
			chainLength = newChainLength;

		}

		chain[i + CLASS_NODE_CLASSID_OFFSET] = classID;
		chain[i + CLASS_NODE_NAME_POINTER_OFFSET] = namePointer;
		chain[i + CLASS_NODE_SUPERCLASSID_OFFSET] = superClassID;
		chain[i + CLASS_NODE_OBJECTID_OFFSET] = objectID;
		chain[i + CLASS_NODE_METHODS_POINTER_OFFSET] = methodsPointer;
		chain[i + CLASS_NODE_FIELDS_POINTER_OFFSET] = fieldsPointer;
		chain[i + CLASS_NODE_INTERFACES_POINTER_OFFSET] = interfacesPointer;

	}

	private final void addThread(int threadID, int classID, int objectID, String threadName) {

		numberOfThreads++;

		if(threadID>threadWaterMark) {
			threadWaterMark=threadID;
		}


//		int internalThreadID = internalThreadIDs++;

/*		if (internalThreadID > threadStackPointers.length) {

			int currentLength = threadStackPointers.length;
			int newLength = currentLength * 2;

			int[] newThreadStackPointers = new int[newLength];
			System.arraycopy(threadStackPointers, 0, newThreadStackPointers, 0, currentLength);

			threadStackPointers = newThreadStackPointers;

//			byte[][] newThreadStackData = new byte[newLength][];
//			System.arraycopy(threadStackData, 0, newThreadStackData, 0, currentLength);

//			threadStackData = newThreadStackData;

			int[][] newThreadStackData2 = new int[newLength][];
			System.arraycopy(threadStackData2, 0, newThreadStackData2, 0, currentLength);

			threadStackData2 = newThreadStackData2;

		
		}
*/
//		threadStackData[internalThreadID] = new byte[THREAD_STACK_DATA_NODE_SIZE * THREAD_STACK_DATA_MAX_STACK];
//		threadStackData2[internalThreadID] = new int[THREAD_STACK_DATA_NODE_LENGTH * THREAD_STACK_DATA_MAX_STACK];

		int bucket = threadID & THREAD_HASHTABLE_MASK;

		int[] chain = threadHashtable[bucket];

		if (chain == null) {

			threadHashtable[bucket] = new int[THREAD_HASHTABLE_INITIAL_CHAIN_LENGTH];
			chain = threadHashtable[bucket];

		}

		int chainLength = chain.length;

		int i = 0;

		while (chain[i] != 0) {

			i += THREAD_NODE_LENGTH;
			if (i == chainLength)
				break;

		}

		if (i == chainLength) {

			int newChainLength = chainLength * 2;
			int[] tempChain = new int[newChainLength];
			System.arraycopy(chain, 0, tempChain, 0, chainLength);
			chain = tempChain;
			threadHashtable[bucket] = chain;
			chainLength = newChainLength;

		}

		chain[i + THREAD_NODE_THREADID_OFFSET] = threadID;
//		chain[i + THREAD_NODE_INTERNALID_OFFSET] = internalThreadID;
		chain[i + THREAD_NODE_CLASSID_OFFSET] = classID;
		chain[i + THREAD_NODE_OBJECTID_OFFSET] = objectID;
		chain[i + THREAD_NODE_NAME_POINTER_OFFSET] = addToStringData(threadName);

	}

/*	public int getInternalThreadID(int threadID) {

		int bucket = threadID & THREAD_HASHTABLE_MASK;

		int[] chain = threadHashtable[bucket];

		if (chain == null) {

			return -1;
		}

		int chainLength = chain.length;

		int i = 0;

		while (i < chainLength) {

			if (chain[i + THREAD_NODE_THREADID_OFFSET] == threadID) {
				return chain[i + THREAD_NODE_INTERNALID_OFFSET];
			}
			i += THREAD_NODE_LENGTH;

		}

		if (i >= chainLength) {

			return -1;

		}

		return -1;

	}
*/
	private final int[] getThreadChain(int threadID) {

		int bucket = threadID & THREAD_HASHTABLE_MASK;

		int[] chain = threadHashtable[bucket];

		
		return chain;

	}

	private final int getThreadNode(int threadID) {

		int bucket = threadID & THREAD_HASHTABLE_MASK;

		int[] chain = threadHashtable[bucket];

		if (chain == null) {

			return -1;
		}

		int chainLength = chain.length;

		int i = 0;

		while (i < chainLength) {

			if (chain[i + THREAD_NODE_THREADID_OFFSET] == threadID) {
				return i;
			}
			i += THREAD_NODE_LENGTH;

		}

		if (i >= chainLength) {

			return -1;

		}

		return -1;

	}

	
	private final int discoverThread(int threadID, long timeStamp) {

		int eventsWritten = 0;
		
		int[] threadChain = getThreadChain(threadID);
		int threadNode = getThreadNode(threadID);
		
		if((threadChain==null)||(threadNode==-1)) {
			System.err.println("not able to find thread");
			System.exit(-1);
		}
		
		int threadNamePointer = threadChain[threadNode+THREAD_NODE_NAME_POINTER_OFFSET];
		int objectID = threadChain[threadNode+THREAD_NODE_OBJECTID_OFFSET];
		int classID = threadChain[threadNode+THREAD_NODE_CLASSID_OFFSET];
		String threadName = strings[threadNamePointer];

		if(writtenClasses[classID]==NOT_WRITTEN) {
			eventsWritten+= discoverClass(classID, timeStamp);
		}
		
		if(!checkObject(objectID)) {
			eventsWritten+= discoverObject(objectID, timeStamp, false);
		} 

		writeInt8(JINSIGHT_V2_ThreadDefine);
		
		writeInt64(timeStamp); //timeStamp
		writeInt32(threadID); // threadID
		writeInt32(objectID); // objectID
		writeInt16(classID); // classID
		writeString(threadName); // threaName

		eventsWritten++;
		
		windStack(timeStamp, threadID);
		
		writtenThreads[threadID]=WRITTEN;
		
		return eventsWritten;

	}

	private final int discoverClass(int classID, long timeStamp) {

		int eventsWritten = 0;
		
		int[] classChain = getClassChain(classID);
		int classNode = getClassNode(classID);

		if(classNode==-1){
			writtenClasses[classID]=WRITTEN;
			return 0;
		}
		
		int superClassID = classChain[classNode+CLASS_NODE_SUPERCLASSID_OFFSET];
		
		if(writtenClasses[superClassID]==NOT_WRITTEN) {
			if(superClassID!=0) {
			//System.out.println("discovering superclass:" + superClassID);
			
			eventsWritten += discoverClass(superClassID, timeStamp);
			}
		}
		
		
		int thisInterfacesPointer = classChain[classNode+CLASS_NODE_INTERFACES_POINTER_OFFSET];
		
		if(thisInterfacesPointer!=NULL) {

			int[] interfaceNodes = interfaces[thisInterfacesPointer];
			int numberOfInterfaces = interfaceNodes.length / INTERFACE_NODE_LENGTH;

			int interfaceNodePointer = 0;
			for(int i=0;i<numberOfInterfaces;i++) {
				
				int interfaceClassID = interfaceNodes[interfaceNodePointer+INTERFACE_NODE_CLASSID_OFFSET];

				if(writtenClasses[interfaceClassID]==NOT_WRITTEN) {
					eventsWritten += discoverClass(interfaceClassID, timeStamp);
				}
		
				interfaceNodePointer+=INTERFACE_NODE_LENGTH;
			}
			
			
		}
		
		eventsWritten += writeClass(classID, timeStamp);

	//	System.out.println("Written class: " + classID);
		writtenClasses[classID]=WRITTEN;

		return eventsWritten;
		
	}

	
	private final int writeClass(int classID, long timeStamp) {
		
		int eventsWritten = 0;
		
		int[] classChain = getClassChain(classID);
		int classNode = getClassNode(classID);
		
		int classNamePointer = classChain[classNode+CLASS_NODE_NAME_POINTER_OFFSET];
		int superClassID = classChain[classNode+CLASS_NODE_SUPERCLASSID_OFFSET];
		int objectID = classChain[classNode+CLASS_NODE_OBJECTID_OFFSET];
		
		int thisMethodsPointer = classChain[classNode+CLASS_NODE_METHODS_POINTER_OFFSET];
		int thisFieldsPointer = classChain[classNode+CLASS_NODE_FIELDS_POINTER_OFFSET];
		int thisInterfacesPointer = classChain[classNode+CLASS_NODE_INTERFACES_POINTER_OFFSET];
		
		
		// Class Define
		
		writeInt8(JINSIGHT_V2_ClassDefine);
		
		writeInt64(timeStamp);
		
		writeInt16(classID);
		
		writeInt32(objectID);

		eventsWritten++;
		
		// Extended Extensive Class Load
		
		writeInt8(JINSIGHT_V2_ExtendedExtensiveClassLoad);
		
		writeInt64(timeStamp);
		
		writeInt16(classID);
		
		writeInt16(0);
		
	//	System.out.println(strings[classNamePointer]);
		writeString(strings[classNamePointer]);
		
		if(thisMethodsPointer!=NULL) {
			
			int[] methodNodes = methods[thisMethodsPointer];
			int numberOfMethods = methodNodes.length / METHOD_NODE_LENGTH;

			writeInt16(numberOfMethods);
			
			int methodNodePointer = 0;
			for(int i=0;i<numberOfMethods;i++) {
				
				int namePointer = methodNodes[methodNodePointer+METHOD_NODE_NAME_POINTER_OFFSET]; 
				int descriptorPointer = methodNodes[methodNodePointer+METHOD_NODE_DESCRIPTOR_POINTER_OFFSET]; 
				int accessFlags = methodNodes[methodNodePointer+METHOD_NODE_ACCESS_FLAGS_OFFSET];
				
				writeString(strings[namePointer]);
				writeString(strings[descriptorPointer]);
				writeInt16(accessFlags);
				
				methodNodePointer+=METHOD_NODE_LENGTH;
			}

			
		} else {
			writeInt16(0);
		}
		
		if(thisFieldsPointer!=NULL) {

			int[] fieldNodes = fields[thisFieldsPointer];
			int numberOfFields = fieldNodes.length / FIELD_NODE_LENGTH;

			writeInt16(numberOfFields);
			
			int fieldNodePointer = 0;
			for(int i=0;i<numberOfFields;i++) {
				
				int namePointer = fieldNodes[fieldNodePointer+FIELD_NODE_NAME_POINTER_OFFSET]; 
				int descriptorPointer = fieldNodes[fieldNodePointer+FIELD_NODE_DESCRIPTOR_POINTER_OFFSET]; 
				int accessFlags = fieldNodes[fieldNodePointer+FIELD_NODE_ACCESS_FLAGS_OFFSET];
				
				writeString(strings[namePointer]);
				writeString(strings[descriptorPointer]);
				writeInt16(accessFlags);
				
				fieldNodePointer+=FIELD_NODE_LENGTH;
			}
			
		} else {
			writeInt16(0);
			
		}

		writeInt16(superClassID);
		
		if(thisInterfacesPointer!=NULL) {

			int[] interfaceNodes = interfaces[thisInterfacesPointer];
			int numberOfInterfaces = interfaceNodes.length / INTERFACE_NODE_LENGTH;

			writeInt16(numberOfInterfaces);
			
			int interfaceNodePointer = 0;
			for(int i=0;i<numberOfInterfaces;i++) {
				
				int interfaceClassID = interfaceNodes[interfaceNodePointer+INTERFACE_NODE_CLASSID_OFFSET];
				
				writeInt16(interfaceClassID);
				
				interfaceNodePointer+=INTERFACE_NODE_LENGTH;
			}
			
			
		} else {
			writeInt16(0);
			
		}
		
		eventsWritten++;

		return eventsWritten;
	}
	
	private final int writeObject(int objectID, long timeStamp, boolean isCreate) {
		
		int eventsWritten = 0;

		int[] objectChain = getObjectChain(objectID);
		int objectNode = getObjectNode(objectID);
		if((objectChain!=null)&&(objectNode!=-1)) {
		int classID = objectChain[objectNode+OBJECT_NODE_CLASSID_OFFSET];
		int threadID = objectChain[objectNode+OBJECT_NODE_THREADID_OFFSET];

		if(isCreate) {
			try {
			if(writtenThreads[threadID]==NOT_WRITTEN) {
				eventsWritten+= discoverThread(threadID, timeStamp);
			}
			}catch(Exception e) {
				//System.out.println(threadID);
			}
			
		}

		
		if(classID!=-1) {

			if(writtenClasses[classID]==NOT_WRITTEN) {
				eventsWritten+= discoverClass(classID, timeStamp);
			}
			
			// Object Define
			
			writeInt8(JINSIGHT_V2_ObjectDefined);
			writeInt64(timeStamp);
			writeInt32(objectID);
			writeInt16(classID);
			eventsWritten++;

			if(isCreate) {
				// Object Create

				writeInt8(JINSIGHT_V2_ObjectCreated);
				writeInt64(timeStamp);
				writeInt32(objectID);
				writeInt16(classID);
				writeInt32(threadID);
				eventsWritten++;

			}		


		
		} else {
			
			int arrayType = objectChain[objectNode+OBJECT_NODE_ARRAY_TYPE_OFFSET];
			int size = objectChain[objectNode+OBJECT_NODE_SIZE_OFFSET];

			
			// Array Define
			
			writeInt8(JINSIGHT_V2_ArrayDefined);
			writeInt64(timeStamp);
			writeInt8(arrayType);
			writeInt32(objectID);
			writeInt32(size);
			eventsWritten++;

			if(isCreate) {
				// Array Create

				writeInt8(JINSIGHT_V2_ArrayCreated);
				writeInt64(timeStamp);
				writeInt8(arrayType);
				writeInt32(objectID);
				writeInt32(size);
				writeInt32(threadID);
				eventsWritten++;

			}		
			
		}
		
		}
		return eventsWritten;
	}
	
private final void expandStrings() {
		
		int currentLength = strings.length;
		int newLength = currentLength * EXPANSION_FACTOR;
		
		String[] newStrings = new String[newLength];
		
		System.arraycopy(strings, 0, newStrings, 0, currentLength);
		strings = newStrings;
		
	}
	
	private final void expandMethods() {
		
		int currentLength = methods.length;
		int newLength = currentLength * EXPANSION_FACTOR;
		
		int[][] newMethods = new int[newLength][];
		
		System.arraycopy(methods, 0, newMethods, 0, currentLength);
		methods = newMethods;
		
	}
	private final void expandFields() {
		
		int currentLength = fields.length;
		int newLength = currentLength * EXPANSION_FACTOR;
		
		int[][] newFields = new int[newLength][];
		
		System.arraycopy(fields, 0, newFields, 0, currentLength);
		interfaces = newFields;
		
	}
	private final void expandInterfaces() {
		
		int currentLength = interfaces.length;
		int newLength = currentLength * EXPANSION_FACTOR;
		
		int[][] newInterfaces = new int[newLength][];
		
		System.arraycopy(interfaces, 0, newInterfaces, 0, currentLength);
		interfaces = newInterfaces;
		
	}
	
	private byte[] prevBuffer = new byte[BUFFER_LENGTH];
	private byte[] currBuffer = new byte[BUFFER_LENGTH];

	private byte[] writeBuffer = new byte[BUFFER_LENGTH];

	private long pseudoPointer = 0;
	private long fileLength = 0;
	private long fileLengthOnePercent = 0;
	private long prevPercentRead = 0;

	private int currBufferPointer = 0;

	private int writeBufferPointer = 0;

	private int currBufferEnd = 0;

	private FileInputStream fis;
	private boolean littleEndian = true;
//	private int classes = 0;
	private int classWaterMark=-1;
	private int threadWaterMark=-1;
	private int objectWaterMark=-1;

	private final boolean checkObject(int objectID) {
		
		int bucket = objectID & WRITTEN_OBJECT_HASHTABLE_MASK;
		
		int[] chain = writtenObjects[bucket];
		
		if(chain==null) {
			
			return false;
			
		}
		
		int chainLength = chain.length;
		
		int i=0;
		while(i<chainLength) {
			
			if(chain[i]==objectID) return true;
			
			i+=WRITTEN_OBJECT_NODE_LENGTH;
			
		}
		
		return false;
	}

	private final int discoverObject(int objectID, long timeStamp, boolean isCreate) {
		
		int eventsWritten=0;
		
		int bucket = objectID & WRITTEN_OBJECT_HASHTABLE_MASK;
		
		int[] chain = writtenObjects[bucket];
		
		if(chain==null) {
			
			chain = new int[WRITTEN_OBJECT_INITIAL_CHAIN_LENGTH];
			writtenObjects[bucket] = chain;
			
		}
		
		int chainLength = chain.length;
		
		int i=0;
		while((i<chainLength)&&(chain[i]!=0)) {
			
			if(chain[i]==objectID) return eventsWritten;
			
			i+= WRITTEN_OBJECT_NODE_LENGTH;
		}
		
		if(i>=chainLength) {
			int currentLength = chainLength;
			int newLength = chainLength*2;
			int[] newChain = new int[newLength];
			System.arraycopy(chain, 0, newChain, 0,currentLength);
			chain = newChain;
			writtenObjects[bucket] = newChain;
		}
		
		eventsWritten = writeObject(objectID, timeStamp, isCreate);
		
		chain[i] = objectID;
		
		return eventsWritten;
	}


	private final boolean checkBannedClassMethodID(int jinsightClassID,int jinsightMethodID) {
		
		int bannedClassIDsLength = bannedClassIDs.length;
		
		for(int i=0;i<bannedClassIDsLength;i++) {
			if(bannedClassIDs[i]==jinsightClassID) {
				if(bannedMethodIDs[i]==jinsightMethodID) return true;
			}
		}
		return false;
	}

/*	private final boolean checkMethodCountClassMethodID(int jinsightClassID,int jinsightMethodID) {
		
		int methodCountClassesLength = methodCountClasses.length;
		
		for(int i=0;i<methodCountClassesLength;i++) {
			if(methodCountClassIDs[i]==jinsightClassID) {
				if(methodCountMethodIDs[i]==jinsightMethodID) return true;
			}
		}
		return false;
	}


	private final int checkMethodCountClass(String className, int jinsightClassID) {
		
		int methodCountClassesLength = methodCountClasses.length;
		
		for(int i=0;i<methodCountClassesLength;i++) {
			if(methodCountClasses[i].equals(className)){
				methodCountClassIDs[i] = jinsightClassID;
				return i;
			}
		}
		return -1;
	}

*/
	
	private final int checkBannedClass(String className, int jinsightClassID) {
		
		int bannedClassesLength = bannedClasses.length;
		
		for(int i=0;i<bannedClassesLength;i++) {
			if(bannedClasses[i].equals(className)){
				bannedClassIDs[i] = jinsightClassID;
				return i;
			}
		}
		return -1;
	}
	
	private final boolean checkBannedMethod(int theClass, String methodName, String descriptor) {
		
		if((bannedMethods[theClass].equals(methodName))&&((bannedDescriptors[theClass].equals(descriptor))||(bannedDescriptors[theClass].equals("*")))) {
			return true;
		} else {
			return false;
		}
}

/*	private final boolean checkMethodCountMethod(int theClass, String methodName, String descriptor) {
		
		if((methodCountMethods[theClass].equals(methodName))&&(methodCountDescriptors[theClass].equals(descriptor))) {
			return true;
		} else {
			return false;
		}
}
*/
	
	
	private final void passOne() {
		
		int objectDefines = 0;
		int objectCreates = 0;
		int arrayDefines = 0;
		int arrayCreates = 0;

		System.out.println("Pass 1.");
		System.out.println("0....5....10...15...20...25...30...35...40...45...50...55...60...65...70...75...80...85...90...95..100%");

		boolean moreEvents = true;

		while (moreEvents) {

			int event = readInt8();

			numberOfEvents++;
			
			switch (event) {

			case JINSIGHT_V2_WideJavaMethodEnter:
				numberOfMethodCalls++;
				skip(22);
				break;

			case JINSIGHT_V2_JavaMethodLeave:
				skip(20);
				break;

			case JINSIGHT_V2_ExtendedExtensiveClassLoad: {

				skip(8);

				int classID = readInt16();

				skip(2);
				String className = readString();

				int isBannedClass = checkBannedClass(className, classID);
	//			int isMethodCountClass = checkMethodCountClass(className, classID);
				
				if(className.equals("java/lang/Object"));
				javaLangObject = classID;
				
				int classNamePointer = stringsPointer++;
				if(stringsPointer>=strings.length) {
					expandStrings();
				}

				strings[classNamePointer] = className;
				
				int thisMethodsPointer = NULL;
				int thisFieldsPointer = NULL;
				int thisInterfacesPointer = NULL;

				
				int numberOfMethods = readInt16();

				if (numberOfMethods > 0) {
					
					thisMethodsPointer = methodsPointer++;
					if(thisMethodsPointer>methods.length) {
						expandMethods();
					}
					
					int[] methodNodes = new int[numberOfMethods*METHOD_NODE_LENGTH];
					methods[thisMethodsPointer] = methodNodes;

					if(stringsPointer+(numberOfMethods*2)>=strings.length) {
						expandStrings();
					}

					int methodNodePointer = 0;
					
					for (int i = 0; i < numberOfMethods; i++) {
						
						String name = readString();
						int methodNamePointer = addToStringData(name);
//						int methodNamePointer = stringsPointer++;
//						strings[methodNamePointer] = name;

						String descriptor = readString();
						int methodDescriptorPointer = addToStringData(descriptor);
//						int methodDescriptorPointer = stringsPointer++;
//						strings[methodDescriptorPointer] = descriptor;

						
						
						int accessFlags = readInt16();
						
						//if((accessFlags&ACC_NATIVE)==ACC_NATIVE) {
							//System.out.println(name  + " " +  accessFlags);
						//}
						
						methodNodes[methodNodePointer+METHOD_NODE_NAME_POINTER_OFFSET] = methodNamePointer;
						methodNodes[methodNodePointer+METHOD_NODE_DESCRIPTOR_POINTER_OFFSET] = methodDescriptorPointer;
						methodNodes[methodNodePointer+METHOD_NODE_ACCESS_FLAGS_OFFSET] = accessFlags;

						if(isBannedClass>=0) {
							if(checkBannedMethod(isBannedClass, name, descriptor)) {
								bannedMethodIDs[isBannedClass] = i;
							}
						}
/*						if(isMethodCountClass>=0) {
							if(checkMethodCountMethod(isMethodCountClass, name, descriptor)) {
								methodCountMethodIDs[isMethodCountClass] = i;
							}
						}
*/						
						methodNodePointer+=METHOD_NODE_LENGTH;
						
					}

				}

				int numberOfFields = readInt16();

				if (numberOfFields > 0) {
					
					thisFieldsPointer = fieldsPointer++;
					if(thisFieldsPointer>fields.length) {
						expandFields();
					}
					
					int[] fieldNodes = new int[numberOfFields*FIELD_NODE_LENGTH];
					fields[thisFieldsPointer] = fieldNodes;

					if(stringsPointer+(numberOfFields*2)>=strings.length) {
						expandStrings();
					}

					int fieldNodePointer = 0;
					
					for (int i = 0; i < numberOfFields; i++) {
						
						String name = readString();
						int fieldNamePointer = addToStringData(name);
						//int fieldNamePointer = stringsPointer++;
						//strings[fieldNamePointer] = name;

						String descriptor = readString();
						int fieldDescriptorPointer = addToStringData(descriptor);
						//int fieldDescriptorPointer = stringsPointer++;
						//strings[fieldDescriptorPointer] = descriptor;
						
						int accessFlags = readInt16();
						
						fieldNodes[fieldNodePointer+FIELD_NODE_NAME_POINTER_OFFSET] = fieldNamePointer;
						fieldNodes[fieldNodePointer+FIELD_NODE_DESCRIPTOR_POINTER_OFFSET] = fieldDescriptorPointer;
						fieldNodes[fieldNodePointer+FIELD_NODE_ACCESS_FLAGS_OFFSET] = accessFlags;
						
						fieldNodePointer+=FIELD_NODE_LENGTH;
						
					}

				}
				

				int superClassID = readInt16();

				int numberOfInterfaces = readInt16();

				if (numberOfInterfaces > 0) {
					
					thisInterfacesPointer = interfacesPointer++;
					if(thisInterfacesPointer>interfaces.length) {
						expandInterfaces();
					}
					
					int[] interfaceNodes = new int[numberOfInterfaces*INTERFACE_NODE_LENGTH];
					//fields[thisInterfacesPointer] = interfaceNodes;
					interfaces[thisInterfacesPointer] = interfaceNodes;

					
					int interfaceNodePointer = 0;
					
					for (int i = 0; i < numberOfInterfaces; i++) {
						
						int interfaceClassID = readInt16();
						
						interfaceNodes[interfaceNodePointer+INTERFACE_NODE_CLASSID_OFFSET] = interfaceClassID;
						
						interfaceNodePointer+=INTERFACE_NODE_LENGTH;
						
					}

				}

				addClass(classID, classNamePointer, superClassID, 0, thisMethodsPointer, thisFieldsPointer, thisInterfacesPointer);


			}
				break;

			case JINSIGHT_V2_ClassDefine:
				skip(14);
				break;

			case JINSIGHT_V2_ArrayClassDefine:
				skip(18);
				break;

			case JINSIGHT_V2_ThreadDefine: {
				skip(8);
				int threadID = readInt32();
				int objectID = readInt32();
				int classID = readInt16();
				String threadName = readString();

				addThread(threadID, classID, objectID, threadName);

			}
				break;
			case JINSIGHT_V2_ThreadNameChange:
			skip(12);
			skipString();
				break;
			case JINSIGHT_V2_ThreadExit:
				skip(12);
				break;

			case JINSIGHT_V2_ThreadStart:
				skip(12);
				break;

			case JINSIGHT_V2_ObjectDefined: {
				objectDefines++;
				skip(8);
				int objectID = readInt32();
				int classID = readInt16();
				addObject(objectID, classID, NULL, NULL, NULL);
			}
				break;

			case JINSIGHT_V2_ObjectCreated: {
				objectCreates++;
				skip(8);
				int objectID = readInt32();
				int classID = readInt16();
				int threadID = readInt32();
				addObject(objectID, classID, threadID, NULL, NULL);
				numberOfObjectsCreated++;
			}
				break;

			case JINSIGHT_V2_ArrayDefined: {
				arrayDefines++;
				skip(8);
				int arrayType = readInt8();
				int objectID = readInt32();
				int size = readInt32();
				addObject(objectID, NULL, NULL, arrayType, size);
			}
				break;

			case JINSIGHT_V2_ArrayCreated: {
				arrayCreates++;
				skip(8);
				int arrayType = readInt8();
				int objectID = readInt32();
				int size = readInt32();
				int threadID = readInt32();
				addObject(objectID, NULL, threadID, arrayType, size);
				numberOfObjectsCreated++;
			}
				break;

			case JINSIGHT_V2_ObjectReclaimed:
				skip(12);
				break;

			case JINSIGHT_V2_MonitorLeave:
				System.err.println("MonitorLeave: UNHANDLED EVENT!!");
				break;

			case JINSIGHT_V2_GCStart:
				skip(8);
				break;
			case JINSIGHT_V2_GCStop:
				skip(8);
				break;

			case JINSIGHT_V2_GC_MonitorRoot:
				skip(4);
				break;
			case JINSIGHT_V2_TraceEnd:
				moreEvents = false;
				break;
			case JINSIGHT_V2_BOBEvent:
				skip(8);
				break;
			case JINSIGHT_V2_EOBEvent:
				skip(8);
				break;
			case JINSIGHT_V2_ClassDefine2:
				skip(18);
				break;
			case JINSIGHT_V2_EndOfFile:
				moreEvents = false;
				break;
			default:
				System.err.println("UNHANDLED EVENT!! " + event);
				moreEvents = false;
				break;
			}

		}


	}

		private final void stackDown2(int threadID, int classID, int methodID, int objectID) {
	
			if(threadStackData2[threadID]==null) {
				threadStackData2[threadID] = new int[THREAD_STACK_DATA_NODE_LENGTH * THREAD_STACK_DATA_MAX_STACK];
	
			}
	
			
			
			int stackPointer = threadStackPointers[threadID];
			
			
			if(stackPointer<0) {
				//System.err.println("oops");
				return;
				
			}
	
			int[] threadStack2 = threadStackData2[threadID];
			int stackFrameOffset = stackPointer * THREAD_STACK_DATA_NODE_LENGTH;

			// not checking for stackFrameOffset > threadStack.length here
			
			threadStack2[stackFrameOffset+THREAD_STACK_DATA_NODE_THREADID_OFFSET] = threadID;
			threadStack2[stackFrameOffset+THREAD_STACK_DATA_NODE_CLASSID_OFFSET] = classID;
			threadStack2[stackFrameOffset+THREAD_STACK_DATA_NODE_METHODID_OFFSET] = methodID;
			threadStack2[stackFrameOffset+THREAD_STACK_DATA_NODE_OBJECTID_OFFSET] = objectID;
			
			threadStackPointers[threadID] = threadStackPointers[threadID]+1;
	
		}

	private final void stackUp(int threadID) {
	
			threadStackPointers[threadID]=threadStackPointers[threadID]-1;
			
	
		}

	boolean endOfFileReached = false;
	
	private final void passTwo() {

		System.out.println("Pass 2.");
		System.out.println("0....5....10...15...20...25...30...35...40...45...50...55...60...65...70...75...80...85...90...95..100%");

		boolean moreEvents = true;
		
		int partCounter=0;
		
		while (moreEvents) {
			
			int eventsRead = 0;
			int eventsWritten = 0;
			
			
			int eventsInThisPart = Integer.MAX_VALUE;
			if(!splitOnBursts) {
				eventsInThisPart = eventsCounts[partCounter];
			}

			
			
			// memset fastArrays
			Arrays.fill(writtenThreads, 0, writtenThreads.length, (byte) 0);
			Arrays.fill(writtenClasses, 0, writtenClasses.length, (byte) 0);
			writtenObjects = new int[WRITTEN_OBJECT_HASHTABLE_SIZE][];

			boolean nextBurst = false;
			
			openNextOutputFile();

			
			
			while ((moreEvents) && (eventsRead <= eventsInThisPart) && (nextBurst==false) ) {

				int event = readInt8();
				eventsRead++;

				switch (event) {

				case JINSIGHT_V2_WideJavaMethodEnter: {

					long timeStamp = readInt64();
					int threadID = readInt32();
					int classID = readInt16();
					int methodID = readInt16();
					int objectID = readInt32();
					int lineNum = readInt16();


					if(checkBannedClassMethodID(classID, methodID)) break;
					
					
/*					
					if(threadBannedStackPointers[threadID]>0) {
						threadBannedStackPointers[threadID]++;
						break;
					}
					
					if(checkBannedClassMethodID(classID, methodID)) {
						if(threadBannedStackPointers[threadID]==0) {
							threadBannedEnter[threadID] = timeStamp;
						}
						threadBannedStackPointers[threadID]++;
					//	System.out.println(bannedStack);
						break;
					}
*/					
/*					if(threadMethodCountStackPointers[threadID]>0) {
						threadMethodCountStackPointers[threadID]++;
						threadMethodCounts[threadID]++;
					} else {

						if(checkMethodCountClassMethodID(classID, methodID)) {
							threadMethodCountStackPointers[threadID]++;
							threadMethodCounts[threadID]++;
							threadObjectCounts[threadID]=0;
						}
					}	
	*/				
					if(writtenThreads[threadID]==NOT_WRITTEN) {
						eventsWritten += discoverThread(threadID, timeStamp);
					}
					try{
					if(writtenClasses[classID]==NOT_WRITTEN) {
						eventsWritten += discoverClass(classID, timeStamp);
					}
					} catch(Exception e) {
						//System.out.println(classID);
					}
					if(objectID!=NULL) {
						if(!checkObject(objectID)) {
							eventsWritten += discoverObject(objectID, timeStamp, false);
						}
					}
					
					
					stackDown2(threadID, classID, methodID, objectID);

					if(threadToRemove[threadID]>0) {
						writeInt8(JINSIGHT_V2_WideJavaMethodEnter);
						writeInt64(timeStamp-threadToRemove[threadID]);
						writeInt32(threadID);
						writeInt16(classID);
						writeInt16(methodID);
						writeInt32(objectID);
						writeInt16(lineNum);
						
					} else {
						writePreviousBytesToOutput(23);
					}
					
					eventsWritten++;

				}
					break;

				case JINSIGHT_V2_JavaMethodLeave: {

					long timeStamp = readInt64();
					long overhead = readInt64();
					int threadID = readInt32();


					
/*					if(threadBannedStackPointers[threadID]>0) {
						threadBannedStackPointers[threadID]--;
						if(threadBannedStackPointers[threadID]==0) {
							threadToRemove[threadID] += (timeStamp - threadBannedEnter[threadID]);
							threadBannedEnter[threadID] = 0L;
						//	System.out.println(threadToRemove[threadID]);
						}

					//	System.out.println(bannedStack);
						break;
					}
*/
/*
					if(threadMethodCountStackPointers[threadID]>0) {
						threadMethodCountStackPointers[threadID]--;
						if(threadMethodCountStackPointers[threadID]==0) {
							{
								writeInt8(JINSIGHT_V2_UserEvent);
								writeInt64(timeStamp-threadToRemove[threadID]);
//								writeInt64(timeStamp);
								writeString("Methods: "+ threadMethodCounts[threadID] + "   Objects: " + threadObjectCounts[threadID]);
							}
							threadMethodCounts[threadID] = 0;
						//	System.out.println(threadToRemove[threadID]);
						}

					//	System.out.println(bannedStack);
					}

*/
					
					if(writtenThreads[threadID]==NOT_WRITTEN) {
						//timeStamp = getPreviousTimeStamp(20);
						discoverThread(threadID, timeStamp);
					}

					stackUp(threadID);
					if(threadToRemove[threadID]>0) {

						writeInt8(JINSIGHT_V2_JavaMethodLeave);
						writeInt64(timeStamp-threadToRemove[threadID]);
						writeInt64(overhead);
						writeInt32(threadID);
						
					} else {
						writePreviousBytesToOutput(21);
					}
					eventsWritten++;

				}
					break;

				case JINSIGHT_V2_ExtendedExtensiveClassLoad: {

					skip(12);
					skipString();
					
					int numberOfMethods = readInt16();

					if (numberOfMethods > 0) {
						
						for (int i = 0; i < numberOfMethods; i++) {
							skipString();
							skipString();
							skip(2);
						}

					}

					int numberOfFields = readInt16();

					if (numberOfFields > 0) {
												
						for (int i = 0; i < numberOfFields; i++) {
							skipString();
							skipString();
							skip(2);
						}

					}
					
					skip(2);

					int numberOfInterfaces = readInt16();

					if (numberOfInterfaces > 0) {
						
						for (int i = 0; i < numberOfInterfaces; i++) {
							
							skip(2);
							
						}

					}

				}
					break;

				case JINSIGHT_V2_ClassDefine:
				{
					skip(14);
				}
					break;

				case JINSIGHT_V2_ArrayClassDefine:
				{
				/*	long timeStamp = readInt64();
					int objectID = readInt32();
					int classID = readInt16();
					int size = readInt32();
					
					//if(classID==0) classID = javaLangObject;
					
					discoverClass(classID, timeStamp);
					writeInt8(JINSIGHT_V2_ArrayClassDefine);
					writeInt64(timeStamp);
					writeInt32(objectID);
					writeInt16(classID);
					writeInt32(size); */
					
					
					skip(18); // what do do here?
					writePreviousBytesToOutput(19);
					eventsWritten++;
				}
					break;

				case JINSIGHT_V2_ThreadDefine: {
					skip(18);
					skipString();

				}
					break;
				case JINSIGHT_V2_ThreadNameChange:
				{
					long timeStamp = readInt64();
					int threadID = readInt32();
					String newThreadName = readString();
					
					if(writtenThreads[threadID]==NOT_WRITTEN) {
						eventsWritten += discoverThread(threadID, timeStamp);
					}
					
					int[] threadChain = getThreadChain(threadID);
					int threadNode = getThreadNode(threadID);
					
					int threadNamePointer = threadChain[threadNode + THREAD_NODE_NAME_POINTER_OFFSET];
					
					strings[threadNamePointer] = newThreadName;
					
					writeInt8(JINSIGHT_V2_ThreadNameChange);
					writeInt64(timeStamp);
					writeInt32(threadID);
					writeString(newThreadName);
					eventsWritten++;
					
				}

				
/*				{
					long timeStamp = readInt64();  // what do do here?
					int threadID = readInt32();
					String newName = readString();
					
					writeInt8(JINSIGHT_V2_ThreadNameChange);
					writeInt64(timeStamp);
					writeInt32(threadID);
					writeString(newName);
					eventsWritten++;
					
				} */
					break;
				case JINSIGHT_V2_ThreadExit:
				{
					long timeStamp = readInt64();  // what do do here?
					int threadID = readInt32();
					
					if(writtenThreads[threadID]==NOT_WRITTEN) {
						eventsWritten += discoverThread(threadID, timeStamp);
					}
//					discoverThread(threadID, timeStamp);
					
					writeInt8(JINSIGHT_V2_ThreadExit);
					writeInt64(timeStamp);
					writeInt32(threadID);
					
					threadStackPointers[threadID]=0;
					threadBannedStackPointers[threadID]=0;
					
					eventsWritten++;
				}
					break;

				case JINSIGHT_V2_ThreadStart:
				{
					skip(12);  // what do do here?
				}
					break;

				case JINSIGHT_V2_ObjectDefined:
					skip(14);
					break;

				case JINSIGHT_V2_ObjectCreated:
				{
					long timeStamp = readInt64();
					int objectID = readInt32();
					skip(2);
					int threadID = readInt32();
					
					try {
					if(threadBannedStackPointers[threadID]==0) {
						threadObjectCounts[threadID]++;
					} } catch(Exception e) {
						System.out.println("ERROR: " + pseudoPointer);
						System.exit(0);
					}
					
					if(objectID!=NULL) {
						checkObject(objectID);
						if(!checkObject(objectID)) {
							//long timeStamp = getPreviousTimeStamp(18);
							eventsWritten += discoverObject(objectID, timeStamp, true);
						}
					}
				}	
					break;

				case JINSIGHT_V2_ArrayDefined:
				{
				/*	long timeStamp = readInt64();
					int type = readInt8();
					int objectID = readInt32();
					int size = readInt32();

//					if(classID==0) classID = javaLangObject;
					
//					discoverClass(classID, timeStamp);
					writeInt8(JINSIGHT_V2_ArrayDefined);
					writeInt64(timeStamp);
					writeInt8(type);
					writeInt32(objectID);
					writeInt32(size); */
					
					skip(17); // what do do here?
					writePreviousBytesToOutput(18);
					eventsWritten++;

				}	
					break;

				case JINSIGHT_V2_ArrayCreated:
				{
					long timeStamp = readInt64();
					skip(1);
					int objectID = readInt32();
					skip(4);
					int threadID = readInt32();

					if(threadBannedStackPointers[threadID]==0) {
						threadObjectCounts[threadID]++;
					}
					
					if(objectID!=NULL) {
						if(!checkObject(objectID)) {
							//long timeStamp = getPreviousTimeStamp(21);
							eventsWritten += discoverObject(objectID, timeStamp, true);
						}
					}
				}
					break;

				case JINSIGHT_V2_ObjectReclaimed:
					skip(12);
					writePreviousBytesToOutput(13); // change this to copyEventToOutput
					eventsWritten++;
					break;

				case JINSIGHT_V2_MonitorLeave:
					System.err.println("MonitorLeave: UNHANDLED EVENT!!");
					break;

				case JINSIGHT_V2_GCStart:
					skip(8);
					writePreviousBytesToOutput(9); // change this to copyEventToOutput
					eventsWritten++;
					break;

				case JINSIGHT_V2_GCStop:
					skip(8);
					writePreviousBytesToOutput(9); // change this to copyEventToOutput
					eventsWritten++;
					break;

				case JINSIGHT_V2_GC_MonitorRoot:
					skip(4);
					break;
				case JINSIGHT_V2_TraceEnd:
					moreEvents = false;
					break;
				case JINSIGHT_V2_BOBEvent:
					skip(8);
					break;
				case JINSIGHT_V2_EOBEvent:
					skip(8);
					if(splitOnBursts) nextBurst=true;
					break;
				case JINSIGHT_V2_ClassDefine2:
					skip(18);
					break;
				case JINSIGHT_V2_EndOfFile:
					moreEvents = false;
					break;
				default:
					// System.err.println("UNHANDLED EVENT!! ");

					moreEvents = false;
					break;
				}
				
				if(endOfFileReached) {
					event=0;
					moreEvents=false;
				}

			}
			
			
			closeCurrentOutputFile(eventsWritten);

			if (splitCharactersWritten < 1) {
				System.out.print('|');
				splitCharactersWritten++;
			}
			
			partCounter++;

		}

	}

	
	
	private final int windStack(long timeStamp, int threadID) {

				
				int eventsWritten = 0;
				
					
					if(threadStackData2[threadID]!=null) {
						
						int threadStackPointer = threadStackPointers[threadID];
						int[] threadStack = threadStackData2[threadID];
						
						
						for(int j=0;j<threadStackPointer;j++) {
						
							
							int stackFramePointer = j * THREAD_STACK_DATA_NODE_LENGTH;
							
							int classID = threadStack[stackFramePointer+THREAD_STACK_DATA_NODE_CLASSID_OFFSET];
							int methodID = threadStack[stackFramePointer+THREAD_STACK_DATA_NODE_METHODID_OFFSET];
							int objectID = threadStack[stackFramePointer+THREAD_STACK_DATA_NODE_OBJECTID_OFFSET];

							if(writtenClasses[classID]==NOT_WRITTEN) {
								eventsWritten += discoverClass(classID, timeStamp);
							}
							if(objectID!=-1) {
								if(!checkObject(objectID)) {
									eventsWritten += discoverObject(objectID, timeStamp, false);
								}
							}
							
							// Enter
							
							writeInt8(JINSIGHT_V2_WideJavaMethodEnter);
							writeInt64(timeStamp);
							writeInt32(threadID);
							writeInt16(classID);
							writeInt16(methodID);
							writeInt32(objectID);
							writeInt16(0);
							
							eventsWritten++;
							
						}
						
					}

				return eventsWritten;
				

				
			}

	
	


	private final int skip(int length) {

		if (currBufferPointer + length > currBufferEnd) {

			int leftToSkip = length - (currBufferEnd - currBufferPointer);

			int read = readBuffer(0);

			if(read<0) {
				return length;
			}
				
			skip(leftToSkip);

		} else {

			currBufferPointer += length;
		}

		return length;

	}

	private final int skipString() {

		int stringLength = readInt16();

		if (currBufferPointer + stringLength >= currBufferEnd) {

			int leftToSkip = stringLength - (currBufferEnd - currBufferPointer);

			readBuffer(0);

			skip(leftToSkip);

		} else {

			currBufferPointer += stringLength;
		}

		return stringLength + 2;

	}

	private final byte readInt8() {

		if (currBufferPointer >= currBufferEnd) {
			readBuffer(0);
		}

		return currBuffer[currBufferPointer++];

	}

	private final int readInt16() {

		byte byte0;
		byte byte1;

		if (currBufferPointer + 2 > currBufferEnd) {

			// int dummy = (currBufferEnd - (currBufferPointer + 2));

			int remaining = (currBufferEnd - currBufferPointer);

			readBuffer(remaining);

		}

		byte0 = currBuffer[currBufferPointer++];
		byte1 = currBuffer[currBufferPointer++];

		int value;

		if (littleEndian) {
			value = ((byte1 & 0xff) << 8) | (byte0 & 0xff);
		} else {
			value = ((byte0 & 0xff) << 8) | (byte1 & 0xff);
		}

		return value;

	}

	private final int readInt32() {

		byte byte0;
		byte byte1;
		byte byte2;
		byte byte3;

		if (currBufferPointer + 4 > currBufferEnd) {

			// int remaining = (currBufferEnd - (currBufferPointer+4)) * -1;
			int remaining = (currBufferEnd - currBufferPointer);
			readBuffer(remaining);

		}

		byte0 = currBuffer[currBufferPointer++];
		byte1 = currBuffer[currBufferPointer++];
		byte2 = currBuffer[currBufferPointer++];
		byte3 = currBuffer[currBufferPointer++];

		int value;

		if (littleEndian) {
			value = (((byte3 & 0xff) << 24) | ((byte2 & 0xff) << 16) | ((byte1 & 0xff) << 8) | (byte0 & 0xff));
		} else {
			value = ((byte0 & 0xff) << 24) | ((byte1 & 0xff) << 16) | ((byte2 & 0xff) << 8) | ((byte3 & 0xff));
		}

		return value;

	}

	private final long readInt64() {

		byte byte0;
		byte byte1;
		byte byte2;
		byte byte3;
		byte byte4;
		byte byte5;
		byte byte6;
		byte byte7;

		if (currBufferPointer + 8 > currBufferEnd) {

			// int remaining = (currBufferEnd - (currBufferPointer+8)) * -1;
			int remaining = (currBufferEnd - currBufferPointer);
			readBuffer(remaining);

		}

		byte0 = currBuffer[currBufferPointer++];
		byte1 = currBuffer[currBufferPointer++];
		byte2 = currBuffer[currBufferPointer++];
		byte3 = currBuffer[currBufferPointer++];
		byte4 = currBuffer[currBufferPointer++];
		byte5 = currBuffer[currBufferPointer++];
		byte6 = currBuffer[currBufferPointer++];
		byte7 = currBuffer[currBufferPointer++];

		long value;

		if (littleEndian) {
			value = ((byte7 & 0xffL) << 56) | ((byte6 & 0xffL) << 48) | ((byte5 & 0xffL) << 40) | ((byte4 & 0xffL) << 32) | ((byte3 & 0xffL) << 24) | ((byte2 & 0xffL) << 16) | ((byte1 & 0xffL) << 8) | (byte0 & 0xffL);
		} else {
			value = ((byte0 & 0xffL) << 56) | ((byte1 & 0xffL) << 48) | ((byte2 & 0xffL) << 40) | ((byte3 & 0xffL) << 32) | ((byte4 & 0xffL) << 24) | ((byte5 & 0xffL) << 16) | ((byte6 & 0xffL) << 8) | (byte7 & 0xffL);
		}

		return value;

	}

	private final int writeInt8(int value) {

		try {

			if (writeBufferPointer + SIZEOF_INT8 >= BUFFER_LENGTH) {
				fos.write(writeBuffer, 0, writeBufferPointer);
				writeBufferPointer = 0;
			}

		} catch (Exception e) {
			System.err.println(e);
		}

		writeBuffer[writeBufferPointer++] = (byte) ((value) & 0xff);

		return SIZEOF_INT8;
		
	}

	private final int writeInt16(int value) {

		try {

			if (writeBufferPointer + SIZEOF_INT16 >= BUFFER_LENGTH) {
				fos.write(writeBuffer, 0, writeBufferPointer);
				writeBufferPointer = 0;
			}

		} catch (Exception e) {
			System.err.println(e);
		}

		if (littleEndian) {

			writeBuffer[writeBufferPointer++] = (byte) ((value) & 0xff);
			writeBuffer[writeBufferPointer++] = (byte) ((value >> 8) & 0xff);

		} else {

			writeBuffer[writeBufferPointer++] = (byte) ((value >> 8) & 0xff);
			writeBuffer[writeBufferPointer++] = (byte) ((value) & 0xff);

		}
		return SIZEOF_INT16;
	}

	private final int writeInt32(int value) {
		
		try {

			if (writeBufferPointer + SIZEOF_INT32 >= BUFFER_LENGTH) {
				fos.write(writeBuffer, 0, writeBufferPointer);
				writeBufferPointer = 0;
			}

		} catch (Exception e) {
			System.err.println(e);
		}

		if (littleEndian) {

			writeBuffer[writeBufferPointer++] = (byte) ((value) & 0xff);
			writeBuffer[writeBufferPointer++] = (byte) ((value >> 8) & 0xff);
			writeBuffer[writeBufferPointer++] = (byte) ((value >> 16) & 0xff);
			writeBuffer[writeBufferPointer++] = (byte) ((value >> 24) & 0xff);

		} else {

			writeBuffer[writeBufferPointer++] = (byte) ((value >> 24) & 0xff);
			writeBuffer[writeBufferPointer++] = (byte) ((value >> 16) & 0xff);
			writeBuffer[writeBufferPointer++] = (byte) ((value >> 8) & 0xff);
			writeBuffer[writeBufferPointer++] = (byte) ((value) & 0xff);

		}
		
		return SIZEOF_INT32;

	}

	private final int writeInt64(long value) {
		try {

			if (writeBufferPointer + SIZEOF_INT64 >= BUFFER_LENGTH) {
				fos.write(writeBuffer, 0, writeBufferPointer);
				writeBufferPointer = 0;
			}

		} catch (Exception e) {
			System.err.println(e);
		}

		if (littleEndian) {

			writeBuffer[writeBufferPointer++] = (byte) ((value) & 0xff);
			writeBuffer[writeBufferPointer++] = (byte) ((value >>> 8) & 0xff);
			writeBuffer[writeBufferPointer++] = (byte) ((value >>> 16) & 0xff);
			writeBuffer[writeBufferPointer++] = (byte) ((value >>> 24) & 0xff);
			writeBuffer[writeBufferPointer++] = (byte) ((value >>> 32) & 0xff);
			writeBuffer[writeBufferPointer++] = (byte) ((value >>> 40) & 0xff);
			writeBuffer[writeBufferPointer++] = (byte) ((value >>> 48) & 0xff);
			writeBuffer[writeBufferPointer++] = (byte) ((value >>> 56) & 0xff);

		} else {

			writeBuffer[writeBufferPointer++] = (byte) ((value >>> 56) & 0xff);
			writeBuffer[writeBufferPointer++] = (byte) ((value >>> 48) & 0xff);
			writeBuffer[writeBufferPointer++] = (byte) ((value >>> 40) & 0xff);
			writeBuffer[writeBufferPointer++] = (byte) ((value >>> 32) & 0xff);
			writeBuffer[writeBufferPointer++] = (byte) ((value >>> 24) & 0xff);
			writeBuffer[writeBufferPointer++] = (byte) ((value >>> 16) & 0xff);
			writeBuffer[writeBufferPointer++] = (byte) ((value >>> 8) & 0xff);
			writeBuffer[writeBufferPointer++] = (byte) ((value) & 0xff);

		}
		
		return SIZEOF_INT64;
	}
	
	
	private final long getPreviousTimeStamp(int startsThisManyBack) { // hacky method
		
		long value=0;
		
		if ((currBufferPointer - startsThisManyBack) >= 0) { // all in curr buffer

			int pointer = currBufferPointer - startsThisManyBack;
			
			byte byte0 = currBuffer[pointer++];
			byte byte1 = currBuffer[pointer++];
			byte byte2 = currBuffer[pointer++];
			byte byte3 = currBuffer[pointer++];
			byte byte4 = currBuffer[pointer++];
			byte byte5 = currBuffer[pointer++];
			byte byte6 = currBuffer[pointer++];
			byte byte7 = currBuffer[pointer++];


			if (littleEndian) {
				value = ((byte7 & 0xffL) << 56) | ((byte6 & 0xffL) << 48) | ((byte5 & 0xffL) << 40) | ((byte4 & 0xffL) << 32) | ((byte3 & 0xffL) << 24) | ((byte2 & 0xffL) << 16) | ((byte1 & 0xffL) << 8) | (byte0 & 0xffL);
			} else {
				value = ((byte0 & 0xffL) << 56) | ((byte1 & 0xffL) << 48) | ((byte2 & 0xffL) << 40) | ((byte3 & 0xffL) << 32) | ((byte4 & 0xffL) << 24) | ((byte5 & 0xffL) << 16) | ((byte6 & 0xffL) << 8) | (byte7 & 0xffL);
			}

		} else {

						
			int bytesInCurrentBuffer = currBufferPointer;
			int bytesInPrevBuffer = startsThisManyBack - bytesInCurrentBuffer;
			int prevFrom = (prevBuffer.length - bytesInPrevBuffer);

			
			
			if(bytesInPrevBuffer>=SIZEOF_INT64) { // all in prev buffer

				int byte0 = currBuffer[prevFrom++];
				int byte1 = currBuffer[prevFrom++];
				int byte2 = currBuffer[prevFrom++];
				int byte3 = currBuffer[prevFrom++];
				int byte4 = currBuffer[prevFrom++];
				int byte5 = currBuffer[prevFrom++];
				int byte6 = currBuffer[prevFrom++];
				int byte7 = currBuffer[prevFrom++];


				if (littleEndian) {
					value = ((byte7 & 0xffL) << 56) | ((byte6 & 0xffL) << 48) | ((byte5 & 0xffL) << 40) | ((byte4 & 0xffL) << 32) | ((byte3 & 0xffL) << 24) | ((byte2 & 0xffL) << 16) | ((byte1 & 0xffL) << 8) | (byte0 & 0xffL);
				} else {
					value = ((byte0 & 0xffL) << 56) | ((byte1 & 0xffL) << 48) | ((byte2 & 0xffL) << 40) | ((byte3 & 0xffL) << 32) | ((byte4 & 0xffL) << 24) | ((byte5 & 0xffL) << 16) | ((byte6 & 0xffL) << 6) | (byte7 & 0xffL);
				}

				
			} else { // split across the buffers
				

				bytesInCurrentBuffer = SIZEOF_INT64 - bytesInPrevBuffer;
				
				byte[] bytes = new byte[8];
				int valuePointer = 0;
				for(int i=0;i<bytesInPrevBuffer;i++) {

					bytes[valuePointer++] = prevBuffer[prevFrom++];
				}
				
				for(int i=0;i<bytesInCurrentBuffer;i++) {

					bytes[valuePointer++] = currBuffer[i];
					
				}
				
				if (littleEndian) {
					value = ((bytes[7] & 0xffL) << 56) | ((bytes[6] & 0xffL) << 48) | ((bytes[5] & 0xffL) << 40) | ((bytes[4] & 0xffL) << 32) | ((bytes[3] & 0xffL) << 24) | ((bytes[2] & 0xffL) << 16) | ((bytes[1] & 0xffL) << 8) | (bytes[0] & 0xffL);
				} else {
					value = ((bytes[0] & 0xffL) << 56) | ((bytes[1] & 0xffL) << 48) | ((bytes[2] & 0xffL) << 40) | ((bytes[3] & 0xffL) << 32) | ((bytes[4] & 0xffL) << 24) | ((bytes[5] & 0xffL) << 16) | ((bytes[6] & 0xffL) << 6) | (bytes[7] & 0xffL);
				}
				
			}
			

		}
		
		return value;
		
	}
	

	private final int writeString(String string) {

		int stringLength = string.length();
		int totalLength = stringLength + SIZEOF_INT16;

		try {

			if (writeBufferPointer + totalLength >= BUFFER_LENGTH) {
				fos.write(writeBuffer, 0, writeBufferPointer);
				writeBufferPointer = 0;
			}

		} catch (Exception e) {
			System.err.println(e);
		}

		writeInt16(stringLength);
		
		// not using utf8 here
		char[] stringChars = string.toCharArray();
		for(int i=0;i<stringLength;i++) {
			writeBuffer[writeBufferPointer++] = (byte) stringChars[i];
		}

		return totalLength;
	}

	
	private final String readString() {

		int stringLength = readInt16();

		if (currBufferPointer + stringLength >= currBufferEnd) {

			int remaining = stringLength - (stringLength - (currBufferEnd - currBufferPointer));

			readBuffer(remaining);

		}

		String string = new String(currBuffer, currBufferPointer, stringLength);

		currBufferPointer += stringLength;

		return string;

	}

	private final void setupBuffers() throws Exception {

		pseudoPointer = 0;
		prevPercentRead = 0;
		try {

			int lengthRead = fis.read(currBuffer);

			currBufferPointer = 0;
			currBufferEnd = lengthRead;

		} catch (Exception e) {
			throw e;
		}

	}

	private final int readBuffer(int remaining) {

		try {
			int toRead = BUFFER_LENGTH - remaining;

			System.arraycopy(currBuffer, 0, prevBuffer, remaining, toRead);

			System.arraycopy(currBuffer, currBufferPointer, currBuffer, 0, remaining);

			int lengthRead = 0;

			try {
				lengthRead = fis.read(currBuffer, remaining, toRead);
			} catch (Exception e) {
				System.err.println(e);
			}

			if (lengthRead < 0) {
				 //System.err.println("File End");
				 return -1;
			}

			currBufferPointer = 0;
			currBufferEnd = lengthRead + remaining;

			pseudoPointer += lengthRead;
			long percentRead = pseudoPointer / fileLengthOnePercent;
			if (percentRead == prevPercentRead + 1) {
				// System.out.println(pseudoPointer / fileLengthOnePercent +
				// "%");
				for (int i = 0; i < 1 - splitCharactersWritten; i++) {
					System.out.print('.');
				}
				splitCharactersWritten = 0;
				prevPercentRead = percentRead;
			}
			
			return lengthRead;

		} catch (Exception e) {
			System.err.println(e);
		}

		return -1;
		

	}

/*	private final void readPreviousBytesIntoArray(byte[] byteArray, int length) {

		if ((currBufferPointer - length) >= 0) {

			System.arraycopy(currBuffer, currBufferPointer - length, byteArray, 0, length);

		} else {

			int bytesInCurrentBuffer = currBufferPointer;
			int bytesInPrevBuffer = length - bytesInCurrentBuffer;
			int prevFrom = (prevBuffer.length - bytesInPrevBuffer);

			int pointer = 0;
			System.arraycopy(byteArray, pointer, prevBuffer, prevFrom, bytesInPrevBuffer);
			pointer += bytesInPrevBuffer;
			System.arraycopy(byteArray, pointer, currBuffer, 0, bytesInCurrentBuffer);

		}
	}
*/
	private final void flushWriteBuffer() {

		try {
				fos.write(writeBuffer, 0, writeBufferPointer);
				writeBufferPointer = 0;

		} catch (Exception e) {
			System.err.println(e);
		}
		
	}	
	
	private final void writeBuffer(byte[] byteArray, int offset, int length) {

		try {

			if (writeBufferPointer + length >= BUFFER_LENGTH) {
				fos.write(writeBuffer, 0, writeBufferPointer);
				writeBufferPointer = 0;
			}

		} catch (Exception e) {
			System.err.println(e);
		}

		System.arraycopy(byteArray, offset, writeBuffer, writeBufferPointer, length);
		writeBufferPointer += length;

	}

/*	private final void writeBuffer(byte[] byteArray) {

		int offset = 0;
		int length = byteArray.length;

		try {

			if (writeBufferPointer + length >= BUFFER_LENGTH) {
				fos.write(writeBuffer, 0, writeBufferPointer);
				writeBufferPointer = 0;
			}

		} catch (Exception e) {
			System.err.println(e);
		}

		System.arraycopy(byteArray, offset, writeBuffer, writeBufferPointer, length);
		writeBufferPointer += length;

	}
*/
	private final void writePreviousBytesToOutput(int length) {

		int currFrom = currBufferPointer - length;

		if (currFrom >= 0) {

			writeBuffer(currBuffer, currFrom, length);

		} else {

			int bytesInCurrentBuffer = currBufferPointer;
			int bytesInPrevBuffer = length - bytesInCurrentBuffer;
			int prevFrom = (prevBuffer.length - bytesInPrevBuffer);

			writeBuffer(prevBuffer, prevFrom, bytesInPrevBuffer);
			writeBuffer(currBuffer, 0, bytesInCurrentBuffer);

		}
	}

	private final void openFile(String fileName) throws Exception {

		try {

			File file = new File(fileName);

			fileLength = file.length();
			fileLengthOnePercent = fileLength / 100L;

			fis = new FileInputStream(file);

			
		} catch (Exception e) {

			throw e;
		}

	}

	private final void closeFile() throws Exception {

		try {

			fis.close();

		} catch (Exception e) {

			throw e;
		}

	}

	private final void skipHeader() {

		skip(45);
	}


	private final void reportStatistics() {

		System.out.println();
		System.out.println("Events:   " + numberOfEvents);
		System.out.println("Methods:  " + numberOfMethodCalls);
		System.out.println("Threads:  " + numberOfThreads);
		System.out.println("Classes:  " + numberOfClasses);
		System.out.println("Objects:  " + numberOfObjects);
		System.out.println("Created:  " + numberOfObjectsCreated);
		System.out.println();
		System.gc();

		long OneMeg = 1024 * 1024;

		long max = Runtime.getRuntime().maxMemory() / OneMeg;
		long total = Runtime.getRuntime().totalMemory() / OneMeg;
		long free = Runtime.getRuntime().freeMemory() / OneMeg;

		System.out.println("Heap Max:  " + max + "M");
		System.out.println("Heap Size: " + total + "M");
		System.out.println("Heap Used: " + (total - free) + "M");
		System.out.println("Heap Free: " + free + "M");
		System.out.println();

	}

	private final void reportHeader() {

		System.out.println("File Length: " + fileLength + " bytes");

		System.out.print("Platform: ");

		switch (headerPlatform) {
		case JINSIGHT_V2_S390:
			System.out.println("z/OS");
			break;
		case JINSIGHT_V2_AIXPPC:
			System.out.println("AIX");
			break;
		case JINSIGHT_V2_LINUX390:
			System.out.println("zLinux");
			break;
		case JINSIGHT_V2_SOLARISSPARC:
			System.out.println("Solaris");
			break;
		case JINSIGHT_V2_Win32x86:
			System.out.println("Windows");
			break;
		case JINSIGHT_V2_LINUXX86:
			System.out.println("x86 Linux");
			break;
		default:
			System.out.println("Unknown");
			break;
		}
		
		
		System.out.println();

	}

	private final void calculateSplitFiles() {
		
		
		if(numberOfParts==0) {
		
			splitOnBursts = true;
			eventsPerPart= Integer.MAX_VALUE;
			
		} else {

			eventsPerPart = numberOfEvents / numberOfParts;
		
			int remainder = numberOfEvents - (eventsPerPart * numberOfParts);
		
			eventsCounts = new int[numberOfParts];
		
			eventsCounts[0] = eventsPerPart + remainder;
			for(int i=0; i<numberOfParts;i++) {
			
				eventsCounts[i] = eventsPerPart ;
			
			}
		
			eventsPerPart += remainder;
		
		}
		
		System.out.println("Number of Parts:" + numberOfParts);
		System.out.println("Events per Part:" + eventsPerPart);
		System.out.println();

	}
	private final void cleanUpAndExit() {
		endOfFileReached=true;
		
	}
	private final void openNextOutputFile() {

		
		
		try {
			currentFileName = fileName + "." + ++partsWritten;
			fos = new FileOutputStream(currentFileName);
		} catch (Exception e) {
			System.err.println(e);
		}

		writeHeader();

	}

	private final void closeCurrentOutputFile(int actualNumberOfEventsWritten) {
		try {

			writeInt8(JINSIGHT_V2_TraceEnd);
			writeInt32(0x00DEAD);
			writeInt32(0x00DEAD);
			
			flushWriteBuffer();
			fos.close();
			
			RandomAccessFile raf = new RandomAccessFile(currentFileName, "rw");
			
			raf.seek(9);
			
			byte[] littleEndiaEventsInThisPart = {0,0,0,0};
			
			littleEndiaEventsInThisPart[0] = (byte) ((actualNumberOfEventsWritten) & 0xff);
			littleEndiaEventsInThisPart[1] = (byte) ((actualNumberOfEventsWritten>>>8) & 0xff);
			littleEndiaEventsInThisPart[2] = (byte) ((actualNumberOfEventsWritten>>>16) & 0xff);
			littleEndiaEventsInThisPart[3] = (byte) ((actualNumberOfEventsWritten>>>24) & 0xff);
			
			raf.write(littleEndiaEventsInThisPart);
			raf.close();
			
			
		} catch (Exception e) {
			System.err.println(e);
		}
		
		
	}

	private final void readHeader() {

		byte binary = readInt8();
		if (binary != 'b') {
			System.err.println("Not a binary trace file, " + (char) binary);
			System.exit(-7);
		}

		headerVersion = readInt32();
		headerPlatform = readInt32();

		if (headerVersion < JINSIGHT_V2_VERSION_7) {
			System.err.println("Unsupported Version, " + headerVersion);
			System.exit(-7);
		}

	//	headerNumberOfEvents = readInt32();
		skip(4);
	//	headerMaxThreads = readInt32();
		skip(4);
	//	headerMaxClasses = readInt32();
		skip(4);

		headerTicksPerMicrosecond = readInt32();
		
		System.out.println("TicksPerMicrosecond+ " + headerTicksPerMicrosecond);
		headerTicksPerMicrosecond = 5000;
		headerStartTicks = readInt64();
		System.out.println("StartTicks+ " + headerStartTicks);

		headerJvmStartSeconds = readInt32();
		System.out.println("JvmStartSeconds+ " + headerJvmStartSeconds);

		headerConnectionStartSeconds = readInt32();
		System.out.println("ConnectionStartSeconds+ " + headerConnectionStartSeconds);

		headerJvmOverhead = readInt32();
		System.out.println("JvmOverhead+ " + headerJvmOverhead);

		switch (headerPlatform) {
		case JINSIGHT_V2_S390:
			littleEndian = false;
			break;
		case JINSIGHT_V2_AIXPPC:
			littleEndian = false;
			break;
		case JINSIGHT_V2_LINUX390:
			littleEndian = false;
			break;
		case JINSIGHT_V2_SOLARISSPARC:
			littleEndian = false;
			break;
		default:
			littleEndian = true;
			break;
		}
	}
	
	private final void setupSpecials() {
		
		

		int classID = 0;

		String className = "Unknown";

		int classNamePointer = stringsPointer++;

		if(stringsPointer>=strings.length) {
			expandStrings();
		}

		strings[classNamePointer] = className;
		
		int thisMethodsPointer = NULL;
		int thisFieldsPointer = NULL;
		int thisInterfacesPointer = NULL;

		
		int numberOfMethods = 256;

		if (numberOfMethods > 0) {
			
			thisMethodsPointer = methodsPointer++;
			if(thisMethodsPointer>methods.length) {
				expandMethods();
			}
			
			int[] methodNodes = new int[numberOfMethods*METHOD_NODE_LENGTH];
			methods[thisMethodsPointer] = methodNodes;

			if(stringsPointer+(numberOfMethods*2)>=strings.length) {
				expandStrings();
			}

			int methodNodePointer = 0;
			
			for (int i = 0; i < numberOfMethods; i++) {
				
				int methodNamePointer = addToStringData("unknown"+i);

				int methodDescriptorPointer = addToStringData("()V");
				
				int accessFlags = 0;
				
				methodNodes[methodNodePointer+METHOD_NODE_NAME_POINTER_OFFSET] = methodNamePointer;
				methodNodes[methodNodePointer+METHOD_NODE_DESCRIPTOR_POINTER_OFFSET] = methodDescriptorPointer;
				methodNodes[methodNodePointer+METHOD_NODE_ACCESS_FLAGS_OFFSET] = accessFlags;
				
				methodNodePointer+=METHOD_NODE_LENGTH;
				
			}

		}

		int numberOfFields = 256;

		if (numberOfFields > 0) {
			
			thisFieldsPointer = fieldsPointer++;
			if(thisFieldsPointer>fields.length) {
				expandFields();
			}
			
			int[] fieldNodes = new int[numberOfFields*FIELD_NODE_LENGTH];
			fields[thisFieldsPointer] = fieldNodes;

			if(stringsPointer+(numberOfFields*2)>=strings.length) {
				expandStrings();
			}

			int fieldNodePointer = 0;
			
			for (int i = 0; i < numberOfFields; i++) {
				
				int fieldNamePointer = addToStringData("unknown"+i);

				int fieldDescriptorPointer = addToStringData("I");
				
				int accessFlags = 0;
				
				fieldNodes[fieldNodePointer+FIELD_NODE_NAME_POINTER_OFFSET] = fieldNamePointer;
				fieldNodes[fieldNodePointer+FIELD_NODE_DESCRIPTOR_POINTER_OFFSET] = fieldDescriptorPointer;
				fieldNodes[fieldNodePointer+FIELD_NODE_ACCESS_FLAGS_OFFSET] = accessFlags;
				
				fieldNodePointer+=FIELD_NODE_LENGTH;
				
			}

		}
		

		int superClassID = 0;

		int numberOfInterfaces = 0;

		if (numberOfInterfaces > 0) {
			

		}

		addClass(classID, classNamePointer, superClassID, 0, thisMethodsPointer, thisFieldsPointer, thisInterfacesPointer);


		
	}
	
	private final void writeHeader() {
		//System.out.println("writeheader");
		
		boolean savedEndianism = littleEndian;
		
		littleEndian = true;
		
		writeInt8('b');
		writeInt32(headerVersion);
		writeInt32(headerPlatform);
		
		writeInt32(eventsPerPart);
		writeInt32(numberOfThreads);
		writeInt32(numberOfClasses);
		
		writeInt32(headerTicksPerMicrosecond);
		
		writeInt64(headerStartTicks);
		writeInt32(headerJvmStartSeconds);
		writeInt32(headerConnectionStartSeconds);

		writeInt32(headerJvmOverhead);

		littleEndian = savedEndianism;

	}

	public  static void main(String[] args) {

		TraceSplitter traceSplitter = new TraceSplitter();

		if(args.length!=2) {
			System.out.println("Usage:");
			System.out.println("   TraceSplitter inputFileName numberOfParts");
			System.exit(-1);
		}

		
		try {
		
			traceSplitter.numberOfParts = Integer.parseInt(args[1]);
			
		} catch (Exception e) {
			System.out.println("Number of Parts (" + args[1] + ") is not a number");
			System.exit(-1);
		}
		
		
		fileName = args[0];

		try {
			traceSplitter.openFile(fileName);
		} catch (Exception e) {
			
			System.out.println("Unable to open input file (" + fileName + "),  " + e);
			System.exit(-1);
		}

		try {
			traceSplitter.setupBuffers();
		} catch (Exception e) {
			System.err.println(e);
			System.exit(-1);
		}

		traceSplitter.createObjectHashtable();
		traceSplitter.createClassHashtable();
		traceSplitter.createThreadHashtable();
		traceSplitter.createStrings();
		traceSplitter.createMethods();
		traceSplitter.createFields();
		traceSplitter.createInterfaces();
		
		traceSplitter.setupSpecials();

		traceSplitter.readHeader();
		traceSplitter.reportHeader();

		traceSplitter.passOne();
		traceSplitter.reportStatistics();
		traceSplitter.createFastLookupArrays();

		traceSplitter.calculateSplitFiles();

		try {
			traceSplitter.closeFile();
			traceSplitter.openFile(fileName);
		} catch (Exception e) {
			System.err.println(e);
			System.exit(-1);
		}

		try {
			traceSplitter.setupBuffers();
		} catch (Exception e) {
			System.err.println(e);
			System.exit(-1);
		}

		traceSplitter.skipHeader();
		traceSplitter.passTwo();

	}

}
