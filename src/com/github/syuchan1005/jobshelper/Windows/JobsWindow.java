package com.github.syuchan1005.jobshelper.Windows;

import com.github.syuchan1005.jobshelper.Color;
import com.github.syuchan1005.jobshelper.Entity;
import com.github.syuchan1005.jobshelper.Material;
import lombok.Data;
import lombok.Getter;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by syuchan on 2016/04/30.
 */
@Data
public class JobsWindow {
	private JPanel MainPane;
	private JPanel BasicPane;
	private JTabbedPane ListTabPane;
	private List<ContentWindow> contentWindowList = new ArrayList<>();
	private BasicWindow basicWindow;

	@Getter
	private static Object[][] EventList = {{"Break", Material.class}, {"TNTBreak", Material.class},
			{"Place", Material.class}, {"Kill", Entity.class}, {"MMKill", null},
			{"Fish", Material.class}, {"Craft", Material.class}, {"Smelt", Material.class},
			{"Brew", Material.class}, {"Enchant", Material.class}, {"Repair", Material.class},
			{"Breed", Entity.class}, {"Tame", Entity.class}, {"Dye", Material.class}, {"Shear", Color.class},
			{"Milk", Entity.class}, {"Explore", null}, {"Eat", Material.class}, {"custom-kill", null}};

	public JobsWindow() {
		this.setContentWindow();
	}

	private void setContentWindow() {
		for (int i = 0; i < EventList.length; i++) {
			Class clazz = (Class) EventList[i][1];
			contentWindowList.add(new ContentWindow((String) EventList[i][0], clazz, "income", "experience"));
			this.ListTabPane.addTab((String) EventList[i][0], contentWindowList.get(i).getMainPane());
		}
	}

	private void createUIComponents() {
		this.basicWindow = new BasicWindow();
		this.BasicPane = basicWindow.getMainPane();
	}

	public String toYaml() {
		String yaml = basicWindow.toYaml();
		for (ContentWindow window : contentWindowList) {
			yaml += window.toYaml();
		}
		return yaml;
	}
}
