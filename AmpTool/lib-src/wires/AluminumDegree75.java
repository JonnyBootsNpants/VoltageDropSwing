package wires;


public class AluminumDegree75 extends AmWire {
		
	public AluminumDegree75(double amps) {
		ampsRequired = amps;
		sets = 1;
		double ampsTemp = ampsRequired;
		while (ampsTemp > 340.0) {
			++sets;
			ampsTemp = ampsRequired/sets;
		}
		size = getWire(ampsTemp);
		eqGroundSize = getEqGroundSize(ampsRequired);
		GECSize = getGECAluminumSize();
		}
		
	private String getWire(double amps) {
		return (amps<=20 ? wireSizes[0] : amps<=30 ? wireSizes[1] : amps<=40 ? wireSizes[2] :
				amps<=50 ? wireSizes[3] : amps<=65 ? wireSizes[4] : amps<=75 ? wireSizes[5] :
				amps<=90 ? wireSizes[6] : amps<=100 ? wireSizes[7] : amps<=120? wireSizes[8] : 
				amps<=135 ? wireSizes[9] : amps<=155 ? wireSizes[10] : amps<=180 ? wireSizes[11] :
				amps<= 205 ? wireSizes[12] : amps<= 230 ? wireSizes[13] : amps<=250 ? wireSizes[14] :
				amps<=270 ? wireSizes[15]: amps<=310 ? wireSizes[16]: wireSizes[17]	);
	}
	
	private String getGECAluminumSize() {
		int s = 0;
		for(int i = 0; i < wireSizes.length; ++i) {
			if(size.equals(wireSizes[i])) {
				s = i;
				break;
			}
		}
		if (!eqGroundAlum) {
			if (sets == 1) {
				return (s <= 9 ? wireSizes[2] : s <= 11 ? wireSizes[3] : s <= 13 ? wireSizes[4] :
						s <= 15 ? wireSizes[6] : wireSizes[8]);
			}
			//need to figure this out exactly
			if (ampsRequired <= 520) {
				return wireSizes[9];
			}
			return wireSizes[10];
		} else {return "not ready";}
		
	}
	
}
