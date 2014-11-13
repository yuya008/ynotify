package ynotify;

public class Ynotify {
    
    public static void main(String[] args) {
    	Util.parseArgs(args);
        
        if (args[0].equals("server")) {
            new Server(Config.Server_notify_path);
        } else if (args[0].equals("client")) {
            new Client(Config.Client_notify_path);
        } else {
            System.err.println("args fail");
            System.exit(1);
        }
    }
}
