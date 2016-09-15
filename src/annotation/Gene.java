package annotation;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * This class represents an <code>Annotation</code> with a name and an optional
 * coding region.
 * <p>
 * Most constructors for this class are not exposed. To construct a
 * <code>Gene</code>, use a {@link GeneBuilder}:
 * <pre>
 * <code>
 * Gene g = (new GeneBuilder())
 *     .addBlock(new Block("chr2", 1300, 1350, Strand.POSITIVE))
 *     .addBlock(new Block("chr2", 1400, 1450, Strand.POSITIVE))
 *     .addName("myGene")
 *     .addCodingRegion(1325, 1425)
 *     .build();
 * </code>
 * </pre>
 */
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
    
    /**
     * Gets the name of this.
     * <p>
     * If this object doesn't have a name, this method returns the empty <code>String</code>
     */
    public String getName() {
        return name;
    }

    /**
     * Whether or not this has a coding region.
     */
    public boolean hasNoCodingRegion() {
        return cdsStartPos == cdsEndPos; 
    }
    
    /**
     * Gets the coding region of this object.
     * <p>
     * @return the coding region of this object, if it exists, as an
     * <code>Annotation</code> wrapped in an <code>Optional</code>; otherwise,
     * an empty <code>Optional</code>
     */
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
    
    /**
     * A builder class for constructing {@link Gene}s.
     * <p>
     * An object of this class can be loaded with <code>Block</code>s, and will
     * construct the corresponding <code>Gene</code> when its
     * <code>build()</code> method is invoked. Any disagreement among the
     * <code>Block</code>s (for example, conflicting reference names) will
     * result in an exception being thrown.
     * <p>
     * Building a <code>Gene</code> with no name will cause the name to default
     * to the empty <code>String</code>. A <code>Gene</code>'s coding region is
     * also optional.
     */
    public static class GeneBuilder extends BlockedBuilder {
        
        protected String name = "";
        protected int cdsStart;
        protected int cdsEnd;
        protected boolean newCds = false;
        
        /**
         * Constructs a new builder containing no <code>Block</code>s.
         */
        public GeneBuilder() {
            super();
        }

        /**
         * Adds a name to this builder.
         * <p>
         * Calling this method a second time will simply overwrite the name
         * that was passed the first time.
         * @param name - the name to add
         * @return this builder for method-chaining
         */
        public GeneBuilder addName(String name) {
            this.name = name;
            return this;
        }
        
        /**
         * Adds a coding region to this builder.
         * <p>
         * The values passed to this method are not checked for consistency
         * until the <code>build()</code> method is invoked. Calling this
         * method a second time will simply overwrite the values that were
         * passed the first time.
         * @param cdsStart - the start coordinate of the coding region
         * @param cdsEnd - the end coordinate of the coding region
         * @return this builder for method-chaining
         */
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

        // TODO a lot of code duplication. Can we separate out the validation code?
        
        /**
         * Builds and returns the <code>Gene</code> represented by this
         * builder.
         * <p>
         * Information about the returned <code>Gene</code> not explicitly set,
         * such as reference name and <code>Strand</code>, is derived from the
         * <code>Block</code>s within this builder. If no name is set, the
         * returned <code>Gene</code>'s name is the empty <code>String</code>.
         * If no coding region is set, the returned <code>Gene</code> will not
         * have a coding region.
         * @throws IllegalArgumentException if this builder contains no
         * <code>Block</code>s
         * @throws IllegalArgumentException if all of this builder's
         * <code>Block</code>s do not have the same strandedness
         * @throws IllegalArgumentException if all of this builder's
         * <code>Block</code>s do not have the same reference name
         * @throws IllegalArgumentException if any two of this builder's
         * <code>Block</code>s overlap or touch end-to-end
         * @throws IllegalArgumentException if the end of the coding region
         * occurs before the start
         * @throws IllegalArgumentException if the coding region starts before
         * the start of the first <code>Block</code>
         * @throws IllegalArgumentException if the coding region ends after the
         * end of the last <code>Block</code>
         */
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
                    throw new IllegalArgumentException("Attempted to build an " +
                            "Annotation with overlapping blocks.");
                } else if (!currBlock.getStrand().equals(strand)) {
                    throw new IllegalArgumentException("Attempted to build an " +
                            "Annotation with blocks of different strandednesses.");
                } else if (!currBlock.getReferenceName().equals(ref)) {
                    throw new IllegalArgumentException("Attempted to build an " +
                            "Annotation with blocks from different references.");
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
                    throw new IllegalArgumentException("Attempted to build an " +
                            "Annotation with an invalid coding region. " +
                            "cdsStart must be greater than or equal to " +
                            "cdsEnd. cdsStart: " + cdsStart + ", cdsEnd: " +
                            cdsEnd);
                }
                if (cdsStart < start) {
                    throw new IllegalArgumentException("Attempted to build an " +
                            "Annotation with an invalid coding region. " +
                            "cdsStart must be greater than or equal to " +
                            "the starting reference position. cdsStart: " + 
                            cdsStart + ", refStart: " + start);
                }
                if (cdsEnd > end) {
                    throw new IllegalArgumentException("Attempted to build an " +
                            "Annotation with an invalid coding region. " +
                            "cdsStart must be less than or equal to " +
                            "the ending reference position. cdsStart: " + 
                            cdsStart + ", refEnd: " + end);
                }
            } else {
                cdsStart = start;
                cdsEnd = start;
            }
            
            return new Gene(this);
        }
    }
}