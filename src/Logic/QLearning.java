package Logic;
//import java.awt.JobAttributes;
import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Date;
import java.util.List;

import GUI.Instance;
import GUI.MachineGUI;
import GUI.OperationAllocation;
import GUI.OperationGUI;
import GUI.Pair;
import GUI.Schedule;
import GUI.ScheduleFrame;

public class QLearning {

	private double LearningRate;
	private double DiscountFactor;
	private int iterations;
	private double epsilon;
	private String filename;
	private int njobs, nmachines, navg_op;
	private Job[] Jobs;
	private Zone[] zone;//new
	int countZones;//new
	public Machine[] Machines;
	private int Cmax;
	private int BestSol;
	public int max_num_operations;
	public LinkedList<Operation> OrderedList;
	public File instance;
	String file_saved;
	// Create an array 
	int[] array ;
	public LinkedList<Operation> FullOperationList;
	public LinkedList<Operation> HabOperationList;
	public int auxiliar=0;
	public PrintWriter pwQV;
	public File SolutionFileQV;
	private Hashtable<String, Integer> actions = new Hashtable<String, Integer>();

	
	public QLearning(File[] file, double LearningRate, double DiscountFactor, int cycles, double epsilon) throws FileNotFoundException{
		this.LearningRate = LearningRate;
		this.DiscountFactor = DiscountFactor;
		this.epsilon = epsilon;
		this.iterations = cycles;
		this.filename = file[0].getName();
		this.max_num_operations = 0;	
		OrderedList = new LinkedList<Operation> ();
		FullOperationList = new LinkedList<Operation> ();
		HabOperationList = new LinkedList<Operation> ();
		SolutionFileQV=new File("Solutions/Mine/QV-" + filename + ".txt");
		pwQV = new PrintWriter(SolutionFileQV);
	}
	
	public QLearning(double LearningRate){
		this.LearningRate = LearningRate;

	}
	
	
	public void ReadData(File[] file) throws IOException{
		System.out.println("Name "+file[0].getName().toString()+" "+file[1].getName());
		
		
        String s= new String();
        FileReader f;
	 	
	 	//verify if file[1] is timeRecordings
	 	//File instance2 = file[1];
 		String s1 = new String();
        FileReader f1 = new FileReader(file[1]);
	 	BufferedReader a1 = new BufferedReader(f1);
	 	s = a1.readLine();
	 	if (s.contains("actionDurationTable")) {
	 		 f = new FileReader(file[0]);
	 		instance = file[0];
		}else {
			f = new FileReader(file[1]);
			f1 = new FileReader(file[0]);
		 	a1 = new BufferedReader(f1);
		 	a1.readLine();
		 	instance = file[1];
		}
	 	BufferedReader a = new BufferedReader(f);
	 	//Read First Line
	 	a.readLine();
	 	a.readLine();
	 	a.readLine();
	 	a.readLine();
	 	
	 // operations
	 	s = a.readLine(); 
	 //	int countOperations = s.split(",").length;
	 //	System.out.println(" size "+countOperations);
	 	
	 // Robot left, robot right, operator
	 	a.readLine();
	 	nmachines = 3; 
	 	Machines = new Machine[nmachines];
	 	
	 // products
	 	s = a.readLine(); 
	 	String []cadArray1 = s.split("=");	 	
	 	String []cadArray2 = cadArray1[1].split("[..]+");
	 	String []cadArray = cadArray2[1].split(";");	 
	 	njobs = Integer.parseInt(cadArray[0]);
	 	Jobs = new Job[njobs];	 		
	 	//System.out.println(" size "+cadArray2.length+" "+cadArray1[1].toString());
	 	
	 // zones
	 	s = a.readLine(); 
	 	cadArray1 = s.split("=");	 	
	 	cadArray2 = cadArray1[1].split("[..]+");
	 	cadArray = cadArray2[1].split(";");
	 	countZones = Integer.parseInt(cadArray[0]);
	 	zone = new Zone[countZones];
	 	
	 	a.readLine();
	 	a.readLine();
	 	a.readLine();
	 	a.readLine();
	 	
	 	//System.out.println(" product "+ njobs+" zones "+countZones+" operations "+countOperations);
	 	
	 	//int tmp_zone = 0;
	 	
	 	//count zones
	 	for (int i = 0; i < countZones; i++) {	 	
	 		zone[i] = new Zone(i+1);	
	 		//count products
	 		for (int j = 0; j < njobs; j++) {
	 			if (i == 0) {
	 				Jobs[j] = new Job(j);
	 				Jobs[j].operations = new ArrayList<Operation>();
	 				a1.readLine();// read [ or white space
				 	a1.readLine();// read product
				}
	 			
	 			a.readLine(); //product name
	 			//System.out.println(" zone "+ i);
		 		
	 			//System.out.println(j);
	 			//System.out.println(" product "+zone[i].product.length +" zones "+zone.length+" operations "+zone[i].product[j].operations.length);
	 			
	 		 	//count operation
	 			s = a.readLine();//First operation
	 			int k = 0; //number of operations
	 			char ch1 = ']';
	 			while (!s.equals("") && s.charAt(0) != ch1) {
	 				boolean zone_occupied = false; // value Hashtable
	 		 		
		 			cadArray1 = s.split("%");
		 		 	cadArray = cadArray1[0].split(",");	
		 		 	
		 		 	
		 		 	for (int l = 0; l < 3; l++) {
		 		 		//int job_operation_machine[] = new int[3]; // key Hashtable
		 		 		//job_operation_machine[0] = j; // job
			 		 	//job_operation_machine[1] = k; // operation
		 		 		//job_operation_machine[2] = l; //machine
		 		 		String job_operation_machine = ""+j+k+l;
		 		 		//System.out.println("ppp "+job_operation_machine);
			 		 	zone_occupied = (cadArray[l].equals("0"))?false:true;			 	
			 		 	zone[i].job_operation_occupied.put(job_operation_machine, zone_occupied);//fill in Hashtable
			 		 	//System.out.println(zone[i].job_operation_occupied.get(job_operation_machine));
			 		 	// System.out.println("job "+ j+" operation "+k+" machine "+l+" zone occupied "+ zone_occupied+" boolean ");
					}	 		 	
		 		 	
		 		 	//read times
		 		 	if (i == 0) {
		 		 		s1 = a1.readLine();// read operation
			 		 	cadArray1 = s1.split("[ \t]+");
			 		 	cadArray = cadArray1[0].split(",");
			 		 	ArrayList <Integer> arrayTimes = new ArrayList<Integer>();// fill in with times > 0
			 		 	
			 		 	
			 		 	for (int k2 = 0; k2 < cadArray.length; k2++) {
			 		 		//System.out.println(" cadArray "+ cadArray[k2]);
							if (!cadArray[k2].equals("0")) {
								//System.out.println(" izq "+ cadArray[k2]);
								arrayTimes.add(Integer.parseInt(cadArray[k2]));
							}
			 		 	}
						
			 		 	Jobs[j].operations.add(new Operation(k, j, cadArray1[2]));
			 		 	actions.put(cadArray1[2], k);
			 		 //	System.out.println(" name "+ cadArray1[2]);
			 		 	Jobs[j].operations.get(k).machines = new int[arrayTimes.size()];
			 		 	Jobs[j].operations.get(k).times = new int[arrayTimes.size()];
			 		 	Jobs[j].operations.get(k).QV = new double[arrayTimes.size()];
			 		 	
			 		 	int k2 = 0;
			 		 	for (int l = 0; l < cadArray.length; l++) {
			 		 		if (!cadArray[l].equals("0")){
			 		 			//System.out.println(" down "+ cadArray[l]+" position "+ l);
			 		 			Jobs[j].operations.get(k).machines[k2] = l+1; 
			 		 			Jobs[j].operations.get(k).times[k2] = Integer.parseInt(cadArray[l]);
			 		 			//Initialize the QV
			 		 			Jobs[j].operations.get(k).QV[k2] = 0;
			 		 			k2++;
			 		 		}
						}
					}
		 		 	k++;
		 		 	s = a.readLine();
				}
	 			max_num_operations = (max_num_operations < k)?(k):max_num_operations;
	 		 	
	 		}
	 		//System.out.println(zone[i].job_operation_occupied.size());
	 		a.readLine();
	 		
		}
	 	
	 	for (int j=0; j < nmachines; j++)
	 		Machines[j] = new Machine(j);
	 	//max_num_operations = countOperations;
	 	System.out.println(" [max_num_operations "+ max_num_operations+" njobs "+ njobs);
	 	for (int m=0; m < nmachines; m++)
	 		Machines[m].QValues = new double[njobs][max_num_operations];
	 	
	 	a.readLine(); // reading line finishActionBefore
	 	
	 	s = a.readLine();
	 	char ch1 = '|';
	 	while (s.charAt(0) != ch1) {	 		
	 		cadArray = s.split(",");
	 		//System.out.println(cadArray[0] + " " + cadArray[1] + " size " + cadArray.length);
	 		for (int i = 0; i < Jobs.length; i++) {
				Jobs[i].operations.get(actions.get(cadArray[1])).operation_precedent = actions.get(cadArray[0]);
			}
	 		s = a.readLine();
	 	}
	 	
	 	a.readLine();//reading white space
	 	a.readLine(); // reading line back2back	 	
	 	s = a.readLine();
	 	while (s.charAt(0) != ch1) {	 		
	 		cadArray = s.split(",");
	 		//System.out.println(cadArray[0] + " " + cadArray[1] + " size " + cadArray.length);
	 		for (int i = 0; i < Jobs.length; i++) {
				Jobs[i].operations.get(actions.get(cadArray[1])).back2back_before = actions.get(cadArray[0]);
			}
	 		s = a.readLine();
	 	}
//	 printValues();
	 	a.close();
	 	a1.close();		
	}

