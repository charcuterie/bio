package sequence;

public final class Sequences {
    
    private Sequences() { }
    
    public static String reverseComplement(String s) {
        return reverse(complement(s));
    }
    
    public static String reverse(String s) {
        return (new StringBuilder(s).reverse().toString());
    }
    
    public static String complement(String s) {
        char[] cs = s.toCharArray();
        char[] rtrn = new char[cs.length];
        for (int i = 0; i < cs.length; i++) {
            rtrn[i] = complement(cs[i]);
        }
        return String.valueOf(rtrn);
    }
    
    public static char complement(char c) {
        switch (c) {
        case 'A': return 'T';
        case 'C': return 'G';
        case 'G': return 'C';
        case 'T': return 'A';
        case 'N': return 'N';
        case 'a': return 't';
        case 'c': return 'g';
        case 'g': return 't';
        case 't': return 'a';
        case 'n': return 'n';
        default: throw new IllegalArgumentException("Unsupported base: " + c);
        }
    }
}