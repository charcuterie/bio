package annotation;

public interface Annotation extends Stranded {

    public String getReferenceName();
    
    public int getStart();
    
    public int getEnd();
}