	public static double roundToDecimals(double d, int c) {
		int temp=(int)((d*Math.pow(10,c)));
		return (((double)temp)/Math.pow(10,c));
		}
	
	public void SavetoFile(String FileName, int cmax){
		PrintWriter pw;
		
		//String PathSol = "Solutions/Mine/Test.txt";
		String PathSol = FileName;
		File SolutionFile=new File(PathSol);
		try {
			pw = new PrintWriter(SolutionFile);
			//pw.println(instance.getName());
			pw.println(cmax);
			//pw.println(0.0);//tardiness
			pw.println(nmachines);//number of machines
			pw.flush();
			
			for (int m=0; m < njobs; m++)
				for (int n=0; n < Jobs[m].operations.size(); n++){
					pw.print(m +"\t"); //Job ID
					pw.print(n +"\t"); //Oper ID
					pw.write(Jobs[m].operations.get(n).name +"\t"); //Oper name
					pw.write(Jobs[m].operations.get(n).Ma +"\t"); //index of the machine that executed it
					pw.write(Jobs[m].operations.get(n).initial_time +"\t");
					pw.write(Jobs[m].operations.get(n).end_time +"\t");
					pw.write(Jobs[m].operations.get(n).back2back_before +"\t");//operation back to back before
					pw.write(0 +"\n");
					pw.flush();
				}
		pw.close();	
		SaveToTxt();
		}
		catch (FileNotFoundException e) {
			System.err.println("Problem writing to file "+SolutionFile.getAbsolutePath());
			e.printStackTrace();
		}
		
	}
	
