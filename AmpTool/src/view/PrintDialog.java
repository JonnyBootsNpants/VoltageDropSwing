package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.miginfocom.swing.MigLayout;

import components.SeriesResult;
import components.VdropModel;
import constants.CalcToPerform;

public class PrintDialog extends JDialog implements ActionListener
{
	final String phase = "\u03D5";
	final String deg = "\u00B0";
	VDropPanel panel;
	VdropModel model;
	VdropFrame parent;
	JTextArea textArea;
	
	
	PrintDialog(VdropFrame parent, VDropPanel panel, VdropModel model)
	{
		super(parent, "Print Results..", true);

		this.panel = panel;
		this.model = model;
		this.parent = parent;

		textArea = new JTextArea();
		JScrollPane scroll = new JScrollPane(textArea);
		JButton printButton = new JButton("Print");

		textArea.setEditable(true);
		
		printButton.addActionListener(this);
		String output = FormatOutput();
		textArea.setText(output);
		textArea.setFont(new Font("Courier New", Font.PLAIN, 12));

		getContentPane().setLayout(new MigLayout());
		getContentPane().add(scroll, "wrap");
		getContentPane().add(printButton, "grow");

		setLocationRelativeTo(parent);
		setResizable(true);
		pack();
		setVisible(true);
	}

	private String FormatOutput()
	{
		StringBuilder sb = new StringBuilder();

		final boolean ac = model.isAC();
		final boolean alum = model.isAlum();
		final boolean threePhase = model.isThreePhase();
		final CalcToPerform calc = model.getCalc();
		final boolean singleAmps = panel.isSingleAmps();
		final boolean singleFeet = panel.isSingleFeet();
		final String singleLoad = singleAmps ? "Amps" : "kVA";
		final String singleLength = singleFeet ? "Feet" : "Meters";
		final double singleLoadDouble = singleAmps ? model.getAmps() : model.getKva();
		final double singleLengthDouble = singleFeet ? model.getLengthInFeet() : model.getLengthInMeters();
		final String seriesLoad = model.getSeriesLoad() ? "Load(amps)" : "Load(VA)";
		final String seriesLength = model.getSeriesFeet() ? "Length(feet)" : "Length(meters)";

		final SeriesResult seriesResult = model.getSeriesResult();
		final int maximumIndex = seriesResult.maximumIndexForCalculation;

		final String ph = model.isThreePhase() ? "3" + phase : "1" + phase;
		final String acdc = String.format("Source             =    %s" + (ac ? ", " : "") + "%s%n",
				ac ? "AC" : "DC", ac ? ph : "");
		final String volt = String.format("Voltage            =    %.1f%n", model.getVoltage());
		final String cond = String.format("Cond. Material     =    %s%n", model.getConduitMaterial());
		final String powf = String.format("Power Factor       =    %.2f%n", model.getPowerFactor());
		final String temp = String.format("Cond. Temp.        =    %.0f%sC%n", model.getTemperature(), deg);
		final String metl = String.format("Conductor Metal    =    %s%n", alum ? "Aluminum" : "Copper");
		final String cndp = String.format("Cond. Per Phase    =    %d%n", model.getConductorsPerPhase());

		final String wire = String.format("Wire Size          =    %s%n", model.getWireSize());

		final String vdrp = String.format("Max Voltage Drop   =    %.2f%%%n", model.getVdropPercent() * 100.0);
		final String silo = String.format("Load               =    %.1f %s%n", singleLoadDouble, singleLoad);
		final String sile = String.format("Length (1-way)     =    %.1f %s%n", singleLengthDouble, singleLength);

		sb.append("Voltage Drop Calculation Results:\n\n");

		if (!parent.isFileNameNull())
		{
			String file = parent.getFileName().toString();
			sb.append("File: " + file + "\n\n");
		}

		sb.append(acdc);
		sb.append(volt);

		if (ac)
		{
			sb.append(cond);
			sb.append(powf);
		}
		sb.append(temp);
		sb.append(metl);
		sb.append(cndp);

		String result = null;
		String source = "Based on:\nANSI/IEEE Standard 141, 1993\nNEC Ch. 9, Tables 8 & 9";
		if (panel.isSingleSelected())
		{
			switch (calc)
			{
				case GETGAUGE:
					sb.append(vdrp);
					sb.append(silo);
					sb.append(sile);
					
					if (model.isWireSizeOver())
					{
						result = "\nResult:\nThe Minimum Conductor Gauage is\nLarger than 1000 MCM\n\n";
					}
					else
					{
						result = String.format("%nResult:%nThe Minimum Conductor Gauge is #%s.%n%n", model.getWireSize());
					}
					break;
				case GETVDROP:
					sb.append(wire);
					sb.append(silo);
					sb.append(sile);
					
					result = String.format("\nResult:%nThe Voltage Drop is %.2f%%, %.2fV" + (threePhase ? " L-L." : ".") + "%n%n",
							model.getVdropPercent() * 100.0, model.getVdrop());
					break;
				case GETMAXLOAD:
					sb.append(wire);
					sb.append(vdrp);
					sb.append(sile);
					result = String.format("\nResult:%nThe Maximum Load is %.1f %s%n%n", singleLoadDouble, singleLoad);
					break;
				case GETLENGTH:
					sb.append(wire);
					sb.append(vdrp);
					sb.append(silo);
					result = String.format("\nResult:%nThe Maximum Length is %.1f %s%n%n", singleLengthDouble, singleLength);
					break;
			}
		}
		else
		{
			sb.append("\nSegment Info:\n\n");
			sb.append(String.format("   %s     %s      %s      %s\n", "Seg", "Gauge", seriesLoad, seriesLength));
			sb.append(String.format("   %s     %s     %s     %s\n", "---", "-----", "----------", "------------"));
			if (maximumIndex >= 0)
				for (int i = 0; i <= maximumIndex; ++i)
					sb.append(String.format("%5d%10s%14.2f%16.2f\n", i + 1, model.getSeriesSizes()[i],
							model.getSeriesLoads()[i], model.getSeriesLengths()[i]));
			sb.append("\nResults Info:\n\n");
			sb.append(String.format("   %s      %s      %s      %s\n", "Seg", "Total Amps", "SegVD%", "CumVD%"));
			sb.append(String.format("   %s      %s      %s      %s\n", "---", "----------", "------", "------"));
			if (maximumIndex >= 0)
			{
				for (int i = 0; i <= maximumIndex; ++i)
				{
					double load = seriesResult.resultLoads[i];
					double segVD = seriesResult.resultSegmentVDs[i];
					double cumVD = seriesResult.resultCumulativeVDs[i];
					sb.append(String.format("%5d%15.2f%13.2f%12.2f\n", i + 1, load, segVD * 100.0, cumVD * 100.0));

				}
				result = String.format("%n%nResult:%nThe Total Voltage Drop is %.2f%%.%n%n", seriesResult.resultCumulativeVDs[maximumIndex] * 100.0);
			}
		}
		sb.append(result);
		sb.append(source);
		return sb.toString();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		try
		{
			textArea.print();
		}
		catch (PrinterException ex)
		{
			showErrorDialog();
		}
	}
	public void showErrorDialog()
	{
		JOptionPane.showMessageDialog(this, "There was a problem printing the calculation.", "Warning", 
										JOptionPane.WARNING_MESSAGE);
	}
}
