import java.util.Scanner;

public class NextFit {

    static void nextFit(int blockSize[], int m, int processSize[], int n) {
        int allocation[] = new int[n];
        int lastAllocated = 0; // keeps track of the last allocated block index
        int totalUsed = 0;

        for (int i = 0; i < n; i++) {
            allocation[i] = -1; // initially not allocated
            int j = lastAllocated;
            int count = 0;
            boolean allocated = false;

            // loop through all blocks once (circularly)
            while (count < m) {
                if (blockSize[j] >= processSize[i]) {
                    // allocate
                    allocation[i] = j + 1; // store block number (1-based)
                    blockSize[j] -= processSize[i];
                    totalUsed += processSize[i];
                    lastAllocated = j; // update pointer
                    allocated = true;
                    break;
                }
                j = (j + 1) % m; // move circularly
                count++;
            }

            // if not allocated, move pointer one ahead to prevent rechecking same block next time
            if (!allocated)
                lastAllocated = (lastAllocated + 1) % m;
        }

        // print results
        System.out.println("\nProcess No.\tProcess Size\tBlock No.");
        for (int i = 0; i < n; i++) {
            System.out.print(" " + (i + 1) + "\t\t" + processSize[i] + "\t\t");
            if (allocation[i] != -1)
                System.out.println(allocation[i]);
            else
                System.out.println("Not Allocated");
        }

        // calculate memory utilization
        int totalMemoryUsed = totalUsed;
        int totalMemoryAvailable = 0;
        for (int size : blockSize)
            totalMemoryAvailable += size;

        int totalMemory = totalMemoryAvailable + totalMemoryUsed;
        double utilization = (double) totalMemoryUsed / totalMemory * 100;

        System.out.println("\nTotal Memory Available: " + totalMemory);
        System.out.println("Total Memory Used: " + totalMemoryUsed);
        System.out.printf("Memory Utilization: %.2f%%\n", utilization);
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        System.out.print("Enter the number of memory blocks: ");
        int m = in.nextInt();
        int blockSize[] = new int[m];
        System.out.println("Enter the sizes of the blocks:");
        for (int i = 0; i < m; i++) {
            blockSize[i] = in.nextInt();
        }

        System.out.print("Enter the number of processes: ");
        int n = in.nextInt();
        int processSize[] = new int[n];
        System.out.println("Enter the sizes of the processes:");
        for (int i = 0; i < n; i++) {
            processSize[i] = in.nextInt();
        }

        nextFit(blockSize, m, processSize, n);
        in.close();
    }
}
