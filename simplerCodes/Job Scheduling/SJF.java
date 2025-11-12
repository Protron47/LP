// Import all utilities from java.util (like Scanner)
import java.util.*;

// This is our main program
public class SJF {
    // This is the 'main' function where all the code runs
    public static void main(String[] args) {
        // Create a new Scanner tool named 'sc' to read user input
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter the number of processes: "); // Ask the user
        int n = sc.nextInt(); // Read the number and store it in 'n'

        // --- Setup Arrays ---
        // Create arrays to hold all the data for 'n' processes
        String task_id[] = new String[n]; // To hold names (e.g., "P1")
        int arrival_time[] = new int[n]; // To hold Arrival Times
        int burst_time[] = new int[n]; // To hold Burst Times
        int scheduled_time[] = new int[n]; // To hold Start Times (when it *starts* running)
        int waiting_time[] = new int[n]; // To hold Waiting Times
        int total_time[] = new int[n]; // To hold Turnaround Times
        boolean done[] = new boolean[n]; // To track which processes are finished

        // --- Input process data ---
        // Loop 'n' times (once for each process)
        for (int i = 0; i < n; i++) {
            System.out.print("\nEnter Task ID: "); // Ask for ID
            task_id[i] = sc.next(); // Read and store the ID/name
            System.out.print("Enter Arrival Time: "); // Ask for AT
            arrival_time[i] = sc.nextInt(); // Read and store the Arrival Time
            System.out.print("Enter Burst Time: "); // Ask for BT
            burst_time[i] = sc.nextInt(); // Read and store the Burst Time
            // 'done[i]' is automatically 'false' by default
        }

        // --- Main Scheduling Loop ---
        // 'completed' counts how many processes have finished
        int completed = 0;
        // 'currentTime' is our master clock
        int currentTime = 0;
        
        // Keep looping as long as 'completed' is less than 'n'
        while (completed < n) {
            // --- Find the best process to run *now* ---
            // 'idx' will hold the *index* of the shortest job. -1 means "none found".
            int idx = -1;
            // 'minBT' will track the shortest burst time. Start it at "infinity".
            int minBT = Integer.MAX_VALUE;

            // Loop through all 'n' processes to find the shortest one
            for (int i = 0; i < n; i++) {
                // Check 3 things:
                // 1. Is process 'i' NOT done?
                // 2. Did process 'i' *already arrive*? (at <= currentTime)
                // 3. Is its burst time *shorter* than the 'minBT' we've found so far?
                if (!done[i] && arrival_time[i] <= currentTime && burst_time[i] < minBT) {
                    minBT = burst_time[i]; // If yes, this is our new shortest burst time
                    idx = i; // This is the *index* of our new best choice
                }
            } // End of 'for' loop (we have checked all processes)

            // --- Handle CPU Idle or Run Process ---
            // Check if 'idx' is still -1
            if (idx == -1) {
                // If yes, no process has arrived *yet*. The CPU is idle.
                currentTime++; // Just move the clock forward by 1
                continue; // Skip the rest of this 'while' loop and check again
            }

            // --- Schedule process 'idx' ---
            // (If we are here, 'idx' holds the index of the shortest job)
            scheduled_time[idx] = currentTime; // Its Start Time is the 'currentTime'
            // Waiting Time = Start Time - Arrival Time
            waiting_time[idx] = scheduled_time[idx] - arrival_time[idx];
            // Turnaround Time = Waiting Time + Burst Time
            total_time[idx] = waiting_time[idx] + burst_time[idx];
            // --- This is NON-PREEMPTIVE ---
            // Add its *entire* burst time to the clock
            currentTime += burst_time[idx];
            // Mark this process as DONE
            done[idx] = true;
            // Add 1 to our 'completed' counter
            completed++;
        } // End of 'while' loop

        // --- Display results ---
        System.out.println("\n--- SJF (Non-Preemptive) Scheduling Results ---"); // Header
        System.out.println("Task\tAT\tBT\tST\tWT\tTAT"); // Table columns
        double avgWT = 0, avgTAT = 0; // Variables for calculating average
        
        // Loop through all processes
        for (int i = 0; i < n; i++) {
            // Print their final details in a table row
            System.out.println(task_id[i] + "\t" + arrival_time[i] + "\t" + burst_time[i] + "\t" +
                    scheduled_time[i] + "\t" + waiting_time[i] + "\t" + total_time[i]);
            // Add this process's times to the totals
            avgWT += waiting_time[i];
            avgTAT += total_time[i];
        }

        // Print the final averages
        System.out.println("\nAverage Waiting Time: " + (avgWT / n));
        System.out.println("Average Turnaround Time: " + (avgTAT / n));
        // Close the scanner tool
        sc.close();
    } // End of 'main'
} // End of 'SJF' class