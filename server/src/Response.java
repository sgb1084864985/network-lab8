import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.function.Function;

// Response.java

public class Response{
    private static Response singleton;
    private HashMap<String,Function<HttpMessage,HttpMessage>> mapFun = new HashMap<>();

    public static Response getResponse(){
        if(singleton==null){
            singleton=new Response();
        }
        return singleton;
    }

    public HttpMessage apply(HttpMessage t){
        return mapFun.get(t.gMethodType().name()).apply(t);
    }

    private Response(){
        mapFun.put("GET",new HttpGet());
        mapFun.put("POST", new HttpPost());
    }
}

class HttpAnalysisResult{
    HashMap<String,String> fields=new HashMap<>();
    HttpMessage response=new HttpMessage();
    HttpMessage request;

    HttpAnalysisResult(HttpMessage t){
        request=t;
        response.put("!version", t.get("!version"));
    }
    void analysisFileType(){
        String postfix=fields.get("postfix");
        String contentType=null;
        String filePath=null;
        if(postfix.equals("html")||postfix.equals("txt")||postfix.equals("xml")){
            contentType="text";
            // if(postfix.equals("html")) filePath="html";
            // else if(postfix.equals("txt")) filePath="txt";
            filePath=postfix;
        }
        else if(postfix.equals("png")||postfix.equals("jpg")||postfix.equals("gif")){
            contentType="image";
            filePath="img";
        }
        else if(postfix.equals("js")){
        // else{
            contentType="application";
            filePath=postfix;
        }
        fields.put("content-type", contentType);
        fields.put("filePath",filePath);
    }
}

abstract class HttpMethod implements Function<HttpMessage,HttpMessage>{
    // HttpMessage getResponseMessage(HttpMessage t){
    //     HttpMessage msg=new HttpMessage();
    //     msg.put("!version", t.get("!version"));
    //     return msg;
    // }

    File getFile(HttpAnalysisResult r){
        String uri=r.request.get("!uri");
        String paths[]=uri.split("/");
        String filename=paths[paths.length-1];
        String postfix = filename.substring(filename.lastIndexOf(".")+1);
        String filePath=null;
        r.fields.put("uri", uri);
        r.fields.put("postfix",postfix);
        r.analysisFileType();
        filePath=r.fields.get("filePath");
        return new File("../"+filePath+"/"+paths[paths.length-1]);
    }

    File getExecutable(HttpAnalysisResult r){
        String uri=r.request.get("!uri");
        int i = uri.lastIndexOf("/");
        String filename=uri.substring(i+1);

        File app = new File("../app/bin");
        String [] files = app.list((File dir,String name)->{
            // System.out.println(name);
            return name.matches(filename+"\\..*");
        });

        if(files.length==0) return null;

        i=files[0].lastIndexOf(".");

        String postfix="exe";
        if(i!=-1) 
            postfix = files[0].substring(i+1);

        r.fields.put("postfix",postfix);
        r.analysisFileType();
        return new File(app.getAbsoluteFile()+"\\"+files[0]);
    }

    void getResponseContentStream(HttpAnalysisResult analysis,File file){
        try{
            BufferedInputStream input = new BufferedInputStream(new FileInputStream(file));
            String postfix=analysis.fields.get("postfix");
            String contentType=analysis.fields.get("content-type");
            analysis.response.contentInput=input;
            analysis.response.contentLen=file.length();
            analysis.response.put("content-length", Long.toString(analysis.response.contentLen));
            analysis.response.put("content-type", contentType+"/"+postfix);        
        }
        catch(FileNotFoundException ex){
            ex.printStackTrace();
        }
    }
}

class HttpGet extends HttpMethod{
    @Override
    public HttpMessage apply(HttpMessage t) {
        HttpAnalysisResult analysis = new HttpAnalysisResult(t);
        File file = getFile(analysis);
        if(file==null || !file.exists() || !file.isFile()){
            analysis.response.put("!status", "404 Not Found");
        }
        else{
            analysis.response.put("!status", "200 OK");
            getResponseContentStream(analysis, file);
        }
        if(analysis.request.contentLen>0){
            try{
                analysis.request.contentInput.skip(
                    analysis.request.contentLen
                );
            }
            catch(IOException ex){

            }
        }
        return analysis.response;
    }
}

class HttpPost extends HttpMethod{
    @Override
    public HttpMessage apply(HttpMessage t) {
        HttpAnalysisResult analysis = new HttpAnalysisResult(t);
        File file = getExecutable(analysis);
        if(file==null || !file.exists() || !file.isFile()){
            analysis.response.put("!status", "404 Not Found");
        }
        else{
            analysis.response.put("!status", "200 OK");
            String postfix=analysis.fields.get("postfix");
            // String contentType=analysis.fields.get("content-type");
            if(postfix.equals("class")){
                try{
                    
                    URL classUrl= file.getParentFile().toURI().toURL();
                    URL[] urls=new URL[]{classUrl};
                    ClassLoader classLoader=new URLClassLoader(urls);
                    Class<?> cl = classLoader.loadClass(
                        file.getName().substring(0, file.getName().indexOf(".class"))
                    );
                    Method m=cl.getMethod("apply", InputStream.class, Long.class);
                    File returnedFile=(File)m.invoke(
                        null,
                        analysis.request.contentInput,
                        analysis.request.contentLen
                    );
                    analysis.fields.put("postfix",
                        returnedFile.getName().substring(
                            returnedFile.getName().lastIndexOf(".")+1
                        )
                    );
                    analysis.analysisFileType();
                    getResponseContentStream(analysis, returnedFile);
                }
                catch(Exception ex){
                    ex.printStackTrace();
                }
            }
            else{
                analysis.response.put("!status", "404 Not Found");
            }

        }
        return analysis.response;
    }
}