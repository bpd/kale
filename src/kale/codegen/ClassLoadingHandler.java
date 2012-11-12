package kale.codegen;

import java.io.PrintWriter;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.TraceClassVisitor;

public class ClassLoadingHandler implements ClassHandler
{
	private final OpenClassLoader classLoader;
	
	public ClassLoadingHandler(OpenClassLoader classLoader)
	{
		this.classLoader = classLoader;
	}
	
	@Override
	public void handle(String className, byte[] bytecode)
	{
		System.out.println("loading class " + className);
		
		new ClassReader(bytecode).accept(new TraceClassVisitor(new PrintWriter(System.out)), ClassReader.SKIP_DEBUG);
		
		classLoader.loadClass(bytecode);
	}
}
