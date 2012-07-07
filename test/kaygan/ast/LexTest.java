package kaygan.ast;

import java.io.IOException;
import java.io.StringReader;

import junit.framework.TestCase;

public class LexTest extends TestCase
{
	public void testCommentWS() throws IOException
	{
		String input = " /*  */  0b01010 0x54ab7c0d8e8f ";
		Lexer lexer = new Lexer(new StringReader(input));
		
		assertEquals( lexer.next().type, TokenType.WS );
		assertToken( lexer.next(), TokenType.Comment, "/*  */" );
		
		assertEquals( lexer.next().type, TokenType.WS );
		assertToken( lexer.next(), TokenType.Binary, "0b01010" );
		
		assertEquals( lexer.next().type, TokenType.WS );
		assertToken( lexer.next(), TokenType.Hex, "0x54ab7c0d8e8f" );
		
		assertEquals( lexer.next().type, TokenType.WS );
		
		assertEquals( lexer.next().type, TokenType.EOF );	
	}
	
	public void testNumbers() throws IOException
	{
		String input = " 954 0 1 0.2 1.1 2.33 ";
		Lexer lexer = new Lexer(new StringReader(input));
		
		assertEquals( lexer.next().type, TokenType.WS );
		assertToken( lexer.next(), TokenType.Int, "954" );
		
		assertEquals( lexer.next().type, TokenType.WS );
		assertToken( lexer.next(), TokenType.Int, "0" );
		
		assertEquals( lexer.next().type, TokenType.WS );
		assertToken( lexer.next(), TokenType.Int, "1" );
		
		assertEquals( lexer.next().type, TokenType.WS );
		assertToken( lexer.next(), TokenType.Real, "0.2" );
		
		assertEquals( lexer.next().type, TokenType.WS );
		assertToken( lexer.next(), TokenType.Real, "1.1" );
		
		assertEquals( lexer.next().type, TokenType.WS );
		assertToken( lexer.next(), TokenType.Real, "2.33" );
		
		assertEquals( lexer.next().type, TokenType.WS );
		
		assertEquals( lexer.next().type, TokenType.EOF );
	}
	
	protected void assertToken(Token token, TokenType type, String value)
	{
		assertEquals(type, token.type);
		assertEquals(value, token.value);
	}
}
