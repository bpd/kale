package kaygan.cell;

import java.io.Closeable;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.StringReader;

import kaygan.Chain;
import kaygan.Function;
import kaygan.Sequence;
import kaygan.atom.Num;
import kaygan.atom.Pair;
import kaygan.atom.Symbol;

public class CellReader implements Closeable
{
	private final PushbackReader reader;
	
	private int line;
	
	private int column;
	
	private int prevColumn;
	
	
	public CellReader(Reader reader)
	{
		this.reader = new PushbackReader(reader, 1);
	}
	
	@Override
	public void close()
	{
		try
		{
			reader.close();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	protected void ignoreWhitespace()
	{
		int c = read();
		while( Character.isWhitespace(c) )
		{
			c = read();
		}
		unread(c);
	}
	
	protected int read()
	{
		try
		{
			int c = reader.read();
			if( c == '\n' )
			{
				line++;
				column = 0;
			}
			column++;
			
			return c;
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	protected void unread(int c)
	{
		try
		{
			if( c == '\n' )
			{
				line--;
				column = prevColumn;
			}
			reader.unread(c);
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	protected Object parse()
	{
		ignoreWhitespace();
		
		int c = read();
		
		if( c == '0' )
		{
			int next = read();
			if( next == 'x' )
			{
				// hex literal
				return readHexNumber();
			}
			else if( next == 'b' )
			{
				// binary literal				
				return readBinaryNumber();
			}
			else if( Character.isDigit(next) )
			{
				// number literal
				return readNumber();
			}
			else
			{
				error("Unknown number token: " + (char) c );
			}
		}
		else if( isDigit(c) )
		{
			unread(c);
			return readNumber();
		}
		else if( isSymbolChar(c) )
		{
			// symbol
			unread(c);
			String symbol = readSymbol();
			
			ignoreWhitespace();
			
			int next = read();
			if( next == ':' )
			{
				ignoreWhitespace();
				
				return new Cell(new Bind(symbol), parse());
			}
			else
			{
				unread(next);
				return new Symbol(symbol);
			}
		}
		else if( c == '[' )
		{
			return readList( BEGIN_SEQUENCE, END_SEQUENCE );
		}
		else if( c == '(' )
		{
			return readList( BEGIN_CHAIN, END_CHAIN );
		}
		else if( c == ']' )
		{
			return END_SEQUENCE;
		}
		else if( c == ')' )
		{
			return END_CHAIN;
		}
		else if( isEOF(c) )
		{
			// TODO eof, null or explicit EOF element?
			return null;
		}
		
		error("Unknown input: " + (char) c);
		
		return null;
	}
	
	private static final Symbol BEGIN_SEQUENCE = new Symbol("[");
	
	private static final Symbol END_SEQUENCE = new Symbol("]");
	
	private static final Symbol BEGIN_CHAIN = new Symbol("(");
	
	private static final Symbol END_CHAIN = new Symbol(")");
	
	protected static boolean isEOF(int c)
	{
		return c == 65535 || c == -1;
	}
	
	protected Object readList( Symbol begin, Symbol end )
	{
		Cell list = new Cell( begin, null );
		
		Cell current = list;
		
		Object next = parse();
		
		while( next != end )
		{
			Cell cell = new Cell(next, null);
			
			current.right = cell;
			current = cell;
			
			next = parse();
		}
		
		return list;
	}
	
//	public Cell evalSequence()
//	{
//		//Sequence sequence = new Sequence();
//		
//		while( true )
//		{
//			Object f = eval();
//			
//			sequence.add( f );
//			
//			// look for the end of the cell
//			ignoreWhitespace();
//			
//			int next = read();
//			if( next == ']'
//				|| isEOF(next) )
//			{
//				// found the end of the cell
//				break;
//			}
//			unread(next);
//		}
//		
//		return sequence;
//	}
	
//	public Function evalChain()
//	{
//		Chain chain = new Chain();
//		
//		while( true )
//		{
//			chain.add( eval() );
//			
//			// look for the end of the cell
//			ignoreWhitespace();
//			
//			int next = read();
//			if( next == ')'
//				|| isEOF(next) )
//			{
//				// found the end of the cell
//				break;
//			}
//			unread(next);
//		}
//		
//		return chain;
//	}
	
	static boolean isHexDigit(int c)
	{
		return (c >= 'a' && c <= 'z')
			|| (c >= 'A' && c <= 'Z')
			|| (c >= '0' && c <= '9');
	}
	
	protected Number readHexNumber()
	{
		int first = read();
		
		if( !isHexDigit(first) )
		{
			error("Expected hex digit");
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append( (char) first );
		
		int c = read();
		
		while( isHexDigit(c) )
		{
			sb.append( (char) c );
			c = read();
		}
		
		unread(c);
		
		Integer i = 0;
		try
		{
			i = Integer.parseInt(sb.toString(), 16);
		}
		catch(NumberFormatException e)
		{
			error("Bad hex value: " + e.getMessage());
		}
		return i;
	}
	
	static boolean isBinaryDigit(int c)
	{
		return c == '0' || c == '1';
	}
	
	protected Number readBinaryNumber()
	{
		int first = read();
		
		if( !isBinaryDigit(first) )
		{
			error("Expected binary digit");
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append( (char) first );
		
		int c = read();
		
		while( isBinaryDigit(c) )
		{
			sb.append( (char) c );
			c = read();
		}
		unread(c);
		
		Integer i = 0;
		try
		{
			i = Integer.parseInt(sb.toString(), 2);
		}
		catch(NumberFormatException e)
		{
			error("Bad binary value: " + e.getMessage());
		}
		return i;
	}
	
	static boolean isDigit(int c)
	{
		// TODO further restrict this from more general
		//      unicode interpretation?
		
		return Character.isDigit(c);
	}
	
	protected Number readNumber()
	{
		
		int c = read();
		
		if( !isDigit(c) )
		{
			error("Expected digit");
		}
		
		StringBuilder sb = new StringBuilder();
		
		boolean real = false;
		
		do
		{
			sb.append( (char) c );
			c = read();
			if( c == '.' )
			{
				real = true;
				sb.append( (char) c);
				c = read();
			}
		}
		while( isDigit(c) );
		
		unread(c);
		
		Number i = 0;
		try
		{
			if( real )
			{
				i = Double.parseDouble(sb.toString());
			}
			else
			{
				i = Integer.parseInt(sb.toString());
			}
		}
		catch(NumberFormatException e)
		{
			error("Bad number value: " + e.getMessage());
		}
		return i;
	}
	
	protected void error(String message)
	{
		// TODO: tag line number, column, etc
		
		throw new RuntimeException(message);
	}
	
	
	static boolean isSymbolChar(int c)
	{
		return !(  c == ':'
				|| c == '['
				|| c == ']'
				|| c == '('
				|| c == ')'
				|| isEOF(c) )
				&& !Character.isWhitespace(c);
		
	}
	
	
	protected String readSymbol()
	{
		ignoreWhitespace();
		
		StringBuilder symbol = new StringBuilder();
		
		int c = read();
		
		if( !isSymbolChar(c) )
		{
			error("Expected letter to begin symbol");
		}
		
		symbol.append( (char) c );
		
		c = read();
		while(	isSymbolChar(c) )
		{
			symbol.append( (char) c );
			c = read();
		}
		unread(c);
		
		return symbol.toString();
	}
	
	public static Object parse(String input)
	{
		final CellReader reader = new CellReader(new StringReader(input));
		
//		int resultCount = 0;
		
		
		return reader.parse();
		
//		Sequence sequence = new Sequence();
//		
//		Function f = reader.eval();
//		while( f != null )
//		{
//			sequence.add(f);
//			
//			resultCount++;
//			
//			Function next = reader.eval();
//			if( next == null )
//			{
//				if( resultCount == 1 )
//				{
//					return f;
//				}
//			}
//			f = next;
//		}
//
//		return sequence;		
	}

}
