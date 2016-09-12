package annotation;

/**
 * An enumeration of strands or orientations.
 */
public enum Strand {

    POSITIVE('+') {
        Strand reverse() { return NEGATIVE; }
    },

    NEGATIVE('-') {
        Strand reverse() { return POSITIVE; }
    },

    BOTH('.') {
        Strand reverse() { return BOTH; }
    };
    
    private char value;
    
    private Strand(char value) {
        this.value = value;
    }

    /**
     * Returns a <code>Strand</code> enum corresponding to a given
     * <code>String</code>.
     * <ul><code>
     * <li>"+" - Strand.POSITIVE
     * <li>"-" - Strand.NEGATIVE
     * <li>"." - Strand.BOTH
     * </code></ul>
     * @param s - the <code>String</code> corresponding to a <code>Strand</code>
     * @throws IllegalArgumentException if passed a <code>String</code> which
     * does not correspond to a <code>Strand</code>
     */
    public static Strand fromString(String s) {
        switch(s) {
        case "+":
            return Strand.POSITIVE;
        case "-":
            return Strand.NEGATIVE;
        case ".":
            return Strand.BOTH;
        default:
            throw new IllegalArgumentException("String " + s + " does not " +
                    "correspond to a known Strand");
        }
    }
    
    @Override
    public String toString() {
        return "" + value;
    }
    
    /**
     * Returns the reverse of this <code>Strand</code>.
     * <ul><code>
     * <li>reverse(Strand.POSITIVE) == Strand.NEGATIVE
     * <li>reverse(Strand.NEGATIVE) == Strand.POSITIVE
     * <li>reverse(Strand.BOTH) == Strand.BOTH
     * </code></ul>
     */
    abstract Strand reverse();
}