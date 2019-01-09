package sourcecodemodeler.network;

import sourcecodemodeler.model.Sound;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

public abstract class NetworkConnection {
    private ConnectionThread connectionThread = new ConnectionThread();
    private Consumer<Serializable> onReceiveCallback;

    public NetworkConnection(Consumer<Serializable> onReceiveCallback) {
        this.onReceiveCallback = onReceiveCallback;
        connectionThread.setDaemon(true); // Prevents blocking exiting from JBM. (?)
    }

    public void startConnection() throws Exception {
        String s = isReceiver() ? "Receiver" : "Sender";
        System.out.println(s + " started.");
        try {
            connectionThread.start();
        } catch (IllegalThreadStateException e) {
            System.out.println("Problem starting " + s);
        }
    }

    public void send(Serializable data) throws Exception {
        if (connectionThread.out != null) {
            connectionThread.out.writeObject(data);
        } else {
            //System.out.println("Error: ObjectOutputStream is null.");
            System.out.println("Unable to find next node.");
        }
    }

    public void closeConnection() throws Exception {
        String s = isReceiver() ? "Receiver" : "Sender";
        try {
            connectionThread.socket.close();
            System.out.println(s + " closed.");
        } catch (Exception e) {
            System.out.println("Problem when closing " + s);
        }
    }

    protected abstract boolean isReceiver();
    protected abstract String getIP();
    protected abstract int getPort();

    private class ConnectionThread extends Thread {
        private Socket socket;
        private ObjectOutputStream out;

        @Override
        public void run() {
            try (ServerSocket serverSocket = isReceiver() ? new ServerSocket(getPort()) : null;
                 Socket socket = isReceiver() ? serverSocket.accept() : new Socket(getIP(), getPort());
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream()))
            {
                this.socket = socket;
                this.out = out;
                socket.setTcpNoDelay(true); // Allows faster sending of messages.
                while (true) {
                    Serializable data = (Serializable)in.readObject();
                    Sound.play(System.getProperty("user.dir")+"\\source-code-modeler\\resources\\sound.wav");
                    onReceiveCallback.accept(data);
                }
            } catch (Exception e) {
                onReceiveCallback.accept("Connection closed. Retrying in a few seconds...");
                //e.printStackTrace();
                run(); // No longer needed.
            }
        }
    }

}
