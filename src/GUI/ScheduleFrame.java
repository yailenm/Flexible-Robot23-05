package GUI;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

//import hffsp.model.HFFSPInstance;
//import hffsp.model.Schedule;

import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ScheduleFrame extends JFrame {
	
	private static final long serialVersionUID = 6561306520838117775L;
	
	Schedule bestSchedule;
	ScheduleJComponent scheduleJComponent;
	Instance problemInstance;
	
	public ScheduleFrame(Instance problemInstance, Schedule initialSchedule, String title) {
		this.problemInstance=problemInstance;
		this.setTitle(problemInstance.getName()+" | makespan: "+initialSchedule.getMakespan()+" " +title);
		scheduleJComponent = new ScheduleJComponent(initialSchedule, problemInstance);
		
	//	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //to close the main application
		//this.setSize(1200, 750);
		
		
		//new frame size
		java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();		
		//int l =  problemInstance.machines.length*100;
		setBounds((screenSize.width-1200)/2, (screenSize.height-350)/2, 1200, 350);
				
		JPanel jp = new javax.swing.JPanel();
		jp.setSize(200,250);       
		
       // final JButton validate = new JButton("  Validate   ");
        final JButton undo = new JButton("    Undo     ");
        final JButton fix = new JButton("     Fix     ");
       // final JButton end_fix = new JButton("   End Fix   ");
        
        final JButton optimize = new JButton(" Right Shift ");
        final JButton ql = new JButton("  Q-Learning ");
        JButton save_files = new JButton("Save Schedule");
        JButton save_schedule = new JButton("Save Gantt Chart");
        
        
       // final JButton perturbations = new JButton("Add Perturbation");
        
       // jp.add(validate);
        jp.add(fix);
       // jp.add(end_fix);
        jp.add(optimize);
        jp.add(ql);
        jp.add(undo);
        jp.add(save_files);
        jp.add(save_schedule); 
        
        
        this.add(jp,"South");
        optimize.setEnabled(false);
        
        fix.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
				scheduleJComponent.fix = true;	
				scheduleJComponent.contPintar =1;
					//System.out.println("O3 ");
					//end_fix.setEnabled(true);
					fix.setEnabled(false);
				//	validate.setEnabled(false);
					optimize.setEnabled(true);
					ql.setEnabled(false);
					//optim = false;
					setResizable(false);			
			}
		});
        
        
        save_schedule.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				JFileChooser saveFileChooser = new JFileChooser(System.getProperty("user.dir"));
	        	saveFileChooser.setDialogTitle("Enter the name for the png image");
	        	FileNameExtensionFilter restrict = new FileNameExtensionFilter("Only .png files", "png"); 
	        	//saveFileChooser.addChoosableFileFilter(restrict);
	        	saveFileChooser.setFileFilter(restrict);
	        	saveFileChooser.setBorder(new LineBorder(Color.BLUE));

	        	BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
	        	Graphics2D graphics2D = image.createGraphics();
	        	paint(graphics2D);
	        	try {
		        	int returnVal = saveFileChooser.showSaveDialog(null);
		        	if (returnVal == JFileChooser.APPROVE_OPTION) {		
		        	    File fileC = saveFileChooser.getSelectedFile();
		        	    //ImageIO.write(image, "jpg", new File("test.jpg"));	        	    
		        	    
		        	  //Son estas dos líneas o solo la de abajo
		        	    String newName = fileC.getName() + ".png";
		        	    ImageIO.write(image, "png", new File(newName));
		        	    
		        	  //Es solo esta línea o las dos de arriba
		        	  //ImageIO.write(image, "png", fileC);
		        	}	
	        	} catch (IOException e1) {
	        	// TODO Auto-generated catch block
	        		e1.printStackTrace();
	        		}
				
				
			}
		});
        
        
        save_files.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				JFileChooser saveFileChooser = new JFileChooser(System.getProperty("user.dir"));
	        	saveFileChooser.setDialogTitle("Enter the name for the text file");
	        	FileNameExtensionFilter restrict = new FileNameExtensionFilter("Only .txt files", "txt"); 
	        	//saveFileChooser.addChoosableFileFilter(restrict);
	        	saveFileChooser.setFileFilter(restrict);
	        	saveFileChooser.setBorder(new LineBorder(Color.BLUE));
	        	
	        	try {
		        	int returnVal = saveFileChooser.showSaveDialog(null);
		        	if (returnVal == JFileChooser.APPROVE_OPTION) {		
		        	    File fileB = saveFileChooser.getSelectedFile();
		        	    //File fileC = 
		        	    PrintWriter pw = new PrintWriter(fileB);
		    			pw.println(0.0);
		    			pw.flush();
		    			
		    			pw.close();	
		        	    
		        	}	
	        	} catch (IOException e1) {
	        	// TODO Auto-generated catch block
	        		e1.printStackTrace();
	        		}


			}
		});
        
        
        optimize.addActionListener(new ActionListener() {
        	        	
			@Override
			public void actionPerformed(ActionEvent e) {
				if (scheduleJComponent.newTimeOperationFix != -1) {
					scheduleJComponent.shiftRight();
					ScheduleFrame.this.setTitle(ScheduleFrame.this.problemInstance.getName()+" | makespan: "+scheduleJComponent.timeHorizon);
					fix.setEnabled(true);
					scheduleJComponent.newTimeOperationFix = -1;
					//validate.setEnabled(false);
					optimize.setEnabled(false);	
					ql.setEnabled(true);	
					setResizable(true);
				}else {
					JOptionPane.showMessageDialog(ScheduleFrame.this, "You should fix one operation", "ERROR", JOptionPane.ERROR_MESSAGE);
				}
				
			}
        });
               
		this.add(scheduleJComponent);
		this.setVisible(true);
	}

	
    public void saveSchedule(String msg) throws IOException {
        scheduleJComponent.saveSchedule(problemInstance.getName() + "-" + msg);
    }
	
}
