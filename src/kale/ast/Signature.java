package kale.ast;

import kale.Scope;


public class Signature extends ASTNode
{
	final boolean operator;
	
	final ParamDecl[] params;
	
	final Id returnType;
	
	public Signature( boolean operator, ParamDecl[] params, Id returnType)
	{
		this.operator = operator;
		this.params = params;
		this.returnType = returnType;
		
		addChildren( params );
		addChild( returnType );
	}
	
	public boolean isOperator()
	{
		return operator;
	}
	
	public ParamDecl[] getParams()
	{
		return params;
	}
	
	public Id getReturnType()
	{
		return returnType;
	}
	
	@Override
	public void inferType(Scope scope)
	{
		scope = scope.newSubScope();
		
		for( ParamDecl param : params )
		{
			param.inferType(scope);
		}
		
		returnType.inferType(scope);
	}
	
	public String toDescriptor()
	{
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		for( ParamDecl param : params )
		{
			sb.append( param.getType().toDescriptor() );
		}
		sb.append(')');
		sb.append( returnType.getType().toDescriptor() );
		return sb.toString();
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		if( this.operator )
		{
			sb.append(" operator ");
		}
		
		sb.append('(');
		for( int i=0; i<params.length; i++ )
		{
			if( i > 0 )
			{
				sb.append(", ");
			}
			sb.append( params[i].toString() );
		}
		sb.append(") ").append( returnType );
		
		return sb.toString();
	}
}
