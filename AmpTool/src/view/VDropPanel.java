package view;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import components.VdropModel;

import constants.*;

import net.miginfocom.swing.MigLayout;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import java.text.*;

import view.SeriesVDropPanel;
import wires.*;

public class VDropPanel extends JPanel {

	static final String known = "\nEnter Known\nVariables";
	final private String ACString = "AC - Alternating Current (60Hz)";
	final private String DCString = "DC - Direct Current";

	final private String[] voltages = { "120", "208", "240", "277", "480",
			"600" };
	final private String[] temperatures = { "60\u00B0, 140\u00B0 F",
			"75\u00B0 C, 167\u00B0 F", "90\u00B0 C, 194\u00B0 F" };
	final private String[] conductors = { "Copper", "Aluminum" };
	final private String[] perPhase = { "1", "2", "3", "4", "5", "6", "7", "8",
			"9", "10", "11", "12" };
	final private String[] powerFactors = { "1.0", "0.95", "0.90", "0.85",
			"0.80", "0.75", "0.70", "0.65", "0.60" };
	final private String[] vdrop = { "2.0", "3.0", "5.0" };

	final private ConduitType[] conduits = ConduitType.values();

	private WireSize[] wireSizes = WireSize.values();

	private JPanel sourcePanel, ACParametersPanel, cablePanel, solvePanel,
			knownPanel, resultPanel, typeOfCalculationPanel, cardPanel,
			singlePanel;

	private SeriesVDropPanel seriesPanel;

	public CardLayout calculationTypeCardLayout;

	private double loadInitial, lengthInitial;

	private String vdropInitial, powerFactorInitial, voltageInitial,
			conductorsPerPhaseInitial;

	private JRadioButton acButton, dcButton;

	private JRadioButton loadButton, vdropButton, gaugeButton, lengthButton;

	private JRadioButton AWGButton, MMButton, ampButton, kVAButton, feetButton,
			metersButton;

	private JRadioButton singleButton, seriesButton;
	private ButtonGroup calculationGroup, solveGroup, ACDCGroup;

	private JComboBox voltageBox, conduitBox, loadCircuitBox,
			conductorMaterialBox, temperatureBox, conductorsPerPhaseBox,
			powerFactorBox, vdropBox, gaugeBox;

	private JLabel loadCurrentLabel, conductorLengthLabel, vdropLabel, gaugeLabel;
	private JLabel conduitLabel, powerFactorLabel, loadCircuitLabel;

	private JFormattedTextField loadField, lengthField;

	private DecimalFormat vdropFormat, vdropPercentFormat;
	private NumberFormat nf;
	private Font labelFont;

	private JTextPane resultPane;
	private JButton resultButton;
	private JTextArea warningPane;

	private VdropModel vdropModel;

	private VoltageListener voltageListen;
	private ConduitBoxListener conduitListen;
	private LoadCircuitBoxListener loadCircuitListen;
	private PowerFactorListener powerFactorListen;
	private TemperatureBoxListener temperatureListen;
	private ConductorMaterialBoxListener conductorMaterialBoxListen;
	private ConductorsPerPhaseBoxListener conductorsPerPhaseBoxListen;
	private ACDCListener acdcListen;
	private SolveListener solveListen;
	private GaugeListener gaugeListen;
	private AmpsKvaListener ampsKvaListen;
	private FeetMetersListener feetMetersListen;
	private CalculationListener calculationListen;
	private LengthListener lengthListen;
	private LoadListener loadListen;
	private VdropBoxListener vdropListen;

