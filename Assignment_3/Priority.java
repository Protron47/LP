import java.util.*;

class Process {
    int pid, at, bt, pr, ct, tat, wt, remaining;
    boolean done = false;
}

public class Priority{
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of processes: ");
        int n = sc.nextInt();
        Process[] p = new Process[n];

        // Input
        for (int i = 0; i < n; i++) {
            p[i] = new Process();
            p[i].pid = i + 1;
            System.out.println("\nEnter Arrival Time, Burst Time, Priority for P" + (i + 1) + ":");
            p[i].at = sc.nextInt();
            p[i].bt = sc.nextInt();
            p[i].pr = sc.nextInt();
            p[i].remaining = p[i].bt; // for preemptive use
        }

        System.out.println("\nChoose Scheduling Type:");
        System.out.println("1. Non-Preemptive Priority Scheduling");
        System.out.println("2. Preemptive Priority Scheduling");
        System.out.print("Enter choice: ");
        int choice = sc.nextInt();

        switch (choice) {
            case 1:
                nonPreemptivePriority(p, n);
                break;
            case 2:
                preemptivePriority(p, n);
                break;
            default:
                System.out.println("Invalid choice!");
        }

        sc.close();
    }

    // ---------------- NON-PREEMPTIVE PRIORITY SCHEDULING ----------------
    public static void nonPreemptivePriority(Process[] p, int n) {
        int time = 0, completed = 0;
        double totalTAT = 0, totalWT = 0;

        while (completed < n) {
            int idx = -1;
            int bestPriority = Integer.MAX_VALUE;

            // find highest priority process among arrived ones
            for (int i = 0; i < n; i++) {
                if (!p[i].done && p[i].at <= time && p[i].pr < bestPriority) {
                    bestPriority = p[i].pr;
                    idx = i;
                }
            }

            if (idx == -1) {
                time++;
            } else {
                p[idx].ct = time + p[idx].bt;
                p[idx].tat = p[idx].ct - p[idx].at;
                p[idx].wt = p[idx].tat - p[idx].bt;
                p[idx].done = true;

                time = p[idx].ct;
                completed++;
                totalTAT += p[idx].tat;
                totalWT += p[idx].wt;
            }
        }

        displayResults(p, n, totalTAT, totalWT, "Non-Preemptive Priority Scheduling");
    }

    // ---------------- PREEMPTIVE PRIORITY SCHEDULING ----------------
    public static void preemptivePriority(Process[] p, int n) {
        int time = 0, completed = 0;
        double totalTAT = 0, totalWT = 0;

        while (completed < n) {
            int idx = -1;
            int bestPriority = Integer.MAX_VALUE;

            // select process with highest priority among arrived ones
            for (int i = 0; i < n; i++) {
                if (!p[i].done && p[i].at <= time && p[i].pr < bestPriority) {
                    bestPriority = p[i].pr;
                    idx = i;
                }
            }

            if (idx == -1) {
                time++; // idle CPU
                continue;
            }

            // execute process for 1 unit (preemptive)
            p[idx].remaining--;
            time++;

            if (p[idx].remaining == 0) {
                p[idx].done = true;
                p[idx].ct = time;
                p[idx].tat = p[idx].ct - p[idx].at;
                p[idx].wt = p[idx].tat - p[idx].bt;
                totalTAT += p[idx].tat;
                totalWT += p[idx].wt;
                completed++;
            }
        }

        displayResults(p, n, totalTAT, totalWT, "Preemptive Priority Scheduling");
    }

    // ---------------- DISPLAY RESULTS ----------------
    public static void displayResults(Process[] p, int n, double totalTAT, double totalWT, String title) {
        System.out.println("\n--- " + title + " ---");
        System.out.println("PID\tAT\tBT\tPR\tCT\tTAT\tWT");
        for (int i = 0; i < n; i++) {
            System.out.println(p[i].pid + "\t" + p[i].at + "\t" + p[i].bt + "\t" +
                               p[i].pr + "\t" + p[i].ct + "\t" + p[i].tat + "\t" + p[i].wt);
        }
        System.out.printf("\nAverage Turnaround Time: %.2f", totalTAT / n);
        System.out.printf("\nAverage Waiting Time: %.2f\n", totalWT / n);
    }
}
