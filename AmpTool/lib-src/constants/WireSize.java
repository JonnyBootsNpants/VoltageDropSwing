package constants;


/*public interface WireSize {
	//abstract AWGWireSize[] values();
}*/

public enum WireSize  {
	
	EIGHTEEN(0.0, 0.0, 7.77, 7.77, 7.77, 12.8, 12.8, 12.8, 7.77, 12.8),
	SIXTEEN(0.0, 0.0, 4.89, 4.89, 4.89, 8.05, 8.05, 8.05, 4.89, 8.05),
	FOURTEEN(0.058, 0.073, 3.1, 3.1, 3.1, 5.06, 5.06, 5.06, 3.07, 5.06),
	TWELVE(0.054, 0.068, 2.0, 2.0, 2.0, 3.2, 3.2, 3.2, 1.93, 3.18),
	TEN(0.050, 0.063, 1.2, 1.2, 1.2, 2.0, 2.0, 2.0, 1.21, 2.00), 
	EIGHT(0.052, 0.065, 0.78, 0.78, 0.78, 1.3, 1.3, 1.3, 0.764, 1.26),
	SIX(0.051, 0.064, 0.49, 0.49, 0.49, 0.81, 0.81, 0.81, 0.491, 0.808),
	FOUR(0.048, 0.060, 0.31, 0.31, 0.31, 0.51, 0.51, 0.51, 0.308, 0.508),
	THREE(0.047, 0.059, 0.25, 0.25, 0.25, 0.40, 0.41, 0.40, 0.245, 0.403),
	TWO(0.045, 0.057, 0.19, 0.20, 0.20, 0.32, 0.32, 0.32, 0.194, 0.319), 
	ONE(0.046, 0.057, 0.15, 0.16, 0.16, 0.25, 0.26, 0.25, 0.154, 0.253),
	ONE_AUGHT(0.044, 0.055, 0.12, 0.13, 0.12, 0.20, 0.21, 0.20, 0.122, 0.201),
	TWO_AUGHT(0.043, 0.054, 0.10, 0.10, 0.10, 0.16, 0.16, 0.16, 0.0967, 0.159),
	THREE_AUGHT(0.042, 0.052, 0.077, 0.082, 0.079, 0.13, 0.13, 0.13, 0.0766, 0.126),
	FOUR_AUGHT(0.041, 0.051, 0.062, 0.067, 0.063, 0.10, 0.11, 0.10, 0.0608, 0.100),
	TWOFIFTY(0.041, 0.052, 0.052, 0.057, 0.054, 0.085, 0.090, 0.086, 0.515, 0.0847),
	THREEHUNDRED(0.041, 0.051, 0.044, 0.049, 0.045, 0.071, 0.076, 0.072, 0.0429, 0.0707),
	THREEFIFTY(0.040, 0.050, 0.038, 0.043, 0.039, 0.061, 0.066, 0.063, 0.0367, 0.0605),
	FOURHUNDRED(0.040, 0.049, 0.033, 0.038, 0.035, 0.054, 0.059, 0.055, 0.0321, 0.0529),
	FIVEHUNDRED(0.039, 0.048, 0.027, 0.032, 0.029, 0.043, 0.048, 0.045, 0.0258, 0.0424), 
	SIXHUNDRED(0.039, 0.048, 0.023, 0.028, 0.025, 0.036, 0.041, 0.038, 0.0214, 0.0353),
	SEVENFIFTY(0.038, 0.048, 0.019, 0.024, 0.021, 0.029, 0.034, 0.031, 0.0171, 0.0282),
	ONETHOUSAND(0.037, 0.046, 0.015, 0.019, 0.018, 0.023, 0.027, 0.025, 0.0129, 0.0212);
	
	
	private WireSize(double ReactanceKFootinPVCAlum, 
					double ReactanceKFootinSteel, 
					double CopperResistanceKFootAt75inPVC, 
					double CopperResistanceKFootAt75inAlum,
					double CopperResistanceKFootAt75inSteel,
					double AlumResistanceKFootAt75inPVC, 
					double AlumResistanceKFootAt75inAlum,
					double AlumResistanceKFootAt75inSteel,
					double CopperDCResistanceAt75,
					double AlumDCResistanceAt75) 
					{
		
		this.ReactanceKFootinPVCAlum = ReactanceKFootinPVCAlum;
		this.ReactanceKFootinSteel = ReactanceKFootinSteel;
		this.CopperResistanceKFootAt75inPVC = CopperResistanceKFootAt75inPVC;
		this.CopperResistanceKFootAt75inAlum = CopperResistanceKFootAt75inAlum;
		this.CopperResistanceKFootAt75inSteel = CopperResistanceKFootAt75inSteel;
		this.AlumResistanceKFootAt75inPVC = AlumResistanceKFootAt75inPVC;
		this.AlumResistanceKFootAt75inAlum = AlumResistanceKFootAt75inAlum;
		this.AlumResistanceKFootAt75inSteel = AlumResistanceKFootAt75inSteel;
		this.CopperDCResistanceAt75 = CopperDCResistanceAt75;
		this.AlumDCResistanceAt75 = AlumDCResistanceAt75;
		
	}
	
	@Override
	public String toString() {
		switch (this) {
		case EIGHTEEN:
			return "18";
		case SIXTEEN:
			return "16";
		case FOURTEEN:
			return "14";
		case TWELVE:
			return "12";
		case TEN:
			return "10";
		case EIGHT:
			return "8";
		case SIX:
			return "6";
		case FOUR:
			return "4";
		case THREE:
			return "3";
		case TWO:
			return "2";
		case ONE:
			return "1";
		case ONE_AUGHT:
			return "1/0";
		case TWO_AUGHT:
			return "2/0";
		case THREE_AUGHT:
			return "3/0";
		case FOUR_AUGHT:
			return "4/0";
		case TWOFIFTY:
			return "250";
		case THREEHUNDRED:
			return "300";
		case THREEFIFTY:
			return "350";
		case FOURHUNDRED:
			return "400";
		case FIVEHUNDRED:
			return "500";
		case SIXHUNDRED:
			return "600";
		case SEVENFIFTY:
			return "750";
		case ONETHOUSAND:
			return "1000";
		default:
			return "";
		}

	}
	public String nameOf() {
		return super.toString();
	}
	public int ampacity;
	
	public double ReactanceKFootinPVCAlum;
	public double ReactanceKFootinSteel;
	public double CopperResistanceKFootAt75inPVC;
	public double CopperResistanceKFootAt75inAlum;
	public double CopperResistanceKFootAt75inSteel;
	public double AlumResistanceKFootAt75inPVC;
	public double AlumResistanceKFootAt75inAlum;
	public double AlumResistanceKFootAt75inSteel;
	public double CopperDCResistanceAt75;
	public double AlumDCResistanceAt75; 
	
	
}
	
	
	
	