	public void SaveToTxt(){
		PrintWriter pw;
		//String PathSol = "Solutions/Mine/Test.txt";
		String PathSol = "Schedule.txt";
		File SolutionFile=new File(PathSol);
		try {
			pw = new PrintWriter(SolutionFile);
			//pw.println(instance.getName());
			
			pw.print("ACTIONS = {");
			System.out.println(actions.size());
			for (int i = 0; i < actions.size(); i++){				
				pw.write(Jobs[0].operations.get(i).name);				
				if(i < actions.size()-1)
					pw.write(", ");
				pw.flush();
			}
			
			pw.println("};");
			pw.println("RESOURCES = {robotL, robotR, operator};");
			pw.print("PRODUCTS = [");
			for (int i = 1; i <= njobs; i++) {
				pw.write(i+" ");
			}						
			pw.println("];");
			pw.flush();
			
			pw.print("actionStartTime = [");
			String end_time = "";
			String duration = "";
			String resource = "";
			for (int m = 0; m < njobs; m++){
				for (int n = 0; n < Jobs[m].operations.size(); n++){
					pw.print( Jobs[m].operations.get(n).initial_time); //initial time
					end_time += Jobs[m].operations.get(n).end_time;
					duration += Jobs[m].operations.get(n).proc_time;
					resource += (Jobs[m].operations.get(n).Ma+1);
					if(n < Jobs[m].operations.size()-1){
						pw.write(", ");
						resource += ", ";
						duration += ", ";
						end_time += ", ";
					}
					
				}
				if(m < Jobs.length-1){
					pw.print(", ");
					resource += ", ";
					duration += ", ";
					end_time += ", ";
				}
				pw.flush();
			}
			pw.println("];");
			pw.println("actionEndTime = ["+end_time+"];");
			pw.println("actionDuration = ["+duration+"];");
			pw.println("selectedResource = ["+resource+"];");
			pw.flush();
			
			pw.print("resourceStartTimes = [");
			for (int i = 0; i < nmachines; i++) {
				//System.out.println(Machines[i].Op_executed.size());
				pw.print(Machines[i].initial_time_machine);
				if(i < nmachines-1)
					pw.write(", ");
				pw.flush();
			}
			pw.println("];");
			
			String resourceDurations = "";
			int cycleTime = 0;
			pw.print("resourceEndTimes = [");
			for (int i = 0; i < nmachines; i++) {
				pw.print(Machines[i].end_time_machine);
				resourceDurations += (Machines[i].end_time_machine-Machines[i].initial_time_machine);
				cycleTime = (cycleTime < (Machines[i].end_time_machine-Machines[i].initial_time_machine))?(Machines[i].end_time_machine-Machines[i].initial_time_machine):cycleTime;
				if(i < nmachines-1) {
					pw.write(", ");
					resourceDurations += ", ";
				}
				pw.flush();
			}
			pw.println("];");
			
			pw.println("resourceDurations = ["+resourceDurations+"];");
			pw.println("obj = "+cycleTime+";");
			pw.println("softConstr = ;");
			pw.println("cycleTime = "+cycleTime+";");
			pw.println("maxDuration = ;");
		pw.close();			
		}
		catch (FileNotFoundException e) {
			System.err.println("Problem writing to file "+SolutionFile.getAbsolutePath());
			e.printStackTrace();
		}
		
	}
	
	public void PrintJob(){
		System.out.println(Jobs[1].GetID());
		System.out.println("Number of operations: " + Jobs[1].operations.size());
		System.out.println("Possibilities Operation 1: ");
		for (int g = 0; g < Jobs[1].operations.get(0).machines.length; g++){
			System.out.print(Jobs[1].operations.get(0).machines[g]+" ");
			System.out.println(Jobs[1].operations.get(0).times[g]);
		}
		
	}
	
	public void PrintRoutes(){
		for(int j=0; j < njobs; j++)
			Jobs[j].PrintRoute();
	}
	

	public void SearchRoutesVersion2(){	
		int j_index=0;
		//Digo que las operaciones iniciales de cada trabajo estan habilitadas
		for(int j=0; j < njobs; j++)
			HabOperationList.add(Jobs[j].operations.get(0));
		
		//Mientras queden operaciones sin maquinas asignadas sigo llenando FullOperationsList 
		//seleccionando una operacion random de las habilitadas
		while (!HabOperationList.isEmpty()){
			//Selecciono una operacion y pongo en habilitadas la proxima de ese Job si es que tiene mas
			
			double rnd = Math.random();
			int index = Math.abs((int) Math.round(rnd * (HabOperationList.size()-1)));
			Operation Oper = HabOperationList.get(index);
			FullOperationList.add(Oper);
			HabOperationList.remove(index);
			
			//si no era la ultima del trabajo
			j_index = Oper.GetJob(); //indice del trabajo de la operacion
			
			int o_index = Oper.GetID(); //indice de la operacion
			for (int i = o_index+1; i < Jobs[j_index].operations.size(); i++) {//por si hay mas de dos op seguidas en un mismo job
				//if (Jobs[j_index].operations.length-1 != o_index){
					if (Jobs[j_index].operations.get(i).back2back_before == o_index) {
						 Oper = Jobs[j_index].operations.get(i);
						 FullOperationList.add(Oper);
						 o_index = Oper.GetID();
					}else {
						if (i == Jobs[j_index].operations.size()-1) {
							HabOperationList.add(Jobs[j_index].operations.get(o_index+1));
						}
						
					}
				//}
			}
			
		}
		
//		//Print FullOperList
//		for (int h=0; h < FullOperationList.size(); h++){
//			Operation Ope = FullOperationList.get(h);
//			System.out.println("Oper " + Ope.GetID()+" Job " + Ope.GetJob());
//		}
			
		
		//Ahora para cada operacion en FullOper... seleccionar una maquina
		for (int f=0; f < FullOperationList.size(); f++){
			Operation Ope = FullOperationList.get(f);
			//System.out.println("aux_end "+Jobs[Ope.GetJob()].aux_end);
			int m = Ope.MachineSelection(epsilon, Machines, Jobs[Ope.GetJob()].aux_end,Jobs[Ope.GetJob()].operations);
			Ope.Ma = Ope.machines[m]-1;
			Ope.M = Machines[Ope.machines[m]-1];
			Ope.index_Ma = m;
			Machines[Ope.machines[m]-1].work += Ope.proc_time;
			Machines[Ope.machines[m]-1].Op_assigned.add(Ope);
			//JobEndTimes[j_index]=Ope.end_time;
			Jobs[Ope.GetJob()].aux_end = Ope.end_time;
			//System.out.println("job "+Ope.GetJob()+" op "+Ope.GetID()+" maq "+Ope.M.GetID()+" Ma "+Ope.Ma+" end time " +Ope.end_time);
			
		}

//		for (int jo=0; jo < njobs; jo++)
//			System.out.print(Jobs[jo].aux_end+"	");
		
//		System.out.println();
	}
	
