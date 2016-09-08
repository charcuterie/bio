package sequence;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class FastqParser extends SequenceParser<FastqSequence> {

    private final PhredEncoding pe;
    private final static int NUM_FASTQ_LINES = 4;
    private static final Logger logger = LogManager.getLogger(FastqParser.class);
    
    public FastqParser(Path p, PhredEncoding pe) throws IOException {
        super(p);
        if (pe == null) {
            throw new IllegalArgumentException("INSTATIATION FAIL: " +
                    "FastqParser constructed with null PhredEncoding.");
        }
        this.pe = pe;
        findNext();
    }
    
    public FastqParser(Path p) throws IOException {
        this(p, PhredEncoding.SANGER);
    }
    
    protected void findNext() {
        String[] s = new String[NUM_FASTQ_LINES];
        String line = null;
        int i = 0;
        try {
            while (i < NUM_FASTQ_LINES && (line = br.readLine()) != null) {
                s[i] = line;
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (s[3] != null) {
            next = new FastqSequence(s[0].substring(1), s[1], s[3], pe);
        } else {
            next = null;
            logger.warn("FASTQ file " + p.toString() + " has an incomplete final record.");
        }
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
    
}
