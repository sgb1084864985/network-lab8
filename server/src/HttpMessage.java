import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Scanner;

// HttpMessage.java

public class HttpMessage implements Message{
    HttpMethodType hType;
    // String head;
    // byte[] contents;
    long contentLen=0;
    BufferedInputStream contentInput;
    // BufferedOutputStream contentOutput;
    HashMap<String,String> fields=new HashMap<>();
    @Override
    public boolean read(InputStream in) throws IOException {
        BufferedInputStream bufReader = new BufferedInputStream(in);
        StringBuilder stringBuffer=new StringBuilder();
        ///-----------------------------------------
        // BufferedOutputStream logout=null;
        // try{
        //     File file = new File("head.log");
        //     logout=new BufferedOutputStream(new FileOutputStream(file,true));
        // }
        // catch(FileNotFoundException ex){}
        ///-----------------------------------------
        int s1;
        int lineCounter=0;
        boolean lastCarriage=false;
        while(true){
            s1=bufReader.read();
            if(s1==-1){
                //---------------
                // logout.flush();
                // logout.close();
                //---------------
                throw new IOException("socket closed");
            }
            if(s1==0x0d){
                lastCarriage=true;
                continue;
            }
            if(s1==0x0a && lastCarriage==true){
                if(lineCounter==0){
                    break;
                }
                lineCounter=0;
            }
            else lineCounter++;
            lastCarriage=false;

            if(lineCounter>4096){
                System.out.println("head too long!");
            }
            stringBuffer.append((char)s1);
            //---------------------
            // logout.write(s1);
            //---------------------
        }
        Scanner scan = new Scanner(stringBuffer.toString());
        String [] Line =scan.nextLine().split(" ");
        fields.put("!method", Line[0]);
        fields.put("!uri", Line[1]);
        fields.put("!version", Line[2]);
        hType=HttpMethodType.valueOf(Line[0]);

        while(scan.hasNext()){
            Line=scan.nextLine().split(":\\s"); 
            fields.put(Line[0],Line[1]);
        }
        scan.close();
        // head=stringBuffer.toString();

        String len = fields.get("Content-Length");
        int iLen=0;
        if(len!=null && (iLen=Integer.valueOf(len))>0){
            // contents=new byte[iLen];
            // bufReader.read(contents);
            contentLen=iLen;
            contentInput=bufReader;
        }


        ///--------------------
            // logout.write('\n');
            // logout.write('\n');
            // logout.flush();
            // logout.close();
        ///---------------------

        return true;
    }

    HttpMethodType gMethodType(){
        return hType;
    }

    String get(String f){
        return fields.get(f);
    }

    void put(String f,String v){
        fields.put(f, v);
    }

    @Override
    public void send(SocketInfo s)throws IOException{
        PrintWriter output = new PrintWriter(s.out);
        output.printf("%s %s\r\n",fields.get("!version"),fields.get("!status"));
        fields.forEach((key,value)->{
            if(key.charAt(0)!='!'){
                output.printf("%s: %s\r\n",key,value);
            }
        });
        output.printf("\r\n");
        output.flush();
        // if(contents!=null && contents.length>0){
        //     s.out.write(contents);
        //     s.out.flush();
        // }
        if(contentLen>0){
            int l=0;
            int bufSize=1024;
            byte [] buffer = new byte[bufSize];
            do{
                l=contentInput.read(buffer);
                s.out.write(buffer, 0, l);
            }while(l==bufSize);
            s.out.flush();
            contentInput.close();
        }

    }
}

