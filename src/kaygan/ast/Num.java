package kaygan.ast;

import kaygan.Token;

public class Num extends Value
{
	public final Token token;
	
	public Num(Token token)
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
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("{Num: ").append(token).append('}');
		return sb.toString();
	}
}
