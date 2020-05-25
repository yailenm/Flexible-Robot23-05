package GUI;

import java.util.List;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.Arrays;
import java.util.Vector;

/**
 * This class represents an instance of the Hybrid Flexible Flowline Scheduling Problem. The file
 * may begin with a number of comment lines, starting with a pound sign (#).
 * 
 * @author bert
 */
public class HFFSPInstance {
    
    //---------- Member variables -----------------------------------------------------------------
	
	/** used internally to read the file. */
	StreamTokenizer tokenizer;
	
	/** Name of this instance, taken from the file name. */
	private final String name;
	
    // Instance characteristics
	/** The number of machines. */
    public final int numMachines;          // HFFSPInstance
    
    /** The number of jobs. */
    public final int numJobs;
    
    /** The number of stages. */
    public final int numStages;
    
    /** The number of machines per stage. */
    private final int[] numMachinesPerStage;
    
    /** Processing times of jobs on machines. The first index denotes the job id, the second the machine id. */ 
    private final int[][] processingTimes;  // Job
    
    /** Visited states for jobs. The first index is the job id, the second the stage number. */
    private final int[][] stagesVisited;    // Job
    
    /** Eligible machines. The first index is the job ID, the second the machine id. */
    private final int[][] eligibleMachines; // Job
    
    /** Release times. Indexes are machine ID's. */
    private final int[] releaseTimes;       // Machine
    
    /** Predecessors of jobs. First index is job id. */
    private final int[][] predecessors;     // Job
    
    /** Sequence dependent setup times. */
    private final int[][][] setupTimes;     // Machine? or HFFSPInstance?
    
    /** Anticipatory flags for setup times. */
    private final int[][][] anticipatoryFlags; // Machine? or HFFSPInstance?
    
    /** 
     * Lags. First index is job number, second is machine number. There are lag values for every
     * machine <em>not</em> in the last stage.
     */
    private final int[][] lags;            // Job
    
    /** Machines in this instance. */
    private final MachineGUI[] machines;
    
    /** Jobs to be processed in this instance. */
    private final JobGUI[] jobs;
    
    //---------- Construction ---------------------------------------------------------------------

    /**
     * Creates a HFFSPInstance object from the file with the specified name.
     * @param fileName     the name of the file containing the HFFSP instance definition
     * @throws IOException when the file was not found, unaccessible, or not of the expected format.
     */
    public HFFSPInstance(String fileName) throws IOException {
        this(new File(fileName));
    }

    /**
     * Creates a HFFSPInstance object from the specified file.
     * @param file         the file containing the HFFSP instance definition
     * @throws IOException when the file was not found, unaccessible, or not of the expected format.
     */
    public HFFSPInstance(File file) throws IOException {
    	tokenizer = new StreamTokenizer(new FileReader(file));
        tokenizer.eolIsSignificant(true);
        tokenizer.commentChar('#');
        
        name = file.getParent() + File.separator + file.getName();
        
        // Preamble
        numJobs = readInt();
        numMachines = readInt();
        numStages = readInt();
        skipEOL();
        numMachinesPerStage = new int[numStages];
        for (int i = 0; i < numMachinesPerStage.length; i++) {
            numMachinesPerStage[i] = readInt();
        }
        skipEOL();
        
        // Processing times:
        processingTimes = readIntMatrix(numJobs, numMachines, true);
        
//        System.out.println("PROCESSING TIMES");
//        for (int i = 0; i < processingTimes.length; i++) {
//            System.out.println(Arrays.toString(processingTimes[i]));
//        }
        
        // Read stages
        skipWord("STAGES"); skipEOL();
        stagesVisited = readIntMatrix(numJobs, numStages);
        
        // Eligible machines
        skipWord("ELEG"); skipEOL();
        eligibleMachines = readIntMatrix(numJobs, numMachines);
        
        // Release Times
        skipWord("RELMACHINE"); skipEOL();
        tokenizer.eolIsSignificant(false);
        releaseTimes = new int[numMachines];
        for (int mach = 0; mach < numMachines; mach++) {
			releaseTimes[mach] = readInt();
		}
        tokenizer.eolIsSignificant(true);
        skipEOL();
//        System.out.println("Release times " + Arrays.toString(releaseTimes));
        
        // Predecessors
        skipWord("PREC"); skipEOL();
        predecessors = new int[numJobs][];
//        System.out.println("Predecessors");
        for (int job = 0; job < numJobs; job++) {
			predecessors[job] = decrement(readIntList());
			
//			System.out.println(Arrays.toString(predecessors[job]));
		}
        
        // Sequence Dependent Setup Times
        skipWord("SSD"); skipEOL();
        setupTimes = new int[numMachines][][];
        for (int mach = 0; mach < numMachines; mach++) {
			skipWord("M" + mach); skipEOL();
			setupTimes[mach] = readIntMatrix(numJobs, numJobs);
		}
        
        // Anticipatory flags
        skipWord("ANTICIPATIVE"); skipEOL();
        anticipatoryFlags = new int[numMachines][][];
        for (int mach = 0; mach < numMachines; mach++) {
			skipWord("M" + mach); skipEOL();
			anticipatoryFlags[mach] = readIntMatrix(numJobs, numJobs);
		}
        
        // Lags
        skipWord("LAGS"); skipEOL();
        // There are lag values for each job and each machine NOT in the final stage
        int numLags = numMachines - getNumMachinesAtStage(numStages-1);
        lags = readIntMatrix(numJobs, numLags);
        
        // Create machine objects
        machines = new MachineGUI[numMachines];
        int curStage = 0;
        int machineCount = 0;
    	for (int i = 0; i < machines.length; i++) {
			machines[i] = new MachineGUI(i, curStage, releaseTimes[i]);
			machineCount++;
			if (machineCount >= getNumMachinesAtStage(curStage)) {
				curStage++;
				machineCount = 0;
			}
		}
    	
    	// Create job objects
    	jobs = new JobGUI[numJobs];
    	
    	for (int jobID = 0; jobID < jobs.length; jobID++) {
			jobs[jobID] = new JobGUI(
					jobID, stagesVisited[jobID], 
					eligibleMachines[jobID], processingTimes[jobID], 
					predecessors[jobID], lags[jobID]);
		}
    }
    
