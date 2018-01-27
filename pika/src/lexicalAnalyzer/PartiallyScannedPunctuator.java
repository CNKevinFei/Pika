package lexicalAnalyzer;

import tokens.LextantToken;
import tokens.NullToken;
import tokens.Token;
import inputHandler.LocatedChar;
import inputHandler.LocatedCharString;
import logging.PikaLogger;

public class PartiallyScannedPunctuator extends LocatedCharString {

	public PartiallyScannedPunctuator(LocatedChar c) {
		super(c);
	}

	
	// queries
	public Boolean isPunctuator() {
		return asPunctuator() != Punctuator.NULL_PUNCTUATOR;
	}
	
	// conversions
	public Punctuator asPunctuator() {
		return Punctuator.forLexeme(asString());
	}
	public Token asToken() {
		if(isEmpty()) {
			PikaLogger log = PikaLogger.getLogger("compiler.lexicalAnalyzer");
			log.severe("Lexical error: no matching punctuator.");
			return NullToken.make(startingLocation);
		}
		assert(isPunctuator());
		return LextantToken.make(startingLocation, asString(), asPunctuator());
	}
}
