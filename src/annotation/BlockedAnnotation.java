package annotation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class BlockedAnnotation extends AnnotationImpl implements Annotation {

    private final List<Block> blocks;
    
    public BlockedAnnotation(Block b) {
        super(b.getReferenceName(), b.getStart(), b.getEnd(), b.getStrand());
        
        List<Block> tmp = new ArrayList<>();
        tmp.add(b);
        blocks = Collections.unmodifiableList(tmp);
    }
    
    BlockedAnnotation(String ref, int start, int end, Strand strand, List<Block> blocks) {
        super(ref, start, end, strand);
        this.blocks = Collections.unmodifiableList(blocks);
    }

    @Override
    public Stranded reverseComplement() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getNumberOfBlocks() {
        return blocks.size();
    }

    @Override
    public Iterator<Block> getBlocks() {
        return blocks.iterator();
    }
}