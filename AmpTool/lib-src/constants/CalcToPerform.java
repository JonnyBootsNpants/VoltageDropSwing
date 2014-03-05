package constants;

import java.io.Serializable;

public enum CalcToPerform {

	GETGAUGE, GETVDROP, GETMAXLOAD, GETLENGTH;

	@Override
	public String toString() {

		switch (this) {
		
		case GETGAUGE:
			return "Conductor Gauge";
		case GETVDROP:
			return "Voltage Drop";
		case GETMAXLOAD:
			return "Maximum Load";
		case GETLENGTH:
			return "Maximum Length";
		default:
			return "";
		}
	}
}
