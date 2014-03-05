package wires;


public abstract class AmWire extends Wire {
	
	final static String[] wireSizes = {"#12", "#10", "#8", "#6", "#4", "#3", "#2",
		"#1", "#1/0", "#2/0", "#3/0", "#4/0",
		"250 MCM", "300 MCM", "350 MCM", "400 MCM",
		"500 MCM", "600 MCM"};
	
	String getEqGroundSize(double amps) {
		if (!eqGroundAlum) {
			return (amps<=20 ? wireSizes[0] : amps<=60 ? wireSizes[1] : amps<=100 ? wireSizes[2] :
					amps<=200 ? wireSizes[3] : amps<=300 ? wireSizes[4] : amps<=400 ? wireSizes[5]:
					amps<=500 ? wireSizes[6] : amps<=600 ? wireSizes[7] : amps<=800 ? wireSizes[8]:
					amps<=1000 ? wireSizes[9] : amps<=1200 ? wireSizes[10] : amps<=1600 ? wireSizes[11]:
					amps<=2000 ? wireSizes[12] : amps<=2500 ? wireSizes[14] : amps<= 3000 ? wireSizes[15]:
					amps<=4000 ? wireSizes[16] : "Out of range");
			
		} else {return "not ready";}
		
	}
	
	

	
	public String getWireSize() {
		return size + " ("+ sets + (sets > 1 ? " sets)" : " set)");
	}
	public String getEqGroundSize() {
		return eqGroundSize + (eqGroundAlum ? " aluminum" : " copper");
	}
	public String getGECSize () {
		return GECSize + (GECAlum ? " aluminum" : " copper");
	}
	
	
}
