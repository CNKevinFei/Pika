package asmCodeGenerator.runtime;

import static asmCodeGenerator.Macros.*;
import static asmCodeGenerator.codeStorage.ASMCodeFragment.CodeType.*;
import static asmCodeGenerator.codeStorage.ASMOpcode.*;
import asmCodeGenerator.Labeller;
import asmCodeGenerator.codeStorage.ASMCodeFragment;

public class MemoryManager {
	// Debug Mode. DEBUGGING Adds debug code and executes insertDebugMain when the program is initiailzed.
	// Debug Mode. DEBUGGING2 does not insertDebugMain, but prints allocation diagnostics.
	private static final boolean DEBUGGING = false;
	private static final boolean DEBUGGING2 = false;		// does not insertDebugMain
	
	// ASM Subroutines.  User/Compiler-writer needs only ALLOCATE and DEALLOCATE
	private static final String MEM_MANAGER_INITIALIZE =   "-mem-manager-initialize";
	private static final String MEM_MANAGER_MAKE_TAGS =    "-mem-manager-make-tags";
	private static final String MEM_MANAGER_MAKE_ONE_TAG = "-mem-manager-one-tag";
	public  static final String MEM_MANAGER_ALLOCATE =     "-mem-manager-allocate";
	public  static final String MEM_MANAGER_DEALLOCATE =   "-mem-manager-deallocate";
	private static final String MEM_MANAGER_REMOVE_BLOCK = "-mem-manager-remove-block";
	
	// Main memory manager variables.
	private static final String MEM_MANAGER_HEAP_START_PTR =   "$heap-start-ptr";
	private static final String MEM_MANAGER_HEAP_END_PTR =     "$heap-after-ptr";
	private static final String MEM_MANAGER_FIRST_FREE_BLOCK = "$heap-first-free";
	private static final String MEM_MANAGER_HEAP =             "$heap-memory";
	
	// locals for MAKE_TAGS
	private static final String MMGR_BLOCK_RETURN_ADDRESS = "$mmgr-tags-return";
	private static final String MMGR_BLOCK_START =     		"$mmgr-tags-start";
	private static final String MMGR_BLOCK_SIZE =      		"$mmgr-tags-size";
	private static final String MMGR_BLOCK_PREVPTR =   		"$mmgr-tags-prevptr";
	private static final String MMGR_BLOCK_NEXTPTR =   		"$mmgr-tags-nextptr";
	private static final String MMGR_BLOCK_AVAILABLE = 		"$mmgr-tags-available";
	
	// locals for ONE_TAG
	private static final String MMGR_ONETAG_RETURN_ADDRESS = "$mmgr-onetag-return";
	private static final String MMGR_ONETAG_LOCATION =  	 "$mmgr-onetag-location";
	private static final String MMGR_ONETAG_AVAILABLE = 	 "$mmgr-onetag-available";
	private static final String MMGR_ONETAG_SIZE =      	 "$mmgr-onetag-size";
	private static final String MMGR_ONETAG_POINTER =   	 "$mmgr-onetag-pointer";
	
	// locals and branch targets for ALLOCATE
	private static final String MMGR_ALLOC_RETURN_ADDRESS = 	"$mmgr-alloc-return";
	private static final String MMGR_ALLOC_SIZE = 				"$mmgr-alloc-size";
	private static final String MMGR_ALLOC_CURRENT_BLOCK =  	"$mmgr-alloc-current-block";
	private static final String MMGR_ALLOC_REMAINDER_BLOCK =	"$mmgr-alloc-remainder-block";
	private static final String MMGR_ALLOC_REMAINDER_SIZE = 	"$mmgr-alloc-remainder-size";
	private static final String MMGR_ALLOC_FOUND_BLOCK =		"-mmgr-alloc-found-block";
	private static final String MMGR_ALLOC_PROCESS_CURRENT = 	"-mmgr-alloc-process-current";
	private static final String MMGR_ALLOC_TEST_BLOCK =  		"-mmgr-alloc-test-block";
	private static final String MMGR_ALLOC_NO_BLOCK_WORKS = 	"-mmgr-alloc-no-block-works";
	private static final String MMGR_ALLOC_RETURN_USERBLOCK =	"-mmgr-alloc-return-userblock";
	
	// locals and branch targets for DEALLOCATE	
	private static final String MMGR_DEALLOC_RETURN_ADDRESS = 	"$mmgr-dealloc-return";
	private static final String MMGR_DEALLOC_BLOCK = 			"$mmgr-dealloc-block";

	// locals and branch targets for REMOVE_BLOCK
	private static final String MMGR_REMOVE_RETURN_ADDRESS = 	"$mmgr-remove-return";
	private static final String MMGR_REMOVE_BLOCK = 			"$mmgr-remove-block";
	private static final String MMGR_REMOVE_PREV = 				"$mmgr-remove-prev";
	private static final String MMGR_REMOVE_NEXT = 				"$mmgr-remove-next";
	private static final String MMGR_REMOVE_PROCESS_PREV = 		"-mmgr-remove-process-prev";
	private static final String MMGR_REMOVE_NO_PREV =			"-mmgr-remove-no-prev";
	private static final String MMGR_REMOVE_PROCESS_NEXT =		"-mmgr-remove-process-next";
	private static final String MMGR_REMOVE_DONE = 				"-mmgr-remove-done";
	
	// variables used by a macro (method newBlock) but could be shared by all instances of the macro
	// (although currently there is only one instance.)  allocated in initialization.
	private static final String MMGR_NEWBLOCK_BLOCK = "$mmgr-newblock-block";
	private static final String MMGR_NEWBLOCK_SIZE =  "$mmgr-newblock-size";

	// locals and subroutine tag for store string record
	public static final String MEM_STORE_STRING_HEADER = "-mem-store-string-header";
	public static final String MEM_STORE_STRING_RETURN_ADDRESS = "$mem-string-return";
	
	// locals and subroutine tag for store array record
	public static final String MEM_STORE_ARRAY_HEADER = "-mem-store-array-header";
	private static final String MEM_STORE_ARRAY_RETURN_ADDRESS = "$mem-array-return";
	public static final String MEM_STORE_ARRAY_ONE_BYTE = "-mem-store-array-one-byte";
	public static final String MEM_STORE_ARRAY_FOUR_BYTE = "-mem-store-array-four-byte";
	public static final String MEM_STORE_ARRAY_EIGHT_BYTE = "-mem-store-array-eight-byte";
	private static final String MEM_STORE_ARRAY_ONE_BYTE_RETURN_ADDRESS = "$mem-store-array-one-byte-return";
	private static final String MEM_STORE_ARRAY_FOUR_BYTE_RETURN_ADDRESS = "$mem-store-array-four-byte-return";
	private static final String MEM_STORE_ARRAY_EIGHT_BYTE_RETURN_ADDRESS = "$mem-store-array-eight-byte-return";
	private static final String MEM_STORE_ARRAY_BLOCK = "$mem-store-array-block";
	private static final String MEM_STORE_ARRAY_LENGTH = "$mem-store-array-length";
	private static final String MEM_STORE_ARRAY_LOOP_ONE = "$mem-store-array-loop-one";
	private static final String MEM_STORE_ARRAY_LOOP_END_ONE = "$mem-store-array-loop-end-one";
	private static final String MEM_STORE_ARRAY_LOOP_FOUR = "$mem-store-array-loop-four";
	private static final String MEM_STORE_ARRAY_LOOP_END_FOUR = "$mem-store-array-loop-end-four";
	private static final String MEM_STORE_ARRAY_LOOP_EIGHT = "$mem-store-array-loop-eight";
	private static final String MEM_STORE_ARRAY_LOOP_END_EIGHT = "$mem-store-array-loop-end-eight";
	private static final String MEM_STORE_ARRAY_BLOCK_HEADER = "$mem-store-array-block-header";
	private static final String MEM_STORE_ARRAY_STATUS_HEADER = "$mem-store-array-status-header";
	private static final String MEM_STORE_ARRAY_LENGTH_HEADER = "$mem-store-array-length-header";
	private static final String MEM_STORE_ARRAY_SIZE_HEADER = "$mem-store-array-size-header";
	private static final String MEM_STORE_ARRAY_FLAG = "$mem-store-array-flag";
	private static final String MEM_STORE_ARRAY_EMPTY_ONE = "-mem-store-array-empty-one";
	private static final String MEM_STORE_ARRAY_EMPTY_FOUR = "-mem-store-array-empty-four";
	private static final String MEM_STORE_ARRAY_EMPTY_EIGHT = "-mem-store-array-empty-eight";
		
	// locals and subroutine tag for array record release
	public static final String MEM_ARRAY_RELEASE = "-mem-array-release";
	private static final String MEM_ARRAY_RELEASE_RETURN_ADDRESS = "$mem-array-release-return-address";
	private static final String MEM_ARRAY_RELEASE_ARRAY_ADDRESS = "$mem-array-release-array-address";
	private static final String MEM_ARRAY_RELEASE_ARRAY_LENGTH = "$mem-array-release-array-length";
	private static final String MEM_ARRAY_RELEASE_NOT_REF = "$mem-array-release-not-ref";
	private static final String MEM_ARRAY_RELEASE_LOOP = "$mem-array-release-loop";
	private static final String MEM_ARRAY_RELEASE_END = "$mem-array-release-end";
	
	// locals and subroutine tag for array record clone
	public static final String MEM_ARRAY_CLONE = "-mem-array-clone";
	private static final String MEM_ARRAY_CLONE_RETURN_ADDRESS = "$mem-array-clone-return-address";
	private static final String MEM_ARRAY_CLONE_ARRAY_ADDRESS = "$mem-array-clone-array-address";
	private static final String MEM_ARRAY_CLONE_RESULT_ADDRESS = "$mem-array-clone-result-address";
	private static final String MEM_ARRAY_CLONE_RECORD_SIZE = "$mem-array-clone-record-size";
	private static final String MEM_ARRAY_CLONE_LOOP = "$mem-array-clone-loop";
	private static final String MEM_ARRAY_CLONE_END = "$mem-array-clone-end";
	
	// locals and subroutine tag for array record index
	public static final String MEM_ARRAY_INDEX = "-mem-array-index";
	private static final String MEM_ARRAY_INDEX_RETURN_ADDRESS = "$mem-array-index-return-address";
	private static final String MEM_ARRAY_INDEX_NUM = "$mem-array-index-num";
	private static final String MEM_ARRAY_INDEX_ARRAY_ADDRESS = "$mem-array-index-array-address";
	private static final String MEM_ARRAY_INDEX_SIZE = "$mem-array-index-size";
	private static final String MEM_ARRAY_INDEX_FLAG = "$mem-array-index-flag";
	
	// locals and subroutine tag for int array print 
	public static final String MEM_ARRAY_INT_PRINT = "-mem-array-int-print";
	private static final String MEM_ARRAY_INT_RETURN_ADDRESS = "$mem-array-int-return-address";
	private static final String MEM_ARRAY_INT_ARRAY_ADDRESS = "$mem-array-int-array-address";
	private static final String MEM_ARRAY_INT_END_ARRAY_ADDRESS = "$mem-array-int-end-array-address";
	private static final String MEM_ARRAY_INT_LENGTH = "$mem-array-int-length";
	private static final String MEM_ARRAY_INT_SIZE = "$mem-array-int-size";
	private static final String MEM_ARRAY_INT_ARRAY = "$mem-array-int-array";
	private static final String MEM_ARRAY_INT_LOOP = "$mem-array-int-loop";
	private static final String MEM_ARRAY_INT_PRINT_END = "$mem-array-int-print-end";
	
	// locals and subroutine tag for int array print 
	public static final String MEM_ARRAY_FLOAT_PRINT = "-mem-array-float-print";
	private static final String MEM_ARRAY_FLOAT_RETURN_ADDRESS = "$mem-array-float-return-address";
	private static final String MEM_ARRAY_FLOAT_ARRAY_ADDRESS = "$mem-array-float-array-address";
	private static final String MEM_ARRAY_FLOAT_END_ARRAY_ADDRESS = "$mem-array-float-end-array-address";
	private static final String MEM_ARRAY_FLOAT_LENGTH = "$mem-array-float-length";
	private static final String MEM_ARRAY_FLOAT_SIZE = "$mem-array-float-size";
	private static final String MEM_ARRAY_FLOAT_ARRAY = "$mem-array-float-array";
	private static final String MEM_ARRAY_FLOAT_LOOP = "$mem-array-float-loop";
	private static final String MEM_ARRAY_FLOAT_PRINT_END = "$mem-array-float-print-end";
	
	// locals and subroutine tag for int array print 
	public static final String MEM_ARRAY_CHAR_PRINT = "-mem-array-char-print";
	private static final String MEM_ARRAY_CHAR_RETURN_ADDRESS = "$mem-array-char-return-address";
	private static final String MEM_ARRAY_CHAR_ARRAY_ADDRESS = "$mem-array-char-array-address";
	private static final String MEM_ARRAY_CHAR_END_ARRAY_ADDRESS = "$mem-array-char-end-array-address";
	private static final String MEM_ARRAY_CHAR_LENGTH = "$mem-array-char-length";
	private static final String MEM_ARRAY_CHAR_SIZE = "$mem-array-char-size";
	private static final String MEM_ARRAY_CHAR_ARRAY = "$mem-array-char-array";
	private static final String MEM_ARRAY_CHAR_LOOP = "$mem-array-char-loop";
	private static final String MEM_ARRAY_CHAR_PRINT_END = "$mem-array-char-print-end";
	
	// locals and subroutine tag for int array print 
	public static final String MEM_ARRAY_BOOL_PRINT = "-mem-array-bool-print";
	private static final String MEM_ARRAY_BOOL_RETURN_ADDRESS = "$mem-array-bool-return-address";
	private static final String MEM_ARRAY_BOOL_ARRAY_ADDRESS = "$mem-array-bool-array-address";
	private static final String MEM_ARRAY_BOOL_END_ARRAY_ADDRESS = "$mem-array-bool-end-array-address";
	private static final String MEM_ARRAY_BOOL_LENGTH = "$mem-array-bool-length";
	private static final String MEM_ARRAY_BOOL_SIZE = "$mem-array-bool-size";
	private static final String MEM_ARRAY_BOOL_ARRAY = "$mem-array-bool-array";
	private static final String MEM_ARRAY_BOOL_LOOP = "$mem-array-bool-loop";
	private static final String MEM_ARRAY_BOOL_PRINT_END = "$mem-array-bool-print-end";
	private static final String MEM_ARRAY_BOOL_TRUE = "$mem-array-bool-true";
	private static final String MEM_ARRAY_BOOL_END = "$mem-array-bool-end";
	
	// locals and subroutine tag for string array print 
	public static final String MEM_ARRAY_STRING_PRINT = "-mem-array-string-print";
	private static final String MEM_ARRAY_STRING_RETURN_ADDRESS = "$mem-array-string-return-address";
	private static final String MEM_ARRAY_STRING_ARRAY_ADDRESS = "$mem-array-string-array-address";
	private static final String MEM_ARRAY_STRING_END_ARRAY_ADDRESS = "$mem-array-string-end-array-address";
	private static final String MEM_ARRAY_STRING_LENGTH = "$mem-array-string-length";
	private static final String MEM_ARRAY_STRING_SIZE = "$mem-array-string-size";
	private static final String MEM_ARRAY_STRING_ARRAY = "$mem-array-string-array";
	private static final String MEM_ARRAY_STRING_LOOP = "$mem-array-string-loop";
	private static final String MEM_ARRAY_STRING_PRINT_END = "$mem-array-string-print-end";

	// locals and subroutine tag for rational array print 
	public static final String MEM_ARRAY_RATIONAL_PRINT = "-mem-array-rational-print";
	private static final String MEM_ARRAY_RATIONAL_RETURN_ADDRESS = "$mem-array-rational-return-address";
	private static final String MEM_ARRAY_RATIONAL_ARRAY_ADDRESS = "$mem-array-rational-array-address";
	private static final String MEM_ARRAY_RATIONAL_END_ARRAY_ADDRESS = "$mem-array-rational-end-array-address";
	private static final String MEM_ARRAY_RATIONAL_LENGTH = "$mem-array-rational-length";
	private static final String MEM_ARRAY_RATIONAL_SIZE = "$mem-array-rational-size";
	private static final String MEM_ARRAY_RATIONAL_ARRAY = "$mem-array-rational-array";
	private static final String MEM_ARRAY_RATIONAL_LOOP = "$mem-array-rational-loop";
	private static final String MEM_ARRAY_RATIONAL_PRINT_END = "$mem-array-rational-print-end";
	
	// local and subroutine tags for rational GCD
	public static final String MEM_RAT_GCD = "-mem-rat-GCD";
	private static final String MEM_RAT_GCD_RETURN_ADDRESS = "$mem-rat-gcd-return-address";
	private static final String MEM_RAT_GCD_NUMERATOR = "$mem-rat-gcd-numerator";
	private static final String MEM_RAT_GCD_DENOMINATOR = "$mem-rat-gcd-denominator";
	private static final String MEM_RAT_GCD_A = "$mem-rat-gcd-a";
	private static final String MEM_RAT_GCD_B = "$mem-rat-gcd-b";
	private static final String MEM_RAT_GCD_LOOP = "$mem-rat-gcd-loop";
	private static final  String MEM_RAT_GCD_END = "$mem-rat-gcd-end";
	private static final  String MEM_RAT_GCD_NEG = "$mem-rat-gcd-neg";
	
