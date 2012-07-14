package kaygan.ast;

import kaygan.Token;
import kaygan.type.NamedType;
import kaygan.type.Type;

public class Str extends Value
{
	public static final Type TYPE = new NamedType("String");
	
	public final Token token;
	
	public Str(Token token)
	{
		this.token = token;
		this.type = TYPE;
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

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("{Str: ").append(token).append('}');
		return sb.toString();
	}
}
