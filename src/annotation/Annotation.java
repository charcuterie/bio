package annotation;

import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;

public interface Annotation extends Stranded {

    public String getReferenceName();
    
    public int getStart();
    
    public int getEnd();
    
    public int getNumberOfBlocks();
    
    public int getSize();
    
    public int getSpan();
    
    public Iterator<Block> getBlocks();
    
    public Stream<Block> getBlockStream();
    
    public boolean overlaps(Annotation other);
    
    public Optional<Annotation> minus(Annotation other);
    
    public Annotation union(Annotation other);
    
    public Optional<Annotation> intersection(Annotation other);
    
    public Optional<Annotation> xor(Annotation other);
}