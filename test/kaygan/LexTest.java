package kaygan;

import java.io.IOException;
import java.io.StringReader;

import junit.framework.TestCase;

public class LexTest extends TestCase
{
	public void testCommentWS() throws IOException
	{
		String input = " /*  */  0b01010 0x54ab7c0d8e8f ";
		Lexer lexer = new Lexer(new StringReader(input));
		
		assertToken( lexer.next(), TokenType.Comment, "/*  */" );

		assertToken( lexer.next(), TokenType.Binary, "0b01010" );

		assertToken( lexer.next(), TokenType.Hex, "0x54ab7c0d8e8f" );
		
		assertEquals( lexer.next().type, TokenType.EOF );	
	}
	
	public void testNumbers() throws IOException
	{
		String input = " 954 0 1 0.2 1.1 2.33 ";
		Lexer lexer = new Lexer(new StringReader(input));
		
		assertToken( lexer.next(), TokenType.Int, "954" );
		
		assertToken( lexer.next(), TokenType.Int, "0" );
		
		assertToken( lexer.next(), TokenType.Int, "1" );
		
		assertToken( lexer.next(), TokenType.Real, "0.2" );
		
		assertToken( lexer.next(), TokenType.Real, "1.1" );
		
		assertToken( lexer.next(), TokenType.Real, "2.33" );
		
		assertEquals( lexer.next().type, TokenType.EOF );
	}
	
	public void testSymbolPart() throws IOException
	{
		String input = " asdf fds @#%*fdsa ";
		Lexer lexer = new Lexer(new StringReader(input));
		
		assertToken( lexer.next(), TokenType.SymbolPart, "asdf" );
		
		assertToken( lexer.next(), TokenType.SymbolPart, "fds" );
		
		assertToken( lexer.next(), TokenType.SymbolPart, "@#%*fdsa" );
		
		assertEquals( lexer.next().type, TokenType.EOF );
	}
	
	public void testControl() throws IOException
	{
		String input = " { } ( ) [ ] : . .. ";
		Lexer lexer = new Lexer(new StringReader(input));
		
		assertEquals( lexer.next().type, TokenType.OPEN_BRACE );
		assertEquals( lexer.next().type, TokenType.CLOSE_BRACE );
		
		assertEquals( lexer.next().type, TokenType.OPEN_PAREN );
		assertEquals( lexer.next().type, TokenType.CLOSE_PAREN );
		
		assertEquals( lexer.next().type, TokenType.OPEN_BRACKET );
		assertEquals( lexer.next().type, TokenType.CLOSE_BRACKET );
		
		assertEquals( lexer.next().type, TokenType.COLON );

		assertEquals( lexer.next().type, TokenType.FULL_STOP );
		
		assertEquals( lexer.next().type, TokenType.BETWEEN );
		
		assertEquals( lexer.next().type, TokenType.EOF );
	}
	
	public void testString() throws IOException
	{
		String input = " \"asdf\" \"1234\" ";
		Lexer lexer = new Lexer(new StringReader(input));
		
		assertToken( lexer.next(), TokenType.String, "\"asdf\"" );
		assertToken( lexer.next(), TokenType.String, "\"1234\"" );		
		assertEquals( lexer.next().type, TokenType.EOF );
	}
	
	public void testRangeVsSymbol() throws IOException
	{
		String input = " 2..4 a..b ref1.ref2 ";
		Lexer lexer = new Lexer(new StringReader(input));
		
		assertToken( lexer.next(), TokenType.Int, "2" );
		assertToken( lexer.next(), TokenType.BETWEEN, ".." );
		assertToken( lexer.next(), TokenType.Int, "4" );
		
		assertToken( lexer.next(), TokenType.SymbolPart, "a" );
		assertToken( lexer.next(), TokenType.BETWEEN, ".." );
		assertToken( lexer.next(), TokenType.SymbolPart, "b" );
		
		assertToken( lexer.next(), TokenType.SymbolPart, "ref1.ref2" );
		
		assertEquals( lexer.next().type, TokenType.EOF );
	}
	
	protected void assertToken(Token token, TokenType type, String value)
	{
		assertEquals(type, token.type);
		assertEquals(value, token.value);
	}
}