	public void Initialize(){
		
		for (int j=0; j < njobs; j++) {			
			Jobs[j].Start(Machines);					
		}
	}
	
	public boolean AllJobsFinished(Job[] Jobs){
		boolean flag = true;
		
		for (int b=0; b < njobs ;b++)
			if (!Jobs[b].finished)
				return flag = false;
		
		return flag;
	}
	
	
	public void ProcessNonDelay(double alpha, double gamma, int iter) throws CloneNotSupportedException{		
		boolean finished = false;
		Operation op_selected = null;
		Operation last_op = null;
		LinkedList<Machine> Working = new LinkedList<Machine> ();		
		//int counter = 0;
		
		while(!finished){
			for (int m=0; m < nmachines; m++){
				if (!Machines[m].Queue.isEmpty()){
					Working.add(Machines[m]);

					op_selected = Machines[m].ActionSelection(Jobs, epsilon, LearningRate, pwQV, zone);

						
					Machines[m].Queue.remove(op_selected);
					Machines[m].Op_executed.add(op_selected);
					op_selected.end_time = op_selected.initial_time + op_selected.proc_time;
					//System.out.println("Select job "+op_selected.GetJob()+" op "+op_selected.GetID()+" maq "+op_selected.M.GetID()+" Ma "+op_selected.Ma+" initial time "+op_selected.initial_time );
					//a su trabajo actualizarle el end_time y mandar la otra
					Jobs[op_selected.GetJob()].j_end_time = op_selected.end_time;
					Jobs[op_selected.GetJob()].time_remaining = Jobs[op_selected.GetJob()].time_remaining - op_selected.proc_time;
					Machines[m].initial_time_machine = Machines[m].Op_executed.getFirst().initial_time;
					Machines[m].end_time_machine = Machines[m].Op_executed.getLast().end_time;
				}
			}
			
			for (int n=0; n < Working.size(); n++){
				last_op = Working.get(n).Op_executed.getLast();
				//last_op = Machines[n].Op_executed.getLast();
				if (last_op.GetID() < Jobs[last_op.GetJob()].operations.size()-1)
					 Jobs[last_op.GetJob()].SendNext(last_op.GetID(), Machines);
					else
						 Jobs[last_op.GetJob()].finished = true;
			}
						
			Working.clear();
			//////////////
			
			if (AllJobsFinished(Jobs))
				finished = true;
			
			//System.out.println("...."+Machines[0].Op_executed.size());
		}
		
		
		
	}
	
	
	public double GetMaxNextQV(Operation op){
		double max=0, ch2;		
		//buscar el maximo entre las operaciones que se quedan en esta cola y las de la cola de la proxima op de ese job
		double ch1 = Machines[op.Ma].MaxQVQueue();
		//chequear que no sea la ultima operacion de ese Job
		if (op.GetID() < Jobs[op.GetJob()].operations.size()-1)
			ch2 = Jobs[op.GetJob()].operations.get(op.GetID()+1).M.MaxQVQueue();
		else 
			ch2 =0;
		
		max = ( ch1 > ch2) ? ch1 : ch2;
		
		return max;
	}
	
	
	public double GetMinNextQV(Operation op){
		double min=0, ch2;		
		//buscar el maximo entre las operaciones que se quedan en esta cola y las de la cola de la proxima op de ese job
		double ch1 = Machines[op.Ma].MinQVQueue();
		//chequear que no sea la ultima operacion de ese Job
		if (op.GetID() < Jobs[op.GetJob()].operations.size()-1)
			ch2 = Jobs[op.GetJob()].operations.get(op.GetID()+1).M.MinQVQueue();
		else 
			ch2 =0;
		
		min = ( ch1 < ch2) ? ch1 : ch2;
		
		return min;
	}
	
	
	public void UpdateQV(Operation op, double temp, double alpha, double gamma, int R01){
		double R = (double) 1/op.proc_time;
//		double WR = Jobs[op.GetJob()].time_remaining;
		//double aux = Machines[op.Ma].QValues[op.GetJob()][op.GetID()];
		//Machines[op.Ma].QValues[op.GetJob()][op.GetID()] = aux + alpha * (R + gamma * temp);
		//Machines[op.Ma].QValues[op.GetJob()][op.GetID()] += alpha *(R01 - Machines[op.Ma].QValues[op.GetJob()][op.GetID()]);
		Machines[op.Ma].QValues[op.GetJob()][op.GetID()] += alpha *(R - Machines[op.Ma].QValues[op.GetJob()][op.GetID()]);
		//Machines[op.Ma].QValues[op.GetJob()][op.GetID()] += alpha * (R + gamma * temp - Machines[op.Ma].QValues[op.GetJob()][op.GetID()]);
	}
	
	public void UpdateQVGlobal(Operation op, double alpha, double gamma, double temp, int cmax){
	//	double R = (double) 1/cmax;
		double aux = Machines[op.Ma].QValues[op.GetJob()][op.GetID()];
		
		
//		Machines[op.Ma].QValues[op.GetJob()][op.GetID()] = aux + alpha * (R + gamma * temp - aux);
		
		//Machines[op.Ma].QValues[op.GetJob()][op.GetID()] = aux + alpha * (R - aux);
		
		//cuando es 0 o 1
		Machines[op.Ma].QValues[op.GetJob()][op.GetID()] = aux + alpha * (cmax + gamma * temp - aux);
		
		//Machines[op.Ma].QValues[op.GetJob()][op.GetID()] += alpha * (1/op.proc_time + gamma * temp - Machines[op.Ma].QValues[op.GetJob()][op.GetID()]);
	}
	
	
	public double MostWorkRemaining(Operation op){
		return 0;
	}
	
		
	public int CalculateCmax(Job[] UJobs){
		int cmax = UJobs[0].j_end_time;
		
		for (int j=1; j<njobs; j++)
			if (UJobs[j].j_end_time > cmax)
				cmax = UJobs[j].j_end_time;
		
		return cmax;
	}
	
		
	public void PrintInitialQueues(){
		for (int m=0; m < nmachines; m++)
			if (!Machines[m].Queue.isEmpty())
				Machines[m].PrintQueue();
			//System.out.println(Machines[m].Queue.getFirst().GetID());
	}
	