	public void setSeriesCopy(WireSize size) {
		seriesPanel.setCopyBoxContents(size);
	}
	public WireSize getSeriesCopy() {
		return seriesPanel.getCopyBoxContents();
	}
	public boolean isSingleAmps()
	{
		return ampButton.isSelected();
	}
	public boolean isSingleFeet()
	{
		return feetButton.isSelected();
	}
	public boolean isSingleCalcValid()
	{
		return ! resultPane.getText().equals(known);
	}
	public boolean isSeriesCalcValid()
	{
		return seriesPanel.isCalcValid();
	}
	public void insertModel(VdropModel vdropModel) {

		this.vdropModel = vdropModel;

		removeListeners();

		voltageBox.setSelectedItem(voltageInitial = Double.toString(vdropModel.getVoltage()));
		if (vdropModel.isAC()) {
			acButton.doClick();
			acOn();
		}
		else {
			dcButton.doClick();
			acOff();
		}
		conduitBox.setSelectedItem(vdropModel.getConduitMaterial());
		loadCircuitBox.setSelectedIndex(vdropModel.isThreePhase() ? 1 : 0);
		powerFactorBox.setSelectedItem(powerFactorInitial = Double.toString(vdropModel.getPowerFactor()));
		double temp = vdropModel.getTemperature();
		temperatureBox.setSelectedIndex(temp == 60.0 ? 0 : temp == 75 ? 1 : 2);
		conductorMaterialBox.setSelectedIndex(vdropModel.isAlum() ? 1 : 0);
		conductorsPerPhaseBox.setSelectedItem(conductorsPerPhaseInitial = Integer.toString(vdropModel.getConductorsPerPhase()));
		switch (vdropModel.getCalc()) {
			case GETGAUGE:
				gaugeButton.doClick();
				break;
			case GETVDROP:
				vdropButton.doClick();
				break;
			case GETMAXLOAD:
				loadButton.doClick();
				break;
			case GETLENGTH:
				lengthButton.doClick();
				break;
		}
		gaugeBox.setSelectedItem(vdropModel.getWireSize());
		loadField.setText(vdropFormat.format(kVAButton.isSelected() ? vdropModel.getKva() : vdropModel.getAmps()));
		lengthField.setText(vdropFormat.format(feetButton.isSelected() ? (lengthInitial = vdropModel.getLengthInFeet()) : (lengthInitial = vdropModel.getLengthInMeters())));
		vdropBox.setSelectedItem(vdropInitial = vdropFormat.format(vdropModel.getVdropPercent() * 100));
		
		seriesPanel.insertModel(vdropModel);
		addListeners();
		updateSingleResult();
	}

	private void removeListeners() {
		acButton.removeActionListener(acdcListen);
		dcButton.removeActionListener(acdcListen);
		voltageBox.removeActionListener(voltageListen);
		conduitBox.removeActionListener(conduitListen);
		loadCircuitBox.removeActionListener(loadCircuitListen);
		powerFactorBox.removeActionListener(powerFactorListen);
		temperatureBox.removeActionListener(temperatureListen);
		conductorMaterialBox.removeActionListener(conductorMaterialBoxListen);
		conductorsPerPhaseBox.removeActionListener(conductorsPerPhaseBoxListen);
		gaugeButton.removeActionListener(solveListen);
		vdropButton.removeActionListener(solveListen);
		loadButton.removeActionListener(solveListen);
		lengthButton.removeActionListener(solveListen);
		gaugeBox.removeActionListener(gaugeListen);
		lengthField.removePropertyChangeListener(lengthListen);
		loadField.removePropertyChangeListener(loadListen);
		vdropBox.removeActionListener(vdropListen);
	}

	private void addListeners() {
		acButton.addActionListener(acdcListen);
		dcButton.addActionListener(acdcListen);
		voltageBox.addActionListener(voltageListen);
		conduitBox.addActionListener(conduitListen);
		loadCircuitBox.addActionListener(loadCircuitListen);
		powerFactorBox.addActionListener(powerFactorListen);
		temperatureBox.addActionListener(temperatureListen);
		conductorMaterialBox.addActionListener(conductorMaterialBoxListen);
		conductorsPerPhaseBox.addActionListener(conductorsPerPhaseBoxListen);
		gaugeButton.addActionListener(solveListen);
		vdropButton.addActionListener(solveListen);
		loadButton.addActionListener(solveListen);
		lengthButton.addActionListener(solveListen);
		gaugeBox.addActionListener(gaugeListen);
		lengthField.addPropertyChangeListener(lengthListen);
		loadField.addPropertyChangeListener(loadListen);
		vdropBox.addActionListener(vdropListen);
	}

	public boolean isKVASelected() {
		return kVAButton.isSelected();
	}

	public void setKVA(boolean isKVA) {
		kVAButton.removeActionListener(ampsKvaListen);
		ampButton.removeActionListener(ampsKvaListen);
		if (isKVA)
			kVAButton.doClick();
		else
			ampButton.doClick();
		kVAButton.addActionListener(ampsKvaListen);
		ampButton.addActionListener(ampsKvaListen);

	}

	public boolean isFeetSelected() {
		return feetButton.isSelected();
	}

	public void setFeet(boolean isFeet) {
		feetButton.removeActionListener(feetMetersListen);
		metersButton.removeActionListener(feetMetersListen);
		if (isFeet)
			feetButton.doClick();
		else
			metersButton.doClick();
		feetButton.addActionListener(feetMetersListen);
		metersButton.addActionListener(feetMetersListen);
	}

