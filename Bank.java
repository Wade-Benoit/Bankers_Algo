
public interface Bank {

    // Adds a Customer to the Bank.
    void addCustomer(int custNum, int[] maxDemand);


    // Outputs the values of available, maximum, allocation, and need.
    void getState();

    //Requests the resources for that customer
    boolean requestResources(int custNum, int[] request);

    //Releases the resources for that customer
    void releaseResources(int custNum, int[] release);
}
