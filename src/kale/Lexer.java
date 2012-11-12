package kale;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Lexer
{
	private final CharReader reader;
	
	/** a sequential list of all tokens parsed, for highlighting, etc */
	private List<Token> tokens = new ArrayList<Token>();
	
	private final List<Token> buffer = new ArrayList<Token>();
	
	private int beginOffset;
	
	private final StringBuilder contentBuffer = new StringBuilder();
	
	/**
	 * ignore carriage returns in token offsets, since Java
	 * text editors ignore that when calculating character offsets
	 */
	int crCount = 0;
	
	static final Set<String> KEYWORDS = new HashSet<String>(
			Arrays.asList( 
					"package", "import", "type", "interface",
					"if", "else", "while", "return", "func",
					"operator" )
		);
	
	public Lexer(Reader reader)
	{
		this.reader = new CharReader(reader, 2); // LA(2)
	}
	
	public Token peek()
	{
		return peek(1);
	}
	
	public Token peek(int lookAhead)
	{
		// populate the buffer with as many token as we need
		for( int i=buffer.size(); i<=lookAhead+1; i++ )
		{
			buffer.add( read() );
		}
		
		return buffer.get(lookAhead - 1);
	}
	
	protected Token end(TokenType type)
	{
		String value = contentBuffer.toString();
		
		Token token = new Token(	type, 
									beginOffset,
									beginOffset + value.length(), 
									value);
		
		// reset token state
		contentBuffer.setLength(0);
		
		return token;
	}
	
	/**
	 * @return Returns a list of all tokens read so far by this Lexer
	 * 
	 */
	public List<Token> getTokenList()
	{
		return tokens;
	}
	
	protected void consume() throws IOException
	{
		contentBuffer.appendCodePoint( reader.read() );
	}
	
	protected int peekChar() throws IOException
	{
		return reader.peek();
	}
	
	protected void pushChar(int c) throws IOException
	{
		contentBuffer.setLength( contentBuffer.length() -1 );
		reader.unread(c);
	}
	
	protected boolean isWS(int c)
	{
		return c == ' ' || c == '\t' || c == '\r' || c == '\n';
	}
	
	protected boolean isEOF(int c)
	{
		return c == -1 || c == 65535;
	}
	
	protected boolean isDigit(int c)
	{
		return c >= '0' && c <= '9';
	}
	
	protected boolean isControl(int c)
	{
		return c == '{' || c == '}'
			|| c == '(' || c == ')'
			|| c == '[' || c == ']'
			;
	}
	
	protected boolean isPunctuation(int c)
	{
		return c == '.' || c == ','
			|| c == ';' //|| c == '='
			//|| c == '&'
			;
	}
	
	protected boolean isSymbol(int c)
	{
		return !(isControl(c) || isEOF(c) || isWS(c) || isPunctuation(c));
	}
	
	
	public Token next()
	{
		if( buffer.size() > 0 )
		{
			return buffer.remove(0);
		}
		
		return read();
	}
	
	
	protected Token read()
	{
		Token next = readToken();
		while( next.type == TokenType.Comment )
		{
			// add comment to the token stream
			tokens.add( next );
			
			next = readToken();
		}
		
		tokens.add( next );
		
		return next;
	}
	
	private Token readToken()
	{
		try
		{
			int c = peekChar();
			
			// eat any whitespace
			if( isWS(c) )
			{
				do
				{
					int ws = reader.read();
					if( ws == '\r' )
					{
						crCount++;
					}
				}
				while( isWS( peekChar() ) );
				
				c = peekChar();
			}
			
			beginOffset = reader.getOffset() - crCount;
			
			switch(c)
			{
			case '(': consume(); return end(TokenType.OPEN_PAREN);
			case ')': consume(); return end(TokenType.CLOSE_PAREN);
			
			case '[': consume(); return end(TokenType.OPEN_BRACKET);
			case ']': consume(); return end(TokenType.CLOSE_BRACKET);
			
			case '{': consume(); return end(TokenType.OPEN_BRACE);
			case '}': consume(); return end(TokenType.CLOSE_BRACE);
			
			case '.': consume(); return end(TokenType.DOT);
			case ',': consume(); return end(TokenType.COMMA);
			case ';': consume(); return end(TokenType.SEMICOLON);
			case '=':
				// in order for an equal sign to be its own token,
				// it must not be followed by a symbol char
				consume();
				if( !isSymbol( peekChar() ) )
				{
					return end(TokenType.EQUALS);
				}
				pushChar(c);
				break;
				
			//case '&': consume(); return end(TokenType.AMP);
			}
			
			// String
			if( c == '"' )
			{
				// consume the '"' without adding to contentBuffer
				do
				{
					// TODO escaping
					consume();
				}
				while( peekChar() != '"' && !isEOF( peekChar() ) );
				
				// consume the '"' without adding to contentBuffer
				consume();
				
				return end(TokenType.String);
			}
			
			// Comment
			if( c == '/' )
			{
				consume();
				
				if( peekChar() == '/' )
				{
					// comment
					consume_comment:
					while(true)
					{
						consume();
						
						int peek = peekChar();
						if( peek == '\r' )
						{
							crCount++;
						}
						else if( peek == '\n' || isEOF(peek) )
						{
							//consume();
							break consume_comment;
						}
					}
					
					return end(TokenType.Comment);
				}
				else
				if( peekChar() == '*' )
				{
					// comment
					consume_comment:
					while(true)
					{
						consume();
						
						int peek = peekChar();
						if( peek == '*' )
						{
							consume();
							if( peekChar() == '/' )
							{
								consume();
								break consume_comment;
							}
						}
						else if( isEOF(peek) )
						{
							throw new IOException("EOF reached before end /*");
						}
					}

					return end(TokenType.Comment);
				}
				pushChar(c);
			}
			
			// Int
			if( c == '-' )
			{
				consume();
				
				c = peekChar();
				
				if( !(isDigit(c) || isSymbol(c)) )
				{
					// char immediately following the '-' is not a digit,
					// and can't be folded into a symbol, so treat the '-'
					// itself as a symbol in and of itself
					return end(TokenType.Symbol);
				}
				// else fall through and include the chars
				// following the '-' in a digit or symbol
				
			}
			
			if( isDigit(c) )
			{
				do
				{
					consume();
				}
				while( isDigit( peekChar() ) );
	
				return end(TokenType.Int);
			}
			
			if( isSymbol(c) )
			{
				// symbol part
				do
				{
					consume();
				}
				while( isSymbol( peekChar() ) );
				
				String symbolValue = contentBuffer.toString();
				if( KEYWORDS.contains( symbolValue ) )
				{
					return end(TokenType.Keyword);
				}
				
				if(	symbolValue.equals("true")
					|| symbolValue.equals("false") )
				{
					return end(TokenType.Boolean);
				}
				
				return end(TokenType.Symbol);
			}
			
			return end(TokenType.EOF);
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
