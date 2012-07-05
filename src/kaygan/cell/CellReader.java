package kaygan.cell;

import java.io.Closeable;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.StringReader;

public class CellReader implements Closeable
{
	private final PushbackReader reader;
	
	int line;
	
	int column;
	
	
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
	
	protected Cell parse()
	{
		Cell cell = parseCell();
		
		ignoreWhitespace();
		
		if( peek() == ':' )
		{
			read(); // consume ':'
			
			// left cell is being bound to right cell
			return new Cons( cell, parseCell() );
		}
		else
		{
			return cell;
		}
	}
	
	protected Cell parseCell()
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
				return new Atom(new Integer(0), Atom.Num);
			}
		}
		else if( c == '[' )
		{
			read();
			return readList( Atom.Sequence, END_SEQUENCE );
		}
		else if( c == '(' )
		{
			read();
			return readList( Atom.Chain, END_CHAIN );
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
			return Atom.Nil;
		}
		
		error("Unknown input: " + (char) c);
		
		return null;
	}
	
	//private static final Atom BEGIN_SEQUENCE = new Atom("[", Atom.Symbol);
	
	private static final Atom END_SEQUENCE = new Atom("]", Atom.Symbol);
	
	//private static final Atom BEGIN_CHAIN = new Atom("(", Atom.Symbol);
	
	private static final Atom END_CHAIN = new Atom(")", Atom.Symbol);
	
	protected static boolean isEOF(int c)
	{
		return c == 65535 || c == -1;
	}
	
	protected Cell readList( Cell type, Atom end )
	{
		Cell next = parse();
		
		Cons list, current;
		current = list = new Cons(next, Atom.Nil);
		
		next = parse();
		
		while( next != end )
		{
			Cons cell = new Cons(next, Atom.Nil);
			
			current.right = cell;
			current = cell;
			
			next = parse();
		}
		
		list.setType(type);
		
		System.out.println("list: " + list.toCellString());
		
		return list;
	}
	
	static boolean isHexDigit(int c)
	{
		return (c >= 'a' && c <= 'z')
			|| (c >= 'A' && c <= 'Z')
			|| (c >= '0' && c <= '9');
	}
	
	protected Atom readHexNumber()
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
		return new Atom(i, Atom.Num);
	}
	
	static boolean isBinaryDigit(int c)
	{
		return c == '0' || c == '1';
	}
	
	protected Atom readBinaryNumber()
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
		return new Atom(i, Atom.Num);
	}
	
	static boolean isDigit(int c)
	{
		// TODO further restrict this from more general
		//      unicode interpretation?
		
		return Character.isDigit(c);
	}
	
	protected Atom readNumber()
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
		return new Atom(i, Atom.Num);
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
	
	
	protected Atom readSymbol()
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
		
		return new Atom(symbol.toString(), Atom.Symbol);
	}
	
	public static Object parse(String input)
	{
		final CellReader reader = new CellReader(new StringReader(input));
		
		Cell first = reader.parse();
		
		Cell next = reader.parse();
		if( next == Atom.Nil )
		{
			// no additional inputs, just return the parsed value
			return first;
		}

		// there are multiple inputs, so built a list
		Cons cell = new Cons(next, Atom.Nil);
		Cons root = new Cons(first, cell);
		
		while( (next = reader.parse()) != Atom.Nil )
		{
			cell.right = cell = new Cons(next, Atom.Nil);
		}
		
		return root;
	}

}
