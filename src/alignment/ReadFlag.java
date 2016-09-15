package alignment;

import java.util.EnumSet;
import java.util.Set;

public enum ReadFlag {
    READ_PAIRED(                0x1),
    READ_MAPPED_IN_PROPER_PAIR( 0x2),
    READ_UNMAPPED(              0x4),
    MATE_UNMAPPED(              0x8),
    READ_REVERSE_STRAND(        0x10),
    MATE_REVERSE_STRAND(        0x20),
    FIRST_IN_PAIR(              0x40),
    SECOND_IN_PAIR(             0x80),
    NOT_PRIMARY_ALIGNMENT(      0x100),
    FAILS_QUALITY_CHECKS(       0x200),
    PCR_OR_OPTICAL_DUPLICATE(   0x400),
    SUPPLEMENTARY_ALIGNMENT(    0x800);
    
    private final int val;
    
    private ReadFlag(int val) {
        this.val = val;
    }
    
    public int intValue() {
        return val;
    }
    
    public static Set<ReadFlag> parseInt(int i) {
        Set<ReadFlag> s = EnumSet.noneOf(ReadFlag.class);
        for (ReadFlag f : values()) {
            if (f.isSet(i)) {
                s.add(f);
            }
        }
        return s;
    }
    
    private boolean isSet(int flag) {
        return (val & flag) != 0;
    }
}