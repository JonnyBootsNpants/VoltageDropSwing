package components;

import java.io.Serializable;

public class SeriesResult implements Serializable {
	
	private static final long serialVersionUID = -8514389689730915282L;
	
	public int maximumIndexForCalculation = -1;
	public double[] resultLoads = new double[99];
	public double[] resultSegmentVDs = new double[99];
	public double[] resultCumulativeVDs = new double[99];
}