package kaygan.type;

import java.util.Arrays;
import java.util.Map;

public class FunctionType extends Type
{
	private static final Type TYPE = new Type("Type<Function>");
	
	private final Type[] argTypes;
	
	private final Type retType;
	
	public FunctionType( Type[] argsTypes, Type retType )
	{
		this.argTypes = argsTypes;
		this.retType = retType;
		this.type = TYPE;
	}
	
	public Type[] getArgTypes()
	{
		return argTypes;
	}
	
	public Type getRetType()
	{
		return retType;
	}
	
	@Override
	public boolean accept(Type type)
	{
		
		return true;
	}
	
	@Override
	public FunctionType substitute(Map<Type, Type> substitutions)
	{
		Type[] newArgTypes = new Type[argTypes.length];
		
		// and then go through the rest of the arguments and 
		for(int i=0; i<newArgTypes.length; i++)
		{
			newArgTypes[i] = argTypes[i].substitute(substitutions);
		}
		
		Type newRetType = retType.substitute(substitutions);
		
		FunctionType newFuncType = new FunctionType(newArgTypes, newRetType);
		
		//substitutions.put( this, newFuncType );
		
		return newFuncType;
	}


	@Override
	public String toString()
	{
		return new StringBuilder()
			.append('(')
			.append(Arrays.toString(argTypes)).append("=>").append(retType)
			.append(')')
			.toString();
	}
}
