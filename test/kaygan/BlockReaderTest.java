package kaygan;

import junit.framework.TestCase;

public class BlockReaderTest extends TestCase
{
	protected Object eval(String input)
	{
		return BlockReader.eval(input);
	}
	
	public void testReadInt()
	{
		assertEquals( 1234, eval(" 1234 ") );
	}
	
	public void testReadReal()
	{
		assertEquals( 1234.2, eval(" 1234.2 ") );
	}
	
	public void testReadBinary()
	{
		assertEquals( 8, eval(" 0b1000 ") );
	}
	
	public void testReadHex()
	{
		assertEquals( 0xaf, eval(" 0xaf ") );
	}
	
	public void testReadSymbol()
	{
		assertEquals( "asdf", eval(" asdf ") );
	}
	
	public void testReadMultipleSymbols()
	{
		Object o = BlockReader.eval(" 1 2 asdf 3 4 fdsa ", new Chain());
		
		System.out.println("o: " + o);
	}
	
	public void testReadNested()
	{
		Object o = eval(" [ 1 (a b) ] ");
		
		System.out.println("o: " + o);
	}
	
	public void testReadFunction()
	{
		Object o = eval(" o: ([a b] a + b ) ");
		
		System.out.println(o);
	}
	
	
	
	
//	
//	public void testReadChain()
//	{
//		
//	}
}
