package kaygan;

import junit.framework.TestCase;

public class BindTest extends TestCase
{
	protected Object eval(String input)
	{
		Function sequence = BlockReader.eval(input);
		
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
		Object o = eval(" a: 7 b: [ 1 2 3 a ] ");
		
		assertEquals("[ a:7 b:[ 1 2 3 7 ] ]", o.toString());
	}
	
	public void testReadNestedSequence()
	{
		Object o = eval(" a: 7 b: [ 1 2 3 a [8 a] ] ");
		
		assertEquals("[ a:7 b:[ 1 2 3 7 [ 8 7 ] ] ]", o.toString());
	}
	
	public void testReadNestedRebind()
	{
		Object o = eval(" a: 7 b: [ 1 2 3 a a:6 [8 a] ] ");
		
		assertEquals("[ a:7 b:[ 1 2 3 7 a:6 [ 8 6 ] ] ]", o.toString());
	}
	
//	public void testReadFunction()
//	{
//		Object o = eval(" a: ([a b] a + b)  a  ");
//		
//		System.out.println(" o => " + o);
//	}
}
