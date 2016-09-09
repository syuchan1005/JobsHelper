package com.github.syuchan1005.jobshelper.Windows;

import com.github.syuchan1005.jobshelper.Color;
import com.github.syuchan1005.jobshelper.Material;
import org.yaml.snakeyaml.Yaml;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by syuchan on 2016/05/04.
 */
public class MainWindow extends JFrame {
	private JTabbedPane JobTabList;
	private JPanel MainPane;
	private JTextArea ResultArea;
	private JButton CreateButton;
	private JButton CopyButton;
	private JButton loadButton;
	private JButton saveButton;
	private JButton AddTabButton;
	private JButton RemoveTabButton;
	private JPopupMenu jPopupMenu;
	private JToolBar ToolBar;
	private JPanel ResultPane;
	private static List<JobsWindow> jobsWindowList = new ArrayList<>();
	private static JFileChooser jFileChooser;
	private static Yaml yaml = new Yaml();


	public MainWindow() {
		this.setFrameData();
		this.ResultArea.setTabSize(2);
		this.setButtonListener();
		this.setToolBar();
		addJobTab(null);
		this.setLookAndFeel(this);
		setEvent();
		this.setVisible(true);
	}

	private void setFrameData() {
		this.setTitle("JobsHelper");
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setContentPane(MainPane);
		this.setSize(700, 750);
	}

	private boolean heightFlag = false;

