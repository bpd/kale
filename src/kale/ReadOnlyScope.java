package kale;

import kale.ast.ASTNode;
import kale.ast.Id;

public interface ReadOnlyScope
{
	ASTNode get(Id id);
	
	ASTNode get(String symbol);
}
