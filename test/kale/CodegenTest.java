package kale;

import java.io.InputStreamReader;
import java.io.StringReader;

import junit.framework.TestCase;
import kale.Parser;
import kale.ast.CompilationUnit;

public class CodegenTest extends TestCase
{
	public void testBasicReturn()
	{
		Parser parser = new Parser(
				new InputStreamReader(
					ParserTest.class.getResourceAsStream("function.kl")) );
			
		CompilationUnit unit = parser.parse();
			
		Object result = unit.execute();

		assertTrue( "Hello".equals(result) );
	}
	
	public void testIf()
	{
		{
		String if1 = "package asdf main() string { " 
					+ "if true { return \"true1\"; } " 
					+ "return \"false1\"; } ";

		Object result = new Parser( new StringReader(if1) ).parse().execute();
			
		assertTrue( "true1".equals(result) );
		}
		
		{
		String if1 = "package asdf main() string { " 
					+ "if false { return \"true1\"; } " 
					+ "return \"false1\"; } ";

		Object result = new Parser( new StringReader(if1) ).parse().execute();
			
		assertTrue( "false1".equals(result) );
		}
		
		{
		String if1 = "package asdf main() string { " 
					+ "if false { return \"true1\"; } "
					+ "else if true { return \"true2\"; } "
					+ "return \"false1\"; } ";

		Object result = new Parser( new StringReader(if1) ).parse().execute();
			
		assertTrue( "true2".equals(result) );
		}
		
		{
		String if1 = "package asdf main() string { " 
					+ "if false { return \"true1\"; } "
					+ "else if false { return \"true2\"; } "
					+ "else { return \"false1\"; } } ";

		Object result = new Parser( new StringReader(if1) ).parse().execute();
			
		assertTrue( "false1".equals(result) );
		}
		
	}
}