	private void setEvent() {
		JobTabList.addMouseMotionListener(
				new MouseAdapter() {

					@Override
					public void mouseMoved(MouseEvent e) {
						int y = e.getY();
						int w = JobTabList.getHeight() - 5;
						heightFlag = false;
						e.getComponent().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						if (y > w) {
							e.getComponent().setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));
							heightFlag = true;
						}
					}

					@Override
					public void mouseDragged(MouseEvent e) {
						if (heightFlag) {
							int y = e.getY();
							JobTabList.setPreferredSize(new Dimension(JobTabList.getWidth(), y));
							ResultPane.setPreferredSize(new Dimension(ResultPane.getWidth(), getHeight() - ToolBar.getHeight() - y));
							JobTabList.revalidate();
							ResultPane.revalidate();
							repaint();
							revalidate();
						}
					}
				}
		);
	}

	private void setButtonListener() {
		CreateButton.addActionListener(event -> {
			this.ResultArea.setText(toYaml());
		});
		CopyButton.addActionListener(event -> {
			copyToClipboad(this.ResultArea.getText());
		});
		AddTabButton.addActionListener(event -> {
			addJobTab(null);
		});
		RemoveTabButton.addActionListener(event -> {
			JobTabList.remove(JobTabList.getSelectedIndex());
			if (jobsWindowList.size() == 1) {
				jobsWindowList.clear();
			} else {
				jobsWindowList.remove(JobTabList.getSelectedIndex());
			}
		});
		loadButton.addActionListener(event -> {
			if (jFileChooser == null) jFileChooser = new JFileChooser();
			if (jFileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
			LinkedHashMap<String, Map<String, Object>> jobs = ((LinkedHashMap<String, LinkedHashMap<String, Map<String, Object>>>) yaml.load(getText(jFileChooser.getSelectedFile()).replaceAll("\\t", "    "))).get("Jobs");
			for (Map.Entry<String, Map<String, Object>> jobEntry : jobs.entrySet()) {
				JobsWindow jobsWindow = new JobsWindow();
				/* basicWindow */
				{
					BasicWindow basic = jobsWindow.getBasicWindow();
					basic.getTitleNameField().setText(jobEntry.getKey());
					basic.getFullNameField().setText((String) jobEntry.getValue().get("fullname"));
					basic.getShortNameField().setText((String) jobEntry.getValue().get("shortname"));
					basic.getDescriptionField().setText((String) jobEntry.getValue().get("description"));
					basic.getChatColorBox().setSelectedItem(Color.valueOf((String) jobEntry.getValue().get("ChatColour")));
					Object max = jobEntry.getValue().get("max-level");
					basic.getMaxLevelCheck().setSelected(max != null);
					basic.getMaxLevelField().setText(max == null ? null : max.toString());
					Object slots = jobEntry.getValue().get("slots");
					basic.getSlolsCheck().setSelected(slots != null);
					basic.getSlotsField().setText(slots == null ? null : slots.toString());
					basic.getLPEField().setText((String) jobEntry.getValue().get("leveling-progression-equation"));
					basic.getIPEField().setText((String) jobEntry.getValue().get("income-progression-equation"));
					basic.getEPEField().setText((String) jobEntry.getValue().get("experience-progression-equation"));
				}
				/* ContentWindow */
				{
					List<ContentWindow> contentWindowList = jobsWindow.getContentWindowList();
					for (Map.Entry<String, Object> contentEntry : jobEntry.getValue().entrySet()) {
						if (!(contentEntry.getValue() instanceof Map)) continue;
						contentWindowList.stream().filter(win -> win.getTitle().equals(contentEntry.getKey())).findFirst().ifPresent(window -> {
							for (Map.Entry<String, Map<String, Map<String, String>>> entry : ((Map<String, Map<String, Map<String, String>>>) contentEntry.getValue()).entrySet()) {
								if (window.getAnEnum() == Material.class) {
									if (entry.getKey().indexOf("-") != -1) {
										String[] split = entry.getKey().split("-");
										window.getTableModel().addRow(new Object[]{split[0], split[1], entry.getValue().get("income"), entry.getValue().get("experience")});
									} else {
										window.getTableModel().addRow(new Object[]{entry.getKey(), 0, entry.getValue().get("income"), entry.getValue().get("experience")});
									}
								} else {
									window.getTableModel().addRow(new Object[]{entry.getKey(), entry.getValue().get("income"), entry.getValue().get("experience")});
								}
							}
						});
					}
				}
				addJobTab(jobsWindow);
			}
		});
		saveButton.addActionListener(event -> {
			if (jFileChooser == null) jFileChooser = new JFileChooser();
			if (jFileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
			File file = jFileChooser.getSelectedFile();
			try {
				file.createNewFile();
				PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
				pw.println(toYaml().replaceAll("\\t", "    "));
				pw.close();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, e.getMessage());
			}
		});
	}

	private void setToolBar() {
		JPopupMenu pop = new JPopupMenu();
		JMenuItem item = new JMenuItem("Test");
		pop.add(item);
		this.ToolBar.add(pop);
	}

	public void addJobTab(JobsWindow jobsWindow) {
		if (jobsWindow == null) jobsWindow = new JobsWindow();
		jobsWindowList.add(jobsWindow);
		String name = jobsWindow.getBasicWindow().getTitleNameField().getText();
		if (name.length() == 0) name = "Untitled";
		JobTabList.addTab(name, jobsWindow.getMainPane());
	}

	public static String getText(File file) {
		try {
			byte[] fileContentBytes = Files.readAllBytes(Paths.get(file.toString()));
			return new String(fileContentBytes, StandardCharsets.UTF_8);
		} catch (IOException e) {
			return "";
		}
	}

	public static void copyToClipboad(String select) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection selection = new StringSelection(select);
		clipboard.setContents(selection, selection);
	}

	public static void setLookAndFeel(JFrame jFrame) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ReflectiveOperationException | UnsupportedLookAndFeelException e) {
			JOptionPane.showMessageDialog(jFrame, e.getMessage());
		}
		SwingUtilities.updateComponentTreeUI(jFrame);
	}

	public String toYaml() {
		String yaml = "Jobs:\n";
		for (JobsWindow jobsWindow : jobsWindowList) {
			yaml += jobsWindow.toYaml();
		}
		return yaml;
	}

}
