package annotation;

import java.awt.Color;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.stream.IntStream;

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
    
    public double getScore() {
        return score;
    }
    
    public Color getColor() {
        return color;
    }

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
    
    public static class BEDBuilder extends GeneBuilder {
        
        private double score;
        private Color color;
        
        public BEDBuilder() {
            super();
            score = DEFAULT_SCORE;
            color = DEFAULT_COLOR;
        }
        
        public BEDBuilder addScore(double score) {
            this.score = score;
            return this;
        }
        
        public BEDBuilder addColor(Color color) {
            this.color = color;
            return this;
        }
        
        public BEDBuilder addColor(int r, int g, int b) {
            color = new Color(r, g, b);
            return this;
        }
        
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
            
            return new BEDFileRecord(this);
        }
    }
}