	public boolean isSingleSelected() {
		return singleButton.isSelected();
	}

	public void setSingle(boolean isSingle) {
		if (isSingle)
			singleButton.doClick();
		else
			seriesButton.doClick();
	}

	public VDropPanel(VdropModel vdropModel) {

		super(new MigLayout("filly, gapy 10!"));

		vdropFormat = new DecimalFormat("0.00");
		vdropPercentFormat = new DecimalFormat("0.00%");

		sourcePanel = new SourcePanel();
		add(sourcePanel, "split 2, growx");

		typeOfCalculationPanel = new TypeOfCalculationPanel();
		add(typeOfCalculationPanel, "grow 10, wrap");

		ACParametersPanel = new ACParametersPanel();
		add(ACParametersPanel, "wrap, growx");

		cablePanel = new CablePanel();
		add(cablePanel, "wrap, growx");

		singlePanel = new SinglePanel();
		seriesPanel = new SeriesVDropPanel(vdropModel);

		calculationTypeCardLayout = new CardLayout();
		cardPanel = new JPanel(calculationTypeCardLayout);
		cardPanel.add(singlePanel, "Single");
		cardPanel.add(seriesPanel, "Series");

		calculationTypeCardLayout.show(cardPanel, "Series");

		add(cardPanel, "h ::1000, grow, pushy 200");

		this.vdropModel = vdropModel;
		initializeModel();
	}

	private class SourcePanel extends JPanel {

		public SourcePanel() {

			super(new MigLayout());

			acButton = new JRadioButton(ACString, true);
			dcButton = new JRadioButton(DCString);

			acButton.setActionCommand(ACString);
			dcButton.setActionCommand(DCString);

			acdcListen = new ACDCListener();

			acButton.addActionListener(acdcListen);
			dcButton.addActionListener(acdcListen);

			ACDCGroup = new ButtonGroup();

			ACDCGroup.add(acButton);
			ACDCGroup.add(dcButton);

			voltageBox = new JComboBox(voltages);
			voltageBox.setEditable(true);

			voltageInitial = (String) voltageBox.getSelectedItem();

			voltageListen = new VoltageListener();
			voltageBox.addActionListener(voltageListen);

			JLabel voltageLabel = new JLabel("Voltage");

			add(acButton, "h 13!");
			add(voltageLabel, "wrap, gapleft 25");
			add(dcButton, "h 13!, push, aligny bottom");
			add(voltageBox, "w :127:127, gapleft 25");
			setBorder(BorderFactory.createTitledBorder("Power Source"));
		}
	}

	private class TypeOfCalculationPanel extends JPanel {

		public TypeOfCalculationPanel() {
			super(new MigLayout());

			singleButton = new JRadioButton("Single Calculation");
			seriesButton = new JRadioButton("Series Calculation", true);

			singleButton.setActionCommand("Single");
			seriesButton.setActionCommand("Series");

			calculationGroup = new ButtonGroup();
			calculationGroup.add(singleButton);
			calculationGroup.add(seriesButton);

			calculationListen = new CalculationListener();
			singleButton.addActionListener(calculationListen);
			seriesButton.addActionListener(calculationListen);

			add(singleButton, "h 13!, wrap");
			add(seriesButton, "h 13!, push, aligny bottom");

			setBorder(BorderFactory.createTitledBorder("Type of Calculation"));
		}
	}

	private class ACParametersPanel extends JPanel {

		public ACParametersPanel() {
			super(new MigLayout("", "[]55[]55[]"));
			{
				conduitBox = new JComboBox(conduits);

				conduitListen = new ConduitBoxListener();
				conduitBox.addActionListener(conduitListen);

				powerFactorBox = new JComboBox(powerFactors);
				powerFactorBox.setEditable(true);
				powerFactorInitial = (String) powerFactorBox.getSelectedItem();

				powerFactorListen = new PowerFactorListener();
				powerFactorBox.addActionListener(powerFactorListen);

				String[] loadCircuits = { "1\u03D5", "3\u03D5" };
				loadCircuitBox = new JComboBox(loadCircuits);

				loadCircuitListen = new LoadCircuitBoxListener();
				loadCircuitBox.addActionListener(loadCircuitListen);

				conduitLabel = new JLabel("Conduit Material");
				powerFactorLabel = new JLabel("Power Factor");
				loadCircuitLabel = new JLabel("Configuration");

				add(conduitLabel);
				add(loadCircuitLabel);
				add(powerFactorLabel, "wrap");

				add(conduitBox, "w :127:127");
				add(loadCircuitBox, "w :127:127");
				add(powerFactorBox, "w :127:127");

				setBorder(BorderFactory.createTitledBorder("AC Parameters"));
			}
		}
	}

