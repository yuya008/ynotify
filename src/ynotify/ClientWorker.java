package ynotify;

import java.io.*;
import java.util.*;

public class ClientWorker implements Runnable {
    private String path = null;
    private ArrayList<Message> fileList = new ArrayList<Message>();
    private ArrayList<Message> newList = new ArrayList<Message>();
    
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
        this.prot = new Protocol(this.client, this.path);
    }
    
    @Override
    public void run() {
        for (;;) {
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
                e.printStackTrace();
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
