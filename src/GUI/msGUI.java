package GUI;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.EventQueue;
import java.io.File;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

import monitor.FileMonitor;
import tools.AnalysisType;
import tools.MachineID;

import javax.swing.JScrollPane;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @author liuxing
 * @email liuxing2@genomics.cn
 * @date 2018_10_30
 *
 */

public class msGUI {
	
	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private JFrame frame;
	private JLabel locallabel;
	private JLabel remotelabel;
	private JLabel thirdlabel;
	private JLabel samplelabel;
	private JLabel machinelabel;
	private JLabel analysislabel;
	private JLabel refreshlabel;
	private JTextField localtxt;
	private JTextField remotetxt;
	private JTextField thirdtxt;
	private JTextField sampletxt;
	private JTextField refreshtxt;
	private JButton localbrowse;
	private JButton remotebrowse;
	private JButton thirdbrowse;
	private JButton samplebrowse;
	private JButton runbutton;
	private JButton stopbutton;
	private JScrollPane scrollPane;
	private JTextPane logtxt;
	private JComboBox<String> machinebox;
	private JComboBox<String> analysisbox;
	private static String[] analysislist = {"Protein", "Metabolite",  "Small_molecule"};
	private static String[] machinelist = {"4500", "5500", "5600", "6500", "GCMS", "G2XS", "ICP", "LUMOS", "MALDI", "QE", "QEHF", "ORBI", "TQS", "TQSALTIS", "TQS9000"};
	
	private FileMonitor monitor;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable(){
			public void run() {
				try {
					msGUI window = new msGUI();
					window.GUIConstruct();
					//window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public msGUI() {
		
	}
	
	public void GUIConstruct(){
		
		frame = new JFrame("MS DATA Transfer");
		frame.setBounds(450, 250, 800, 600);
		frame.getContentPane().setBackground(SystemColor.inactiveCaptionBorder);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		remotelabel = new JLabel("Remote Path");
		remotelabel.setFont(new Font("Segoe UI Light", Font.BOLD, 16));
		
		remotetxt = new JTextField();
		remotetxt.setForeground(Color.LIGHT_GRAY);
		remotetxt.setFont(new Font("Tahoma", Font.PLAIN, 14));
		//remotetxt.setText("E:\\remote\\MSData\\Machine_number\\year\\season\\");
		remotetxt.setText("E:\\BGI_work\\luhuifang\\MSTestData\\remote\\5600-1\\2018\\season4");
		remotetxt.setColumns(20);
		remotetxt.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				remotetxt.setForeground(Color.BLACK);
			}
		});
		
		locallabel = new JLabel("Local Path");
		locallabel.setFont(new Font("Segoe UI Light", Font.BOLD, 16));
		
