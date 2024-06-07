package view;

import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.util.EventObject;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerListModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import components.SeriesResult;
import components.VdropModel;

import constants.WireSize;
import net.miginfocom.swing.MigLayout;

public class SeriesVDropPanel extends JPanel
{
	final String segment = "\nEnter\nSegment Info";
	private WireSize[] wireSizes = WireSize.values();
	private SeriesSegmentPanel seriesSegmentPanel;
	private SeriesResultPanel seriesResultPanel;

	public JComboBox copyWireBox;

	public JButton copyWireButton;

	public JRadioButton seriesFeetButton;
	public JRadioButton seriesMetersButton;
	public ButtonGroup seriesLengthGroup;

	public JRadioButton seriesAmpsButton;
	public JRadioButton seriesVAButton;
	public ButtonGroup seriesLoadGroup;

	private SegmentInfoPanel segmentInfoPanel;
	public JScrollPane scrollSegment;

	private JLabel seriesLengthLabel;
	private JLabel seriesLoadLabel;

	private CalculationResultsPanel calculationResultsPanel;

	public JScrollPane scrollResults;
	public Font labelFont;

	public JSpinner[] sizeSpinners;
	private JTextField[] lengths, loads;
	public double[] lengthsInitial, loadsInitial;
	public JTextField[] totalLoads, segmentVDs, cumulativeVDs;
	public JTextPane resultPane;

	private VdropModel vdropModel;

	private SeriesResult seriesResult;

	private DecimalFormat resultFormat;
	private DecimalFormat totalFormat;

	private boolean listenersActive = true;

	public boolean isCalcValid()
	{
		return ! resultPane.getText().equals(segment);
	}
	
	public WireSize getCopyBoxContents()
	{
		return (WireSize) copyWireBox.getSelectedItem();
	}

	public void setCopyBoxContents(WireSize size)
	{
		copyWireBox.setSelectedItem(size);
	}

	public SeriesVDropPanel(VdropModel vdropModel) {
		super(new MigLayout("insets 0, fill"));

		this.vdropModel = vdropModel;

		resultFormat = new DecimalFormat("0.00");
		totalFormat = new DecimalFormat("0.00%");

		seriesSegmentPanel = new SeriesSegmentPanel();
		add(seriesSegmentPanel, "growy, split 2");

		seriesResultPanel = new SeriesResultPanel();
		add(seriesResultPanel, "grow");
	}

	public void insertModel(VdropModel vdropModel)
	{
		this.vdropModel = vdropModel;
		listenersActive = true;
		if (vdropModel.getSeriesLoad())
		{
			seriesAmpsButton.doClick();
			seriesLoadLabel.setText("Amp Load");
		}
		else
		{
			seriesVAButton.doClick();
			seriesLoadLabel.setText("VA Load");
		}
		if (vdropModel.getSeriesFeet())
		{
			seriesFeetButton.doClick();
			seriesLengthLabel.setText("Feet");
		}
		else
		{
			seriesMetersButton.doClick();
			seriesLengthLabel.setText("Meters");
		}
		updateResultText();
		updateSegmentText();
		listenersActive = true;
	}

	private class SeriesSegmentPanel extends JPanel
	{