	// local and subroutine tags for rational store
	public static final String MEM_RAT_STORE = "-mem-rat-store";
	private static final String MEM_RAT_STORE_RETURN_ADDRESS = "$mem-rat-store-return-address";
	private static final String MEM_RAT_STORE_TARGET_ADDRESS = "$mem-rat-store-target-address";
	private static final String MEM_RAT_STORE_NUMERATOR = "$mem-rat-store-numerator";
	private static final String MEM_RAT_STORE_DENOMINATOR = "$mem-rat-store-denominator";
	
	// local and subroutine tags for rational aids
	public static final String MEM_RAT_AID = "-mem-rat-aid";
	private static final String MEM_RAT_AID_RETURN_ADDRESS = "$mem-rat-aid-return-address";
	private static final String MEM_RAT_AID_A = "$mem-rat-aid-a";
	private static final String MEM_RAT_AID_B = "$mem-rat-aid-b";
	
	// local and subroutine tags for rational print
	public static final String MEM_RAT_PRINT = "-mem-rat-print";
	private static final String MEM_RAT_PRINT_RETURN_ADDRESS = "$mem-rat-print-return-address";
	private static final String MEM_RAT_PRINT_A = "$mem-rat-print-a";
	private static final String MEM_RAT_PRINT_B = "$mem-rat-print-b";
	private static final String MEM_RAT_PRINT_NEG = "$mem-rat-print-neg";
	private static final String MEM_RAT_PRINT_RAT = "$mem-rat-print-rat";
	private static final String MEM_RAT_PRINT_END = "$mem-rat-print-end";
	private static final String MEM_RAT_PRINT_CON = "$mem-rat-print-con";
	private static final String MEM_RAT_PRINT_ZERO = "$mem-rat-print-zero";
	
	// local and subroutine tags for rational add
	public static final String MEM_RAT_ADD = "-mem-rat-add";
	private static final String MEM_RAT_ADD_RETURN_ADDRESS = "$mem-rat-add-return-address";
	private static final String MEM_RAT_ADD_A_NUM = "$mem-rat-add-a-num";
	private static final String MEM_RAT_ADD_A_DEN = "$mem-rat-add-a-den";
	private static final String MEM_RAT_ADD_B_NUM = "$mem-rat-add-b-num";
	private static final String MEM_RAT_ADD_B_DEN = "$mem-rat-add-b-den";
	
	// local and subroutine tags for rational subtract
	public static final String MEM_RAT_SUBTRACT = "-mem-rat-subtract";
	private static final String MEM_RAT_SUBTRACT_RETURN_ADDRESS = "$mem-rat-subtract-return-address";
	private static final String MEM_RAT_SUBTRACT_A_NUM = "$mem-rat-subtract-a-num";
	private static final String MEM_RAT_SUBTRACT_A_DEN = "$mem-rat-subtract-a-den";
	private static final String MEM_RAT_SUBTRACT_B_NUM = "$mem-rat-subtract-b-num";
	private static final String MEM_RAT_SUBTRACT_B_DEN = "$mem-rat-subtract-b-den";
	
	// local and subroutine tags for rational multiply
	public static final String MEM_RAT_MULTIPLY = "-mem-rat-multiply";
	private static final String MEM_RAT_MULTIPLY_RETURN_ADDRESS = "$mem-rat-multiply-return-address";
	private static final String MEM_RAT_MULTIPLY_A_NUM = "$mem-rat-multiply-a-num";
	private static final String MEM_RAT_MULTIPLY_A_DEN = "$mem-rat-multiply-a-den";
	private static final String MEM_RAT_MULTIPLY_B_NUM = "$mem-rat-multiply-b-num";
	private static final String MEM_RAT_MULTIPLY_B_DEN = "$mem-rat-multiply-b-den";
		
	// local and subroutine tags for rational divide
	public static final String MEM_RAT_DIVIDE = "-mem-rat-divide";
	private static final String MEM_RAT_DIVIDE_RETURN_ADDRESS = "$mem-rat-divide-return-address";
	private static final String MEM_RAT_DIVIDE_A_NUM = "$mem-rat-divide-a-num";
	private static final String MEM_RAT_DIVIDE_A_DEN = "$mem-rat-divide-a-den";
	private static final String MEM_RAT_DIVIDE_B_NUM = "$mem-rat-divide-b-num";
	private static final String MEM_RAT_DIVIDE_B_DEN = "$mem-rat-divide-b-den";
	
	// local and subroutine tags for array record test
	public static final String MEM_ARRAY_RECORD_VALID = "-mem-arrya-record-valid";
	private static final String MEM_ARRAY_RECORD_VALID_RETURN_ADDRESS = "-mem-arrya-record-valid-return-address";
	private static final String MEM_ARRAY_RECORD_VALID_ARRAY_ADDRESS = "-mem-arrya-record-valid-array-address";
	
	
	
	// a tag is:
	//		prev/next ptr:	4 bytes
	//		size:			4 bytes
	//		isAvailable:	1 byte	
	private static final int MMGR_TAG_SIZE_IN_BYTES = 9;
	private static final int MMGR_TWICE_TAG_SIZE = 2 * MMGR_TAG_SIZE_IN_BYTES;
	private static final int TAG_POINTER_OFFSET = 0;
	private static final int TAG_SIZE_OFFSET = 4;
	private static final int TAG_AVAIL_OFFSET = 8;
	
	// extra information for string record
	public static final int MEM_STRING_RECORD_EXTRA = 13;
	private static final int MEM_STRING_STATUS_OFFSET = 4;
	private static final int MEM_STRING_LENGTH_OFFSET = 8;
	public static final int MEM_STRING_CONTENT_OFFSET = 12;
	
	// extra information for array record
	public static final int MEM_ARRAY_HEADER = 16;
	private static final int MEM_ARRAY_STATUS_OFFSET = 4;
	private static final int MEM_ARRAY_SIZE_OFFSET = 8;
	public static final int MEM_ARRAY_LENGTH_OFFSET = 12;
	public static final int MEM_ARRAY_CONTENT_OFFSET = 16;
	
	// the only tunable parameter.
	private static final int MEM_MANAGER_WASTE_TOLERANCE = MMGR_TWICE_TAG_SIZE + 8;

	

	// this code should reside on the executable pathway before the application.
	public static ASMCodeFragment codeForInitialization() {
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_VOID);
		frag.add(Label, MEM_MANAGER_INITIALIZE);
		
		declareI(frag, MEM_MANAGER_HEAP_START_PTR);	// declare variables
		declareI(frag, MEM_MANAGER_HEAP_END_PTR);	
		declareI(frag, MEM_MANAGER_FIRST_FREE_BLOCK);
		
		declareI(frag, MMGR_NEWBLOCK_BLOCK);
		declareI(frag, MMGR_NEWBLOCK_SIZE);
		
		frag.add(PushD, MEM_MANAGER_HEAP);				// set heapStart and heapEnd
		frag.add(Duplicate);
		storeITo(frag, MEM_MANAGER_HEAP_START_PTR);
		storeITo(frag, MEM_MANAGER_HEAP_END_PTR);
		
		frag.add(PushI, 0);								// no blocks allocated.
		storeITo(frag, MEM_MANAGER_FIRST_FREE_BLOCK);

		if(DEBUGGING) {
			insertDebugMain(frag);
		}
		
		return frag;
	}


	// this goes after the main program, so that MEM_MANAGER_HEAP is after all other variable declarations.
	public static ASMCodeFragment codeForAfterApplication() {
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_VOID);	
		
		frag.append(subroutineMakeTags());
		frag.append(subroutineMakeOneTag());
		frag.append(subroutineAllocate());
		frag.append(subroutineDeallocate());
		frag.append(subroutineRemoveBlock());
		frag.append(subroutineStoreStringHeader());
		frag.append(subroutineStoreArrayHeader());
		frag.append(subroutineStoreArrayOneByte());
		frag.append(subroutineStoreArrayFourByte());
		frag.append(subroutineStoreArrayEightByte());
		frag.append(subroutineArrayRelease());
		frag.append(subroutineArrayClone());
		frag.append(subroutineArrayIndex());
		frag.append(subroutineRatGCD());
		frag.append(subroutineRatStore());
		frag.append(subroutineRatAid());
		frag.append(subroutineRatPrint());
		frag.append(subroutineRatAdd());
		frag.append(subroutineRatSubtract());
		frag.append(subroutineRatMultiply());
		frag.append(subroutineRatDivide());
		frag.append(subroutineArrayIntPrint());
		frag.append(subroutineArrayFloatPrint());
		frag.append(subroutineArrayCharPrint());
		frag.append(subroutineArrayBoolPrint());
		frag.append(subroutineArrayStringPrint());
		frag.append(subroutineArrayRationalPrint());
		frag.append(subroutineArrayRecordTest());
		if(DEBUGGING) {
			frag.append(subroutineDebugPrintBlock());
			frag.append(subroutineDebugPrintFreeList());
		}
		
		frag.add(DLabel, MEM_MANAGER_HEAP);	
		
		return frag;
	}
	
	
	
	private static ASMCodeFragment subroutineMakeTags() {
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_VOID);
		frag.add(Label, MEM_MANAGER_MAKE_TAGS);		// [...prevPtr nextPtr isAvail start size (return)]
													// size must include the tags.

		declareI(frag, MMGR_BLOCK_SIZE);
		declareI(frag, MMGR_BLOCK_START);
		declareI(frag, MMGR_BLOCK_AVAILABLE);
		declareI(frag, MMGR_BLOCK_NEXTPTR);
		declareI(frag, MMGR_BLOCK_PREVPTR);
		declareI(frag, MMGR_BLOCK_RETURN_ADDRESS);
		
		
		// store the params and return address
		storeITo(frag, MMGR_BLOCK_RETURN_ADDRESS); // [... prevPtr nextPtr isAvail start size]
		storeITo(frag, MMGR_BLOCK_SIZE);		// [... prevPtr nextPtr isAvail start]
		storeITo(frag, MMGR_BLOCK_START);		// [... prevPtr nextPtr isAvail]
		storeITo(frag, MMGR_BLOCK_AVAILABLE);	// [... prevPtr nextPtr]
		storeITo(frag, MMGR_BLOCK_NEXTPTR);		// [... prevPtr]
		storeITo(frag, MMGR_BLOCK_PREVPTR);		// [... ]
		
		// make the start tag
		loadIFrom(frag, MMGR_BLOCK_PREVPTR);		// [... prevPtr]
		loadIFrom(frag, MMGR_BLOCK_SIZE);		// [... prevPtr size]
		loadIFrom(frag, MMGR_BLOCK_AVAILABLE);	// [... prevPtr size isAvail]
		loadIFrom(frag, MMGR_BLOCK_START);		// [... prevPtr size isAvail tagLocation]
		frag.add(Call,  MEM_MANAGER_MAKE_ONE_TAG);			
		
		// make the end tag
		loadIFrom(frag, MMGR_BLOCK_NEXTPTR);		// [... nextPtr]
		loadIFrom(frag, MMGR_BLOCK_SIZE);		// [... nextPtr size]
		loadIFrom(frag, MMGR_BLOCK_AVAILABLE);	// [... nextPtr size isAvail]
		loadIFrom(frag, MMGR_BLOCK_START);		// [... nextPtr size isAvail start]
		tailTag(frag);							// [... nextPtr size isAvail tailTagLocation]
		frag.add(Call,  MEM_MANAGER_MAKE_ONE_TAG);	
		
		loadIFrom(frag, MMGR_BLOCK_RETURN_ADDRESS);
		frag.add(Return);
		
		return frag;
	}
	
	private static ASMCodeFragment subroutineMakeOneTag() {
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_VOID);
		frag.add(Label, MEM_MANAGER_MAKE_ONE_TAG);		// [... ptr size isAvail location (return)]

		declareI(frag, MMGR_ONETAG_RETURN_ADDRESS);
		declareI(frag, MMGR_ONETAG_LOCATION);
		declareI(frag, MMGR_ONETAG_AVAILABLE);
		declareI(frag, MMGR_ONETAG_SIZE);
		declareI(frag, MMGR_ONETAG_POINTER);

		// store the params (except ptr) and return address
		storeITo(frag, MMGR_ONETAG_RETURN_ADDRESS); // [... ptr size isAvail location]
		storeITo(frag, MMGR_ONETAG_LOCATION);		// [... ptr size isAvail]
		storeITo(frag, MMGR_ONETAG_AVAILABLE); 		// [... ptr size]
		storeITo(frag, MMGR_ONETAG_SIZE); 			// [... ptr]

		loadIFrom(frag, MMGR_ONETAG_LOCATION);		// [.. ptr location]
		writeTagPointer(frag);

		loadIFrom(frag, MMGR_ONETAG_SIZE);			// [.. size]
		loadIFrom(frag, MMGR_ONETAG_LOCATION);		// [.. size location]
		writeTagSize(frag);

		loadIFrom(frag, MMGR_ONETAG_AVAILABLE);		// [.. isAvail]
		loadIFrom(frag, MMGR_ONETAG_LOCATION);		// [.. isAvail location]
		writeTagAvailable(frag);
		
		
		loadIFrom(frag, MMGR_ONETAG_RETURN_ADDRESS);
		frag.add(Return);
		
		return frag;
	}	

	private static ASMCodeFragment subroutineAllocate() {
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_VOID);
		frag.add(Label, MEM_MANAGER_ALLOCATE);		// [... usableSize (return)]

		declareI(frag, MMGR_ALLOC_RETURN_ADDRESS);
		declareI(frag, MMGR_ALLOC_SIZE);
		declareI(frag, MMGR_ALLOC_CURRENT_BLOCK);
		declareI(frag, MMGR_ALLOC_REMAINDER_BLOCK);
		declareI(frag, MMGR_ALLOC_REMAINDER_SIZE);
		

		//store return addr
		storeITo(frag, MMGR_ALLOC_RETURN_ADDRESS);	// [... usableSize]
		
		if(DEBUGGING2) {
			printAccumulatorTop(frag, "--allocate %d bytes\n");
		}

		//convert user size to mmgr size and store
		frag.add(PushI, MMGR_TWICE_TAG_SIZE);			// [... usableSize 2*tagsize]
		frag.add(Add);									// [... size]
		storeITo(frag, MMGR_ALLOC_SIZE);				// [...]

	
		//initialize current block
			loadIFrom(frag, MEM_MANAGER_FIRST_FREE_BLOCK);
			storeITo(frag, MMGR_ALLOC_CURRENT_BLOCK);

		// if (curblock == null) goto NO_BLOCK_WORKS
		frag.add(Label, MMGR_ALLOC_PROCESS_CURRENT);
			loadIFrom(frag, MMGR_ALLOC_CURRENT_BLOCK);
			frag.add(JumpFalse, MMGR_ALLOC_NO_BLOCK_WORKS);

		// if (curblock.size >= allocsize) goto FOUND_BLOCK		
		frag.add(Label, MMGR_ALLOC_TEST_BLOCK);
			loadIFrom(frag, MMGR_ALLOC_CURRENT_BLOCK);		// [... block]
			if(DEBUGGING2) {
				printAccumulatorTop(frag, "--testing block %d\n");
			}
			readTagSize(frag);								// [... block.size]
			loadIFrom(frag, MMGR_ALLOC_SIZE);				// [... block.size allocSize]
			frag.add(Subtract);								// [... block.size-allocSize]
			frag.add(PushI, 1);								// [... block.size-allocSize 1]
			frag.add(Add);									// [... block.size-allocSize+1]
			frag.add(JumpPos, MMGR_ALLOC_FOUND_BLOCK);


			// curblock = curblock.nextptr
			loadIFrom(frag, MMGR_ALLOC_CURRENT_BLOCK);
			tailTag(frag);
			readTagPointer(frag);
			storeITo(frag, MMGR_ALLOC_CURRENT_BLOCK);


		// loop to NEXT_BLOCK
		frag.add(Jump, MMGR_ALLOC_PROCESS_CURRENT);
		
		
		frag.add(Label, MMGR_ALLOC_FOUND_BLOCK);
			// remove block from free list
			loadIFrom(frag, MMGR_ALLOC_CURRENT_BLOCK);
			frag.add(Call, MEM_MANAGER_REMOVE_BLOCK);
			
			// if (not wasting much memory) use this block as is
			loadIFrom(frag, MMGR_ALLOC_CURRENT_BLOCK);		// [... block]
			readTagSize(frag);								// [... block.size]
			loadIFrom(frag, MMGR_ALLOC_SIZE);				// [... block.size allocSize]
			frag.add(Subtract);								// [... waste]
			frag.add(PushI, MEM_MANAGER_WASTE_TOLERANCE);	// [... waste tolerance]
			frag.add(Subtract);								// [... over-tolerance-amt]
			frag.add(JumpNeg, MMGR_ALLOC_RETURN_USERBLOCK);
			

			// make two blocks from current block
			loadIFrom(frag, MMGR_ALLOC_CURRENT_BLOCK);		// [... block]
			loadIFrom(frag, MMGR_ALLOC_SIZE);				// [... block size]
			frag.add(Add);									// [... remainderBlock]
			storeITo(frag, MMGR_ALLOC_REMAINDER_BLOCK);		// [...]

			loadIFrom(frag, MMGR_ALLOC_SIZE);				// [... size]
			loadIFrom(frag, MMGR_ALLOC_CURRENT_BLOCK);		// [... size block]
			readTagSize(frag);								// [... size block.size]
			frag.add(Exchange);								// [... block.size size]
			frag.add(Subtract);								// [... leftoverbytes]
			storeITo(frag, MMGR_ALLOC_REMAINDER_SIZE);		// [...]
			
