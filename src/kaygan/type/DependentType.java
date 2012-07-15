package kaygan.type;

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
	public Type substitute(Type from, Type to)
	{
		Type newType = returnType.substitute(from, to);
		
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
			
			if( oldArgs.length == newArgs.length )
			{
				for(int i=0; i<oldArgs.length; i++)
				{
					newFuncType = newFuncType.substitute(newArgs[i], oldArgs[i]);
				}
			}
			else
			{
				System.err.println("Arg mismatch");
				new RuntimeException().fillInStackTrace().printStackTrace();
			}
			return newFuncType.getRetType();
		}
		else
		{
			return newType;
		}
	}
	
	@Override
	public int hashCode()
	{
		return returnType.hashCode();
	}
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof Type && ((Type)o).equals(this.returnType);
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("ret<").append(returnType).append('>');
		return sb.toString();
	}
	
}
