package ynotify;

import java.io.File;

public class Message implements java.io.Serializable {
    public File file;
    public String fileRelativePath;
    public String fileType;
    public long fileSize;
    public String operation;
    public String md5;
    
    public static Message createNewMessage(File f, String path) {
        Message msg = new Message();
        msg.file = f;
        msg.fileSize = f.length();
        msg.fileRelativePath = path.replace("\\", "/");
        msg.operation = "Check";
        msg.md5 = f.isFile() ? Util.fileMd5(f) : "";
        msg.fileType = f.isDirectory() ? "dir" : "file";
        return msg;
    }
    
    @Override
    public Message clone() {
        Message msg = new Message();
        msg.file = this.file;
        msg.fileSize = this.fileSize;
        msg.fileRelativePath = this.fileRelativePath;
        msg.operation = "Check";
        msg.fileType = this.fileType;
        msg.md5 = this.file.isFile() ? Util.fileMd5(this.file) : "";
        
        return msg;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj instanceof Message) {
            Message m = (Message)obj;
            if (m.file.getAbsolutePath().equals(this.file.getAbsolutePath())) {
                return true;
            }
        }
        return false;
    }
}
