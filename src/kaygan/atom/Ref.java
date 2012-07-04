package kaygan.atom;

import kaygan.Function;
import kaygan.Sequence;

public class Ref implements Function
{
	private final Sequence sequence;
	
	private final Symbol symbol;
	
	public Ref( Sequence sequence, Symbol symbol )
	{
		this.sequence = sequence;
		this.symbol = symbol;
	}

	@Override
	public Function bind(Function f)
	{
		return this;
	}

	@Override
	public Function eval()
	{
		Function f = sequence.resolve(symbol);
		
		System.out.println("symbol " + symbol + " resolved to " + f + " within sequence: " + sequence);
		
		Function result = f.eval();
		
		System.out.println("eval'd to: " + result);
		
		return result;
	}
	
	@Override
	public String toString()
	{
		return symbol.toString();
	}
	
}
