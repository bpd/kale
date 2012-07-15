package kaygan.ast;

import java.util.Arrays;

import kaygan.Scope;
import kaygan.Token;
import kaygan.type.ListType;
import kaygan.type.Type;

public class Array extends Block
{
	public final Token open;
	public final Token close;
	
	public Array(Token open, Token close, Exp[] exps)
	{
		super(exps);
		
		this.open = open;
		this.close = close;
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
	public void link(Scope scope)
	{
		for( Exp e : exps )
		{
			e.link(scope);
		}
	}
	
	@Override
	public Type inferType()
	{
		super.inferType();
		
		if( this.type != null )
		{
			return this.type;
		}
		
		ListType type = new ListType();
		
		for( Exp exp : this.exps )
		{
			if( !(exp instanceof Bind) )
			{
				exp.inferType();
				type.add( exp.type );
			}
		}
		
		this.type = type;
		return this.type;
	}

	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("{Array: ");
		sb.append(" contents:").append( Arrays.toString(exps) );
		sb.append('}');
		return sb.toString();
	}
}
