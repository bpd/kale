package kale.ast;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.V1_7;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import kale.ReadOnlyScope;
import kale.Scope;
import kale.codegen.ClassName;


public class InterfaceDecl extends Type
{
	// package1.subpackage2.subpackage3
	String packageName;
		
	private final Map<String, FunctionSignature> methodMap = new HashMap<String, FunctionSignature>();
	
	final Id name;
	
	final FunctionSignature[] methods;
	
	final Scope scope = new Scope();
	
	public InterfaceDecl(Id name, FunctionSignature[] methods)
	{
		this.name = name;
		
		this.methods = methods;
		
		Set<String> names = new HashSet<String>();
		
		for( FunctionSignature method : methods )
		{
			if( names.add( method.getName() ) )
			{
				scope.set( method.getName(), method );
				
				methodMap.put( method.getName(), method );
			}
			else
			{
				error("Type already contains element named " + method.getName());
			}
		}
		
		setType(this);
		
		addChild( name );
		addChildren( methods );
	}
	
	public FunctionSignature[] getMethods()
	{
		return methods;
	}
	
	public String getName()
	{
		return name.id;
	}
	
//	public NamedSignature get(String symbol)
//	{
//		return methodMap.get(symbol);
//	}
	
	public String getPackageName()
	{
		return packageName;
	}
	
	public void setPackageName(String packageName)
	{
		this.packageName = packageName;
	}
	
	// TODO dupe of TypeDecl
	@Override
	public String getFullName()
	{
		StringBuilder sb = new StringBuilder();
		if( packageName != null )
		{
			sb.append(packageName);
			sb.append('.');
		}
		sb.append( getName() );
		return sb.toString();
	}

	// TODO dupe of TypeDecl
	@Override
	public String toDescriptor()
	{
		StringBuilder sb = new StringBuilder();
		sb.append('L');
		sb.append( ClassName.toJavaFriendly( getFullName() ) );
		sb.append(';');
		return sb.toString();
	}

	@Override
	public boolean isEquivalent(Type type)
	{
		if( type instanceof TypeDecl )
		{
			return ((TypeDecl)type).isEquivalent( this );
		}
		return false;
	}
	
	@Override
	public void inferType(Scope scope)
	{
		// create a new scope for the interface
		scope = scope.newSubScope();
		
		// add each method to the scope before we do
		// any type inference of the methods themselves,
		// that way methods have visibility of one another
		for( FunctionSignature method : methods )
		{
			scope.set( method.getName(), method );
		}
		
		for( FunctionSignature method : methods )
		{
			method.setParentType(this);
			method.inferType(scope);
		}
	}
	
	public byte[] emitBytecode()
	{
		// FIXME: compute max stack/locals ourselves?
		ClassWriter cw = new ClassWriter( ClassWriter.COMPUTE_FRAMES );

		cw.visit(	V1_7,
					ACC_PUBLIC + Opcodes.ACC_ABSTRACT + Opcodes.ACC_INTERFACE, // attributes
								// + ACC_ABSTRACT + ,
					ClassName.toJavaFriendly( packageName + "." + name.id ),	// class name (optional with AnonymousClassLoader)
					null,							// 
					"java/lang/Object",				// extends
					new String[]{});				// implemented interfaces
		
		MethodVisitor mv;
//		
//		// no-arg constructor
//		{
//			mv = cw.visitMethod(	ACC_PUBLIC,		// attributes
//									"<init>",		// method name
//									"()V",			// descriptor
//									null,			// signature
//									null);			// exceptions ([])
//			
//			mv.visitCode();
//			
//			// invoke java.lang.Object constructor
//			mv.visitVarInsn(ALOAD, 0);   // local[0] == this
//			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
//			
//			mv.visitInsn(RETURN);
//			mv.visitMaxs(1, 1);
//			mv.visitEnd();
//			
//			mv.visitEnd();
//		}
		
		// emit methods
		for( FunctionSignature method : methods )
		{
			int flags = ACC_PUBLIC + Opcodes.ACC_ABSTRACT;
//			if( method.getSignature().isOperator() )
//			{
//				flags += Opcodes.ACC_STATIC;
//			}
			mv = cw.visitMethod(	flags,	// attributes
									ClassName.toJavaFriendly( method.getName() ),// method name
									method.getSignature().toDescriptor(),// descriptor
									null,			// signature
									null);			// exceptions ([])
			
			//method.emitBytecode(mv);
			
			mv.visitEnd(); // end method
		}
		
		return cw.toByteArray();
	}
	
	public ReadOnlyScope getScope()
	{
		return scope;
	}

	@Override
	public String toString()
	{
		StringBuilder sb =  new StringBuilder();
		sb.append("interface ").append(this.name.id).append(" { \n");
		
		for( FunctionSignature method : methods )
		{
			sb.append("  ").append(method).append("\n");
		}
		sb.append("}");
		return sb.toString();
	}
}
