package kale.ast;

import kale.Scope;

public class FieldDecl extends ASTNode
{
	Type parentType;
	
	final Id name;
	
	final Id type;
	
	public FieldDecl(Id name, Id type)
	{
		this.name = name;
		this.type = type;
		
		addChild( name );
		addChild( type );
	}
	
	public String getName()
	{
		return name.id;
	}
	
	public Type getParentType()
	{
		return parentType;
	}
	
	public void setParentType(Type parentType)
	{
		this.parentType = parentType;
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
