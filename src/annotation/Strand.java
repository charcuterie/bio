package annotation;

public enum Strand {

    POSITIVE('+', '+') {
        Strand reverse() { return NEGATIVE; }
    },

    NEGATIVE('-', '-') {
        Strand reverse() { return POSITIVE; }
    },

    UNKNOWN('?', '.') {
        Strand reverse() { return UNKNOWN; }
    };
    
    private char value;
    private char bedValue;
    
    private Strand(char value, char bedValue) {
        this.value = value;
        this.bedValue = bedValue;
    }
    
    public String toBEDString() {
        return "" + bedValue;
    }
    
    @Override
    public String toString() {
        return "" + value;
    }
    
    abstract Strand reverse();
}