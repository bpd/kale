package kale.runtime.test;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.util.Arrays;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import sun.invoke.anon.AnonymousClassLoader;

public class GuardTest
{
	static final MethodHandles.Lookup lookup = MethodHandles.lookup();
	
	static class Person
	{
		String name;
		
		public void sayHello()
		{
			System.out.println("Person.sayHello(), name: " + this.name);
		}
	}
	
	static class Dog
	{
		int age;
		
		public void sayHello()
		{
			System.out.println("Dog.sayHello(), age: " + this.age);
		}
	}
	
	static class Other
	{
		public void sayHello()
		{
			System.out.println("Other.sayHello()");
		}
	}
	
	
	// InterfaceCallSite knows its methodName, methodType, and interface name
	// it can then upgrade the Guard
	static void slowPath(Object o, String methodName, MethodType type, Object[] args) throws Throwable
	{
		System.out.println("Slow path: " + o);
		System.out.println("method: " + methodName + " " + type);
		System.out.println("args: " + Arrays.toString(args));
		
		MethodHandle mh = lookup.findVirtual(	o.getClass(),
												methodName,
												MethodType.methodType(void.class) );
		
		// need to use invoke instead of invokeExact because
		// we need it to upcast to Dog
		mh.invoke(o);
	}
	
	
	public static void hello()
	{
		System.out.println("Hello2");
	}

	static final MethodHandle instanceOfHandle = makeInstanceOf();
	
	private static MethodHandle makeInstanceOf()
	{
		try
		{
			return MethodHandles.lookup().findVirtual( 
					Class.class,
					"isInstance",
					MethodType.methodType(
							boolean.class,
							Object.class) );
		}
		catch(Throwable e)
		{
			throw new RuntimeException(e);
		}
	}
	
	
	public static CallSite bootstrap(	MethodHandles.Lookup caller,
										String name,
										MethodType type )
		throws	NoSuchMethodException,
				IllegalAccessException,
				ClassNotFoundException
	{
		System.out.println("bootstrap");
		System.out.println("name: " + name);
		System.out.println("type: " + type);
		
		MutableCallSite callsite = new MutableCallSite(type);
		
		MethodHandle slowPath = lookup.findStatic(
				GuardTest.class,
				"slowPath",
				MethodType.methodType(
						void.class,		// return type
						Object.class,	// receiver
						String.class,	// method name
						MethodType.class,// method type
						
						Object[].class	// args
						)
				);
		
		slowPath = MethodHandles.insertArguments(slowPath, 1, name, type);
		slowPath = slowPath.asVarargsCollector(Object[].class);
		slowPath = slowPath.asType( type );
		
		MethodHandle guarded = MethodHandles.guardWithTest(
			instanceOfHandle.bindTo(Person.class),
			
			lookup.findVirtual(
					Person.class,
					"sayHello",
					MethodType.methodType( void.class )
			).asType(type),
			
			MethodHandles.guardWithTest(
				instanceOfHandle.bindTo(Dog.class),
				lookup.findVirtual(	Dog.class,
					"sayHello",
					MethodType.methodType( void.class )
				).asType(type),
				
				slowPath
			)
		);
		
		System.out.println("guarded");
		
		callsite.setTarget( guarded );
		
		return callsite;
	}
	
	
	
	static MethodHandle invokeDynamicCaller(	String methodName,
												MethodType signature,
												Object[]...args )
														throws	NoSuchMethodException,
																Throwable
	{
		System.out.println("invokeDynamicCaller()");
		
		ClassWriter cw = new ClassWriter( ClassWriter.COMPUTE_FRAMES );
		MethodVisitor mv;
		
		cw.visit(	Opcodes.V1_7,
					Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER,
					"loader", // classname
					null,
					"java/lang/Object", // superclass
					null );
		
		mv = cw.visitMethod(	Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, 
								methodName,
								signature.toMethodDescriptorString(), 
								null, 
								null );
		mv.visitCode();
		
		
//		mv.visitTypeInsn(Opcodes.NEW, targetTypeName);
//		mv.visitInsn(Opcodes.DUP);
//		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, targetTypeName, "<init>", "()V");
		
		MethodType mt = MethodType.methodType(
										CallSite.class,
										MethodHandles.Lookup.class,
										String.class,
										MethodType.class
										);
		
		Handle handle = new Handle(	Opcodes.H_INVOKESTATIC,
									GuardTest.class.getName().replace('.', '/'),
									"bootstrap",
									mt.toMethodDescriptorString() );
		
		System.out.println("Invoke dynamic" + signature.toMethodDescriptorString());
		
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		
		mv.visitInvokeDynamicInsn(	methodName,
									signature.toMethodDescriptorString(),
									handle
									);
		
		
		
		if( signature.returnType() == void.class )
		{
			mv.visitInsn(Opcodes.RETURN);
		}
		else if( signature.returnType().isPrimitive() )
		{
			mv.visitInsn(Opcodes.IRETURN);
		}
		else
		{
			mv.visitInsn(Opcodes.ARETURN);
		}
		
		mv.visitMaxs(1, 1);
		mv.visitEnd();
		
		mv.visitEnd();
		
		AnonymousClassLoader loader = new AnonymousClassLoader();
		
		byte[] classBytes = cw.toByteArray();
		
		// generate our class
		Class<?> refc = loader.loadClass( classBytes );
		
		System.out.println("refc: " + refc);
		
		System.out.println("invoking invokedynamic main()");

		// invoke callPerson(),
		//   which creates a new Person and invokes
		//   sayHello() dynamically
		//
		return lookup.findStatic(	refc,
									methodName,
									signature
									);
	}
	
	public static void main(String[] args) throws Throwable
	{
		
		{
			MethodHandle handle = invokeDynamicCaller(	"sayHello",
					MethodType.methodType(void.class, Object.class)
					);
			
		Person p = new Person();
		p.name = "Brian";
		
		handle = handle.asType( MethodType.methodType(void.class, Person.class) );
		
		handle.invokeExact( p );
		
		p.name = "Jason";
		
		handle.invokeExact( p );
		}
		
		{
			// Note: if I just reuse the handler from above and call invoke()
			//       again, I get a Person -> Dog cast error...
			//       looking up a new MethodHandle correctly invokes the slow path
			//       ... so MethodHandles are tightly bound somehow to their initial
			//       caller?
		MethodHandle handle = invokeDynamicCaller(	"sayHello",
													MethodType.methodType(void.class, Object.class)
													);
		Dog d = new Dog();
		d.age = 5;
		
		handle = handle.asType( MethodType.methodType(void.class, Dog.class) );
		
		handle.invokeExact( d );
		
		d.age = 6;
		handle.invokeExact( d );
		}
		
		{
			// Note: if I just reuse the handler from above and call invoke()
			//       again, I get a Person -> Dog cast error...
			//       looking up a new MethodHandle correctly invokes the slow path
			//       ... so MethodHandles are tightly bound somehow to their initial
			//       caller?
		MethodHandle handle = invokeDynamicCaller(	"sayHello",
													MethodType.methodType(void.class, Object.class)
													);
		Other o = new Other();
		
		handle = handle.asType( MethodType.methodType(void.class, Other.class) );
		
		handle.invokeExact( o );
		}
		
		MethodHandle spread = MethodHandles.spreadInvoker( MethodType.methodType(boolean.class, String.class, int.class), 1 );
		
		System.out.println( spread );
		
	}
}
