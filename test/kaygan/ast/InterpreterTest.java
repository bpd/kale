package kaygan.ast;

import java.util.List;

import junit.framework.TestCase;

public class InterpreterTest extends TestCase
{
	public void testSimpleBind()
	{
		List<Object> results = Interpreter.interpret(" asdf:1234 asdf ");
		
		assertEquals( 1, results.size() );
		assertEquals( 1234, results.get(0) );
	}
	
	public void testSimpleFunction()
	{
		List<Object> results = Interpreter.interpret(" a: { b c | [ 1 2 b c ] } (a 3 4) ");
		
		//assertEquals( 1, results.size() );
		//assertEquals( 1234, results.get(0) );
		
		System.out.println("results: " + results);
	}
}
