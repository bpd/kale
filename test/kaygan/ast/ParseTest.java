package kaygan.ast;

import java.io.StringReader;

import junit.framework.TestCase;

public class ParseTest extends TestCase
{
	public void testParse1()
	{
		String input = " asdf \"1234\" ";
		Parser parser = new Parser(new StringReader(input));
		
		System.out.println( parser.program() );
		
	}
	
	public void testParseBind()
	{
		String input = " asdf asdf:1234 ";
		Parser parser = new Parser(new StringReader(input));
		
		System.out.println( parser.program() );
		
	}
}
