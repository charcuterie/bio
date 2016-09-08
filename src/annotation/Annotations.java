package annotation;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class Annotations {
    
    public static final Annotation EMPTY = new EmptyAnnotation();

    public static Annotation getBlockedAnnotation(List<Block> bs) {

        if (bs.isEmpty()) {
            return EMPTY;
        }
        
        Collections.sort(bs, Comparator.comparing(Block::getStart)
                                       .thenComparing(Block::getEnd));
        
        Block prevBlock = null;
        Iterator<Block> iter = bs.iterator();
        String ref = "";
        Strand strand = Strand.UNKNOWN;
        int start = 0;
        int end = 0;
        
        while (iter.hasNext()) {
            Block currBlock = iter.next();
            if (prevBlock == null) {
                ref = currBlock.getReferenceName();
                strand = currBlock.getStrand();
                start = currBlock.getStart();
            } else if (prevBlock.getEnd() >= currBlock.getStart()) {
                throw new IllegalArgumentException();
            } else if (!currBlock.getStrand().equals(strand)) {
                throw new IllegalArgumentException();
            } else if (!currBlock.getReferenceName().equals(ref)) {
                throw new IllegalArgumentException();
            }
            end = currBlock.getEnd();
            prevBlock = currBlock;
        }
        
        return new BlockedAnnotation(ref, start, end, strand, bs);
    }
    
    private Annotations() { }
    
    private static class EmptyAnnotation implements Annotation {

        @Override
        public Strand getStrand() {
            return Strand.UNKNOWN;
        }

        @Override
        public Stranded reverseComplement() {
            return this;
        }

        @Override
        public String getReferenceName() {
            return "";
        }

        @Override
        public int getStart() {
            return 0;
        }

        @Override
        public int getEnd() {
            return 0;
        }
    }
}
