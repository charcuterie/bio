package annotation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class BlockedAnnotation extends AnnotationImpl implements Annotation {

    protected final List<Block> blocks;
    
    public BlockedAnnotation(BlockedBuilder b) {
        super(b);
        this.blocks = Collections.unmodifiableList(b.blocks);
    }
    
    public BlockedAnnotation(Annotation b) {
        super(b.getReferenceName(), b.getStart(), b.getEnd(), b.getStrand());
        
        List<Block> tmp = new ArrayList<>();
        b.getBlocks().forEachRemaining(tmp::add);
        blocks = Collections.unmodifiableList(tmp);
    }
    
    BlockedAnnotation(String ref, int start, int end, Strand strand, List<Block> blocks) {
        super(ref, start, end, strand);
        this.blocks = Collections.unmodifiableList(blocks);
    }
    
    @Override
    public int getSize() {
        return getBlockStream().mapToInt(b -> b.getSize()).sum();
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        
        if (!(o instanceof BlockedAnnotation)) {
            return false;
        }
        
        BlockedAnnotation other = (BlockedAnnotation) o;
        
        return ref.equals(other.ref) &&
               start == other.start &&
               end == other.end &&
               strand.equals(other.strand) &&
               blocks.equals(other.blocks);
    }
    
    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode = 37 * hashCode + ref.hashCode();
        hashCode = 37 * hashCode + strand.hashCode();
        hashCode = 37 * hashCode + start;
        hashCode = 37 * hashCode + end;
        for (Block b : blocks) {
            hashCode = 37 * hashCode + b.hashCode();
        }
        return hashCode;
    }
    
    public static class BlockedBuilder extends AnnotationBuilder {

        protected List<Block> blocks = new ArrayList<>();
        
        public BlockedBuilder() {
            super();
        }

        public BlockedBuilder addBlocks(Collection<Block> bs) {
            bs.iterator().forEachRemaining(blocks::add);
            return this;
        }
        
        public BlockedBuilder addBlock(Block b) {
            blocks.add(b);
            return this;
        }
        
        @Override
        public Annotation build() {
            
            if (blocks.isEmpty()) {
                throw new IllegalArgumentException("Attempted to build a " +
                        "BlockedAnnotation with no blocks.");
            }
            
            Collections.sort(blocks, Comparator.comparing(Block::getStart)
                                               .thenComparing(Block::getEnd));

            Block prevBlock = null;
            Iterator<Block> iter = blocks.iterator();
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
            
            this.start = start;
            this.end = end;
            this.ref = ref;
            this.strand = strand;
            
            return new BlockedAnnotation(this);
        }
    }
}