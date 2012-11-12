package kale.runtime.test;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import kale.Parser;
import kale.ParserTest;
import kale.ast.CompilationUnit;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;

import sun.invoke.anon.AnonymousClassLoader;

public class InvokeMethodHandle
{
	static final MethodHandles.Lookup lookup = MethodHandles.lookup();
	
//	static
//	{
//		try
//		{
//		mh = lookup.findStatic(	InvokeMethodHandle.class,
//								"doInvoke",
//								MethodType.methodType(String.class)
//								);
//		}
//		catch(Throwable e)
//		{
//			throw new RuntimeException(e);
//		}
//	}

//	static MethodHandle mh;

//	public static String doInvoke()
//	{
//		return "Hello";
//	}
	
	static interface Greetable
	{
		String sayHello();
	}
	
	static class Person implements Greetable
	{
		public String sayHello()
		{
			return "Hello";
		}
	}
	
	static class Animal implements Greetable
	{
		public String sayHello()
		{
			return "Hello";
		}
	}
	
	static class Computer implements Greetable
	{
		public String sayHello()
		{
			return "Hello";
		}
	}
	
	private static final int ITERATIONS = 100000;
	
//	public static void invokeMethodHandle() throws Throwable
//	{
//		for(int i=0; i<ITERATIONS; i++)
//		{
//			String s = (String)mh.invokeExact();
//		}
//	}
	
	public static void invokeReflection() throws Throwable
	{
		Person p = new Person();
		Animal a = new Animal();
		Computer c = new Computer();
		
		for(int i=0; i<ITERATIONS; i++)
		{
			invokeWithReflection(p);
			invokeWithReflection(a);
			invokeWithReflection(c);
		}
	}
	
	public static void invokeWithReflection(Object o) throws Throwable
	{
		Object s = o.getClass().getDeclaredMethod("sayHello").invoke(o);
	}
	
	//-- Cached Reflection --//
	
	static class ReflectiveInlineCache
	{
		final String methodName;
		
		final Class<?>[] paramTypes;
		
		public ReflectiveInlineCache(String methodName, Class<?>...paramTypes)
		{
			this.methodName = methodName;
			this.paramTypes = paramTypes;
		}
		
		public class Entry
		{
			final Class<?> clazz;
			
			final Method method;
			
			public Entry(Class<?> clazz, Method method)
			{
				this.clazz = clazz;
				this.method = method;
			}
		}
		
		final List<Entry> entries = new ArrayList<Entry>();
		
		
		public Object invoke(Object o, Object...args) throws Throwable
		{
			Class<?> receiverClass = o.getClass();
			
			// look for a cached method
			for( Entry entry : entries )
			{
				if( entry.clazz.equals(receiverClass) )
				{
					return entry.method.invoke(o, args);
				}
			}
			
			Method method = receiverClass
								.getDeclaredMethod(methodName, paramTypes);
			
			Entry entry = new Entry(receiverClass, method);
			entries.add( entry );
			
			return entry.method.invoke(o, args);
		}
	}
	
	
	
	public static void invokeCachedReflection() throws Throwable
	{
		Person p = new Person();
		Animal a = new Animal();
		Computer c = new Computer();
		
		ReflectiveInlineCache cache = new ReflectiveInlineCache("sayHello");
		
		for(int i=0; i<ITERATIONS; i++)
		{
			cache.invoke(p);
			cache.invoke(a);
			cache.invoke(c);
		}
	}
	
	public static void invokeStaticInline() throws Throwable
	{
		Person p = new Person();
		Animal a = new Animal();
		Computer c = new Computer();
		
		for(int i=0; i<ITERATIONS; i++)
		{
			invokeWithStaticInline(p);
			invokeWithStaticInline(a);
			invokeWithStaticInline(c);
		}
	}
	
	public static void invokeWithStaticInline(Object o) throws Throwable
	{
		if( o instanceof Person )
		{
			String s = ((Person)o).sayHello();
		}
		else if( o instanceof Animal )
		{
			String s = ((Animal)o).sayHello();	
		}
		else if( o instanceof Computer )
		{
			String s = ((Computer)o).sayHello();
		}
	}
	
//	public static void invokeInvokeStatic() throws Throwable
//	{
//		Method m = InvokeMethodHandle.class.getMethod("doInvoke");
//		
//		for(int i=0; i<ITERATIONS; i++)
//		{
//			String s = doInvoke();
//		}
//	}
	
	public static void invokeInvokeInterface() throws Throwable
	{
		Person p = new Person();
		Animal a = new Animal();
		Computer c = new Computer();
		
		for(int i=0; i<ITERATIONS; i++)
		{
			invokeInterface(p);
			invokeInterface(a);
			invokeInterface(c);
		}
	}
	
