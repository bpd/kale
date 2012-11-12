package kale.ast;

import kale.ReadOnlyScope;
import kale.Scope;

public class FunctionSignature extends Type
{
	Type parentType;
	
	final Id name;
	
	final Signature signature;
	
	public FunctionSignature(Id name, Signature signature)
	{
		this.name = name;
		this.signature = signature;
		
		setType( this );
		
		addChild( name );
		addChild( signature );
	}
	
	public String getName()
	{
		return name.id;
	}
	
	public Signature getSignature()
	{
		return signature;
	}
	
	@Override
	public void inferType(Scope scope)
	{
		signature.inferType(scope);
	}
	
	public Type getParentType()
	{
		return parentType;
	}
	
	public void setParentType(Type parentType)
	{
		this.parentType = parentType;
	}
	
	public String getQualifiedPath()
	{
		return parentType == null ? "" : parentType.getFullName();
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append( name.id ).append(' ').append( signature.toString() );
		return sb.toString();
	}

	@Override
	public ReadOnlyScope getScope()
	{
		throw new UnsupportedOperationException(
				"NamedSignatures have no scope");
	}

	@Override
	public String getFullName()
	{
		throw new UnsupportedOperationException(
					"NamedSignatures have no full name");
	}

	/**
	 * NamedSignatures take the object as their first argument
	 */
	@Override
	public String toDescriptor()
	{
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		//sb.append( parentType.toDescriptor() );
		sb.append( "Ljava/lang/Object;" );
		for( ParamDecl param : signature.getParams() )
		{
			sb.append( param.getType().toDescriptor() );
		}
		sb.append(')');
		sb.append( signature.getReturnType().getType().toDescriptor() );
		return sb.toString();
	}

	@Override
	public boolean isEquivalent(Type type)
	{
		if( type instanceof FunctionDecl )
		{
			return ((FunctionDecl)type).isEquivalent( this );
		}
		return false;
	}
}
