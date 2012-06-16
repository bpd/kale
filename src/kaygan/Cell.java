package kaygan;

import java.util.HashMap;
import java.util.Map;

public class Cell
{
	private final Map<?, ?> entries = new HashMap<Object, Object>();
	
	int size()
	{
		return entries.size();
	}
	
	/**
	 * What we do we know about this cell?
	 * Possible named types?  Value ranges?  Other call sites? 
	 *
	 */
	public static class Constraint
	{
		
	}
}
