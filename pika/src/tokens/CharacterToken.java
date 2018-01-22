package tokens;

import inputHandler.TextLocation;

public class CharacterToken extends TokenImp {
	protected char value;
	
	protected CharacterToken(TextLocation location, String lexeme) {
		super(location, lexeme);
	}
	protected void setValue(String value) {
		this.value = value.charAt(0);
	}
	public char getValue() {
		return value;
	}
	
	public static CharacterToken make(TextLocation location, char character) {
		String lexeme = Character.toString(character);
		CharacterToken result = new CharacterToken(location, lexeme);
		result.setValue(lexeme);
		return result;
	}
	
	@Override
	protected String rawString() {
		return "character, " + value;
	}
}
