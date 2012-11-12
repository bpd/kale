package kale.ast;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.V1_7;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import kale.Scope;
import kale.codegen.ClassHandler;
import kale.codegen.ClassName;

public class PackageDecl extends ASTNode
{
	// Id | QualifiedId
	final Id packageName;
	
	// ImportDecl imports;
	
	final TypeDecl[] types;
	
	final FunctionDecl[] functions;
	
	final InterfaceDecl[] interfaces;
	
	public PackageDecl(	Id packageName,
							TypeDecl[] types,
							FunctionDecl[] functions,
							InterfaceDecl[] interfaces )
	{
		this.packageName = packageName;
		this.types = types;
		this.functions = functions;
		this.interfaces = interfaces;
		
		addChild( packageName );
		addChildren( types );
		addChildren( functions );
		addChildren( interfaces );
	}
	
	public String getName()
	{
		return packageName.toString();
	}
	
	public TypeDecl[] getTypes()
	{
		return types;
	}
	
	public InterfaceDecl[] getInterfaces()
	{
		return interfaces;
	}
	
	public FunctionDecl[] getFunctions()
	{
		return functions;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("package ").append(packageName).append('\n');
		
		for( TypeDecl type : types )
		{
			sb.append( type ).append('\n');
		}
		
		for( InterfaceDecl ifc : interfaces )
		{
			sb.append( ifc ).append('\n');
		}
		
		for( FunctionDecl function : functions )
		{
			sb.append( function ).append('\n');
		}
		return sb.toString();
	}
	
	@Override
	public void inferType(Scope scope)
	{
		// add all types, interfaces, functions to the scope
		// FIXME add as full path or local path?  both?
		
//		if( packageName instanceof Id )
//		{
//			ASTNode pkgNode = scope.get(packageName);
//			if( pkgNode != null && pkgNode instanceof PackageDecl )
//			{
//				PackageDecl pkg = (PackageDecl)pkgNode;
//				
//				// package with this name already exists
//				// not a problem, just need to add the
//				// top-level elements of this package to
//				// the existing package...
//				//
//				// However, if there is a naming conflict
//				// with the top-level elements, THAT is an error
//				
//				
//			}
//			else if( pkgNode == null )
//			{
//				// package name is not in use, so set it
//				scope.set( packageName.toString(), this );
//			}
//			else
//			{
//				// error, our package is already declared
//				// as something other than a package
//			}
//		}
//		else if( packageName instanceof Id )
//		{
//			Id pkgName = (Id)packageName;
//			
//			// resolve each element of the package name,
//			// populating the hierarchy as we go
//			
//			ASTNode pkgNode = scope.get(packageName);
//			
////			for( Token token : pkgName.getSymbols() )
////			{
////				
////			}
//			
//		}
		
		// FIXME check for duplicate
		
		// configure scope
		
		for( TypeDecl type : types )
		{
			if( scope.containsSymbol(type.getName()) )
			{
				type.error("Symbol already declared");
			}
			scope.set(type.getName(), type);
			type.setPackageName(packageName.toString());
		}
		
		for( InterfaceDecl ifc : interfaces )
		{
			if( scope.containsSymbol(ifc.getName()) )
			{
				ifc.error("Symbol already declared");
			}
			scope.set(ifc.getName(), ifc);
			ifc.setPackageName(packageName.toString());
		}
		
		TypeDecl functionContainer = new TypeDecl(	new Id("__functions"),
													new FieldDecl[0],
													functions);
		functionContainer.setPackageName(packageName.toString());
		
		for( FunctionDecl function : functions )
		{
			if( scope.containsSymbol(function.getName()) )
			{
				function.error("Symbol already declared");
			}
			scope.set( function.getName(), function );
			//function.setPackageName(packageName.toString());
			function.setParentType( functionContainer );
		}
		
		
		// infer types
		
		for( TypeDecl type : types )
		{
			type.inferType(scope);
		}
		
		for( InterfaceDecl ifc : interfaces )
		{
			ifc.inferType(scope);
		}
		
		for( FunctionDecl function : functions )
		{
			function.inferType(scope);
		}
		
		// infer function and method body
		for( FunctionDecl function : functions )
		{
			function.inferBody(scope);
		}
		
		for( TypeDecl type : types )
		{
			type.inferBody(scope);
		}
	}
	
	public String getFunctionClassName()
	{
		return packageName.toString() + ".__functions";
	}
	
	public void emitBytecode(ClassHandler classHandler)
	{
		// gen user types
		for( TypeDecl type : types )
		{
			byte[] classBytes = type.emitBytecode();
			
			classHandler.handle(type.getFullName(), classBytes);
			
			//Class<?> clazz = classLoader.loadClass(classBytes);
		}
		
		// gen interfaces
		for( InterfaceDecl ifc : interfaces )
		{
			byte[] classBytes = ifc.emitBytecode();
			
			classHandler.handle(ifc.getFullName(), classBytes);
			
			//Class<?> clazz = classLoader.loadClass(classBytes);
		}
		
		// create a package class to store all static functions
		
		// FIXME: compute max stack/locals ourselves?
		ClassWriter functionClassWriter = new ClassWriter( ClassWriter.COMPUTE_FRAMES );
		
		String functionClassName = getFunctionClassName();
		
		functionClassWriter.visit(	V1_7,
				ACC_PUBLIC + ACC_FINAL, // attributes
							// + ACC_ABSTRACT + ACC_INTERFACE,
				ClassName.toJavaFriendly(functionClassName),	// class name (optional with AnonymousClassLoader)
				null,							// 
				"java/lang/Object",				// extends
				new String[]{});				// implemented interfaces
		
		for( FunctionDecl function : functions )
		{
			MethodVisitor mv;
			
			mv = functionClassWriter.visitMethod(	ACC_PUBLIC + ACC_STATIC,		// attributes
					ClassName.toJavaFriendly( function.getName() ),// method name
					function.getSignature().toDescriptor(),// descriptor
					null,			// signature
					null);			// exceptions ([])
			
			
			function.emitBytecode( mv );
			
			mv.visitEnd(); // end method
		}
		
		//Class<?> clazz = classLoader.loadClass(functionClassWriter.toByteArray());
		classHandler.handle(functionClassName, functionClassWriter.toByteArray());
	}
}
