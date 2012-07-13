package kaygan.ast;

import kaygan.Scope;
import kaygan.Token;
import kaygan.type.NamedType;
import kaygan.type.Type;

public class Str extends Value
{
	public final Token token;
	
	public Str(Token token)
	{
		this.token = token;
	}
	
	@Override
	public int getOffset()
	{
		return token.beginOffset;
	}

	@Override
	public int getLength()
	{
		return token.endOffset - token.beginOffset;
	}
	
	@Override
	public ASTNode findNode(int offset)
	{
		return overlaps(offset) ? this : null;
	}
	
	private static final Type TYPE = new NamedType("String");
	
	@Override
	public Type inferType(Scope scope)
	{
		return TYPE;
	}
	
	@Override
	public Type getType()
	{
		return TYPE;
	}


	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("{Str: ").append(token).append('}');
		return sb.toString();
	}
}
