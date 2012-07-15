package kaygan.type;

import java.util.Map;

public class DependentType extends Type
{
	final Type returnType;
	
	final Type[] targetArgTypes;
	
	public DependentType( Type returnType, Type[] targetArgTypes )
	{
		this.returnType = returnType;
		this.targetArgTypes = targetArgTypes;
	}

	@Override
	public boolean accept(Type type)
	{
		return true;
	}

	@Override
	public Type substitute(Map<Type, Type> substitutions)
	{
		Type newType = returnType.substitute(substitutions);
		
		if( returnType.equals(newType) )
		{
			return this;
		}
		
		if( newType instanceof FunctionType )
		{
			// the dependency has been fulfilled with
			// a function literal, so patch up
			// the arguments to match the callsite
			// so substitutions flow through
			//
			FunctionType newFuncType = (FunctionType)newType;
			
			Type[] oldArgs = targetArgTypes;
			Type[] newArgs = newFuncType.getArgTypes();
			
			if( oldArgs.length != newArgs.length )
			{
				throw new TypeException(
						"Expected " + oldArgs.length 
							+ " arguments, found " + newArgs.length
								+ " at " + newFuncType.toString());
			}

			for(int i=0; i<oldArgs.length; i++)
			{
				substitutions.put(newArgs[i], oldArgs[i]);
			}
			
			newFuncType = newFuncType.substitute(substitutions);
			
			substitutions.put(this, newFuncType);
			
			return newFuncType.getRetType();
		}
		else
		{
			return newType;
		}
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("ret<").append(returnType).append('>');
		return sb.toString();
	}
	
}
