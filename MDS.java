/** 
 * @author
 *   Sivagurunathan Velayutham sxv176330
 *   Sai Spandan Gogineni sxg175130
 *   Shivani Mankhotia sxm180018
 *   Maitreyee Mhasakar mam171630
 * 
 */

package sxm180018;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Multi-dimensional search: Websites like Amazon, allows users to specify attributes of products that
 * they are seeking, and shows products that have most of those
 * attributes.  To make search efficient, the data is organized as Index.
 * Index uses appropriate data structures, like as balanced trees for faster retrival.
 * As the objects change, these access structures have to be kept consistent.
 * */

public class MDS {

    /**
     * Accessing Database with help of DBManager
     * */

    private DBManager dbManager;

    public MDS() {
        dbManager = DBManager.getConnection();
    }

    /*
      PRIVATE METHODS FOR LP3
     */

    /**
     * Adding item to Description Index for Search
     * @param item to be indexed
     * */

    private void addIndex(Item item) {
        for (long text : item.getList()) {
            Set<Item> products = dbManager.getDescriptionIndex().getOrDefault(text, null);
            if (products == null) {
                products = new HashSet<>();
            }
            products.add(item);
            dbManager.getDescriptionIndex().put(text, products);
        }
    }


    /**
     * Removing item from Description Index
     * @param item to be removed from index
     * */

    private void removeIndex(Item item) {
        for (long text : item.getList()) {
            Set<Item> products = dbManager.getDescriptionIndex().getOrDefault(text, null);
            if (products != null) {
                products.remove(item);
                if (products.size() == 0)
                    dbManager.getDescriptionIndex().remove(text);
            }
        }
    }

    /*
      PUBLIC METHODS OF LP3
     */

    /**
     * Insert a new item whose description is given in the list.  If an entry with the same id already exists, then its
     * description and price are replaced by the new values, unless list is null or empty,
     * in which case, just the price is updated.
     * @param id item ID
     * @param price price of the Item
     * @param list description of the Item
     * @return 1 if the item is new, and 0 otherwise.
     */

    public int insert(long id, Money price, java.util.List<Long> list) {
        Item item = dbManager.getItemTable().get(id);
        int status = 0;
        if (item == null) {
            item = Builder.of(Item::new)
                    .with(Item::setId, id)
                    .with(Item::setPrice, price)
                    .with(Item::setList, new HashSet<>(list))
                    .build();
            status = 1;
        }
        else {
            if (list.size() > 0) {
                removeIndex(item);
                item.setList(new HashSet<>(list));
            }
            item.setPrice(price);
        }
        addIndex(item);
        dbManager.getItemTable().put(id, item);
        return status;
    }

    /**
     * Given the item id, return the price of the item
     * @param id item id
     * @return 0 if not found else return price of the item
     */

    public Money find(long id) {
        Item item = dbManager.getItemTable().getOrDefault(id, null);
        return item == null ? Money.ZERO: item.getPrice();
    }


    /**
     * Delete the item from Storage
     * @param id item id
     * @return the sum of the long ints that are in the description of the item deleted,
     *        or 0, if such an id did not exist.
     */

    public long delete(long id) {
        Item item = dbManager.getItemTable().getOrDefault(id, null);
        if (item != null) {
            long sum = item.getList().stream().mapToLong(Long::longValue).sum();
            removeIndex(item);
            dbManager.getItemTable().remove(id, item);
            return sum;
        }
        return 0;
    }

    /**
     * Given a long int, find items whose description contains that number (exact match with one of the long ints in the
     * item's description), and return lowest price of those items.
     * @param n text to search
     * @return lowest price of search items, 0 if not found.
     */

    public Money findMinPrice(long n) {
        Set<Item> items = dbManager.getDescriptionIndex().getOrDefault(n, null);
        if (items != null) {
            return items.stream().map(Item::getPrice).min(MDS.Money::compareTo).orElse(Money.ZERO);
        }
        return Money.ZERO;
    }

    /**
     * Given a long int, find items whose description contains that number (exact match with one of the long ints in the
     * item's description), and return highest price of those items.
     * @param n text to search
     * @return Highest price of search items, 0 if not found.
     */

