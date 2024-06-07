package view;

import wires.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import net.miginfocom.swing.MigLayout;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import java.text.*;

public class AmpPanel extends JPanel  {
	// Values for the fields
	private double amps = 20;

	// Labels to identify the fields

	private JLabel ampLabel;
	private JLabel wireLabel;
	private JLabel eqGroundLabel;
	private JLabel GECLabel;

	// Strings for the labels
	private static String ampString = "Load current: ";
	private static String wireString = "Wire Required: ";
	private static String eqGroundString = "Equipment Ground Required: ";
	private static String GECString = "GEC Required: ";
	// Fields for data entry
	private JFormattedTextField ampField;
	private JTextField wireField;
	private JTextField eqGroundField;
	private JTextField GECField;
	
	private JComboBox wireType;
	private JCheckBox codeOption;
	
	

	// Formats to format and parse numbers
	private NumberFormat ampFormat;

	// Objects with the wire sizes
	private Wire wireRequired;
	

	public AmpPanel() {
		super(new MigLayout("insets 12", "[][]", "[][][][]17[]"));
		setUpFormats();
		
		
		
		
		String[] wireStrings = {"Copper", "Aluminum"};
		wireType = new JComboBox(wireStrings); 
		wireType.setSelectedIndex(0);
		
		wireType.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				if (wireType.getSelectedIndex() == 0) {
					amps = ((Number) ampField.getValue()).doubleValue();				
					wireRequired = new CopperDegree75(amps);
					String wire = wireRequired.getWireSize();
					String eqGround = wireRequired.getEqGroundSize();
					wireField.setText(new String(wire));
					eqGroundField.setText(new String(eqGround));
				} else {
					amps = ((Number) ampField.getValue()).doubleValue();				
					wireRequired = new AluminumDegree75(amps);
					String wire = wireRequired.getWireSize();
					String eqGround = wireRequired.getEqGroundSize();
					wireField.setText(new String(wire));
					eqGroundField.setText(new String(eqGround));
				}
			}
		});
		
		
		codeOption = new JCheckBox("Allow NEC 240.4(B)");
		//TO do: add listener
		
		
		
		
		
		
		

		wireRequired = new CopperDegree75(amps);
		String wire = wireRequired.getWireSize();
		String eqGround = wireRequired.getEqGroundSize();
		String GEC = wireRequired.getGECSize();

		// Create the labels.
		ampLabel = new JLabel(ampString);
		wireLabel = new JLabel(wireString);
		eqGroundLabel = new JLabel(eqGroundString);
		GECLabel = new JLabel(GECString);

		// Create the text fields and set them up.
		ampField = new JFormattedTextField(ampFormat);
		ampField.setValue(new Double(amps));
		ampField.setColumns(10);
		ampField.addPropertyChangeListener("value", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				if (wireType.getSelectedIndex() == 0) {
					amps = ((Number) ampField.getValue()).doubleValue();				
					wireRequired = new CopperDegree75(amps);
					String wire = wireRequired.getWireSize();
					String eqGround = wireRequired.getEqGroundSize();
					String GEC = wireRequired.getGECSize();
					wireField.setText(new String(wire));
					eqGroundField.setText(new String(eqGround));
					GECField.setText(new String(GEC));
				} else {
					amps = ((Number) ampField.getValue()).doubleValue();				
					wireRequired = new AluminumDegree75(amps);
					String wire = wireRequired.getWireSize();
					String eqGround = wireRequired.getEqGroundSize();
					String GEC = wireRequired.getGECSize();
					wireField.setText(new String(wire));
					eqGroundField.setText(new String(eqGround));
					GECField.setText(new String(GEC));
				}
			}
		});
		

		wireField = new JTextField();
		wireField.setText(new String(wire));
		wireField.setColumns(17);
		wireField.setEditable(true);
				
		eqGroundField = new JTextField();
		eqGroundField.setText(new String(eqGround));
		eqGroundField.setColumns(17);
		eqGroundField.setEditable(true);
		
		GECField = new JTextField();
		GECField.setText(new String(GEC));
		GECField.setColumns(17);
		GECField.setEditable(true);
		
		
		// Tell accessibility tools about label/textfield pairs.
		ampLabel.setLabelFor(ampField);
		wireLabel.setLabelFor(wireField);
		eqGroundLabel.setLabelFor(eqGroundField);
		GECLabel.setLabelFor(GECField);

		
		add(ampLabel);
		add(ampField, "w :150:150, grow, wrap");
		
		add(wireLabel);
		add(wireField, "w :150:150, wrap");
		
		add(eqGroundLabel);
		add(eqGroundField, "w :150:150, wrap");
		
		add(GECLabel);
		add(GECField, "w :150:150, wrap");
		
		add(wireType, "w 150!");
		add(codeOption, "align center");
		

		// Put the panels in this panel, labels on left,
		// text fields on right.
		
		
		
	}

	private void setUpFormats() {
		ampFormat = NumberFormat.getNumberInstance();
		ampFormat.setGroupingUsed(true);
	}
}

	