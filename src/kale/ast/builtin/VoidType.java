package kale.ast.builtin;

import kale.ast.FieldDecl;
import kale.ast.FunctionDecl;
import kale.ast.Id;
import kale.ast.TypeDecl;

public class VoidType extends TypeDecl
{
	public VoidType()
	{
		super(
			new Id("void"),
			new FieldDecl[0],
			new FunctionDecl[0]			
		);
		
		setPackageName("kl");
	}
	
	@Override
	public String toDescriptor()
	{
		return "V";
	}
};
