package com.plancrawler.controller;


import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class CalibrationDialog {

	public static double calibrate(JComponent component, double measVal, String measText) {
		// opens a window and allows user to enter in the expected length of the
		// measurement

		JLabel mLabel = new JLabel("measured value: " + measText);

		JLabel instruction = new JLabel("Enter in correct measurement value:");

		JTextField feetField = new JTextField("0");
		JLabel feet = new JLabel("feet and ");
		JTextField inchField = new JTextField("0");
		JLabel inch1 = new JLabel(" + ");
		JTextField fracField = new JTextField("0/4");
		JLabel inch2 = new JLabel(" inches");

		Box box = Box.createHorizontalBox();
		box.add(feetField);
		box.add(feet);
		box.add(inchField);
		box.add(inch1);
		box.add(fracField);
		box.add(inch2);

		JComponent[] inputs = new JComponent[] { mLabel, new JLabel(" "), instruction, box, new JLabel(" ") };
		JOptionPane.showMessageDialog(component, inputs, "Enter Calibrated Measurement", JOptionPane.QUESTION_MESSAGE);
		
		return parseAnswer(feetField.getText(), inchField.getText(), fracField.getText()) / measVal;
	}

	private static double parseAnswer(String feet, String inches, String frac) {
		double answer = 0;

		answer += (double) Integer.parseInt(feet);
		System.out.println("feet: "+answer);
		answer += (double) Integer.parseInt(inches) / 12d;
		System.out.println("inches: "+ Integer.parseInt(inches));
		String[] tokens = frac.split("/");
		double num = (double) Integer.parseInt(tokens[0]);
		double denom = (double) Integer.parseInt(tokens[1]);
		System.out.println("num: "+ num + " / denom: "+ denom);
		answer += (num / denom) / 12d;

		return answer;
	}

}