	public static void invokeInterface(Greetable g) throws Throwable
	{
		String s = g.sayHello();
	}
	
	public static void testInvokes() throws Throwable
	{
		long start,stop;
		
		CompilationUnit kaleUnit = new Parser(
				new InputStreamReader(
					ParserTest.class.getResourceAsStream(
							"/kale/invoke-performance.kl")) ).parse();
		
		Class<?> kaleClass = kaleUnit.compile();
		
		
//		start = System.currentTimeMillis();
//		invokeMethodHandle();
//		stop = System.currentTimeMillis();
//		
//		System.out.println("invoke method handle took " + (stop-start) + "ms");
		
		start = System.currentTimeMillis();
		invokeReflection();
		stop = System.currentTimeMillis();
		
		System.out.println("invoke reflection took " + (stop-start) + "ms");
		
		start = System.currentTimeMillis();
		invokeCachedReflection();
		stop = System.currentTimeMillis();
		
		System.out.println("invoke cached reflection took " + (stop-start) + "ms");
		
//		start = System.currentTimeMillis();
//		invokeInvokeStatic();
//		stop = System.currentTimeMillis();
//		
//		System.out.println("invokestatic took " + (stop-start) + "ms");
		
		start = System.currentTimeMillis();
		invokeInvokeInterface();
		stop = System.currentTimeMillis();
		
		System.out.println("invokeinterface took " + (stop-start) + "ms");
		
		start = System.currentTimeMillis();
		CompilationUnit.execute(kaleClass);
		stop = System.currentTimeMillis();
		
		System.out.println("invoke kale interface took " + (stop-start) + "ms");
		
		start = System.currentTimeMillis();
		invokeStaticInline();
		stop = System.currentTimeMillis();
		
		System.out.println("invokeStaticInline took " + (stop-start) + "ms");
		
	}
	
	
//	public static void invoke() throws Throwable
//	{
//		ClassWriter cw = new ClassWriter( ClassWriter.COMPUTE_FRAMES );
//		MethodVisitor mv;
//		
//		cw.visit(	Opcodes.V1_7,
//					Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER,
//					"loader", // classname
//					null,
//					"java/lang/Object", // superclass
//					null );
//		
////		{
////		mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
////		mv.visitCode();
////		mv.visitVarInsn(Opcodes.ALOAD, 0);
////		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
////		mv.visitInsn(Opcodes.RETURN);
////		mv.visitMaxs(1, 1);
////		mv.visitEnd();
////		}
//		
//		// generate a call{classname} method for each class
//		//
//		{
//			mv = cw.visitMethod(	Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, 
//									"invokeHandle", 
//									"()Ljava/lang/String;", 
//									null, 
//									null );
//			mv.visitCode();
//			
//			mv.visitFieldInsn(	Opcodes.GETSTATIC,
//								"kaygan/runtime/InvokeMethodHandle",
//								"mh",
//								"Ljava/lang/invoke/MethodHandle;");
//			
//			mv.visitMethodInsn(	Opcodes.INVOKEVIRTUAL,
//								"java/lang/invoke/MethodHandle",
//								"invokeExact",
//								"()Ljava/lang/String;"
//								);
//
//			mv.visitInsn(Opcodes.ARETURN);
//			mv.visitMaxs(1, 1);
//			mv.visitEnd();
//		}
//		
//		mv.visitEnd();
//		
//		AnonymousClassLoader loader = new AnonymousClassLoader();
//		
//		byte[] classBytes = cw.toByteArray();
//		
//		// debug class
//		ClassReader cr = new ClassReader( classBytes );
//		
//		int flags = ClassReader.SKIP_DEBUG;
//
////		cr.accept(new TraceClassVisitor(null,
////            new ASMifier(),
////            new PrintWriter(System.out)), flags);
//		
//		cr.accept(new TraceClassVisitor(null,
//	            new Textifier(),
//	            new PrintWriter(System.out)), flags);
//		
//		// generate our class
//		Class<?> refc = loader.loadClass( classBytes );
//		
//		System.out.println("refc: " + refc);
//		
//		System.out.println("invoking invokedynamic main()");
//		
//
//			// invoke callPerson(),
//			//   which creates a new Person and invokes
//			//   sayHello() dynamically
//			//
//			MethodHandle invokeHandle = lookup.findStatic(	refc,
//															"invokeHandle",
//															MethodType.methodType(String.class)
//														);
//
//			// polymorphic signature... takes return type into account
//			String o = (String)invokeHandle.invokeExact();
//		
//			System.out.println("o: " + o);
//
//	}
	
	public static void main(String[] args) throws Throwable
	{
		//invoke();
		testInvokes();
	}
}
