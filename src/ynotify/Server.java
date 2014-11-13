package ynotify;

public class Server {
    private String path = null;
	
    public Server(String path) {
        this.path = path;
        initServer();
    }
	
    private void initServer() {
        ServerWorker sw = new ServerWorker(this.path);
//        Thread t = new Thread(sw);
//        t.start();
        sw.run();
    }
}
