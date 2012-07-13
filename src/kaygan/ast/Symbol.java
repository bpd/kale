package kaygan.ast;

import kaygan.Scope;
import kaygan.Token;
import kaygan.type.Type;

public class Symbol extends Exp
{
	public final Token symbol;
	
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
	
	private Type type = Type.ANY;
	
	@Override
	public Type inferType(Scope scope)
	{
		Object o = scope.get( this.symbol() );
		if( o != null 
			&& o instanceof ASTNode )
		{
			type = ((ASTNode)o).inferType(scope);
		}
		return type;
	}
	
	@Override
	public Type getType()
	{
		return type;
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
