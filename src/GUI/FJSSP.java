package GUI;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.Rectangle;
import javax.swing.JFileChooser;
import java.io.*;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import Logic.QLearning;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;



public class FJSSP extends JFrame {

	private static final long serialVersionUID = 1L;
	private JButton OpenFile = null;
	private JPanel panel = null;
	private JFileChooser FileChooser = new JFileChooser(System.getProperty("user.dir"));  //  @jve:decl-index=0:visual-constraint="649,12"
	private JFileChooser FileChooserLoad = new JFileChooser(System.getProperty("user.dir"));
	public File []file = new File[2];
	private File sol_file ;
	private JLabel jLabel1 = null;
	private JButton JSSP = null;
	private JLabel jLabel2 = null;
	private JTextField epsilon = null;
	private JTextField alpha = null;
	private JTextField gamma = null;
	private JLabel discFactor = null;
	private JLabel LRate = null;
	private JLabel Epsilon = null;
	private JLabel Iterations = null;
	private JTextField Cycles = null;	
	public Logic.QLearning ql;
	
//	private JButton Draw = null;
	private JPanel jContentPane1 = null;
	@SuppressWarnings("unused")
	private int countWordsInFile(File filew) throws IOException {
        int numberOfWords = 0;  // Number of words.
        String s= new String();
        FileReader f = new FileReader(filew);
	 	BufferedReader a = new BufferedReader(f);
	 	s = a.readLine();
	 	String []cadArray = s.split("[ \t]+");
	 	numberOfWords = cadArray.length;
	 	a.close();
        return numberOfWords;
    }


