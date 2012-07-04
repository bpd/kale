package kaygan;

import junit.framework.TestCase;

public class BindTest extends TestCase
{
	protected Function eval(String input)
	{
		Function f = BlockReader.eval(input);
		f = f.bindTo(null);
		return f.eval();
	}
	
	public void testReadInt()
	{
		Object o = eval(" 1234 ");
		
		assertEquals("1234", o.toString());
	}
	
	public void testReadBind()
	{
		Object o = eval(" a: 5678  a ");
		
		assertEquals("[ a:5678 5678 ]", o.toString());
	}
	
	public void testReadSequence()
	{
		Object o = eval(" a: 7 b: [ 1 2 3 a ] b ");
		
		assertEquals("[ a:7 b:[ 1 2 3 7 ] [ 1 2 3 7 ] ]", o.toString());
	}
	
	public void testReadNestedSequence()
	{
		Object o = eval(" a: 7 b: [ 1 2 3 a [8 a] ] b ");
		
		assertEquals("[ a:7 b:[ 1 2 3 7 [ 8 7 ] ] [ 1 2 3 7 [ 8 7 ] ] ]", o.toString());
	}
	
	public void testReadNestedRebind()
	{
		Function o = eval(" a: 7 b: [ 1 2 3 a a:6 [8 a] ] b ");
		
		System.out.println("o.type: " + o.getType());

		assertEquals("[ a:7 b:[ 1 2 3 7 a:6 [ 8 6 ] ] [ 1 2 3 7 a:6 [ 8 6 ] ] ]", o.toString());
	}
	
	public void testReadAdjacentBind()
	{
		Function o = eval(" b:[ a:6 a ] b ");
		
		System.out.println("o.type: " + o.getType());
		
		System.out.println("o: " + o.toString());
		
		//assertEquals("[ 1 2 3 7 [ 8 6 ] ]", o.toString());
	}
	
//	public void testExchange()
//	{
//		Object o = eval(" a: 7 b:8 [ b:a a:b ] ");
//		
//		System.out.println("o: " + o);
//	}
	
//	public void testReadFunction()
//	{
//		Object o = eval(" a: ([a b] a + b)  a  ");
//		
//		System.out.println(" o => " + o);
//	}
}