	public void PrintValues(){
		System.out.println("My Learning Rate is: " + LearningRate);
		System.out.println("My Discount Factor is: " + DiscountFactor);
		System.out.println("My Epsilon is: " + epsilon);
		System.out.println("Number of iterations: " + iterations);
		System.out.println("The instance is: " + filename);
		System.out.println("Number of jobs: " + njobs);
		System.out.println("Number of machines: " + nmachines);
		System.out.println("Average number of machines per operation: " + navg_op);
	}

		
	public void RestartTimesForOnce(){
		//restart machines' times
		for (int x=0; x<nmachines; x++){
			Machines[x].time = 0;
			Machines[x].TempOrderedList.clear();
			//Machines[x].Op_assigned.clear();
			Machines[x].minInitialM = 0;
		}
		
		//restart jobs' times and each job restarts its operations' times
		for (int j=0; j<njobs; j++){
			Jobs[j].aux_end = 0;
			Jobs[j].j_end_time =0;
			Jobs[j].finished =  false;
			for (int o=0; o<Jobs[j].operations.size(); o++){
				Jobs[j].operations.get(o).initial_time = 0;
				Jobs[j].operations.get(o).end_time = 0;
				Jobs[j].operations.get(o).temp_end = 0;
			//	Jobs[j].operations[o].proc_time = 0;
			}
				
		}
		for (int i = 0; i < zone.length; i++) {
			zone[i].time = 0;
		}
		//FullOperationList.clear();
	}
	
		
	public void RestartTimes(){	
		//restart machines' times
		for (int x=0; x<nmachines; x++){
			Machines[x].time = 0;
			Machines[x].TempOrderedList.clear();
			Machines[x].Op_assigned.clear();
			Machines[x].minInitialM = 0;
		}
		
		//restart jobs' times and each job restarts its operations' times
		for (int j=0; j<njobs; j++){
			Jobs[j].aux_end = 0;
			Jobs[j].j_end_time =0;
			Jobs[j].finished =  false;
			for (int o=0; o<Jobs[j].operations.size(); o++){
				Jobs[j].operations.get(o).initial_time = 0;
				Jobs[j].operations.get(o).end_time = 0;
				Jobs[j].operations.get(o).temp_end = 0;
				Jobs[j].operations.get(o).proc_time = 0;
			}
				
		}
		FullOperationList.clear();
	}

	
	public void PrintOrders(){
		for (int m=0; m<nmachines; m++)
			Machines[m].PrintOrder();
	}
	
	
	public void Locate_Op_OrderedList(Operation op){
		boolean located = false;
		int i=0;
		while (!located){
			// if the List is still empty or this value is higher than the highest so far, then add it
			if ((OrderedList.isEmpty()) || (OrderedList.getLast().end_time > op.end_time)){  
//			if (OrderedList.isEmpty()) {  
				OrderedList.add(op);
				located = true;
			}
			else{
				if (op.end_time < OrderedList.get(i).end_time)
					i++;
				else{
					OrderedList.add(i, op);
					located = true;
				}
			}
		}
	}	
	
	public void PrintQValues(){
		for(int q=0; q < njobs; q++)
			Jobs[q].PrintQV();
	}
	
	public void PrintQValuesMachines(){
		for(int q=0; q < nmachines; q++)
			Machines[q].PrintQValues();
	}
	
	public int[] CheckAvailability(Machine M, int time_slot, int min_posible_start){ //chequear en las operaciones asignadas los tiempos ocupados
		int [] resultado = new int[2];
		boolean located = false;
		//si esta vacia es 0 todo
		//if ((M.Op_executed_Optim.size()==0)||(M.Op_executed_Optim.getFirst().initial_time >= time_slot)){
		if ((M.Op_executed_Optim.isEmpty())||
				((M.Op_executed_Optim.getFirst().initial_time >= time_slot) && (M.Op_executed_Optim.getFirst().initial_time >= min_posible_start + time_slot))){
			resultado[0] = 0;
			resultado[1] = 0;
			located = true;
		}
		else{
			for (int i=0; i < M.Op_executed_Optim.size()-1; i++){
			  int aux = M.Op_executed_Optim.get(i+1).initial_time - M.Op_executed_Optim.get(i).end_time;
			//si la dif entre en inicial de una y el final de la otra es de tama–o del slot necesario entonces devuelvo pos y tiempo inicial
				//if (aux >= time_slot) {
				if ((aux >= time_slot) && (M.Op_executed_Optim.get(i).end_time >= min_posible_start)){
					//it fits here
					resultado[0] = i+1;
					resultado[1] = M.Op_executed_Optim.get(i).end_time;
					located = true;
				}
			}
		}
	
		if (!located){
			resultado[0] = M.Op_executed_Optim.size();
			resultado[1] = M.Op_executed_Optim.getLast().end_time;
		}		
		return resultado;
	}
	
	
	public void ClearTimesOpt(){		
		for (int j=0; j < njobs; j++)
			Jobs[j].temp_endtime=0;
		
		OrderedList.clear();
		
		for (int m=0; m < nmachines; m++)
			Machines[m].Op_executed_Optim.clear();
		
		for(int m=0; m < zone.length; m++)
			zone[m].time = 0;
	}
	
	
	
