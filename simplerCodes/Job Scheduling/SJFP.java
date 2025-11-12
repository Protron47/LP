// Import all utilities from java.util (like Scanner)
import java.util.*;

// This is our program, named SJFP (SJF Preemptive)
public class SJFP {
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
        int burst_time[] = new int[n]; // To hold the *original* Burst Times
        
        // --- This is the new array for preemptive ---
        int remaining_burst_time[] = new int[n]; // To track how much time is *left*
        
        int completion_time[] = new int[n]; // To hold the *finish* time
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
            
            // Set the 'remaining' time to the *full* burst time to start
            remaining_burst_time[i] = burst_time[i];
            
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
            // 'minRBT' will track the shortest *remaining* burst time. Start at "infinity".
            int minRBT = Integer.MAX_VALUE;

            // Loop through all 'n' processes to find the shortest one *available*
            for (int i = 0; i < n; i++) {
                // Check 4 things:
                // 1. Is process 'i' NOT done?
                // 2. Did process 'i' *already arrive*? (at <= currentTime)
                // 3. Is its *remaining* time *shorter* than the 'minRBT' we've found?
                // 4. Does it still have time left to run? (remaining > 0