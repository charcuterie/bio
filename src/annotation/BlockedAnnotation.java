package annotation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class BlockedAnnotation implements Annotation, Blocked {

    private final String ref;
    private final Strand strand;
    private final int start;
    private final int end;
    private final List<Block> blocks;
    
    public BlockedAnnotation(Block b) {

        ref = b.getReferenceName();
        strand = b.getStrand();
        start = b.getStart();
        end = b.getEnd();
        
        List<Block> tmp = new ArrayList<>();
        tmp.add(b);
        blocks = Collections.unmodifiableList(tmp);
    }
    
    BlockedAnnotation(String ref, int start, int end, Strand strand, List<Block> blocks) {
        this.ref = ref;
        this.start = start;
        this.end = end;
        this.strand = strand;
        this.blocks = Collections.unmodifiableList(blocks);
    }
    
    @Override
    public Strand getStrand() {
        return strand;
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

    @Override
    public String getReferenceName() {
        return ref;
    }

    @Override
    public int getStart() {
        return start;
    }

    @Override
    public int getEnd() {
        return end;
    }
}