	private class CablePanel extends JPanel {

		public CablePanel() {

			super(new MigLayout("", "[]55[]55[]"));

			temperatureBox = new JComboBox(temperatures);
			temperatureBox.setSelectedIndex(1);

			temperatureListen = new TemperatureBoxListener();
			temperatureBox.addActionListener(temperatureListen);

			conductorMaterialBox = new JComboBox(conductors);

			conductorMaterialBoxListen = new ConductorMaterialBoxListener();
			conductorMaterialBox
					.addActionListener(conductorMaterialBoxListen);

			conductorsPerPhaseBox = new JComboBox(perPhase);
			conductorsPerPhaseBox.setEditable(true);

			conductorsPerPhaseInitial = (String) conductorsPerPhaseBox
					.getSelectedItem();

			conductorsPerPhaseBoxListen = new ConductorsPerPhaseBoxListener();
			conductorsPerPhaseBox
					.addActionListener(conductorsPerPhaseBoxListen);

			JLabel temperatureLabel = new JLabel("Temperature");
			JLabel conductorMaterialLabel = new JLabel("Conductor Metal");
			JLabel conductorsPerPhaseLabel = new JLabel("Conductors per Phase");

			add(temperatureLabel);
			add(conductorMaterialLabel);
			add(conductorsPerPhaseLabel, "wrap");

			add(temperatureBox, "w :127:127");
			add(conductorMaterialBox, "w :127:127");
			add(conductorsPerPhaseBox, "w :127:127");

			setBorder(BorderFactory.createTitledBorder("Conductor Parameters"));

		}
	}

	private class SinglePanel extends JPanel {
		public SinglePanel() {
			super(new MigLayout("fillx, insets 0"));
			solvePanel = new SolvePanel();
			add(solvePanel, "wrap, grow");

			knownPanel = new KnownPanel();
			add(knownPanel, "growy, split 2");

			resultPanel = new ResultPanel();
			add(resultPanel, "grow");
		}
	}

	private class SolvePanel extends JPanel {

		public SolvePanel() {

			super(new MigLayout("fill"));

			loadButton = new JRadioButton("Maximum Load");
			vdropButton = new JRadioButton("Voltage Drop", true);
			gaugeButton = new JRadioButton("Conductor Gauge");
			lengthButton = new JRadioButton("Conductor Length");

			solveListen = new SolveListener();

			vdropButton.addActionListener(solveListen);
			loadButton.addActionListener(solveListen);
			gaugeButton.addActionListener(solveListen);
			lengthButton.addActionListener(solveListen);

			solveGroup = new ButtonGroup();

			solveGroup.add(loadButton);
			solveGroup.add(vdropButton);
			solveGroup.add(gaugeButton);
			solveGroup.add(lengthButton);

			add(gaugeButton);
			add(vdropButton);
			add(loadButton);
			add(lengthButton);

			setBorder(BorderFactory.createTitledBorder("Solve for:"));
		}
	}

	private class KnownPanel extends JPanel {

