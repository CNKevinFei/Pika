package lexicalAnalyzer;


import logging.PikaLogger;

import inputHandler.InputHandler;
import inputHandler.LocatedChar;
import inputHandler.LocatedCharStream;
import inputHandler.PushbackCharStream;
import inputHandler.TextLocation;
import tokens.IdentifierToken;
import tokens.LextantToken;
import tokens.NullToken;
import tokens.IntegerToken;
import tokens.FloatToken;
import tokens.StringToken;
import tokens.Token;

import static lexicalAnalyzer.PunctuatorScanningAids.*;

public class LexicalAnalyzer extends ScannerImp implements Scanner {
	private boolean isLastPunctuator = false;
	
	public static LexicalAnalyzer make(String filename) {
		InputHandler handler = InputHandler.fromFilename(filename);
		PushbackCharStream charStream = PushbackCharStream.make(handler);
		return new LexicalAnalyzer(charStream);
	}

	public LexicalAnalyzer(PushbackCharStream input) {
		super(input);
	}

	
	//////////////////////////////////////////////////////////////////////////////
	// Token-finding main dispatch	

	@Override
	protected Token findNextToken() {
		LocatedChar ch = nextNonWhitespaceChar();
		boolean tem = isLastPunctuator;
		
		isLastPunctuator = false;
		
		if(ch.isDigit() || (ch.isSign()&&tem) ||ch.isChar('.')) {
			return scanNumber(ch);
		}
		else if(ch.isString()) {
			return scanString(ch);
		}
		/*else if(ch.isChracter()) {
			return scanChracter(ch);
		}*/
		else if(ch.isLetter()) {
			return scanIdentifier(ch);
		}
		else if(ch.isCommentStart()) {
			return scanComment(ch);
		}
		else if(isPunctuatorStart(ch)) {
			isLastPunctuator = true;
			return PunctuatorScanner.scan(ch, input);
		}
		else if(isEndOfInput(ch)) {
			return NullToken.make(ch.getLocation());
		}
		else {
			lexicalError(ch);
			return findNextToken();
		}
	}


	private LocatedChar nextNonWhitespaceChar() {
		LocatedChar ch = input.next();
		while(ch.isWhitespace()) {
			ch = input.next();
		}
		return ch;
	}
	
	
	//////////////////////////////////////////////////////////////////////////////
	// Integer/Float lexical analysis	

	private Token scanNumber(LocatedChar firstChar) {
		StringBuffer buffer = new StringBuffer();
		LocatedChar point = firstChar;
		boolean isInt = true;
		
		buffer.append(firstChar.getCharacter());
		appendSubsequentDigits(buffer);
		point = appendPoint(buffer);
		appendSubsequentDigits(buffer);
		
		if(point!=null || firstChar.isChar('.'))
			isInt = false;
		
		if(buffer.charAt(buffer.length()-1)=='.') {
			if(point==null)
				input.pushback(firstChar);
			else
				input.pushback(point);
			
			isInt = true;
			
			buffer = buffer.deleteCharAt(buffer.length()-1);
		}
		
		if(buffer.length()==0) {
			firstChar = input.next();
		
			return PunctuatorScanner.scan(firstChar, input);
		}
		
		else if(isInt) {
			return IntegerToken.make(firstChar.getLocation(), buffer.toString());
		}
		else {
			appendExponent(buffer);
			
			return FloatToken.make(firstChar.getLocation(), buffer.toString());
		}
		
	}
	
	private void appendSubsequentDigits(StringBuffer buffer) {
		LocatedChar c = input.next();
		while(c.isDigit()) {
			buffer.append(c.getCharacter());
			c = input.next();
		}
		input.pushback(c);
	}
	
	
	private LocatedChar appendPoint(StringBuffer buffer) {
		LocatedChar c = input.next();
		//System.out.println(c.getCharacter());
		if(c.isChar('.'))
			buffer.append(c.getCharacter());
		else {
			input.pushback(c);
			c = null;
		}
			
		return c;
	}
	
