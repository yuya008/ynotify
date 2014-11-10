package ynotify;

import java.io.*;
import java.security.MessageDigest;

public class Util
{
    private final static String[] hexDigits = {"0", "1", "2", "3", "4",  
        "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
    
    public static String md5(String inputString)
    {  
        return encodeByMD5(inputString);  
    }
    
    public static boolean validateMd5(String password, String inputString){  
        if(password.equals(encodeByMD5(inputString))){  
            return true;  
        } else{  
            return false;  
        }  
    }
    
    private static String encodeByMD5(String originString){  
        if (originString != null){  
            try{  
                MessageDigest md = MessageDigest.getInstance("MD5");    
                byte[] results = md.digest(originString.getBytes());
                String resultString = byteArrayToHexString(results);  
                return resultString.toUpperCase();  
            } catch(Exception ex){  
                ex.printStackTrace();  
            }  
        }  
        return null;  
    }
    
    private static String byteArrayToHexString(byte[] b){  
        StringBuffer resultSb = new StringBuffer();  
        for (int i = 0; i < b.length; i++){  
            resultSb.append(byteToHexString(b[i]));  
        }  
        return resultSb.toString();  
    }
    
    private static String byteToHexString(byte b){  
        int n = b;  
        if (n < 0)  
            n = 256 + n;  
        int d1 = n / 16;  
        int d2 = n % 16;  
        return hexDigits[d1] + hexDigits[d2];  
    }
    
    public static String readFile(File f)
    {
        FileReader fr = null;
        BufferedReader br = null;
        char[] buffer = new char[1024];
        StringBuilder sb = new StringBuilder();
        int readnum = -1;
        
        try {
            fr = new FileReader(f);
            br = new BufferedReader(fr);
            
            for (;;) {
                readnum = br.read(buffer, 0, 1024);
                if (readnum == -1) {
                    break;
                }
                sb.append(buffer, 0, readnum);
            }            
            br.close();
            fr.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        return sb.toString();
    }
    
    public static String fileMd5(File f)
    {
        return md5(readFile(f));
    }
    
    public static String convertSeparator(String str)
    {
        if (File.pathSeparatorChar == '\\') {
            return str.replace('/', '\\');
        }
        return str.replace('\\', '/');
    }
    
    public static String[] parseArgs(String[] args) {
        char lastchar;
        if (args.length != 2) {
            System.err.println("args fail");
            System.exit(1);
        }
        if (args[1] != null) {
            lastchar = args[1].charAt(args[1].length() - 1);
            if (lastchar != '/') {
                args[1] += "/";
            }
        } else {
            System.err.println("args fail");
            System.exit(1);
        }
        return args;
    }
}
