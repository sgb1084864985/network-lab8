
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class dopost {
    public static File apply(InputStream input,Long length){
        // BufferedInputStream bInput=new BufferedInputStream(in, size)
        String passwd="1094";
        String uid="3190101094";
        StringBuilder buffer= new StringBuilder();
        HashMap<String,String> fields = new HashMap<>();
        int count=0;
        boolean reading=true;
        try{
            while(true){
                while(true){
                    count++;
                    if(count>length){
                        reading=false;
                        break;
                    }
                    int readChar=input.read();
                    
                    if(readChar=='&'){
                        break;
                    }
                    buffer.append((char)readChar);
                }
                String []entry=buffer.toString().split("=");
                if(entry.length!=2){
                    return new File("../html/login_illegal.html");
                }
                fields.put(entry[0], entry[1]);
                if(!reading) break;
                buffer.setLength(0);
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        
        String _passwd=fields.get("pass");
        String _uid=fields.get("login");
        if(_passwd!=null && _passwd.equals(passwd) && _uid!=null && _uid.equals(uid)){
            return new File("G:\\java\\os_lab8\\server\\html\\login_success.html");
        }
        return new File("G:\\java\\os_lab8\\server\\html\\login_fail.html");
    }
}
