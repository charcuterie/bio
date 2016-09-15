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

public class SAMFileRecord implements Alignment {

    private final FastqSequence fastq;
    private final Annotation annot;
    private final Set<ReadFlag> flags;
    private final String cigar;
    private final String mdTag;
    
    private SAMFileRecord(SAMRecord sam) {
        annot = (new BlockedBuilder())
                .addBlock(new Block(sam.getReferenceName(),
                                    sam.getStart(),
                                    sam.getEnd(),
                                    sam.getReadNegativeStrandFlag()
                                            ? Strand.NEGATIVE
                                            : Strand.POSITIVE))
                .build();
        fastq = new FastqSequence(sam.getReadName(), sam.getReadString(), 
                                  sam.getBaseQualities());
        cigar = sam.getCigarString();
        mdTag = (String) sam.getAttribute("MD");
        flags = ReadFlag.parseInt((sam.getFlags()));
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
    public Annotation reverseComplement() {
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
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Optional<Annotation> minus(Annotation other) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Annotation union(Annotation other) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<Annotation> intersection(Annotation other) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<Annotation> xor(Annotation other) {
        // TODO Auto-generated method stub
        return null;
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