    //---------- Public methods -------------------------------------------------------------------
    
        /**
     * Returns the name of this problem instance.
     * @return the name of this problem instance.
     */
    public String getName() {
    	return name;
    }
    
    /**
     * Returns the machines that are part of this HFFSP instance. This method creates a defensive
     * copy of the corresponding attribute, so limit the number of method calls, or use 
     * {@link #getMachine(int)} instead.
     * 
     * @return the machines that are part of this HFFSP instance.
     */
    public MachineGUI[] getMachines() {
    	return Arrays.copyOf(machines, machines.length);
    }
    
    /**
     * Returns the machines that are part of this HFFSP instance as a list.
     * @return the machines that are part of this HFFSP instance as a list.
     */
    public List<MachineGUI> getMachineList() {
    	return Arrays.asList(getMachines());
    }
    
    /**
     * Returns the machine with the specified id.
     * @param machineID the ID of the machine.
     * @return the machine with the specified id.
     */
    public MachineGUI getMachine(int machineID) {
    	return machines[machineID];
    }
    
    /**
     * Returns the jobs that are part of this HFFSP instance. This method creates a defensive
     * copy of the corresponding attribute, so limit the number of method calls.
     * @return the jobs that are part of this HFFSP instance. 
     */
    public JobGUI[] getJobs() {
    	return Arrays.copyOf(jobs, jobs.length);
    }
    
    /**
     * Returns the jobs that are part of this HFFSP instance as a list.
     * @return the jobs that are part of this HFFSP instance as a list.
     */
    public List<JobGUI> getJobList() {
    	return Arrays.asList(getJobs());
    }
    
    /**
     * Returns the job with the specified id.
     * @param jobID a job id, starting from 0.
     * @return the job with the specified id.
     */
    public JobGUI getJob(int jobID) {
    	return jobs[jobID];
    }
    
    /**
     * Returns the number of machines at the specified stage.
     * @param stage the stage number, starting at 0.
     * @return the number of machines at the specified stage.
     */
    public int getNumMachinesAtStage(int stage) {
    	if(stage >= numMachinesPerStage.length) {
    		return 0;
    	} else {
    		return numMachinesPerStage[stage];
    	}
    }
    
    /**
     * Returns the setup time on the specified machine when processing job <code>nextJobID</code> after <code>firstJobID</code>.
     * @param machine   the machine to be checked.
     * @param firstJob  the first job.
     * @param nextJob   the job to be scheduled after the first one on the same machine.
     * @return the setup time.
     */
    public int getSetupTime(MachineGUI machine, JobGUI firstJob, JobGUI nextJob) {
    	return setupTimes[machine.getId()][firstJob.getId()][nextJob.getId()];
    }
    
    
    /**
     * Determines whether the sequence dependent setup time between the specified jobs on the specified machine is anticipatory or not. Anticipatory
     * setups can be done before the next job is released at the previous stage.
     * @param machine   the machine to be checked.
     * @param firstJob  the first job.
     * @param nextJob   the job to be scheduled after the first one on the same machine.
     * @return 1 if the setup time is anticipatory, else 0.
     */
    public int getAnticipatorySetup(MachineGUI machine, JobGUI firstJob, JobGUI nextJob) {
    	return anticipatoryFlags[machine.getId()][firstJob.getId()][nextJob.getId()];
    }
        
    //---------- Helper methods -------------------------------------------------------------------
    