	public void GetBackwardForward(){
		
		//Finding slots...
		for (int t=0; t < OrderedList.size(); t++){
			//int min_initial_j = 0;
			int min_initial_j = Jobs[OrderedList.get(t).GetJob()].temp_endtime;
		//	boolean zone_occupied = false;
		//	ArrayList<Integer> arrayZone = new ArrayList<Integer>();
			//int timeZone = 0;
			//zone
		/*	String array = ""+OrderedList.get(t).GetJob()+ OrderedList.get(t).GetID()+(OrderedList.get(t).machines[0]-1);			
			String finalArray = ""+OrderedList.get(t).GetJob()+ OrderedList.get(t).GetID()+(OrderedList.get(t).machines[0]-1);
			//buscar la zona					
			for (int i = 0; i < zone.length; i++){
				if (zone[i].job_operation_occupied.get(array).equals(true)){				
					if (min_initial_j < zone[i].time) 
						min_initial_j = zone[i].time;					
				}
			}*/
			
			//int min_initial_j = Jobs[OrderedList.get(t).GetJob()].temp_endtime;
			//CheckAvailability on the first machine, and this gives a possible initial time on the machine
			int[] data = CheckAvailability(Machines[OrderedList.get(t).machines[0]-1], OrderedList.get(t).times[0], min_initial_j);
			int index = data[0]; int min_initial_m = data[1];
			int initial = (min_initial_j > min_initial_m) ? min_initial_j : min_initial_m;
			int min_initial = initial;
			int min_end = initial + OrderedList.get(t).times[0];
			OrderedList.get(t).temp_index = 0;
			OrderedList.get(t).index_Ma = 0;
			
			
			for (int p=1; p < OrderedList.get(t).machines.length; p++){
			//	array = ""+OrderedList.get(t).GetJob()+ OrderedList.get(t).GetID()+(OrderedList.get(t).machines[p]-1);			
				//buscar la zona y ver precedencia		
				//arrayZone.clear();
			/*	for (int i = 0; i < zone.length; i++){
					if (zone[i].job_operation_occupied.get(array).equals(true)){
						if (min_initial_j < zone[i].time) {
							min_initial_j = zone[i].time;
						}
						//System.out.println("ocupa la zona "+(i+1)+" time zone "+zone[i].time+" nuevo time "+timeZone);
					}
				}*/
				data = CheckAvailability(Machines[OrderedList.get(t).machines[p]-1], OrderedList.get(t).times[p], min_initial_j);
				min_initial_m = data[1];
				initial = (min_initial_j > min_initial_m) ? min_initial_j : min_initial_m;
				if (initial + OrderedList.get(t).times[p] < min_end){	
					index = data[0]; 
					min_initial = initial;
					min_end = initial + OrderedList.get(t).times[p];
					OrderedList.get(t).temp_index = p;
					OrderedList.get(t).index_Ma = p;
				//	finalArray = ""+OrderedList.get(t).GetJob()+ OrderedList.get(t).GetID()+(OrderedList.get(t).machines[p]-1); 
				}
			}
			//Jobs[OrderedList.get(t).GetJob()].temp_endtime += OrderedList.get(t).times[OrderedList.get(t).temp_index];
			Jobs[OrderedList.get(t).GetJob()].temp_endtime = min_end;
			OrderedList.get(t).initial_time = min_initial;
			OrderedList.get(t).end_time = min_end;
			OrderedList.get(t).M = Machines[OrderedList.get(t).machines[OrderedList.get(t).temp_index]-1];
			OrderedList.get(t).Ma = OrderedList.get(t).machines[OrderedList.get(t).temp_index]-1;
			
			//insertar con posicion donde va...
			if (index==0)
				Machines[OrderedList.get(t).machines[OrderedList.get(t).temp_index]-1].Op_executed_Optim.addFirst(OrderedList.get(t));
			else{
				//Machines[OrderedList.get(t).temp_index].Op_executed_Optim.add(index, OrderedList.get(t));
				if (index==Machines[OrderedList.get(t).machines[OrderedList.get(t).temp_index]-1].Op_executed_Optim.size())
					Machines[OrderedList.get(t).machines[OrderedList.get(t).temp_index]-1].Op_executed_Optim.addLast(OrderedList.get(t));
				else
				Machines[OrderedList.get(t).machines[OrderedList.get(t).temp_index]-1].Op_executed_Optim.add(index, OrderedList.get(t));
			}
			
			//update time of zones
		/*	for (int i = 0; i < zone.length; i++)
				if (zone[i].job_operation_occupied.get(finalArray).equals(true))			
					zone[i].time = min_end;			*/			
				
		}
		
		//Print this step
//		for (int i=0; i<OrderedList.size(); i++){
//			System.out.print("J"+OrderedList.get(i).GetJob()+"O"+OrderedList.get(i).GetID()+" Prefers M" + OrderedList.get(i).machines[OrderedList.get(i).temp_index]);
//			System.out.println(" From "+OrderedList.get(i).initial_time + " To " + OrderedList.get(i).end_time);
//		}
		
	}
	
