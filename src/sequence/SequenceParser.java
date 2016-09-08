package sequence;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.logging.log4j.Logger;

import utils.CloseableIterator;

public abstract class SequenceParser<T extends Sequence> implements CloseableIterator<T> {

    protected final Path p;
    protected final BufferedReader br;
    protected T next;
    
    public SequenceParser(Path p) throws IOException {
        if (p == null) {
            throw new IllegalArgumentException("INSTANTIATION FAIL: " +
                    "SequenceParser constructed with null path.");
            
        }
        
        this.p = p;
        br = Files.newBufferedReader(p, StandardCharsets.US_ASCII);
    }
    
    protected abstract Logger getLogger();
    
    @Override
    public void close() {
        try {
            br.close();
        } catch (IOException e) {
            getLogger().error("Exception caught when closing reader.", e);
        }
    }
    
    @Override
    public boolean hasNext() {
        return next != null;
    }
    
    @Override
    public T next() {
        T rtrn = next;
        findNext();
        return rtrn;
    }
    
    @Override
    public List<T> toList() {
        final List<T> list = new ArrayList<>();
        while (hasNext()) {
            list.add(next());
        }
        close();
        return list;
    }
    
    @Override
    public Stream<T> stream() {
        final Spliterator<T> s = Spliterators.spliteratorUnknownSize(this, Spliterator.ORDERED);
        return StreamSupport.stream(s, false).onClose(this::close);
    }
    
    protected abstract void findNext();
}