		localtxt = new JTextField();
		localtxt.setForeground(Color.LIGHT_GRAY);
		localtxt.setFont(new Font("Tahoma", Font.PLAIN, 14));
		//localtxt.setText("E:\\local\\MSData\\");
		localtxt.setText("E:\\BGI_work\\luhuifang\\MSTestData\\local\\5600-1");
		localtxt.setColumns(10);
		localtxt.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				localtxt.setForeground(Color.BLACK);
			}
		});
		
		thirdlabel = new JLabel("Processing Path");
		thirdlabel.setFont(new Font("Segoe UI Light", Font.BOLD, 16));
		
		thirdtxt = new JTextField();
		thirdtxt.setForeground(Color.LIGHT_GRAY);
		thirdtxt.setFont(new Font("Tahoma", Font.PLAIN, 14));
		//thirdtxt.setText("E:\\process\\MSData\\Machine_number\\");
		thirdtxt.setText("E:\\BGI_work\\luhuifang\\MSTestData\\process\\5600-1");
		thirdtxt.setColumns(10);
		thirdtxt.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				localtxt.setForeground(Color.BLACK);
			}
		});
		
		samplelabel = new JLabel("Sample List");
		samplelabel.setFont(new Font("Segoe UI Light", Font.BOLD, 16));
		
		sampletxt = new JTextField();
		sampletxt.setForeground(Color.LIGHT_GRAY);
		sampletxt.setFont(new Font("Tahoma", Font.PLAIN, 14));
		//sampletxt.setText("sample_list.txt");
		sampletxt.setText("E:\\BGI_work\\luhuifang\\MSTestData\\local\\5600-1\\2018\\season4\\samplelist.txt");
		sampletxt.setColumns(10);
		sampletxt.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				localtxt.setForeground(Color.BLACK);
			}
		});
		
		remotebrowse = new JButton("Browse...");
		remotebrowse.setFont(new Font("Tahoma", Font.PLAIN, 12));
		remotebrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooseFile(e, remotetxt, "Choose reomote path");
			}
		});
		
		localbrowse = new JButton("Browse...");
		localbrowse.setFont(new Font("Tahoma", Font.PLAIN, 12));
		localbrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooseFile(e, localtxt, "Choose local path");
			}
		});
		
		thirdbrowse = new JButton("Browse...");
		thirdbrowse.setFont(new Font("Tahoma", Font.PLAIN, 12));
		thirdbrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooseFile(e, thirdtxt, "Choose the processing path");
			}
		});
		
		samplebrowse = new JButton("Browse...");
		samplebrowse.setFont(new Font("Tahoma", Font.PLAIN, 12));
		samplebrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooseFile(e, sampletxt, "Choose the sample list file path");
			}
		});
		
		refreshlabel = new JLabel("File Changing Timeout(s)");
		refreshlabel.setFont(new Font("Segoe UI Light", Font.BOLD, 16));
		
		refreshtxt = new JTextField();
		refreshtxt.setForeground(Color.LIGHT_GRAY);
		refreshtxt.setFont(new Font("Tahoma", Font.PLAIN, 14));
		refreshtxt.setText("300");
		refreshtxt.setColumns(10);
		refreshtxt.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				refreshtxt.setForeground(Color.BLACK);
			}
		});
	
		logtxt = new JTextPane();
		logtxt.setText("");
		
		scrollPane = new JScrollPane(logtxt);
		
		machinelabel = new JLabel("Machine ID");
		machinelabel.setFont(new Font("Segoe UI Light", Font.BOLD, 16));
		
		machinebox = new JComboBox<String>(machinelist);
		machinebox.setForeground(Color.LIGHT_GRAY);
		machinebox.setFont(new Font("Tahoma", Font.PLAIN, 14));
		machinebox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				machinebox.setForeground(Color.BLACK);
			}			
		});
		
		analysislabel = new JLabel("Analysis Type");
		analysislabel.setFont(new Font("Segoe UI Light", Font.BOLD, 16));
		
		analysisbox = new JComboBox<String>(analysislist);
		analysisbox.setForeground(Color.LIGHT_GRAY);
		analysisbox.setFont(new Font("Tahoma", Font.PLAIN, 14));
		analysisbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				analysisbox.setForeground(Color.BLACK);
			}
		});
		
		runbutton = new JButton("Run");
		runbutton.setFont(new Font("Segoe UI Light", Font.BOLD, 14));
		runbutton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				startRun();
			}
		});
		runbutton.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				changeEnbled(false);
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					startRun();
				}
			}
		});
		
		stopbutton = new JButton("Stop");
		stopbutton.setEnabled(false);
		stopbutton.setFont(new Font("Segoe UI Light", Font.BOLD, 14));
		
		stopbutton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				stopRun();
			}
		});
		stopbutton.addKeyListener(new KeyAdapter() {
			public void keyPressed (KeyEvent e) {
				stopRun();
			}
		});
			
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		
		groupLayout.setHorizontalGroup(
				groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createSequentialGroup()
						.addGap(74)
						.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
							.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE)
							.addGroup(groupLayout.createSequentialGroup()
								.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
									.addComponent(locallabel)
									.addComponent(remotelabel)
									.addComponent(thirdlabel)
									.addComponent(samplelabel)
									.addComponent(machinelabel)
									.addComponent(analysislabel)
									.addComponent(refreshlabel))
									
								.addGap(18)
								.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
									.addGroup(groupLayout.createSequentialGroup()
										.addComponent(runbutton)
										.addGap(49)
										.addComponent(stopbutton))
									.addComponent(localtxt, GroupLayout.DEFAULT_SIZE, 401, Short.MAX_VALUE)
									.addComponent(remotetxt, GroupLayout.DEFAULT_SIZE, 401, Short.MAX_VALUE)
									.addComponent(thirdtxt, GroupLayout.DEFAULT_SIZE, 401, Short.MAX_VALUE)
									.addComponent(sampletxt, GroupLayout.DEFAULT_SIZE, 401, Short.MAX_VALUE)
									.addComponent(machinebox)
									.addComponent(analysisbox)
									.addComponent(refreshtxt, GroupLayout.DEFAULT_SIZE, 401, Short.MAX_VALUE))
								
								.addGap(18)
								.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
									.addComponent(localbrowse)
									.addComponent(remotebrowse)
									.addComponent(thirdbrowse)
									.addComponent(samplebrowse))))
						.addGap(90))
			);
			
			groupLayout.setVerticalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
					.addGroup(groupLayout.createSequentialGroup()
						.addGap(36)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
							.addComponent(locallabel)
							.addComponent(localtxt, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
							.addComponent(localbrowse))
						.addGap(28)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
							.addComponent(remotelabel)
							.addComponent(remotetxt, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
							.addComponent(remotebrowse))
						.addGap(31)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(thirdlabel)
								.addComponent(thirdtxt, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
								.addComponent(thirdbrowse))
						.addGap(31)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(samplelabel)
								.addComponent(sampletxt, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
								.addComponent(samplebrowse))
						.addGap(31)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(machinelabel)
								.addComponent(machinebox))
						.addGap(31)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(analysislabel)
								.addComponent(analysisbox))
						.addGap(31)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(refreshlabel)
								.addComponent(refreshtxt, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE))
						.addGap(32)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
							.addComponent(runbutton)
							.addComponent(stopbutton))
						.addGap(11)
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
						.addGap(37))
			);

			logtxt.setFont(new Font("Segoe UI Historic", Font.PLAIN, 13));
			scrollPane.setViewportView(logtxt);
			frame.getContentPane().setLayout(groupLayout);
			
			Style def = logtxt.getStyledDocument().addStyle(null, null);
			Style normal = logtxt.addStyle("normal",  def);
			Style s = logtxt.addStyle("red",  normal);
			StyleConstants.setForeground(s, Color.RED);
			logtxt.setParagraphAttributes(normal,  true);
			frame.pack();
			frame.setVisible(true);
		
	}
	
	private boolean filePathCheck(File file, JTextPane logtxt) {
		
		if (!file.exists()){
			try{
				logtxt.getDocument().insertString(0, df.format(new Date()) + " " + file.getAbsolutePath() + 
						" is not exists! Please check it ! \n", logtxt.getStyle("red"));
			}catch (BadLocationException e) {
				
			}
			return false;
		}else {
			return true;
		}	
	}
	
	private void changeEnbled(boolean flag) {
		runbutton.setEnabled(flag);
		localbrowse.setEnabled(flag);
		remotebrowse.setEnabled(flag);
		stopbutton.setEnabled(!flag);
	}
	
	private void chooseFile(ActionEvent e, JTextField textField, String title) {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		chooser.showDialog(new JLabel(), title);
		File file = chooser.getSelectedFile();
		if (file != null) {
			textField.setForeground(Color.BLACK);
			textField.setText(file.getAbsolutePath().toString());
		}
	}
	
	private void startRun(){
		logtxt.setText("");
		File localfile = new File(localtxt.getText());
		String remotedir = remotetxt.getText();
		File remotefile = new File(remotedir);
		String processdir = thirdtxt.getText();
		String machine = (String) machinebox.getSelectedItem();
		String analysis = (String) analysisbox.getSelectedItem();
		AnalysisType analysistype = AnalysisType.getAnalysisType(analysis);
		MachineID machineID = MachineID.getMachineID(machine);
		String season = remotefile.getName();
		String year = remotefile.getParentFile().getName();
		File processfile = new File(processdir + File.separator + year + File.separator + season);
		File samplelist = new File(sampletxt.getText());
		
		if (!remotefile.exists()) {
			remotefile.mkdirs();
			String loginfo = df.format(new Date()) + " Create the remote directory: " + remotefile.getAbsolutePath() + "\n";
			try {
				logtxt.getDocument().insertString(0, loginfo, logtxt.getStyle("blue"));
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
		
		long scantimeout = 300;
		long refreshtimeout = Long.parseLong(refreshtxt.getText());
		monitor = new FileMonitor(localfile, remotefile, processfile, samplelist, analysistype, machineID, scantimeout, refreshtimeout, logtxt);
		if (filePathCheck(remotefile, logtxt) && filePathCheck(localfile, logtxt) && filePathCheck(processfile, logtxt) && filePathCheck(samplelist, logtxt)) {
			changeEnbled(false);
			monitor.start();
		}
	}
	
	private void stopRun(){
		changeEnbled(true);
		
		try {
			monitor.stop();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
}
