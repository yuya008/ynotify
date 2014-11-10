package ynotify;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class Protocol {
    private String path = null;
    private HashMap<File, String> hm = null;
    private Net net = null;
    private ObjectOutputStream oos = null;
    private ObjectInputStream ois = null;
	
    public Protocol(Net net, String path, HashMap<File, String> hm) {
        this.net = net;
        this.path = path;
        this.hm = hm;
    }
	
    public Protocol(Net net, String path) {
        this.net = net;
        this.path = path;
    }
	
    public void checkObject(File f) throws IOException {
        String md5str = "";
        String nPath = "";
        
        if (oos == null) {
            oos = new ObjectOutputStream(net.getWriteStream());
        }
		
        md5str = hm.get(f);
        nPath = f.getAbsolutePath().substring(this.path.length());
		
        oos.writeBytes(nPath);
        oos.flush();
        oos.writeBytes(md5str);
        oos.flush();
        
        if (checkStatus() == false && f.isFile()) { // transfer
            transfer(f);
        }
    }
	
    private boolean checkFile(String s, String amd5) {
        File f = new File(this.path + s);
        String m = null;
        
        if (!f.exists()) {
            return false;
        }
        if (!amd5.equals("dir")) {
            m = Util.fileMd5(f);
            if (!m.equals(amd5)) {
                return false;
            }
        }
        return true;
    }
	
    public void receiveObject() throws IOException {
        String amd5 = null, cfile = null;
		
        cfile = readObject();
        amd5 = readObject();
        
        if (checkFile(cfile, amd5) == false) {
            sayStatus("no");
            if (amd5.equals("dir")) {
                mkDir(cfile);
            } else {
                touchFile(cfile);
            }
        } else {
            sayStatus("ok");
        }
    }

    private long readFileLength() throws IOException {
        long cfile = -1;
        
        if (ois == null) {
            ois = new ObjectInputStream(net.getReadStream());
        }
        return ois.readLong();
    }
	
    public void touchFile(String s) throws IOException
    {
        File f = new File(this.path + s);
        BufferedOutputStream bos = null;
        byte b[] = new byte[1024];
        int readn = 0, n = -1, lindex = -1;
        long fileLength = readFileLength();
        String dirp = "";
        
        lindex = s.lastIndexOf(File.separator);
        if (lindex != -1) {
            dirp = s.substring(0, lindex);
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
	
    private void mkDir(String cfile) {
        File f = new File(this.path + cfile);
        f.mkdirs();
    }
	
    private void sayStatus(String status) throws IOException
    {
        if (oos == null) {
            oos = new ObjectOutputStream(net.getWriteStream());
        }
        oos.writeBytes(status);
        oos.flush();
    }
	
    private String readObject() throws IOException {
        byte[] buf = new byte[1024];
        String cfile = null;
        int i = -1;
        
        if (ois == null) {
            ois = new ObjectInputStream(net.getReadStream());
        }
        
        i = ois.read(buf);
        if (i == -1) {
            throw new IOException("read object fail");
        }
        cfile = new String(buf, 0, i);
        return cfile;
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
    
    private void sayFileLength(long n) throws IOException {
    	if (oos == null) {
            oos = new ObjectOutputStream(net.getWriteStream());
    	}
    	oos.writeLong(n);
    	oos.flush();
    }
    
    private void transfer(File tf) throws IOException {
        byte buf[] = new byte[1024];
        
        FileInputStream dis = null;
        BufferedOutputStream sis = null;
        int readn = -1;
        
        dis = new FileInputStream(tf);
        if (oos == null) {
            oos = new ObjectOutputStream(net.getWriteStream());
        }
        
        sayFileLength(tf.length());
        
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
