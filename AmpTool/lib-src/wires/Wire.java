package wires;

public abstract class Wire {
	
	public abstract String getWireSize();
	public abstract String getEqGroundSize();
	public abstract String getGECSize();
	
	String size;
	String eqGroundSize;
	String GECSize;
	boolean eqGroundAlum = true;
	boolean GECAlum = true;
	double ampsRequired;
	int sets;
	int wireAmpacity;
	final static String degree = "\u00B0";
	
}
