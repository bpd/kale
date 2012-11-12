package kale.codegen.jvm;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_7;

import java.io.PrintWriter;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;


import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;

import org.objectweb.asm.Opcodes;

import sun.invoke.anon.AnonymousClassLoader;

public class ObjectDispatch
{
	public static interface Invoker
	{
		int invoke(Object o);
		
		int invoke(test.Person o);
	}
	
	public static interface GettableAge
	{
		int getAge();
	}
	
//	public class TestInvoker implements Invoker
//	{
//		public int invoke(test.Person p)
//		{
//			return p.age;
//		}
//	}
	
	public static byte[] gen()
	{
		ClassWriter cw = new ClassWriter( 0 ); //ClassWriter.COMPUTE_MAXS );

		MethodVisitor mv;

		cw.visit(	V1_7,
					ACC_PUBLIC, // attributes
								// + ACC_ABSTRACT + ACC_INTERFACE,
					"",			// class name (optional with AnonymousClassLoader)
					null,						// 
					"java/lang/Object",			// extends
					new String[]{				// implemented interfaces
						"kale/codegen/jvm/ObjectDispatch$Invoker"
					});
		
		// generate no-arg constructor
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
		
		// invoke(Person):int
		{
			mv = cw.visitMethod(	ACC_PUBLIC,					// attributes
									"invoke",					// method name
									"(Ltest/Person;)I",			// signature
									null,						// signature
									null						// exceptions
									);
			
			// BEGIN calculate()
			mv.visitCode();
			
			mv.visitVarInsn(Opcodes.ALOAD, 1); // push first argument
			//mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "test/Person", "getAge", "()I");
			mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "kale/codegen/jvm/ObjectDispatch$GettableAge", "getAge", "()I");
			
			mv.visitInsn(IRETURN);
			mv.visitMaxs(2, 2);  // 2 stack, 1 local for arg
			mv.visitEnd();
			
			// END calculate()
			
			mv.visitEnd();
		}
		
		// invoke(Object):int
		{
			mv = cw.visitMethod(	ACC_PUBLIC,					// attributes
									"invoke",					// method name
									"(Ljava/lang/Object;)I",			// signature
									null,						// signature
									null						// exceptions
									);
			
			// BEGIN calculate()
			mv.visitCode();
			
			mv.visitInsn(Opcodes.ICONST_0);  // load const zero
			
			mv.visitInsn(IRETURN);
			mv.visitMaxs(2, 2);  // 2 stack, 1 local for arg
			mv.visitEnd();
			
			// END calculate()
			
			mv.visitEnd();
		}
		
		cw.visitEnd();
		
		return cw.toByteArray();
	}
	
	public static void main(String[] args) throws Throwable
	{
//		ClassReader cr = new ClassReader("kaygan/codegen/jvm/ObjectDispatch$TestInvoker");
//		
//		int flags = ClassReader.SKIP_DEBUG;
//
//		cr.accept(new TraceClassVisitor(null,
//            new ASMifier(),
//            new PrintWriter(System.out)), flags);
		
//		cr.accept(new TraceClassVisitor(null,
//	            new Textifier(),
//	            new PrintWriter(System.out)), flags);
		
		Class<?> clazz = new AnonymousClassLoader().loadClass( gen() );
		
		Object o = clazz.newInstance();
		if( o instanceof Invoker )
		{
			Invoker invoker = (Invoker)o;
			
			test.Person p = new test.Person();
			p.age = 29;
			
			int i = invoker.invoke(p);
			System.out.println("i = " + i);
		}
	}
}
