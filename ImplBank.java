
import java.util.Arrays;


public class ImplBank implements Bank {
    private static final int MAX_REQUESTS = 20; // The maximum number of resource requests
    private int numOfRequests; // the number of resource requests

    private int numOfCustomers = Customer.COUNT; // The number of Customers

    // Array containing the amount of each resource available
    private int[] available;

    // 2d array containing the maximum demand of each Customer
    private int[][] maximum;

    // 2d array containing the amount currently allocated to each Customer
    private int[][] allocation;



    // Initializes Bank
    public ImplBank(int[] avail) {
        this.available = avail;
        this.maximum = new int[numOfCustomers][available.length];
        this.allocation = new int[numOfCustomers][available.length];
    }

   // Add a customer to the bank
    @Override
    public void addCustomer(int custNum, int[] maxDemand) {
        // Check if the specified Customer Number is greater
        // than the length of the maximum array
        if (custNum > maximum.length) {
            // Re-create the maximum array with an increased sizes
            maximum = resizeCustomerArray(maximum, custNum);
        }

        // Add the specified demand to the maximum array
        // at the specified customer number
        maximum[custNum] = maxDemand;
    }


    // Outputs values of resources
    @Override
    public void getState() {
        StringBuilder sb = new StringBuilder();

        // Append available
        sb.append("\n\nAvailable:\n----------\n");
        for(int i = 0; i < available.length; i++){
            sb.append("Resource " + i + ": " + available[i] + "\n");
        }

        // Append allocated
        sb.append("\n\nAllocated:\n----------\n");
        for(int i = 0; i < allocation[0].length; i++){
            // Sum of all allocated resources
            int sum = 0;

            for(int j = 0; j < allocation.length; j++){
                sum += allocation[j][i];
            }

            sb.append("Resource " + i + ": " + sum + "\n");
        }

        // Append max
        sb.append("\n\nMaximum Demand:\n----------");
        for(int i = 0; i < maximum.length; i++){
            sb.append("\nCustomer " + i + ": ");

            // Append individual resources to line.
            for(int j = 0; j < maximum[i].length; j++){
                sb.append(" Resource " + j + ": " + maximum[i][j]);
            }
        }

        // Append need
        sb.append("\n\nNeed:\n----------");
        for(int i = 0; i < maximum.length; i++){
            sb.append("\nCustomer " + i + ": ");

            for(int j = 0; j < maximum[i].length; j++){
                sb.append(" Resource " + j + ": " + (maximum[i][j] - maximum[i][j]));
            }
        }

        // Prints everything in one go.
        System.out.println(sb);
    }

    //Requests resources for this specific customer
    @Override
    public boolean requestResources(int custNum, int[] request) {
        logRequest();

        for (int i = 0; i < request.length; i++) {
            if (request[i] > available[i]) return false;

            if (request[i] > maximum[custNum][i]) return false;
        }

        if (!safeState(custNum, request)) return false;

        for (int i = 0; i < request.length; i++) {
            available[i] -= request[i];
            allocation[custNum][i] += request[i];
        }

        return true;
    }


    //Releases the resources
    public synchronized void releaseResources(int custNum, int[] release){
        // Release resources
        for(int i = 0; i < release.length; i++){
            available[i] += release[i];
            allocation[custNum][i] -= release[i];
        }
        getState();
    }



    //Safety algorithm implementation
    private boolean safeState(int custNum, int[] request) {
        int[] clonedResources = available.clone();
        int[][] clonedAllocation = allocation.clone();

        // First check if any part of the request requires more resources than are available (unsafe state)
        for (int i = 0; i < clonedResources.length; i++) {
            if (request[i] > clonedResources[i]) {
                return false;
            }
        }

        // If we reach this point, the first request was valid so we execute it on the simulated resources
        for (int i = 0; i < clonedResources.length; i++) {
            clonedResources[i] -= request[i];
            clonedAllocation[custNum][i] += request[i];
        }

        // Create new boolean array and set all to false
        boolean[] canFinish = new boolean[numOfCustomers];

        for (int i = 0; i < canFinish.length; i++) {
            canFinish[i] = false;
        }

        // Now check if there is an order wherein other customers can still finish after us
        for (int i = 0; i < numOfCustomers; i++) {
            // Find a customer that can finish a request. Loop through all resources per customer
            for (int j = 0; j < numOfCustomers; j++) {
                if (!canFinish[j]) {
                    for (int k = 0; k < clonedResources.length; k++) {
                        // If the need (maximum - allocation = need) is not greater than the amount of available resources, thread can finish
                        if (!((maximum[j][k] - clonedAllocation[j][k]) > clonedResources[k])) {
                            canFinish[j] = true;
                            for (int l = 0; l < clonedResources.length; l++) {
                                clonedResources[l] += clonedAllocation[j][l];
                            }
                        }
                    }
                }
            }
        }

        // Restore the value of need and allocation for this thread
        for (int i = 0; i < available.length; i++) {
            clonedAllocation[custNum][i] -= request[i];
        }

        // After all the previous calculations, loop through the array and see if every customer could complete the transaction for their maximum demand
        for (boolean aCanFinish : canFinish) {
            if (!aCanFinish) {
                return false;
            }
        }

        return true;
    }

    //Writes a 2D array using StringBuilder
    private void writeCustomerArray(int[][] array, StringBuilder sb) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null || array[i].length == 0) continue;

            sb.append("Customer ").append(i).append(":");

            for (int k : array[i]) {
                sb.append(" ").append(k);
            }
        }
    }

    // Logs a request made from a customer
    private void logRequest() {
        if (numOfRequests >= MAX_REQUESTS) {
            Thread.currentThread().interrupt();
        }

        numOfRequests++;
    }

    //Used to resize an array
    private static int[][] resizeCustomerArray(int[][] original, int newLength) {
        if (original == null) return null;

        final int[][] result = new int[newLength][];
        for (int i = 0; i < original.length; i++) {
            result[i] = Arrays.copyOf(original[i], original[i].length);
        }

        return result;
    }
}
