package com.github.syuchan1005.jobshelper.Windows;

import com.github.syuchan1005.jobshelper.Material;
import lombok.Data;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by syuchan on 2016/05/04.
 */
@Data
public class ContentWindow {
	private JPanel MainPane;
	private DefaultTableModel tableModel;
	private JTable ContentTable;
	private JLabel NameLabel;
	private JButton addButton;
	private JButton removeButton;

	private String Title;
	private Class anEnum;

	public ContentWindow(String Title, Class Enum, String... TableColumn) {
		this.anEnum = Enum;
		List<String> strlist = new ArrayList<>();
		String name;
		if (Enum == null) {
			strlist.add("Name");
		} else {
			String[] strs = Enum.toString().split("\\.");
			strlist.add(strs[strs.length - 1]);
			if (anEnum == Material.class) strlist.add("meta");
		}
		strlist.addAll(Arrays.asList(TableColumn));
		this.setComponentsData(Title, strlist.toArray(new String[0]));
		this.ContentTable.getTableHeader().setReorderingAllowed(false);
		this.setButtonListener();
	}

	private void setButtonListener() {
		addButton.addActionListener(event -> {
			Object[] objects;
			if (anEnum == Material.class) {
				objects = new Object[]{null, 0, 0, 0};
			} else {
				objects = new Object[]{null, 0, 0};
			}
			this.tableModel.addRow(objects);
		});
		removeButton.addActionListener(event -> {
			int[] rows = this.ContentTable.getSelectedRows();
			for (int i = rows.length - 1; i >= 0; i--) {
				this.tableModel.removeRow(rows[i]);
			}
		});
	}

	private void setComponentsData(String Title, String... TableColumn) {
		this.Title = Title;
		this.NameLabel.setText(this.Title);
		this.tableModel = new DefaultTableModel(null, TableColumn);
		this.ContentTable.setModel(this.tableModel);
		JComboBox comboBox = new JComboBox();
		if (this.anEnum != null) {
			Arrays.stream(this.anEnum.getEnumConstants()).map(obj -> obj.toString()).forEach(str -> comboBox.addItem(str));
			this.ContentTable.getColumn(TableColumn[0]).setCellEditor(new DefaultCellEditor(comboBox));
			if (this.anEnum == Material.class) {
				ContentTable.getColumn("Material").setPreferredWidth(106);
				ContentTable.getColumn("meta").setPreferredWidth(15);
				ContentTable.getColumn("income").setPreferredWidth(150);
				ContentTable.getColumn("experience").setPreferredWidth(150);
			}
		}
	}

	public String toYaml() {
		if (this.tableModel.getRowCount() == 0) return "";
		String yaml = "\t\t" + this.Title + ":\n";
		for (int i = 0; i < this.tableModel.getRowCount(); i++) {
			int column = 0;
			Object o = this.tableModel.getValueAt(i, column++);
			yaml += "\t\t\t" + ((o == null) ? "UNKNOW" : o);
			if (anEnum == Material.class) {
				column++;
				if (!this.tableModel.getValueAt(i, column).equals(0))
					yaml += "-" + this.tableModel.getValueAt(i, column);
			}
			yaml += ":\n";
			yaml += "\t\t\t\tincome: " + this.tableModel.getValueAt(i, column++) + "\n";
			yaml += "\t\t\t\texperience: " + this.tableModel.getValueAt(i, column++) + "\n";
		}
		return yaml;
	}
}
