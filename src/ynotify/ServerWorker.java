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
    
    private void ServerReAccept()
    {
        try {
            net.close();
            this.net = null;
            this.prot = null;
            this.net = Net.serverAccept();
            this.prot = new Protocol(this.net, this.path);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public void run() {
        for (;;) {
            try {
                this.prot.receiveObject();
            } catch (Exception e) {
                System.err.println(e.getMessage());
                ServerReAccept();
            }
        }
    }
}
