package kaygan.cell;

import java.io.Closeable;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.StringReader;

public class CellReader implements Closeable
{
	private final PushbackReader reader;
	
	private int line;
	
	private int column;
	
	
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
		while( Character.isWhitespace( peek() ) )
		{
			read();
		}
	}
	
	protected int peek()
	{
		try
		{
			int peekChar = reader.read();
			
			reader.unread( peekChar );

			return peekChar;
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
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
	
	protected Object parse()
	{
		Object cell = parseCell();
		
		ignoreWhitespace();
		
		if( peek() == ':' )
		{
			read(); // consume ':'
			
			// left cell is being bound to right cell
			return new Cell( cell, parseCell() );
		}
		else
		{
			return cell;
		}
	}
	
	protected Object parseCell()
	{
		ignoreWhitespace();
		
		int c = peek();
		
		if( c == '0' )
		{
			read(); // consume '0'
			
			int next = peek();
			if( next == 'x' )
			{
				read(); // consume 'x'
				// hex literal
				return readHexNumber();
			}
			else if( next == 'b' )
			{
				read(); // consume 'b'
				// binary literal				
				return readBinaryNumber();
			}
			else if( isDigit(next) )
			{
				// number literal
				return readNumber();
			}
			else
			{
				// zero followed by something that's not a digit,
				// so just make it a zero and defer to the next pass
				return new Integer(0);
			}
		}
		else if( c == '[' )
		{
			read();
			return readList( BEGIN_SEQUENCE, END_SEQUENCE );
		}
		else if( c == '(' )
		{
			read();
			return readList( BEGIN_CHAIN, END_CHAIN );
		}
		else if( c == ']' )
		{
			read();
			return END_SEQUENCE;
		}
		else if( c == ')' )
		{
			read();
			return END_CHAIN;
		}
		else if( isDigit(c) )
		{
			return readNumber();
		}
		else if( isSymbolChar(c) )
		{
			return readSymbol();
		}
		else if( isEOF(c) )
		{
			// TODO eof, null or explicit EOF element?
			return null;
		}
		
		error("Unknown input: " + (char) c);
		
		return null;
	}
	
	private static final String BEGIN_SEQUENCE = "[";
	
	private static final String END_SEQUENCE = "]";
	
	private static final String BEGIN_CHAIN = "(";
	
	private static final String END_CHAIN = ")";
	
	protected static boolean isEOF(int c)
	{
		return c == 65535 || c == -1;
	}
	
	protected Object readList( String begin, String end )
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
	
	static boolean isHexDigit(int c)
	{
		return (c >= 'a' && c <= 'z')
			|| (c >= 'A' && c <= 'Z')
			|| (c >= '0' && c <= '9');
	}
	
	protected Number readHexNumber()
	{
		if( !isHexDigit( peek() ) )
		{
			error("Expected hex digit");
		}
		
		StringBuilder sb = new StringBuilder();
		
		while( isHexDigit( peek() ) )
		{
			sb.append( (char) read() );
		}
		
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
		if( !isBinaryDigit( peek() ) )
		{
			error("Expected binary digit");
		}
		
		StringBuilder sb = new StringBuilder();
		
		while( isBinaryDigit( peek() ) )
		{
			sb.append( (char) read() );
		}
		
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
		StringBuilder sb = new StringBuilder();
		
		boolean real = false;
		
		while( isDigit( peek() ) )
		{
			sb.append( (char) read() );

			if( peek() == '.' )
			{
				real = true;
				sb.append( (char) read());
			}
		}
		
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
		
		if( !isSymbolChar( peek() ) )
		{
			error("Expected letter to begin symbol");
		}
		
		StringBuilder symbol = new StringBuilder();
		
		while(	isSymbolChar( peek() ) )
		{
			symbol.append( (char) read() );
		}
		
		return symbol.toString();
	}
	
	public static Object parse(String input)
	{
		final CellReader reader = new CellReader(new StringReader(input));
		
		Object first = reader.parse();
		
		Object next = reader.parse();
		if( next == null )
		{
			// no additional inputs, just return the parsed value
			return first;
		}

		// there are multiple inputs, so built a list
		Cell cell = new Cell(next, null);
		Cell root = new Cell(first, cell);
		
		while( (next = reader.parse()) != null )
		{
			cell.right = cell = new Cell(next, null);
		}
		
		return root;
	}

}
