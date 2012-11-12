package kale.ast;

import kale.ReadOnlyScope;

// TODO this should really only abstract TypeDecl and InterfaceDecl
public abstract class Type extends ASTNode
{
	public abstract ReadOnlyScope getScope();
	
	/**
	 * Type name (without package prefix)
	 * @return
	 */
	public abstract String getName();
	
	/**
	 * Type name (with package prefix)
	 * @return
	 */
	public abstract String getFullName();
	
	/**
	 * Java type descriptor for this type
	 * @return
	 */
	public abstract String toDescriptor();
	
	/**
	 * Determines if this type is equivalent to the given type
	 * 
	 * @param type
	 * @return
	 */
	public abstract boolean isEquivalent(Type type);
}
