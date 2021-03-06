package sequence;

import annotation.AnnotationFileRecord;
import annotation.Strand;

/**
 * 
 */
public class FastaSequence implements Sequence, AnnotationFileRecord {

    protected final String sequence;
    protected final String name;
    protected static final String nl = System.getProperty("line.separator");
    
    public FastaSequence(String name, String seq) {
        if (name == null) {
            throw new IllegalArgumentException("INSTANTIATION FAIL: " +
                    "Attempted to construct Sequence with null name.");
        }
        if (seq == null) {
            throw new IllegalArgumentException("INSTANTIATION FAIL: " +
                    "Attempted to construct Sequence with null sequence.");
            
        }
        this.name = name;
        this.sequence = seq;
    }
    
    @Override
    public String getBases() {
        return sequence;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    public FastaSequence changeName(String name) {
        return new FastaSequence(name, sequence);
    }
    
    public Strand getStrand() {
        return Strand.BOTH;
    }
    
    public FastaSequence reverseComplement() {
        return reverseComplement(name);
    }
    
    public FastaSequence reverseComplement(String name) {
        return new FastaSequence(name, Sequences.reverseComplement(sequence));
    }
    
    public FastaSequence subsequence(int start, int end) {
        return subsequence(name, start, end);
    }
    
    public FastaSequence subsequence(String name, int start, int end) {
        String subseq = sequence.substring(Math.max(start, 0),
                        Math.min(end, sequence.length()));
        return new FastaSequence(name, subseq);
    }
    
    public int length() {
        return sequence.length();
    }
    
    public boolean isPolyA() {
        return sequence.chars().allMatch(c -> c == 'a' || c == 'A') ||
               sequence.chars().allMatch(c -> c == 't' || c == 'T');
    }
    
    public String toFasta() {
        return ">" + name + nl + sequence;
    }
    
    @Override
    public String toFormattedString() {
        return toFasta();
    }
    
    @Override
    public String toString() {
        return name + ": " + sequence;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        
        if (!(o instanceof FastaSequence)) {
            return false;
        }
        
        FastaSequence other = (FastaSequence) o;
        
        return name.equals(other.name) && sequence.equals(other.sequence);
    }
    
    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode = 37 * hashCode + name.hashCode();
        hashCode = 37 * hashCode + sequence.hashCode();
        return hashCode;
    }
}