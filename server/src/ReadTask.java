import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Stack;
// ReadTask.java

public class ReadTask implements Runnable{
    MessageListener ls;
    Stack<SocketInfo> FreeList;
    SocketInfo wSocketInfo;
    InputStream MessageInStream;
    boolean running=true;
    ReadTask(Stack<SocketInfo> fl,MessageListener ls){
        this.ls=ls;
        this.FreeList=fl;
    }
    void stop(){
        running=false;
        synchronized(FreeList){
            FreeList.notify();
        }
    }
    @Override
    public void run() {
        while(running){
            synchronized(FreeList){
                while(FreeList.isEmpty()){
                    try{
                        FreeList.wait();
                    }
                    catch(InterruptedException ex){
                    }
                    finally{
                        if(running==false) return;
                    }
                } 
                wSocketInfo=FreeList.pop();
            }

            try{
                MessageInStream=
                    // new BufferedInputStream(
                        wSocketInfo.s.getInputStream()
                    // )
                ;
                System.out.println("client processing");
                ls.read(wSocketInfo, MessageInStream);
            }
            catch(IOException ex){
                ex.printStackTrace();
                break;
            }
            finally{
            }
        }
    }
}