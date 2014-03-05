package components;

import java.io.Serializable;
import java.util.Observable;

import constants.*;
import static java.lang.Math.*;

public class VdropModel extends Observable implements Serializable
{

	private static final long serialVersionUID = 7574941945837976039L;
	static final int VAinKVA = 1000;
	static final double threePhaseConst = sqrt(3);
	static final double feetInMeters = 3.28084;

	private SingleVdropCalc singleVdropCalc = new SingleVdropCalc();
	private SeriesVdropCalc seriesVdropCalc = new SeriesVdropCalc();

	public VdropModel() {

	}

	public double[] getSeriesLoads()
	{
		return seriesVdropCalc.loads;
	}

	public double[] getSeriesLengths()
	{
		return seriesVdropCalc.lengths;
	}

	public WireSize[] getSeriesSizes()
	{
		WireSize[] sizes = new WireSize[99];
		for (int i = 0; i < sizes.length; ++i)
			sizes[i] = seriesVdropCalc.singleVdropCalcs[i].wireSize;
		return sizes;
	}

	public void updateSingle()
	{
		singleVdropCalc.update();
		setChanged();
		notifyObservers();
	}

	public void updateSeries()
	{
		seriesVdropCalc.update();
		setChanged();
		notifyObservers();
	}

	public void setAC(boolean ac)
	{
		singleVdropCalc.ac = ac;
		seriesVdropCalc.setAC(ac);
	}

	public void setVoltage(double voltage, boolean ampsIsGoodForSingle)
	{
		singleVdropCalc.setVoltage(voltage, ampsIsGoodForSingle);
		seriesVdropCalc.setVoltage(voltage);
	}

	public double getVoltage()
	{
		return singleVdropCalc.voltage;
	}

	public void setConduitType(ConduitType conduitType)
	{
		singleVdropCalc.conduitType = conduitType;
		seriesVdropCalc.setConduitType(conduitType);
	}

	public void setThreePhase(boolean threePhase, boolean ampsIsGoodForSingle)
	{
		singleVdropCalc.setThreePhase(threePhase, ampsIsGoodForSingle);
		seriesVdropCalc.setThreePhase(threePhase);
	}

	public void setPowerFactor(double powerFactor)
	{
		singleVdropCalc.powerFactor = powerFactor;
		seriesVdropCalc.setPowerFactor(powerFactor);
	}

	public void setTemperature(double temperatureInC)
	{
		singleVdropCalc.temperatureInC = temperatureInC;
		seriesVdropCalc.setTemperatureInC(temperatureInC);
	}

	public void setAlum(boolean alumWire)
	{
		singleVdropCalc.alumWire = alumWire;
		seriesVdropCalc.setAlumWire(alumWire);
	}

	public void setConductorsPerPhase(int conductorsPerPhase)
	{
		singleVdropCalc.conductorsPerPhase = conductorsPerPhase;
		seriesVdropCalc.setConductorsPerPhase(conductorsPerPhase);
	}

	public void setCalc(CalcToPerform calcToPerform)
	{
		singleVdropCalc.calcToPerform = calcToPerform;
	}

	public void setWireSize(WireSize wireSize)
	{
		singleVdropCalc.wireSize = wireSize;
	}

	public void setSeriesWireSize(WireSize wireSize)
	{
		seriesVdropCalc.setWireSize(wireSize);
	}

	public void setWireSize(WireSize wireSize, int index)
	{
		seriesVdropCalc.setWireSize(wireSize, index);
	}

	public void setVdropPercent(double vdropPercent)
	{
		singleVdropCalc.vdropPercent = vdropPercent;
	}

	public void setCurrent(double current)
	{
		singleVdropCalc.loadInAmps = current;
		singleVdropCalc.loadInKva = singleVdropCalc.threePhase ? (current * singleVdropCalc.voltage * threePhaseConst) / VAinKVA
				: (current * singleVdropCalc.voltage) / VAinKVA;
	}

	public void setKva(double kva)
	{
		singleVdropCalc.loadInKva = kva;
		singleVdropCalc.loadInAmps = singleVdropCalc.threePhase ? (kva * VAinKVA) / (singleVdropCalc.voltage * threePhaseConst)
				: (kva * VAinKVA) / singleVdropCalc.voltage;
	}

	public void setLengthInFeet(double lengthInFeetOneWay)
	{
		singleVdropCalc.lengthInFeetOneWay = lengthInFeetOneWay;
		singleVdropCalc.lengthInMetersOneWay = lengthInFeetOneWay / feetInMeters;
	}

	public void setLengthInMeters(double lengthInMetersOneWay)
	{
		singleVdropCalc.lengthInFeetOneWay = lengthInMetersOneWay * feetInMeters;
		singleVdropCalc.lengthInMetersOneWay = lengthInMetersOneWay;
	}

	// use method overloading
	public void setSeriesLoad(double load, int index)
	{
		seriesVdropCalc.loads[index] = load;
	}

	public void setSeriesLoad(double[] loads)
	{
		seriesVdropCalc.loads = loads;
	}

	public void setSeriesLength(double length, int index)
	{
		seriesVdropCalc.lengths[index] = length;
	}

	public void setSeriesLength(double[] lengths)
	{
		seriesVdropCalc.lengths = lengths;
	}

	public void setSeriesFeet(boolean feet)
	{
		seriesVdropCalc.feet = feet;
	}

	// should be setSeriesAmps
	public void setSeriesLoad(boolean amps)
	{
		seriesVdropCalc.amps = amps;
	}

	public boolean isThreePhase()
	{
		return singleVdropCalc.threePhase;
	}

	public boolean isAC()
	{
		return singleVdropCalc.ac;
	}

	public WireSize getWireSize()
	{
		return singleVdropCalc.wireSize;
	}

	public boolean isWireSizeOver()
	{
		return singleVdropCalc.wireSizeOver;
	}

	public double getVdrop()
	{
		return singleVdropCalc.vdrop;
	}

	public double getVdropPercent()
	{
		return singleVdropCalc.vdropPercent;
	}

	public double getKva()
	{
		return singleVdropCalc.loadInKva;
	}

	public double getAmps()
	{
		return singleVdropCalc.loadInAmps;
	}

	public double getLengthInFeet()
	{
		return singleVdropCalc.lengthInFeetOneWay;
	}

	public double getLengthInMeters()
	{
		return singleVdropCalc.lengthInMetersOneWay;
	}

	public SeriesResult getSeriesResult()
	{
		return seriesVdropCalc.seriesResult;
	}

	public ConduitType getConduitMaterial()
	{
		return singleVdropCalc.conduitType;
	}

	public double getPowerFactor()
	{
		return singleVdropCalc.powerFactor;
	}

	public boolean isAlum()
	{

		return singleVdropCalc.alumWire;
	}

	public double getTemperature()
	{
		return singleVdropCalc.temperatureInC;
	}

	public CalcToPerform getCalc()
	{
		return singleVdropCalc.calcToPerform;
	}

	public int getConductorsPerPhase()
	{
		return singleVdropCalc.conductorsPerPhase;
	}

	public boolean getSeriesLoad()
	{
		return seriesVdropCalc.amps;
	}

	public boolean getSeriesFeet()
	{
		return seriesVdropCalc.feet;
	}
	public WireSize[] getWireSizes()
	{
		return seriesVdropCalc.getWireSizes();
	}
	
}