		public SeriesSegmentPanel() {
			super(new MigLayout("filly", "", "[]0[]"));

			JLabel copyWireLabel = new JLabel("Wire Size to Copy");
			JLabel lengthUnitLabel = new JLabel("Length Units");
			JLabel loadUnitLabel = new JLabel("Load Units");

			JLabel seriesSegLabel = new JLabel("Seg");
			JLabel seriesSizeLabel = new JLabel("Size");

			seriesLengthLabel = new JLabel("Feet");
			seriesLoadLabel = new JLabel("Amp Load");

			copyWireBox = new JComboBox(wireSizes);
			copyWireBox.setSelectedItem(WireSize.TWELVE);

			seriesFeetButton = new JRadioButton("Feet", true);
			seriesMetersButton = new JRadioButton("Meters");

			seriesFeetButton.setActionCommand("Feet");
			seriesMetersButton.setActionCommand("Meters");

			seriesLengthGroup = new ButtonGroup();
			seriesLengthGroup.add(seriesFeetButton);
			seriesLengthGroup.add(seriesMetersButton);

			seriesAmpsButton = new JRadioButton("Amps", true);
			seriesVAButton = new JRadioButton("VA");

			seriesLoadGroup = new ButtonGroup();
			seriesLoadGroup.add(seriesAmpsButton);
			seriesLoadGroup.add(seriesVAButton);

			ButtonListener buttonListen = new ButtonListener();

			seriesAmpsButton.addActionListener(buttonListen);
			seriesVAButton.addActionListener(buttonListen);
			seriesFeetButton.addActionListener(buttonListen);
			seriesMetersButton.addActionListener(buttonListen);

			copyWireButton = new JButton("Copy to All");

			copyWireButton.addActionListener(buttonListen);

			segmentInfoPanel = new SegmentInfoPanel();
			scrollSegment = new JScrollPane(segmentInfoPanel);
			scrollSegment
					.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollSegment
					.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

			JPanel selectionsPanel = new JPanel(new MigLayout("insets 0", // top
																			// panel
																			// selections
					"[]15[]12[]"));
			selectionsPanel.add(copyWireLabel);
			selectionsPanel.add(lengthUnitLabel);
			selectionsPanel.add(loadUnitLabel, "wrap");
			selectionsPanel.add(copyWireBox, "growx");
			selectionsPanel.add(seriesFeetButton);
			selectionsPanel.add(seriesAmpsButton, "wrap");
			selectionsPanel.add(copyWireButton, "growx");
			selectionsPanel.add(seriesMetersButton);
			selectionsPanel.add(seriesVAButton, "wrap");

			JPanel labelPanel = new JPanel(new MigLayout("insets 0"));
			labelPanel.add(seriesSegLabel, "gapleft 15");
			labelPanel.add(seriesSizeLabel, "gapleft 15");
			labelPanel.add(seriesLengthLabel, "gapleft 45, w 35!");
			labelPanel.add(seriesLoadLabel, "gapleft 16");

			selectionsPanel.add(labelPanel,
					"span 3, wrap, pushy 200, aligny bottom");

			add(selectionsPanel, "growx, h 115!, wrap, aligny bottom");
			add(scrollSegment,
					"h 155:155:100%, w 235!, pushy 200, span 3, grow");

			setBorder(BorderFactory.createTitledBorder("Segment Info"));
		}
	}

	private class SegmentInfoPanel extends JPanel
	{

		public SegmentInfoPanel() {
			super(new MigLayout(""));
			sizeSpinners = new JSpinner[99];
			loads = new JTextField[99];
			lengths = new JTextField[99];
			loadsInitial = new double[99];
			lengthsInitial = new double[99];

			for (int i = 0; i < 99; ++i)
			{

				JLabel segLabel = new JLabel(String.valueOf(i + 1));
				JSpinner sizeSpinner = makeSpinner();

				JTextField lengthField = new JTextField();
				JTextField loadField = new JTextField();

				lengthField.setHorizontalAlignment(JTextField.RIGHT);
				loadField.setHorizontalAlignment(JTextField.RIGHT);

				sizeSpinners[i] = sizeSpinner;
				loads[i] = loadField;
				lengths[i] = lengthField;

				loadsInitial[i] = 0.0;
				lengthsInitial[i] = 0.0;

				KeyListener keyListen = new KeyListener();

				loadField.addFocusListener(new LoadFocusListener());
				loadField.addActionListener(new LoadActionListener());
				loadField.addKeyListener(keyListen);

				lengthField.addFocusListener(new LengthFocusListener());
				lengthField.addActionListener(new LengthActionListener());
				lengthField.addKeyListener(keyListen);

				sizeSpinner.addChangeListener(new GaugeListener());

				add(segLabel);
				add(sizeSpinner, "w 68!");
				add(lengthField, "w 55!");
				add(loadField, "w 55!, wrap");
			}
		}

		private JSpinner makeSpinner()
		{
			final SpinnerListModel sizeModel = new SpinnerListModel(wireSizes);
			JSpinner sizeSpinner = new JSpinner(sizeModel);
			final JComboBox<WireSize> sizeSpinnerBox = new JComboBox<>(wireSizes);
			sizeSpinnerBox.addItemListener(new ItemListener()
			{

				public void itemStateChanged(ItemEvent e)
				{
					sizeModel.setValue(sizeSpinnerBox.getSelectedItem());
				}
			});

			sizeModel.addChangeListener(new ChangeListener()
			{

				public void stateChanged(ChangeEvent e)
				{
					sizeSpinnerBox.setSelectedItem(sizeModel.getValue());
				}
			});

			sizeSpinner.setEditor(sizeSpinnerBox);
			sizeSpinnerBox.setSelectedItem(WireSize.TWELVE);
			return sizeSpinner;
		}
	}

