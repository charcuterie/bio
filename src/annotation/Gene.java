package annotation;

public class Gene extends BlockedAnnotation {

    private static final int NO_CDS = -1;
    private final int cdsStartPos;
    private final int cdsEndPos;

    public Gene(Block b) {
        super(b);
        cdsStartPos = NO_CDS;
        cdsEndPos = NO_CDS;
    }
    
    public Annotation getCodingRegion() {
        if (cdsStartPos == NO_CDS || cdsEndPos == NO_CDS) {
            return Annotations.EMPTY;
        }
        
        Block cds = new Block(getReferenceName(), cdsStartPos, cdsEndPos, getStrand());
        return intersection(cds);
    }
    
    public Annotation get5UTR() {
        
    }

    public Annotation get3UTR() {
        
    }
}