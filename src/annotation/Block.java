package annotation;

public class Block implements Annotation {

    private final String ref;
    private final Strand strand;
    private final int start;
    private final int end;

    public Block(String ref, int start, int end, Strand strand) {

        if (start >= end) {
            throw new IllegalArgumentException("INSTANTIATION FAIL: Block " +
                "end " + end + " must be greater than block start " + start);
        }
        
        this.ref = ref;
        this.start = start;
        this.end = end;
        this.strand = strand;
    }
    
    @Override
    public Strand getStrand() {
        return strand;
    }

    @Override
    public Stranded reverseComplement() {
        return new Block(ref, start, end, strand.reverse());
    }

    @Override
    public String getReferenceName() {
        return ref;
    }

    @Override
    public int getStart() {
        return start;
    }

    @Override
    public int getEnd() {
        return end;
    }

    public boolean overlaps(Block a) {

        if (a == null) {
            return false;
        }
        
        if (!ref.equals(a.ref)) {
            return false;
        }
        
        return this.start < a.end && a.start < this.end;
    }
}