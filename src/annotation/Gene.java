package annotation;

import java.util.Iterator;
import java.util.Optional;

public class Gene implements Blocked, Annotation {

    private final BlockedAnnotation blocks;
    
    private static final int NO_CDS = -1;
    private final int cdsStartPos;
    private final int cdsEndPos;

    public Gene(Block b) {
        blocks = new BlockedAnnotation(b);
        cdsStartPos = NO_CDS;
        cdsEndPos = NO_CDS;
    }
    
    public Optional<Blocked> getCodingRegion() {
        if (cdsStartPos == NO_CDS || cdsEndPos == NO_CDS) {
            return Optional.empty();
        }
        
    }
    
    public Optional<Annotation> get5UTR() {
        
    }

    public Optional<Annotation> get3UTR() {
        
    }

    @Override
    public Strand getStrand() {
        return blocks.getStrand();
    }

    @Override
    public Stranded reverseComplement() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getReferenceName() {
        return blocks.getReferenceName();
    }

    @Override
    public int getStart() {
        return blocks.getStart();
    }

    @Override
    public int getEnd() {
        return blocks.getEnd();
    }

    @Override
    public int getNumberOfBlocks() {
        return blocks.getNumberOfBlocks();
    }

    @Override
    public Iterator<Block> getBlocks() {
        return blocks.getBlocks();
    }
}