package kaygan.ast.ast;

import kaygan.ast.Token;

public class Num extends Value
{
	public final Token token;
	
	public Num(Token token)
	{
		this.token = token;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("{Num: ").append(token).append('}');
		return sb.toString();
	}
}
