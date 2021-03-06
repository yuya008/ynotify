package ynotify;

import java.io.*;

public class Protocol {
    private String path = null;
    private Net net = null;
    private ObjectOutputStream oos = null;
    private ObjectInputStream ois = null;
	
    public Protocol(Net net, String path) {
        this.net = net;
        this.path = path;
    }
	
    public void checkObject(Message msg) throws IOException {  
        if (oos == null) {
            oos = new ObjectOutputStream(net.getWriteStream());
        }
        oos.writeObject(msg);
        oos.flush();
        if (checkStatus() == false && msg.fileType.equals("file")) { // transfer
            transfer(msg);
        }
    }
	
    private boolean checkFile(Message msg) {
        File f = new File(this.path + msg.fileRelativePath);
        String m = null;
        
        if (!f.exists()) {
            return false;
        }
        if (!msg.md5.equals("")) {
            m = Util.fileMd5(f);
            if (!m.equals(msg.md5)) {
                return false;
            }
        }
        return true;
    }
    
    public void deleteFile(String p)
    {
        File f = new File(p);
        if (!f.exists()) {
            return;
        }
        
        if (f.isDirectory()) {
            File[] fl = f.listFiles();
            for (File file : fl) {
                if (file.isDirectory()) {
                    deleteFile(file.getAbsolutePath());
                }
                file.delete();
            }
        }
        f.delete();
    }
	
    public void receiveObject() throws Exception {
        Message msg = (Message)readObject();
        
        switch (msg.operation) {
            case "Check":
                if (checkFile(msg) == false) {
                    if (msg.fileType.equals("dir")) {
                        sayStatus("ok");
                        mkDir(msg);
                    } else {
                        sayStatus("no");
                        touchFile(msg);
                    }
                } else {
                    sayStatus("ok");
                }break;
            case "Delete":
                deleteFile(this.path + msg.fileRelativePath);
                sayStatus("ok");
                break;
        }
    }
	
    public void touchFile(Message msg) throws IOException {
        File f = new File(this.path + msg.fileRelativePath);
        BufferedOutputStream bos = null;
        byte b[] = new byte[1024];
        int readn = 0, n = -1, lindex = -1;
        long fileLength = msg.fileSize;
        String dirp = "";
        
        lindex = msg.fileRelativePath.lastIndexOf(File.separator);
        if (lindex != -1) {
            dirp = msg.fileRelativePath.substring(0, lindex);
        }
            
        File fdir = new File(this.path + dirp);
        if (!fdir.isDirectory()) {
            fdir.mkdirs();
        }
        f.createNewFile();
            
        if (fileLength == 0) {
            return;
        }
            
        bos = new BufferedOutputStream(new FileOutputStream(f));
        if (ois == null) {
            ois = new ObjectInputStream(this.net.getReadStream());
        }

        for (;;) {
            n = ois.read(b);
            if (n == -1) {
                break;
            }
            bos.write(b, 0, n);
            readn += n;
            if (readn == fileLength) {
                break;
            }
        }
        bos.close();  
    }
	
    private void mkDir(Message msg) {
        File f = new File(this.path + msg.fileRelativePath);
        f.mkdirs();
    }
	
    private void sayStatus(String status) throws IOException {
        if (oos == null) {
            oos = new ObjectOutputStream(net.getWriteStream());
        }
        oos.writeBytes(status);
        oos.flush();
    }
	
    private Message readObject() throws Exception {
        if (ois == null) {
            ois = new ObjectInputStream(net.getReadStream());
        }
        
        Message i = (Message) ois.readObject();
        return i;
    }
	
    private boolean checkStatus() throws IOException {
        byte b[] = new byte[5];
        int readn = -1;
        String status = null;

        if (ois == null) {
            ois = new ObjectInputStream(net.getReadStream());
        }
        readn = ois.read(b);
        status = new String(b, 0, readn);
        return status.equals("ok");
    }
    
    private void transfer(Message msg) throws IOException {
        byte buf[] = new byte[1024];
        
        FileInputStream dis = null;
        int readn = -1;
        
        dis = new FileInputStream(msg.file);
        if (oos == null) {
            oos = new ObjectOutputStream(net.getWriteStream());
        }
        
        for (;;) {
            readn = dis.read(buf);
            if (readn == -1) {
                break;
            }
            oos.write(buf, 0, readn);
        }
        
        oos.flush();
        if (dis != null) {
            dis.close();
        }
    }
}
