// Import all utilities from java.util (like Scanner, Queue, LinkedList)
import java.util.*;

// Define a 'Process' blueprint (class) to hold all info for one process
class Process {
    int pid;        // Process ID (e.g., 1, 2, 3)
    int at;         // Arrival Time (when it enters the queue)
    int bt;         // Burst Time (the total work it needs)
    int rbt;        // Remaining Burst Time (how much work is left)
    int ct;         // Completion Time (when it finishes)
    int tat;        // Turnaround Time (ct - at)
    int wt;         // Waiting Time (tat - bt)
    boolean completed; // A flag to track if it's done (true/false)
}

// This is our main program
public class RoundRobin {
    // This is the 'main' function where all the code runs
    public static void main(String[] args) {
        // Create a new Scanner tool named 'sc' to read user input
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of processes: "); // Ask the user
        int n = sc.nextInt(); // Read the number and store it in 'n'

        System.out.print("Enter Time Quantum: "); // Ask for the time slice
        int tq = sc.nextInt(); // Read the number and store it in 'tq'

        // Create an array 'p' that can hold 'n' number of Process objects
        Process[] p = new Process[n];

        // --- Input process details ---
        // Loop 'n' times (once for each process)
        for (int i = 0; i < n; i++) {
            p[i] = new Process(); // Create a new, blank Process object in the array
            p[i].pid = i + 1; // Set its ID (e.g., 1, 2, 3...)
            System.out.print("Enter Arrival Time for P" + (i + 1) + ": "); // Ask for AT
            p[i].at = sc.nextInt(); // Read and store the Arrival Time
            System.out.print("Enter Burst Time for P" + (i + 1) + ": "); // Ask for BT
            p[i].bt = sc.nextInt(); // Read and store the Burst Time
            p[i].rbt = p[i].bt; // Set the 'remaining' time to the *full* burst time
        }

        // --- Sort by arrival time ---
        // This is important to know which process to start with
        Arrays.sort(p, Comparator.comparingInt(a -> a.at));

        // --- Setup for the main loop ---
        // Create a 'q' (Ready Queue) using a LinkedList (First-In, First-Out)
        Queue<Process> q = new LinkedList<>();
        // Create a 'boolean' array to track if a process is *already* in the 'q'
        boolean[] inQueue = new boolean[n];
        // 'time' is our master clock, starts at 0
        int time = 0;
        // 'completed' counts how many processes have finished
        int completed = 0;
        // 'totalWT' and 'totalTAT' will sum up times for calculating the average
        int totalWT = 0, totalTAT = 0;

        // --- Start with first arrival ---
        // Add the very first process (at index 0, since we sorted) to the queue
        q.add(p[0]);
        // Mark it as "in the queue"
        inQueue[0] = true;
        // Set the master clock 'time' to the arrival time of the first process
        time = p[0].at;

        // --- Main Scheduling Loop ---
        // Keep looping as long as 'completed' is less than 'n'
        while (completed < n) {
            
            // --- Handle Idle CPU ---
            // Check if the ready queue 'q' is empty
            if (q.isEmpty()) {
                // If it is, the CPU is idle. We need to find the next process to arrive.
                // Loop through all processes
                for (int i = 0; i < n; i++) {
                    // Find the first one that is NOT completed
                    if (!p[i].completed) {
                        // Fast-forward 'time' to its arrival time (if it's in the future)
                        time = Math.max(time, p[i].at);
                        // Add this process to the now-empty queue
                        q.add(p[i]);
                        // Mark it as "in the queue"
                        inQueue[i] = true;
                        // Stop this 'for' loop (we only needed to find one)
                        break;
                    }
                }
            } // End of 'if (q.isEmpty())'

            // --- Run a Process ---
            // Get the process at the *front* of the queue. 'poll()' removes it.
            Process curr = q.poll();

            // Check if its remaining time is *more* than the time quantum
            if (curr.rbt > tq) {
                // If yes, let it run for a full 'tq' slice
                time += tq; // Add 'tq' to the master clock
                curr.rbt -= tq; // Subtract 'tq' from its remaining time
            } else {
                // If no, it will finish in this turn (or exactly on this turn)
                time += curr.rbt; // Add its *remaining* time to the clock (e.g., only 2ms left)
                curr.rbt = 0; // The process has 0 time remaining
                curr.ct = time; // This is its Completion Time
                curr.tat = curr.ct - curr.at; // Calculate Turnaround Time
                curr.wt = curr.tat - curr.bt; // Calculate Waiting Time
                curr.completed = true; // Mark this process as DONE
                completed++; // Add 1 to our 'completed' counter

                // Add its times to the totals for the average
                totalWT += curr.wt;
                totalTAT += curr.tat;
            }

            // --- Add newly arrived processes ---
            // After running a slice, check if any *new* processes arrived
            // Loop through all processes
            for (int i = 0; i < n; i++) {
                // Check 3 things:
                // 1. Is it *not* in the queue?
                // 2. Is it *not* completed?
                // 3. Did it arrive *before or at* the current 'time'?
                if (!inQueue[i] && !p[i].completed && p[i].at <= time) {
                    q.add(p[i]); // If yes to all, add it to the *back* of the queue
                    inQueue[i] = true; // Mark it as "in the queue"
                }
            }

            // --- Re-add the current process if needed ---
            // Check if the process we just ran ('curr') is *not* finished
            if (!curr.completed) {
                // If it's not done, add it to the *back* of the queue
                q.add(curr);
            }
        } // End of 'while (completed < n)' loop

        // --- Print results ---
        System.out.println("\nProcess\tAT\tBT\tCT\tTAT\tWT"); // Print the table header
        // Loop through all 'n' processes
        for (int i = 0; i < n; i++) {
            // Print their final details in a table row
            System.out.println("P" + p[i].pid + "\t" + p[i].at + "\t" + p[i].bt + "\t" +
                               p[i].ct + "\t" + p[i].tat + "\t" + p[i].wt);
        }

        // Print the final averages
        // '(double)' is used to force floating-point division (e.g., 10 / 3 = 3.333)
        System.out.println("\nAverage Waiting Time = " + (double) totalWT / n);
        System.out.println("Average Turnaround Time = " + (double) totalTAT / n);

        // Close the scanner tool
        sc.close();
    } // End of 'main'
} // End of 'RoundRobin' class