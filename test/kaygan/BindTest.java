package kaygan;

import junit.framework.TestCase;

public class BindTest extends TestCase
{
	protected Object eval(String input)
	{
		Sequence sequence = BlockReader.eval(input);
		sequence.bind();
		return sequence.eval();
	}
	
	public void testReadInt()
	{
		Object o = eval(" 1234 ");
		
		assertEquals("[ 1234 ]", o.toString());
	}
	
	public void testReadBind()
	{
		Object o = eval(" a: 5678  a ");
		
		assertEquals("[ a:5678 5678 ]", o.toString());
	}
	
	public void testReadSequence()
	{
		Object o = eval(" a: 7 b: [ 1 2 3 a ] b ");
		
		assertEquals("[ [ 1 2 3 7 ] ]", o.toString());
	}
	
	public void testReadNestedSequence()
	{
		Object o = eval(" a: 7 b: [ 1 2 3 a [8 a] ] ");
		
		assertEquals("[ a:7 b:[ 1 2 3 7 [ 8 7 ] ] ]", o.toString());
	}
	
	public void testReadNestedRebind()
	{
		Object o = eval(" a: 7 b: [ 1 2 3 a a:6 [8 a] ] ");
		
		assertEquals("[ a:7 b:[ 1 2 3 6 a:6 [ 8 6 ] ] ]", o.toString());
	}
	
	public void testExchange()
	{
		Object o = eval(" a: 7 b:8 [ b:a a:b ] ");
		
		System.out.println("o: " + o);
	}
	
//	public void testReadFunction()
//	{
//		Object o = eval(" a: ([a b] a + b)  a  ");
//		
//		System.out.println(" o => " + o);
//	}
}