	private JButton getOpenFile() {
		if (OpenFile == null) {
			OpenFile = new JButton();
			OpenFile.setForeground(new Color(0, 0, 0));
			OpenFile.setBounds(new Rectangle(207, 19, 85, 29));
			OpenFile.setText("Open");
			
			FileNameExtensionFilter filter = new FileNameExtensionFilter("DZN files", "dzn");
			FileChooser.setFileFilter(filter);
			FileChooser.setMultiSelectionEnabled(true);
			
			OpenFile.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					
					int returnVal = FileChooser.showOpenDialog(FJSSP.this);
					
					if (returnVal == JFileChooser.APPROVE_OPTION) {
		                file = FileChooser.getSelectedFiles();		                
					}
				}
			});
		}
		return OpenFile;
	}


	private JPanel getPanel() {
		if (panel == null) {
			Iterations = new JLabel();
			Iterations.setForeground(new Color(255, 255, 255));
			Iterations.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 12));
			Iterations.setBounds(new Rectangle(30, 209, 82, 16));
			Iterations.setText("Iterations");
			Epsilon = new JLabel();
			Epsilon.setForeground(new Color(255, 255, 255));
			Epsilon.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 12));
			Epsilon.setBounds(new Rectangle(30, 176, 62, 16));
			Epsilon.setText("Epsilon");
			LRate = new JLabel();
			LRate.setForeground(new Color(255, 255, 255));
			LRate.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 12));
			LRate.setBounds(new Rectangle(31, 140, 98, 16));
			LRate.setText("Learning Rate");
			discFactor = new JLabel();
			discFactor.setForeground(new Color(255, 255, 255));
			discFactor.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 12));
			discFactor.setBounds(new Rectangle(31, 107, 116, 16));
			discFactor.setText("Discount Factor");
			jLabel2 = new JLabel();
			jLabel2.setForeground(new Color(255, 255, 255));
			jLabel2.setFont(new Font("Tahoma", Font.BOLD, 13));
			jLabel2.setBounds(new Rectangle(31, 73, 201, 16));
			jLabel2.setText("Parameters for the Q-Learning");
			jLabel1 = new JLabel();
			jLabel1.setForeground(new Color(255, 255, 255));
			jLabel1.setFont(new Font("Tahoma", Font.BOLD, 13));
			jLabel1.setBounds(new Rectangle(31, 24, 178, 16));
			jLabel1.setText("Select instance to Solve");
			panel = new JPanel();
			panel.setBackground(new Color(0, 128, 128));
			panel.setBorder(new BevelBorder(BevelBorder.LOWERED, new Color(51, 153, 255), null, null, null));
			panel.setLayout(null);
			panel.add(getOpenFile(), null);
			panel.add(jLabel1, null);
			panel.add(jLabel2, null);
			panel.add(getEpsilon(), null);
			panel.add(getAlpha(), null);
			panel.add(getGamma(), null);
			panel.add(discFactor, null);
			panel.add(LRate, null);
			panel.add(Epsilon, null);
			panel.add(Iterations, null);
			panel.add(getCycles(), null);
			panel.add(getJSSP(), null);
			
			JButton btnLoadSchedule = new JButton("Load Schedule");
			btnLoadSchedule.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					//open the text file with the schedule
					int returnVal = FileChooserLoad.showOpenDialog(null);
					//FileChooser.setBorder(new LineBorder(Color.BLUE));
					if (returnVal == JFileChooser.APPROVE_OPTION) {
		                sol_file = FileChooserLoad.getSelectedFile();
		                //System.out.println("fichero seleccionado " + sol_file.getName());
		                try {
							Test test = new Test(sol_file);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		                
					}
				}
			});
			btnLoadSchedule.setBounds(288, 168, 129, 32);
			panel.add(btnLoadSchedule);
			
			java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
			setBounds((screenSize.width-454)/2, (screenSize.height-520)/2, 478, 279);
			setResizable(false);
			
		}
		return panel;
	}
	
	

	private JButton getJSSP() {
		if (JSSP == null) {
			JSSP = new JButton();
			JSSP.setBounds(new Rectangle(288, 124, 129, 32));
			JSSP.setText("Flexible JSSP");
			JSSP.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					double LR = Double.parseDouble(alpha.getText());
					double DF = Double.parseDouble(gamma.getText());
					double epsi = Double.parseDouble(epsilon.getText());
					int cycles = Integer.parseInt(Cycles.getText());
					if (file[0]==null || file.length!=2) {
						JOptionPane.showMessageDialog(null, "You must select two files first", "Error", JOptionPane.OK_OPTION);
					}else {
						try {
							try {
								ql = new QLearning(file, LR, DF, cycles, epsi);
								
							} catch (FileNotFoundException e2) {
								// TODO Auto-generated catch block
								e2.printStackTrace();
							}
							try {						
								ql.ReadData(file);						
		//						QL2.ReadDataDueDates(file);
							} catch (IOException e1) {
								//System.err.println ("Error opening file");
								e1.printStackTrace();
							}
							try {
								//QL2.ExecuteEverybody(LR, DF);
								ql.Execute(LR, DF);
							} catch (FileNotFoundException e1) {
								e1.printStackTrace();
							} catch (CloneNotSupportedException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}catch (IndexOutOfBoundsException e2) {
							JOptionPane.showMessageDialog(null, "Files not valid","Error", JOptionPane.OK_OPTION);
						}
					}
				}
			});
		}
		return JSSP;
	}


	private JTextField getEpsilon() {
		if (epsilon == null) {
			epsilon = new JTextField();
			epsilon.setFont(new Font("Tahoma", Font.PLAIN, 12));
			epsilon.setHorizontalAlignment(SwingConstants.CENTER);
			epsilon.setBounds(new Rectangle(150, 170, 73, 28));
			epsilon.setText("0.2");
		}
		return epsilon;
	}


	private JTextField getAlpha() {
		if (alpha == null) {
			alpha = new JTextField();
			alpha.setFont(new Font("Tahoma", Font.PLAIN, 12));
			alpha.setHorizontalAlignment(SwingConstants.CENTER);
			alpha.setBounds(new Rectangle(150, 135, 73, 28));
			alpha.setText("0.1");
		}
		return alpha;
	}


	private JTextField getGamma() {
		if (gamma == null) {
			gamma = new JTextField();
			gamma.setFont(new Font("Tahoma", Font.PLAIN, 12));
			gamma.setHorizontalAlignment(SwingConstants.CENTER);
			gamma.setBounds(new Rectangle(150, 100, 73, 28));
			gamma.setText("0.8");
		}
		return gamma;
	}


	private JTextField getCycles() {
		if (Cycles == null) {
			Cycles = new JTextField();
			Cycles.setFont(new Font("Tahoma", Font.PLAIN, 12));
			Cycles.setHorizontalAlignment(SwingConstants.CENTER);
			Cycles.setBounds(new Rectangle(150, 203, 73, 28));
			Cycles.setText("1");
		}
		return Cycles;
	}


	/**
	 * This method initializes jContentPane1	
	 * @return javax.swing.JPanel	
	 */
	@SuppressWarnings("unused")
	private JPanel getJContentPane1() {
		if (jContentPane1 == null) {
			jContentPane1 = new JPanel();
			jContentPane1.setLayout(new BorderLayout());			
			
		}
		return jContentPane1;
	}


	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				FJSSP thisClass = new FJSSP();
				thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				thisClass.setVisible(true);
			}
		});
	}

	
	public FJSSP() {
		super();
		initialize();
	}


	private void initialize() {
		this.setSize(454, 311);
		this.setContentPane(getPanel());
		this.setTitle("POC Two-armed robot");
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
