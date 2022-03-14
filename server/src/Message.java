import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
// Message.java

public interface Message{
    public void send(SocketInfo s)throws IOException;
    public boolean read(InputStream in)throws IOException;
}

enum MessageType{
    NORMAL,END,START
}