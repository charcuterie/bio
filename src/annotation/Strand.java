package annotation;

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
        
    public static Strand fromString(String s) {
        switch(s) {
        case "+":
            return Strand.POSITIVE;
        case "-":
            return Strand.NEGATIVE;
        case ".":
            return Strand.BOTH;
        default:
            throw new IllegalArgumentException("Unrecognized string");
        }
    }
    
    @Override
    public String toString() {
        return "" + value;
    }
    
    abstract Strand reverse();
}