		public KnownPanel() {
			super(new MigLayout("", "", "[][]15[][]15[][]15[][]"));

			vdropBox = new JComboBox(vdrop);
			vdropBox.setEditable(true);

			vdropInitial = (String) vdropBox.getSelectedItem();

			vdropListen = new VdropBoxListener();
			vdropBox.addActionListener(vdropListen);

			gaugeBox = new JComboBox(wireSizes);
			gaugeBox.setSelectedIndex(3);
			gaugeListen = new GaugeListener();
			gaugeBox.addActionListener(gaugeListen);

			nf = NumberFormat.getNumberInstance();

			loadField = new JFormattedTextField(nf);
			lengthField = new JFormattedTextField(nf);

			nf.setGroupingUsed(false);

			loadField.setColumns(2);
			lengthField.setColumns(2);

			loadListen = new LoadListener();
			loadField.addPropertyChangeListener("value", loadListen);

			gaugeLabel = new JLabel("Conductor Gauge");
			vdropLabel = new JLabel("Percent Voltage Drop");
			loadCurrentLabel = new JLabel("Load");
			conductorLengthLabel = new JLabel("Length (1-way)");

			AWGButton = new JRadioButton("AWG", true);
			MMButton = new JRadioButton("mm\u00B2");

			MMButton.setEnabled(false);

			ButtonGroup gaugeGroup = new ButtonGroup();
			gaugeGroup.add(AWGButton);
			gaugeGroup.add(MMButton);

			ampButton = new JRadioButton("Amps", true);
			kVAButton = new JRadioButton("kVA");

			ampButton.setActionCommand("Amps");
			kVAButton.setActionCommand("kVA");

			ampsKvaListen = new AmpsKvaListener();
			ampButton.addActionListener(ampsKvaListen);
			kVAButton.addActionListener(ampsKvaListen);

			ButtonGroup currentGroup = new ButtonGroup();
			currentGroup.add(ampButton);
			currentGroup.add(kVAButton);

			feetButton = new JRadioButton("Feet", true);
			metersButton = new JRadioButton("Meters");

			feetButton.setActionCommand("Feet");
			metersButton.setActionCommand("Meters");

			feetMetersListen = new FeetMetersListener();
			feetButton.addActionListener(feetMetersListen);
			metersButton.addActionListener(feetMetersListen);

			lengthListen = new LengthListener();
			lengthField.addPropertyChangeListener("value", lengthListen);

			ButtonGroup lengthGroup = new ButtonGroup();
			lengthGroup.add(feetButton);
			lengthGroup.add(metersButton);

			lengthField.setHorizontalAlignment(JTextField.RIGHT);
			loadField.setHorizontalAlignment(JTextField.RIGHT);

			add(gaugeLabel, "wrap");
			add(gaugeBox, "w :127:127");
			add(AWGButton);
			add(MMButton, "wrap");
			add(vdropLabel, "wrap");
			add(vdropBox, "w :127:127, wrap");
			add(loadCurrentLabel, "wrap");
			add(loadField, "growx");
			add(ampButton);
			add(kVAButton, "wrap");
			add(conductorLengthLabel, "wrap");
			add(lengthField, "growx");
			add(feetButton);
			add(metersButton);
			setBorder(BorderFactory.createTitledBorder("Known Variables:"));
		}
	}

	private class ResultPanel extends JPanel {

		public ResultPanel() {
			super(new MigLayout("fill"));

			StyledDocument document = new DefaultStyledDocument();
			Style defaultStyle = document.getStyle(StyleContext.DEFAULT_STYLE);
			StyleConstants.setAlignment(defaultStyle,
					StyleConstants.ALIGN_CENTER);

			resultPane = new JTextPane(document);
			resultPane.setEditable(false);
			resultPane.setFont(new Font("Serif", Font.PLAIN, 14));

			resultPane.setText(known);

			warningPane = new JTextArea(
					"Does not consider allowed\nNEC ampacity or derating");
			warningPane.setOpaque(false);

			labelFont = UIManager.getFont("Label.font");
			warningPane.setFont(labelFont);

			add(resultPane, "h 87!, wrap, growx, gaptop 40");
			add(warningPane, "wrap, alignx center, aligny top, pushy 200");
			// add(resultButton, "aligny bottom, push, growx, wrap");

			setBorder(BorderFactory.createTitledBorder("Results:"));
		}
	}

	private void initializeModel() {
		vdropModel.setAC(acButton.isSelected());
		vdropModel.setVoltage(Double.parseDouble((String) voltageBox
				.getSelectedItem()), ampButton.isSelected());
		vdropModel.setConduitType((ConduitType) conduitBox.getSelectedItem());
		if (loadCircuitBox.getSelectedItem().equals("1\u03D5")) {
			vdropModel.setThreePhase(false, ampButton.isSelected());
		} else {
			vdropModel.setThreePhase(true, ampButton.isSelected());
		}
		vdropModel.setPowerFactor(Double.parseDouble((String) powerFactorBox
				.getSelectedItem()));
		switch (temperatureBox.getSelectedIndex()) {
			case 0:
				vdropModel.setTemperature(60.0);
				break;
			case 1:
				vdropModel.setTemperature(75.0);
				break;
			case 2:
				vdropModel.setTemperature(90.0);
				break;
		}
		if (conductorMaterialBox.getSelectedItem().equals("Aluminum")) {
			vdropModel.setAlum(true);
		} else {
			vdropModel.setAlum(false);
		}
		vdropModel.setConductorsPerPhase(Integer
				.parseInt((String) conductorsPerPhaseBox.getSelectedItem()));
		if (gaugeButton.isSelected()) {
			vdropModel.setCalc(CalcToPerform.GETGAUGE);
			allButGaugeOn();
		}
		if (vdropButton.isSelected()) {
			vdropModel.setCalc(CalcToPerform.GETVDROP);
			allButVdropOn();
		}
		if (loadButton.isSelected()) {
			vdropModel.setCalc(CalcToPerform.GETMAXLOAD);
			allButLoadOn();
		}
		if (lengthButton.isSelected()) {
			vdropModel.setCalc(CalcToPerform.GETLENGTH);
			allButLengthOn();
		}
		vdropModel.setWireSize((WireSize) gaugeBox.getSelectedItem());
		vdropModel.setSeriesWireSize((WireSize) seriesPanel.copyWireBox.getSelectedItem());
		vdropModel.setVdropPercent(Double.parseDouble((String) vdropBox
				.getSelectedItem()) / 100);
		boolean amps = seriesPanel.seriesAmpsButton.isSelected();
		vdropModel.setSeriesLoad(amps);
		boolean feet = seriesPanel.seriesFeetButton.isSelected();
		vdropModel.setSeriesFeet(feet);
	}

