package sourcecodemodeler.network;

import java.io.Serializable;
import java.util.function.Consumer;

public class Receiver extends NetworkConnection {
    private int port;

    public Receiver(int port, Consumer<Serializable> onReceiveCallback) {
        super(onReceiveCallback);
        this.port = port;
    }

    @Override
    protected boolean isReceiver() {
        return true;
    }

    @Override
    protected String getIP() {
        return null;
    }

    @Override
    protected int getPort() {
        return port;
    }

}