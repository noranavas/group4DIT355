package sourcecodemodeler;

import javafx.concurrent.Task;
import sourcecodemodeler.network.Receiver;
import sourcecodemodeler.network.Sender;

import java.io.IOException;
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
    Task startReceiver = new Task() {
        @Override
        protected Object call() throws Exception {
            System.out.println("startReceiver task started.");
            receiver.start(PORT);
            return null;
        }
    };

    Task connectSender = new Task() {
        @Override
        protected Object call() {
            System.out.println("connectSender task started.");
            sender.connect(IP_ADDRESS[0], PORT);
            return null;
        }
    };

    Task sendFiles = new Task() {
        @Override
        protected Object call() {
            sender.sendFiles(PATH_TO_XML_FILES);
            return null;
        }
    };

    Task receiveFiles = new Task() {
      @Override
      protected Object call() {
          try {
              receiver.receiveFiles();
          } catch (IOException e) {
              e.printStackTrace();
              System.out.println("Error in task to receive files.");
          }
          return null;
      }
    };

    Task closeSender = new Task(){
        @Override
        protected Object call() {
            sender.close();
            return null;
        }
    };

    //===== Methods =====//
    public void runThreads() {
        Thread startReceiverThread = new Thread(startReceiver);
        Thread connectSenderThread = new Thread(connectSender);
        Thread sendFilesThread = new Thread(sendFiles);
        Thread receiveFilesThread = new Thread(receiveFiles);
        Thread closeSenderThread = new Thread(closeSender);

        try {
            TimeUnit.SECONDS.sleep(1);

            // Executes thread.
            startReceiverThread.run();

            // Waits until thread is done.
            startReceiverThread.join();

            connectSenderThread.run();
            connectSenderThread.join();

            sendFilesThread.run();
            sendFilesThread.join();

            receiveFilesThread.run();
            receiveFilesThread.join();

            //closeSenderThread.run();
            //closeSenderThread.join();

            // Interrupts the thread. Not sure. KILLS INSTANCE?
            startReceiverThread.interrupt();
            connectSenderThread.interrupt();
            sendFilesThread.interrupt();
            receiveFilesThread.interrupt();
            //closeSenderThread.interrupt();

        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("Problem running threads.");
        }
    }

}
