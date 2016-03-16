import java.util.Comparator;

public class EndComparator implements Comparator<Task> {
	@Override
	public int compare(Task t1, Task t2) {
		return Integer.compare(t1.endTime, t2.endTime);
	}
}