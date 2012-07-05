package kaygan.cell;

import java.io.Closeable;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.StringReader;

import kaygan.atom.Symbol;

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
			else if( Character.isDigit(next) )
			{
				// number literal
				return readNumber();
			}
			else
			{
				error("Unknown number token: " + (char) next );
			}
		}
		else if( isDigit(c) )
		{
			return readNumber();
		}
		else if( isSymbolChar(c) )
		{
			// symbol
			String symbol = readSymbol();
			
			ignoreWhitespace();
			
			int next = peek();
			if( next == ':' )
			{
				read(); // consume ':'
				ignoreWhitespace();
				
				return new Cell(new Bind(symbol), parse());
			}
			else
			{
				return new Symbol(symbol);
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
		int first = read();
		
		if( !isBinaryDigit(first) )
		{
			error("Expected binary digit");
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append( (char) first );
		
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
		
		StringBuilder symbol = new StringBuilder();
		
		if( !isSymbolChar( peek() ) )
		{
			error("Expected letter to begin symbol");
		}
		
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
		Cell second = new Cell(next, null);
		Cell root = new Cell(first, second);
		
		Cell cell = root;
		
		next = reader.parse();
		
		while( next != null )
		{
			Cell nextCell = new Cell(next, null);
			
			cell.right = nextCell;
			cell = nextCell;
			
			next = reader.parse();
		}
		
		return root;
	}

}
