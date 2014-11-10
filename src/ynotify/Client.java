package ynotify;

public class Client {
    private String notifyPath = null;
    
    public Client(String notifyPath) {
        this.notifyPath = notifyPath;
        clientInit();
    }
    
    private void clientInit() {
        ClientWorker cw = new ClientWorker(notifyPath);
        Thread worker = new Thread(cw);
//        worker.setDaemon(true);
        worker.start();
//        cw.run();
    }
}