//			debugPrintI(frag, "alloc-current-block:   ", MMGR_ALLOC_CURRENT_BLOCK);
//			debugPrintI(frag, "alloc-current-size:    ", MMGR_ALLOC_SIZE);
//			debugPrintI(frag, "alloc-remainder-block: ", MMGR_ALLOC_REMAINDER_BLOCK);
//			debugPrintI(frag, "alloc-remainder-size:  ", MMGR_ALLOC_REMAINDER_SIZE);
			
			// make the tags for first new block.
			frag.add(PushI, 0);								// prevPtr
			frag.add(PushI, 0);								// nextPtr
			frag.add(PushI, 0);								// isAvailable
			loadIFrom(frag, MMGR_ALLOC_CURRENT_BLOCK);		// start_addr
			loadIFrom(frag, MMGR_ALLOC_SIZE);				// size of block
			frag.add(Call, MEM_MANAGER_MAKE_TAGS);	
			
			// make the tags for remainder block.
			frag.add(PushI, 0);								// prevPtr
			frag.add(PushI, 0);								// nextPtr
			frag.add(PushI, 1);								// isAvailable
			loadIFrom(frag, MMGR_ALLOC_REMAINDER_BLOCK);	// start_addr
			loadIFrom(frag, MMGR_ALLOC_REMAINDER_SIZE);		// size of block
			frag.add(Call, MEM_MANAGER_MAKE_TAGS);	
			
			// insert remainder block into free block list
			loadIFrom(frag, MMGR_ALLOC_REMAINDER_BLOCK);
			frag.add(PushI, MMGR_TAG_SIZE_IN_BYTES);
			frag.add(Add);
			frag.add(Call, MEM_MANAGER_DEALLOCATE);
			
			// currentBlock is now usable.
			frag.add(Jump, MMGR_ALLOC_RETURN_USERBLOCK);
		
		
		
		frag.add(Label, MMGR_ALLOC_NO_BLOCK_WORKS);
			if(DEBUGGING2) {
				printString(frag, "--NO BLOCK WORKS\n");
			}
			loadIFrom(frag, MMGR_ALLOC_SIZE);			// [... size]
//			debugPrintI(frag, "alloc ", MEM_MANAGER_HEAP_END_PTR);
			newBlock(frag);								// [... block]
