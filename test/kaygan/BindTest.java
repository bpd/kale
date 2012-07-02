package kaygan;

import junit.framework.TestCase;

public class BindTest extends TestCase
{
	protected Object eval(String input)
	{
		return BlockReader.eval(input).eval(new Scope());
	}
	
	public void testReadInt()
	{
		Object o = eval(" 1234 ");
		
		assertEquals("( 1234 )", o.toString());
	}
	
	public void testReadBind()
	{
		Object o = eval(" a: 1234  a ");
		
		System.out.println(" o => " + o);
	}
}
