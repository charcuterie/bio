package annotation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class AnnotationImpl implements Annotation {
    
    protected final String ref;
    protected final Strand strand;
    protected final int start;
    protected final int end;
    
    public AnnotationImpl(AnnotationBuilder b) {
        this.ref = b.ref;
        this.strand = b.strand;
        this.start = b.start;
        this.end = b.end;
    }
    
    public AnnotationImpl(String ref, int start, int end, Strand strand) {

        if (start >= end) {
            throw new IllegalArgumentException("INSTANTIATION FAIL: Block " +
                "end " + end + " must be greater than block start " + start);
        }
        
        this.ref = ref;
        this.start = start;
        this.end = end;
        this.strand = strand;
    }
    
    public String getReferenceName() {
        return ref;
    }
    
    public Strand getStrand() {
        return strand;
    }
    
    public int getStart() {
        return start;
    }
    
    public int getEnd() {
        return end;
    }
    
    @Override
    public int getSize() {
        return end - start;
    }
    
    @Override
    public int getSpan() {
        return getSize();
    }
    
    @Override
    public boolean overlaps(Annotation a) {
        return intersection(a).isPresent();
    }
    
    @Override
    public Stream<Block> getBlockStream() {
        final Spliterator<Block> s = Spliterators.spliteratorUnknownSize(getBlocks(), Spliterator.ORDERED);
        return StreamSupport.stream(s, false);
    }
    
    protected int[] merge(Annotation other, BiFunction<Boolean, Boolean, Boolean> op) {
        
        // Flatten the annotations and add a sentinel value at the end
        int[] thisEndpoints = new int[getNumberOfBlocks() * 2 + 1];
        int idx = 0;
        Iterator<Block> blocks = getBlocks();
        while (blocks.hasNext()) {
            Block block = blocks.next();
            thisEndpoints[idx++] = block.getStart();
            thisEndpoints[idx++] = block.getEnd();
        }
        
        int[] otherEndpoints = new int[other.getNumberOfBlocks() * 2 + 1];
        idx = 0;
        blocks = other.getBlocks();
        while (blocks.hasNext()) {
            Block block = blocks.next();
            otherEndpoints[idx++] = block.getStart();
            otherEndpoints[idx++] = block.getEnd();
        }

        int sentinel = Math.max(thisEndpoints[thisEndpoints.length - 2],
                                otherEndpoints[otherEndpoints.length - 2]) + 1;
        thisEndpoints[thisEndpoints.length - 1] = sentinel;
        otherEndpoints[otherEndpoints.length - 1] = sentinel;
        
        // Go through the flattened annotations and at each endpoint, determine whether
        // it is in the result
        int thisIdx = 0;
        int otherIdx = 0;
        List<Integer> rtrnEndpoints = new ArrayList<Integer>();
        int scan = Math.min(thisEndpoints[thisIdx], otherEndpoints[otherIdx]);
        while (scan < sentinel) {
            boolean in_this = !((scan < thisEndpoints[thisIdx]) ^ (thisIdx % 2 == 1));
            boolean in_other = !((scan < otherEndpoints[otherIdx]) ^ (otherIdx % 2 == 1));
            boolean in_result = op.apply(in_this, in_other);
            
            if (in_result ^ (rtrnEndpoints.size() % 2 == 1)) {
                rtrnEndpoints.add(scan);
            }
            if (scan == thisEndpoints[thisIdx]) {
                thisIdx++;
            }
            if (scan == otherEndpoints[otherIdx]) {
                otherIdx++;
            }
            scan = Math.min(thisEndpoints[thisIdx], otherEndpoints[otherIdx]);
        }

        return rtrnEndpoints.stream().mapToInt(i -> i).toArray();
    }
    
    @Override
    public Optional<Annotation> minus(Annotation other) {

        if (other == null) {
            return Optional.of(this);
        }
        
        if ((strand.equals(Strand.NEGATIVE) || (strand.equals(Strand.POSITIVE)))
                && strand.reverse().equals(other.getStrand())) {
            return Optional.of(this);
        }
        
        int[] flattened = merge(other, (a, b) -> a && !b);
        List<Block> blocks = new ArrayList<>();

        for (int i = 0; i < flattened.length; i += 2) {
            blocks.add(new Block(getReferenceName(), flattened[i], flattened[i + 1], strand));
        }
        
        return blocks.isEmpty() ? Optional.empty()
                                : Optional.of((new BlockedAnnotation.BlockedBuilder()).addBlocks(blocks).build());
    }
    
    @Override
    public Annotation union(Annotation other) {
        if (other == null) {
            return this;
        }
        
        Strand returnStrand = strand.equals(other.getStrand()) ? strand : Strand.BOTH;
        
        int[] flattened = merge(other, (a, b) -> a || b);
        List<Block> blocks = new ArrayList<>();

        for (int i = 0; i < flattened.length; i += 2) {
            blocks.add(new Block(getReferenceName(), flattened[i], flattened[i + 1], returnStrand));
        }
        
        return (new BlockedAnnotation.BlockedBuilder()).addBlocks(blocks).build();
    }
    
    @Override
    public Optional<Annotation> intersection(Annotation other) {
        if (other == null) {
            return Optional.empty();
        }
        
        Strand returnStrand = null;
        if (strand.equals(other.getStrand())) {
            returnStrand = strand;
        } else if (strand.equals(Strand.BOTH)) {
            returnStrand = other.getStrand();
        } else if (other.getStrand().equals(Strand.BOTH)) {
            returnStrand = strand;
        } else {
            return Optional.empty();
        }
        
        int[] flattened = merge(other, (a, b) -> a && b);
        List<Block> blocks = new ArrayList<>();

        for (int i = 0; i < flattened.length; i += 2) {
            blocks.add(new Block(getReferenceName(), flattened[i], flattened[i + 1], returnStrand));
        }
        
        return blocks.isEmpty() ? Optional.empty()
                                : Optional.of((new BlockedAnnotation.BlockedBuilder()).addBlocks(blocks).build());
    }
    
    @Override
    public Optional<Annotation> xor(Annotation other) {
        if (other == null) {
            return Optional.of(this);
        }
        
        Strand returnStrand = strand.equals(other.getStrand()) ? strand : Strand.BOTH;
        
        int[] flattened = merge(other, (a, b) -> a ^ b);
        List<Block> blocks = new ArrayList<>();

        for (int i = 0; i < flattened.length; i += 2) {
            blocks.add(new Block(getReferenceName(), flattened[i], flattened[i + 1], returnStrand));
        }
        
        return blocks.isEmpty() ? Optional.empty()
                : Optional.of((new BlockedAnnotation.BlockedBuilder()).addBlocks(blocks).build());
    }
    
    public abstract static class AnnotationBuilder {
        
        protected int start;
        protected int end;
        protected String ref;
        protected Strand strand;
        
        public AnnotationBuilder() { }
        
        public abstract Annotation build();
    }
}