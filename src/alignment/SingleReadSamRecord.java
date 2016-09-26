package alignment;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import annotation.Annotation;
import annotation.Block;
import annotation.BlockedAnnotation.BlockedBuilder;
import annotation.Strand;
import htsjdk.samtools.SAMRecord;
import sequence.FastqSequence;

public class SingleReadSamRecord implements Alignment {

    private final FastqSequence fastq;
    private final Annotation annot;
    private final Set<ReadFlag> flags;
    private final String cigar;
    private final String mdTag;
    
    private SingleReadSamRecord(SAMRecord sam) {
        annot = new Block(sam.getReferenceName(),
                          sam.getStart(),
                          sam.getEnd(),
                          sam.getReadNegativeStrandFlag()
                              ? Strand.NEGATIVE
                              : Strand.POSITIVE);
        
        fastq = new FastqSequence(sam.getReadName(), sam.getReadString(), 
                                  sam.getBaseQualities());
        cigar = sam.getCigarString();
        mdTag = (String) sam.getAttribute("MD");
        flags = ReadFlag.parseInt((sam.getFlags()));
    }
    
    public boolean isPaired() {
        return flags.contains(ReadFlag.READ_PAIRED);
    }
    
    public boolean isMappedInProperPair() {
        return flags.contains(ReadFlag.READ_MAPPED_IN_PROPER_PAIR);
    }
    
    public boolean isMapped() {
        return !flags.contains(ReadFlag.READ_UNMAPPED);
    }
    
    public boolean hasMappedMate() {
        return !flags.contains(ReadFlag.MATE_UNMAPPED);
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
    public SingleReadSamRecord reverseComplement() {
        // TODO Auto-generated method stub
        return null;
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
        return fastq.getBases();
    }

    @Override
    public String getName() {
        return fastq.getName();
    }
}