import java.util.LinkedList;

// MessageQueue.java

public class MessageQueue{
    LinkedList<SocketInfo> q_sock=new LinkedList<>();
    LinkedList<Message> q_msg=new LinkedList<>();
    
    boolean isEmpty(){
        return q_sock.isEmpty();
    }

    void push(SocketInfo s,Message m){
        q_sock.add(s);
        q_msg.add(m);
    }

    synchronized void pushWithNotify(SocketInfo s,Message m){
        push(s, m);
        notify();
    }

    SocketInfo peepSocketInfo(){
        return q_sock.getFirst();
    }
    Message peepMessage(){
        return q_msg.getFirst();
    }

    void pop(){
        q_sock.removeFirst();
        q_msg.removeFirst();
    }
}