package kaygan.ast;

import kaygan.Token;
import kaygan.type.NamedType;
import kaygan.type.Type;

public class Num extends Value
{
	public static final Type TYPE = new NamedType("Num");
	
	public final Token token;
	
	public Num(Token token)
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
		sb.append("{Num: ").append(token).append('}');
		return sb.toString();
	}
}
