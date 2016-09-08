package annotation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Block extends AnnotationImpl implements Annotation {

    public Block(String ref, int start, int end, Strand strand) {
        super(ref, start, end, strand);
    }

    @Override
    public Stranded reverseComplement() {
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
}