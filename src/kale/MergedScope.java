package kale;

import kale.ast.ASTNode;

public class MergedScope extends Scope
{
	final Scope lexical;
	
	public MergedScope( Scope parent, Scope lexical )
	{
		super(parent);
		
		this.lexical = lexical;
	}
	
	@Override
	public ASTNode get(String symbol)
	{
		ASTNode node = super.get(symbol);
		if( node == null )
		{
			node = lexical.get(symbol);
		}
		return node;
	}
	
}
