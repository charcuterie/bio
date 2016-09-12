package datastructures;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import annotation.Annotation;
import utils.FilteredIterator;

/**
 * This class is a tree structure suitable for storing <code>Annotation</code>s
 * across an entire genome, e.g., the records from a BED file or a collection
 * of reads from a BAM file.
 */
public class GenomeTree {

    private Map<String, IntervalSetTree<Annotation>> chroms;
    
    /**
     * Constructs an empty <code>GenomeTree</code>.
     */
    public GenomeTree() {
        chroms = new HashMap<>();
    }
    
    /**
     * Whether or not this has any <code>Annotation</code>s stored in it.
     */
    public boolean isEmpty() {
        return chroms.isEmpty() || getSize() == 0;
    }
    
    /**
     * Gets the total number of <code>Annotation</code>s in this.
     */
    public int getSize() {
        return chroms.values()
                     .stream()
                     .mapToInt(IntervalSetTree::size)
                     .sum();
    }
    
    /**
     * Inserts an <code>Annotation</code> into this.
     * @param a - the <code>Annotation</code> to insert
     * @return <code>true</code> if the addition resulted in a change to the
     * tree; otherwise (if the value was already present, for example),
     * <code>false</code>
     */
    public boolean insert(Annotation a) {
        return chroms.computeIfAbsent(a.getReferenceName(), 
                t -> new IntervalSetTree<>()).insert(a);
    }
    
    /**
     * Gets an <code>Iterator</code> over all values in this which overlap
     * a given <code>Annotation</code>
     * @param a - the given <code>Annotation</code>
     */
    public Iterator<Annotation> getOverlappers(Annotation a) {
        IntervalSetTree<Annotation> tree = chroms.get(a.getReferenceName());
        
        if (tree == null) {
            return Collections.emptyIterator();
        } else {
            return new FilteredIterator<Annotation>(tree.overlappers(a),
                    o -> a.getReferenceName().equals(o.getReferenceName()) && o.overlaps(a));
        }
    }
}