package datastructures;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import annotation.Annotation;
import utils.FilteredIterator;

public class GenomeTree {

    private Map<String, IntervalSetTree<Annotation>> chroms;
    
    public GenomeTree() {
        chroms = new HashMap<>();
    }
    
    public boolean isEmpty() {
        return chroms.isEmpty() || getSize() == 0;
    }
    
    public int getSize() {
        return chroms.values()
                     .stream()
                     .mapToInt(IntervalSetTree::size)
                     .sum();
    }
    
    public boolean insert(Annotation a) {
        return chroms.computeIfAbsent(a.getReferenceName(), 
                t -> new IntervalSetTree<>()).insert(a);
    }
    
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