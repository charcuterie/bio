package annotation;

import java.awt.Color;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.stream.IntStream;

public class BEDFileRecord extends Gene implements AnnotationFileRecord {

    private final double score;
    private final Color color;
    
    private final static double DEFAULT_SCORE = 0;
    private final static Color DEFAULT_COLOR = Color.BLACK;
    private static final int[] VALID_NUM_FIELDS = new int[] {3, 4, 5, 6, 8, 9, 12};
    private static final int MAX_FIELDS = 12;
    
    public BEDFileRecord(BEDBuilder b) {
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
        sb.append("\t" + strand.toBEDString());
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
    
    // TODO complete
    public static BEDFileRecord fromFormattedString(String s) {
        return null;
    }
    
    public static class BEDBuilder extends GeneBuilder {
        
        private double score;
        private Color color;
        
        public BEDBuilder() {
            super();
            score = DEFAULT_SCORE;
            color = DEFAULT_COLOR;
        }
        
        public BEDBuilder score(double score) {
            this.score = score;
            return this;
        }
        
        public BEDBuilder color(Color color) {
            this.color = color;
            return this;
        }
        
        public BEDBuilder color(int r, int g, int b) {
            color = new Color(r, g, b);
            return this;
        }
        
        public BEDBuilder color(int rgb) {
            this.color = new Color(rgb);
            return this;
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