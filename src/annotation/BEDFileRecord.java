package annotation;

import java.awt.Color;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.stream.IntStream;

/**
 * This class represents a record contained in one line of a BED file.
 * <p>
 * Constructors for this class are not exposed. To construct a
 * <code>BEDFileRecord</code>, use a {@link BEDBuilder}:
 * <pre>
 * <code>
 * BEDFileRecord b = (new BEDBuilder())
 *     .addBlock(new Block("chr2", 1300, 1350, Strand.POSITIVE))
 *     .addBlock(new Block("chr2", 1400, 1450, Strand.POSITIVE))
 *     .addName("myBedName")
 *     .addCodingRegion(1325, 1425)  // This is drawn as a thick line
 *     .addColor(Color.LILAC)
 *     .addScore(0.54321)
 *     .build();
 * </code>
 * </pre>
 * Unspecified fields will have sensible defaults.
 */
public final class BEDFileRecord extends Gene implements AnnotationFileRecord {

    private final double score;
    private final Color color;
    
    private final static double DEFAULT_SCORE = 0;
    private final static Color DEFAULT_COLOR = Color.BLACK;
    private static final int[] VALID_NUM_FIELDS = new int[] {3, 4, 5, 6, 8, 9, 12};
    private static final int MAX_FIELDS = 12;
    
    protected BEDFileRecord(BEDBuilder b) {
        super(b);
        this.score = b.score;
        this.color = b.color;
    }
    
    /**
     * Gets the score of this.
     */
    public double getScore() {
        return score;
    }
    
    /**
     * Gets the color of this.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Converts this to a properly formatted <code>String</code> suitable for
     * outputting directly to a file.
     * <p>
     * Most fields in a BED file are optional. Use the <code>numFields</code>
     * parameter to specify the number of fields contained in the returned
     * <code>String</code>.
     * @param numFields - the number of fields to output
     */
    public String toFormattedString(int numFields) {
        if (IntStream.of(VALID_NUM_FIELDS).noneMatch(x -> x == numFields)) {
            throw new IllegalArgumentException("Attempted to convert BED " + 
                    "record to string, but requested numFields " + numFields
                    + ". Number of fields must be either 3, 4, 5, 6, 8, 9 " + 
                    "12.");
        }
        
        // Required fields
        final StringBuilder sb = new StringBuilder();
        sb.append(ref + "\t" + start + "\t" + end);
        if (numFields == 3) return sb.toString();
        
        // Name
        String bedName = name.isEmpty() ? "." : name;
        sb.append("\t" + bedName);
        if (numFields == 4) return sb.toString();
        
        // Score
        sb.append("\t" + score);
        if (numFields == 5) return sb.toString();
        
        // Strand
        sb.append("\t" + strand.toString());
        if (numFields == 6) return sb.toString();
        
        // Thick
        sb.append("\t" + cdsStartPos + "\t" + cdsEndPos);
        if (numFields == 8) return sb.toString();
        
        sb.append("\t" + color.getRed() + "," + color.getGreen() + "," +
                color.getBlue());
        if (numFields == 9) return sb.toString();
        
        sb.append("\t" + getNumberOfBlocks() + "\t");
        Iterator<Block> blocks = getBlocks();
        while (blocks.hasNext()) {
            Annotation block = blocks.next();
            sb.append(block.getSize() + ","); // trailing comma after last block is OK
        }
        sb.append("\t");
        blocks = getBlocks();
        while (blocks.hasNext()) {
            Annotation block = blocks.next();
            sb.append((block.getStart() - getStart()) + ","); // trailing comma after last is OK
        }
        return sb.toString();
        
    }

    @Override
    public String toFormattedString() {
        return toFormattedString(MAX_FIELDS);
    }

