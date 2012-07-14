package kaygan.ast;

import kaygan.Scope;
import kaygan.Token;
import kaygan.type.FunctionType;
import kaygan.type.Type;

public class Symbol extends Exp
{
	public final Token symbol;
	
	/** the ASTNode this symbol references */
	public ASTNode ref;
	
	public Symbol(Token symbol)
	{
		this.symbol = symbol;
	}
	
	public String symbol()
	{
		return symbol.value;
	}
	
	
	@Override
	public int getOffset()
	{
		return symbol.beginOffset;
	}

	@Override
	public int getLength()
	{
		return symbol.endOffset - symbol.beginOffset;
	}
	
	@Override
	public ASTNode findNode(int offset)
	{
		return overlaps(offset) ? this : null;
	}
	
	@Override
	public void link(Scope scope)
	{
		Object o = scope.get( this.symbol() );
		if( o != null && o instanceof ASTNode )
		{
			this.ref = (ASTNode)o;
		}
		else
		{
			this.error("Unknown reference");
			this.ref = Type.ERROR;
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
		
		this.ref.inferType();
		
		if( this.ref instanceof Type )
		{
			this.type = (Type)this.ref;
		}
		else if( this.ref instanceof Function )
		{
			if( this.ref.type instanceof FunctionType )
			{
				FunctionType refType = (FunctionType)this.ref.type;
				this.type = refType;//.getRetType();
			}
			else
			{
				this.error("Expected function to have function type");
				this.type = Type.ERROR;
			}
		}
		else
		{
			this.type = this.ref.type;
		}
		
		return this.type;
	}
	
	
	@Override
	public int hashCode()
	{
		return symbol.value.hashCode();
	}
	
	@Override
	public boolean equals(Object o )
	{
		if( o instanceof Symbol )
		{
			return ((Symbol)o).symbol.value.equals(this.symbol.value);
		}
		return false;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append('{');
		sb.append("Symbol: ");
		sb.append(symbol);
		sb.append('}');
		return sb.toString();
	}
}
