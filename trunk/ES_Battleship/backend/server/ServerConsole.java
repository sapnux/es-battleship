package backend.server;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ServerConsole extends JFrame implements ActionListener {
	
	Container mainPanel;
	JScrollPane logScrollPane;
	protected JTextArea logArea;
	JButton closeButton, clearButton, saveButton;
	
	public ServerConsole () {
		
		GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        
        setTitle("Battleship Server");
		mainPanel = getContentPane();
		mainPanel.setLayout(gridbag);
		
		logArea = new JTextArea();
		logArea.setLineWrap(true);
		logArea.setWrapStyleWord(true);
		logArea.setEditable(false);
		
		logScrollPane = new JScrollPane(
			logArea,
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		logScrollPane.setWheelScrollingEnabled(true);
		logScrollPane.setPreferredSize(new Dimension(400, 300));
		logScrollPane.setMinimumSize(new Dimension(400, 300));
		logScrollPane.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("Activity log"),
                                BorderFactory.createEmptyBorder(2,2,2,2)),
                logScrollPane.getBorder()));
                
        c.weightx = 1.0;
		c.weighty = 0.8;
		c.gridwidth = 4;
		c.gridheight = 10;
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;

		gridbag.setConstraints(logScrollPane, c);
		mainPanel.add(logScrollPane);
		
		Dimension buttonDim = new Dimension(70,25);
		c.insets = new Insets(5,10,5,5);
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.gridx = 1;
		c.gridy = 11;
		c.fill = GridBagConstraints.NONE;
		
		closeButton = new JButton("Shutdown");
		closeButton.setToolTipText("Shut down the server");
		closeButton.setPreferredSize(new Dimension(95,25));
		closeButton.setActionCommand("Close");
		closeButton.addActionListener(this);
		gridbag.setConstraints(closeButton, c);
		mainPanel.add(closeButton);
		
		c.gridx = 2;
		c.gridy = 11;
		c.insets = new Insets(5,1,5,5);
		
		clearButton = new JButton("Clear");
		clearButton.setToolTipText("Clear log window");
		clearButton.setPreferredSize(buttonDim);
		clearButton.setMnemonic(KeyEvent.VK_C);
		clearButton.setActionCommand("Clear");
		clearButton.addActionListener(this);
		gridbag.setConstraints(clearButton, c);
		mainPanel.add(clearButton);
		
		c.gridx = 3;
		c.gridy = 11;
		
		saveButton = new JButton("Save");
		saveButton.setToolTipText("Save current log to file");
		saveButton.setPreferredSize(buttonDim);
		saveButton.setMnemonic(KeyEvent.VK_S);
		saveButton.setActionCommand("Save");
		saveButton.addActionListener(this);
		gridbag.setConstraints(saveButton, c);
		mainPanel.add(saveButton);
		
		addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
            	System.exit(0);
            }
        });
    }
        
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Close")) {
			System.exit(0);
		}
		else if(e.getActionCommand().equals("Clear")) {
			logArea.setText("");
		}
		
		else if(e.getActionCommand().equals("Save")) {
			try {
				PrintWriter out;
				GregorianCalendar today = new GregorianCalendar();
				String logName = "Battleship-log-"+
					today.get(Calendar.MONTH)+"-"+
					today.get(Calendar.DATE)+".txt";
					
				JFileChooser fc = new JFileChooser();	
				fc.setSelectedFile(new File(logName));
    			int returnVal = fc.showSaveDialog(mainPanel);
    			if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();           
					out = new PrintWriter(
						new FileOutputStream(file));
					out.print(logArea.getText());
					out.close();
				}
			} catch (IOException ex) {
				System.out.println(ex);
			}
		}		
    }// actionPerformed
    
    public void write(String str) {
    	logArea.append(str+"\n");
    }
}