	private class SeriesResultPanel extends JPanel
	{
		public SeriesResultPanel() {

			super(new MigLayout("fill", "", "[]0[]"));

			JPanel resultInfo = new JPanel(new MigLayout("fill, insets 0"));
			JPanel resultLabels = new JPanel(new MigLayout("insets n n 0 n"));

			JLabel totalLoadArea = new JLabel("Total Amps");
			JLabel segmentVDArea = new JLabel("Seg VD%");
			JLabel cumulativeVDArea = new JLabel("Cum. VD%");

			resultLabels.add(totalLoadArea, "gapleft 16");
			resultLabels.add(segmentVDArea, "gapleft 8");
			resultLabels.add(cumulativeVDArea, "gapleft 8");

			StyledDocument document = new DefaultStyledDocument();
			Style defaultStyle = document.getStyle(StyleContext.DEFAULT_STYLE);
			StyleConstants.setAlignment(defaultStyle,
					StyleConstants.ALIGN_CENTER);

			resultPane = new JTextPane(document);
			resultPane.setEditable(true);
			resultPane.setFont(new Font("Serif", Font.PLAIN, 14));
			resultPane.setText(segment);

			resultInfo.add(resultPane, "h 80!, wrap, growx");
			resultInfo.add(resultLabels, "wrap, pushy 200, aligny bottom");

			calculationResultsPanel = new CalculationResultsPanel();
			scrollResults = new JScrollPane(calculationResultsPanel);
			scrollResults
					.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			JScrollBar segmentBar = scrollSegment.getVerticalScrollBar();
			JScrollBar resultBar = scrollResults.getVerticalScrollBar();
			resultBar.setModel(segmentBar.getModel());

			add(resultInfo, "growx, h 115!, wrap, aligny bottom");
			add(scrollResults, "w 225!, h 155:155:100%, pushy 200, growy");
			setBorder(BorderFactory.createTitledBorder("Results"));
		}
	}

	// panel within ScrollPane
	private class CalculationResultsPanel extends JPanel
	{
		public CalculationResultsPanel() {
			super(new MigLayout("", "", "[]8"));
			totalLoads = new JTextField[99];
			segmentVDs = new JTextField[99];
			cumulativeVDs = new JTextField[99];
			for (int i = 0; i < 99; ++i)
			{

				JLabel segLabel = new JLabel(String.valueOf(i + 1));

				JTextField totalLoadField = new JTextField();
				JTextField segmentVDField = new JTextField();
				JTextField cumulativeVDField = new JTextField();

				totalLoadField.setEditable(true);
				segmentVDField.setEditable(true);
				cumulativeVDField.setEditable(true);

				totalLoadField.setHorizontalAlignment(JTextField.RIGHT);
				segmentVDField.setHorizontalAlignment(JTextField.RIGHT);
				cumulativeVDField.setHorizontalAlignment(JTextField.RIGHT);

				totalLoads[i] = totalLoadField;
				segmentVDs[i] = segmentVDField;
				cumulativeVDs[i] = cumulativeVDField;

				add(segLabel);
				add(totalLoadField, "w 55!");
				add(segmentVDField, "w 55!");
				add(cumulativeVDField, "w 55!, wrap");
			}
		}
	}

	public void updateSeriesResult()
	{
		vdropModel.updateSeries();
		updateResultText();
	}

	private void updateResultText()
	{
		seriesResult = vdropModel.getSeriesResult();
		int i = 0;
		if (seriesResult.maximumIndexForCalculation >= 0)
		{
			resultPane
					.setText("\nThe Total Voltage Drop is\n"
							+ totalFormat
									.format(seriesResult.resultCumulativeVDs[seriesResult.maximumIndexForCalculation]));
		} else
		{
			resultPane.setText("\nEnter\nSegment Info");
		}
		while (i <= seriesResult.maximumIndexForCalculation)
		{
			totalLoads[i].setText(resultFormat
					.format(seriesResult.resultLoads[i]));
			segmentVDs[i].setText(resultFormat
					.format(seriesResult.resultSegmentVDs[i] * 100.0));
			cumulativeVDs[i].setText(resultFormat
					.format(seriesResult.resultCumulativeVDs[i] * 100.0));
			++i;
		}
		for (int j = totalLoads.length - 1; j > seriesResult.maximumIndexForCalculation; --j)
		{
			totalLoads[j].setText("");
			segmentVDs[j].setText("");
			cumulativeVDs[j].setText("");
		}
	}

	private void updateSegmentText()
	{
		loadsInitial = vdropModel.getSeriesLoads();
		lengthsInitial = vdropModel.getSeriesLengths();

		for (int i = 0; i < loadsInitial.length; ++i)
		{
			loads[i].setText(Double.toString(loadsInitial[i]));
			lengths[i].setText(Double.toString(lengthsInitial[i]));
			if (i > seriesResult.maximumIndexForCalculation)
			{
				loads[i].setText("");
				lengths[i].setText("");
			}
			WireSize size = vdropModel.getSeriesSizes()[i];
			((JComboBox) (sizeSpinners[i].getEditor())).setSelectedItem(size);
		}
	}

	private class LoadFocusListener extends FocusAdapter
	{

		@Override
		public void focusLost(FocusEvent e)
		{
			doLoadAction(e);
		}
	}

