package kaygan.ast;

import kaygan.Scope;
import kaygan.type.Type;

public class Bind extends Exp
{
	public final Symbol symbol;
	
	public final Exp exp;
	
	public Bind(Symbol symbol, Exp exp)
	{
		this.symbol = symbol;
		this.exp = exp;
	}
	
	
	
	@Override
	public int getOffset()
	{
		return symbol.getOffset();
	}

	@Override
	public int getLength()
	{
		return exp.getOffset() - symbol.getOffset() + exp.getLength();
	}

	@Override
	public ASTNode findNode(int offset)
	{
		// if this node has errors, we want
		// it to 'cover' its contents
		if( hasErrors() && overlaps(offset) )
		{
			return this;
		}
		
		if( symbol.overlaps(offset) )
		{
			return symbol;
		}
		
		if( exp.overlaps(offset) )
		{
			return exp.findNode(offset);
		}
		
		return overlaps(offset) ? this : null;
	}
	
	@Override
	public void link(Scope scope)
	{
		if( this.exp instanceof Bind )
		{
			this.exp.error("Cannot bind to a bind");
		}
		else
		{
			// link the expression before we set the scope
			// that way it is not seeing its own value
			this.exp.link( scope );
			
			String key = this.symbol.symbol();
			
			Object bound = scope.getLocal(key);
			
			if( bound != null )
			{
				this.error("Symbol " + key + " already bound to " + bound);
			}
			else
			{
				scope.set( key, this.exp );
			}
			
			this.symbol.ref = this.exp;
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
		
		exp.inferType();
		
		this.type = this.symbol.type = this.exp.type;
		return this.type;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("{ Bind: ")
			.append(symbol).append(':').append(exp).append('}');
		return sb.toString();
	}
}
