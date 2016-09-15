package sequence;

/**
 * This interface defines the behavior of a nucleotide sequence.
 * <p>
 * All objects of classes which implement this interface
 * <ul>
 * <li>be named
 * <li>be associated with a string of nucleotide bases
 */
public interface Sequence {

    /**
     * Gets the bases of this as a <code>String</code>.
     */
    public String getBases();
    
    /**
     * Gets the name of this.
     */
    public String getName();
}
