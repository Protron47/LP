// Import all utilities from java.util (like Scanner)
import java.util.*;

// Define a 'Process' blueprint (class) to hold all info for one process
class Process {
    // pid=ID, at=Arrival, bt=Burst, pr=Priority, ct=Completion, tat=Turnaround, wt=Wait
    int pid, at, bt, pr, ct, tat, wt, remaining;
    // 'done' is a flag to track if it's finished (true/false)
    boolean done = false;
}

// This is our main program
public class Priority{
    // This is the 'main' function where all the code runs
    public static void main(String[] args) {
        // Create a new Scanner tool named 'sc' to read user input
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of processes: "); // Ask the user
        int n = sc.nextInt(); // Read the number and store it in 'n'
        // Create an array 'p' that can hold 'n' number of Process objects
        Process[] p = new Process[n];

        // --- Input process details ---
        // Loop 'n' times (once for each process)
        for (int i = 0; i < n; i++) {
            p[i] = new Process(); // Create a new, blank Process object in the array
            p[i].pid = i + 1; // Set its ID (e.g., 1, 2, 3...)
            System.out.println("\nEnter Arrival Time, Burst Time, Priority for P" + (i + 1) + ":");
            p[i].at = sc.nextInt(); // Read and store Arrival Time
            p[i].bt = sc.nextInt(); // Read and store Burst Time
            p[i].pr = sc.nextInt(); // Read and store Priority
            p[i].remaining = p[i].bt; // Set 'remaining' time (for preemptive)
        }

        // --- Let the user choose the mode ---
        System.out.println("\nChoose Scheduling Type:");
        System.out.println("1. Non-Preemptive Priority Scheduling");
        System.out.println("2. Preemptive Priority Scheduling");
        System.out.print("Enter choice: ");
        int choice = sc.nextInt(); // Read the user's choice (1 or 2)

        // This 'switch' statement jumps to the correct block of code
        switch (choice) {
            case 1: // If they typed 1
                nonPreemptivePriority(p, n); // Call the non-preemptive function
                break; // Stop
            case 2: // If they typed 2
                preemptivePriority(p, n); // Call the preemptive function
                break; // Stop
            default: // If they typed anything else
                System.out.println("Invalid choice!"); // Print an error
        }

        sc.close(); // Close the scanner tool
    } // End of 'main'

    // ---------------- NON-PREEMPTIVE PRIORITY SCHEDULING ----------------
    // This is a separate function (method) that runs the non-preemptive logic
    public static void nonPreemptivePriority(Process[] p, int n) {
        int time = 0; // 'time' is our master clock
        int completed = 0; // 'completed' counts finished processes
        double totalTAT = 0, totalWT = 0; // For calculating averages

        // Keep looping as long as 'completed' is less than 'n'
        while (completed < n) {
            // --- Find the best process to run *now* ---
            // 'idx' will hold the *index* of the highest priority job. -1 = "none found".
            int idx = -1;
            // 'bestPriority' will track the best priority. Start it at "infinity".
            // (Remember: LOW number = HIGH priority)
            int bestPriority = Integer.MAX_VALUE; 

            // Loop through all 'n' processes to find the best one
            for (int i = 0; i < n; i++) {
                // Check 3 things:
                // 1. Is process 'i' NOT done?
                // 2. Did process 'i' *already arrive*? (at <= time)
                // 3. Is its priority *better* (lower) than 'bestPriority'?
                if (!p[i].done && p[i].at <= time && p[i].pr < bestPriority) {
                    bestPriority = p[i].pr; // This is our new best priority
                    idx = i; // This is the *index* of our new best choice
                }
            } // End of 'for' loop (we have checked all processes)

            // --- Handle CPU Idle or Run Process ---
            if (idx == -1) {
                // If no process is found, CPU is idle
                time++; // Just move the clock forward by 1
            } else {
                // --- Schedule process 'idx' ---
                // (This is NON-PREEMPTIVE, so it runs to completion)
                p[idx].ct = time + p[idx].bt; // Completion Time = Clock + *Full* Burst Time
                p[idx].tat = p[idx].ct - p[idx].at; // Calculate Turnaround Time
                p[idx].wt = p[idx].tat - p[idx].bt; // Calculate Waiting Time
                p[idx].done = true; // Mark this process as DONE

                time = p[idx].ct; // Jump the master clock to the new completion time
                completed++; // Add 1 to our 'completed' counter
                totalTAT += p[idx].tat; // Add to total for average
                totalWT += p[idx].wt; // Add to total for average
            }
        } // End of 'while' loop

        // All processes are done, so call the display function
        displayResults(p, n, totalTAT, totalWT, "Non-Preemptive Priority Scheduling");
    }

