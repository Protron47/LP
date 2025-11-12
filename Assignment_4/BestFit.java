import java.util.Scanner;

public class BestFit {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of memory blocks: ");
        int m = sc.nextInt();
        int blockSize[] = new int[m];
        boolean blockAllocated[] = new boolean[m];

        System.out.println("Enter sizes of memory blocks:");
        for (int i = 0; i < m; i++) {
            blockSize[i] = sc.nextInt();
            blockAllocated[i] = false;
        }

        System.out.print("Enter number of processes: ");
        int n = sc.nextInt();
        int processSize[] = new int[n];
        int allocation[] = new int[n];
        for (int i = 0; i < n; i++) allocation[i] = -1;

        System.out.println("Enter sizes of processes:");
        for (int i = 0; i < n; i++) processSize[i] = sc.nextInt();

        // Best Fit Allocation
        for (int i = 0; i < n; i++) {
            int bestIdx = -1;
            for (int j = 0; j < m; j++) {
                if (!blockAllocated[j] && blockSize[j] >= processSize[i]) {
                    if (bestIdx == -1 || blockSize[j] < blockSize[bestIdx]) {
                        bestIdx = j;
                    }
                }
            }
            if (bestIdx != -1) {
                allocation[i] = bestIdx;
                blockAllocated[bestIdx] = true;
            }
        }

        // Output result
        System.out.println("\nProcess No.\tProcess Size\tBlock No.");
        int totalUsed = 0;
        for (int i = 0; i < n; i++) {
            System.out.print("   " + (i + 1) + "\t\t" + processSize[i] + "\t\t");
            if (allocation[i] != -1) {
                System.out.println(allocation[i] + 1);
                totalUsed += processSize[i];
            } else {
                System.out.println("Not Allocated");
            }
        }

        // Memory Utilization
        int totalMemory = 0;
        for (int size : blockSize) totalMemory += size;
        double utilization = (double) totalUsed / totalMemory * 100;

        System.out.println("\nTotal Memory Available: " + totalMemory);
        System.out.println("Total Memory Used: " + totalUsed);
        System.out.printf("Memory Utilization: %.2f%%\n", utilization);

        sc.close();
    }
}
