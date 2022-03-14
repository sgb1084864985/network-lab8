import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.net.Socket;

// SocketInfo.java

public class SocketInfo implements Comparable<SocketInfo>{
    Socket s;
    BufferedOutputStream out;
    SocketInfo(Socket s,OutputStream out){
        this.s=s;
        this.out=new BufferedOutputStream(out);
    }
    @Override
    public int compareTo(SocketInfo o) {
        if(s.hashCode()<o.s.hashCode()) return -1;
        else if(s.hashCode()>o.s.hashCode()) return 1;
        else{
            if(s.equals(o.s)) return 0;
            return -1;
        }
    }
}