    /**
     * Parses a BED-formatted <code>String</code> and returns the corresponding
     * annotation as a <code>BEDFileRecord</code>
     * @param s - the <code>String</code> to parse
     */
    public static BEDFileRecord fromFormattedString(String s) {
        BEDBuilder bb = new BEDBuilder();
        String[] fields = s.trim().split("\\s+");
        int numFields = fields.length;
        
        if (IntStream.of(VALID_NUM_FIELDS).noneMatch(x -> x == numFields)) {
            throw new IllegalArgumentException("fromFormattedString() was passed a String with "
                    + numFields + " fields. A properly formatted BED String must have between three and"
                    + " twelve fields, and cannot have seven, ten or eleven fields.");
        }

        // Fields 0, 1, and 2 are guaranteed to be present; otherwise this isn't a valid BED file.
        String chrom = fields[0];
        int chromStart = Integer.parseInt(fields[1]);
        int chromEnd = Integer.parseInt(fields[2]);

        
        // All fields present. This is an Annotation with multiple blocks
        if (numFields == 12) {
            Strand strand = Strand.fromString(fields[5]);
            int blockCount = Integer.parseInt(fields[9]);
            int[] blockSizes = parseCommaSeparatedString(fields[10]);
            int[] blockStarts = parseCommaSeparatedString(fields[11]);
            
            if (blockStarts.length != blockCount || blockSizes.length != blockCount) {
                throw new IllegalArgumentException("Malformed BED String. blockCount = " + blockCount
                        + ", blockSizes = " + blockSizes.length + ", and blockStarts = " + blockStarts.length +
                        ". All should be equal.");
            }
            
            // TODO check consistency between field[2] and last block.
            
            for (int i = 0; i < blockCount; i++) {
                bb.addBlock(new Block(chrom, chromStart + blockStarts[i], chromStart + blockStarts[i] + blockSizes[i], strand));
            }
        
        // This is an Annotation with one block and strand information.
        } else if (numFields >= 6) {
            Strand strand = Strand.fromString(fields[5]);
            bb.addBlock(new Block(chrom, chromStart, chromEnd, strand));

        // This is an Annotation with one block and no strand information: default to Strand.BOTH.
        } else {
            bb.addBlock(new Block(chrom, chromStart, chromEnd, Strand.BOTH));
        }
        
        // Annotation has been constructed. Add the rest of the fields.

        // Add name
        if (numFields >= 4) {
            bb.addName(fields[3]);
        }
        
        // Add score
        if (numFields >= 5) {
            bb.addScore(Double.parseDouble(fields[4]));
        }
        
        // No need to add strand information. Should be contained in the blocks.
        
        // Add line thickness.
        if (numFields >= 8) {
            bb.addCodingRegion(Integer.parseInt(fields[6]), Integer.parseInt(fields[7]));
        }
        
        // Add color.
        if (numFields >= 9) {
            if (fields[8].equals(".") || fields[8].equals("0")) {
                bb.addColor(DEFAULT_COLOR);
            } else {
                int[] colorVals = parseCommaSeparatedString(fields[8]);
                if (colorVals.length != 3) {
                    throw new IllegalArgumentException("Formatted BED string has invalid color value: " + fields[8]);
                }
                bb.addColor(new Color(colorVals[0], colorVals[1], colorVals[2]));
            }
        }
        
        // All done. Return the BED record.
        return bb.build();
    }
    
    // TODO Look into using Pattern regex?
    // Helper method for fromFormattedString(). Handles parsing of the blockStarts, blockSizes and color BED fields.
    private static int[] parseCommaSeparatedString(String s) {
        String[] tmp = s.endsWith(",") ? s.substring(0, s.length() - 1).split(",") : s.split(",");
        int[] rtrn = new int[tmp.length];
        for (int i = 0; i < rtrn.length; i++) {
            rtrn[i] = Integer.parseInt(tmp[i]);
        }
        return rtrn;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        
        if (!(o instanceof BEDFileRecord)) {
            return false;
        }
        
        BEDFileRecord other = (BEDFileRecord) o;
        
        return ref.equals(other.ref) &&
               name.equals(other.name) &&
               start == other.start &&
               end == other.end &&
               strand.equals(other.strand) &&
               cdsStartPos == other.cdsStartPos &&
               cdsEndPos == other.cdsEndPos &&
               blocks.equals(other.blocks) &&
               score == other.score &&
               color.equals(other.color);
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
        hashCode = 37 * hashCode + Double.hashCode(score);
        hashCode = 37 * hashCode + color.hashCode();
        for (Block b : blocks) {
            hashCode = 37 * hashCode + b.hashCode();
        }

        return hashCode;
    }
    
