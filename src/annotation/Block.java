package annotation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Block extends AnnotationImpl implements Annotation {

    public Block(String ref, int start, int end, Strand strand) {
        super(ref, start, end, strand);
    }

    @Override
    public Block reverseComplement() {
        return new Block(ref, start, end, strand.reverse());
    }

    @Override
    public int getNumberOfBlocks() {
        return 1;
    }

    @Override
    public Iterator<Block> getBlocks() {
        List<Block> l = new ArrayList<>();
        l.add(this);
        return l.iterator();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        
        if (!(o instanceof Block)) {
            return false;
        }
        
        Block other = (Block) o;
        
        return ref.equals(other.ref) &&
               start == other.start &&
               end == other.end &&
               strand.equals(other.strand);
    }
    
    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode = 37 * hashCode + ref.hashCode();
        hashCode = 37 * hashCode + strand.hashCode();
        hashCode = 37 * hashCode + start;
        hashCode = 37 * hashCode + end;
        return hashCode;
    }
}