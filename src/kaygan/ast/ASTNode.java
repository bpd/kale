package kaygan.ast;

public abstract class ASTNode
{
	public abstract int getOffset();
	
	public abstract int getLength();
	
	public boolean overlaps(int offset)
	{
		int nodeOffset = getOffset();
		return nodeOffset <= offset && (nodeOffset+getLength()) > offset;
	}
	
	public abstract ASTNode findNode(int offset);
}
