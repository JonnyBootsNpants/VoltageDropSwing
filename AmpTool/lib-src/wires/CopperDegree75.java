package wires;


public class CopperDegree75 extends AmWire {
		
	public CopperDegree75(double amps) {
		ampsRequired = amps;
		sets = 1;
		double ampsTemp = ampsRequired;
		while (ampsTemp > 420.0) {
			++sets;
			ampsTemp = ampsRequired/sets;
		}
		size = getWire(ampsTemp);
		eqGroundSize = getEqGroundSize(ampsRequired);
		GECSize = getGECCopperSize();
		}
	
	private String getWire(double amps) {
		return (amps<=25 ? wireSizes[0] : amps<=35 ? wireSizes[1] : amps<=50 ? wireSizes[2] :
				amps<=65 ? wireSizes[3] : amps<=85 ? wireSizes[4] : amps<=100 ? wireSizes[5] :
				amps<=115 ? wireSizes[6] : amps<=130 ? wireSizes[7] : amps<=150? wireSizes[8] : 
				amps<=175 ? wireSizes[9] : amps<=200 ? wireSizes[10] : amps<=230 ? wireSizes[11] :
				amps<= 255 ? wireSizes[12] : amps<= 285 ? wireSizes[13] : amps<=310 ? wireSizes[14] :
				amps<=335 ? wireSizes[15]: amps<=380 ? wireSizes[16]: wireSizes[17]	);
	}
	
	private String getGECCopperSize() {
		int s = 0;
		for(int i = 0; i < wireSizes.length; ++i) {
			if(size.equals(wireSizes[i])) {
				s = i;
				break;
			}
		}
		if (!eqGroundAlum) {
			if (sets == 1) {
				return (s <= 6 ? wireSizes[2] : s <= 8 ? wireSizes[3] : s <= 10 ? wireSizes[4] :
						s <= 12 ? wireSizes[6] : wireSizes[8]);
			}
			//need to figure this out exactly
			if (ampsRequired <= 520) {
				return wireSizes[9];
			}
			return wireSizes[10];
		} else {return "not ready";}
		
	}
	
}