//			debugPrintI(frag, "alloc ", MEM_MANAGER_HEAP_END_PTR);
			storeITo(frag, MMGR_ALLOC_CURRENT_BLOCK);
		
		// [... ] -> [... userBlock] & return
		frag.add(Label, MMGR_ALLOC_RETURN_USERBLOCK);
			loadIFrom(frag, MMGR_ALLOC_CURRENT_BLOCK);	// [... block]
			frag.add(PushI, MMGR_TAG_SIZE_IN_BYTES);	// [... block tagsize]
			frag.add(Add);								// [... userBlock]

			loadIFrom(frag, MMGR_ALLOC_RETURN_ADDRESS);
			frag.add(Return);
			
		return frag;
	}



	// [... block] -> [...]
	// pre: block is in Free Block List.
	private static ASMCodeFragment subroutineRemoveBlock() {
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_VOID);
		frag.add(Label, MEM_MANAGER_REMOVE_BLOCK);		// [... block (return)]

		declareI(frag, MMGR_REMOVE_RETURN_ADDRESS);
		declareI(frag, MMGR_REMOVE_BLOCK);
		declareI(frag, MMGR_REMOVE_PREV);
		declareI(frag, MMGR_REMOVE_NEXT);
		
		storeITo(frag, MMGR_REMOVE_RETURN_ADDRESS);		// [... block]
		storeITo(frag, MMGR_REMOVE_BLOCK);				// [... ]
		
		// get prev and next
		loadIFrom(frag, MMGR_REMOVE_BLOCK);		// [... block]
		readTagPointer(frag);					// [... prev]
		storeITo(frag, MMGR_REMOVE_PREV);
		
		loadIFrom(frag, MMGR_REMOVE_BLOCK);		// [... block]
		tailTag(frag);							// [... blockTail]
		readTagPointer(frag);					// [... next]
		storeITo(frag, MMGR_REMOVE_NEXT);
		
		
		//set prev block's ptr
		frag.add(Label, MMGR_REMOVE_PROCESS_PREV);
			loadIFrom(frag, MMGR_REMOVE_PREV);
			frag.add(JumpFalse, MMGR_REMOVE_NO_PREV);
			
			// prev.nextPtr = next
			loadIFrom(frag, MMGR_REMOVE_NEXT);			// [... next]
			loadIFrom(frag, MMGR_REMOVE_PREV);			// [... next prev]
			tailTag(frag);								// [... next prevTail]
			writeTagPointer(frag);						// [...]
			frag.add(Jump, MMGR_REMOVE_PROCESS_NEXT);

		frag.add(Label, MMGR_REMOVE_NO_PREV);
			loadIFrom(frag, MMGR_REMOVE_NEXT);
			storeITo(frag, MEM_MANAGER_FIRST_FREE_BLOCK);

		//set next block's ptr
		frag.add(Label, MMGR_REMOVE_PROCESS_NEXT);
			loadIFrom(frag, MMGR_REMOVE_NEXT);
			frag.add(JumpFalse, MMGR_REMOVE_DONE);
			
			// next.prevPtr = prev
			loadIFrom(frag, MMGR_REMOVE_PREV);			// [... prev]
			loadIFrom(frag, MMGR_REMOVE_NEXT);			// [... prev next]
			writeTagPointer(frag);						// [...]

		frag.add(Label, MMGR_REMOVE_DONE);		
			loadIFrom(frag, MMGR_REMOVE_RETURN_ADDRESS);
			frag.add(Return);
		
		return frag;
	}

	// [... usableBlockPtr (return)]
	private static ASMCodeFragment subroutineDeallocate() {
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_VOID);
		frag.add(Label, MEM_MANAGER_DEALLOCATE);		// [... blockptr (return)]
		
		declareI(frag, MMGR_DEALLOC_RETURN_ADDRESS);
		declareI(frag, MMGR_DEALLOC_BLOCK);
		
		//store return addr
		storeITo(frag, MMGR_DEALLOC_RETURN_ADDRESS);	// [... usableBlock]
		
		// convert user block to mmgr block
		frag.add(PushI, MMGR_TAG_SIZE_IN_BYTES);		// [... usableBlock tagsize]
		frag.add(Subtract);								// [... block]
		storeITo(frag, MMGR_DEALLOC_BLOCK);				// [...]
		
		// firstFree.prev = block
		loadIFrom(frag, MMGR_DEALLOC_BLOCK);
		loadIFrom(frag, MEM_MANAGER_FIRST_FREE_BLOCK);	// [... block firstFree]
		writeTagPointer(frag);											

		// block.prev = 0
		frag.add(PushI, 0);
		loadIFrom(frag, MMGR_DEALLOC_BLOCK);	// [... 0 block]
		writeTagPointer(frag);
		
		// block.next = firstFree
		loadIFrom(frag, MEM_MANAGER_FIRST_FREE_BLOCK);	// [... firstFree]
		loadIFrom(frag, MMGR_DEALLOC_BLOCK);			// [... firstFree block]
		tailTag(frag);								// [... firstFree blockTail]
		writeTagPointer(frag);
				
		// block.avail1 = 1;
		frag.add(PushI, 1);						// [... 1]
		loadIFrom(frag, MMGR_DEALLOC_BLOCK);	// [... 1 block]
		writeTagAvailable(frag);

		// block.avail2 = 1;
		frag.add(PushI, 1);						// [... 1]
		loadIFrom(frag, MMGR_DEALLOC_BLOCK);	// [... 1 block]
		tailTag(frag);						// [... 1 blockTail]
		writeTagAvailable(frag);

		// firstFree = block
		loadIFrom(frag, MMGR_DEALLOC_BLOCK);
		storeITo(frag, MEM_MANAGER_FIRST_FREE_BLOCK);
		
		// return
		loadIFrom(frag, MMGR_DEALLOC_RETURN_ADDRESS);
		frag.add(Return);
		return frag;
	}
	
	// [ ...blockAddr, length] -> [ ...]
	private static ASMCodeFragment subroutineStoreStringHeader() {
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_VOID);
		
		frag.add(Label, MEM_STORE_STRING_HEADER);
			declareI(frag, MEM_STORE_STRING_RETURN_ADDRESS);
			
			storeITo(frag, MEM_STORE_STRING_RETURN_ADDRESS);
			frag.add(Exchange);
			
			frag.add(Duplicate);
			frag.add(PushI,6);
			frag.add(StoreI);
			
			frag.add(Duplicate);
			frag.add(PushI, MEM_STRING_STATUS_OFFSET);
			frag.add(Add);
			frag.add(PushI,9);
			frag.add(StoreI);
			
			frag.add(PushI, MEM_STRING_LENGTH_OFFSET);
			frag.add(Add);
			frag.add(Exchange);
			frag.add(StoreI);
			
			loadIFrom(frag, MEM_STORE_STRING_RETURN_ADDRESS);
			frag.add(Return);
			
		return frag;		
	}
	
	//[...blockAddr, size, length, status]
	// [...blockAddr, length, blockAddr, size, blockAddr, status, blockAddr]->[...length]
	private static ASMCodeFragment subroutineStoreArrayHeader() {
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_VOID);
		
		frag.add(Label, MEM_STORE_ARRAY_HEADER);
			declareI(frag, MEM_STORE_ARRAY_RETURN_ADDRESS);
			declareI(frag, MEM_STORE_ARRAY_BLOCK_HEADER);
			declareI(frag, MEM_STORE_ARRAY_STATUS_HEADER);
			declareI(frag, MEM_STORE_ARRAY_LENGTH_HEADER);
			declareI(frag, MEM_STORE_ARRAY_SIZE_HEADER);
		
			storeITo(frag, MEM_STORE_ARRAY_RETURN_ADDRESS);
			storeITo(frag, MEM_STORE_ARRAY_STATUS_HEADER);
			storeITo(frag, MEM_STORE_ARRAY_LENGTH_HEADER);
			storeITo(frag, MEM_STORE_ARRAY_SIZE_HEADER);
			storeITo(frag, MEM_STORE_ARRAY_BLOCK_HEADER);
			
			loadIFrom(frag, MEM_STORE_ARRAY_BLOCK_HEADER);
			frag.add(PushI, 7);
			frag.add(StoreI);
			
			loadIFrom(frag, MEM_STORE_ARRAY_BLOCK_HEADER);
			frag.add(PushI, MEM_ARRAY_STATUS_OFFSET);
			frag.add(Add);
			loadIFrom(frag, MEM_STORE_ARRAY_STATUS_HEADER);
			frag.add(StoreI);
			
			loadIFrom(frag, MEM_STORE_ARRAY_BLOCK_HEADER);
			frag.add(PushI, MEM_ARRAY_SIZE_OFFSET);
			frag.add(Add);
			loadIFrom(frag, MEM_STORE_ARRAY_SIZE_HEADER);
			frag.add(StoreI);
			
			loadIFrom(frag, MEM_STORE_ARRAY_BLOCK_HEADER);
			frag.add(PushI, MEM_ARRAY_LENGTH_OFFSET);
			frag.add(Add);
			loadIFrom(frag, MEM_STORE_ARRAY_LENGTH_HEADER);
			frag.add(StoreI);
			
			loadIFrom(frag, MEM_STORE_ARRAY_BLOCK_HEADER);
			loadIFrom(frag, MEM_STORE_ARRAY_LENGTH_HEADER);
			loadIFrom(frag, MEM_STORE_ARRAY_RETURN_ADDRESS);
			frag.add(Return);
			
		return frag;
	}
	
	private static ASMCodeFragment subroutineStoreArrayOneByte() {
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_VOID);
		
		frag.add(Label, MEM_STORE_ARRAY_ONE_BYTE);
			declareI(frag, MEM_STORE_ARRAY_ONE_BYTE_RETURN_ADDRESS);
			declareI(frag, MEM_STORE_ARRAY_LENGTH);
			declareI(frag, MEM_STORE_ARRAY_BLOCK);
			declareI(frag, MEM_STORE_ARRAY_FLAG);
		
			storeITo(frag, MEM_STORE_ARRAY_ONE_BYTE_RETURN_ADDRESS);
			storeITo(frag, MEM_STORE_ARRAY_FLAG);
			storeITo(frag, MEM_STORE_ARRAY_LENGTH);
			storeITo(frag, MEM_STORE_ARRAY_BLOCK);
			
			
			frag.add(Label, MEM_STORE_ARRAY_LOOP_ONE);
				loadIFrom(frag, MEM_STORE_ARRAY_LENGTH);
				frag.add(JumpFalse, MEM_STORE_ARRAY_LOOP_END_ONE);
				loadIFrom(frag, MEM_STORE_ARRAY_LENGTH);
				frag.add(PushI, 1);
				frag.add(Subtract);
				storeITo(frag, MEM_STORE_ARRAY_LENGTH);
				loadIFrom(frag, MEM_STORE_ARRAY_LENGTH);
				loadIFrom(frag, MEM_STORE_ARRAY_BLOCK);
				frag.add(PushI, MEM_ARRAY_CONTENT_OFFSET);
				frag.add(Add);
				frag.add(Add);
				loadIFrom(frag, MEM_STORE_ARRAY_FLAG);
				frag.add(JumpFalse, MEM_STORE_ARRAY_EMPTY_ONE);
				frag.add(Exchange);
				frag.add(StoreC);
				frag.add(Jump,MEM_STORE_ARRAY_LOOP_ONE);
				frag.add(Label, MEM_STORE_ARRAY_EMPTY_ONE);
				frag.add(PushI, 0);
				frag.add(StoreC);
				frag.add(Jump,MEM_STORE_ARRAY_LOOP_ONE);
				
				
				
			frag.add(Label, MEM_STORE_ARRAY_LOOP_END_ONE);
			loadIFrom(frag, MEM_STORE_ARRAY_BLOCK);
			loadIFrom(frag, MEM_STORE_ARRAY_ONE_BYTE_RETURN_ADDRESS);
			frag.add(Return);
			
			return frag;
	}
	
	//[...blockAddr, length]
	private static ASMCodeFragment subroutineStoreArrayFourByte() {
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_VOID);
		
		frag.add(Label, MEM_STORE_ARRAY_FOUR_BYTE);
			declareI(frag, MEM_STORE_ARRAY_FOUR_BYTE_RETURN_ADDRESS);
		
			storeITo(frag, MEM_STORE_ARRAY_FOUR_BYTE_RETURN_ADDRESS);
			storeITo(frag, MEM_STORE_ARRAY_FLAG);
			storeITo(frag, MEM_STORE_ARRAY_LENGTH);
			storeITo(frag, MEM_STORE_ARRAY_BLOCK);
			
			
			frag.add(Label, MEM_STORE_ARRAY_LOOP_FOUR);
				loadIFrom(frag, MEM_STORE_ARRAY_LENGTH);
				frag.add(JumpFalse, MEM_STORE_ARRAY_LOOP_END_FOUR);
				loadIFrom(frag, MEM_STORE_ARRAY_LENGTH);
				frag.add(PushI, 1);
				frag.add(Subtract); //[...length-1]
				storeITo(frag, MEM_STORE_ARRAY_LENGTH);
				loadIFrom(frag, MEM_STORE_ARRAY_LENGTH);
				frag.add(PushI, 4);
				frag.add(Multiply); //[...4*(length)]
				loadIFrom(frag, MEM_STORE_ARRAY_BLOCK); //[...4*(length), blockAddr]
				frag.add(PushI, MEM_ARRAY_CONTENT_OFFSET); //[...4*(length), blockAddr, contentOff]
				frag.add(Add);
				frag.add(Add);
				loadIFrom(frag, MEM_STORE_ARRAY_FLAG);
				frag.add(JumpFalse, MEM_STORE_ARRAY_EMPTY_FOUR);
				frag.add(Exchange);
				frag.add(StoreI);
				frag.add(Jump,MEM_STORE_ARRAY_LOOP_FOUR);
				frag.add(Label, MEM_STORE_ARRAY_EMPTY_FOUR);
				frag.add(PushI, 0);
				frag.add(StoreI);
				frag.add(Jump,MEM_STORE_ARRAY_LOOP_FOUR);
				
				
			frag.add(Label, MEM_STORE_ARRAY_LOOP_END_FOUR);
			loadIFrom(frag, MEM_STORE_ARRAY_BLOCK);
			loadIFrom(frag, MEM_STORE_ARRAY_FOUR_BYTE_RETURN_ADDRESS);
			frag.add(Return);
			
			return frag;
	}
	
	private static ASMCodeFragment subroutineStoreArrayEightByte() {
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_VOID);
		
		frag.add(Label, MEM_STORE_ARRAY_EIGHT_BYTE);
			declareI(frag, MEM_STORE_ARRAY_EIGHT_BYTE_RETURN_ADDRESS);
		
			storeITo(frag, MEM_STORE_ARRAY_EIGHT_BYTE_RETURN_ADDRESS);
			storeITo(frag, MEM_STORE_ARRAY_FLAG);
			storeITo(frag, MEM_STORE_ARRAY_LENGTH);
			storeITo(frag, MEM_STORE_ARRAY_BLOCK);
			
			
			frag.add(Label, MEM_STORE_ARRAY_LOOP_EIGHT);
				loadIFrom(frag, MEM_STORE_ARRAY_LENGTH);
				frag.add(JumpFalse, MEM_STORE_ARRAY_LOOP_END_EIGHT);
				loadIFrom(frag, MEM_STORE_ARRAY_LENGTH);
				frag.add(PushI, 1);
				frag.add(Subtract);
				storeITo(frag, MEM_STORE_ARRAY_LENGTH);
				loadIFrom(frag, MEM_STORE_ARRAY_LENGTH);
				frag.add(PushI, 8);
				frag.add(Multiply);
				loadIFrom(frag, MEM_STORE_ARRAY_BLOCK);
				frag.add(PushI, MEM_ARRAY_CONTENT_OFFSET);
				frag.add(Add);
				frag.add(Add);
				loadIFrom(frag, MEM_STORE_ARRAY_FLAG);
				frag.add(JumpFalse, MEM_STORE_ARRAY_EMPTY_EIGHT);
				frag.add(Exchange);
				frag.add(StoreF);
				frag.add(Jump,MEM_STORE_ARRAY_LOOP_EIGHT);
				frag.add(Label, MEM_STORE_ARRAY_EMPTY_EIGHT);
				frag.add(PushF, 0.0);
				frag.add(StoreF);
				frag.add(Jump,MEM_STORE_ARRAY_LOOP_EIGHT);
				
				
			frag.add(Label, MEM_STORE_ARRAY_LOOP_END_EIGHT);
			loadIFrom(frag, MEM_STORE_ARRAY_BLOCK);
			loadIFrom(frag, MEM_STORE_ARRAY_EIGHT_BYTE_RETURN_ADDRESS);
			frag.add(Return);
			
			return frag;
	}
	
	// [...arrayAddr(ret)]
	private static ASMCodeFragment subroutineArrayRelease() {
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_VOID);
		
		frag.add(Label, MEM_ARRAY_RELEASE);
			declareI(frag, MEM_ARRAY_RELEASE_RETURN_ADDRESS);
			declareI(frag, MEM_ARRAY_RELEASE_ARRAY_ADDRESS);
			declareI(frag, MEM_ARRAY_RELEASE_ARRAY_LENGTH);
			
			storeITo(frag, MEM_ARRAY_RELEASE_RETURN_ADDRESS);
			storeITo(frag, MEM_ARRAY_RELEASE_ARRAY_ADDRESS);
			
			loadIFrom(frag, MEM_ARRAY_RELEASE_ARRAY_ADDRESS);
			frag.add(Call, MEM_ARRAY_RECORD_VALID);
			
			loadIFrom(frag, MEM_ARRAY_RELEASE_ARRAY_ADDRESS);
			frag.add(PushI, MEM_ARRAY_STATUS_OFFSET);
			frag.add(Add);
			frag.add(LoadI);
			//[...status]
			frag.add(PushI, 2);
			frag.add(Add);
			//[...newStatus]
			loadIFrom(frag, MEM_ARRAY_RELEASE_ARRAY_ADDRESS);
			frag.add(PushI, MEM_ARRAY_STATUS_OFFSET);
			frag.add(Add);
			//[...newSatus, statusAddress]
			frag.add(Exchange);
			frag.add(StoreI);
			
			loadIFrom(frag, MEM_ARRAY_RELEASE_ARRAY_ADDRESS);
			//[...arrayAddr]
			loadIFrom(frag, MEM_ARRAY_RELEASE_ARRAY_ADDRESS);
			frag.add(PushI, MEM_ARRAY_STATUS_OFFSET);
			frag.add(Add);
			frag.add(LoadI);
			frag.add(PushI, 4);
			frag.add(BTAnd);
			//[...arrayAddr, flag]
			
			frag.add(JumpFalse, MEM_ARRAY_RELEASE_NOT_REF);
			loadIFrom(frag, MEM_ARRAY_RELEASE_ARRAY_ADDRESS);
			frag.add(PushI, MEM_ARRAY_LENGTH_OFFSET);
			frag.add(Add);
			frag.add(LoadI);
				// [...length]
			storeITo(frag, MEM_ARRAY_RELEASE_ARRAY_LENGTH);
				// [...length]
			
			frag.add(Label, MEM_ARRAY_RELEASE_LOOP);
				loadIFrom(frag, MEM_ARRAY_RELEASE_ARRAY_LENGTH);
				frag.add(JumpFalse, MEM_ARRAY_RELEASE_END);
				loadIFrom(frag, MEM_ARRAY_RELEASE_ARRAY_LENGTH);
				frag.add(PushI, 1);
				frag.add(Subtract);
				storeITo(frag, MEM_ARRAY_RELEASE_ARRAY_LENGTH);
				
				loadIFrom(frag, MEM_ARRAY_RELEASE_ARRAY_LENGTH);
				//[...length]
				
				loadIFrom(frag, MEM_ARRAY_RELEASE_ARRAY_ADDRESS);
				frag.add(PushI, MEM_ARRAY_SIZE_OFFSET);
				frag.add(Add);
				frag.add(LoadI);
				//[...length, size]
				
				frag.add(Multiply);
				//[...offset]
				
				frag.add(PushI, MEM_ARRAY_CONTENT_OFFSET);
				loadIFrom(frag, MEM_ARRAY_RELEASE_ARRAY_ADDRESS);
				frag.add(Add);
				frag.add(Add);
				
				//[...address]
				frag.add(LoadI);
				//[...arrayAddr]
				loadIFrom(frag, MEM_ARRAY_RELEASE_RETURN_ADDRESS);
				loadIFrom(frag, MEM_ARRAY_RELEASE_ARRAY_ADDRESS);
				loadIFrom(frag, MEM_ARRAY_RELEASE_ARRAY_LENGTH);

				frag.add(Call, MEM_ARRAY_RELEASE);
				storeITo(frag, MEM_ARRAY_RELEASE_ARRAY_LENGTH);
				storeITo(frag, MEM_ARRAY_RELEASE_ARRAY_ADDRESS);
				storeITo(frag, MEM_ARRAY_RELEASE_RETURN_ADDRESS);
				frag.add(Jump, MEM_ARRAY_RELEASE_LOOP);
				
				
			
			frag.add(Label, MEM_ARRAY_RELEASE_NOT_REF);
				loadIFrom(frag, MEM_ARRAY_RELEASE_ARRAY_ADDRESS);
				frag.add(Call, MEM_MANAGER_DEALLOCATE);
				
				
			frag.add(Label, MEM_ARRAY_RELEASE_END);
				loadIFrom(frag, MEM_ARRAY_RELEASE_RETURN_ADDRESS);
				frag.add(Return);
			
				
			return frag;
		
	}
	
	//[...arrayAddr]
	private static ASMCodeFragment subroutineArrayClone() {
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_VOID);
		
		frag.add(Label, MEM_ARRAY_CLONE);
			declareI(frag, MEM_ARRAY_CLONE_RETURN_ADDRESS);
			declareI(frag, MEM_ARRAY_CLONE_ARRAY_ADDRESS);
			declareI(frag, MEM_ARRAY_CLONE_RESULT_ADDRESS);
			declareI(frag, MEM_ARRAY_CLONE_RECORD_SIZE);
			
			
			storeITo(frag, MEM_ARRAY_CLONE_RETURN_ADDRESS);
			storeITo(frag, MEM_ARRAY_CLONE_ARRAY_ADDRESS);
			
			loadIFrom(frag, MEM_ARRAY_CLONE_ARRAY_ADDRESS);
			frag.add(PushI, MEM_ARRAY_LENGTH_OFFSET);
			frag.add(Add);
			frag.add(LoadI);
			//[...length]
			loadIFrom(frag, MEM_ARRAY_CLONE_ARRAY_ADDRESS);
			frag.add(PushI, MEM_ARRAY_SIZE_OFFSET);
			frag.add(Add);
			frag.add(LoadI);
			//[...length, size]
			
			frag.add(Multiply);
			//[...contentSize]
			frag.add(PushI, MEM_ARRAY_HEADER);
			frag.add(Add);
			storeITo(frag, MEM_ARRAY_CLONE_RECORD_SIZE);
			loadIFrom(frag, MEM_ARRAY_CLONE_RECORD_SIZE);
			//[...recordSize]
			
			frag.add(Call, MEM_MANAGER_ALLOCATE);
			//[...cloneAddr]
			storeITo(frag, MEM_ARRAY_CLONE_RESULT_ADDRESS);
			
			frag.add(Label, MEM_ARRAY_CLONE_LOOP);
				loadIFrom(frag, MEM_ARRAY_CLONE_RECORD_SIZE);
				frag.add(JumpFalse, MEM_ARRAY_CLONE_END);
				loadIFrom(frag, MEM_ARRAY_CLONE_RECORD_SIZE);
				frag.add(PushI,1);
				frag.add(Subtract);
				storeITo(frag, MEM_ARRAY_CLONE_RECORD_SIZE);
				loadIFrom(frag, MEM_ARRAY_CLONE_RECORD_SIZE);
				loadIFrom(frag, MEM_ARRAY_CLONE_ARRAY_ADDRESS);
				frag.add(Add);
				frag.add(LoadC);
				
				loadIFrom(frag, MEM_ARRAY_CLONE_RECORD_SIZE);
				loadIFrom(frag, MEM_ARRAY_CLONE_RESULT_ADDRESS);
				frag.add(Add);
				frag.add(Exchange);
				frag.add(StoreC);

				frag.add(Jump, MEM_ARRAY_CLONE_LOOP);
				
			frag.add(Label, MEM_ARRAY_CLONE_END);
				loadIFrom(frag, MEM_ARRAY_CLONE_RESULT_ADDRESS);
				loadIFrom(frag, MEM_ARRAY_CLONE_RETURN_ADDRESS);
				frag.add(Return);
				
			return frag;
			
	}
	
	//[...arrayAddr, index, flag]
	private static ASMCodeFragment subroutineArrayIndex() {
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_VOID);
		
		frag.add(Label, MEM_ARRAY_INDEX);
			declareI(frag, MEM_ARRAY_INDEX_RETURN_ADDRESS);
			declareI(frag, MEM_ARRAY_INDEX_ARRAY_ADDRESS);
			declareI(frag, MEM_ARRAY_INDEX_NUM);
			declareI(frag, MEM_ARRAY_INDEX_SIZE);
			declareI(frag, MEM_ARRAY_INDEX_FLAG);
			
			storeITo(frag, MEM_ARRAY_INDEX_RETURN_ADDRESS);
			storeITo(frag, MEM_ARRAY_INDEX_FLAG);
			storeITo(frag, MEM_ARRAY_INDEX_NUM);
			storeITo(frag, MEM_ARRAY_INDEX_ARRAY_ADDRESS);
			
			loadIFrom(frag, MEM_ARRAY_INDEX_NUM);
			frag.add(JumpNeg, RunTime.ARRAY_INDEX_NEGATIVE);
			loadIFrom(frag, MEM_ARRAY_INDEX_ARRAY_ADDRESS);
			frag.add(PushI,MEM_ARRAY_LENGTH_OFFSET);
			frag.add(Add);
			frag.add(LoadI);
			frag.add(PushI,1);
			frag.add(Subtract);
			loadIFrom(frag, MEM_ARRAY_INDEX_NUM);
			frag.add(Subtract);
			frag.add(JumpNeg, RunTime.ARRAY_INDEX_EXCEED);
			
			loadIFrom(frag, MEM_ARRAY_INDEX_ARRAY_ADDRESS);
			frag.add(PushI, MEM_ARRAY_SIZE_OFFSET);
			frag.add(Add);
			frag.add(LoadI);
			//[...size]
			storeITo(frag, MEM_ARRAY_INDEX_SIZE);
			loadIFrom(frag, MEM_ARRAY_INDEX_SIZE);
			loadIFrom(frag, MEM_ARRAY_INDEX_NUM);
			//[...size,index]
			frag.add(Multiply);
			frag.add(PushI, MEM_ARRAY_HEADER);
			loadIFrom(frag, MEM_ARRAY_INDEX_ARRAY_ADDRESS);
			frag.add(Add);
			frag.add(Add);
			//[...elementAddr]
			
			loadIFrom(frag, MEM_ARRAY_INDEX_RETURN_ADDRESS);
			frag.add(Return);
				
			return frag;
	}
	
	// [int,int]
	private static ASMCodeFragment subroutineRatGCD() {
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_VOID);
		
		frag.add(Label, MEM_RAT_GCD);
			declareI(frag, MEM_RAT_GCD_RETURN_ADDRESS);
			declareI(frag, MEM_RAT_GCD_NUMERATOR);
			declareI(frag, MEM_RAT_GCD_DENOMINATOR);
			declareI(frag, MEM_RAT_GCD_A);
			declareI(frag, MEM_RAT_GCD_B);
			
			storeITo(frag, MEM_RAT_GCD_RETURN_ADDRESS);
			storeITo(frag, MEM_RAT_GCD_DENOMINATOR);
			storeITo(frag, MEM_RAT_GCD_NUMERATOR);
			
			loadIFrom(frag, MEM_RAT_GCD_NUMERATOR);
			storeITo(frag, MEM_RAT_GCD_A);
			loadIFrom(frag, MEM_RAT_GCD_DENOMINATOR);
			storeITo(frag, MEM_RAT_GCD_B);
			
			frag.add(Label, MEM_RAT_GCD_LOOP);
				loadIFrom(frag, MEM_RAT_GCD_B);
				frag.add(JumpFalse, MEM_RAT_GCD_END);
				loadIFrom(frag, MEM_RAT_GCD_B);
				loadIFrom(frag, MEM_RAT_GCD_A);
				loadIFrom(frag, MEM_RAT_GCD_B);
				frag.add(Remainder);
				storeITo(frag, MEM_RAT_GCD_B);
				storeITo(frag, MEM_RAT_GCD_A);
				frag.add(Jump, MEM_RAT_GCD_LOOP);
				
			frag.add(Label, MEM_RAT_GCD_END);
				loadIFrom(frag, MEM_RAT_GCD_NUMERATOR);
				loadIFrom(frag, MEM_RAT_GCD_A);
				frag.add(Divide);
				frag.add(JumpNeg, MEM_RAT_GCD_NEG);
				loadIFrom(frag, MEM_RAT_GCD_NUMERATOR);
				loadIFrom(frag, MEM_RAT_GCD_A);
				frag.add(Divide);
				loadIFrom(frag, MEM_RAT_GCD_DENOMINATOR);
				loadIFrom(frag, MEM_RAT_GCD_A);
				frag.add(Divide);
				
				loadIFrom(frag, MEM_RAT_GCD_RETURN_ADDRESS);
				frag.add(Return);
				
			frag.add(Label, MEM_RAT_GCD_NEG);
				loadIFrom(frag, MEM_RAT_GCD_NUMERATOR);
				loadIFrom(frag, MEM_RAT_GCD_A);
				frag.add(Divide);
				frag.add(Negate);
				loadIFrom(frag, MEM_RAT_GCD_DENOMINATOR);
				loadIFrom(frag, MEM_RAT_GCD_A);
				frag.add(Divide);
				frag.add(Negate);
			
				loadIFrom(frag, MEM_RAT_GCD_RETURN_ADDRESS);
				frag.add(Return);
				
			return frag;
	}
	
	private static ASMCodeFragment subroutineRatStore() {
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_VOID);
		
		frag.add(Label, MEM_RAT_STORE);
			declareI(frag, MEM_RAT_STORE_RETURN_ADDRESS);
			declareI(frag, MEM_RAT_STORE_NUMERATOR);
			declareI(frag, MEM_RAT_STORE_DENOMINATOR);
			declareI(frag, MEM_RAT_STORE_TARGET_ADDRESS);
			
			storeITo(frag, MEM_RAT_STORE_RETURN_ADDRESS);
			storeITo(frag, MEM_RAT_STORE_DENOMINATOR);
			storeITo(frag, MEM_RAT_STORE_NUMERATOR);
			storeITo(frag, MEM_RAT_STORE_TARGET_ADDRESS);
			
			loadIFrom(frag, MEM_RAT_STORE_TARGET_ADDRESS);
			loadIFrom(frag, MEM_RAT_STORE_NUMERATOR);
			frag.add(StoreI);
			
			loadIFrom(frag, MEM_RAT_STORE_TARGET_ADDRESS);
			frag.add(PushI, 4);
			frag.add(Add);
			loadIFrom(frag, MEM_RAT_STORE_DENOMINATOR);
			frag.add(StoreI);
			
			loadIFrom(frag, MEM_RAT_STORE_RETURN_ADDRESS);
			frag.add(Return);
			
			return frag;
	}
	
	private static ASMCodeFragment subroutineRatAid() {
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_VOID);
		
		frag.add(Label, MEM_RAT_AID);
			declareI(frag, MEM_RAT_AID_RETURN_ADDRESS);
			declareI(frag, MEM_RAT_AID_A);
			declareI(frag, MEM_RAT_AID_B);
			
			storeITo(frag, MEM_RAT_AID_RETURN_ADDRESS);
			storeITo(frag, MEM_RAT_AID_B);
			storeITo(frag, MEM_RAT_AID_A);
			
			loadIFrom(frag, MEM_RAT_AID_A);
			loadIFrom(frag, MEM_RAT_AID_B);
			frag.add(Divide);
			loadIFrom(frag, MEM_RAT_AID_B);
			
			loadIFrom(frag, MEM_RAT_AID_RETURN_ADDRESS);
			
			frag.add(Return);
			
			return frag;
	}
	
	private static ASMCodeFragment subroutineRatPrint() {
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_VOID);
		
		frag.add(Label, MEM_RAT_PRINT);
			declareI(frag, MEM_RAT_PRINT_RETURN_ADDRESS);
			declareI(frag, MEM_RAT_PRINT_A);
			declareI(frag, MEM_RAT_PRINT_B);
	
			storeITo(frag, MEM_RAT_PRINT_RETURN_ADDRESS);
			storeITo(frag, MEM_RAT_PRINT_B);
			storeITo(frag, MEM_RAT_PRINT_A);
			
			loadIFrom(frag, MEM_RAT_PRINT_A);
			frag.add(JumpFalse, MEM_RAT_PRINT_ZERO);
			loadIFrom(frag, MEM_RAT_PRINT_A);
			loadIFrom(frag, MEM_RAT_PRINT_B);
			frag.add(Divide);
			
			frag.add(JumpFalse, MEM_RAT_PRINT_NEG);
			loadIFrom(frag, MEM_RAT_PRINT_A);
			loadIFrom(frag, MEM_RAT_PRINT_B);
			frag.add(Divide);
			frag.add(PushD, RunTime.INTEGER_PRINT_FORMAT);
			frag.add(Printf);
			
			frag.add(Label, MEM_RAT_PRINT_RAT);
			loadIFrom(frag, MEM_RAT_PRINT_A);
			loadIFrom(frag, MEM_RAT_PRINT_B);
			frag.add(Remainder);
			
			frag.add(JumpFalse, MEM_RAT_PRINT_END);
			frag.add(PushI, 95);
			frag.add(PushD, RunTime.CHAR_PRINT_FORMAT);
			frag.add(Printf);
			
			loadIFrom(frag, MEM_RAT_PRINT_A);
			loadIFrom(frag, MEM_RAT_PRINT_B);
			frag.add(Remainder);
			frag.add(PushD, RunTime.INTEGER_PRINT_FORMAT);
			frag.add(Printf);
			
			frag.add(PushI, 47);
			frag.add(PushD, RunTime.CHAR_PRINT_FORMAT);
			frag.add(Printf);
			
			loadIFrom(frag, MEM_RAT_PRINT_B);
			loadIFrom(frag, MEM_RAT_PRINT_B);
			frag.add(JumpPos, MEM_RAT_PRINT_CON);
			frag.add(Negate);
			frag.add(Label, MEM_RAT_PRINT_CON);
			frag.add(PushD, RunTime.INTEGER_PRINT_FORMAT);
			frag.add(Printf);
			
			frag.add(Jump, MEM_RAT_PRINT_END);
			
			frag.add(Label, MEM_RAT_PRINT_ZERO);
				frag.add(PushI, 0);
				frag.add(PushD, RunTime.INTEGER_PRINT_FORMAT);
				frag.add(Printf);
			frag.add(Label, MEM_RAT_PRINT_END);
				loadIFrom(frag, MEM_RAT_PRINT_RETURN_ADDRESS);
				frag.add(Return);
			
			frag.add(Label, MEM_RAT_PRINT_NEG);
				loadIFrom(frag, MEM_RAT_PRINT_B);
				frag.add(JumpPos, MEM_RAT_PRINT_RAT);
				frag.add(PushI, 45);
				frag.add(PushD, RunTime.CHAR_PRINT_FORMAT);
				frag.add(Printf);
				frag.add(Jump, MEM_RAT_PRINT_RAT);
				
			return frag;
				
	}
	
	private static ASMCodeFragment subroutineRatAdd() {
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_VOID);
		
		frag.add(Label, MEM_RAT_ADD);
			declareI(frag, MEM_RAT_ADD_RETURN_ADDRESS);
			declareI(frag, MEM_RAT_ADD_A_NUM);
			declareI(frag, MEM_RAT_ADD_A_DEN);
			declareI(frag, MEM_RAT_ADD_B_NUM);
			declareI(frag, MEM_RAT_ADD_B_DEN);
			
			storeITo(frag, MEM_RAT_ADD_RETURN_ADDRESS);
			storeITo(frag, MEM_RAT_ADD_B_DEN);
			storeITo(frag, MEM_RAT_ADD_B_NUM);
			storeITo(frag, MEM_RAT_ADD_A_DEN);
			storeITo(frag, MEM_RAT_ADD_A_NUM);
			
			loadIFrom(frag, MEM_RAT_ADD_A_NUM);
			loadIFrom(frag, MEM_RAT_ADD_B_DEN);
			frag.add(Multiply);
			
			loadIFrom(frag, MEM_RAT_ADD_B_NUM);
			loadIFrom(frag, MEM_RAT_ADD_A_DEN);
			frag.add(Multiply);
			
			frag.add(Add);
			
			loadIFrom(frag, MEM_RAT_ADD_A_DEN);
			loadIFrom(frag, MEM_RAT_ADD_B_DEN);
			frag.add(Multiply);
			
			frag.add(Call,MEM_RAT_GCD);
			
			loadIFrom(frag, MEM_RAT_ADD_RETURN_ADDRESS);
			frag.add(Return);;
			
			return frag;
	}
	
	private static ASMCodeFragment subroutineRatSubtract() {
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_VOID);
		
		frag.add(Label, MEM_RAT_SUBTRACT);
			declareI(frag, MEM_RAT_SUBTRACT_RETURN_ADDRESS);
			declareI(frag, MEM_RAT_SUBTRACT_A_NUM);
			declareI(frag, MEM_RAT_SUBTRACT_A_DEN);
			declareI(frag, MEM_RAT_SUBTRACT_B_NUM);
			declareI(frag, MEM_RAT_SUBTRACT_B_DEN);
			
			storeITo(frag, MEM_RAT_SUBTRACT_RETURN_ADDRESS);
			storeITo(frag, MEM_RAT_SUBTRACT_B_DEN);
			storeITo(frag, MEM_RAT_SUBTRACT_B_NUM);
			storeITo(frag, MEM_RAT_SUBTRACT_A_DEN);
			storeITo(frag, MEM_RAT_SUBTRACT_A_NUM);
			
			loadIFrom(frag, MEM_RAT_SUBTRACT_A_NUM);
			loadIFrom(frag, MEM_RAT_SUBTRACT_B_DEN);
			frag.add(Multiply);
			
			loadIFrom(frag, MEM_RAT_SUBTRACT_B_NUM);
			loadIFrom(frag, MEM_RAT_SUBTRACT_A_DEN);
			frag.add(Multiply);
			
			frag.add(Subtract);
			
			loadIFrom(frag, MEM_RAT_SUBTRACT_A_DEN);
			loadIFrom(frag, MEM_RAT_SUBTRACT_B_DEN);
			frag.add(Multiply);
			
			frag.add(Call,MEM_RAT_GCD);
			
			loadIFrom(frag, MEM_RAT_SUBTRACT_RETURN_ADDRESS);
			frag.add(Return);
			
			return frag;
	}
	
	private static ASMCodeFragment subroutineRatMultiply() {
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_VOID);
		
		frag.add(Label, MEM_RAT_MULTIPLY);
			declareI(frag, MEM_RAT_MULTIPLY_RETURN_ADDRESS);
			declareI(frag, MEM_RAT_MULTIPLY_A_NUM);
			declareI(frag, MEM_RAT_MULTIPLY_A_DEN);
			declareI(frag, MEM_RAT_MULTIPLY_B_NUM);
			declareI(frag, MEM_RAT_MULTIPLY_B_DEN);
			
			storeITo(frag, MEM_RAT_MULTIPLY_RETURN_ADDRESS);
			storeITo(frag, MEM_RAT_MULTIPLY_B_DEN);
			storeITo(frag, MEM_RAT_MULTIPLY_B_NUM);
			storeITo(frag, MEM_RAT_MULTIPLY_A_DEN);
			storeITo(frag, MEM_RAT_MULTIPLY_A_NUM);
			
			loadIFrom(frag, MEM_RAT_MULTIPLY_A_NUM);
			loadIFrom(frag, MEM_RAT_MULTIPLY_B_NUM);
			frag.add(Multiply);
			
			loadIFrom(frag, MEM_RAT_MULTIPLY_A_DEN);
			loadIFrom(frag, MEM_RAT_MULTIPLY_B_DEN);
			frag.add(Multiply);
			
			frag.add(Call,MEM_RAT_GCD);
			
			loadIFrom(frag, MEM_RAT_MULTIPLY_RETURN_ADDRESS);
			frag.add(Return);
			
			return frag;
	}
	
	private static ASMCodeFragment subroutineRatDivide() {
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_VOID);
		
		frag.add(Label, MEM_RAT_DIVIDE);
			declareI(frag, MEM_RAT_DIVIDE_RETURN_ADDRESS);
			declareI(frag, MEM_RAT_DIVIDE_A_NUM);
			declareI(frag, MEM_RAT_DIVIDE_A_DEN);
			declareI(frag, MEM_RAT_DIVIDE_B_NUM);
			declareI(frag, MEM_RAT_DIVIDE_B_DEN);
			
			storeITo(frag, MEM_RAT_DIVIDE_RETURN_ADDRESS);
			storeITo(frag, MEM_RAT_DIVIDE_B_DEN);
			storeITo(frag, MEM_RAT_DIVIDE_B_NUM);
			storeITo(frag, MEM_RAT_DIVIDE_A_DEN);
			storeITo(frag, MEM_RAT_DIVIDE_A_NUM);
			
			loadIFrom(frag, MEM_RAT_DIVIDE_B_NUM);
			frag.add(JumpFalse, RunTime.RAT_DIVIDE_BY_ZERO_RUNTIME_ERROR);
			loadIFrom(frag, MEM_RAT_DIVIDE_A_NUM);
			loadIFrom(frag, MEM_RAT_DIVIDE_B_DEN);
			frag.add(Multiply);
			
			loadIFrom(frag, MEM_RAT_DIVIDE_A_DEN);
			loadIFrom(frag, MEM_RAT_DIVIDE_B_NUM);
			frag.add(Multiply);
			
			frag.add(Call,MEM_RAT_GCD);
			
			loadIFrom(frag, MEM_RAT_DIVIDE_RETURN_ADDRESS);
			frag.add(Return);
			
			return frag;
	}
	
	// [...arrayAddr(ret)]
	private static ASMCodeFragment subroutineArrayIntPrint() {
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_VOID);
		
		frag.add(Label, MEM_ARRAY_INT_PRINT);
			declareI(frag, MEM_ARRAY_INT_RETURN_ADDRESS);
			declareI(frag, MEM_ARRAY_INT_ARRAY_ADDRESS);
			declareI(frag, MEM_ARRAY_INT_END_ARRAY_ADDRESS);
			declareI(frag, MEM_ARRAY_INT_LENGTH);
			declareI(frag, MEM_ARRAY_INT_SIZE);
			
			storeITo(frag, MEM_ARRAY_INT_RETURN_ADDRESS);
			storeITo(frag, MEM_ARRAY_INT_ARRAY_ADDRESS);
			
			loadIFrom(frag, MEM_ARRAY_INT_ARRAY_ADDRESS);
			//[...arrayAddress]
			frag.add(Call, MEM_ARRAY_RECORD_VALID);
			//[...]
			
			
			loadIFrom(frag, MEM_ARRAY_INT_ARRAY_ADDRESS);
			frag.add(PushI, MEM_ARRAY_STATUS_OFFSET);
			frag.add(Add);
			frag.add(LoadI);
			
			frag.add(PushI, 91);
			frag.add(PushD, RunTime.CHAR_PRINT_FORMAT);
			frag.add(Printf);
			
			loadIFrom(frag, MEM_ARRAY_INT_ARRAY_ADDRESS);
			frag.add(PushI, MEM_ARRAY_LENGTH_OFFSET);
			frag.add(Add);
			frag.add(LoadI);
			storeITo(frag, MEM_ARRAY_INT_LENGTH);
			
			loadIFrom(frag, MEM_ARRAY_INT_ARRAY_ADDRESS);
			frag.add(PushI, MEM_ARRAY_SIZE_OFFSET);
			frag.add(Add);
			frag.add(LoadI);
			storeITo(frag, MEM_ARRAY_INT_SIZE);
			
			loadIFrom(frag, MEM_ARRAY_INT_ARRAY_ADDRESS);
			frag.add(PushI, MEM_ARRAY_HEADER);
			loadIFrom(frag, MEM_ARRAY_INT_LENGTH);
			loadIFrom(frag, MEM_ARRAY_INT_SIZE);
			frag.add(Multiply);
			frag.add(Add);
			frag.add(Add);
			storeITo(frag, MEM_ARRAY_INT_END_ARRAY_ADDRESS);
			
			frag.add(JumpPos, MEM_ARRAY_INT_ARRAY);
			
			frag.add(Label, MEM_ARRAY_INT_LOOP);
				loadIFrom(frag, MEM_ARRAY_INT_LENGTH);
				
				frag.add(JumpFalse, MEM_ARRAY_INT_PRINT_END);
				loadIFrom(frag, MEM_ARRAY_INT_END_ARRAY_ADDRESS);
				loadIFrom(frag, MEM_ARRAY_INT_LENGTH);
				loadIFrom(frag, MEM_ARRAY_INT_SIZE);
				frag.add(Multiply);
				frag.add(Subtract);
				frag.add(LoadI);
				frag.add(PushD, RunTime.INTEGER_PRINT_FORMAT);
				frag.add(Printf);
				
				loadIFrom(frag, MEM_ARRAY_INT_LENGTH);
				frag.add(PushI, 1);
				frag.add(Subtract);
				storeITo(frag, MEM_ARRAY_INT_LENGTH);
				loadIFrom(frag, MEM_ARRAY_INT_LENGTH);
				frag.add(JumpFalse, MEM_ARRAY_INT_LOOP);
				
				frag.add(PushI, 44);
				frag.add(PushD, RunTime.CHAR_PRINT_FORMAT);
				frag.add(Printf);
				frag.add(PushI, 32);
				frag.add(PushD, RunTime.CHAR_PRINT_FORMAT);
				frag.add(Printf);
				
				frag.add(Jump, MEM_ARRAY_INT_LOOP);
				
				
			frag.add(Label, MEM_ARRAY_INT_ARRAY);
				loadIFrom(frag, MEM_ARRAY_INT_LENGTH);
			
				frag.add(JumpFalse, MEM_ARRAY_INT_PRINT_END);
				
				loadIFrom(frag, MEM_ARRAY_INT_RETURN_ADDRESS);
				loadIFrom(frag, MEM_ARRAY_INT_ARRAY_ADDRESS);
				loadIFrom(frag, MEM_ARRAY_INT_END_ARRAY_ADDRESS);
				loadIFrom(frag, MEM_ARRAY_INT_LENGTH);
				loadIFrom(frag, MEM_ARRAY_INT_SIZE);
				
				loadIFrom(frag, MEM_ARRAY_INT_END_ARRAY_ADDRESS);
				loadIFrom(frag, MEM_ARRAY_INT_LENGTH);
				loadIFrom(frag, MEM_ARRAY_INT_SIZE);
				frag.add(Multiply);
				frag.add(Subtract);
				frag.add(LoadI);
				frag.add(Duplicate);
				frag.add(JumpFalse, RunTime.RECORD_ARRAY_ERROR);
				
				frag.add(Call, MEM_ARRAY_INT_PRINT);
				
				storeITo(frag, MEM_ARRAY_INT_SIZE);
				storeITo(frag, MEM_ARRAY_INT_LENGTH);
				storeITo(frag, MEM_ARRAY_INT_END_ARRAY_ADDRESS);
				storeITo(frag, MEM_ARRAY_INT_ARRAY_ADDRESS);
				storeITo(frag, MEM_ARRAY_INT_RETURN_ADDRESS);
				
				loadIFrom(frag, MEM_ARRAY_INT_LENGTH);
				frag.add(PushI, 1);
				frag.add(Subtract);
				storeITo(frag, MEM_ARRAY_INT_LENGTH);
				loadIFrom(frag, MEM_ARRAY_INT_LENGTH);
				frag.add(JumpFalse, MEM_ARRAY_INT_ARRAY);
				
				frag.add(PushI, 44);
				frag.add(PushD, RunTime.CHAR_PRINT_FORMAT);
				frag.add(Printf);
				frag.add(PushI, 32);
				frag.add(PushD, RunTime.CHAR_PRINT_FORMAT);
				frag.add(Printf);
				
				frag.add(Jump, MEM_ARRAY_INT_ARRAY);
				
				
				
			frag.add(Label, MEM_ARRAY_INT_PRINT_END);
				frag.add(PushI, 93);
				frag.add(PushD, RunTime.CHAR_PRINT_FORMAT);
				frag.add(Printf);
				loadIFrom(frag, MEM_ARRAY_INT_RETURN_ADDRESS);
				
				frag.add(Return);
			
			return frag;	
	}
	
	private static ASMCodeFragment subroutineArrayFloatPrint() {
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_VOID);
		
		frag.add(Label, MEM_ARRAY_FLOAT_PRINT);
			declareI(frag, MEM_ARRAY_FLOAT_RETURN_ADDRESS);
			declareI(frag, MEM_ARRAY_FLOAT_ARRAY_ADDRESS);
			declareI(frag, MEM_ARRAY_FLOAT_END_ARRAY_ADDRESS);
			declareI(frag, MEM_ARRAY_FLOAT_LENGTH);
			declareI(frag, MEM_ARRAY_FLOAT_SIZE);
			
			storeITo(frag, MEM_ARRAY_FLOAT_RETURN_ADDRESS);
			storeITo(frag, MEM_ARRAY_FLOAT_ARRAY_ADDRESS);
			
			loadIFrom(frag, MEM_ARRAY_FLOAT_ARRAY_ADDRESS);
			//[...arrayAddress]
			frag.add(Call, MEM_ARRAY_RECORD_VALID);
			//[...]
			
			
			loadIFrom(frag, MEM_ARRAY_FLOAT_ARRAY_ADDRESS);
			frag.add(PushI, MEM_ARRAY_STATUS_OFFSET);
			frag.add(Add);
			frag.add(LoadI);
			
			frag.add(PushI, 91);
			frag.add(PushD, RunTime.CHAR_PRINT_FORMAT);
			frag.add(Printf);
			
			loadIFrom(frag, MEM_ARRAY_FLOAT_ARRAY_ADDRESS);
			frag.add(PushI, MEM_ARRAY_LENGTH_OFFSET);
			frag.add(Add);
			frag.add(LoadI);
			storeITo(frag, MEM_ARRAY_FLOAT_LENGTH);
			
			loadIFrom(frag, MEM_ARRAY_FLOAT_ARRAY_ADDRESS);
			frag.add(PushI, MEM_ARRAY_SIZE_OFFSET);
			frag.add(Add);
			frag.add(LoadI);
			storeITo(frag, MEM_ARRAY_FLOAT_SIZE);
			
			loadIFrom(frag, MEM_ARRAY_FLOAT_ARRAY_ADDRESS);
			frag.add(PushI, MEM_ARRAY_HEADER);
			loadIFrom(frag, MEM_ARRAY_FLOAT_LENGTH);
			loadIFrom(frag, MEM_ARRAY_FLOAT_SIZE);
			frag.add(Multiply);
			frag.add(Add);
			frag.add(Add);
			storeITo(frag, MEM_ARRAY_FLOAT_END_ARRAY_ADDRESS);
			
			frag.add(JumpPos, MEM_ARRAY_FLOAT_ARRAY);
			
			frag.add(Label, MEM_ARRAY_FLOAT_LOOP);
				loadIFrom(frag, MEM_ARRAY_FLOAT_LENGTH);
				
				frag.add(JumpFalse, MEM_ARRAY_FLOAT_PRINT_END);
				loadIFrom(frag, MEM_ARRAY_FLOAT_END_ARRAY_ADDRESS);
				loadIFrom(frag, MEM_ARRAY_FLOAT_LENGTH);
				loadIFrom(frag, MEM_ARRAY_FLOAT_SIZE);
				frag.add(Multiply);
				frag.add(Subtract);
				frag.add(LoadF);
				frag.add(PushD, RunTime.FLOAT_PRINT_FORMAT);
				frag.add(Printf);
				
				loadIFrom(frag, MEM_ARRAY_FLOAT_LENGTH);
				frag.add(PushI, 1);
				frag.add(Subtract);
				storeITo(frag, MEM_ARRAY_FLOAT_LENGTH);
				loadIFrom(frag, MEM_ARRAY_FLOAT_LENGTH);
				frag.add(JumpFalse, MEM_ARRAY_FLOAT_LOOP);
				
				frag.add(PushI, 44);
				frag.add(PushD, RunTime.CHAR_PRINT_FORMAT);
				frag.add(Printf);
				frag.add(PushI, 32);
				frag.add(PushD, RunTime.CHAR_PRINT_FORMAT);
				frag.add(Printf);
				
				frag.add(Jump, MEM_ARRAY_FLOAT_LOOP);
				
				
			frag.add(Label, MEM_ARRAY_FLOAT_ARRAY);
				loadIFrom(frag, MEM_ARRAY_FLOAT_LENGTH);
			
				frag.add(JumpFalse, MEM_ARRAY_FLOAT_PRINT_END);
				
				loadIFrom(frag, MEM_ARRAY_FLOAT_RETURN_ADDRESS);
				loadIFrom(frag, MEM_ARRAY_FLOAT_ARRAY_ADDRESS);
				loadIFrom(frag, MEM_ARRAY_FLOAT_END_ARRAY_ADDRESS);
				loadIFrom(frag, MEM_ARRAY_FLOAT_LENGTH);
				loadIFrom(frag, MEM_ARRAY_FLOAT_SIZE);
				
				loadIFrom(frag, MEM_ARRAY_FLOAT_END_ARRAY_ADDRESS);
				loadIFrom(frag, MEM_ARRAY_FLOAT_LENGTH);
				loadIFrom(frag, MEM_ARRAY_FLOAT_SIZE);
				frag.add(Multiply);
				frag.add(Subtract);
				frag.add(LoadI);
				frag.add(Duplicate);
				frag.add(JumpFalse, RunTime.RECORD_ARRAY_ERROR);
				
				frag.add(Call, MEM_ARRAY_FLOAT_PRINT);
				
				storeITo(frag, MEM_ARRAY_FLOAT_SIZE);
				storeITo(frag, MEM_ARRAY_FLOAT_LENGTH);
				storeITo(frag, MEM_ARRAY_FLOAT_END_ARRAY_ADDRESS);
				storeITo(frag, MEM_ARRAY_FLOAT_ARRAY_ADDRESS);
				storeITo(frag, MEM_ARRAY_FLOAT_RETURN_ADDRESS);
				
				loadIFrom(frag, MEM_ARRAY_FLOAT_LENGTH);
				frag.add(PushI, 1);
				frag.add(Subtract);
				storeITo(frag, MEM_ARRAY_FLOAT_LENGTH);
				loadIFrom(frag, MEM_ARRAY_FLOAT_LENGTH);
				frag.add(JumpFalse, MEM_ARRAY_FLOAT_ARRAY);
				
				frag.add(PushI, 44);
				frag.add(PushD, RunTime.CHAR_PRINT_FORMAT);
				frag.add(Printf);
				frag.add(PushI, 32);
				frag.add(PushD, RunTime.CHAR_PRINT_FORMAT);
				frag.add(Printf);
				
				frag.add(Jump, MEM_ARRAY_FLOAT_ARRAY);
				
				
				
			frag.add(Label, MEM_ARRAY_FLOAT_PRINT_END);
				frag.add(PushI, 93);
				frag.add(PushD, RunTime.CHAR_PRINT_FORMAT);
				frag.add(Printf);
				loadIFrom(frag, MEM_ARRAY_FLOAT_RETURN_ADDRESS);
				
				frag.add(Return);
			
			return frag;
	}
	
	private static ASMCodeFragment subroutineArrayCharPrint() {
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_VOID);
		
		frag.add(Label, MEM_ARRAY_CHAR_PRINT);
			declareI(frag, MEM_ARRAY_CHAR_RETURN_ADDRESS);
			declareI(frag, MEM_ARRAY_CHAR_ARRAY_ADDRESS);
			declareI(frag, MEM_ARRAY_CHAR_END_ARRAY_ADDRESS);
			declareI(frag, MEM_ARRAY_CHAR_LENGTH);
			declareI(frag, MEM_ARRAY_CHAR_SIZE);
			
			storeITo(frag, MEM_ARRAY_CHAR_RETURN_ADDRESS);
			storeITo(frag, MEM_ARRAY_CHAR_ARRAY_ADDRESS);
			
			loadIFrom(frag, MEM_ARRAY_CHAR_ARRAY_ADDRESS);
			//[...arrayAddress]
			frag.add(Call, MEM_ARRAY_RECORD_VALID);
			//[...]
			
			
			loadIFrom(frag, MEM_ARRAY_CHAR_ARRAY_ADDRESS);
			frag.add(PushI, MEM_ARRAY_STATUS_OFFSET);
			frag.add(Add);
			frag.add(LoadI);
			
			frag.add(PushI, 91);
			frag.add(PushD, RunTime.CHAR_PRINT_FORMAT);
			frag.add(Printf);
			
			loadIFrom(frag, MEM_ARRAY_CHAR_ARRAY_ADDRESS);
			frag.add(PushI, MEM_ARRAY_LENGTH_OFFSET);
			frag.add(Add);
			frag.add(LoadI);
			storeITo(frag, MEM_ARRAY_CHAR_LENGTH);
			
			loadIFrom(frag, MEM_ARRAY_CHAR_ARRAY_ADDRESS);
			frag.add(PushI, MEM_ARRAY_SIZE_OFFSET);
			frag.add(Add);
			frag.add(LoadI);
			storeITo(frag, MEM_ARRAY_CHAR_SIZE);
			
			loadIFrom(frag, MEM_ARRAY_CHAR_ARRAY_ADDRESS);
			frag.add(PushI, MEM_ARRAY_HEADER);
			loadIFrom(frag, MEM_ARRAY_CHAR_LENGTH);
			loadIFrom(frag, MEM_ARRAY_CHAR_SIZE);
			frag.add(Multiply);
			frag.add(Add);
			frag.add(Add);
			storeITo(frag, MEM_ARRAY_CHAR_END_ARRAY_ADDRESS);
			
			frag.add(JumpPos, MEM_ARRAY_CHAR_ARRAY);
			
			frag.add(Label, MEM_ARRAY_CHAR_LOOP);
				loadIFrom(frag, MEM_ARRAY_CHAR_LENGTH);
				
				frag.add(JumpFalse, MEM_ARRAY_CHAR_PRINT_END);
				loadIFrom(frag, MEM_ARRAY_CHAR_END_ARRAY_ADDRESS);
				loadIFrom(frag, MEM_ARRAY_CHAR_LENGTH);
				loadIFrom(frag, MEM_ARRAY_CHAR_SIZE);
				frag.add(Multiply);
				frag.add(Subtract);
				frag.add(LoadC);
				frag.add(PushD, RunTime.CHAR_PRINT_FORMAT);
				frag.add(Printf);
				
				loadIFrom(frag, MEM_ARRAY_CHAR_LENGTH);
				frag.add(PushI, 1);
				frag.add(Subtract);
				storeITo(frag, MEM_ARRAY_CHAR_LENGTH);
				loadIFrom(frag, MEM_ARRAY_CHAR_LENGTH);
				frag.add(JumpFalse, MEM_ARRAY_CHAR_LOOP);
				
				frag.add(PushI, 44);
				frag.add(PushD, RunTime.CHAR_PRINT_FORMAT);
				frag.add(Printf);
				frag.add(PushI, 32);
				frag.add(PushD, RunTime.CHAR_PRINT_FORMAT);
				frag.add(Printf);
				
				frag.add(Jump, MEM_ARRAY_CHAR_LOOP);
				
				
			frag.add(Label, MEM_ARRAY_CHAR_ARRAY);
				loadIFrom(frag, MEM_ARRAY_CHAR_LENGTH);
			
				frag.add(JumpFalse, MEM_ARRAY_CHAR_PRINT_END);
				
				loadIFrom(frag, MEM_ARRAY_CHAR_RETURN_ADDRESS);
				loadIFrom(frag, MEM_ARRAY_CHAR_ARRAY_ADDRESS);
				loadIFrom(frag, MEM_ARRAY_CHAR_END_ARRAY_ADDRESS);
				loadIFrom(frag, MEM_ARRAY_CHAR_LENGTH);
				loadIFrom(frag, MEM_ARRAY_CHAR_SIZE);
				
				loadIFrom(frag, MEM_ARRAY_CHAR_END_ARRAY_ADDRESS);
				loadIFrom(frag, MEM_ARRAY_CHAR_LENGTH);
				loadIFrom(frag, MEM_ARRAY_CHAR_SIZE);
				frag.add(Multiply);
				frag.add(Subtract);
				frag.add(LoadI);
				frag.add(Duplicate);
				frag.add(JumpFalse, RunTime.RECORD_ARRAY_ERROR);
				
				frag.add(Call, MEM_ARRAY_CHAR_PRINT);
				
				storeITo(frag, MEM_ARRAY_CHAR_SIZE);
				storeITo(frag, MEM_ARRAY_CHAR_LENGTH);
				storeITo(frag, MEM_ARRAY_CHAR_END_ARRAY_ADDRESS);
				storeITo(frag, MEM_ARRAY_CHAR_ARRAY_ADDRESS);
				storeITo(frag, MEM_ARRAY_CHAR_RETURN_ADDRESS);
				
				loadIFrom(frag, MEM_ARRAY_CHAR_LENGTH);
				frag.add(PushI, 1);
				frag.add(Subtract);
				storeITo(frag, MEM_ARRAY_CHAR_LENGTH);
				loadIFrom(frag, MEM_ARRAY_CHAR_LENGTH);
				frag.add(JumpFalse, MEM_ARRAY_CHAR_ARRAY);
				
				frag.add(PushI, 44);
				frag.add(PushD, RunTime.CHAR_PRINT_FORMAT);
				frag.add(Printf);
				frag.add(PushI, 32);
				frag.add(PushD, RunTime.CHAR_PRINT_FORMAT);
				frag.add(Printf);
				
				frag.add(Jump, MEM_ARRAY_CHAR_ARRAY);
				
				
				
			frag.add(Label, MEM_ARRAY_CHAR_PRINT_END);
				frag.add(PushI, 93);
				frag.add(PushD, RunTime.CHAR_PRINT_FORMAT);
				frag.add(Printf);
				loadIFrom(frag, MEM_ARRAY_CHAR_RETURN_ADDRESS);
				
				frag.add(Return);
			
			return frag;
	}
	
	private static ASMCodeFragment subroutineArrayBoolPrint() {
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_VOID);
		
		frag.add(Label, MEM_ARRAY_BOOL_PRINT);
			declareI(frag, MEM_ARRAY_BOOL_RETURN_ADDRESS);
			declareI(frag, MEM_ARRAY_BOOL_ARRAY_ADDRESS);
			declareI(frag, MEM_ARRAY_BOOL_END_ARRAY_ADDRESS);
			declareI(frag, MEM_ARRAY_BOOL_LENGTH);
			declareI(frag, MEM_ARRAY_BOOL_SIZE);
			
			storeITo(frag, MEM_ARRAY_BOOL_RETURN_ADDRESS);
			storeITo(frag, MEM_ARRAY_BOOL_ARRAY_ADDRESS);
			
			loadIFrom(frag, MEM_ARRAY_BOOL_ARRAY_ADDRESS);
			//[...arrayAddress]
			frag.add(Call, MEM_ARRAY_RECORD_VALID);
			//[...]
			
			
			loadIFrom(frag, MEM_ARRAY_BOOL_ARRAY_ADDRESS);
			frag.add(PushI, MEM_ARRAY_STATUS_OFFSET);
			frag.add(Add);
			frag.add(LoadI);
			
			frag.add(PushI, 91);
			frag.add(PushD, RunTime.CHAR_PRINT_FORMAT);
			frag.add(Printf);
			
			loadIFrom(frag, MEM_ARRAY_BOOL_ARRAY_ADDRESS);
			frag.add(PushI, MEM_ARRAY_LENGTH_OFFSET);
			frag.add(Add);
			frag.add(LoadI);
			storeITo(frag, MEM_ARRAY_BOOL_LENGTH);
			
			loadIFrom(frag, MEM_ARRAY_BOOL_ARRAY_ADDRESS);
			frag.add(PushI, MEM_ARRAY_SIZE_OFFSET);
			frag.add(Add);
			frag.add(LoadI);
			storeITo(frag, MEM_ARRAY_BOOL_SIZE);
			
			loadIFrom(frag, MEM_ARRAY_BOOL_ARRAY_ADDRESS);
			frag.add(PushI, MEM_ARRAY_HEADER);
			loadIFrom(frag, MEM_ARRAY_BOOL_LENGTH);
			loadIFrom(frag, MEM_ARRAY_BOOL_SIZE);
			frag.add(Multiply);
			frag.add(Add);
			frag.add(Add);
			storeITo(frag, MEM_ARRAY_BOOL_END_ARRAY_ADDRESS);
			
			frag.add(JumpPos, MEM_ARRAY_BOOL_ARRAY);
			
			frag.add(Label, MEM_ARRAY_BOOL_LOOP);
				loadIFrom(frag, MEM_ARRAY_BOOL_LENGTH);
				
				frag.add(JumpFalse, MEM_ARRAY_BOOL_PRINT_END);
				loadIFrom(frag, MEM_ARRAY_BOOL_END_ARRAY_ADDRESS);
				loadIFrom(frag, MEM_ARRAY_BOOL_LENGTH);
				loadIFrom(frag, MEM_ARRAY_BOOL_SIZE);
				frag.add(Multiply);
				frag.add(Subtract);
				frag.add(LoadC);
				frag.add(JumpTrue, MEM_ARRAY_BOOL_TRUE);
				frag.add(PushD, RunTime.BOOLEAN_FALSE_STRING);
				frag.add(Jump, MEM_ARRAY_BOOL_END);
				frag.add(Label, MEM_ARRAY_BOOL_TRUE);
				frag.add(PushD, RunTime.BOOLEAN_TRUE_STRING);
				frag.add(Label, MEM_ARRAY_BOOL_END);
				frag.add(PushD, RunTime.STRING_PRINT_FORMAT);
				frag.add(Printf);
				
				
				loadIFrom(frag, MEM_ARRAY_BOOL_LENGTH);
				frag.add(PushI, 1);
				frag.add(Subtract);
				storeITo(frag, MEM_ARRAY_BOOL_LENGTH);
				loadIFrom(frag, MEM_ARRAY_BOOL_LENGTH);
				frag.add(JumpFalse, MEM_ARRAY_BOOL_LOOP);
				
				frag.add(PushI, 44);
				frag.add(PushD, RunTime.CHAR_PRINT_FORMAT);
				frag.add(Printf);
				frag.add(PushI, 32);
				frag.add(PushD, RunTime.CHAR_PRINT_FORMAT);
				frag.add(Printf);
				
				frag.add(Jump, MEM_ARRAY_BOOL_LOOP);
				
				
			frag.add(Label, MEM_ARRAY_BOOL_ARRAY);
				loadIFrom(frag, MEM_ARRAY_BOOL_LENGTH);
			
				frag.add(JumpFalse, MEM_ARRAY_BOOL_PRINT_END);
				
				loadIFrom(frag, MEM_ARRAY_BOOL_RETURN_ADDRESS);
				loadIFrom(frag, MEM_ARRAY_BOOL_ARRAY_ADDRESS);
				loadIFrom(frag, MEM_ARRAY_BOOL_END_ARRAY_ADDRESS);
				loadIFrom(frag, MEM_ARRAY_BOOL_LENGTH);
				loadIFrom(frag, MEM_ARRAY_BOOL_SIZE);
				
				loadIFrom(frag, MEM_ARRAY_BOOL_END_ARRAY_ADDRESS);
				loadIFrom(frag, MEM_ARRAY_BOOL_LENGTH);
				loadIFrom(frag, MEM_ARRAY_BOOL_SIZE);
				frag.add(Multiply);
				frag.add(Subtract);
				frag.add(LoadI);
				frag.add(Duplicate);
				frag.add(JumpFalse, RunTime.RECORD_ARRAY_ERROR);
				
				frag.add(Call, MEM_ARRAY_BOOL_PRINT);
				
				storeITo(frag, MEM_ARRAY_BOOL_SIZE);
				storeITo(frag, MEM_ARRAY_BOOL_LENGTH);
				storeITo(frag, MEM_ARRAY_BOOL_END_ARRAY_ADDRESS);
				storeITo(frag, MEM_ARRAY_BOOL_ARRAY_ADDRESS);
				storeITo(frag, MEM_ARRAY_BOOL_RETURN_ADDRESS);
				
				loadIFrom(frag, MEM_ARRAY_BOOL_LENGTH);
				frag.add(PushI, 1);
				frag.add(Subtract);
				storeITo(frag, MEM_ARRAY_BOOL_LENGTH);
				loadIFrom(frag, MEM_ARRAY_BOOL_LENGTH);
				frag.add(JumpFalse, MEM_ARRAY_BOOL_ARRAY);
				
				frag.add(PushI, 44);
				frag.add(PushD, RunTime.CHAR_PRINT_FORMAT);
				frag.add(Printf);
				frag.add(PushI, 32);
				frag.add(PushD, RunTime.CHAR_PRINT_FORMAT);
				frag.add(Printf);
				
				frag.add(Jump, MEM_ARRAY_BOOL_ARRAY);
				
				
				
			frag.add(Label, MEM_ARRAY_BOOL_PRINT_END);
				frag.add(PushI, 93);
				frag.add(PushD, RunTime.CHAR_PRINT_FORMAT);
				frag.add(Printf);
				loadIFrom(frag, MEM_ARRAY_BOOL_RETURN_ADDRESS);
				
				frag.add(Return);
			
			return frag;
	}
	
	private static ASMCodeFragment subroutineArrayStringPrint() {
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_VOID);
		
		frag.add(Label, MEM_ARRAY_STRING_PRINT);
			declareI(frag, MEM_ARRAY_STRING_RETURN_ADDRESS);
			declareI(frag, MEM_ARRAY_STRING_ARRAY_ADDRESS);
			declareI(frag, MEM_ARRAY_STRING_END_ARRAY_ADDRESS);
			declareI(frag, MEM_ARRAY_STRING_LENGTH);
			declareI(frag, MEM_ARRAY_STRING_SIZE);
			
			storeITo(frag, MEM_ARRAY_STRING_RETURN_ADDRESS);
			storeITo(frag, MEM_ARRAY_STRING_ARRAY_ADDRESS);
			
			loadIFrom(frag, MEM_ARRAY_STRING_ARRAY_ADDRESS);
			//[...arrayAddress]
			frag.add(Call, MEM_ARRAY_RECORD_VALID);
			//[...]
			
			
			loadIFrom(frag, MEM_ARRAY_STRING_ARRAY_ADDRESS);
			frag.add(PushI, MEM_ARRAY_STATUS_OFFSET);
			frag.add(Add);
			frag.add(LoadI);
			
			frag.add(PushI, 91);
			frag.add(PushD, RunTime.CHAR_PRINT_FORMAT);
			frag.add(Printf);
			
			loadIFrom(frag, MEM_ARRAY_STRING_ARRAY_ADDRESS);
			frag.add(PushI, MEM_ARRAY_LENGTH_OFFSET);
			frag.add(Add);
			frag.add(LoadI);
			storeITo(frag, MEM_ARRAY_STRING_LENGTH);
			
			loadIFrom(frag, MEM_ARRAY_STRING_ARRAY_ADDRESS);
			frag.add(PushI, MEM_ARRAY_SIZE_OFFSET);
			frag.add(Add);
			frag.add(LoadI);
			storeITo(frag, MEM_ARRAY_STRING_SIZE);
			
			loadIFrom(frag, MEM_ARRAY_STRING_ARRAY_ADDRESS);
			frag.add(PushI, MEM_ARRAY_HEADER);
			loadIFrom(frag, MEM_ARRAY_STRING_LENGTH);
			loadIFrom(frag, MEM_ARRAY_STRING_SIZE);
			frag.add(Multiply);
			frag.add(Add);
			frag.add(Add);
			storeITo(frag, MEM_ARRAY_STRING_END_ARRAY_ADDRESS);
			
			frag.add(JumpPos, MEM_ARRAY_STRING_ARRAY);
			
			frag.add(Label, MEM_ARRAY_STRING_LOOP);
				loadIFrom(frag, MEM_ARRAY_STRING_LENGTH);
				
				frag.add(JumpFalse, MEM_ARRAY_STRING_PRINT_END);
				loadIFrom(frag, MEM_ARRAY_STRING_END_ARRAY_ADDRESS);
				loadIFrom(frag, MEM_ARRAY_STRING_LENGTH);
				loadIFrom(frag, MEM_ARRAY_STRING_SIZE);
				frag.add(Multiply);
				frag.add(Subtract);
				frag.add(LoadI);
				frag.add(Duplicate);
				frag.add(JumpFalse, RunTime.RECORD_STRING_ERROR);
				frag.add(PushI, MEM_STRING_CONTENT_OFFSET);
				frag.add(Add);
				frag.add(PushD, RunTime.STRING_PRINT_FORMAT);
				frag.add(Printf);
				
				loadIFrom(frag, MEM_ARRAY_STRING_LENGTH);
				frag.add(PushI, 1);
				frag.add(Subtract);
				storeITo(frag, MEM_ARRAY_STRING_LENGTH);
				loadIFrom(frag, MEM_ARRAY_STRING_LENGTH);
				frag.add(JumpFalse, MEM_ARRAY_STRING_LOOP);
				
				frag.add(PushI, 44);
				frag.add(PushD, RunTime.CHAR_PRINT_FORMAT);
				frag.add(Printf);
				frag.add(PushI, 32);
				frag.add(PushD, RunTime.CHAR_PRINT_FORMAT);
				frag.add(Printf);
				
				frag.add(Jump, MEM_ARRAY_STRING_LOOP);
				
				
			frag.add(Label, MEM_ARRAY_STRING_ARRAY);
				loadIFrom(frag, MEM_ARRAY_STRING_LENGTH);
			
				frag.add(JumpFalse, MEM_ARRAY_STRING_PRINT_END);
				
				loadIFrom(frag, MEM_ARRAY_STRING_RETURN_ADDRESS);
				loadIFrom(frag, MEM_ARRAY_STRING_ARRAY_ADDRESS);
				loadIFrom(frag, MEM_ARRAY_STRING_END_ARRAY_ADDRESS);
				loadIFrom(frag, MEM_ARRAY_STRING_LENGTH);
				loadIFrom(frag, MEM_ARRAY_STRING_SIZE);
				
				loadIFrom(frag, MEM_ARRAY_STRING_END_ARRAY_ADDRESS);
				loadIFrom(frag, MEM_ARRAY_STRING_LENGTH);
				loadIFrom(frag, MEM_ARRAY_STRING_SIZE);
				frag.add(Multiply);
				frag.add(Subtract);
				frag.add(LoadI);
				frag.add(Duplicate);
				frag.add(JumpFalse, RunTime.RECORD_ARRAY_ERROR);
				
				frag.add(Call, MEM_ARRAY_STRING_PRINT);
				
				storeITo(frag, MEM_ARRAY_STRING_SIZE);
				storeITo(frag, MEM_ARRAY_STRING_LENGTH);
				storeITo(frag, MEM_ARRAY_STRING_END_ARRAY_ADDRESS);
				storeITo(frag, MEM_ARRAY_STRING_ARRAY_ADDRESS);
				storeITo(frag, MEM_ARRAY_STRING_RETURN_ADDRESS);
				
				loadIFrom(frag, MEM_ARRAY_STRING_LENGTH);
				frag.add(PushI, 1);
				frag.add(Subtract);
				storeITo(frag, MEM_ARRAY_STRING_LENGTH);
				loadIFrom(frag, MEM_ARRAY_STRING_LENGTH);
				frag.add(JumpFalse, MEM_ARRAY_STRING_ARRAY);
				
				frag.add(PushI, 44);
				frag.add(PushD, RunTime.CHAR_PRINT_FORMAT);
				frag.add(Printf);
				frag.add(PushI, 32);
				frag.add(PushD, RunTime.CHAR_PRINT_FORMAT);
				frag.add(Printf);
				
				frag.add(Jump, MEM_ARRAY_STRING_ARRAY);
				
				
				
			frag.add(Label, MEM_ARRAY_STRING_PRINT_END);
				frag.add(PushI, 93);
				frag.add(PushD, RunTime.CHAR_PRINT_FORMAT);
				frag.add(Printf);
				loadIFrom(frag, MEM_ARRAY_STRING_RETURN_ADDRESS);
				
				frag.add(Return);
			
			return frag;
	}
	
	private static ASMCodeFragment subroutineArrayRationalPrint() {
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_VOID);
		
		frag.add(Label, MEM_ARRAY_RATIONAL_PRINT);
			declareI(frag, MEM_ARRAY_RATIONAL_RETURN_ADDRESS);
			declareI(frag, MEM_ARRAY_RATIONAL_ARRAY_ADDRESS);
			declareI(frag, MEM_ARRAY_RATIONAL_END_ARRAY_ADDRESS);
			declareI(frag, MEM_ARRAY_RATIONAL_LENGTH);
			declareI(frag, MEM_ARRAY_RATIONAL_SIZE);
			
			storeITo(frag, MEM_ARRAY_RATIONAL_RETURN_ADDRESS);
			storeITo(frag, MEM_ARRAY_RATIONAL_ARRAY_ADDRESS);
			
			loadIFrom(frag, MEM_ARRAY_RATIONAL_ARRAY_ADDRESS);
			//[...arrayAddress]
			frag.add(Call, MEM_ARRAY_RECORD_VALID);
			//[...]
			
			
			loadIFrom(frag, MEM_ARRAY_RATIONAL_ARRAY_ADDRESS);
			frag.add(PushI, MEM_ARRAY_STATUS_OFFSET);
			frag.add(Add);
			frag.add(LoadI);
			
			frag.add(PushI, 91);
			frag.add(PushD, RunTime.CHAR_PRINT_FORMAT);
			frag.add(Printf);
			
			loadIFrom(frag, MEM_ARRAY_RATIONAL_ARRAY_ADDRESS);
			frag.add(PushI, MEM_ARRAY_LENGTH_OFFSET);
			frag.add(Add);
			frag.add(LoadI);
			storeITo(frag, MEM_ARRAY_RATIONAL_LENGTH);
			
			loadIFrom(frag, MEM_ARRAY_RATIONAL_ARRAY_ADDRESS);
			frag.add(PushI, MEM_ARRAY_SIZE_OFFSET);
			frag.add(Add);
			frag.add(LoadI);
			storeITo(frag, MEM_ARRAY_RATIONAL_SIZE);
			
			loadIFrom(frag, MEM_ARRAY_RATIONAL_ARRAY_ADDRESS);
			frag.add(PushI, MEM_ARRAY_HEADER);
			loadIFrom(frag, MEM_ARRAY_RATIONAL_LENGTH);
			loadIFrom(frag, MEM_ARRAY_RATIONAL_SIZE);
			frag.add(Multiply);
			frag.add(Add);
			frag.add(Add);
			storeITo(frag, MEM_ARRAY_RATIONAL_END_ARRAY_ADDRESS);
			
			frag.add(JumpPos, MEM_ARRAY_RATIONAL_ARRAY);
			
			frag.add(Label, MEM_ARRAY_RATIONAL_LOOP);
				loadIFrom(frag, MEM_ARRAY_RATIONAL_LENGTH);
				
				frag.add(JumpFalse, MEM_ARRAY_RATIONAL_PRINT_END);
				loadIFrom(frag, MEM_ARRAY_RATIONAL_END_ARRAY_ADDRESS);
				loadIFrom(frag, MEM_ARRAY_RATIONAL_LENGTH);
				loadIFrom(frag, MEM_ARRAY_RATIONAL_SIZE);
				frag.add(Multiply);
				frag.add(Subtract);
				frag.add(Duplicate);
				frag.add(LoadI);
				frag.add(Exchange);
				frag.add(PushI,4);
				frag.add(Add);
				frag.add(LoadI);
				frag.add(Duplicate);
				frag.add(JumpFalse, RunTime.RAT_WITH_ZERO_DENOMINATOR_RUNTIME_ERROR);
				frag.add(Call, MEM_RAT_PRINT);
				
				
				loadIFrom(frag, MEM_ARRAY_RATIONAL_LENGTH);
				frag.add(PushI, 1);
				frag.add(Subtract);
				storeITo(frag, MEM_ARRAY_RATIONAL_LENGTH);
				loadIFrom(frag, MEM_ARRAY_RATIONAL_LENGTH);
				frag.add(JumpFalse, MEM_ARRAY_RATIONAL_LOOP);
				
				frag.add(PushI, 44);
				frag.add(PushD, RunTime.CHAR_PRINT_FORMAT);
				frag.add(Printf);
				frag.add(PushI, 32);
				frag.add(PushD, RunTime.CHAR_PRINT_FORMAT);
				frag.add(Printf);
				
				frag.add(Jump, MEM_ARRAY_RATIONAL_LOOP);
				
				
			frag.add(Label, MEM_ARRAY_RATIONAL_ARRAY);
				loadIFrom(frag, MEM_ARRAY_RATIONAL_LENGTH);
			
				frag.add(JumpFalse, MEM_ARRAY_RATIONAL_PRINT_END);
				
				loadIFrom(frag, MEM_ARRAY_RATIONAL_RETURN_ADDRESS);
				loadIFrom(frag, MEM_ARRAY_RATIONAL_ARRAY_ADDRESS);
				loadIFrom(frag, MEM_ARRAY_RATIONAL_END_ARRAY_ADDRESS);
				loadIFrom(frag, MEM_ARRAY_RATIONAL_LENGTH);
				loadIFrom(frag, MEM_ARRAY_RATIONAL_SIZE);
				
				loadIFrom(frag, MEM_ARRAY_RATIONAL_END_ARRAY_ADDRESS);
				loadIFrom(frag, MEM_ARRAY_RATIONAL_LENGTH);
				loadIFrom(frag, MEM_ARRAY_RATIONAL_SIZE);
				frag.add(Multiply);
				frag.add(Subtract);
				frag.add(LoadI);
				frag.add(Duplicate);
				frag.add(JumpFalse, RunTime.RECORD_ARRAY_ERROR);
				
				frag.add(Call, MEM_ARRAY_RATIONAL_PRINT);
				
				storeITo(frag, MEM_ARRAY_RATIONAL_SIZE);
				storeITo(frag, MEM_ARRAY_RATIONAL_LENGTH);
				storeITo(frag, MEM_ARRAY_RATIONAL_END_ARRAY_ADDRESS);
				storeITo(frag, MEM_ARRAY_RATIONAL_ARRAY_ADDRESS);
				storeITo(frag, MEM_ARRAY_RATIONAL_RETURN_ADDRESS);
				
				loadIFrom(frag, MEM_ARRAY_RATIONAL_LENGTH);
				frag.add(PushI, 1);
				frag.add(Subtract);
				storeITo(frag, MEM_ARRAY_RATIONAL_LENGTH);
				loadIFrom(frag, MEM_ARRAY_RATIONAL_LENGTH);
				frag.add(JumpFalse, MEM_ARRAY_RATIONAL_ARRAY);
				
				frag.add(PushI, 44);
				frag.add(PushD, RunTime.CHAR_PRINT_FORMAT);
				frag.add(Printf);
				frag.add(PushI, 32);
				frag.add(PushD, RunTime.CHAR_PRINT_FORMAT);
				frag.add(Printf);
				
				frag.add(Jump, MEM_ARRAY_RATIONAL_ARRAY);
				
				
				
			frag.add(Label, MEM_ARRAY_RATIONAL_PRINT_END);
				frag.add(PushI, 93);
				frag.add(PushD, RunTime.CHAR_PRINT_FORMAT);
				frag.add(Printf);
				loadIFrom(frag, MEM_ARRAY_RATIONAL_RETURN_ADDRESS);
				
				frag.add(Return);
			
			return frag;
	}
	
	private static ASMCodeFragment subroutineArrayRecordTest() {
	
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_VOID);
		
		frag.add(Label, MEM_ARRAY_RECORD_VALID);
			declareI(frag, MEM_ARRAY_RECORD_VALID_RETURN_ADDRESS);
			declareI(frag, MEM_ARRAY_RECORD_VALID_ARRAY_ADDRESS);
			
			storeITo(frag, MEM_ARRAY_RECORD_VALID_RETURN_ADDRESS);
			storeITo(frag, MEM_ARRAY_RECORD_VALID_ARRAY_ADDRESS);
			
			loadIFrom(frag, MEM_ARRAY_RECORD_VALID_ARRAY_ADDRESS);
			frag.add(LoadI);
			frag.add(PushI, 7);
			frag.add(Subtract);
			frag.add(JumpTrue, RunTime.RECORD_ARRAY_ERROR);
			
			loadIFrom(frag, MEM_ARRAY_RECORD_VALID_ARRAY_ADDRESS);
			frag.add(PushI, MEM_ARRAY_STATUS_OFFSET);
			frag.add(Add);
			frag.add(LoadI);
			frag.add(PushI, 2);
			frag.add(BTAnd);
			frag.add(JumpTrue, RunTime.RECORD_ARRAY_DELETED_ERROR);
		
		loadIFrom(frag,MEM_ARRAY_RECORD_VALID_RETURN_ADDRESS);
		frag.add(Return);
		return frag;
	}		
			
	
