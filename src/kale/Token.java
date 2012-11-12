package kale;

public class Token
{
	/** (inclusive) */
	public final int beginOffset;
	
	/** (exclusive) */
	public final int endOffset;
	
	public final TokenType type;
	
	public final String value;
	
	public Token(TokenType type, String value)
	{
		this( type, 0, 0, value );
	}
	
	public Token(TokenType type, int beginOffset, int endOffset, String value)
	{
		this.type = type;
		this.beginOffset = beginOffset;
		this.endOffset = endOffset;
		this.value = value;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append('<').append(type).append("@")
			.append(beginOffset).append(':').append(endOffset)
			.append(", ").append(value).append(">");
		
		return sb.toString();
	}
	
}
