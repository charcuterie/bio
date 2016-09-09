package utils;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

public class FilteredIterator<T> implements Iterator<T> {
    private Iterator<? extends T> iterator;
    private Predicate<T> predicate;
    private T next;

    public FilteredIterator(Iterator<? extends T> iterator, Predicate<T> predicate) {
        this.iterator = iterator;
        this.predicate = predicate;

        next = findNext();
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        T rtrn = next;
        next = findNext();
        return rtrn;
    }

    private T findNext() {

        while (iterator.hasNext()) {
            T o = iterator.next();

            if (predicate.test(o)) {
                return o;
            }
        }
        
        return null;
    }

    @Override
    public void remove() {
        iterator.remove();
    }
}