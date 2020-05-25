package GUI;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;




/**
 * JComponent to draw Gantt-charts of schedules.
 * 
 * @author Tony Wauters (tony.wauters@kahosl.be): the original code 
 * @author bert (bert.vanvreckem@gmail.com): changes for HFFSP instances
 * @author Yailen Martinez (yailenm@gmail.com): changes to load solution without validating the resulting schedule 
 */

public class ScheduleJComponent extends JComponent{
    

    private static final long serialVersionUID = -8215813460445276579L;

    Schedule schedule;
    Instance problemInstance;

    int border=50;
    int machineUnitHeight=30;
    private int gapBetweenMachines=20;
    int[][][] machineCapacities;

    Map <Integer,Integer> machineIdToCurrentYMap=new HashMap<Integer, Integer>();
    Map <JobGUI,Color> jobToColorMap=new HashMap<JobGUI, Color>();

    int axisUnit=10;
    double axisUnitPixels = 40;

    private RenderingHints renderHints;
    
    List <Boton> rec = new ArrayList<Boton>();//list of buttons
    int count =0;//position of button 
    int contPintar =0;
    boolean fix = false;//if fix is pressed
    Boton operationFix;//button fix
    int newTimeOperationFix = -1;// new time of the operation fixed
    int timeHorizon;
    
    public ScheduleJComponent(Schedule schedule, Instance instance) {
        this.schedule=schedule;
        this.problemInstance=instance;
        renderHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
        renderHints.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
    }

    public void update(Schedule schedule){
        this.schedule=schedule;
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
       super.paintComponent(g);
    	Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHints(renderHints);

        timeHorizon=schedule.getMakespan();
        axisUnit = (int)((double)timeHorizon/((double)this.getWidth()/axisUnitPixels));
        if (axisUnit<1) axisUnit=1;
        double xScale=((double)(this.getWidth()-2*border))/(double)timeHorizon;

        drawMachines(g2d, timeHorizon, xScale);
        

        // Draw schedule  
        if(contPintar == 0) {
        	 this.removeAll();    	  
             rec.clear();
             count = 0;
        	for (OperationAllocation operAlloc : schedule.getAllocations()) {
	          drawAllocation(g2d, xScale, operAlloc);
        	}
        	  	
        }
        
        // Draw timeline
        //int axisY=border+(machineUnitHeight*12)+(gapBetweenMachines*12); //move time line up and down
        int axisY=border+(machineUnitHeight*4)+(gapBetweenMachines*problemInstance.getMachineList().size()); //move time line up and down
        int axisX=border;
            
        g2d.drawLine(border, axisY, border+(int)(timeHorizon*xScale), axisY);
        for (int t=0;t<=timeHorizon;t++){
            if (t%axisUnit==0 || t==timeHorizon){
                g2d.drawLine(axisX+(int)(t*xScale), axisY-5, axisX+(int)(t*xScale), axisY+5);
                g2d.drawString(""+t, axisX+(int)(t*xScale), axisY+12);
            }
        }     
			
    }

    
    /**
     * Saves schedule to file
     * @param baseName
     */
    public void saveSchedule(String baseName) throws IOException {
        File imgFile = new File("Solutions/Solution-" + baseName + ".png");
        
        BufferedImage bi = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D ig2 = bi.createGraphics();
     
        ig2.drawRect(0, 0, getWidth(), getHeight());
        ig2.setBackground(Color.lightGray);
       
        paintAll(ig2);
        ImageIO.write(bi, "PNG", imgFile);
       
    }
    
    
    /**
     * Draws an occupied time slot in the bar of the specified machine, with a specified start and end time. In
     * addition, you can specify border and fill color, a message, and whether this concerns an idle time slot.
     * If the time slot is idle, the height of the box is reduced. 
     * @param g2d         graphics object
     * @param xScale      X-axis scale
     * @param machine     the machine this time slot belongs to
     * @param startTime   start of the time slot
     * @param endTime     end of the time slot
     * @param fillColor   color of the inside of the slot
     * @param borderColor color of the border
     * @param idle        denotes whether this concerns an idle box
     * @param msg         message to be drawn inside the box
     */
    private void drawTimeBox(Graphics2D g2d, double xScale, MachineGUI machine, int startTime, int endTime, Color fillColor, Color borderColor, boolean idle, String msg,int job, int operation, OperationGUI op, boolean borde) {
        int margin = idle ? 3 : 0; 
        int currentY = machineIdToCurrentYMap.get(machine.getId());
       /* g2d.setPaint(fillColor);
        g2d.fillRect(
                (int)(border+xScale*startTime), 
                currentY+margin, 
                (int)(xScale*(double)(endTime-startTime)), 
                machineUnitHeight-2*margin);
        
        g2d.setPaint(borderColor);
        g2d.drawRect(
                (int)(border+xScale*startTime), 
                currentY+margin, 
                (int)(xScale*(double)(endTime-startTime)), 
                machineUnitHeight-2*margin);*/
        
      
        if(msg != null || msg != "") {
        	//Draw button
       // 	System.out.println(msg);
        //	 g2d.setPaint(Color.black);
          /*   g2d.drawString(msg, 
                     (int)(border + xScale*startTime)+4, 
                     (int)(currentY + ((double)machineUnitHeight/2.0)));   */
        	Rectangle r =  new Rectangle((int)(border+xScale*startTime), 
  	              currentY+margin, (int)(xScale*(double)(endTime-startTime)), 
    	              machineUnitHeight-2*margin);
	      	rec.add(count, new Boton(msg, fillColor,r,operation,job,count,startTime, endTime, machine.getId(), op,borde) );
	      //	modif.add(count, r);
	      //	temp.add(count, new Boton(msg, fillColor,r,operation,job,count,min_itime,startTime, endTime, machine.getId(),op,borde) );
	     /* 	if(machine.getId() == problemInstance.machines.length-1)
	      		y = currentY+margin +(machineUnitHeight-2*margin)+20;*/
	      	this.add(rec.get(count));
	      	count++;
	      	
	        // Draw text inside the box
                    
        }
        
    }
    
    
    private void drawSlack(Graphics2D g2d, double xScale, JobGUI job, MachineGUI machine, int fromTime, int slack){
    	if(slack == 0) return;
    	
    	//grey box??
        int currentY = machineIdToCurrentYMap.get(machine.getId());
        int x1 = (int)(border+xScale*fromTime);
        int x2 = (int)(border+xScale*(fromTime+slack));
        int y = (int)(currentY + ((double)machineUnitHeight/2.0));

        // color of the lag is a bit darker than the job color.
//        g2d.setPaint(jobColor(job).darker());
        g2d.setPaint(Color.black);

        g2d.drawLine(x1, y, x2, y);     // draw line
        g2d.drawLine(x2, y-5, x2, y+5); // draw bar at end of line
    	
    }

