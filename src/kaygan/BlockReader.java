package kaygan;

import java.io.Closeable;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.StringReader;

import kaygan.atom.Num;
import kaygan.atom.Pair;
import kaygan.atom.Symbol;

public class BlockReader implements Closeable
{
	private final PushbackReader reader;
	
	private int line;
	
	private int column;
	
	private int prevColumn;
	
	
	public BlockReader(Reader reader)
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
	
	public Function eval( Sequence parent )
	{
		ignoreWhitespace();
		
		int c = read();
		
		if( c == '0' )
		{
			int next = read();
			if( next == 'x' )
			{
				// hex literal
				return new Num( readHexNumber() );
			}
			else if( next == 'b' )
			{
				// binary literal				
				return new Num( readBinaryNumber() );
			}
			else if( Character.isDigit(next) )
			{
				// number literal
				return new Num( readNumber() );
			}
			else
			{
				error("Unknown number token: " + (char) c );
			}
		}
		else if( isDigit(c) )
		{
			unread(c);
			return new Num( readNumber() );
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
				
				Function cell = eval( parent );
				if( cell == null )
				{
					error("Expected value after " + symbol + ":");
				}
				
				return new Pair(symbol, cell);
			}
			else
			{
				unread(next);
				return new Symbol(symbol);
			}
		}
		else if( c == '[' )
		{
			return evalSequence( new Sequence( parent ) );
		}
		else if( c == '(' )
		{
			return evalChain( new Chain( parent ) );
		}
		else if( c == 65535 )
		{
			// TODO eof, null or explicit EOF element?
			return null;
		}
		
		error("Unknown input: " + (char) c);
		
		return null;
	}
	
	protected boolean isEOF(int c)
	{
		return c == 65535;
	}
	
	public Function evalSequence(Sequence sequence)
	{
		Function previous = sequence;
		
		while( true )
		{
			Function f = eval( sequence );
			
			previous = previous.bind(f);
			
			// look for the end of the cell
			ignoreWhitespace();
			
			int next = read();
			if( next == ']'
				|| next == ')'
				|| isEOF(next) )
			{
				// found the end of the cell
				break;
			}
			unread(next);
		}
		
		return previous;
	}
	
	public Function evalChain(Chain chain)
	{
		Function current = chain;
		
		while( true )
		{
			current = current.bind( eval( chain ) );
			
			// look for the end of the cell
			ignoreWhitespace();
			
			int next = read();
			if( next == ')'
				|| isEOF(next) )
			{
				// found the end of the cell
				break;
			}
			unread(next);
		}
		
		return current;
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
				|| c == ')')
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
	
	public static Function eval(String input)
	{
		return eval( input, new Sequence() );
	}
	
	/**
	 * By default, the default scope is a chain.
	 * 
	 * That way, at a REPL when someone types '1 + 2'
	 *   it will function as expected
	 * 
	 * @param input
	 * @param sequence
	 * @return
	 */
	public static Function eval(String input, Sequence sequence)
	{
		return new BlockReader(new StringReader(input)).evalSequence(sequence);
	}

}
