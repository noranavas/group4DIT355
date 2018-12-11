package sourcecodemodeler.network;

import java.io.Serializable;
import java.util.function.Consumer;

public class Sender extends NetworkConnection {
    private int port;
    private String ip;

    public Sender(int port, String ip, Consumer<Serializable> onReceiveCallback) {
        super(onReceiveCallback);
        this.port = port;
        this.ip = ip;
    }

    @Override
    protected boolean isReceiver() {
        return false;
    }

    @Override
    protected String getIP() {
        return ip;
    }

    @Override
    protected int getPort() {
        return port;
    }
}
