package annotation;

import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;

import datastructures.Interval;

public interface Annotation extends Interval {

    public String getReferenceName();
    
    public int getStart();
    
    public int getEnd();
    
    public int getNumberOfBlocks();
    
    public int getSize();
    
    public int getSpan();
    
    public Strand getStrand();
    
    public Annotation reverseComplement();
    
    public Iterator<Block> getBlocks();
    
    public Stream<Block> getBlockStream();
    
    public boolean overlaps(Annotation other);
    
    public Optional<Annotation> minus(Annotation other);
    
    public Annotation union(Annotation other);
    
    public Optional<Annotation> intersection(Annotation other);
    
    public Optional<Annotation> xor(Annotation other);
}