	private void allButGaugeOn() {
		gaugeBox.setVisible(false);
		AWGButton.setVisible(true);
		MMButton.setVisible(true);
		gaugeLabel.setVisible(false);
		vdropBox.setVisible(true);
		vdropLabel.setVisible(true);
		loadField.setVisible(true);
		ampButton.setVisible(true);
		kVAButton.setVisible(true);
		loadCurrentLabel.setVisible(true);
		lengthField.setVisible(true);
		feetButton.setVisible(true);
		metersButton.setVisible(true);
		conductorLengthLabel.setVisible(true);
	}

	private void allButVdropOn() {
		gaugeBox.setVisible(true);
		AWGButton.setVisible(true);
		MMButton.setVisible(true);
		gaugeLabel.setVisible(true);
		vdropBox.setVisible(false);
		vdropLabel.setVisible(false);
		loadField.setVisible(true);
		ampButton.setVisible(true);
		kVAButton.setVisible(true);
		loadCurrentLabel.setVisible(true);
		lengthField.setVisible(true);
		feetButton.setVisible(true);
		metersButton.setVisible(true);
		conductorLengthLabel.setVisible(true);

	}

	private void allButLoadOn() {
		gaugeBox.setVisible(true);
		AWGButton.setVisible(true);
		MMButton.setVisible(true);
		gaugeLabel.setVisible(true);
		vdropBox.setVisible(true);
		vdropLabel.setVisible(true);
		loadField.setVisible(false);
		ampButton.setVisible(true);
		kVAButton.setVisible(true);
		loadCurrentLabel.setVisible(false);
		lengthField.setVisible(true);
		feetButton.setVisible(true);
		metersButton.setVisible(true);
		conductorLengthLabel.setVisible(true);

	}

	private void allButLengthOn() {
		gaugeBox.setVisible(true);
		AWGButton.setVisible(true);
		MMButton.setVisible(true);
		gaugeLabel.setVisible(true);
		vdropBox.setVisible(true);
		vdropLabel.setVisible(true);
		loadField.setVisible(true);
		ampButton.setVisible(true);
		kVAButton.setVisible(true);
		loadCurrentLabel.setVisible(true);
		lengthField.setVisible(false);
		feetButton.setVisible(true);
		metersButton.setVisible(true);
		conductorLengthLabel.setVisible(false);

	}

	private void acOff() {
		conduitBox.setVisible(false);
		loadCircuitBox.setVisible(false);
		powerFactorBox.setVisible(false);
		conduitLabel.setVisible(false);
		loadCircuitLabel.setVisible(false);
		powerFactorLabel.setVisible(false);
	}

	private void acOn() {
		conduitBox.setVisible(true);
		loadCircuitBox.setVisible(true);
		powerFactorBox.setVisible(true);
		conduitLabel.setVisible(true);
		loadCircuitLabel.setVisible(true);
		powerFactorLabel.setVisible(true);
	}

