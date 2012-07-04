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
	
//	protected Function eval()
//	{
//		return this.eval((Function)null);
//	}
	
	protected Function eval(Function parent)
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
				
				Function cell = eval(parent);
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
			return evalSequence(parent);
		}
		else if( c == '(' )
		{
			return evalChain();
		}
		else if( isEOF(c) )
		{
			// TODO eof, null or explicit EOF element?
			return null;
		}
		
		error("Unknown input: " + (char) c);
		
		return null;
	}
	
	protected static boolean isEOF(int c)
	{
		return c == 65535 || c == -1;
	}
	
	public Sequence evalSequence(Function parent)
	{
		Sequence sequence = new Sequence();
		sequence.setParent(parent);
		
		while( true )
		{
			Function f = eval(sequence);
			
			sequence.add( sequence.bind( f ) );
			
			// look for the end of the cell
			ignoreWhitespace();
			
			int next = read();
			if( next == ']'
				|| isEOF(next) )
			{
				// found the end of the cell
				break;
			}
			unread(next);
		}
		
		return sequence;
	}
	
	public Function evalChain()
	{
		Chain chain = new Chain();
		
		while( true )
		{
			// TODO parse chain
			//chain.add( eval() );
			
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
		
		return chain;
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
	
	public static Function eval(String input)
	{
		return eval(input, new Sequence() );
	}
	
	public static Function eval(String input, Sequence sequence)
	{
		final BlockReader reader = new BlockReader(new StringReader(input));
		
		int resultCount = 0;
		
		Function f = reader.eval(sequence);
		while( f != null )
		{
			f = sequence.bind(f);
			
			sequence.add(f);
			
			resultCount++;
			
			Function next = reader.eval(sequence);
			if( next == null )
			{
				if( resultCount == 1 )
				{
					return f;
				}
			}
			f = next;
		}

		return sequence;		
	}

}
