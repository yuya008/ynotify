package ynotify;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;

public class ClientWorker implements Runnable {
    private String path = null;
    private HashMap<File, String> hm = new HashMap<File, String>();
    public boolean push = false;
    private Net client = null;

    private Protocol prot = null;

    public ClientWorker(String notifyPath) {
        this.path = notifyPath;
        try {
            this.client = Net.initClient(Config.Client_hostname, Config.Client_port);
        } catch (IOException ex) {
            ex.printStackTrace();
            if (this.client != null) {
                try {                    
                    this.client.close();
                } catch (IOException ex1) {
                    ex1.printStackTrace();
                }
            }
        }
        this.prot = new Protocol(this.client, this.path, this.hm);
    }
    
    @Override
    public void run() {
        for (;;) {
            hm.clear();
            doScan(this.path);
            doPush();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void doPush() {
        Iterator<File> fsetIter = hm.keySet().iterator();
        File tf = null;
        
        if (hm.size() == 0) {
            return;
        }
        
        for (;fsetIter.hasNext();) {
            tf = fsetIter.next();
            try {
                prot.checkObject(tf);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
    }
    
    private void doScan(String scanPath)
    {
        File f = new File(scanPath);
        File[] files = f.listFiles();
        
        for (File dirent : files) {
            if (dirent.isDirectory()) {
                hm.put((File)dirent, (String)"dir");
                doScan(dirent.getAbsolutePath());
            } else {
                hm.put((File)dirent, (String)Util.fileMd5(dirent));
            }
        }
    }
}
