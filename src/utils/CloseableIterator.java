package utils;

import java.io.Closeable;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public interface CloseableIterator<T> extends Iterator<T>, Closeable {

    public void close();
    
    public List<T> toList();
    
    public Stream<T> stream();
}