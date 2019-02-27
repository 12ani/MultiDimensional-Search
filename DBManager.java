/** 
 * @author
 *   Sivagurunathan Velayutham sxv176330
 *   Sai Spandan Gogineni sxg175130
 *   Shivani Mankhotia sxm180018
 *   Maitreyee Mhasakar mam171630
 * 
 */
package sxm180018;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class DBManager implements MDSApplicationDB {

    private static DBManager dbManager;

    private DBManager() {
        if(dbManager != null) {
            // Avoid Reflection overloading
            throw new RuntimeException("Use getInstance() instead");
        }
    }

    public static DBManager getConnection() {
        if(dbManager == null)
            dbManager = new DBManager();
        return dbManager;
    }

    public TreeMap<Long, Item> getItemTable() {
        return item;
    }

    public Map<Long, Set<Item>> getDescriptionIndex() {
        return index;
    }

}