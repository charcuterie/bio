package annotation;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class Gene extends BlockedAnnotation {

    protected final String name;
    
    protected final int cdsStartPos;
    protected final int cdsEndPos;

    protected Gene(GeneBuilder b) {
        super(b);
        this.name = b.name;
        this.cdsStartPos = b.cdsStart;
        this.cdsEndPos = b.cdsEnd;
    }
    
    public Gene(Annotation a, String name, int cdsStartPos, int cdsEndPos) {
        super(a);
        this.name = name;
        this.cdsStartPos = cdsStartPos;
        this.cdsEndPos = cdsEndPos;
    }
    
    Gene(String ref, String name, int start, int end, int cdsStartPos, int cdsEndPos, Strand strand, List<Block> blocks) {
        super(ref, start, end, strand, blocks);
        this.name = name;
        this.cdsStartPos = cdsStartPos;
        this.cdsEndPos = cdsEndPos;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean hasNoCodingRegion() {
        return cdsStartPos == cdsEndPos; 
    }
    
    public Optional<Annotation> getCodingRegion() {
        if (cdsStartPos == cdsEndPos) {
            return Optional.empty();
        }
        
        Block cds = new Block(getReferenceName(), cdsStartPos, cdsEndPos, getStrand());
        return intersection(cds);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        
        if (!(o instanceof Gene)) {
            return false;
        }
        
        Gene other = (Gene) o;
        
        return ref.equals(other.ref) &&
               name.equals(other.name) &&
               start == other.start &&
               end == other.end &&
               strand.equals(other.strand) &&
               cdsStartPos == other.cdsStartPos &&
               cdsEndPos == other.cdsEndPos &&
               blocks.equals(other.blocks);
    }
    
    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode = 37 * hashCode + ref.hashCode();
        hashCode = 37 * hashCode + strand.hashCode();
        hashCode = 37 * hashCode + start;
        hashCode = 37 * hashCode + end;
        hashCode = 37 * hashCode + name.hashCode();
        hashCode = 37 * hashCode + cdsStartPos;
        hashCode = 37 * hashCode + cdsEndPos;
        for (Block b : blocks) {
            hashCode = 37 * hashCode + b.hashCode();
        }

        return hashCode;
    }
    
    public static class GeneBuilder extends BlockedBuilder {
        
        protected String name = "";
        protected int cdsStart;
        protected int cdsEnd;
        protected boolean newCds = false;
        
        public GeneBuilder() {
            super();
        }

        public GeneBuilder addName(String name) {
            this.name = name;
            return this;
        }
        
        public GeneBuilder addCodingRegion(int cdsStart, int cdsEnd) {
            this.cdsStart = cdsStart;
            this.cdsEnd = cdsEnd;
            newCds = true;
            return this;
        }
        
        @Override
        public GeneBuilder addBlock(Block b) {
            return (GeneBuilder) super.addBlock(b);
        }
        
        @Override
        public GeneBuilder addBlocks(Collection<Block> bs) {
            return (GeneBuilder) super.addBlocks(bs);
        }
        
        @Override
        public Gene build() {
            
            if (blocks.isEmpty()) {
                throw new IllegalArgumentException("Attempted to build a gene " +
                        "with no blocks.");
            }
            
            Collections.sort(blocks, Comparator.comparing(Block::getStart)
                                               .thenComparing(Block::getEnd));

            Block prevBlock = null;
            Iterator<Block> iter = blocks.iterator();
            String ref = "";
            Strand strand = Strand.BOTH;
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
            
            if (newCds) {
                if (cdsStart >= cdsEnd) {
                    throw new IllegalArgumentException();
                }
                if (cdsStart < start) {
                    throw new IllegalArgumentException();
                }
                if (cdsEnd > end) {
                    throw new IllegalArgumentException();
                }
            } else {
                cdsStart = start;
                cdsEnd = start;
            }
            
            return new Gene(this);
        }
    }
}