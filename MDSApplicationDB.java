/** Application DB to store the item objects.
 * @author
 *   Sivagurunathan Velayutham sxv176330
 *   Sai Spandan Gogineni sxg175130
 *   Shivani Mankhotia sxm180018
 *   Maitreyee Mhasakar mam171630
 * 
 */
package sxm180018;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public interface MDSApplicationDB {

    /**
     * Item Table
     * */
    TreeMap<Long, Item> item = new TreeMap<>();

    /**
     * Index For Description to Items.
     * */
    Map<Long, Set<Item>> index = new HashMap<>();
}