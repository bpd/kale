package kaygan.type;

import kaygan.Scope;
import kaygan.ast.ASTNode;

public abstract class Type extends ASTNode
{
	public static final Type TYPE = new NamedType("Type");
	
	public static final Type ANY = new NamedType("Any");
	
	public static final Type ERROR = new NamedType("Error");

	@Override
	public int getOffset()
	{
		return 0;
	}

	@Override
	public int getLength()
	{
		return 0;
	}

	@Override
	public ASTNode findNode(int offset)
	{
		return null;
	}

	@Override
	public Type inferType(Scope scope)
	{
		return this;
	}

	@Override
	public Type getType()
	{
		return this;
	}
	
	
	
}
