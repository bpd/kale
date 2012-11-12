package kale.codegen.jvm;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import sun.invoke.anon.AnonymousClassLoader;
import static org.objectweb.asm.Opcodes.*;

public class BytecodeGenerator
{
	public static int test1 = 7;
	public static int test2 = -1;
	
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
		
		mv.visitFieldInsn(GETSTATIC, "kale/codegen/jvm/BytecodeGenerator", "test1", "I");
		mv.visitFieldInsn(PUTSTATIC, "kale/codegen/jvm/BytecodeGenerator", "test2", "I");
		
		mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		mv.visitLdcInsn("Hello World!");
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
		
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
			System.out.println("before run: " + test2);
			
			((Runnable)o).run();
			
			System.out.println("after run: " + test2);
		}
	}
	
	
}
