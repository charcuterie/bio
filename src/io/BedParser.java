package io;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import annotation.BEDFileRecord;

public final class BedParser extends FileParser<BEDFileRecord> {

    private static final Logger logger = LogManager.getLogger(BedParser.class);
    
    public BedParser(Path p) throws IOException {
        super(p);
        findNext();
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected void findNext() {
        try {
            String line = br.readLine();
            next = line == null ? null : BEDFileRecord.fromFormattedString(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}