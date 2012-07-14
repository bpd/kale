package kaygan.type;

import kaygan.ast.ASTNode;

public abstract class Type extends ASTNode
{
	public static final Type TYPE = new NamedType("Type");
	
	public static final Type ANY = new NamedType("Any");
	
	public static final Type ERROR = new NamedType("Error");
	
	
	public abstract boolean accept(Type type);
//	{
//		// TODO - Any type accepts all types
//		//      - NamedType only accepts types named the same
//		//      - StructuralType accepts types with the same structure, etc
//		
//		// TODO work DependentType in here somewhere (inference)
//		
//		return false;
//	}
	

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
	
}
