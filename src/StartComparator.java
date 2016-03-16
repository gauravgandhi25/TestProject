import java.util.Comparator;

public class StartComparator implements Comparator<Task> {
	@Override
	public int compare(Task t1, Task t2) {
		return Integer.compare(t1.startTime, t2.startTime);
	}
}