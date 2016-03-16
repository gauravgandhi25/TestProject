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
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

@SuppressWarnings("unchecked")
public class Solution3 {

	static Map<String, ArrayList<Task>> allTasks = new TreeMap<String, ArrayList<Task>>();
	static Map<String, String[][]> timeLines = new HashMap<String, String[][]>();
	static ArrayList<Task> newTasks = new ArrayList<>();
	
	public static void main(String[] args) {

		readFile(allTasks);
		readRequestFile(newTasks);

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

		ArrayList<Task> temp = new ArrayList<Task>();
		for (Task task : newTasks) {
			if (checkIfFeasible(task)) {
				System.out.println(task.name + " added");
				temp.add(task);
				
			}			
		}
		writeToFile(temp);
	}
	
	private static boolean checkIfFeasible(Task task) {

		ArrayList<Task> tasks = allTasks.get(task.location);
		ArrayList<Task> temp = (ArrayList<Task>) tasks.clone();
		temp.add(task);

		Set<Integer> inCompatibleTasks = getInCompatibleTasks(temp);

		if (inCompatibleTasks.size() != 0) {
			return false; // doesn't have room
		}
		
		for (Entry<String, String[][]> entry : timeLines.entrySet()) {
			String[][] timeLine = entry.getValue();
			for (int i = 0; i < 3; i++) {
				if (isTaskInBetween(timeLine[i], task.startTime - Constants.GLOBAL_MIN,
						task.endTime - Constants.GLOBAL_MIN, task.name)) {
					return false;
				}
			}
		}

		tasks = temp;

		Collections.sort(tasks, new StartComparator());
		String[][] timeLine = new String[3][Constants.GLOBAL_MAX - Constants.GLOBAL_MIN + 1];

		for (int i = 0; i < 3; i++)
			Arrays.fill(timeLine[i], "00");

		updateTimeLine(timeLine, tasks);
		print(timeLine);
		timeLines.put(task.location, timeLine);
		allTasks.put(task.location, tasks);
		return true;
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

	private static void readRequestFile(ArrayList<Task> newTasks) {

		try {
			BufferedReader reader = new BufferedReader(new FileReader(Constants.SECTION3_INPUT_REQUESTS));
			String line = reader.readLine();
			int i = 1;
			while (line != null) {
				String[] temp = line.split(" ");
				Task task = new Task(temp[0], temp[1], Integer.parseInt(temp[2]), Integer.parseInt(temp[3]), i);
				i++;
				newTasks.add(task);
				line = reader.readLine();
			}
			reader.close();
		} catch (Exception e) {

		}

	}

	// This method processes top left section of the page
	private static String[][] processSection(ArrayList<Task> tasks) {

		Collections.sort(tasks, new StartComparator());

		String[][] timeLine = new String[3][Constants.GLOBAL_MAX - Constants.GLOBAL_MIN + 1];

		for (int i = 0; i < 3; i++)
			Arrays.fill(timeLine[i], "00");

		updateTimeLine(timeLine, tasks);
		printTimeLine(timeLine);
		// writeToFile(tasks);
		return timeLine;
	}

	// Based on provided compatible list of tasks, this function updates the
	// timeline
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

	// This method reads from the file and adds all tasks to the global key
	// value map. Key is the location
	private static void readFile(Map<String, ArrayList<Task>> allTasks) {

		ArrayList<Task> tempList = new ArrayList<Task>();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(Constants.SECTION3_INPUT));
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
				} 
				i++;

			} else {
				j++;
				count--;
			}
		}
		return inCompatible;
	}

	// This method checks of there is given task in between start and end inclusive
	private static boolean isTaskInBetween(String[] timeLine, int start, int end, String name) {

		for (int i = start; i <= end; i++) {
			if (timeLine[i].equalsIgnoreCase(name))
				return true;
		}
		return false;
	}

	// Print the time line for one locations
	private static void printTimeLine(String[][] timeLine) {

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
			BufferedWriter writer = new BufferedWriter(new FileWriter(Constants.SECTION3_OUTPUT, false));

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