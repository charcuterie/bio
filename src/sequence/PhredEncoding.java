package sequence;

/**
 * An enumeration of Phred quality-score encodings.
 */
public enum PhredEncoding {

    SANGER(33),
    ILLUMINA_13(64);
    
    private final int offset;
    
    private PhredEncoding(int offset) {
        this.offset = offset;
    }
    
    /**
     * The offset value of this <code>PhredEncoding</code>.
     * <p>
     * As an example,
     * <code>PhredEncoding.SANGER.offset()</code> evaluates to 33, because
     * the Sanger encoding uses the "!" character, which has an ASCII-value of
     * 33, to represent a quality of 0. Likewise, the Sanger encoding uses the
     * "I" character, which has an ASCII of 73, to represent a quality of 40.
     */
    public final int offset() {
        return this.offset;
    }
}