package kaygan.type;

import java.util.Arrays;

public class FunctionType extends Type
{
	private static final Type TYPE = new NamedType("Type<Function>");
	
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
	public FunctionType substitute(Type from, Type to)
	{
		Type[] newArgTypes = new Type[argTypes.length];
		for(int i=0; i<newArgTypes.length; i++)
		{
			newArgTypes[i] = argTypes[i].substitute(from, to);
		}
		
		Type newRetType = retType.substitute(from, to);
		
		return new FunctionType(newArgTypes, newRetType);
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