    /**
     * Draw the specified allocation on the Gantt-chart.
     * @param g2d         graphics object
     * @param xScale      X-axis scale
     * @param alloc  an operation allocation
     */
    private void drawAllocation(Graphics2D g2d, double xScale, OperationAllocation alloc) {
        JobGUI job = alloc.getOperation().getJob();  
        
        // Now draw the actual operation
        drawTimeBox(
                g2d, xScale, 
                alloc.getMachine(), 
                alloc.getStartTime(), alloc.getEndTime(), 
                jobColor(job), Color.black, 
                false,
                //"J"+job.getId()+"-"+alloc.getOperation().getId());
        		""+alloc.getOperation().getName(), job.getId(), alloc.getOperation().getId(),alloc.getOperation(),alloc.getBorder());
      
        /* drawTimeBox(
                g2d, xScale, 
                alloc.getMachine(), 
                alloc.getStartTime(), alloc.getEndTime(), 
                jobColor(job), Color.black, 
                false,
                //"J"+job.getId()+"-"+alloc.getOperation().getId());
        		""+alloc.getOperation().getName());*/
        
     // Draw slack
        drawSlack(g2d, xScale, 
                job,
                alloc.getMachine(), 
                alloc.getEndTime(), 
                alloc.getOperation().getSlack());
//                job.getSlack(alloc.getMachine().getId()));
    }

    
    /**
     * Returns a unique color for each job.
     * @param job a job.
     * @return a unique color for the specified job.
     */
    private Color jobColor(JobGUI job) {
        if (!jobToColorMap.containsKey(job)) {
            Random rand = new Random(job.getId());
            Color color = new Color(rand.nextInt(255),rand.nextInt(255),rand.nextInt(255));
            
            jobToColorMap.put(job, color);
            return color;
        } else {
            return jobToColorMap.get(job);
        }
    }

	
    private void drawMachines(Graphics2D g2d, int timeHorizon, double xScale) {
        int currentX=border;
        int currentY=border;
       // int j =0;
        for (MachineGUI machine :problemInstance.getMachineList()){
            machineIdToCurrentYMap.put(machine.getId(), currentY);
            int machineId = machine.getId();
            //int capacity = 1; //machine.getCapacity();
            switch (machineId) {
			case 0:
				g2d.drawString("armL", (int)((double)border/3.0), currentY+(int)((double)machineUnitHeight/2.0));
				break;
			case 1:
				g2d.drawString("armR", (int)((double)border/3.0), currentY+(int)((double)machineUnitHeight/2.0));
				break;
			default:
				g2d.drawString("oper", (int)((double)border/3.0), currentY+(int)((double)machineUnitHeight/2.0));
				break;
			}          
            	
           // for (int i=0;i<capacity;i++){
           // System.out.println("currentX "+currentX+" mmm "+currentY);
                g2d.drawRect(currentX, currentY, (int)(timeHorizon*xScale), machineUnitHeight);
                currentY+=machineUnitHeight;
           // }          
            currentY+=gapBetweenMachines;
        }
    }
    
    
    public void shiftRight() {
		// TODO Auto-generated method stub
    	fix = false;
    	int plusNewTimeAll = newTimeOperationFix-operationFix.duration;
    	//System.out.println("time diferencia "+plusNewTimeAll);	
    	//System.out.println("op fija "+operationFix.getJob()+ " op "+operationFix.operation+" name "+operationFix.text);	
    	
    	for (int i = 0; i < rec.size(); i++) { //buscar en el arreglo de botones
			if (rec.get(i).getStart_time() >= operationFix.getFinal_time()) { //si el tiempo de inicio de la op es mayor o igual q el tiempo final de la op modificada 
			//	System.out.println("OPeration con tiempo de iniciio mayor q el final de la fija");
			//	System.out.println("job "+rec.get(i).job+" op "+rec.get(i).operation+" name "+ rec.get(i).text);
				rec.get(i).start_time+= plusNewTimeAll;
				rec.get(i).final_time += plusNewTimeAll;
				schedule.setStartTime(rec.get(i).op,rec.get(i).start_time);
				schedule.setEndTime(rec.get(i).op,rec.get(i).final_time);
				if (rec.get(i).op.getBackToBackBefore() != -1 && !(rec.get(i).getJob() == operationFix.getJob() && rec.get(i).op.getBackToBackBefore() == operationFix.getOperation())) { // si tiene operacion back to back y no es al fija
				//	System.out.println("tiene back to back y no es la fija "+ rec.get(i).op.getBackToBackBefore());
						//System.out.println("hay back to back "+rec.get(i).text+" back "+rec.get(i).op.getBackToBackBefore());
						for (int j = 0; j < rec.size(); j++) { // search operation back to back
							
							if (rec.get(i).getJob() == rec.get(j).getJob() && rec.get(i).op.getBackToBackBefore() == rec.get(j).getOperation() //mismo trabajo y la op back to back									
									&& rec.get(j).start_time < operationFix.final_time) {
								rec.get(j).start_time+= plusNewTimeAll;
								rec.get(j).final_time += plusNewTimeAll;
								schedule.setStartTime(rec.get(j).op,rec.get(j).start_time);
								schedule.setEndTime(rec.get(j).op,rec.get(j).final_time);
							//	System.out.println("encontro la op back to back y se mueve");
								break;
							}
						}
					
				}
				//tempCount++;
			}
		}
    	
    	//up to date times operation modified 
    	rec.get(operationFix.position).duration = newTimeOperationFix;
    	//rec.get(operationFix.position).start_time+= plusNewTimeAll;
		rec.get(operationFix.position).final_time = rec.get(operationFix.position).start_time + rec.get(operationFix.position).duration;
		schedule.setStartTime(rec.get(operationFix.position).op,rec.get(operationFix.position).start_time);
		schedule.setEndTime(rec.get(operationFix.position).op,rec.get(operationFix.position).final_time);
		
		//clean attributes operation modified
		operationFix = null;
		newTimeOperationFix = 0;
		contPintar = 0;
		schedule.setMakespan((schedule.getMakespan()+plusNewTimeAll));
		timeHorizon = schedule.getMakespan();
		repaint();
	}
       
