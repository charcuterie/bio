package sequence;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class FastaParser extends SequenceParser<FastaSequence> {

    private static final int NUM_FASTA_LINES = 2;
    private static final Logger logger = LogManager.getLogger(FastaParser.class);
    
    public FastaParser(Path p) throws IOException {
        super(p);
        findNext();
    }
    
    @Override
    protected void findNext() {
        String[] s = new String[NUM_FASTA_LINES];
        String line = null;
        int i = 0;
        try {
            while (i < NUM_FASTA_LINES && (line = br.readLine()) != null) {
                s[i] = line;
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (s[1] != null) {
            next = new FastaSequence(s[0].substring(1), s[1]);
        } else {
            next = null;
            logger.warn("FASTA file " + p.toString() + " has an incomplete final record.");
        }
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}