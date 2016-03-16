class Task {

	String name;
	String location;
	int startTime;
	int endTime;
	int id;

	public Task(String location, String name, int startTime, int endTime, int id) {
		super();
		this.name = name;
		this.startTime = startTime;
		this.endTime = endTime;
		this.id = id;
		this.location = location;
	}

	@Override
	public String toString() {
		return "" + id + " " + startTime + " " + endTime + "";
	}
}
