package annotation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * This class represents an <code>Annotation</code> which is made up of
 * <code>Block</code>s.
 * <p>
 * Most constructors for this class are not exposed. To construct a
 * <code>BlockedAnnotation</code>, use a {@link BlockedBuilder}:
 * <pre>
 * <code>
 * BlockedAnnotation b = (new BlockedBuilder())
 *     .addBlock(new Block("chr2", 1300, 1350, Strand.POSITIVE))
 *     .addBlock(new Block("chr2", 1400, 1450, Strand.POSITIVE))
 *     .build();
 * </code>
 * </pre>
 */
public class BlockedAnnotation extends AnnotationImpl implements Annotation {

    protected final List<Block> blocks;
    
    protected BlockedAnnotation(BlockedBuilder b) {
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
    public Annotation reverseComplement() {
        BlockedBuilder bb = new BlockedBuilder();
        for (Block b : blocks) {
            bb.addBlock(b.reverseComplement());
        }
        return bb.build();
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
    
    /**
     * A builder class for constructing {@link BlockedAnnotation}s.
     * <p>
     * An object of this class can be loaded with <code>Block</code>s, and will
     * construct the corresponding <code>BlockedAnnotation</code> when its
     * <code>build()</code> method is invoked. Any disagreement among the
     * <code>Block</code>s (for example, conflicting reference names) will
     * result in an exception being thrown.
     */
    public static class BlockedBuilder extends AnnotationBuilder {

        protected List<Block> blocks = new ArrayList<>();

        /**
         * Constructs a new builder containing no <code>Block</code>s.
         */
        public BlockedBuilder() {
            super();
        }

        /**
         * Adds all the <code>Block</code>s in the given
         * <code>Collection</code> to this builder.
         * @param bs - the <code>Collection</code> of <code>Block</code>s to
         * add
         * @return this builder for method chaining
         */
        public BlockedBuilder addBlocks(Collection<Block> bs) {
            bs.iterator().forEachRemaining(blocks::add);
            return this;
        }
        
        /**
         * Adds a <code>Block</code> to this builder.
         * @param b - the <code>Block</code> to add
         * @return this builder for method-chaining
         */
        public BlockedBuilder addBlock(Block b) {
            blocks.add(b);
            return this;
        }
        
        /**
         * {@inheritDoc}
         * <p>
         * Information about the returned <code>Annotation</code> (reference
         * name, <code>Strand</code>, etc.) is derived from the
         * <code>Block</code>s within this builder. 
         * @throws IllegalArgumentException if this builder contains no
         * <code>Block</code>s
         * @throws IllegalArgumentException if all of this builder's
         * <code>Block</code>s do not have the same strandedness
         * @throws IllegalArgumentException if all of this builder's
         * <code>Block</code>s do not have the same reference name
         * @throws IllegalArgumentException if any two of this builder's
         * <code>Block</code>s overlap or touch end-to-end
         */
        @Override
        public Annotation build() {
            
            if (blocks.isEmpty()) {
                throw new IllegalArgumentException("Attempted to build an " +
                        "Annotation with no blocks.");
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
            
            return new BlockedAnnotation(this);
        }
    }
}