	public void Order(){
		for(int x=0; x < njobs; x++)
			for (int y=0; y < Jobs[x].operations.size(); y++)
				Locate_Op_OrderedList(Jobs[x].operations.get(y));
	
//		for (int i=0; i<OrderedList.size(); i++)
//			System.out.println("J"+OrderedList.get(i).GetJob()+"O"+OrderedList.get(i).GetID());
	}
	
	
	public void ExecuteModeOptimization(){
		//order the operations by end-time (the highest go first)
		Order();
		GetBackwardForward();
		ClearTimesOpt();
		Order();
		GetBackwardForward2();
		/*for (int j=0; j < njobs; j++)
			Jobs[j].j_end_time = Jobs[j].operations.get(Jobs[j].operations.size()-1).end_time;*/
		//ComputeMakespan
	}
	
	
	private void GetBackwardForward2() {
		// TODO Auto-generated method stub
		//Finding slots...
	//	System.out.println("________________");
				for (int t=0; t < OrderedList.size(); t++){
					int min_initial_j=0,min_end = 0,min_initial=0,index  =0;
					//ArrayList<Integer> arrayZone = new ArrayList<Integer>();				
					String array = "";
					String finalArray = "";
					//System.out.println("job "+OrderedList.get(t).GetJob()+" op "+OrderedList.get(t).GetID());
					if (OrderedList.get(t).back2back_before>-1) {						
						int beforeMachine = Jobs[OrderedList.get(t).GetJob()].operations.get(OrderedList.get(t).back2back_before).Ma;
						int indexTime =0;
						array = ""+OrderedList.get(t).GetJob()+ OrderedList.get(t).GetID()+beforeMachine;
						finalArray = ""+OrderedList.get(t).GetJob()+ OrderedList.get(t).GetID()+beforeMachine;
						//search zones				
						for (int i = 0; i < zone.length; i++){
							if (zone[i].job_operation_occupied.get(array).equals(true)){							
								if (min_initial_j < zone[i].time)
									min_initial_j = zone[i].time;
							}
						}
						
						for (int i = 0; i < OrderedList.get(t).times.length; i++) {
							if (OrderedList.get(t).machines[i]-1 == beforeMachine) {
								indexTime = i;
							}
						}
						//CheckAvailability on the first machine, and this gives a possible initial time on the machine
						int[] data = CheckAvailability(Machines[beforeMachine], OrderedList.get(t).times[indexTime], min_initial_j);
						index = data[0]; int min_initial_m = data[1];
						int initial = (min_initial_j > min_initial_m) ? min_initial_j : min_initial_m;
						min_initial = initial;
						OrderedList.get(t).index_Ma = indexTime;
						min_end = initial + OrderedList.get(t).times[OrderedList.get(t).index_Ma];
						OrderedList.get(t).temp_index = OrderedList.get(t).index_Ma;
					}else{
						array = ""+OrderedList.get(t).GetJob()+ OrderedList.get(t).GetID()+(OrderedList.get(t).machines[0]-1);			
						finalArray = ""+OrderedList.get(t).GetJob()+ OrderedList.get(t).GetID()+(OrderedList.get(t).machines[0]-1);
						//search zones				
						for (int i = 0; i < zone.length; i++){
							if (zone[i].job_operation_occupied.get(array).equals(true)){							
								if (min_initial_j < zone[i].time)
									min_initial_j = zone[i].time;								
							}
						}
					//	min_initial_j = Jobs[OrderedList.get(t).GetJob()].temp_endtime;
						//CheckAvailability on the first machine, and this gives a possible initial time on the machine
						int[] data = CheckAvailability(Machines[OrderedList.get(t).machines[0]-1], OrderedList.get(t).times[0], min_initial_j);
						index = data[0]; int min_initial_m = data[1];
						int initial = (min_initial_j > min_initial_m) ? min_initial_j : min_initial_m;
						min_initial = initial;
						min_end = initial + OrderedList.get(t).times[0];
						OrderedList.get(t).temp_index = 0;
						OrderedList.get(t).index_Ma = 0;					
						
						for (int p=1; p < OrderedList.get(t).machines.length; p++){
							array = ""+OrderedList.get(t).GetJob()+ OrderedList.get(t).GetID()+(OrderedList.get(t).machines[p]-1);			
							min_initial_j = 0;
							//search zone						
							for (int i = 0; i < zone.length; i++){
								if (zone[i].job_operation_occupied.get(array).equals(true)){								
									if (min_initial_j < zone[i].time)
										min_initial_j = zone[i].time;
								}
							}
							data = CheckAvailability(Machines[OrderedList.get(t).machines[p]-1], OrderedList.get(t).times[p], min_initial_j);
							min_initial_m = data[1];
							initial = (min_initial_j > min_initial_m) ? min_initial_j : min_initial_m;
							if (initial + OrderedList.get(t).times[p] < min_end){	
								index = data[0]; 
								min_initial = initial;
								min_end = initial + OrderedList.get(t).times[p];
								OrderedList.get(t).temp_index = p;
								OrderedList.get(t).index_Ma = p;
								finalArray = ""+OrderedList.get(t).GetJob()+ OrderedList.get(t).GetID()+(OrderedList.get(t).machines[p]-1);
							}
						}
						//System.out.println("min_initial "+min_initial+" min_end "+min_end+" index_Ma "+ OrderedList.get(t).index_Ma+" maq "+Machines[OrderedList.get(t).Ma].GetID()+" Ma "+OrderedList.get(t).Ma+" index "+index);
					}
					
					//Jobs[OrderedList.get(t).GetJob()].temp_endtime += OrderedList.get(t).times[OrderedList.get(t).temp_index];
					Jobs[OrderedList.get(t).GetJob()].temp_endtime = min_end;
					OrderedList.get(t).initial_time = min_initial;
					OrderedList.get(t).end_time = min_end;
					OrderedList.get(t).M = Machines[OrderedList.get(t).machines[OrderedList.get(t).temp_index]-1];
					OrderedList.get(t).Ma = OrderedList.get(t).machines[OrderedList.get(t).temp_index]-1;
					OrderedList.get(t).proc_time = OrderedList.get(t).times[OrderedList.get(t).index_Ma];
				//	System.out.println("Job "+ OrderedList.get(t).GetJob()+" op "+ OrderedList.get(t).GetID()+" min_initial "+min_initial+" min_end "+min_end+" index_Ma "+ OrderedList.get(t).index_Ma+" maq "+Machines[OrderedList.get(t).Ma].GetID()+" Ma "+OrderedList.get(t).Ma+" time "+OrderedList.get(t).times[OrderedList.get(t).index_Ma]);
					//System.out.println("min_initial "+OrderedList.get(t).initial_time+" min_end "+OrderedList.get(t).end_time+" index_Ma "+ OrderedList.get(t).index_Ma+" maq "+Machines[OrderedList.get(t).Ma].GetID()+" Ma "+OrderedList.get(t).Ma+" index "+index);
					//insertar con posicion donde va...
					if (index==0)
						Machines[OrderedList.get(t).machines[OrderedList.get(t).temp_index]-1].Op_executed_Optim.addFirst(OrderedList.get(t));
					else{
						//Machines[OrderedList.get(t).temp_index].Op_executed_Optim.add(index, OrderedList.get(t));
						if (index==Machines[OrderedList.get(t).machines[OrderedList.get(t).temp_index]-1].Op_executed_Optim.size())
							Machines[OrderedList.get(t).machines[OrderedList.get(t).temp_index]-1].Op_executed_Optim.addLast(OrderedList.get(t));
						else{
						//	System.out.println("size "+Machines[OrderedList.get(t).machines[OrderedList.get(t).temp_index]-1].Op_executed_Optim.size()+" index "+index);
							Machines[OrderedList.get(t).machines[OrderedList.get(t).temp_index]-1].Op_executed_Optim.add(index, OrderedList.get(t));
						}
							
					}
					
					//update time of zones
					for (int i = 0; i < zone.length; i++)
						if (zone[i].job_operation_occupied.get(finalArray).equals(true))			
							zone[i].time = min_end;	
				}
				
				//Print this step
//				for (int i=0; i<OrderedList.size(); i++){
//					System.out.print("J"+OrderedList.get(i).GetJob()+"O"+OrderedList.get(i).GetID()+" Prefers M" + OrderedList.get(i).machines[OrderedList.get(i).temp_index]);
//					System.out.println(" From "+OrderedList.get(i).initial_time + " To " + OrderedList.get(i).end_time);
//				}
	}

