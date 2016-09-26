package io;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sequence.FastaSequence;

public final class FastaParser extends FileParser<FastaSequence> {

    private static final Logger logger = LogManager.getLogger(FastaParser.class);
    private String nextName = null;

    public FastaParser(Path p) throws IOException {
        super(p);
        findFirst();
    }

    private void findFirst() throws IOException {
        String line = null;
        
        line = br.readLine();
        if (line == null) {
            nextName = null;
        } else if (!line.startsWith(">")) {
            throw new IOException("");
        } else {
            nextName = line.substring(1);
        }

        findNext();
    }
    
    @Override
    protected void findNext() {
        if (nextName == null) {
            next = null;
            return;
        }
        
        StringBuilder sequence = new StringBuilder();
        String line = null;

        try {
            while ((line = br.readLine()) != null && !line.startsWith(">")) {
                sequence.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        next = new FastaSequence(nextName, sequence.toString());
        nextName = line;
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}