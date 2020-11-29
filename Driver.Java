
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;


public class Driver {

    public static void main(String[] args) {
        System.out.println("Banker's Algorithm, Wade Benoit");


        Scanner s = new Scanner(System.in);

        //Initializes number of resources randomly
        int c = getRandomInt(10);
        int[] resources = new int[c];


        //Initializes amounts of those resources randomly
        for (int i = 0; i < resources.length; i++) {
            System.out.print("Initiating Resources Randomly...");
            resources[i] = getRandomInt(10);
        }

        //Initializes Bank
        Bank bank = new ImplBank(resources);


        int[] maximum = new int[resources.length];

        //Initializes Thread array
        Thread[] customers = new Thread[Customer.COUNT];



        int custNum = 0;
        int resourceNum = 0;


        //Handles New Customers
        for (int i = 0; i < Customer.COUNT; i++) {
            for (int resource : resources) {
                maximum[resourceNum++] = getRandomInt(resource);
            }

            customers[custNum] = new Thread(new Customer(custNum, maximum, bank));
            bank.addCustomer(custNum, maximum);

            ++custNum;

            resourceNum = 0;
        }



        //Starts all threads!   -----------------------------
        for (Thread thread : customers) {
            thread.start();
        }



    }




    //function returning random int with argument max
    private static int getRandomInt(int max) {
        return ThreadLocalRandom.current().nextInt(0, max + 1);
    }
}
