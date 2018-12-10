package sourcecodemodeler.network;

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
            System.out.println("IllegalThreadStateException");
        }
    }

    public void send(Serializable data) throws Exception {
        System.out.println("Sending data...");
        if (connectionThread.getOut() != null) {
            connectionThread.out.writeObject(data);
        } else {
            System.out.println("Obj output stream is null");
        }

    }

    public void closeConnection() throws Exception {
        String s = isReceiver() ? "Receiver" : "Sender";
        try {
            connectionThread.socket.close();
            System.out.println(s + " closed.");
        } catch (Exception e) {
            System.out.println("Exception when closing " + s);
        }
    }

    protected abstract boolean isReceiver();
    protected abstract String getIP();
    protected abstract int getPort();

    private class ConnectionThread extends Thread {
        private Socket socket;
        private ObjectOutputStream out;

        public ObjectOutputStream getOut() {
            return out;
        }
        public void setOut(ObjectOutputStream out) {
            this.out = out;
        }

        @Override
        public void run() {
            try
            {
                System.out.println("run() is blblb");
                ServerSocket serverSocket = isReceiver() ? new ServerSocket(getPort()) : null;

                Socket socket;
                if (isReceiver()) {
                    System.out.println("A1");
                    socket = serverSocket.accept();
                    System.out.println("A2");
                } else {
                    System.out.println("B1");
                    System.out.println(getIP() + " " + getPort());
                    socket = new Socket(getIP(), getPort());
                    System.out.println("B2");
                }
                System.out.println("C");
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                this.socket = socket;
                this.out = out;
                socket.setTcpNoDelay(true); // Allows faster sending of messages.

                while (true) {
                    Serializable data = (Serializable) in.readObject();
                    onReceiveCallback.accept(data);
                    if(socket.isConnected()) System.out.println("Socket connected.");
                }
            } catch (Exception e) {
                onReceiveCallback.accept("Connection closed.");
                e.printStackTrace();
            }
        }
    }

}