////////////////////////////////////////////////////////////////////////////////////
//Macros: these get inlined into the subroutines defined above.
////////////////////////////////////////////////////////////////////////////////////

	// [... size] -> [... block]
	// eats new heap space.  allocates a block of given size.
	// does not insert into available list or set availability.
	private static void newBlock(ASMCodeFragment frag) {
		storeITo(frag, MMGR_NEWBLOCK_SIZE);
		// block = heapEnd
		loadIFrom(frag, MEM_MANAGER_HEAP_END_PTR);
		storeITo(frag, MMGR_NEWBLOCK_BLOCK);

		// heapEnd += size
		loadIFrom(frag, MMGR_NEWBLOCK_SIZE);
		addITo(frag, MEM_MANAGER_HEAP_END_PTR);

		// make the tags for our new block.
		frag.add(PushI, 0);								// prevPtr
		frag.add(PushI, 0);								// nextPtr
		frag.add(PushI, 0);								// isAvailable
		loadIFrom(frag, MMGR_NEWBLOCK_BLOCK);			// start_addr
		loadIFrom(frag, MMGR_NEWBLOCK_SIZE);			// size of block
		frag.add(Call, MEM_MANAGER_MAKE_TAGS);			

		// return with block on the stack
		loadIFrom(frag, MMGR_NEWBLOCK_BLOCK);
	}

	// [... blockBaseLocation] -> [... blockTailTagLocation]
	private static void tailTag(ASMCodeFragment frag) {
		frag.add(Duplicate);						// [... block block]
		readTagSize(frag);							// [... block size]
		frag.add(Add);								// [... block+size]
		frag.add(PushI, MMGR_TAG_SIZE_IN_BYTES);	// [... block+size tagsize]
		frag.add(Subtract);							// [... tailTaglocation]
	}

	// [... tagBaseLocation] -> [... tagPointer]		(i.e. nextPtr or prevPtr) 
	private static void readTagPointer(ASMCodeFragment frag) {
		readIOffset(frag, TAG_POINTER_OFFSET);
	}
	// [... tagBaseLocation] -> [... blockSize] 
	private static void readTagSize(ASMCodeFragment frag) {
		readIOffset(frag, TAG_SIZE_OFFSET);
	}
	// [... tagBaseLocation] -> [... blockSize]
	private static void readTagAvailable(ASMCodeFragment frag) {
		readCOffset(frag, TAG_AVAIL_OFFSET);
	}
	// [... ptrToWrite tagBaseLocation] -> [...]
	private static void writeTagPointer(ASMCodeFragment frag) {
		writeIOffset(frag, TAG_POINTER_OFFSET);
	}
	// [... size tagBaseLocation] -> [...] 
	private static void writeTagSize(ASMCodeFragment frag) {
		writeIOffset(frag, TAG_SIZE_OFFSET);
	}
	// [... isAvailable tagBaseLocation] -> [...] 
	private static void writeTagAvailable(ASMCodeFragment frag) {
		writeCOffset(frag, TAG_AVAIL_OFFSET);
	}

	
