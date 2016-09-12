package annotation;

import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;

import datastructures.Interval;

public interface Annotation extends Interval {

    /**
     * Gets the name of the reference that this annotation belongs to.
     * <p>
     * The reference name is typically a chromosome such as "chr1" or
     * "chrX".
     */
    public String getReferenceName();
    
    /**
     * Gets the start coordinate of this <code>Annotation</code>.
     * <p>
     * Annotation coordinates are zero-based, closed-open. 
     */
    @Override
    public int getStart();
    
    /**
     * Gets the end coordinate of this <code>Annotation</code>.
     * <p>
     * Annotation coordinates are zero-based, closed-open.
     */
    @Override
    public int getEnd();
    
    /**
     * Gets the number of blocks that compose this <code>Annotation</code>.
     */
    public int getNumberOfBlocks();
    
    /**
     * Gets the size of this <code>Annotation</code>.
     * <p>
     * The size is the sum of the sizes of this <code>Annotation</code>'s
     * blocks. In typical usage where exons are represented as blocks and
     * introns are implied as the gaps between the blocks, this method would
     * return the total exonic size.
     */
    public int getSize();
    
    /**
     * Gets the span of this <code>Annotation</code>.
     * <p>
     * The span is simply the distance from the 5'-end to the 3'-end of this
     * <code>Annotation</code>. This method will include introns or gaps
     * between blocks when calculating the span.
     * @return
     */
    public int getSpan();
    
    /**
     * Gets the {@link Strand} of this <code>Annotation</code>.
     */
    public Strand getStrand();
    
    public Annotation reverseComplement();
    
    /**
     * Gets an <code>Iterator</code> over the blocks of this
     * <code>Annotation</code>.
     */
    public Iterator<Block> getBlocks();
    
    /**
     * Gets a <code>Stream</code> of the blocks of this
     * <code>Annotation</code>.
     */
    public Stream<Block> getBlockStream();
    
    public boolean overlaps(Annotation other);
    
    public Optional<Annotation> minus(Annotation other);
    
    public Annotation union(Annotation other);
    
    public Optional<Annotation> intersection(Annotation other);
    
    public Optional<Annotation> xor(Annotation other);
}