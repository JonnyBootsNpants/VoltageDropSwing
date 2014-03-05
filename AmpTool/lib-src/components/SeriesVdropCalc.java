package components;

import java.io.Serializable;

import constants.*;
import static java.lang.Math.*;

class SeriesVdropCalc implements Serializable {

	
	private static final long serialVersionUID = -2368370841896323014L;
	boolean feet, amps;
	double[] lengths = new double[99];
	double[] loads = new double[99];
	SeriesResult seriesResult = new SeriesResult();

	SingleVdropCalc[] singleVdropCalcs = new SingleVdropCalc[99];
	private int maximumIndexForCalculation = -1;

	SeriesVdropCalc() {
		
		for (int i = 0; i < lengths.length; ++i) {
			singleVdropCalcs[i] = new SingleVdropCalc(CalcToPerform.GETVDROP);
			lengths[i] = 0.0;
			loads[i] = 0.0;
		}
	}
	
	WireSize[] getWireSizes()
	{
		WireSize[] sizes = new WireSize[99];
		
		for (int i = 0; i < singleVdropCalcs.length; ++i)
		{
			sizes[i] = singleVdropCalcs[i].wireSize;
		}
		
		return sizes;
	}

	void setAC(boolean ac) {
		for (int i = 0; i < singleVdropCalcs.length; ++i) {
			if (singleVdropCalcs[i] == null) {
				break;
			}
			singleVdropCalcs[i].ac = ac;
		}
	}

	void setVoltage(double voltage) {
		for (int i = 0; i < singleVdropCalcs.length; ++i) {
			if (singleVdropCalcs[i] == null) {
				break;
			}
			singleVdropCalcs[i].voltage = voltage;
		}
	}

	void setConduitType(ConduitType conduitType) {
		for (int i = 0; i < singleVdropCalcs.length; ++i) {
			if (singleVdropCalcs[i] == null) {
				break;
			}
			singleVdropCalcs[i].conduitType = conduitType;
		}
	}

	void setThreePhase(boolean threePhase) {
		for (int i = 0; i < singleVdropCalcs.length; ++i) {
			if (singleVdropCalcs[i] == null) {
				break;
			}
			singleVdropCalcs[i].threePhase = threePhase;
		}
	}

	void setPowerFactor(double powerFactor) {
		for (int i = 0; i < singleVdropCalcs.length; ++i) {
			if (singleVdropCalcs[i] == null) {
				break;
			}
			singleVdropCalcs[i].powerFactor = powerFactor;
		}
	}

	void setTemperatureInC(double temperatureInC) {
		for (int i = 0; i < singleVdropCalcs.length; ++i) {
			if (singleVdropCalcs[i] == null) {
				break;
			}
			singleVdropCalcs[i].temperatureInC = temperatureInC;
		}
	}

	void setAlumWire(boolean alumWire) {
		for (int i = 0; i < singleVdropCalcs.length; ++i) {
			if (singleVdropCalcs[i] == null) {
				break;
			}
			singleVdropCalcs[i].alumWire = alumWire;
		}
	}

	void setConductorsPerPhase(int conductorsPerPhase) {
		for (int i = 0; i < singleVdropCalcs.length; ++i) {
			if (singleVdropCalcs[i] == null) {
				break;
			}
			singleVdropCalcs[i].conductorsPerPhase = conductorsPerPhase;
		}
	}

	void setWireSize(WireSize wireSize) {
		for (int i = 0; i < singleVdropCalcs.length; ++i) {
			if (singleVdropCalcs[i] == null) {
				break;
			}
			singleVdropCalcs[i].wireSize = wireSize;
		}
	}

	void setWireSize(WireSize wireSize, int index) {
		singleVdropCalcs[index].wireSize = wireSize;
	}

	public void update() {
		updateResultLoads();
		updateVDs();
		seriesResult.maximumIndexForCalculation = maximumIndexForCalculation;
	}

	private void updateResultLoads() {
		int i = 0;
		while (lengths[i] > 0.0) {
			++i;
		}
		--i;
		// clean up the rest of the result
		/*for (int j = maximumIndexForCalculation; j > i; --j) {
			seriesResult.resultLoads[j] = 0.0;
			seriesResult.resultSegmentVDs[j] = 0.0;
			seriesResult.resultCumulativeVDs[j] = 0.0;
		}*/
		maximumIndexForCalculation = i;
		double total = 0.0;
		while (i >= 0) {
			total += loads[i];
			seriesResult.resultLoads[i] = total;
			--i;
		}
		//for (int k = 0; k <= maximumIndexForCalculation; ++k) {
			//System.out.println("Load:  " + seriesResult.resultLoads[k]);
		//}
	}

	// these calculations should not be here, SingleVdropCalc should do the
	// conversions. !amps is VA, not kVA
	private void updateVDs() {
		double totalVD = 0.0;

		for (int i = 0; i <= maximumIndexForCalculation; ++i) {
			
			if (!amps && (singleVdropCalcs[0].ac && singleVdropCalcs[0].threePhase)) {
				seriesResult.resultLoads[i] = seriesResult.resultLoads[i]
						/ (singleVdropCalcs[i].voltage * sqrt(3));
						
			} else if (!amps) {
				seriesResult.resultLoads[i] = seriesResult.resultLoads[i] / singleVdropCalcs[i].voltage;
			}
			singleVdropCalcs[i].loadInAmps = seriesResult.resultLoads[i];

			if (!feet) {
				singleVdropCalcs[i].lengthInFeetOneWay = lengths[i] * 3.28084;
			} else {
				singleVdropCalcs[i].lengthInFeetOneWay = lengths[i];
			}

			singleVdropCalcs[i].update();

			totalVD += singleVdropCalcs[i].vdropPercent;
			seriesResult.resultSegmentVDs[i] = seriesResult.resultSegmentVDs[i] > 100.0 ? 100.0
					: singleVdropCalcs[i].vdropPercent;
			seriesResult.resultCumulativeVDs[i] = (totalVD > 1.0 ? 1.0 : totalVD);
			
			/*System.out.println("Segment VD    "
					+ singleVdropCalcs[i].vdropPercent);
			System.out.println("CUmulative VD    "
					+ seriesResult.resultCumulativeVDs[i]);*/
		}
	}
}


