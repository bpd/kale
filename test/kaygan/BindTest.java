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
		
		System.out.println(" o => " + o);
	}
	
	public void testReadFunction()
	{
		Object o = eval(" a: ([a b] a + b)  a  ");
		
		System.out.println(" o => " + o);
	}
}
