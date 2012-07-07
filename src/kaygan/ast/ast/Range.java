package kaygan.ast.ast;

import kaygan.ast.Token;

public class Range extends Exp
{
	ASTNode from; // Value | Symbol
	Token between;
	ASTNode to;   // Value | Symbol
	
	public Range(Value from, Token between, Value to)
	{
		this.from = from;
		this.between = between;
		this.to = to;
	}
	
	public Range(Symbol from, Token between, Symbol to)
	{
		this.from = from;
		this.between = between;
		this.to = to;
	}
	
	public Range(Exp from, Token between, Exp to)
	{
		this.from = from;
		this.between = between;
		this.to = to;
	}
	
}
