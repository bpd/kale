package kaygan;

/**
 * In biology, a channel (or pore) is a part of the cell's plasma membrane.
 * It is made up of certain proteins whose function is to control the
 * movement of food and water into the cell.
 * 
 * (Source: http://askabiologist.asu.edu/content/cell-parts)
 * 
 * @author Brian
 *
 */
public interface Channel
{
	Cell invoke(Cell arg);
}
