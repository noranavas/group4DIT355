package sourcecodemodeler;

import javafx.concurrent.Task;
import sourcecodemodeler.network.Receiver;
import sourcecodemodeler.network.Sender;

import java.util.concurrent.TimeUnit;

public class ThreadHandler {
    private static final int PORT = Globals.PORT;
    private static final String PATH_TO_XML_FILES = Globals.PATH_TO_XML_FILES;
    private static final String[] IP_ADDRESS = Globals.IP_ADDRESS;
    Receiver receiver = new Receiver();
    Sender sender = new Sender();

    //===== Constructor(s) =====//
    public ThreadHandler() {}

    //===== Tasks =====//
    // Start the sender and send files.
    Task task2 = new Task(){
        @Override
        protected Object call() throws Exception{
            System.out.println("Task 2 started");
           /* for (int i=10; i>0; i--)
                System.out.println(i);
                */
           receiver.start(5991);
            sender.connect(IP_ADDRESS[0], PORT);
            sender.sendFiles(PATH_TO_XML_FILES);
            //socketNode.sendFiles(System.getProperty("user.dir") + "\\source-code-modeler\\resources\\converted_xml\\", socketNode.getSocket());
            //socketNode.stopConnection();
            return null;
        }
    };

    Task task3 = new Task(){
        @Override
        protected Object call() throws Exception{ //start the sender and send files
            System.out.println("Task 3 started");
            receiver.receiveFiles();
            return null;
        }
    };
    Task task4 = new Task(){
        @Override
        protected Object call() throws Exception{ //start the sender and send files
            System.out.println("Task 4 started");
            sender.close();
            return null;
        }
    };

    public Task getTask2() {
        return task2;
    }
    public Task getTask3() {
        return task3;
    }
    public Task getTask4() {
        return task4;
    }

    public void runThreads() {
        //===== Threads =====//
        //Thread thread1 = new Thread(task1);
        Thread thread2 = new Thread(task2);
        Thread thread3 = new Thread(task3);
        Thread thread4 = new Thread(task4);

        try {
            //thread1.start();
            TimeUnit.SECONDS.sleep(4);
            thread2.run(); //runs the thread
            thread2.join(); //waits until this thread is done
            thread3.run();
            thread3.join();
            thread4.run();
            thread4.join();
            thread4.interrupt(); //interrupts the thread. not sure it KILLS THE INSTANCE
            thread3.interrupt();
            thread2.interrupt();
            //System.out.println("Thread3 is still running: " + thread2.isAlive());
            //thread2.join();
            //thread2.join();
        /*
        synchronized (thread5) {
            thread5.start();
        }
        */
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("Problem running threads.");
        }

    }
}
