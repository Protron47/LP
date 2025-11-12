import java.util.*;

public class FCFS {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter the number of processes: ");
        int n = sc.nextInt();

        String task_id[] = new String[n];
        int arrival_time[] = new int[n];
        int burst_time[] = new int[n];
        int scheduled_time[] = new int[n];
        int waiting_time[] = new int[n];
        int total_time[] = new int[n];

        // Input process data
        for (int i = 0; i < n; i++) {
            System.out.print("\nEnter Task ID: ");
            task_id[i] = sc.next();

            System.out.print("Enter Arrival Time: ");
            arrival_time[i] = sc.nextInt();

            System.out.print("Enter Burst Time: ");
            burst_time[i] = sc.nextInt();
        }

        // Sort by Arrival Time
        for (int i = 0; i < n - 1; i++) {
//            for (int j = i + 1; j < n; j++) {
                if (arrival_time[i] > arrival_time[i+1]) {
                    int temp = arrival_time[i];
                    arrival_time[i] = arrival_time[i+1];
                    arrival_time[i+1] = temp;

                    temp = burst_time[i];
                    burst_time[i] = burst_time[i+1];
                    burst_time[i+1] = temp;

                    String t = task_id[i];
                    task_id[i] = task_id[i+1];
                    task_id[i+1] = t;
    //            }
            }
        }

        // Calculate FCFS times
        int currentTime = 0;
        for (int i = 0; i < n; i++) {
            if (currentTime < arrival_time[i]) {
                currentTime = arrival_time[i];
            }
            scheduled_time[i] = currentTime;
            waiting_time[i] = scheduled_time[i] - arrival_time[i];
            total_time[i] = waiting_time[i] + burst_time[i];
            currentTime += burst_time[i];
        }

        // Display results
        System.out.println("\n--- FCFS Scheduling Results ---");
        System.out.println("Task\tAT\tBT\tST\tWT\tTAT");
        double avgWT = 0, avgTAT = 0;
        for (int i = 0; i < n; i++) {
            System.out.println(task_id[i] + "\t" + arrival_time[i] + "\t" + burst_time[i] + "\t" +
                    scheduled_time[i] + "\t" + waiting_time[i] + "\t" + total_time[i]);
            avgWT += waiting_time[i];
            avgTAT += total_time[i];
        }
        System.out.println("\nAverage Waiting Time: " + (avgWT / n));
        System.out.println("Average Turnaround Time: " + (avgTAT / n));
        sc.close();
    }
}
