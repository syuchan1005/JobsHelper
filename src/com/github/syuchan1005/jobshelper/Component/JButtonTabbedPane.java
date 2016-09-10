package com.github.syuchan1005.jobshelper.Component;

import com.sun.istack.internal.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by syuchan on 2016/09/10.
 */
public class JButtonTabbedPane extends JTabbedPane {
	private Icon closeIcon;
	private Dimension buttonSize;
	private JLabel openTab;
	private JPanel openPanel;
	private OpenListener openListener;
	private CloseListener closeListener;

	public JButtonTabbedPane(@NotNull Icon closeIcon, OpenListener openListener, CloseListener closeListener) {
		super();
		this.closeIcon = closeIcon;
		this.buttonSize = new Dimension(closeIcon.getIconWidth(), closeIcon.getIconHeight());
		this.openListener = openListener;
		this.closeListener = closeListener;
		this.addOpenTab();
	}

	public void addOpenTab() {
		if (openPanel == null) {
			openPanel = new JPanel();
			openPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			openPanel.add(new JLabel("↑ Click it!"));
		}
		if (openTab == null) {
			openTab = new JLabel("  ");
			openTab.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (openListener != null) openListener.onClick();
				}
			});
		}
		super.addTab("", openPanel);
		setTabComponentAt(getTabCount() - 1, openTab);
	}

	@Override
	public void addTab(String title, Component component) {
		super.addTab(null, component);
		setTabComponentAt(getTabCount() - 1, createTab(title, component));
		this.remove(getTabCount() - 2);
		this.addOpenTab();
	}

	@Override
	public void setTitleAt(int index, String title) {
		setTabComponentAt(index, createTab(title, this.getComponentAt(index)));
	}

	protected JPanel createTab(String title, Component component) {
		JPanel tab = new JPanel(new BorderLayout());
		tab.setOpaque(false);
		JLabel label = new JLabel(title);
		label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 4));
		JLabel button = new JLabel(closeIcon);
		button.setPreferredSize(buttonSize);
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (closeListener != null) closeListener.onClick(component);
			}
		});
		tab.add(label, BorderLayout.WEST);
		tab.add(button, BorderLayout.EAST);
		tab.setBorder(BorderFactory.createEmptyBorder(2, 1, 1, 1));
		return tab;
	}

	public interface OpenListener {
		void onClick();
	}

	public interface CloseListener {
		void onClick(Component component);
	}
}
