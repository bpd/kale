package kale.ast;

import kale.Scope;


public class ParamDecl extends ASTNode implements LocalVariable
{
	final Id name;
	
	final Id type;
	
	/** the index of this param in the 'locals' bytecode array */
	int localIndex;
	
	public ParamDecl(Id name, Id type)
	{
		this.name = name;
		this.type = type;
		
		addChild( name );
		addChild( type );
	}
	
	public Id getName()
	{
		return name;
	}
	
	public int getLocalIndex()
	{
		return localIndex;
	}
	
	public void setLocalIndex(int localIndex)
	{
		this.localIndex = localIndex;
	}
	
	@Override
	public void inferType(Scope scope)
	{
		type.inferType(scope);
		setType(type.getType());
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(name).append(' ').append(type);
		return sb.toString();
	}
}
