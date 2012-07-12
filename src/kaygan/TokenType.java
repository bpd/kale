package kaygan;

public enum TokenType
{
	Comment,
	WS,
	Binary,
	Hex,
	Int,
	Real,
	String,
	Symbol,
	EOF,
	
	
	COLON,        // ':'
	
	OPEN_BRACE,   // '{'
	CLOSE_BRACE,  // '}'
	
	OPEN_PAREN,   // '('
	CLOSE_PAREN,  // ')'
	
	OPEN_BRACKET, // '['
	CLOSE_BRACKET,// ']'
	
	PIPE          // '|'
}