	private void updateSingleResult() {

		if (gaugeButton.isSelected()) {
			if (!(loadField.getText().equals("") || lengthField.getText()
					.equals(""))) {
				vdropModel.setCalc(CalcToPerform.GETGAUGE);
				vdropModel.updateSingle();

				if (vdropModel.isWireSizeOver()) {
					resultPane
							.setText("\nThe Minimum Gauge is\nLarger than 1000 MCM");
				} else {
					String gaugeString = vdropModel.getWireSize().toString();
					resultPane.setText("\nThe Minimum Gauge is\n" + gaugeString
							+ " AWG");
					gaugeBox.setSelectedItem(vdropModel.getWireSize());
				}
			}
			allButGaugeOn();
			return;
		}
		if (vdropButton.isSelected()) {
			if (!(loadField.getText().equals("") || lengthField.getText()
					.equals(""))) {
				vdropModel.setCalc(CalcToPerform.GETVDROP);
				vdropModel.updateSingle();
				String vdropString = vdropFormat.format(vdropModel.getVdrop());
				String vdropPercentString = vdropPercentFormat
						.format(vdropModel.getVdropPercent());

				resultPane.setText("\n" + vdropPercentString + "\n"
						+ vdropString + " Volts"
						+ (vdropModel.isThreePhase() && acButton.isSelected() ? " Line-To-Line" : ""));
				vdropBox.setSelectedItem(vdropFormat.format(vdropModel
						.getVdropPercent() * 100));
			}
			allButVdropOn();
			return;

		}
		if (loadButton.isSelected()) {
			if (!lengthField.getText().equals("")) {
				vdropModel.setCalc(CalcToPerform.GETMAXLOAD);
				vdropModel.updateSingle();
				String loadString;
				if (kVAButton.isSelected()) {
					loadString = vdropFormat.format(vdropModel.getKva());
				} else {
					loadString = vdropFormat.format(vdropModel.getAmps());
				}
				resultPane.setText("\nThe Maximum Load is\n" + loadString
						+ (kVAButton.isSelected() ? " kVA" : " amperes"));
				loadField.setText(loadString);
			}
			allButLoadOn();
			return;
		}
		if (lengthButton.isSelected()) {
			if (!loadField.getText().equals("")) {
				vdropModel.setCalc(CalcToPerform.GETLENGTH);
				vdropModel.updateSingle();
				String lengthString = vdropFormat.format(vdropModel
						.getLengthInFeet());
				if (feetButton.isSelected()) {
					lengthString = vdropFormat.format(vdropModel
							.getLengthInFeet());
				} else {
					lengthString = vdropFormat.format(vdropModel
							.getLengthInMeters());
				}
				resultPane.setText("\nThe Maximum Length is\n" + lengthString
						+ (feetButton.isSelected() ? " Feet" : " Meters"));
				lengthField.setText(lengthString);
			}
			allButLengthOn();
		}

	}

