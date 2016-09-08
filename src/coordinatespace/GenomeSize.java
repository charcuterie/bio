package coordinatespace;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains static maps of genome sizes. Used to construct CoordinateSpaces.  
 */
public final class GenomeSize {
	
	public static final Map<String, Integer> MM9;
	public static final Map<String, Integer> MM10;
	public static final Map<String, Integer> HG19;


	static {
		Map<String, Integer> mm9Map = new HashMap<String, Integer>();
		mm9Map.put("chr1", 197195432);
		mm9Map.put("chr2", 181748087);
		mm9Map.put("chr3", 159599783);
		mm9Map.put("chr4", 155630120);
		mm9Map.put("chr5", 152537259);
		mm9Map.put("chr6", 149517037);
		mm9Map.put("chr7", 152524553);
		mm9Map.put("chr8", 131738871);
		mm9Map.put("chr9", 124076172);
		mm9Map.put("chr10", 129993255);
		mm9Map.put("chr11", 121843856);
		mm9Map.put("chr12", 121257530);
		mm9Map.put("chr13", 120284312);
		mm9Map.put("chr14", 125194864);
		mm9Map.put("chr15", 103494974);
		mm9Map.put("chr16", 98319150);
		mm9Map.put("chr17", 95272651);
		mm9Map.put("chr18", 90772031);
		mm9Map.put("chr19", 61342430);
		mm9Map.put("chrX", 166650296);
		mm9Map.put("chrY", 15902555);
		mm9Map.put("chrM", 16299);
		MM9 = Collections.unmodifiableMap(mm9Map);
	}
	
	static {
		Map<String, Integer> mm10Map = new HashMap<String, Integer>();
		mm10Map.put("chr1", 195471971);
		mm10Map.put("chr2", 182113224);
		mm10Map.put("chr3", 160039680);
		mm10Map.put("chr4", 156508116);
		mm10Map.put("chr5", 151834684);
		mm10Map.put("chr6", 149736546);
		mm10Map.put("chr7", 145441459);
		mm10Map.put("chr8", 129401213);
		mm10Map.put("chr9", 124595110);
		mm10Map.put("chr10", 130694993);
		mm10Map.put("chr11", 122082543);
		mm10Map.put("chr12", 120129022);
		mm10Map.put("chr13", 120421639);
		mm10Map.put("chr14", 124902244);
		mm10Map.put("chr15", 104043685);
		mm10Map.put("chr16", 98207768);
		mm10Map.put("chr17", 94987271);
		mm10Map.put("chr18", 90702639);
		mm10Map.put("chr19", 61431566);
		mm10Map.put("chrX", 171031299);
		mm10Map.put("chrY", 91744698);
		mm10Map.put("chrM", 16299);
		MM10 = Collections.unmodifiableMap(mm10Map);
	}
	
	static {
		Map<String, Integer> hg19Map = new HashMap<String, Integer>();
		hg19Map.put("chr1", 249250621);
		hg19Map.put("chr2",	243199373);
		hg19Map.put("chr3",	198022430);
		hg19Map.put("chr4",	191154276);
		hg19Map.put("chr5",	180915260);
		hg19Map.put("chr6",	171115067);
		hg19Map.put("chr7",	159138663);
		hg19Map.put("chr8",	146364022);
		hg19Map.put("chr9",	141213431);
		hg19Map.put("chr10", 135534747);
		hg19Map.put("chr11", 135006516);
		hg19Map.put("chr12", 133851895);
		hg19Map.put("chr13", 115169878);
		hg19Map.put("chr14", 107349540);
		hg19Map.put("chr15", 102531392);
		hg19Map.put("chr16", 90354753);
		hg19Map.put("chr17", 81195210);
		hg19Map.put("chr18", 78077248);
		hg19Map.put("chr19", 59128983);
		hg19Map.put("chr20", 63025520);
		hg19Map.put("chr21", 48129895);
		hg19Map.put("chr22", 51304566);
		hg19Map.put("chrX", 155270560);
		hg19Map.put("chrY",	59373566);
		hg19Map.put("chrM",	16571);
		HG19 = Collections.unmodifiableMap(hg19Map);
	}
}
