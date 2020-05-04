package me.sirtyler.kenkuboard;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.jsyn.devices.AudioDeviceManager;

import me.sirtyler.kenkuboard.util.AudioDevice;
import me.sirtyler.kenkuboard.util.KenkuData;
import me.sirtyler.kenkuboard.util.SoundClip;

public class KenkuBoardGUI {

	private JFrame frmKenkuboard;
	private JCheckBox chckbxEnabled;
	private JComboBox comboBox;
	private JTabbedPane tabbedPane;
	private JFileChooser fc = new JFileChooser();
	private FileNameExtensionFilter filter = new FileNameExtensionFilter("Audio Files", "wav");

	/**
	 * Create the application.
	 */
	public KenkuBoardGUI() {
		initialize();
	}
	
	public void show() {
		frmKenkuboard.setVisible(true);
		frmKenkuboard.revalidate();
		frmKenkuboard.repaint();
	}
	
	public void hide() {
		frmKenkuboard.setVisible(false);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmKenkuboard = new JFrame();
		frmKenkuboard.setTitle("KenkuBoard");
		frmKenkuboard.setResizable(false);
		frmKenkuboard.setBounds(100, 100, 880, 680);
		frmKenkuboard.setPreferredSize(new Dimension(880, 680));
		frmKenkuboard.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmKenkuboard.getContentPane().setLayout(new BorderLayout(0, 0));
		
		tabbedPane = new JTabbedPane();
		frmKenkuboard.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		loadTabs();
		
		JPanel panel = new JPanel();
		frmKenkuboard.getContentPane().add(panel, BorderLayout.SOUTH);
		panel.setLayout(new GridLayout(1, 0, 0, 0));
		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					KenkuData.save();
				} catch (IOException | URISyntaxException e1) {
					e1.printStackTrace();
				}
			}
		});
		panel.add(btnSave);
		
		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fc.setFileFilter(filter);
				int val = fc.showOpenDialog(frmKenkuboard);
				if(val == JFileChooser.APPROVE_OPTION) {
					File f = fc.getSelectedFile();
					addSoundClip(f.getAbsolutePath(), f.getName(), tabbedPane.getTitleAt(tabbedPane.getSelectedIndex()), (JPanel) tabbedPane.getSelectedComponent());
				}
			}
		});
		panel.add(btnAdd);
		
		JButton btnRemove = new JButton("Remove");
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fc.setFileFilter(filter);
				int val = fc.showOpenDialog(frmKenkuboard);
				if(val == JFileChooser.APPROVE_OPTION) {
					File f = fc.getSelectedFile();
					removeSoundClip(f.getName(), tabbedPane.getTitleAt(tabbedPane.getSelectedIndex()), (JPanel) tabbedPane.getSelectedComponent());
				}
			}
		});
		panel.add(btnRemove);
		
		JPanel panel_1 = new JPanel();
		frmKenkuboard.getContentPane().add(panel_1, BorderLayout.NORTH);
		
		chckbxEnabled = new JCheckBox("ENABLED");
		chckbxEnabled.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					if(comboBox.getSelectedIndex() != -1) {
						comboBox.setEnabled(false);
						KenkuBoard.makeStream(((AudioDevice) comboBox.getSelectedItem()).getID());
					} else chckbxEnabled.setSelected(false);
				}
				else {
					System.out.println("DISABLE");
					comboBox.setEnabled(true);
				}
			}
		});
		panel_1.add(chckbxEnabled);
		
		DeviceModel model = new DeviceModel();
		comboBox = new JComboBox(model);
		panel_1.add(comboBox);
		
		JLabel lblSPACE = new JLabel("                                                              ");
		panel_1.add(lblSPACE);
		
		JButton btnAddTab = new JButton("Add Tab");
		btnAddTab.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String title = JOptionPane.showInputDialog(frmKenkuboard, "Enter Tab Name:");
				addTab(title);
				KenkuData.addTab(title);
			}
		});
		btnAddTab.setHorizontalAlignment(SwingConstants.LEADING);
		panel_1.add(btnAddTab);
		
		JButton btnRemoveTab = new JButton("Remove Tab");
		btnRemoveTab.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String title = tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());
				removeTab(title);
				KenkuData.removeTab(title);
			}
		});
		btnRemoveTab.setHorizontalAlignment(SwingConstants.LEADING);
		panel_1.add(btnRemoveTab);
		
		frmKenkuboard.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				try {
					KenkuData.save();
				} catch (IOException | URISyntaxException e) {
					e.printStackTrace();
					System.exit(-1);
				}
				System.exit(0);
			}
		});
	}
	
	public void addTab(String title) {
		JPanel panel_Sounds = new JPanel();
		tabbedPane.addTab(title, panel_Sounds);
		panel_Sounds.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		KenkuData.addTab(title);
	}
	
	public void removeTab(String title) {
		for(int i = 0; i < tabbedPane.getTabCount(); i++) {
			if(title.equalsIgnoreCase(tabbedPane.getTitleAt(i))) {
				tabbedPane.remove(i);
				KenkuData.removeTab(title);
				return;
			}
		}
	}
	
	public void loadTabs() {
		HashMap<String, ArrayList<SoundClip>> map = KenkuData.getTabs();
		if(map.size() > 0) {
			for(Entry<String, ArrayList<SoundClip>> e : map.entrySet()) {
				JPanel panel = new JPanel();
				for(SoundClip clip : e.getValue()) {
					JSoundClip sc = new JSoundClip(clip);
					panel.add(sc);
				}
				tabbedPane.addTab(e.getKey(), panel);
				panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			}
		} else {
			addTab("DEFAULT");
		}
		frmKenkuboard.revalidate();
		frmKenkuboard.repaint();
	}
	
	public void addSoundClip(String path, String name, String tab, JPanel panel) {
		SoundClip clip = new SoundClip(path, name);
		JSoundClip sc = new JSoundClip(clip);
		KenkuData.addSoundClip(tab, clip);
		panel.add(sc);
		frmKenkuboard.revalidate();
		frmKenkuboard.repaint();
	}
	
	public void removeSoundClip(String name, String tab, JPanel panel) {
		SoundClip clip = KenkuData.getSoundClip(tab, name);
		KenkuData.removeSoundClip(tab, clip);
		int x = -1;
		for(int i = 0; i < panel.getComponentCount(); i++) {
			JSoundClip sc = (JSoundClip) panel.getComponent(i);
			if(name.equalsIgnoreCase(sc.name)) x = i;
		}
		if(x > -1) panel.remove(x);
		frmKenkuboard.revalidate();
		frmKenkuboard.repaint();

	}
	
	public void setDevices(AudioDeviceManager manager, int[] devices) {
		DeviceModel model = new DeviceModel();
			for(int i = 0; i < devices.length; i++) {
				model.addDevice(manager.getDeviceName(devices[i]), devices[i]);
			}
		comboBox.setModel(model);
		model.setSelectedItem(model.getLastDevice());
		frmKenkuboard.revalidate();
		frmKenkuboard.repaint();
	}
	
	private class DeviceModel extends AbstractListModel implements ComboBoxModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		ArrayList<AudioDevice> devices = new ArrayList<AudioDevice>();
		AudioDevice selection = null;
		
		public DeviceModel() {
			super();
			clear();
		}
		
		private void clear() {
			devices.clear();
			devices.add(new AudioDevice("DEVICE", -1));
		}
		
		private void addDevice(String name, int device_id) {
			devices.add(new AudioDevice(name, device_id));
		}
		
		private AudioDevice getLastDevice() {
			int x = KenkuData.getAudioDevice();
			if(x > 0) {
				for(AudioDevice d : devices) {
					if(d.getID() == x) return d;
				}
			}
			return null;
		}

		@Override
		public int getSize() {
			return devices.size();
		}

		@Override
		public Object getElementAt(int index) {
			return devices.get(index);
		}

		@Override
		public void setSelectedItem(Object anItem) {
			selection = (AudioDevice) anItem;
		}

		@Override
		public Object getSelectedItem() {
			return selection;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			for(AudioDevice ad : devices) {
				sb.append(ad.toString()+",");
			}
			sb.deleteCharAt(sb.length()-1);
			return sb.toString();
		}
		
	}
	
	class JSoundClip extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String sound_file = "";
		private String name = "";

		public JSoundClip(SoundClip clip) {
			setLayout(new BorderLayout(0, 0));
			
			JTextField lblText = new JTextField(clip.name);
			lblText.setHorizontalAlignment(SwingConstants.CENTER);
			lblText.setEditable(false);
			add(lblText);
			
			JButton btnPlay = new JButton("Play");
			btnPlay.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(chckbxEnabled.isSelected())
						KenkuBoard.player.play(sound_file);
				}
			});
			add(btnPlay, BorderLayout.SOUTH);
			
			sound_file = clip.path;
			name = clip.name;
		}
	}
	
}
