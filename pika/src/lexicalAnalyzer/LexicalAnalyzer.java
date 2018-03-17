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
import tokens.CharacterToken;
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
		else if(ch.isChracter()) {
			return scanCharacter(ch);
		}
		else if(ch.isIdentifierBeginning()) {
			return scanIdentifier(ch);
		}
		else if(ch.isCommentStart()) {
			return scanComment(ch);
		}
		else if(isPunctuatorStart(ch)) {
			Token token = PunctuatorScanner.scan(ch, input);
			if(!token.isLextant(Punctuator.CLOSE_BRACKET, Punctuator.CLOSE_PARENTHESES)) {
				isLastPunctuator = true;
			}
			return token;
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
		
		if(buffer.length()==1 && firstChar.isChar('-')) {
			return PunctuatorScanner.scan(firstChar, input);
		}
		if(point!=null || firstChar.isChar('.')) {
			isInt = false;
		}
			
		
		if(buffer.charAt(buffer.length()-1)=='.') {
			if(point==null)
				input.pushback(firstChar);
			else
				input.pushback(point);
			
			if(!firstChar.isChar('.'))
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
		appendCharacter(firstChar,buffer);
		
		return StringToken.make(firstChar.getLocation(), buffer.toString());
	}
	
	private void appendCharacter(LocatedChar firstChar, StringBuffer buffer) {
		int startLine = firstChar.getLocation().getLineNumber();
		LocatedChar c = input.next();
		
		while(!c.isString() && c.getLocation().getLineNumber()==startLine)
		{
			buffer.append(c.getCharacter());
			c = input.next();
		}
		
		if(!c.isChar('\"')) {
			stringLexicalError(firstChar.getLocation());
			input.pushback(c);
		}
		
	}
	
	//////////////////////////////////////////////////////////////////////////////
	// Character lexical analysis
	
	private Token scanCharacter(LocatedChar firstChar) {
		char character = input.next().getCharacter();
		LocatedChar end = input.next();	
		
		if(!end.isChar('^')) {
			charConstantTooLongLexicalError(firstChar.getLocation());
		}
		
		if(character > 126 || character < 32) {
			charConstantInvalidLexicalError(firstChar.getLocation());
		}
		
		return CharacterToken.make(firstChar.getLocation(), character);
		
	}
	//////////////////////////////////////////////////////////////////////////////
	// Identifier and keyword lexical analysis	

	private Token scanIdentifier(LocatedChar firstChar) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(firstChar.getCharacter());
		appendSubsequentLetter(buffer);

		String lexeme = buffer.toString();
		
		if(Keyword.isAKeyword(lexeme)) {
			this.isLastPunctuator = true;
			return LextantToken.make(firstChar.getLocation(), lexeme, Keyword.forLexeme(lexeme));
		}
		else {
			return IdentifierToken.make(firstChar.getLocation(), lexeme);
		}
	}
	private void appendSubsequentLetter(StringBuffer buffer) {
		LocatedChar c = input.next();
		while(c.isLetter()) {
			buffer.append(c.getCharacter());
			c = input.next();
		}
		input.pushback(c);
		
		if(buffer.length()>32) {
			identifierLexicalError(buffer.toString());
		}
		
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
		
		if(startLine != currentLine)
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
	
	private void identifierLexicalError(String str) {
		PikaLogger log = PikaLogger.getLogger("compiler.lexicalAnalyzer");
		log.severe("Lexical error: Identifier " + str + " is longer than 32 characters.");
	}
	private void charConstantTooLongLexicalError(TextLocation location) {
		PikaLogger log = PikaLogger.getLogger("compiler.lexicalAnalyzer");
		log.severe("Lexical error: character constant too long at line " + location.getLineNumber() + " position " + location.getPosition() + ".");
	}
	private void charConstantInvalidLexicalError(TextLocation location) {
		PikaLogger log = PikaLogger.getLogger("compiler.lexicalAnalyzer");
		log.severe("Lexical error: character constant is not valid at line " + location.getLineNumber() + " position " + location.getPosition() + ".");
	}
	private void stringLexicalError(TextLocation location) {
		PikaLogger log = PikaLogger.getLogger("compiler.lexicalAnalyzer");
		log.severe("Lexical error: string constant can't match at line " + location.getLineNumber() + " position " + location.getPosition() + ".");
	}
	
	
}