    public class Boton extends JButton implements ActionListener, MouseListener {
		   /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Rectangle rect;
		 private int operation;
		   private int job;
		   private int position;
		//   private int min_tinicio;
		   private int start_time; 
		   private int final_time; 
		   private int machine;
		   private int duration; 
		   OperationGUI op;
		   boolean borde;
		  // boolean fixColor;
		   Color fillColor;
		   private int t_max_inic_fix = -1;
		   private String text;
		
			
			public Boton (String text, Color fillColor, Rectangle r, int operation, int job, int positionRec,int start_time, int f_time, int machine,OperationGUI op, boolean borde) {
				String s = text.substring(0, 6);
				//super(s);
				this.setText(s);
				this.text = text;
				this.setOpaque(true);
				this.setBorderPainted(true);
				//this.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
				this.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
				this.setBackground(fillColor);
				this.fillColor = fillColor;
				this.setBounds(r);
				this.setForeground(Color.white);
				rect = new Rectangle(r);
				this.operation = operation;
				this.job = job;
				this.position = positionRec;
				//this.min_tinicio = min_itime;
				this.start_time = start_time;
				this.final_time = f_time;
				this.machine = machine;
				duration = f_time - start_time;
				this.op = op;
				setToolTipText(text + " ("+this.start_time+"-"+this.final_time+")");
				
				
				this.borde = borde;
				if (borde) {
					this.setBorderPainted(true);
					setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(204, 0, 51)), javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(204, 0, 51))));
				}
				//this.fixColor = fixColor;
				
			     /*   for (int i = 0; i < copyFixOP.size(); i++) {
			        	//System.out.println(copyFixOP.get(i).getOperation()+" job "+copyFixOP.get(i).job);
						if (copyFixOP.get(i).getOperation() == operation && copyFixOP.get(i).getJob() == job) {
							this.setBorderPainted(true);
							setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(102,153,255)), javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(102,153,255))));
							break;
						}
					}
				for (int i = 0; i < arrayFix.size(); i++) {
					if(arrayFix.get(i).operation == operation && arrayFix.get(i).job == this.job){
						this.setBackground(Color.black);
						break;
					}
					
				}*/
				this.setMargin(new Insets(1, 1, 1, 1));
				this.setFont(this.getFont().deriveFont(11f));
				// addMouseMotionListener(this);
				 addActionListener( this );
				 addMouseListener(this);
			}		
		
			
			public Rectangle getRect() {
				return rect;
			}
			
			public double getXRect() {
				return rect.x;
			}
			
			public double getYRect() {
				return rect.y;
			}
			
			public int getMaxTimeInicFix() {
				return t_max_inic_fix;
			}
			
			public boolean getContains(int x, int y) {
				return rect.contains(x, y);
					
			}

			
				

			    public int getOperation() {
			        return operation;
			    }

			    public int getJob() {
			        return job;
			    }

			    public int getPosition() {
			        return position;
			    }			   

			    public int getStart_time() {
			        return start_time;
			    }

			    public int getFinal_time() {
			        return final_time;
			    }

			    public int getMachine() {
			        return machine;
			    }

			    public int getDuration() {
			        return duration;
			    }

			    public boolean isBorde() {
			        return borde;
			    }

			    public Color getFillColor() {
			        return fillColor;
			    }
			    
			    @Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					
					if(fix){
						//verificar q el numero introducido sea mayor que el q estaba y q sea un numero
						try {
							if (operationFix != null && operationFix.getJob()==this.job && operationFix.getOperation()==this.operation) {//si es la misma operacion seleccionada anteriormente
								operationFix = null;
								newTimeOperationFix = -1;
								this.setBackground(fillColor);
							//	System.out.println("Misma operacion");
							}else {
								String newTime = JOptionPane.showInputDialog(this, "Enter a new duration", "Information", JOptionPane.INFORMATION_MESSAGE);
							//	System.out.println("Diferente operacion"+newTime);
								if (newTime != null && !newTime.equals("") && Integer.parseInt(newTime) > this.duration) {
									if(operationFix != null) 
										rec.get(operationFix.position).setBackground(operationFix.fillColor);
								//System.out.println("color "+operationFix.fillColor.toString());
									operationFix = new Boton(text, fillColor, rect, operation, job, position, start_time, final_time, machine, op, borde);
									newTimeOperationFix = Integer.parseInt(newTime);
									this.setBackground(Color.black);
								}else {
									if (newTime != null && newTime.equals("")) 
										JOptionPane.showMessageDialog(this, "You should enter a new duration","Message",JOptionPane.WARNING_MESSAGE);
									else if (newTime != null && Integer.parseInt(newTime) <= this.duration) {
										JOptionPane.showMessageDialog(this, "You should enter a duration longer than the previous one ("+this.duration+")", "ERROR", JOptionPane.ERROR_MESSAGE);
									}
								}
							}
						  }catch(NumberFormatException e) {
							  JOptionPane.showMessageDialog(this, "You should enter a number", "ERROR", JOptionPane.ERROR_MESSAGE);
						  }
						}					
					
					//System.out.println(" action optimize " + arrayFix.size());
					 
			
				}
			    
			    //Eventos mouse listener
			    
			    @Override
				public void mouseEntered(MouseEvent evt) {

	                
	                //boton.setEnabled(false);
	            }

				@Override
				public void mouseClicked(MouseEvent arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseExited(MouseEvent arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mousePressed(MouseEvent arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseReleased(MouseEvent arg0) {
					// TODO Auto-generated method stub
					
				}
			
		}


	

		
}