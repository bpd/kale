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
	
	private Type type = Type.ANY;
	
	@Override
	public Type inferType(Scope scope)
	{
		// process all binds in the scope first
		for( Exp exp : contents )
		{
			if( exp instanceof Bind )
			{
				Bind bind = (Bind)exp;
				scope.set( bind.symbol.symbol(), bind.exp );
			}
		}
		
		ListType type = new ListType();
		
		for( Exp exp : contents )
		{
			if( !(exp instanceof Bind) )
			{
				type.add( exp.inferType(scope) );
			}
		}
		
		this.type = type;
		return type;
	}
	
	@Override
	public Type getType()
	{
		return type;
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
