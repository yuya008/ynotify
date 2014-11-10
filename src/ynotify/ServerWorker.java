package ynotify;

import java.io.IOException;

public class ServerWorker implements Runnable {
    private String path = null;
    private Net net = null;
    private Protocol prot = null;
	
    public ServerWorker(String path) {
        this.path = path;
        try {
            net = Net.initServer(Config.Server_hostname, Config.Server_port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.prot = new Protocol(this.net, this.path);
    }
    
    @Override
    public void run() {
        for (;;) {
            try {
                this.prot.receiveObject();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
