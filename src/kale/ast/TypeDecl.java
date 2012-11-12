package kale.ast;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_7;

import java.util.HashSet;
import java.util.Set;

import kale.ReadOnlyScope;
import kale.Scope;
import kale.codegen.ClassName;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import org.objectweb.asm.Opcodes;


public class TypeDecl extends Type
{
	// package1.subpackage2.subpackage3
	String packageName;
	
	final Id typeName;
	
	//final Type type;
	
	final FieldDecl[] fields;
	
	final FunctionDecl[] methods;
	
	final Scope scope = new Scope();
	
	public TypeDecl(Id name, FieldDecl[] fields, FunctionDecl[] methods)
	{
		this.typeName = name;
		
		this.fields = fields;
		this.methods = methods;
		
		addChild( name );
		addChildren( fields );
		addChildren( methods );
		
		// TODO verify no duplicate method names
		Set<String> names = new HashSet<String>();
		
		for( FieldDecl field : fields )
		{
			if( names.add( field.getName() ) )
			{
				scope.set( field.getName(), field );
			}
			else
			{
				error("Type already contains element named " + field.getName());
			}
		}
		
		for( FunctionDecl method : methods )
		{
			if( names.add( method.getName() ) )
			{
				scope.set( method.getName(), method );
			}
			else
			{
				error("Type already contains element named " + method.getName());
			}
		}
		
		// TODO populate internal 'scope' with methods and fields
		
		setType(this);
	}
	
	public ReadOnlyScope getScope()
	{
		return scope;
	}
	
	public FieldDecl[] getFields()
	{
		return fields;
	}
	
	public FunctionDecl[] getMethods()
	{
		return methods;
	}
	
	public FunctionDecl getMethod(String name)
	{
		for( FunctionDecl method : methods )
		{
			if( method.getName().equals(name) )
			{
				return method;
			}
		}
		return null;
	}
	
	public String getName()
	{
		return typeName.id;
	}
	
	public String getPackageName()
	{
		return packageName;
	}
	
	public void setPackageName(String packageName)
	{
		this.packageName = packageName;
	}
	
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
	public boolean isInvocable()
	{
		return true;
	}
	
	public void inferBody(Scope scope)
	{
		scope = scope.newSubScope();
		
		scope = scope.withLexical(this.scope);
		
		for( FunctionDecl method : methods )
		{
			method.inferBody(scope);
		}
	}
	
	@Override
	public void inferType(Scope scope)
	{
		// create a new scope for the type
		scope = scope.newSubScope();
		
		scope = scope.withLexical(this.scope);
		
		for( FieldDecl field : fields )
		{
			field.setParentType( this );
			field.inferType(scope);
			
			//scope.set(field.getName(), field);
		}
		
		// add each method to the scope before we do
		// any type inference of the methods themselves,
		// that way methods have visibility of one another
//		for( FunctionDecl method : methods )
//		{
//			scope.set( method.getName(), method );
//		}
		
		for( FunctionDecl method : methods )
		{
			method.setParentType( this );
			method.inferType(scope);
			
			if( method.getSignature().isOperator() )
			{
				ParamDecl params[] = method.getSignature().getParams();
				if( params.length == 2 )
				{
					// TODO verify parameter types match up
					
				}
				else
				{
					method.error("Operators must have two parameters");
				}
			}
		}
	}
	
	@Override
	public boolean isEquivalent(Type type)
	{
		if( type == null )
		{
			throw new IllegalArgumentException("Type cannot be null");
		}
		
		if( type instanceof TypeDecl )
		{
			// this type is equivalent to the argument 'type'
			// if their full names are the same
			return getFullName().equals( type.getFullName() );
		}
		else if( type instanceof InterfaceDecl )
		{
			// this type is equivalent to the argument 'type'
			// if it implements all the methods in the interface
			InterfaceDecl iface = (InterfaceDecl)type;
			
			for( FunctionSignature methodSig : iface.getMethods() )
			{
				// first, make the sure the type even has this method
				FunctionDecl method = getMethod( methodSig.getName() );
				if( method == null )
				{
					return false;
				}
				else
				{
					// we have a method of the same name, make sure sure
					// params and param types match up
					if( !method.isEquivalent( methodSig ) )
					{
						return false;
					}
				}
			}
			
			return true;
		}

		return false;
	}
	
