import java.util.*;

class Process {
    int pid;        // Process ID
    int at;         // Arrival Time
    int bt;         // Burst Time
    int rbt;        // Remaining Burst Time
    int ct;         // Completion Time
    int tat;        // Turnaround Time
    int wt;         // Waiting Time
    boolean completed;
}

public class RoundRobin {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of processes: ");
        int n = sc.nextInt();

        System.out.print("Enter Time Quantum: ");
        int tq = sc.nextInt();

        Process[] p = new Process[n];

        // Input process details
        for (int i = 0; i < n; i++) {
            p[i] = new Process();
            p[i].pid = i + 1;
            System.out.print("Enter Arrival Time for P" + (i + 1) + ": ");
            p[i].at = sc.nextInt();
            System.out.print("Enter Burst Time for P" + (i + 1) + ": ");
            p[i].bt = sc.nextInt();
            p[i].rbt = p[i].bt;
        }

        // Sort by arrival time
        Arrays.sort(p, Comparator.comparingInt(a -> a.at));

        Queue<Process> q = new LinkedList<>();
        boolean[] inQueue = new boolean[n];
        int time = 0, completed = 0;
        int totalWT = 0, totalTAT = 0;

        // Start with first arrival
        q.add(p[0]);
        inQueue[0] = true;
        time = p[0].at;

        while (completed < n) {
            if (q.isEmpty()) {
                // Jump to next available process if queue empty
                for (int i = 0; i < n; i++) {
                    if (!p[i].completed) {
                        time = Math.max(time, p[i].at);
                        q.add(p[i]);
                        inQueue[i] = true;
                        break;
                    }
                }
            }

            Process curr = q.poll();

            if (curr.rbt > tq) {
                time += tq;
                curr.rbt -= tq;
            } else {
                time += curr.rbt;
                curr.rbt = 0;
                curr.ct = time;
                curr.tat = curr.ct - curr.at;
                curr.wt = curr.tat - curr.bt;
                curr.completed = true;
                completed++;

                totalWT += curr.wt;
                totalTAT += curr.tat;
            }

            // Add newly arrived processes
            for (int i = 0; i < n; i++) {
                if (!inQueue[i] && !p[i].completed && p[i].at <= time) {
                    q.add(p[i]);
                    inQueue[i] = true;
                }
            }

            // If not finished, re-add to queue
            if (!curr.completed) {
                q.add(curr);
            }
        }

        // Print results
        System.out.println("\nProcess\tAT\tBT\tCT\tTAT\tWT");
        for (int i = 0; i < n; i++) {
            System.out.println("P" + p[i].pid + "\t" + p[i].at + "\t" + p[i].bt + "\t" +
                               p[i].ct + "\t" + p[i].tat + "\t" + p[i].wt);
        }

        System.out.println("\nAverage Waiting Time = " + (double) totalWT / n);
        System.out.println("Average Turnaround Time = " + (double) totalTAT / n);

        sc.close();
    }
}
