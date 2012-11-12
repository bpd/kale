package kale;

public enum TokenType
{
	Comment,
	Int,
	String,
	Boolean,
	Symbol,
	Keyword,
	EOF,
	
	DOT,          // '.'
	COMMA,        // ','
	SEMICOLON,    // ';'
	EQUALS,       // '='
	//AMP,          // '&'
	
	OPEN_BRACE,   // '{'
	CLOSE_BRACE,  // '}'
	
	OPEN_PAREN,   // '('
	CLOSE_PAREN,  // ')'
	
	OPEN_BRACKET, // '['
	CLOSE_BRACKET // ']'
}
