package GUI;


public class OperationGUI {

	private final int id;
	public JobGUI Mjob;
	private final int job;
	public int slack;
	@SuppressWarnings("unused")
	private final int startTime, endTime;
	private final String name;
	private final int backToBackBefore;
	/**
	 * Create a new operation with the specified parameters.
	 * @param id  the operation ID.
	 * @param job the job this operation is a part of.
	 */
	
	public OperationGUI(int id, int job, int ST, int ET,String n, int backToBackBefore) {
		this.id = id;
		this.job = job;
		this.startTime = ST;
		this.endTime = ET;
		this.name = n;
		this.backToBackBefore = backToBackBefore;
	}
	
	public int getSlack(){
		return slack;
	}
	
	public int getId() {
		return id;
	}
	
	public void setSlack(int sl) {
		slack = sl;
	}
	
	public int getiJob() {
		return job;
	}

	public JobGUI getJob() {
		return Mjob;
	}
	
	public String getName() {
		return name;
	}
	
	public int getBackToBackBefore() {
		return backToBackBefore;
	}
}