    // ---------------- PREEMPTIVE PRIORITY SCHEDULING ----------------
    // This is a separate function (method) that runs the preemptive logic
    public static void preemptivePriority(Process[] p, int n) {
        int time = 0; // 'time' is our master clock
        int completed = 0; // 'completed' counts finished processes
        double totalTAT = 0, totalWT = 0; // For calculating averages

        // Keep looping as long as 'completed' is less than 'n'
        while (completed < n) {
            // --- Find the best process to run *right now* ---
            int idx = -1; // 'idx' will hold the *index* of the highest priority job
            int bestPriority = Integer.MAX_VALUE; // Start 'bestPriority' at "infinity"

            // Loop through all 'n' processes
            for (int i = 0; i < n; i++) {
                // Check 3 things:
                // 1. Is process 'i' NOT done?
                // 2. Did process 'i' *already arrive*? (at <= time)
                // 3. Is its priority *better* (lower) than 'bestPriority'?
                if (!p[i].done && p[i].at <= time && p[i].pr < bestPriority) {
                    bestPriority = p[i].pr; // This is our new best priority
                    idx = i; // This is the *index* of our new best choice
                }
            } // End of 'for' loop

            // --- Handle CPU Idle or Run Process ---
            if (idx == -1) {
                // If no process is found, CPU is idle
                time++; // Move the clock forward by 1
                continue; // Skip the rest and re-check
            }

            // --- Schedule process 'idx' ---
            // (This is PREEMPTIVE, so it runs for *one* time unit)
            
            // Decrement its *remaining* time by 1
            p[idx].remaining--;
            // Move the master clock forward by 1
            time++;

            // --- Check if the process just finished ---
            if (p[idx].remaining == 0) {
                // If yes, its remaining time is now 0
                p[idx].done = true; // Mark this process as DONE
                p[idx].ct = time; // Its Completion Time is the *current* time
                p[idx].tat = p[idx].ct - p[idx].at; // Calculate Turnaround Time
                p[idx].wt = p[idx].tat - p[idx].bt; // Calculate Waiting Time
                totalTAT += p[idx].tat; // Add to total for average
                totalWT += p[idx].wt; // Add to total for average
                completed++; // Add 1 to our 'completed' counter
            }
            // If it's not finished, the 'while' loop repeats.
            // We will re-check *all* processes (including this one)
            // to see who has the highest priority at the *new* 'time'.
        } // End of 'while' loop

        // All processes are done, so call the display function
        displayResults(p, n, totalTAT, totalWT, "Preemptive Priority Scheduling");
    }

    // ---------------- DISPLAY RESULTS ----------------
    // This is a helper function to print the final table
    public static void displayResults(Process[] p, int n, double totalTAT, double totalWT, String title) {
        System.out.println("\n--- " + title + " ---"); // Print the title
        System.out.println("PID\tAT\tBT\tPR\tCT\tTAT\tWT"); // Print table headers
        // Loop through all processes
        for (int i = 0; i < n; i++) {
            // Print their details
            System.out.println(p[i].pid + "\t" + p[i].at + "\t" + p[i].bt + "\t" +
                               p[i].pr + "\t" + p[i].ct + "\t" + p[i].tat + "\t" + p[i].wt);
        }
        // Print the averages, formatted to 2 decimal places
        System.out.printf("\nAverage Turnaround Time: %.2f", totalTAT / n);
        System.out.printf("\nAverage Waiting Time: %.2f\n", totalWT / n);
    } // End of 'displayResults'
} // End of 'Priority' class