    /**
     * A builder class for constructing {@link BEDFileRecord}s.
     * <p>
     * An object of this class can be loaded with <code>Block</code>s, and will
     * construct the corresponding <code>BEDFileRecord</code> when its
     * <code>build()</code> method is invoked. Any disagreement (for example,
     * conflicting reference names among the blocks, or a coding region outside
     * the bounds of the annotation) will result in an exception being thrown.
     * <p>
     * Many fields in a BED file are optional. If these fields are not
     * explicitly provided to this builder, the following will be used as
     * defaults:
     * <ul>
     * <li>score: <code>0</code>
     * <li>name: <code>""</code>
     * <li>color: <code>Color.BLACK</code>
     * <li>coding region: the <code>BEDFileRecord</code> will have no coding
     * region, and when output as a formatted <code>String</code>, the
     * thickStart and thickEnd fields will both default to the starting
     * reference coordinate
     * </ul> 
     */
    public static class BEDBuilder extends GeneBuilder {
        
        private double score;
        private Color color;
        
        /**
         * Constructs an empty builder.
         * <p>
         * The score is initialized to <code>0</code> and the color is
         * initialized to <code>Color.BLACK</code>.
         */
        public BEDBuilder() {
            super();
            score = DEFAULT_SCORE;
            color = DEFAULT_COLOR;
        }
        
        /**
         * Adds a score to this builder.
         * @param score - the score to add
         * @return this builder form method-chaining
         */
        public BEDBuilder addScore(double score) {
            this.score = score;
            return this;
        }
        
        /**
         * Adds a color to this builder.
         * @param color - the color to add
         * @return this builder for method-chaining
         */
        public BEDBuilder addColor(Color color) {
            this.color = color;
            return this;
        }
        
        /**
         * Adds a color, specified by its RGB values, to this builder.
         * @param r - the red value, between 0 and 255 inclusive
         * @param g - the green value, between 0 and 255 inclusive
         * @param b - the blue value, between 0 and 255 inclusive
         * @return this builder for method-chaining
         * @throws IllegalArgumentException if <code>r</code>, <code>g</code>
         * or <code>b</code> are outside the range 0 to 255 inclusive
         */
        public BEDBuilder addColor(int r, int g, int b) {
            color = new Color(r, g, b);
            return this;
        }
        
        /**
         * Adds a color, specified by its combined RGB value, to this builder.
         * <p>
         * The value <code>rgb</code> should have its red value in bits 16-23,
         * its green value in bits 8-15, and its blue value in bits 0-7.
         * @param rgb - the combined RGB value
         * @return this builder for method-chaining
         */
        public BEDBuilder addColor(int rgb) {
            this.color = new Color(rgb);
            return this;
        }
        
        @Override
        public BEDBuilder addBlock(Block b) {
            return (BEDBuilder) super.addBlock(b);
        }
        
        @Override
        public BEDBuilder addBlocks(Collection<Block> bs) {
            return (BEDBuilder) super.addBlocks(bs);
        }
        
        @Override
        public BEDBuilder addName(String s) {
            return (BEDBuilder) super.addName(s);
        }
        
        @Override
        public BEDBuilder addCodingRegion(int cdsStart, int cdsEnd) {
            return (BEDBuilder) super.addCodingRegion(cdsStart, cdsEnd);
        }
        
        @Override
        public BEDFileRecord build() {
            if (blocks.isEmpty()) {
                throw new IllegalArgumentException();
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
            
            return new BEDFileRecord(this);
        }
    }
}