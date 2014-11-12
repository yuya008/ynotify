package ynotify;

import java.io.*;
import java.util.*;

public class ClientWorker implements Runnable {
    private String path = null;
    private boolean reconnect = false;
    private int reconnTimes = 0;
    private ArrayList<Message> fileList = new ArrayList();
    private ArrayList<Message> newList = new ArrayList();
    
    private Net client = null;

    private Protocol prot = null;

    public ClientWorker(String notifyPath) {
        this.path = notifyPath;
        try {
            this.client = Net.initClient(Config.Client_hostname, Config.Client_port);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            reconnect = true;
            reConnect();
        }
        this.prot = new Protocol(this.client, this.path);
    }
    
    private void reConnect()
    {
        do {
            try {
                if (this.client != null) {
                    this.client.close();
                }
                this.prot = null;
                this.client = Net.initClient(Config.Client_hostname, Config.Client_port);
                reconnTimes = 0;
                reconnect = false;
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
                if (reconnTimes == Config.Client_reconnect_max_times) {
                    System.exit(1);
                }
                
                reconnTimes++;
                System.err.println("sleep "+reconnTimes+" reconnect");

                try {
                    Thread.sleep(reconnTimes * 1000);
                } catch (InterruptedException ex1) {
                    ex1.printStackTrace();
                }
                
            }
        } while (reconnect);
        
        this.prot = new Protocol(this.client, this.path);
    }
    
    @Override
    public void run() {
        for (;;) {
            if (this.reconnect) {
                reConnect();
            }
            doScan(this.path);
            doDifferent();
            doPush();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void doPush() {
        Iterator<Message> fsetIter = fileList.iterator();
        Message msg = null;
        
        if (fileList.isEmpty()) {
            return;
        }
        
        for (;fsetIter.hasNext();) {
            msg = fsetIter.next();
            try {
                prot.checkObject(msg);
            } catch (IOException e) {
                System.err.println(e.getMessage());
                this.reconnect = true;
                return;
            }
        }
    }
    
    private void doDifferent()
    {
        if (fileList.isEmpty()) {
            fileList = this.newList;
            this.newList = null;
            return;
        }
        
        Message msg = null;
        for (Iterator<Message> iter = fileList.iterator(); iter.hasNext();) {
            msg = iter.next();
            if (!fileList.contains(msg)) {
                msg.setOperation("Delete");
                this.newList.add(msg);
            }
        }
        fileList = this.newList;
        this.newList = null;
    }
    
    private void doScan(String scanPath) {
        File f = new File(scanPath);
        File[] files = f.listFiles();
        Message msg = null;
        if (this.newList == null) {
            this.newList = new ArrayList<Message>();
        }
        
        for (File dirent : files) {
           msg = Message.createNewMessage(dirent, dirent.getAbsolutePath().substring(this.path.length()));
           newList.add(msg);
           if (dirent.isDirectory()) {
               doScan(dirent.getAbsolutePath());
           }
        }
    }
}
