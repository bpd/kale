package kaygan;

import java.io.Closeable;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

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
	
	public Cell readCell() throws IOException
	{
		Cell cell = new Cell();
		
		String symbol = readSymbol();
		while( symbol != TOK_END_CELL && symbol != TOK_EOF )
		{
			if( symbol == TOK_BEGIN_CELL )
			{
				return readCell();
			}
			
			if( !( symbol == TOK_END_CELL || symbol == TOK_EOF) )
			{
				if( symbol == TOK_BEGIN_CELL )
				{
					cell.put( cell.size(), readCell() );
				}
				else
				{
					if( symbol.endsWith(COLON) )
					{
						String nextSymbol = readSymbol();
						if( nextSymbol == TOK_BEGIN_CELL )
						{
							cell.put( symbol, readCell() );
						}
						else if( nextSymbol == TOK_END_CELL || symbol == TOK_EOF )
						{
							throw new RuntimeException("Expected value after " + nextSymbol);
						}
						else
						{
							cell.put( symbol, nextSymbol );
						}
					}
					else
					{
						cell.put( cell.size(), symbol );
					}
				}
				
			}
			symbol = readSymbol();
		}
		return cell;
	}
	
	private static final String TOK_END_CELL = "]";
	
	private static final String TOK_BEGIN_CELL = "[";
	
	private static final String COLON = ":";
	
	private static final String TOK_EOF = "";
	
	protected String readSymbol()
	{
		ignoreWhitespace();
		
		int c = read();
		
		if( c == ']' )
		{
			return TOK_END_CELL;
		}
		else if( c == '[' )
		{
			return TOK_BEGIN_CELL;
		}
		else if( c == -1 || c == 65535 )
		{
			return TOK_EOF;
		}
		else
		{
			// read symbol
			StringBuilder atom = new StringBuilder();
			
			while( !Character.isWhitespace(c) && c != 65535 )
			{
				if( c == '[' || c == ']' )
				{
					// we've encountered the next cell structure
					unread(c);
					return atom.toString();
				}
				if( c == ':' )
				{
					// we've encountered the end of the key, so
					// retain the ':' ending char and return the symbol
					atom.append((char)c);
					return atom.toString();
				}
				atom.append((char)c);
				
				c = read();
			}
			return atom.toString();
		}
	}
 
}
