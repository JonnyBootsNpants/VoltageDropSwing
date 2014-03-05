package components;

import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

import java.io.Serializable;

import constants.CalcToPerform;
import constants.ConduitType;
import constants.WireSize;

public class SingleVdropCalc implements Serializable {

	static final int VAinKVA = 1000;
	static final double threePhaseConst = sqrt(3);
	
	private static final long serialVersionUID = -3837322978153321450L;
	private double X;
	private double R;
	private double target;

	boolean wireSizeOver;
	boolean ac;
	double voltage;
	ConduitType conduitType;
	boolean threePhase;
	double powerFactor;
	double temperatureInC;
	boolean alumWire;
	int conductorsPerPhase;
	CalcToPerform calcToPerform;
	WireSize wireSize;
	double vdrop;
	double vdropPercent;
	double loadInAmps;
	double loadInKva;
	double lengthInFeetOneWay;
	double lengthInMetersOneWay;

	public SingleVdropCalc() {
	}

	public SingleVdropCalc(CalcToPerform calcToPerform) {
		this.calcToPerform = calcToPerform;
	}

	/*
	 * public SingleVdropCalc(SingleVdropCalc calc) {
	 * 
	 * this.wireSizeOver = calc.wireSizeOver; this.ac = calc.ac; this.voltage =
	 * calc.voltage; this.conduitType = calc.conduitType; this.threePhase =
	 * calc.threePhase; this.powerFactor = calc.powerFactor; this.temperatureInC
	 * = calc.temperatureInC; this.alumWire = calc.alumWire;
	 * this.conductorsPerPhase = calc.conductorsPerPhase; this.calcToPerform =
	 * calc.calcToPerform; this.wireSize = calc.wireSize; this.vdrop =
	 * calc.vdrop; this.vdropPercent = calc.vdropPercent; this.loadInAmps =
	 * calc.loadInAmps; this.loadInKva = calc.loadInKva; this.lengthInFeetOneWay
	 * = calc.lengthInFeetOneWay; this.lengthInMetersOneWay =
	 * calc.lengthInMetersOneWay;
	 * 
	 * }
	 */
	public void setThreePhase(boolean threePhase, boolean ampsGood) {
		this.threePhase = threePhase;
		if (ampsGood)
			setKVAFromAmps();
		else
			setAmpsFromKVA();
	}
	public void setVoltage(double voltage, boolean ampsGood) {
		this.voltage = voltage;
		if (ampsGood)
			setKVAFromAmps();
		else
			setAmpsFromKVA();	
	}
	private void setAmpsFromKVA() {
		loadInAmps = threePhase ? (loadInKva * VAinKVA) / (voltage * threePhaseConst)
				: (loadInKva * VAinKVA) / voltage;
	}
	private void setKVAFromAmps() {
		loadInKva = threePhase ? (loadInAmps * voltage * threePhaseConst) / VAinKVA
				: (loadInAmps * voltage) / VAinKVA;
	}
	public void update() {

		switch (calcToPerform) {

		case GETGAUGE:

			target = vdropPercent;
			for (WireSize wireSizeLocal : WireSize.values()) {
				setXR(wireSizeLocal, lengthInFeetOneWay);
				setVdrop(loadInAmps);

				if (vdropPercent <= target) {
					wireSize = wireSizeLocal;
					wireSizeOver = false;
					break;
				}
				// should really count the number of members of the array
				// before hand
				if (wireSizeLocal.equals(WireSize.ONETHOUSAND)) {
					wireSizeOver = true;
					
				}
			}
			vdropPercent = target;
			break;
		case GETVDROP:
			setXR(wireSize, lengthInFeetOneWay);
			setVdrop(loadInAmps);
			break;

		case GETMAXLOAD:
			
			setXR(wireSize, lengthInFeetOneWay);
			target = vdropPercent;

			double a = Double.MAX_VALUE;
			double b = 0.0;
			double c = a;

			while (a - b > 0.0001) {

				setVdrop(a);
				if (!(vdropPercent < target)) {
					c = a;
					a = a - (a - b) / 2;
				} else {
					b = a;
					a = c;
				}
			}
			
			loadInAmps = b;
			vdropPercent = target;
			loadInKva = threePhase ? (loadInAmps * voltage * sqrt(3)) / 1000
					: (loadInAmps * voltage) / 1000;
			break;

		case GETLENGTH:

			setXR(wireSize, lengthInFeetOneWay);
			target = vdropPercent;

			double x = Double.MAX_VALUE;
			double y = 0.0;
			double z = x;

			while (x - y > 0.0001) {

				setXR(wireSize, x);
				setVdrop(loadInAmps);
				if (!(vdropPercent < target)) {
					c = x;
					x = x - (x - y) / 2;
				} else {
					y = x;
					x = z;
				}
			}

			lengthInFeetOneWay = y;
			vdropPercent = target;
			setXR(wireSize, loadInAmps);
			lengthInMetersOneWay = lengthInFeetOneWay / 3.28084;
			break;
		}
	}