	@Override
	public String toString()
	{
//		StringBuilder sb =  new StringBuilder();
//		sb.append("type ").append(this.typeName.id).append(" { \n");
//		for( FieldDecl field : fields )
//		{
//			sb.append("  ").append(field).append(";\n");
//		}
//		
//		for( FunctionDecl method : methods )
//		{
//			sb.append("  ").append(method).append("\n");
//		}
//		sb.append("}");
//		return sb.toString();
		
		return getFullName();
	}
	
	public byte[] emitBytecode()
	{
		// FIXME: compute max stack/locals ourselves?
		ClassWriter cw = new ClassWriter( ClassWriter.COMPUTE_FRAMES );
		
		// fields
		for( FieldDecl field : fields )
		{
			FieldVisitor fv = cw.visitField(
								Opcodes.ACC_PUBLIC,	// access
								ClassName.toJavaFriendly( field.getName() ),	// name
								field.getType().toDescriptor(),				// descriptor
								null,				// signature (nullable)
								null);				// value (nullable)
			fv.visitEnd();
		}
		
		String className = ClassName.toJavaFriendly( packageName + "." + typeName.id );

		cw.visit(	V1_7,
					ACC_PUBLIC + ACC_FINAL, // attributes
								// + ACC_ABSTRACT + ACC_INTERFACE,
					className,	// class name (optional with AnonymousClassLoader)
					null,							// 
					"java/lang/Object",				// extends
					new String[]{});				// implemented interfaces
		
		MethodVisitor mv;
		
		// no-arg constructor
		{
			mv = cw.visitMethod(	ACC_PUBLIC,		// attributes
									"<init>",		// method name
									"()V",			// descriptor
									null,			// signature
									null);			// exceptions ([])
			
			mv.visitCode();
			
			// invoke java.lang.Object constructor
			mv.visitVarInsn(ALOAD, 0);   // local[0] == this
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
			
			mv.visitInsn(RETURN);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
			
			mv.visitEnd();
		}
		
		// emit methods
		boolean definedToString = false;
		
		for( FunctionDecl method : methods )
		{
			if( method.getName().equals("toString") )
			{
				definedToString = true;
			}
			
			int flags = ACC_PUBLIC;
			if( method.isStatic() )
			{
				flags += Opcodes.ACC_STATIC;
			}
			mv = cw.visitMethod(	flags,	// attributes
									ClassName.toJavaFriendly( method.getName() ),// method name
									method.getSignature().toDescriptor(),// descriptor
									null,			// signature
									null);			// exceptions ([])
			
			method.emitBytecode(mv);
			
			mv.visitEnd(); // end method
		}
		
		// emit autogenerated 'toString' method if one has not already
		// been defined
		if( !definedToString )
		{
			int flags = ACC_PUBLIC;

			mv = cw.visitMethod(	flags,	// attributes
									"toString",// method name
									"()Ljava/lang/String;",// descriptor
									null,			// signature
									null);			// exceptions ([])
			
			mv.visitCode();
			
			mv.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
			mv.visitInsn(Opcodes.DUP);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V");
			mv.visitVarInsn(Opcodes.ASTORE, 1);
			
			mv.visitVarInsn(ALOAD, 1);
			
			mv.visitLdcInsn("{");
			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
			
			for( FieldDecl field : fields )
			{
				
				// ' {fieldname}='
				mv.visitLdcInsn( " " + ClassName.toJavaFriendly( field.getName() ) + "=" );
				mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
				
				String fieldDesc = field.getType().toDescriptor();
				
				// string prologue
				if( fieldDesc.equals("Ljava/lang/String;") )
				{
					mv.visitLdcInsn("\"");
					mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
				}
				
				// append field value
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(Opcodes.GETFIELD, className, ClassName.toJavaFriendly( field.getName() ), fieldDesc);
				
				if( fieldDesc.equals("I") )
				{
					mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;");
				}
				else
				{
					mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/Object;)Ljava/lang/StringBuilder;");
				}
				
				// string epilogue
				if( fieldDesc.equals("Ljava/lang/String;") )
				{
					mv.visitLdcInsn("\"");
					mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
				}
			}
			
			mv.visitLdcInsn(" }");
			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
			
			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;");
			mv.visitInsn(Opcodes.ARETURN);
			
			mv.visitMaxs(1, 1);
			mv.visitEnd();
			
			mv.visitEnd(); // end method
		}
		
		return cw.toByteArray();
	}
	
	@Override
	public void emitBytecode(MethodVisitor mv)
	{
		// no-op, needed to implement InvocationTarget interface
	}
	
}
