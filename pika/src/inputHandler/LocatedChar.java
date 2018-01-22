package inputHandler;

/** Value object for holding a character and its location in the input text.
 *  Contains delegates to select character operations.
 *
 */
public class LocatedChar {
	Character character;
	TextLocation location;
	
	public LocatedChar(Character character, TextLocation location) {
		super();
		this.character = character;
		this.location = location;
	}

	
	//////////////////////////////////////////////////////////////////////////////
	// getters
	
	public Character getCharacter() {
		return character;
	}
	public TextLocation getLocation() {
		return location;
	}
	public boolean isChar(char c) {
		return character == c;
	}
	
	
	
	//////////////////////////////////////////////////////////////////////////////
	// toString
	
	public String toString() {
		return "(" + charString() + ", " + location + ")";
	}
	private String charString() {
		if(Character.isWhitespace(character)) {
			int i = character;
			return String.format("'\\%d'", i);
		}
		else {
			return character.toString();
		}
	}

	
	//////////////////////////////////////////////////////////////////////////////
	// delegates
	
	public boolean isLetter() {
		return Character.isLowerCase(character) || Character.isUpperCase(character) || isChar('_');
	}
	public boolean isLowerCase() {
		return Character.isLowerCase(character) || isChar('_');
	}
	public boolean isDigit() {
		return Character.isDigit(character);
	}
	public boolean isSign() {
		return isChar('+') || isChar('-');
	}
	public boolean isString() {
		return isChar('\"');
	}
	public boolean isChracter() {
		return isChar('^');
	}
	public boolean isWhitespace() {
		return Character.isWhitespace(character);
	}
	public boolean isCommentStart() {
		return isChar('#');
	}
	public boolean isCommentEnd() {
		return isChar('#');
	}
}
