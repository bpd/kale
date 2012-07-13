package kaygan.ast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaygan.Scope;
import kaygan.Token;

public class Function extends Exp
{
	public final Token open;
	public final Token close;
	
	public final List<Exp> args;
	
	public final List<Exp> contents;
	
	// TODO this really needs to be moved somewhere else, after
	//      the interpreter is more fleshed out
	public Scope scope;
	
	public Function(Token open, Token close, List<Exp> args, List<Exp> contents)
	{
		this.open = open;
		this.close = close;
		this.args = args;
		this.contents = contents;
	}
	
	@Override
	public int getOffset()
	{
		return open.beginOffset;
	}

	@Override
	public int getLength()
	{
		return close.endOffset - open.beginOffset;
	}
	
	@Override
	public ASTNode findNode(int offset)
	{
		for( Exp exp : args )
		{
			if( exp.overlaps(offset) )
			{
				return exp.findNode(offset);
			}
		}
		for( Exp exp : contents )
		{
			if( exp.overlaps(offset) )
			{
				return exp.findNode(offset);
			}
		}
		return overlaps(offset) ? this : null;
	}
	
	@Override
	public void verify()
	{
		final Map<String, Symbol> argNames = new HashMap<String, Symbol>();
		
		for( Exp arg : args )
		{
			Symbol symbol = null;
			if( arg instanceof Bind )
			{
				Bind bind = (Bind)arg;
				symbol = bind.symbol;
			}
			else if( arg instanceof Symbol )
			{
				symbol = (Symbol)arg;
			}
			
			// verify the symbol hasn't already been used for an
			// argument in this function
			if( symbol != null )
			{
				String key = symbol.symbol();
				
				if( argNames.containsKey(key) )
				{
					symbol.error( "Duplicate argument name" );
				}
				else
				{
					argNames.put( key, symbol );
				}
			}
		}
		
		for( Exp e : contents )
		{
			e.verify();
		}
	}


	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("{Function: ");
		sb.append("args:").append(args);
		sb.append(' ').append(" contents:").append(contents);
		sb.append('}');
		return sb.toString();
	}
	
}
