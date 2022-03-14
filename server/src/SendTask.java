import java.io.IOException;

public class SendTask implements Runnable{
    MessageQueue mq;
    boolean running=true;
    boolean interrupted=false;
    SendTask(MessageQueue mq){
        this.mq=mq;
    }

    void interrupt(){
        synchronized(mq){
            interrupted=true;
            mq.notify();
        }
    }
    void stop(){
        running=false;
        interrupt();
    }
    boolean isInterrupted(){
        return interrupted;
    }

    @Override
    public void run() {
        Message m=null;
        SocketInfo s=null;
        while(running){
            synchronized(mq){
                while(mq.isEmpty()){
                    try{
                        mq.wait();
                    }
                    catch(InterruptedException ex){
    
                    }
                    finally{
                        if(isInterrupted()){return;}
                    }
                }
                m=mq.peepMessage();
                s=mq.peepSocketInfo();
                mq.pop();
            }
            try{
                m.send(s);
            }
            catch(IOException ex){
                System.out.println("send failed.");
            }
        }
    }
}