    public Money findMaxPrice(long n) {
        Set<Item> items = dbManager.getDescriptionIndex().getOrDefault(n, null);
        if (items != null) {
            return items.stream().map(Item::getPrice).max(MDS.Money::compareTo).orElse(Money.ZERO);
        }
        return Money.ZERO;
    }

    /**
     * Given a long int n, find the number of items whose description contains n, and in addition,
     * their prices fall within the given range, [low, high].
     * @param n text
     * @param low lower limit
     * @param high upper limit
     */

    public int findPriceRange(long n, Money low, Money high) {
        if (low.compareTo(high) < 0) {
            Set<Item> items = dbManager.getDescriptionIndex().getOrDefault(n, null);
            if (items != null) {
                return items.stream()
                        .collect(Collectors.groupingBy(Item::getPrice, TreeMap::new, Collectors.toSet()))
                        .subMap(low, true, high, true)
                        .values()
                        .stream()
                        .mapToInt(Set::size)
                        .sum();
            }
        }
        return 0;
    }

    /**
     * Increase the price of every product, whose id is in the range [l,h] by r%.
     * Discarding fractional pennies in the new prices of items.
     * @param l lower limit
     * @param h upper limit
     * @param rate to be increased
     * @return  Returns the sum of the net increases of the prices.
     */

    public Money priceHike(long l, long h, double rate) {
        SortedMap<Long, Item> subMap = dbManager.getItemTable().subMap(l, true, h, true);
        BigDecimal netIncrease = BigDecimal.ZERO;

        for (Map.Entry<Long, Item> e : subMap.entrySet()) {
            Item item = e.getValue();
            Money prev = item.getPrice();
            BigDecimal increase = new BigDecimal(prev.toString())
                    .multiply(BigDecimal.valueOf(rate))
                    .divide(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.DOWN);
            netIncrease = netIncrease.add(increase);
            BigDecimal d = new BigDecimal(increase.toString());
            d = d.setScale(2, RoundingMode.DOWN);
            Money increaseMoney = new Money(d.toString());
            prev.d += increaseMoney.dollars();
            prev.c += increaseMoney.cents();
            if (prev.c >= 100) {
                prev.d += (prev.c / 100);
                prev.c %= 100;
            }
            item.setPrice(prev);
        }

        netIncrease = netIncrease.setScale(2, RoundingMode.DOWN);
        return new Money(netIncrease.toString());
    }

    /**
     * Remove elements of list from the description of id.
     * @param id item id
     * @param list description list
     * @return the sum of the numbers that are actually deleted from the description of id else return 0.
    */

    public long removeNames(long id, java.util.List<Long> list) {
        Item item = dbManager.getItemTable().getOrDefault(id, null);
        if (item != null) {
            long sum = item.getList().stream().filter(list::contains).mapToLong(Long::longValue).sum();
            removeIndex(item);
            item.setList(item.getList().stream().filter(e -> !list.contains(e)).collect(Collectors.toSet()));
            addIndex(item);
            return sum;
        }
        return 0;
    }

    // Do not modify the Money class in a way that breaks LP3Driver.java
    public static class Money implements Comparable<Money> {
        // Helper object representing zero money
        static final Money ZERO = new Money();

        long d;
        int c;

        public Money() {
            d = 0;
            c = 0;
        }

        public Money(long d, int c) {
            this.d = d;
            this.c = c;
        }

        public Money(String s) {
            String[] part = s.split("\\.");
            int len = part.length;
            if (len < 1) {
                d = 0;
                c = 0;
            } else if (part.length == 1) {
                d = Long.parseLong(s);
                c = 0;
            } else {
                d = Long.parseLong(part[0]);
                c = Integer.parseInt(part[1]);
            }
        }

        public long dollars() {
            return d;
        }

        public int cents() {
            return c;
        }

        public int compareTo(Money other) { // Complete this, if needed
            BigDecimal thisNumber = new BigDecimal(this.toString());
            BigDecimal otherNumber = new BigDecimal(other.toString());
            return thisNumber.compareTo(otherNumber);
        }

        public String toString() {
            return d + "." + (c < 10 ? "0" + c : c);
        }

        @Override
        public int hashCode() {
            return Objects.hash(d, c);
        }
    }


}
