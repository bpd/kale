package kale.codegen;

public class ClassName
{
	public static String toJavaFriendly(String id)
	{
		// single quotes are not legal identifiers anyway
		id = id.replace("<", "__lt__");
		id = id.replace(">", "__gt__");
		
		id = id.replace("/", "__fslash__");
		
		id = id.replace('.', '/');
		
		return id;
	}
}
