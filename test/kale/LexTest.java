package kale;

import java.io.IOException;
import java.io.StringReader;

import junit.framework.TestCase;
import kale.Lexer;
import kale.Token;
import kale.TokenType;

public class LexTest extends TestCase
{
	public void testCommentWS() throws IOException
	{
		String input = " /*  */   ";
		Lexer lexer = new Lexer(new StringReader(input));
		
		assertToken( lexer.next(), TokenType.Comment, "/*  */" );
		
		assertEquals( lexer.next().type, TokenType.EOF );	
	}
	
	public void testNumbers() throws IOException
	{
		String input = " 954 0 1 -0 -1 -12 -a -asdf ";
		Lexer lexer = new Lexer(new StringReader(input));
		
		assertToken( lexer.next(), TokenType.Int, "954" );
		
		assertToken( lexer.next(), TokenType.Int, "0" );
		
		assertToken( lexer.next(), TokenType.Int, "1" );
		
		assertToken( lexer.next(), TokenType.Int, "-0" );
		
		assertToken( lexer.next(), TokenType.Int, "-1" );
		
		assertToken( lexer.next(), TokenType.Int, "-12" );
		
		assertToken( lexer.next(), TokenType.Symbol, "-a" );
		
		assertToken( lexer.next(), TokenType.Symbol, "-asdf" );
		
		assertEquals( lexer.next().type, TokenType.EOF );
	}
	
	public void testSymbolPart() throws IOException
	{
		String input = " asdf fds @#%*fdsa ";
		Lexer lexer = new Lexer(new StringReader(input));
		
		assertToken( lexer.next(), TokenType.Symbol, "asdf" );
		
		assertToken( lexer.next(), TokenType.Symbol, "fds" );
		
		assertToken( lexer.next(), TokenType.Symbol, "@#%*fdsa" );
		
		assertEquals( lexer.next().type, TokenType.EOF );
	}
	
	public void testControl() throws IOException
	{
		String input = " { } ( ) [ ] = ";
		Lexer lexer = new Lexer(new StringReader(input));
		
		assertEquals( lexer.next().type, TokenType.OPEN_BRACE );
		assertEquals( lexer.next().type, TokenType.CLOSE_BRACE );
		
		assertEquals( lexer.next().type, TokenType.OPEN_PAREN );
		assertEquals( lexer.next().type, TokenType.CLOSE_PAREN );
		
		assertEquals( lexer.next().type, TokenType.OPEN_BRACKET );
		assertEquals( lexer.next().type, TokenType.CLOSE_BRACKET );
		
		assertEquals( lexer.next().type, TokenType.EQUALS );
		
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
	
	protected void assertToken(Token token, TokenType type, String value)
	{
		assertEquals(type, token.type);
		assertEquals(value, token.value);
	}
}