	public void UpdateQValuesProcedure(double alpha, double gamma, int R){
		
		//ACTUALIZAR LOS Q-VALUES DE LA 1era ITERACION CON EL CMAX OBTENIDO
		for (int a=0; a < njobs; a++){
			for (int b=0; b < Jobs[a].operations.size(); b++)
				Jobs[a].operations.get(b).UpdateQVGlobal(alpha, R);	
		//System.out.println();
		}		
		//ACTUALIZAR LOS Q-VALUES DE LA 2da ITERACION CON EL SPT 
		for (int c=0; c<njobs; c++)
			for (int d=0; d < Jobs[c].operations.size(); d++){
				double temp = GetMaxNextQV(Jobs[c].operations.get(d));
				UpdateQV(Jobs[c].operations.get(d), temp, alpha, gamma, R);
		}
						
	}
	
	public void Execute(double alpha, double gamma) throws FileNotFoundException, CloneNotSupportedException{
		Date date = new Date();
		long initial = date.getTime();
		int R;
		//boolean improvement = true;
		//int counter = 0;
//		for (int it = 0; it < 1000; it++){
//			SearchRoutes();
////			PrintRoutes();
//		}
		
//		SearchRoutes();
		
//		SetRoutes();
//		PrintRoutes();
//		SelectQV();
//		PrintRoutes();
//		PrintQValues();
		
//		CalculateJobsFullTimes();
		file_saved = "Solutions/Mine/Solution-" + filename + ".txt";
		SearchRoutesVersion2();
		
		for (int n = 0; n < this.iterations; n++){
			System.out.println("ooooo");
			//PrintRoutes();
			//improvement = true;		
			R=0;
//			RestartTimes();
			ExecuteModeOptimization();
			RestartTimesForOnce();//quitar si no se hace backwardforward
			//SearchRoutes();
//			SearchRoutesVersion2();
			//PrintRoutes();		
			//
			Initialize(); //Send job to the first machine
			//ProcessWithDelay();
			pwQV.println("Iteration " + n);
			ProcessNonDelay(alpha, gamma, n);
			Cmax = CalculateCmax(Jobs);
			
			pwQV.println("makespan-iteration " + n + ": " + Cmax);
			pwQV.println();
			pwQV.flush();
					
			//Variante 2 ModeOptimization
			if (n==0) { 
				BestSol = Cmax; 
				SavetoFile(file_saved, BestSol);
			}
			if (Cmax < BestSol){
				System.out.println("encontre mejor sol");
				BestSol = Cmax;
				SavetoFile(file_saved, BestSol);
				R=1;
					//UpdateQValuesProcedure(alpha, gamma, R);
			}else{
				if(Cmax==BestSol)
					R=1;
				else
					R=0;
			}
			 UpdateQValuesProcedure(alpha, gamma, R);
			 ClearTimesOpt();
		//	}
			
		/*	 while (improvement){
				ExecuteModeOptimization();
				RestartTimesForOnce();
				Initialize();
				//System.out.println("Time zone "+zone[0].time+" time job 1 "+Jobs[0].j_end_time+" machine "+Machines[2].time);
				ProcessNonDelay(alpha, gamma, n);
				int CmaxOpt = CalculateCmax(Jobs);
				int CmaxGlob = (Cmax < CmaxOpt) ? Cmax : CmaxOpt;
				if (CmaxGlob < BestSol){
					System.out.println("encontre mejor sol");
					BestSol = CmaxGlob;
					SavetoFile(file_saved, BestSol);
					R=1;
					//UpdateQValuesProcedure(alpha, gamma, R);
				}
				else{
					improvement = false;
					if (CmaxOpt==BestSol) R=1;
					else R=0;
				}
			 UpdateQValuesProcedure(alpha, gamma, R);
			 ClearTimesOpt();
			}*/
			
			 System.out.println("Time cycle "+Cmax);
			RestartTimesForOnce();
		}
		
		
		System.out.println("The makespan is: "+BestSol);		
				
		Instance instance;
		try {
			//System.out.println(file_saved.toString());
			instance = new Instance("Schedule", 55, file_saved,nmachines);
			Pair<Instance,Schedule> result = GUI.Test.loadSchedule(instance);
			List<Schedule> s = new ArrayList<Schedule>(); 
			ArrayList<OperationAllocation> newAllocs = new ArrayList<OperationAllocation>();
			for (int i=0; i < result.getSecond().getAllocations().size(); i++) {
				OperationGUI operation = result.getSecond().getAllocations().get(i).getOperation();
				MachineGUI machine = result.getSecond().getAllocations().get(i).getMachine();
				int startTime = result.getSecond().getAllocations().get(i).getStartTime();
				int endTime = result.getSecond().getAllocations().get(i).getEndTime();
				boolean border = result.getSecond().getAllocations().get(i).getBorder();
				
				newAllocs.add(new OperationAllocation(operation, machine, startTime, endTime,border));
				
			}
			
			Schedule otro = new Schedule (newAllocs);	

			s.add(otro);
			new File(file_saved);
			
			//System.out.println("The fixBton size is: "+fixBoton.size());
			ScheduleFrame sf = new ScheduleFrame(result.getFirst(), result.getSecond()," Optimized using Q-Learning");
			//sf.saveSchedule(filename);
			sf.setVisible(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		// end new add
		
		Date date1 = new Date();
		long fin = date1.getTime();
		System.out.println((fin-initial)+" Milliseconds...");
		System.out.println((fin - initial) / 1000+"."+(fin - initial) % 1000+" Seconds...");
		System.out.println("----------------");
		
	}
		
}