////////////////////////////////////////////////////////////////////////////////////
//Testing/Debug code
////////////////////////////////////////////////////////////////////////////////////

	private static final String MMGRD_FORMAT = "$$debug-format";
	private static final String MMGRD_FORMAT_FOR_STRING = "$$debug-format-for-string";
	private static final String MMGRD_MAIN_BLOCK1 = "$$mmgrd-main-block1";
	private static final String MMGRD_MAIN_BLOCK2 = "$$mmgrd-main-block2";
	private static final String MMGRD_MAIN_BLOCK3 = "$$mmgrd-main-block3";
	private static final String MMGRD_MAIN_BLOCK4 = "$$mmgrd-main-block4";
	
	private static void insertDebugMain(ASMCodeFragment frag) {
		frag.add(DLabel, MMGRD_FORMAT);
		frag.add(DataS, "%s %d\n");	
		frag.add(DLabel, MMGRD_FORMAT_FOR_STRING);
		frag.add(DataS, "%s");
		declareI(frag, MMGRD_MAIN_BLOCK1);
		declareI(frag, MMGRD_MAIN_BLOCK2);
		declareI(frag, MMGRD_MAIN_BLOCK3);
		declareI(frag, MMGRD_MAIN_BLOCK4);
		
		frag.add(PushI, 30);					// request block of size 30 => 30+18=48
		debugSystemBlockAllocate(frag);
		storeITo(frag, MMGRD_MAIN_BLOCK1);
		debugPrintBlockFromPointer(frag, MMGRD_MAIN_BLOCK1);
		
		frag.add(Call, MMGRD_PRINT_FREE_LIST);
		
		loadIFrom(frag, MMGRD_MAIN_BLOCK1);			// [... block1]
		debugSystemBlockDeallocate(frag);
		debugPrint(frag, "deallocation done\n");
		
		frag.add(Call, MMGRD_PRINT_FREE_LIST);
		
		frag.add(PushI, 40);
		debugSystemBlockAllocate(frag);
		storeITo(frag, MMGRD_MAIN_BLOCK2);
		debugPrintBlockFromPointer(frag, MMGRD_MAIN_BLOCK2);
		
		loadIFrom(frag, MMGRD_MAIN_BLOCK2);			// [... block2]
		debugSystemBlockDeallocate(frag);
		debugPrint(frag, "deallocation 2 done\n");

		frag.add(Call, MMGRD_PRINT_FREE_LIST);
		
		frag.add(PushI, 150);
		debugSystemBlockAllocate(frag);
		storeITo(frag, MMGRD_MAIN_BLOCK3);
		debugPrintBlockFromPointer(frag, MMGRD_MAIN_BLOCK3);

		
		loadIFrom(frag, MMGRD_MAIN_BLOCK3);			// [... block3]
		debugSystemBlockDeallocate(frag);
		debugPrint(frag, "deallocation 3 done\n");

		frag.add(Call, MMGRD_PRINT_FREE_LIST);
		
		frag.add(PushI, 30);
		debugSystemBlockAllocate(frag);
		storeITo(frag, MMGRD_MAIN_BLOCK4);
		debugPrintBlockFromPointer(frag, MMGRD_MAIN_BLOCK4);
		
		frag.add(Call, MMGRD_PRINT_FREE_LIST);
		
		loadIFrom(frag, MMGRD_MAIN_BLOCK4);			// [... block4]
		debugSystemBlockDeallocate(frag);
		debugPrint(frag, "deallocation 4 done\n");

		frag.add(Call, MMGRD_PRINT_FREE_LIST);
		
		// reusing block1
		frag.add(PushI, 25);
		debugSystemBlockAllocate(frag);
		storeITo(frag, MMGRD_MAIN_BLOCK1);
		debugPrintBlockFromPointer(frag, MMGRD_MAIN_BLOCK1);
		
		frag.add(Call, MMGRD_PRINT_FREE_LIST);
		
		loadIFrom(frag, MMGRD_MAIN_BLOCK1);			// [... block4]
		debugSystemBlockDeallocate(frag);
		debugPrint(frag, "deallocation 5 done\n");

		frag.add(Call, MMGRD_PRINT_FREE_LIST);
		
		
		// reusing block1
		frag.add(PushI, 40);
		debugSystemBlockAllocate(frag);
		storeITo(frag, MMGRD_MAIN_BLOCK1);
		debugPrintBlockFromPointer(frag, MMGRD_MAIN_BLOCK1);
		
		frag.add(Call, MMGRD_PRINT_FREE_LIST);
		
		loadIFrom(frag, MMGRD_MAIN_BLOCK1);			// [... block4]
		debugSystemBlockDeallocate(frag);
		debugPrint(frag, "deallocation 6 done\n");

		frag.add(Call, MMGRD_PRINT_FREE_LIST);
	}
	private static void debugPrintBlockFromPointer(ASMCodeFragment frag, String pointerName) {
		loadIFrom(frag, pointerName);
		frag.add(Call, MMGRD_PRINT_BLOCK);	
		debugPrint(frag, "\n");
	}

	// [... size] -> [... block]
	private static void debugSystemBlockAllocate(ASMCodeFragment frag) {
		frag.add(Call, MEM_MANAGER_ALLOCATE);	// [... userblock]
		frag.add(PushI, MMGR_TAG_SIZE_IN_BYTES);
		frag.add(Subtract);						// [... block]
	}
	private static void debugSystemBlockDeallocate(ASMCodeFragment frag) {
		frag.add(PushI, MMGR_TAG_SIZE_IN_BYTES);	// [... block1 tagsize]
		frag.add(Add);								// [... userBlock1]
		frag.add(Call, MEM_MANAGER_DEALLOCATE);		// [...]
	}
	

	// prints top of stack 
	// [... t] -> [... t]
	@SuppressWarnings("unused")
	private static void debugPeekI(ASMCodeFragment frag, String printString) {
		String label = new Labeller("$$debug-peekI").newLabel("");
		frag.add(DLabel, label);
		frag.add(DataS, printString);
		
		frag.add(Duplicate);			// [... t t]
		frag.add(PushD, label);			// [... t t printString]
		frag.add(PushD, MMGRD_FORMAT);  
		frag.add(Printf);
	}	
	private static void debugPrint(ASMCodeFragment frag, String printString) {
		String label = new Labeller("$$debug-print").newLabel("");
		frag.add(DLabel, label);
		frag.add(DataS, printString);
		frag.add(PushD, label);
		frag.add(PushD, MMGRD_FORMAT_FOR_STRING);
		frag.add(Printf);
	}	
	@SuppressWarnings("unused")
	private static void debugPrintI(ASMCodeFragment frag, String printString, String name) {
		String label = new Labeller("$$debug-printI").newLabel("");
		loadIFrom(frag, name);
		frag.add(DLabel, label);
		frag.add(DataS, printString);
		frag.add(PushD, label);
		frag.add(PushD, MMGRD_FORMAT);
		frag.add(Printf);
	}
	

	private static final String MMGRD_PRINT_BLOCK =			  "--mmgrd-print-block";
	private static final String MMGRD_PRINT_FREE_LIST =		  "--mmgrd-print-free-list";
	private static final String MMGRD_PBLOCK_RETURN_ADDRESS = "$$mmgrd-pblock-return";
	private static final String MMGRD_PBLOCK_BLOCK =		  "$$mmgrd-pblock-block";
	private static final String MMGRD_PBLOCK_FORMAT =		  "$$mmgrd-pblock-format";
	private static final String MMGRD_PFREE_RETURN_ADDRESS =  "$$mmgrd-pfree-return";
	private static final String MMGRD_PFREE_CURRENT_BLOCK  =  "$$mmgrd-pfree-current-block";
	private static final String MMGRD_PFREE_LOOP_TEST  	   =  "--mmgrd-pfree-loop-test";
	private static final String MMGRD_PFREE_LOOP_DONE  	   =  "--mmgrd-pfree-loop-done";
	
	// [... block] -> [...]
	private static ASMCodeFragment subroutineDebugPrintBlock() {
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_VOID);
		frag.add(Label, MMGRD_PRINT_BLOCK);				// [... block (return)]
		
		declareI(frag, MMGRD_PBLOCK_RETURN_ADDRESS);
		declareI(frag, MMGRD_PBLOCK_BLOCK);

		storeITo(frag, MMGRD_PBLOCK_RETURN_ADDRESS);	// [... block]
		storeITo(frag, MMGRD_PBLOCK_BLOCK);				// [...]

		loadIFrom(frag, MMGRD_PBLOCK_BLOCK);			// [... block]

		
		frag.add(DLabel, MMGRD_PBLOCK_FORMAT);
		frag.add(DataS, "%8X[size %d %d, avail %1d%1d, %8X %8X]");


		loadIFrom(frag, MMGRD_PBLOCK_BLOCK);
		tailTag(frag);
		readTagPointer(frag);							// [... next]
		
		loadIFrom(frag, MMGRD_PBLOCK_BLOCK);
		readTagPointer(frag);							// [... next prev]
		
		loadIFrom(frag, MMGRD_PBLOCK_BLOCK);
		tailTag(frag);
		readTagAvailable(frag);							// [... next prev avail2]
		loadIFrom(frag, MMGRD_PBLOCK_BLOCK);
		readTagAvailable(frag);							// [... next prev avail2 avail1]

		loadIFrom(frag, MMGRD_PBLOCK_BLOCK);
		tailTag(frag);
		readTagSize(frag);								// [... next prev avail2 avail1 size2]
		loadIFrom(frag, MMGRD_PBLOCK_BLOCK);
		readTagSize(frag);								// [... next prev avail2 avail1 size2 size1]
		
		loadIFrom(frag, MMGRD_PBLOCK_BLOCK);			// [... next prev avail2 avail1 size2 size1 block]
		
		frag.add(PushD, MMGRD_PBLOCK_FORMAT);
		frag.add(Printf);

		loadIFrom(frag, MMGRD_PBLOCK_RETURN_ADDRESS);
		frag.add(Return);
		
		return frag;
	}
	private static ASMCodeFragment subroutineDebugPrintFreeList() {
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_VOID);
		frag.add(Label, MMGRD_PRINT_FREE_LIST);				// [... block (return)]

		declareI(frag, MMGRD_PFREE_RETURN_ADDRESS);
		declareI(frag, MMGRD_PFREE_CURRENT_BLOCK);
		
		storeITo(frag, MMGRD_PFREE_RETURN_ADDRESS);
		
		debugPrint(frag, "Free list:\n");
		
		
		loadIFrom(frag, MEM_MANAGER_FIRST_FREE_BLOCK);
		storeITo(frag, MMGRD_PFREE_CURRENT_BLOCK);
		
		frag.add(Label, MMGRD_PFREE_LOOP_TEST);
		    // if(currentBlock == 0) break;
			loadIFrom(frag, MMGRD_PFREE_CURRENT_BLOCK);		
			frag.add(JumpFalse, MMGRD_PFREE_LOOP_DONE);
			
			// print "    "+currentBlock;
			debugPrint(frag, "    ");						
			loadIFrom(frag, MMGRD_PFREE_CURRENT_BLOCK);
			frag.add(Call, MMGRD_PRINT_BLOCK);
			debugPrint(frag, "\n");
			
			// currentBlock = currentBlock.next
			loadIFrom(frag, MMGRD_PFREE_CURRENT_BLOCK);		// [... block]
			tailTag(frag);									// [... tailTag]
			readTagPointer(frag);							// [... next]
			storeITo(frag, MMGRD_PFREE_CURRENT_BLOCK);
			
			frag.add(Jump, MMGRD_PFREE_LOOP_TEST);

		frag.add(Label, MMGRD_PFREE_LOOP_DONE);
			debugPrint(frag, "\n");
			loadIFrom(frag, MMGRD_PFREE_RETURN_ADDRESS);
			frag.add(Return);
		return frag;
	}	

}
