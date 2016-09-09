package utils;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public interface CloseableIterator<T> extends Iterator<T>, AutoCloseable {

    public void close();
    
    public List<T> toList();
    
    public Stream<T> stream();
}