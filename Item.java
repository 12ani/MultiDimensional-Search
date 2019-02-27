/** Item class representing the Item Table
 * It contains id of the item, Description of the item
 * and price of the item.
 * @see sxm180018.MDS.Money represented as price of the item here.
 * @author
 *   Sivagurunathan Velayutham sxv176330
 *   Sai Spandan Gogineni sxg175130
 *   Shivani Mankhotia sxm180018
 *   Maitreyee Mhasakar mam171630
 * 
 */

package sxm180018;


import java.util.*;

public class Item implements Comparable<Item> {

    private long id;
    private Set<Long> list;
    private MDS.Money price;

    Item() {
    }

    public Set<Long> getList() {
        return list;
    }

    public void setList(Set<Long> list) {
        this.list = new HashSet<>(list);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public MDS.Money getPrice() {
        return price;
    }

    public void setPrice(MDS.Money price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Item) {
            Item product = (Item) obj;
            return this.id == product.id;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "[ Item Id: " + id + " , name: " + Arrays.toString(list.toArray())
                + " Money: " + price.toString() + " ]";
    }

    public int compareTo(Item o) {
        return Long.compare(this.getId(), o.getId());
    }
}