	private class LoadActionListener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			doLoadAction(e);
		}
	}

	private void doLoadAction(EventObject e)
	{
		if (listenersActive)
		{
			int index = -1;
			double value = 0.0;
			JTextField loadField = (JTextField) e.getSource();
			String text = loadField.getText();

			for (int i = 0; i < loads.length; ++i)
			{
				if (loads[i].equals(loadField))
				{
					index = i;
					break;
				}
			}
			if (loadField.getText().equals("") && loadsInitial[index] != 0.0)
			{
				loadsInitial[index] = 0.0;
				vdropModel.setSeriesLoad(value, index);
				updateSeriesResult();
			} 
			else
			{
				try
				{
					value = Double.parseDouble(text);
				} 
				catch (NumberFormatException e1)
				{
					value = loadsInitial[index];
					
					if (value == 0.0)
						loadField.setText("");
					else
						loadField.setText(String.valueOf(value));
					return;
				}
				if (loadsInitial[index] != value || value == 0.0)
				{
					loadsInitial[index] = value;
					vdropModel.setSeriesLoad(value, index);
					if (value == 0.0)
					{
						loadField.setText("");
					}
					updateSeriesResult();
				}
			}
		}
	}

	private class KeyListener extends KeyAdapter
	{

		@Override
		public void keyTyped(KeyEvent e)
		{
			char c = e.getKeyChar();
			if (((c < '0') || (c > '9'))
					&& (c != KeyEvent.VK_BACK_SPACE && c != '.'))
			{
				e.consume(); // ignore event
			}
		}
	}

	private class LengthFocusListener extends FocusAdapter
	{

		@Override
		public void focusLost(FocusEvent e)
		{
			doLengthAction(e);
		}
	}

	private class LengthActionListener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			doLengthAction(e);
		}
	}

	void doLengthAction(EventObject e)
	{
		if (listenersActive)
		{
			int index = -1;
			double value = 0.0;
			JTextField lengthField = (JTextField) e.getSource();
			String text = lengthField.getText();

			for (int i = 0; i < lengths.length; ++i)
			{
				if (lengths[i].equals(lengthField))
				{
					index = i;
					break;
				}
			}
			if (lengthField.getText().equals("") && lengthsInitial[index] != 0.0)
			{
				lengthsInitial[index] = 0.0;
				vdropModel.setSeriesLength(value, index);
				updateSeriesResult();
			} 
			else
			{
				try
				{
					value = Double.parseDouble(text);
				} catch (NumberFormatException e1)
				{
					value = lengthsInitial[index];
					if (value == 0.0)
						lengthField.setText("");
					else
						lengthField.setText(String.valueOf(value));
					return;
				}
				if (lengthsInitial[index] != value || value == 0.0)
				{
					lengthsInitial[index] = value;
					vdropModel.setSeriesLength(value, index);
					if (value == 0.0)
					{
						lengthsInitial[index] = value;
					}
					updateSeriesResult();
				}
			}
		}
	}

	private class GaugeListener implements ChangeListener
	{

		@Override
		public void stateChanged(ChangeEvent e)
		{
			if (listenersActive)
			{
				int index = -1;
				JSpinner spinner = (JSpinner) e.getSource();
				for (int i = 0; i < sizeSpinners.length; ++i)
				{
					if (sizeSpinners[i].equals(spinner))
					{
						index = i;
						break;
					}
				}
				WireSize wireSize = (WireSize) spinner.getValue();
				vdropModel.setWireSize(wireSize, index);
				updateSeriesResult();
			}
		}
	}

	private class ButtonListener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (listenersActive)
			{
				Object source = e.getSource();
				if (source.equals(seriesAmpsButton)
						|| source.equals(seriesVAButton))
				{
					boolean amps = seriesAmpsButton.isSelected();
					vdropModel.setSeriesLoad(amps);
					updateSeriesResult();
					if (amps)
					{
						seriesLoadLabel.setText("Amp Load");
					} else
					{
						seriesLoadLabel.setText("VA Load");

					}
					return;
				}

				if (source.equals(seriesFeetButton)
						|| source.equals(seriesMetersButton))
				{
					boolean feet = seriesFeetButton.isSelected();
					vdropModel.setSeriesFeet(feet);
					updateSeriesResult();
					if (feet)
					{
						seriesLengthLabel.setText("Feet");
					} else
					{
						seriesLengthLabel.setText("Meters");

					}
					return;
				}

				if (source.equals(copyWireButton))
				{
					WireSize sizeToCopy = (WireSize) copyWireBox.getSelectedItem();
					for (int i = 0; i < sizeSpinners.length; ++i)
					{
						sizeSpinners[i].setValue(sizeToCopy);
					}
				}
			}
		}
	}
}