	/*
	 * case GETMAXLOAD: target = vdropPercent; double ampTemp = 0.0;
	 * setXR(wireSize, lengthInFeetOneWay);
	 * 
	 * do { loadInAmps = ampTemp; ampTemp += 0.1; setVdrop(ampTemp); } while
	 * (vdropPercent <= target);
	 * 
	 * vdropPercent = target; loadInKva = threePhase ? (loadInAmps * voltage *
	 * sqrt(3)) / 1000 : (loadInAmps * voltage) / 1000; break;
	 */
	private void setVdrop(double loadInAmps) {
		if (ac) {
			double I = loadInAmps;
			double P = powerFactor;
			double V = voltage;
			vdrop = V
					+ I
					* R
					* cos(acos(P))
					+ I
					* X
					* sin(acos(P))
					- sqrt(pow(V, 2.0)
							- pow(I * X * cos(acos(P)) - I * R * sin(acos(P)),
									2.0));

			if (threePhase) {
				vdrop = vdrop * sqrt(3);
			} else {
				vdrop = vdrop * 2;
			}
		} else {
			double I = loadInAmps;
			vdrop = (2 * R * I);
		}
		vdrop = vdrop / (double) conductorsPerPhase;
		if (vdrop > voltage || Double.isNaN(vdrop) ) {
			vdrop = voltage;
			vdropPercent = 1.0;
		} else {
			vdropPercent = vdrop/voltage;
		}
		
	}

	private void setXR(WireSize wireSize, double lengthInFeetOneWay) {

		if (ac) {
			switch (conduitType) {
			case Steel:
				X = (wireSize.ReactanceKFootinSteel / 1000.0)
						* lengthInFeetOneWay;
				R = (alumWire) ? (wireSize.AlumResistanceKFootAt75inSteel / 1000.0)
						* lengthInFeetOneWay
						: (wireSize.CopperResistanceKFootAt75inSteel / 1000.0)
								* lengthInFeetOneWay;
				break;
			case Aluminum:
				X = (wireSize.ReactanceKFootinPVCAlum / 1000.0)
						* lengthInFeetOneWay;
				R = (alumWire) ? (wireSize.AlumResistanceKFootAt75inAlum / 1000.0)
						* lengthInFeetOneWay
						: (wireSize.CopperResistanceKFootAt75inAlum / 1000.0)
								* lengthInFeetOneWay;
				break;
			case PVC:
				X = (wireSize.ReactanceKFootinPVCAlum / 1000.0)
						* lengthInFeetOneWay;
				R = (alumWire) ? (wireSize.AlumResistanceKFootAt75inPVC / 1000.0)
						* lengthInFeetOneWay
						: (wireSize.CopperResistanceKFootAt75inPVC / 1000.0)
								* lengthInFeetOneWay;
				break;
			case None:
				X = (wireSize.ReactanceKFootinPVCAlum / 1000.0)
						* lengthInFeetOneWay;
				R = (alumWire) ? (wireSize.AlumResistanceKFootAt75inPVC / 1000.0)
						* lengthInFeetOneWay
						: (wireSize.CopperResistanceKFootAt75inPVC / 1000.0)
								* lengthInFeetOneWay;
				break;
			default:
				System.out.println("couldn't choose X and R!");
			}
		} else {

			R = (alumWire) ? (wireSize.AlumDCResistanceAt75 / 1000.0)
					* lengthInFeetOneWay
					: (wireSize.CopperDCResistanceAt75 / 1000.0)
							* lengthInFeetOneWay;
		}

		if (temperatureInC != 75.0) {
			if (alumWire) {
				R = ((R * 1000 / lengthInFeetOneWay) * (1 + 0.00330 * (temperatureInC - 75)))
						/ 1000 * lengthInFeetOneWay;

			} else {
				R = ((R * 1000 / lengthInFeetOneWay) * (1 + 0.00323 * (temperatureInC - 75)))
						/ 1000 * lengthInFeetOneWay;
			}
		}
	}
}
