import java.io.InputStream;

// MessageListener.java

public interface MessageListener{
    void read(SocketInfo s,InputStream in);
}