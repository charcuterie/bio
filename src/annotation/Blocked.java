package annotation;

import java.util.Iterator;

public interface Blocked {

    public int getNumberOfBlocks();
    
    public Iterator<Block> getBlocks();
}