	private void appendExponent(StringBuffer buffer) {
		LocatedChar c = input.next();
		
		if(c.isChar('E')) {
			buffer.append(c.getCharacter());
			
			c = input.next();
			if(c.isChar('+') | c.isChar('-'))
				buffer.append(c.getCharacter());
			else
				input.pushback(c);
			
			appendSubsequentDigits(buffer);
		}
		else
			input.pushback(c);
	}
	
	
	//////////////////////////////////////////////////////////////////////////////
	// String lexical analysis
	
	private Token scanString(LocatedChar firstChar) {
		StringBuffer buffer = new StringBuffer();
		appendCharacter(buffer);
		
		return StringToken.make(firstChar.getLocation(), buffer.toString());
	}
	
	private void appendCharacter(StringBuffer buffer) {
		LocatedChar c = input.next();
		
		while(!c.isString())
		{
			buffer.append(c.getCharacter());
			c = input.next();
		}
			
		
	}
	
	
	//////////////////////////////////////////////////////////////////////////////
	// Identifier and keyword lexical analysis	

	private Token scanIdentifier(LocatedChar firstChar) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(firstChar.getCharacter());
		appendSubsequentLowercase(buffer);

		String lexeme = buffer.toString();
		if(Keyword.isAKeyword(lexeme)) {
			return LextantToken.make(firstChar.getLocation(), lexeme, Keyword.forLexeme(lexeme));
		}
		else {
			return IdentifierToken.make(firstChar.getLocation(), lexeme);
		}
	}
	private void appendSubsequentLowercase(StringBuffer buffer) {
		LocatedChar c = input.next();
		while(c.isLowerCase()) {
			buffer.append(c.getCharacter());
			c = input.next();
		}
		input.pushback(c);
	}
	
	//////////////////////////////////////////////////////////////////////////////
	// Comment lexical analysis
	
	private Token scanComment(LocatedChar firstChar) {
		LocatedChar c = input.next();
		int startLine = firstChar.getLocation().getLineNumber();
		int currentLine = startLine;
		
		
		while((!c.isCommentEnd()) && startLine == currentLine) {
			c=input.next();
			currentLine = c.getLocation().getLineNumber();
		}
		
		if(!c.isCommentEnd())
			input.pushback(c);
		
		return findNextToken();
		
	}
	//////////////////////////////////////////////////////////////////////////////
	// Punctuator lexical analysis	
	// old method left in to show a simple scanning method.
	// current method is the algorithm object PunctuatorScanner.java

	@SuppressWarnings("unused")
	private Token oldScanPunctuator(LocatedChar ch) {
		TextLocation location = ch.getLocation();
		
		switch(ch.getCharacter()) {
		case '*':
			return LextantToken.make(location, "*", Punctuator.MULTIPLY);
		case '+':
			return LextantToken.make(location, "+", Punctuator.ADD);
		case '>':
			return LextantToken.make(location, ">", Punctuator.GREATER);
		case ':':
			if(ch.getCharacter()=='=') {
				return LextantToken.make(location, ":=", Punctuator.ASSIGN);
			}
			else {
				throw new IllegalArgumentException("found : not followed by = in scanOperator");
			}
		case ',':
			return LextantToken.make(location, ",", Punctuator.SEPARATOR);
		case ';':
			return LextantToken.make(location, ";", Punctuator.TERMINATOR);
		default:
			throw new IllegalArgumentException("bad LocatedChar " + ch + "in scanOperator");
		}
	}

	

	//////////////////////////////////////////////////////////////////////////////
	// Character-classification routines specific to Pika scanning.	

	private boolean isPunctuatorStart(LocatedChar lc) {
		char c = lc.getCharacter();
		return isPunctuatorStartingCharacter(c);
	}

	private boolean isEndOfInput(LocatedChar lc) {
		return lc == LocatedCharStream.FLAG_END_OF_INPUT;
	}
	
	
	//////////////////////////////////////////////////////////////////////////////
	// Error-reporting	

	private void lexicalError(LocatedChar ch) {
		PikaLogger log = PikaLogger.getLogger("compiler.lexicalAnalyzer");
		log.severe("Lexical error: invalid character " + ch);
	}

	
}
