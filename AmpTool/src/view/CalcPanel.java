package view;

import java.awt.*;
import java.text.DecimalFormat;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import constants.*;

import net.miginfocom.swing.MigLayout;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import java.text.*;

public class CalcPanel extends JPanel {
	private JTextField answerField;
	private JButton[] numberButtons;
	private ButtonListener buttonListener;
	private JButton decimalButton;
	private JButton plusMinusButton;
	private JButton divideButton;
	private JButton multiplyButton;
	private JButton minusButton;
	private JButton plusButton;
	private JButton squareRootButton;
	private JButton percentButton;
	private JButton oneXButton;
	private JButton equalsButton;
	private JButton MCButton;
	private JButton MRButton;
	private JButton MSButton;
	private JButton MPlusButton;
	private JButton MMinusButton;
	private JButton backButton;
	private JButton CEButton;
	private JButton CButton;
	

	public CalcPanel() {
		super(new MigLayout("insets 15"));
		answerField = new JTextField("0");
		answerField.setEditable(true);
		answerField.setHorizontalAlignment(JTextField.RIGHT);
		answerField.setBackground(Color.WHITE);
		Font answerFont = new Font("Answer", Font.PLAIN, 20);
		answerField.setFont(answerFont);
		
		
		buttonListener = new ButtonListener();
		numberButtons = makeNumberButtons();
		
		decimalButton = new JButton(".");
		plusMinusButton = new JButton("\u00B1");
		divideButton = new JButton("/");
		multiplyButton = new JButton("*");
		minusButton = new JButton("-");
		plusButton = new JButton("+");
		squareRootButton = new JButton("\u221A");
		percentButton = new JButton("%");
		oneXButton = new JButton("1/x");
		equalsButton = new JButton("=");
		MCButton = new JButton("MC");
		MRButton = new JButton("MR");
		MSButton = new JButton("MS");
		MPlusButton = new JButton("M+");
		MMinusButton = new JButton("M-");
		backButton = new JButton("\u2190");
		CEButton = new JButton("CE");
		CButton = new JButton("C");
		JButton[] operatorButtons = {decimalButton, plusMinusButton, divideButton,
				multiplyButton, minusButton, plusButton, squareRootButton,
				percentButton, oneXButton, equalsButton, MCButton, MRButton,
				MSButton, MPlusButton, MMinusButton, backButton, CEButton,
				CButton};
		
		for(JButton button : operatorButtons) {
			button.setFocusable(true);	
			button.setMargin(new Insets(5,5,5,5));
		}
		for(JButton button : numberButtons) {
			button.setFocusable(true);	
			button.setMargin(new Insets(5,5,5,5));
			
		}
		
		add(answerField, "gaptop 5, spanx 5, wrap 10, grow");
		add(MCButton, "h 32!, w 37!");
		add(MRButton, "h 32!, w 37!");
		add(MSButton, "h 32!, w 37!");
		add(MPlusButton, "h 32!, w 37!");
		add(MMinusButton, "h 32!, w 37!, wrap");
		add(backButton, "h 32!, w 37!");
		add(CEButton, "h 32!, w 37!");
		add(CButton, "h 32!, w 37!");
		add(plusMinusButton, "h 32!, w 37!");
		add(squareRootButton, "h 32!, w 37!, wrap");
		add(numberButtons[7], "h 32!, w 37!");
		add(numberButtons[8], "h 32!, w 37!");
		add(numberButtons[9], "h 32!, w 37!");
		add(divideButton, "h 32!, w 37!");
		add(percentButton, "h 32!, w 37!, wrap");
		add(numberButtons[4], "h 32!, w 37!");
		add(numberButtons[5], "h 32!, w 37!");
		add(numberButtons[6], "h 32!, w 37!");
		add(multiplyButton, "h 32!, w 37!");
		add(oneXButton, "h 32!, w 37!, wrap");
		add(numberButtons[1], "h 32!, w 37!");
		add(numberButtons[2], "h 32!, w 37!");
		add(numberButtons[3], "h 32!, w 37!");
		add(minusButton, "h 32!, w 37!");
		add(equalsButton, "w 37!, spany 2, grow, wrap");
		add(numberButtons[0], "h 32!, span 2, grow x");
		add(decimalButton, "h 32!, w 37!");
		add(plusButton, "h 32!, w 37!");
		
	}
	private JButton[] makeNumberButtons() {
		numberButtons = new JButton[10];
		for (int i = 0; i < numberButtons.length; ++i) {
			numberButtons[i] = new JButton(Integer.toString(i));
			numberButtons[i].addActionListener(buttonListener);
		}
		return numberButtons;
	}
	private class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			
		}
		
	}
}
