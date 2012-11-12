package kale.codegen.jvm;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_7;

import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import sun.invoke.anon.AnonymousClassLoader;

public class InvokeDynamicTest
{
	
	static final List<CallSite> callsites = new ArrayList<CallSite>();
	
	public static void printHello()
	{
		System.out.println("hello");
	}
	
	static MethodHandle printHello2Handle;
	
	public static void printHello2()
	{
		System.out.println("hello2");
	}
	
	static MethodHandle printHello3Handle;
	
	public static void printHello3()
	{
		System.out.println("hello3");
	}
	
	static
	{
		try
		{
			printHello2Handle = MethodHandles
							.lookup()
							.findStatic(InvokeDynamicTest.class,
										"printHello2",
										MethodType.methodType(void.class) );
			
			printHello3Handle = MethodHandles
					.lookup()
					.findStatic(InvokeDynamicTest.class,
								"printHello3",
								MethodType.methodType(void.class) );
		}
		catch(Throwable e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public static CallSite bootstrap(	Lookup caller,
										String name, 
										MethodType type,
										String parameterNames )
		throws	NoSuchMethodException,
				IllegalAccessException
	{
		System.out.println("bootstrap: " + parameterNames);
		
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		
		Class<?> thisClass = lookup.lookupClass();
		
		MethodHandle handle = lookup.findStatic(thisClass,
												"printHello",
												MethodType.methodType(void.class) );
		
		CallSite callsite = new MutableCallSite(handle.asType(type));
		//CallSite callsite = new ConstantCallSite(handle.asType(type));
		
		callsites.add( callsite );
		
		return callsite;
	}
	
	
	public static byte[] gen() throws Exception
	{
		ClassWriter cw = new ClassWriter(0);

		MethodVisitor mv;

		cw.visit(	V1_7,
					ACC_PUBLIC, // attributes
								// + ACC_ABSTRACT + ACC_INTERFACE,
					"",			// class name (optional with AnonymousClassLoader)
					null,						// 
					"java/lang/Object",			// extends
					new String[]{				// implemented interfaces
						"java/lang/Runnable"
					});
		
		{
			mv = cw.visitMethod(	ACC_PUBLIC,		// attributes
									"<init>",			// method name
									"()V",			// signature
			null, null);
			
			// BEGIN run()
			mv.visitCode();
			
			mv.visitVarInsn(ALOAD, 0);   // local[0] == this
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
			
			mv.visitInsn(RETURN);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
			
			// END run()
			
			mv.visitEnd();
			}
		
		{
		mv = cw.visitMethod(	ACC_PUBLIC,		// attributes
								"run",			// method name
								"()V",			// signature
		null, null);
		
		// BEGIN run()
		mv.visitCode();
		
		//mv.visitFieldInsn(GETSTATIC, "kaygan/codegen/jvm/BytecodeGenerator", "test1", "I");
		//mv.visitFieldInsn(PUTSTATIC, "kaygan/codegen/jvm/BytecodeGenerator", "test2", "I");
		
		//mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		//mv.visitLdcInsn("Hello World!");
		//mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
		
		//
		
		MethodType mt = MethodType.methodType(
				CallSite.class,
				MethodHandles.Lookup.class,
				String.class,
				MethodType.class,
				String.class
		);
		
		Handle handle = new Handle(	Opcodes.H_INVOKESTATIC,
				"kale/codegen/jvm/InvokeDynamicTest",
				"bootstrap",
				mt.toMethodDescriptorString() );
		
		mv.visitInvokeDynamicInsn(
				"sayHello",
				"()V",
				handle,
				"p1name:p2name:p3name"    // parameter names, like Obj-C
		); 
		
		mv.visitInsn(RETURN);
		mv.visitMaxs(2, 1);
		mv.visitEnd();
		
		// END run()
		
		mv.visitEnd();
		}
		
		cw.visitEnd();
		
		return cw.toByteArray();
	}
	
	public static void main(String[] args) throws Exception
	{
		byte[] bc = gen();
		
		Class<?> clazz = new AnonymousClassLoader().loadClass(bc);
		
		Object o = clazz.newInstance();
		if( o instanceof Runnable )
		{
			// call with first handle
			for( int i=0; i<10; i++ )
			{
				((Runnable)o).run();
			}
			
			// swap callsite
			for( CallSite callsite : callsites )
			{
				callsite.setTarget(printHello2Handle);
			}
			
			// call with second handle
			for( int i=0; i<10; i++ )
			{
				((Runnable)o).run();
			}
		}
	}
}
