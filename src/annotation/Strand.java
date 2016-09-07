package annotation;

public enum Strand {

    POSITIVE('+') {
        Strand reverse() { return NEGATIVE; }
    },

    NEGATIVE('-') {
        Strand reverse() { return POSITIVE; }
    },

    UNKNOWN('?') {
        Strand reverse() { return UNKNOWN; }
    };
    
    private char value;
    
    private Strand(char value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        return "" + value;
    }
    
    abstract Strand reverse();
}