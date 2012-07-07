package kaygan.ast;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayDeque;
import java.util.Deque;

public class Lexer
{
	private final CharReader reader;
	
	private final Deque<Token> buffer = new ArrayDeque<Token>();
	
	private int beginOffset;
	
	private final StringBuilder contentBuffer = new StringBuilder();
	
	public Lexer(Reader reader)
	{
		this.reader = new CharReader(reader, 2); // LA(2)
	}
	
	public Token peek() throws IOException
	{
		if( buffer.size() == 0 )
		{
			Token next = next();
			buffer.add(next);
			return next;
		}
		else
		{
			return buffer.peek();
		}
	}
	
	
	
	protected void begin()
	{
		begin(0);
	}
	
	/**
	 * 
	 * @param startOffset - offset from the current read position when
	 *                      the token actually started
	 */
	protected void begin(int startOffset)
	{
		beginOffset = reader.getOffset() + startOffset;
	}
	
	protected Token end(TokenType type)
	{
		Token token = new Token(	type, 
									beginOffset, reader.getOffset(), 
									contentBuffer.toString());
		
		// reset token state
		reset();
		
		return token;
	}
	
	protected void consume(int c)
	{
		contentBuffer.appendCodePoint(c);
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
	
	protected void reset()
	{
		beginOffset = reader.getOffset();
		contentBuffer.setLength(0);
	}
	
	
	protected boolean isWS(int c)
	{
		return c == ' ' || c == '\t' || c == '\r' || c == '\n';
	}
	
	protected boolean isEOF(int c)
	{
		return c == -1 || c == 65535;
	}
	
	protected boolean isBinaryDigit(int c)
	{
		return c == '0' || c == '1';
	}
	
	protected boolean isHexDigit(int c)
	{
		return (c >= '0' && c <= '9')
				|| (c >= 'a' && c <= 'f')
				|| (c >= 'A' && c <= 'F');
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
			|| c == ':' || c == '.';
	}
	
	protected boolean isSymbol(int c)
	{
		return !(isControl(c) || isEOF(c) || isWS(c));
	}
	
	
	public Token next() throws IOException
	{
		int c = peekChar();
		
		switch(c)
		{
		case '(': consume(); return end(TokenType.OPEN_PAREN);
		case ')': consume(); return end(TokenType.CLOSE_PAREN);
		
		case '[': consume(); return end(TokenType.OPEN_BRACKET);
		case ']': consume(); return end(TokenType.CLOSE_BRACKET);
		
		case '{': consume(); return end(TokenType.OPEN_BRACE);
		case '}': consume(); return end(TokenType.CLOSE_BRACE);
		
		case ':': consume(); return end(TokenType.COLON);
		case '.':
			consume();
			if( peekChar() == '.' )
			{
				consume();
				return end(TokenType.BETWEEN);
			}
			return end(TokenType.FULL_STOP);
		}
		
		if( isWS(c) )
		{
			do
			{
				consume();
			}
			while( isWS( peekChar() ) );
			
			return end(TokenType.WS);
		}
		
		if( c == '/' )
		{
			consume();
			
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
		}
		
		if( c == '0' )
		{
			consume();
			
			int peek = peekChar();
			if( peek == 'b' )
			{
				consume();
				while( isBinaryDigit( peekChar() ) )
				{
					consume();
				}
				return end(TokenType.Binary);
			}
			else if( peek == 'x' )
			{
				consume();
				while( isHexDigit( peekChar() ) )
				{
					consume();
				}
				return end(TokenType.Hex);
			}
			
			// we already have a zero, so fall through
			// to the number lexer (put back the zero we consumed)
			pushChar(c);
		}
		
		if( isDigit(c) )
		{
			do
			{
				consume();
			}
			while( isDigit( peekChar() ) );
			
			// we've read up to a non-digit,
			// if we find a decimal point here read a real
			
			if( peekChar() == '.' )
			{
				do
				{
					consume();
				}
				while( isDigit( peekChar() ) );
				
				return end(TokenType.Real);
			}

			// no decimal point, just an int
			return end(TokenType.Int);
		}
		
		if( isSymbol(c) )
		{
			// symbol part
			int peek = peekChar();
			while( isSymbol(peek) )
			{
				consume();
				peek = peekChar();
			}
			return end(TokenType.SymbolPart);
		}
		
		return end(TokenType.EOF);
	}
}