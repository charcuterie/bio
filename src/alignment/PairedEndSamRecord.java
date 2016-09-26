package alignment;

import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;

import annotation.Annotation;
import annotation.Block;
import annotation.Strand;
import sequence.FastqSequence;
import sequence.Sequence;
import sequence.Sequences;

public class PairedEndSamRecord implements Alignment {

    private final SingleReadSamRecord read1;
    private final SingleReadSamRecord read2;
    private final Annotation annot;
    private Sequence seq;
    
    public PairedEndSamRecord(SingleReadSamRecord read1,
                              SingleReadSamRecord read2) {
        
        if (!read1.getReferenceName().equals(read2.getReferenceName())) {
            throw new IllegalArgumentException(); //TODO
        }
        
        this.read1 = read1;
        this.read2 = read2;
        annot = new Block(read1.getReferenceName(),
                          Math.min(read1.getStart(), read2.getStart()),
                          Math.max(read1.getEnd(), read2.getEnd()),
                          Strand.BOTH);
        
        String read1seq = read1.getStrand().equals(Strand.NEGATIVE)
                        ? Sequences.complement(read1.getBases())
                        : read1.getBases();

        String read2seq = read1.getStrand().equals(Strand.NEGATIVE)
                        ? Sequences.complement(read2.getBases())
                        : read2.getBases();
                        
        seq = new FastqSequence(read1.getName() + "|" + read2.getName(),
                "blah", new byte[4]); //TODO fix this
        
    }
    
    @Override
    public String getReferenceName() {
        return annot.getReferenceName();
    }

    @Override
    public int getStart() {
        return annot.getStart();
    }

    @Override
    public int getEnd() {
        return annot.getEnd();
    }

    @Override
    public int getNumberOfBlocks() {
        return annot.getNumberOfBlocks();
    }

    @Override
    public int getSize() {
        return annot.getSize();
    }

    @Override
    public int getSpan() {
        return annot.getSpan();
    }

    @Override
    public Strand getStrand() {
        return annot.getStrand();
    }

    @Override
    public PairedEndSamRecord reverseComplement() {
        return new PairedEndSamRecord(read1.reverseComplement(), read2.reverseComplement());
    }

    @Override
    public Iterator<Block> getBlocks() {
        return annot.getBlocks();
    }

    @Override
    public Stream<Block> getBlockStream() {
        return annot.getBlockStream();
    }

    @Override
    public boolean overlaps(Annotation other) {
        return annot.overlaps(other);
    }

    @Override
    public Optional<Annotation> minus(Annotation other) {
        return annot.minus(other);
    }

    @Override
    public Annotation union(Annotation other) {
        return annot.union(other);
    }

    @Override
    public Optional<Annotation> intersection(Annotation other) {
        return annot.intersection(other);
    }

    @Override
    public Optional<Annotation> xor(Annotation other) {
        return annot.xor(other);
    }

    @Override
    public String getBases() {
        return seq.getBases();
    }

    @Override
    public String getName() {
        return seq.getName();
    }
}