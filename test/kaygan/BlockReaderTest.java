package kaygan;

import junit.framework.TestCase;
import kaygan.BlockReader;
import kaygan.Function;
import kaygan.atom.Num;

public class BlockReaderTest extends TestCase
{
	protected Function eval(String input)
	{
		return BlockReader.eval(input);
	}
	
	public void testReadInt()
	{
		assertEquals( new Num(1234), eval(" 1234 ") );
	}
	
	public void testReadReal()
	{
		assertEquals( new Num(1234.2), eval(" 1234.2 ") );
	}
	
	public void testReadBinary()
	{
		assertEquals( new Num(8), eval(" 0b1000 ") );
	}
	
	public void testReadHex()
	{
		assertEquals( new Num(0xaf), eval(" 0xaf ") );
	}
	
//	public void testReadMultipleSymbols()
//	{
//		//Object o = BlockReader.eval(" 1 2 asdf 3 4 fdsa ", new Chain());
//		
//		System.out.println("o: " + o);
//	}
	
//	public void testReadNested()
//	{
//		Object o = eval(" [ 1 (a b) ] ");
//		
//		System.out.println("o: " + o);
//	}
//	
//	public void testReadFunction()
//	{
//		Object o = eval(" o: ([a b] a + b ) ");
//		
//		System.out.println(o);
//	}
	
	
	
	
//	
//	public void testReadChain()
//	{
//		
//	}
}
