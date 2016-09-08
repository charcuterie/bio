package sequence;

public enum PhredEncoding {

    SANGER(33),
    ILLUMINA_13(64);
    
    private final int offset;
    
    private PhredEncoding(int offset) {
        this.offset = offset;
    }
    
    public final int offset() {
        return this.offset;
    }
}