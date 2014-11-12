package ynotify;

import java.io.*;
import java.net.*;

public class Net
{
    private Socket s = null;
    private int type = 0;
    private InputStream is = null;
    private OutputStream os = null;
    private static ServerSocket sock = null;
    private static Socket client = null;
	
    public static final int NET_SERVER = 0;
    public static final int NET_CLIENT = 1;
	
    public Net(Socket s, int netType)
    {
    	this.s = s;
    	this.type = netType;
    }
    
    public static Net initServer(String hostname, int port) throws IOException
    {
        Socket clientSocket = null;
        sock = new ServerSocket();
        sock.bind(new InetSocketAddress(hostname, port));
        clientSocket = sock.accept();
        clientSocket.setKeepAlive(true);
        return new Net(clientSocket, NET_SERVER);
    }
    
    public static Net initClient(String hostname, int port) throws IOException
    {
        client = new Socket();
        client.setKeepAlive(true);
        client.connect(new InetSocketAddress(hostname, port));
        return new Net(client, NET_CLIENT);
    }
    
    public static Net serverAccept() throws IOException
    {
        Socket clientSocket = null;
        clientSocket = sock.accept();
        clientSocket.setKeepAlive(true);
        return new Net(clientSocket, NET_SERVER);
    }
    
    public static ServerSocket getServerSocket()
    {
        return Net.sock;
    }
    
    public OutputStream getWriteStream() throws IOException
    {
    	if (s == null) {
            return null;
    	}
    	if (os == null) {
            os = s.getOutputStream();
    	}
    	return os;
    }
    
    public InputStream getReadStream() throws IOException
    {
    	if (s == null) {
            return null;
    	}
    	if (is == null) {
            is = s.getInputStream();
    	}
    	return is;
    }
    
    public void close() throws IOException
    {
    	this.s.close();
    }
}
