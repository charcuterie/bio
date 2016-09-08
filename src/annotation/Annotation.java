package annotation;

import java.util.Iterator;

public interface Annotation extends Stranded {

    public String getReferenceName();
    
    public int getStart();
    
    public int getEnd();
    
    public int getNumberOfBlocks();
    
    public Iterator<Block> getBlocks();
    
    public boolean overlaps(Annotation other);
    
    public Annotation minus(Annotation other);
    
    public Annotation union(Annotation other);
    
    public Annotation intersection(Annotation other);
    
    public Annotation xor(Annotation other);
}