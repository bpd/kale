package kaygan.ast;

import java.util.List;

import kaygan.Scope;
import kaygan.Token;
import kaygan.type.ListType;
import kaygan.type.Type;

public class Array extends Exp
{
	public final Token open;
	public final Token close;
	
	public final List<Exp> contents;
	
	public Array(Token open, Token close, List<Exp> contents)
	{
		this.open = open;
		this.close = close;
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
	public void link(Scope scope)
	{
		for( Exp e : contents )
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
		
		for( Exp exp : this.contents )
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
		sb.append(" contents:").append(contents);
		sb.append('}');
		return sb.toString();
	}
}
