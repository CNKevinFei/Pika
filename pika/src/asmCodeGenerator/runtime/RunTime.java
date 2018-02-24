package asmCodeGenerator.runtime;
import static asmCodeGenerator.codeStorage.ASMCodeFragment.CodeType.*;
import static asmCodeGenerator.codeStorage.ASMOpcode.*;
import asmCodeGenerator.runtime.MemoryManager;
import asmCodeGenerator.codeStorage.ASMCodeFragment;
public class RunTime {
	public static final String EAT_LOCATION_ZERO      = "$eat-location-zero";		// helps us distinguish null pointers from real ones.
	public static final String INTEGER_PRINT_FORMAT   = "$print-format-integer";
	public static final String FLOAT_PRINT_FORMAT   = "$print-format-float";
	public static final String CHAR_PRINT_FORMAT   = "$print-format-char";
	public static final String STRING_PRINT_FORMAT   = "$print-format-string";
	public static final String BOOLEAN_PRINT_FORMAT   = "$print-format-boolean";
	public static final String NEWLINE_PRINT_FORMAT   = "$print-format-newline";
	public static final String SPACE_PRINT_FORMAT     = "$print-format-space";
	public static final String TAB_PRINT_FORMAT     = "$print-format-tab";
	public static final String BOOLEAN_TRUE_STRING    = "$boolean-true-string";
	public static final String BOOLEAN_FALSE_STRING   = "$boolean-false-string";
	public static final String GLOBAL_MEMORY_BLOCK    = "$global-memory-block";
	public static final String USABLE_MEMORY_START    = "$usable-memory-start";
	public static final String STRING_CONSTANT_BLOCK =  "$string-constant-memory";
	public static final String MAIN_PROGRAM_LABEL     = "$$main";
	
	public static final String GENERAL_RUNTIME_ERROR = "$$general-runtime-error";
	public static final String INTEGER_DIVIDE_BY_ZERO_RUNTIME_ERROR = "$$i-divide-by-zero";
	public static final String FLOAT_DIVIDE_BY_ZERO_RUNTIME_ERROR = "$$f-divide-by-zero";
	public static final String RAT_DIVIDE_BY_ZERO_RUNTIME_ERROR = "$$r-divide-by-zero";
	
	public static final String RAT_WITH_ZERO_DENOMINATOR_RUNTIME_ERROR = "$$r-denominator-zero";
	
	public static final String ARRAY_INDEX_NEGATIVE = "$$a-index-negative";
	public static final String ARRAY_INDEX_EXCEED = "$$a-index-exceed";
	public static final String ARRAY_RECORD_ERROR = "$$a-record-error";
	public static final String ARRAY_RECORD_DELETED_ERROR = "$$a-record-deleted-error";
	
	private ASMCodeFragment environmentASM() {
		ASMCodeFragment result = new ASMCodeFragment(GENERATES_VOID);
		result.append(MemoryManager.codeForInitialization());
		result.append(jumpToMain());
		result.append(stringsForPrintf());
		result.append(runtimeErrors());
		result.add(DLabel, USABLE_MEMORY_START);
		return result;
	}
	
