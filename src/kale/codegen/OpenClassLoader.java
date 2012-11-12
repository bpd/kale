package kale.codegen;

public class OpenClassLoader extends ClassLoader
{
	@SuppressWarnings("deprecation")
	public Class<?> loadClass(byte[] b)
		throws ClassFormatError
	{
		return defineClass(b, 0, b.length);
	}
};
