package ynotify;

public class Ynotify {
    
    public static void main(String[] args) {
    	args = Util.parseArgs(args);
        if (args[0].equals("server")) {
            new Server(args[1]);
        } else if (args[0].equals("client")) {
            new Client(args[1]);
        } else {
            System.err.println("args fail");
            System.exit(1);
        }
    }
}
