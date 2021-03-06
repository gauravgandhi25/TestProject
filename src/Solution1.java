import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@SuppressWarnings("unchecked")
public class Solution1 {

	static Map<String, ArrayList<Task>> allTasks = new TreeMap<String, ArrayList<Task>>();
	static Map<String, String[][]> timeLines = new HashMap<String, String[][]>();

	public static void main(String[] args) {

		readFile(allTasks);
		
		System.out.println("01 02 03 04 05 06 07 08 09 10 11 12 13 14 15 16 17 18 19 20\n");
		
		String[][] top_left_timeline = processSection(allTasks.get(Constants.TOP_LEFT));
		timeLines.put(Constants.TOP_LEFT, top_left_timeline);
			
		String[][] top_right_timeline = processSection(allTasks.get(Constants.TOP_RIGHT));
		timeLines.put(Constants.TOP_RIGHT, top_right_timeline);
		
		String[][] bottom_left_timeline = processSection(allTasks.get(Constants.BOTTOM_LEFT));
		timeLines.put(Constants.BOTTOM_LEFT, bottom_left_timeline);
		
		String[][] bottom_right_timeline = processSection(allTasks.get(Constants.BOTTOM_RIGHT));
		timeLines.put(Constants.BOTTOM_RIGHT, bottom_right_timeline);
		
		System.out.println("01 02 03 04 05 06 07 08 09 10 11 12 13 14 15 16 17 18 19 20\n");
	}

	// This method processes top left section of the page
	private static String[][] processSection(ArrayList<Task> tasks) {

		Set<Integer> inCompatibleTasks = getInCompatibleTasks(tasks);
		tasks = getCompatibleTasks(inCompatibleTasks, tasks);
		Collections.sort(tasks, new StartComparator());

		ArrayList<Task> temp = (ArrayList<Task>) tasks.clone();
		Collections.sort(temp, new EndComparator());
		
		temp = null;

		String[][] timeLine = new String[3][Constants.GLOBAL_MAX - Constants.GLOBAL_MIN + 1];

		for (int i = 0; i < 3; i++)
			Arrays.fill(timeLine[i], "00");

		updateTimeLine(timeLine, tasks);
		print(timeLine);
		writeToFile(tasks);
		return timeLine;
	}
	
	// Based on provided compatible list of tasks, this function updates the timeline
	private static void updateTimeLine(String[][] timeLine, ArrayList<Task> tasks) {

		int min = tasks.get(0).startTime;
		int p1 = min - Constants.GLOBAL_MIN;
		min = Constants.GLOBAL_MIN;
		int p2 = p1, p3 = p1;

		for (Task task : tasks) {
			if (p1 == task.startTime - min) {
				p1 = task.startTime - min;
				for (int i = task.startTime - min; i <= task.endTime - min; i++) {
					timeLine[0][p1] = task.name;
					p1++;
				}
			} else if ((p2 == task.startTime - min)) {
				p2 = task.startTime - min;
				for (int i = task.startTime; i <= task.endTime; i++) {
					timeLine[1][p2] = task.name;
					p2++;
				}
			} else if ((p3 == task.startTime - min)) {
				p3 = task.startTime - min;
				for (int i = task.startTime; i <= task.endTime; i++) {
					timeLine[2][p3] = task.name;
					p3++;
				}
			} else if (p1 < task.startTime - min) {
				p1 = task.startTime - min;
				for (int i = task.startTime - min; i <= task.endTime - min; i++) {
					timeLine[0][p1] = task.name;
					p1++;
				}
			} else if ((p2 < task.startTime - min)) {
				p2 = task.startTime - min;
				for (int i = task.startTime; i <= task.endTime; i++) {
					timeLine[1][p2] = task.name;
					p2++;
				}
			} else {
				p3 = task.startTime - min;
				for (int i = task.startTime; i <= task.endTime; i++) {
					timeLine[2][p3] = task.name;
					p3++;
				}
			}
		}
	}

	// This method returns list of compatible tasks, by removing incompatible tasks from list of all tasks
	private static ArrayList<Task> getCompatibleTasks(Set<Integer> inCompatibleTaskset, ArrayList<Task> tasks) {

		ArrayList<Task> temp = ((ArrayList<Task>) tasks.clone());
		int i = 0;

		for (Task task : temp) {
			if (!inCompatibleTaskset.contains(task.id)) {
				i++;
			} else {
				tasks.remove(i);
			}
		}
		return tasks;
	}