	// combine listeners
	private class CalculationListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			calculationTypeCardLayout.show(cardPanel, e.getActionCommand());
		}
	}

	private class ACDCListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals(ACString)) {
				acOn();
				vdropModel.setAC(true);
				updateSingleResult();
				seriesPanel.updateSeriesResult();
			} else {
				acOff();
				vdropModel.setAC(false);
				updateSingleResult();
				seriesPanel.updateSeriesResult();
			}
		}
	}

	private class VoltageListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			if (e.getActionCommand().equals("comboBoxChanged")) {
				String testVoltage = (String) voltageBox.getSelectedItem();
				double newVoltage = 0.0;
				try {
					newVoltage = Double.parseDouble(testVoltage);
				} catch (Exception ex) {
					voltageBox.setSelectedItem(voltageInitial);
					return;
				}
				if (newVoltage < 0.0) {
					voltageBox.setSelectedItem(voltageInitial);
					return;
				}
				voltageInitial = testVoltage;
				vdropModel.setVoltage(newVoltage, ampButton.isSelected());
				updateSingleResult();
				seriesPanel.updateSeriesResult();
			}
		}
	}

	private class ConduitBoxListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("comboBoxChanged")) {
				vdropModel.setConduitType((ConduitType) conduitBox
						.getSelectedItem());
				updateSingleResult();
				seriesPanel.updateSeriesResult();
			}
		}
	}

	private class LoadCircuitBoxListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("comboBoxChanged")) {
				if (loadCircuitBox.getSelectedItem().equals("1\u03D5")) {
					vdropModel.setThreePhase(false, ampButton.isSelected());
					updateSingleResult();
					seriesPanel.updateSeriesResult();
				} else {
					vdropModel.setThreePhase(true, ampButton.isSelected());
					updateSingleResult();
					seriesPanel.updateSeriesResult();
				}
			}
		}
	}

	private class PowerFactorListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("comboBoxChanged")) {
				String testPowerFactor = (String) powerFactorBox
						.getSelectedItem();
				double newPowerFactor = 0.0;
				try {
					newPowerFactor = Double.parseDouble(testPowerFactor);
				} catch (Exception ex) {
					powerFactorBox.setSelectedItem(powerFactorInitial);
					return;
				}
				if (newPowerFactor < 0.0 || newPowerFactor > 1.0) {
					powerFactorBox.setSelectedItem(powerFactorInitial);
					return;
				}
				powerFactorInitial = testPowerFactor;
				vdropModel.setPowerFactor(newPowerFactor);
				updateSingleResult();
				seriesPanel.updateSeriesResult();
			}
		}
	}

	private class TemperatureBoxListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("comboBoxChanged")) {
				switch (temperatureBox.getSelectedIndex()) {
					case 0:
						vdropModel.setTemperature(60.0);
						break;
					case 1:
						vdropModel.setTemperature(75.0);
						break;
					case 2:
						vdropModel.setTemperature(90.0);
						break;
				}
				updateSingleResult();
				seriesPanel.updateSeriesResult();
			}
		}
	}

	private class ConductorMaterialBoxListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("comboBoxChanged")) {
				if (conductorMaterialBox.getSelectedItem().equals("Aluminum")) {
					vdropModel.setAlum(true);
					updateSingleResult();
					seriesPanel.updateSeriesResult();
				} else {
					vdropModel.setAlum(false);
					updateSingleResult();
					seriesPanel.updateSeriesResult();
				}
			}
		}
	}

	private class ConductorsPerPhaseBoxListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String newString = (String) conductorsPerPhaseBox.getSelectedItem();
			int newInt = 0;
			try {
				newInt = Integer.parseInt(newString);
			} catch (Exception ex) {
				conductorsPerPhaseBox
						.setSelectedItem(conductorsPerPhaseInitial);
				return;
			}
			if (newInt < 0) {
				conductorsPerPhaseBox
						.setSelectedItem(conductorsPerPhaseInitial);
				return;
			}
			conductorsPerPhaseInitial = newString;
			vdropModel.setConductorsPerPhase(newInt);
			updateSingleResult();
			seriesPanel.updateSeriesResult();
		}
	}

	private class SolveListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			updateSingleResult();
		}
	}

	private class GaugeListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("comboBoxChanged")) {
				vdropModel.setWireSize((WireSize) gaugeBox.getSelectedItem());
				updateSingleResult();
			}
		}
	}

	private class VdropBoxListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String newString = (String) vdropBox.getSelectedItem();
			double newDouble = 0.0;
			try {
				newDouble = Double.parseDouble(newString);
			} catch (Exception ex) {
				vdropBox.setSelectedItem(vdropInitial);
				return;
			}
			if (newDouble <= 0.0 || newDouble > 100.0) {
				vdropBox.setSelectedItem(vdropInitial);
				return;
			}
			vdropInitial = newString;
			vdropModel.setVdropPercent(newDouble / 100.0);
			updateSingleResult();
		}
	}

	private class LoadListener implements PropertyChangeListener {

		public void propertyChange(PropertyChangeEvent e) {
			if (loadField.getValue() != null) {
				double entered = ((Number) loadField.getValue()).doubleValue();
				if (entered > 0.0 && entered < 50000) {
					if (kVAButton.isSelected()) {
						vdropModel.setKva(entered);
					} else {
						vdropModel.setCurrent(entered);
					}
					loadInitial = entered;
					updateSingleResult();
				} else {
					if (loadInitial > 0.0)
						loadField.setValue(loadInitial);
					else 
						loadField.setValue(null);
				}
			}
		}
	}

	private class AmpsKvaListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (!loadField.getText().equals("")) {
				if (e.getActionCommand().equals("Amps")) {
					vdropModel.setCurrent(Double.parseDouble(loadField
							.getText()));
					updateSingleResult();

				} else {
					vdropModel.setKva(Double.parseDouble(loadField.getText()));
					updateSingleResult();
				}
			}
		}
	}

	private class LengthListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent e) {
			if (lengthField.getValue() != null) {
				double entered = ((Number) lengthField.getValue())
						.doubleValue();
				if (entered > 0.0 && entered < 50000) {
					if (feetButton.isSelected()) {
						vdropModel.setLengthInFeet(entered);
						lengthInitial = entered;
					} else {
						vdropModel.setLengthInMeters(entered);
						lengthInitial = entered;
					}
					updateSingleResult();
				} else {
					if (lengthInitial > 0.0)
						lengthField.setValue(lengthInitial);
					else 
						lengthField.setValue(null);
				}
			}
		}
	}

	private class FeetMetersListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (!lengthField.getText().equals("")) {
				if (e.getActionCommand().equals("Feet")) {
					vdropModel.setLengthInFeet(Double.parseDouble(lengthField
							.getText()));
					updateSingleResult();
				} else {
					vdropModel.setLengthInMeters(Double.parseDouble(lengthField
							.getText()));
					updateSingleResult();
				}
			}
		}
	}
}
