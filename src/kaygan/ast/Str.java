package kaygan.ast;

import kaygan.Token;

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
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("{Str: ").append(token).append('}');
		return sb.toString();
	}
}