	// This method reads from the file and adds all tasks to the global key value map. Key is the location
	private static void readFile(Map<String, ArrayList<Task>> allTasks) {

		ArrayList<Task> tempList = new ArrayList<Task>();

		try {
			BufferedReader reader = new BufferedReader(new FileReader("input.txt"));
			String line = reader.readLine();
			int i = 1;
			while (line != null) {
				String[] temp = line.split(" ");
				Task task = new Task(temp[0], temp[1], Integer.parseInt(temp[2]), Integer.parseInt(temp[3]), i);
				i++;
				tempList.add(task);
				line = reader.readLine();
			}
			reader.close();

			Collections.sort(tempList, new EndComparator());
			Constants.GLOBAL_MAX = tempList.get(tempList.size() - 1).endTime;

			Collections.sort(tempList, new StartComparator());
			Constants.GLOBAL_MIN = tempList.get(0).startTime;

			for (Task task : tempList) {
				if (!allTasks.containsKey(task.location)) {
					allTasks.put(task.location, new ArrayList<Task>());
				}
				allTasks.get(task.location).add(task);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// This method returns incompatible task id.
	private static Set<Integer> getInCompatibleTasks(ArrayList<Task> tasks) {

		ArrayList<Task> tasksByStart;
		ArrayList<Task> tasksByEnd;
		tasksByStart = ((ArrayList<Task>) tasks.clone());
		tasksByEnd = ((ArrayList<Task>) tasks.clone());

		Collections.sort(tasksByStart, new StartComparator());
		Collections.sort(tasksByEnd, new EndComparator());

		Set<Integer> inCompatible = new HashSet<Integer>();

		int count = 0;
		int i = 0, j = 0;

		while (i < tasksByStart.size() && j < tasksByEnd.size()) {

			if (inCompatible.contains(tasksByStart.get(i).id)) {
				i++;
				continue;
			}

			if (inCompatible.contains(tasksByEnd.get(j).id)) {
				j++;
				continue;
			}

			if (tasksByStart.get(i).startTime <= tasksByEnd.get(j).endTime) {
				count++;
				if (count == 4) {
					inCompatible.add(tasksByStart.get(i).id);
					count--;
				} else if (isRunningAnyWhere(tasksByStart.get(i))) {
					inCompatible.add(tasksByStart.get(i).id);
					count--;
				}
				i++;

			} else {
				j++;
				count--;
			}
		}
		return inCompatible;
	}

	// This function determines is given task is running at any previously processed location, locations are processed left to right
	private static boolean isRunningAnyWhere(Task task) {

		if (task.location.equalsIgnoreCase("TOP_LEFT"))
			return false;

		if (task.location.equalsIgnoreCase("TOP_RIGHT") || task.location.equalsIgnoreCase("BOTTOM_LEFT")
				|| task.location.equalsIgnoreCase("BOTTOM_RIGHT")) {
			String[][] timeLine = timeLines.get("TOP_LEFT");
			for (int i = 0; i < 3; i++) {
				if (isTaskInBetween(timeLine[i], task.startTime - Constants.GLOBAL_MIN,
						task.endTime - Constants.GLOBAL_MIN, task.name)) {
					return true;
				}
			}
		}

		if (task.location.equalsIgnoreCase("BOTTOM_LEFT") || task.location.equalsIgnoreCase("BOTTOM_RIGHT")) {
			String[][] timeLine = timeLines.get("TOP_RIGHT");
			for (int i = 0; i < 3; i++) {
				if (isTaskInBetween(timeLine[i], task.startTime - Constants.GLOBAL_MIN,
						task.endTime - Constants.GLOBAL_MIN, task.name)) {
					return true;
				}
			}
		}

		if (task.location.equalsIgnoreCase("BOTTOM_RIGHT")) {
			String[][] timeLine = timeLines.get("BOTTOM_LEFT");
			for (int i = 0; i < 3; i++) {
				if (isTaskInBetween(timeLine[i], task.startTime - Constants.GLOBAL_MIN,
						task.endTime - Constants.GLOBAL_MIN, task.name)) {
					return true;
				}
			}
		}

		return false;
	}
	
	// This function determines, if there is same task in between provided start and end, both inclusive
	private static boolean isTaskInBetween(String[] timeLine, int start, int end, String name) {

		for (int i = start; i <= end; i++) {
			if (timeLine[i].equalsIgnoreCase(name))
				return true;
		}
		return false;
	}

	// Print the time line for one locations
	private static void print(String[][] timeLine) {

		for (int i = 0; i < timeLine.length; i++) {
			for (int j = 0; j < timeLine[i].length; j++)
				System.out.print(timeLine[i][j] + " ");

			System.out.println();
		}
		System.out.println();
	}
	
	// Write tasks to file similar to input format
	private static void writeToFile(ArrayList<Task> tasks) {

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt", true));

			for (Task task : tasks) {
				writer.write(task.location + " " + task.name + " " + task.startTime + " " + task.endTime);
				writer.newLine();
			}
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}