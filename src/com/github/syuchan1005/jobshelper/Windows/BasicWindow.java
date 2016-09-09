package com.github.syuchan1005.jobshelper.Windows;

import com.github.syuchan1005.jobshelper.Color;
import lombok.Getter;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import java.text.NumberFormat;

/**
 * Created by syuchan on 2016/04/30.
 */
@Getter
public class BasicWindow {
	private JPanel MainPane;
	private JTextField TitleNameField;
	private JTextField FullNameField;
	private JTextField ShortNameField;
	private JComboBox ChatColorBox;
	private JTextField DescriptionField;
	private JCheckBox MaxLevelCheck;
	private JFormattedTextField MaxLevelField;
	private JCheckBox SlolsCheck;
	private JFormattedTextField SlotsField;
	private JTextField LPEField;
	private JTextField IPEField;
	private JTextField EPEField;
	private JComboBox ChatDisplayComboBox;

	public BasicWindow() {
		TitleNameField.addActionListener(event -> {
			JTextField jTextField = (JTextField) event.getSource();
			JPanel jPanel = (JPanel) ((JPanel) jTextField.getParent()).getParent();
			JTabbedPane jTabbedPane = (JTabbedPane) jPanel.getParent();
			jTabbedPane.setTitleAt(jTabbedPane.indexOfComponent(jPanel), jTextField.getText());
		});
	}

	private void createUIComponents() {
		this.MaxLevelField = new JFormattedTextField(NumberFormat.getNumberInstance());
		this.SlotsField = new JFormattedTextField(NumberFormat.getNumberInstance());
		this.ChatDisplayComboBox = new JComboBox(new String[]{"full", "title", "job", "shortfull", "shorttitle", "shortjob", "none"});
		this.ChatColorBox = new JComboBox<Enum>(Color.values());
	}

	public String toYaml() {
		String yaml = "\t" + TitleNameField.getText() + ":\n";
		yaml += "\t\tfullname: " + FullNameField.getText() + "\n";
		yaml += "\t\tshortname: " + ShortNameField.getText() + "\n";
		yaml += "\t\tdescription: " + DescriptionField.getText() + "\n";
		yaml += "\t\tChatColour: " + ((Color) ChatColorBox.getSelectedItem()).name() + "\n";
		yaml += "\t\tchat-display: " + ChatDisplayComboBox.getSelectedItem() + "\n";
		if (MaxLevelCheck.isSelected()) yaml += "\t\tmax-level: " + MaxLevelField.getText() + "\n";
		if (SlolsCheck.isSelected()) yaml += "\t\tslots: " + SlotsField.getText() + "\n";
		yaml += "\t\tleveling-progression-equation: " + LPEField.getText() + "\n";
		yaml += "\t\tincome-progression-equation: " + IPEField.getText() + "\n";
		yaml += "\t\texperience-progression-equation: " + EPEField.getText() + "\n";
		return yaml;
	}
}