    /**
     * Expects the next token to be an <code>int</code>, and reads it.
     * @return the integer read from the tokenizer.
     * @throws IOException if the next token was <i>not</i> an integer. 
     */
    private int readInt() throws IOException {
        int tok = tokenizer.nextToken();
        
        // If we encounter an EOL (e.g. because of comments), skip it & try again
        if(tok == StreamTokenizer.TT_EOL) {
            return readInt();
        } else if(tok == StreamTokenizer.TT_NUMBER) {
            return new Double(tokenizer.nval).intValue();
        } else {
            throw new IOException("Number expected while parsing instance file on line " + 
                    tokenizer.lineno() + ". ttype = " + tokenizer.ttype);
        }
    }
    
    /**
     * Expects the next token to be an <code>int</code>, and skips it.
     * @throws IOException if the next token was <i>not</i> an integer. 
     */
    private void skipInt() throws IOException {
        int tok = tokenizer.nextToken();
        
        // If we encounter an EOL (e.g. because of comments), skip it & try again
        if(tok == StreamTokenizer.TT_EOL) {
            skipInt();
        } else if (tok != StreamTokenizer.TT_NUMBER) {
    		throw new IOException(
    				"Number expected while parsing instance file on line " +
    		        tokenizer.lineno() + ". ttype = " + tokenizer.ttype);
    	}
    }
    
//    /**
//     * Expects the next token to be a <code>String</code>, and reads it.
//     * @return the word read from the tokenizer
//     * @throws IOException if the next token was <i>not</i> a string. 
//     */
//    private String readWord() throws IOException {
//        if (tokenizer.nextToken() == StreamTokenizer.TT_WORD) {
//            return tokenizer.sval;
//        } else {
//            throw new IOException("String expected while parsing instance file on line " + 
//                    tokenizer.lineno() + ". ttype = " + tokenizer.ttype);
//        }
//    }
    
    /**
     * Expects the next token to be the specified <code>String</code>, and skips it.
     * @throws IOException if the next token was <i>not</i> the specified word. 
     */
    private void skipWord(String word) throws IOException {
        if (tokenizer.nextToken() != StreamTokenizer.TT_WORD || !tokenizer.sval.equals(word) ) {
        	throw new IOException("String expected while parsing instance file on line " + 
                    tokenizer.lineno() + ". ttype = " + tokenizer.ttype);
        }
    }
    
    /**
     * Expects the next token to be an end-of-line.
     * @throws IOException if the next token was <i>not</i> an end-of-line. 
     */
    private void skipEOL() throws IOException {
        if (tokenizer.nextToken() == StreamTokenizer.TT_EOL) {
            return;
        } else {
            throw new IOException("EOL expected while parsing instance file on line " + 
                    tokenizer.lineno() + ". ttype = " + tokenizer.ttype);
        }
    }
    
    /**
     * Read a two-dimensional array of <code>int</code>s from the tokenizer with the specified dimensions.
     * @param numRows the number of rows to read
     * @param numCols the number of columns to read
     * @param skip    determines whether every other read integer should be skipped
     * @return the matrix that was read from the file
     * @throws IOException if the matrix in the text file was of a different form (e.g. other dimensions)
     * or an unexpected token type was encountered during reading.
     */
    private int[][] readIntMatrix(int numRows, int numCols, boolean skip) throws IOException {
        int[][] result = new int[numRows][numCols];
        
        for (int row = 0; row < result.length; row++) {
            for (int col = 0; col < result[row].length; col++) {
            	if (skip) {skipInt();} 
                result[row][col] = readInt();
            }
            skipEOL();
        }
        
        return result;
    }
    
    private int[][] readIntMatrix(int numRows, int numCols) throws IOException {
    	return readIntMatrix(numRows, numCols, false);
    }
    
    /**
     * Start reading integers starting from the current position and accumulate them, until a token of
     * another type is encountered. Return the accumulated numbers as an array. 
     * @return an array containing all accumulated integers. If only the value -1 was encountered,
     * an empty array is returned.
     * @throws IOException if an unexpected token type was encountered.
     */
    private int[] readIntList() throws IOException {
    	Vector<Integer> result = new Vector<Integer>();
    	while (tokenizer.nextToken() == StreamTokenizer.TT_NUMBER) {
    		result.add(new Double(tokenizer.nval).intValue());
    	}
    	
    	if (result.size() == 1 && result.get(0).equals(new Integer(-1))) {
    		return new int[0];
    	}
    	
    	int[] resultArray = new int[result.size()];
    	for (int i = 0; i < resultArray.length; i++) {
			resultArray[i] = result.get(i);
		}
    	return resultArray;
    }

    /**
     * Decrement all elements in the specified array
     * @param array the array to be decremented
     * @return the decremented array
     */
    private int[] decrement(int[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] -= 1;
        }
        return array;
    }


}