	private ASMCodeFragment jumpToMain() {
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_VOID);
		frag.add(Jump, MAIN_PROGRAM_LABEL);
		return frag;
	}

	private ASMCodeFragment stringsForPrintf() {
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_VOID);
		frag.add(DLabel, EAT_LOCATION_ZERO);
		frag.add(DataZ, 8);
		frag.add(DLabel, INTEGER_PRINT_FORMAT);
		frag.add(DataS, "%d");
		frag.add(DLabel, FLOAT_PRINT_FORMAT);
		frag.add(DataS, "%g");
		frag.add(DLabel, BOOLEAN_PRINT_FORMAT);
		frag.add(DataS, "%s");
		frag.add(DLabel, STRING_PRINT_FORMAT);
		frag.add(DataS, "%s");
		frag.add(DLabel, CHAR_PRINT_FORMAT);
		frag.add(DataS, "%c");
		frag.add(DLabel, NEWLINE_PRINT_FORMAT);
		frag.add(DataS, "\n");
		frag.add(DLabel, TAB_PRINT_FORMAT);
		frag.add(DataS, "\t");
		frag.add(DLabel, SPACE_PRINT_FORMAT);
		frag.add(DataS, " ");
		frag.add(DLabel, BOOLEAN_TRUE_STRING);
		frag.add(DataS, "true");
		frag.add(DLabel, BOOLEAN_FALSE_STRING);
		frag.add(DataS, "false");
		
		return frag;
	}
	
	
	private ASMCodeFragment runtimeErrors() {
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_VOID);
		
		generalRuntimeError(frag);
		divideByZeroError(frag);
		ratDenominatorZeroError(frag);
		arrayIndexError(frag);
		arrayRecordError(frag);
		
		return frag;
	}
	private ASMCodeFragment generalRuntimeError(ASMCodeFragment frag) {
		String generalErrorMessage = "$errors-general-message";

		frag.add(DLabel, generalErrorMessage);
		frag.add(DataS, "Runtime error: %s\n");
		
		frag.add(Label, GENERAL_RUNTIME_ERROR);
		frag.add(PushD, generalErrorMessage);
		frag.add(Printf);
		frag.add(Halt);
		return frag;
	}
	private void divideByZeroError(ASMCodeFragment frag) {
		String intDivideByZeroMessage = "$errors-int-divide-by-zero";
		String floatDivideByZeroMessage = "$errors-float-divide-by-zero";
		String ratDivideByZeroMessage = "$errors-rat-divide-by-zero";
		
		frag.add(DLabel, intDivideByZeroMessage);
		frag.add(DataS, "integer divide by zero");
		
		frag.add(Label, INTEGER_DIVIDE_BY_ZERO_RUNTIME_ERROR);
		frag.add(PushD, intDivideByZeroMessage);
		frag.add(Jump, GENERAL_RUNTIME_ERROR);
		
		frag.add(DLabel, floatDivideByZeroMessage);
		frag.add(DataS, "float divide by zero");
		
		frag.add(Label, FLOAT_DIVIDE_BY_ZERO_RUNTIME_ERROR);
		frag.add(PushD, floatDivideByZeroMessage);
		frag.add(Jump, GENERAL_RUNTIME_ERROR);
		
		frag.add(DLabel, ratDivideByZeroMessage);
		frag.add(DataS, "rat divide by zero");
		
		frag.add(Label, RAT_DIVIDE_BY_ZERO_RUNTIME_ERROR);
		frag.add(PushD, ratDivideByZeroMessage);
		frag.add(Jump, GENERAL_RUNTIME_ERROR);
	}
	
	private void ratDenominatorZeroError(ASMCodeFragment frag) {
		String ratDenominatorZeroError = "$errors-denominator-zero";
				
		frag.add(DLabel, ratDenominatorZeroError);
		frag.add(DataS, "denominator is zero");
		
		frag.add(Label, RAT_WITH_ZERO_DENOMINATOR_RUNTIME_ERROR);
		frag.add(PushD, ratDenominatorZeroError);
		frag.add(Jump, GENERAL_RUNTIME_ERROR);
	}
	
	private void arrayIndexError(ASMCodeFragment frag) {
		String indexNeg = "$errors-index-neg";
		String indexExceed = "$errors-index-exceed";
				
		frag.add(DLabel, indexNeg);
		frag.add(DataS, "array index is negative.");
		
		frag.add(Label, ARRAY_INDEX_NEGATIVE);
		frag.add(PushD, indexNeg);
		frag.add(Jump, GENERAL_RUNTIME_ERROR);
		
		frag.add(DLabel, indexExceed);
		frag.add(DataS, "array index exceeds.");
		
		frag.add(Label, ARRAY_INDEX_EXCEED);
		frag.add(PushD, indexExceed);
		frag.add(Jump, GENERAL_RUNTIME_ERROR);
		
		
	}
	
	private void arrayRecordError(ASMCodeFragment frag) {
		String recordError = "$errors-record-error";
		String recordDeletedError = "$errors-record-deleted-error";
				
		frag.add(DLabel, recordError);
		frag.add(DataS, "array record is not valid.");
		
		frag.add(DLabel, recordDeletedError);
		frag.add(DataS, "array record has been deleted.");
		
		frag.add(Label, ARRAY_RECORD_ERROR);
		frag.add(PushD, recordError);
		frag.add(Jump, GENERAL_RUNTIME_ERROR);
		
		frag.add(Label, ARRAY_RECORD_DELETED_ERROR);
		frag.add(PushD, recordDeletedError);
		frag.add(Jump, GENERAL_RUNTIME_ERROR);
	}
	
	
	public static ASMCodeFragment getEnvironment() {
		RunTime rt = new RunTime();
		return rt.environmentASM();
	}
}
