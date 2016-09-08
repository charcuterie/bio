package datastructures;

/**
 * Closed-open, [), interval on the integer number line. 
 */
public interface Interval extends Comparable<Interval> {

    /**
     * Returns the starting point of this.
     */
    int getStart();

    /**
     * Returns the ending point of this.
     * <p>
     * The interval does not include this point.
     */
    int getEnd();

    /**
     * Returns the length of this.
     */
    default int length() {
        return getEnd() - getStart();
    }

    /**
     * Returns if this interval is adjacent to the specified interval.
     * <p>
     * Two intervals are adjacent if either one ends where the other starts.
     * @param interval - the interval to compare this one to
     * @return if this interval is adjacent to the specified interval.
     */
    default boolean isAdjacent(Interval other) {
        return getStart() == other.getEnd() || getEnd() == other.getStart();
    }
    
    default boolean overlaps(Interval o) {
        return getEnd() > o.getStart() && o.getEnd() > getStart();
    }
    
    default int compareTo(Interval o) {
        if (getStart() > o.getStart()) {
            return 1;
        } else if (getStart() < o.getStart()) {
            return -1;
        } else if (getEnd() > o.getEnd()) {
            return 1;
        } else if (getEnd() < o.getEnd()) {
            return -1;
        } else {
            return 0;
        }
    }
}