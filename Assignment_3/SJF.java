import java.util.*;

public class SJF {
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
        boolean done[] = new boolean[n];

        // Input process data
        for (int i = 0; i < n; i++) {
            System.out.print("\nEnter Task ID: ");
            task_id[i] = sc.next();

            System.out.print("Enter Arrival Time: ");
            arrival_time[i] = sc.nextInt();

            System.out.print("Enter Burst Time: ");
            burst_time[i] = sc.nextInt();
        }

        int completed = 0, currentTime = 0;
        while (completed < n) {
            int idx = -1;
            int minBT = Integer.MAX_VALUE;

            // Find process with shortest burst time among arrived ones
            for (int i = 0; i < n; i++) {
                if (!done[i] && arrival_time[i] <= currentTime && burst_time[i] < minBT) {
                    minBT = burst_time[i];
                    idx = i;
                }
            }

            if (idx == -1) {
                currentTime++; // No process has arrived yet
                continue;
            }

            // Schedule process
            scheduled_time[idx] = currentTime;
            waiting_time[idx] = scheduled_time[idx] - arrival_time[idx];
            total_time[idx] = waiting_time[idx] + burst_time[idx];
            currentTime += burst_time[idx];
            done[idx] = true;
            completed++;
        }

        // Display results
        System.out.println("\n--- SJF (Non-Preemptive) Scheduling Results ---");
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
