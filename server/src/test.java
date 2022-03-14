import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.function.LongPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

// test.java

public class test{


    static void t5() throws Exception{
        File app = new File("G:/java/os_lab8/server/app");
        System.out.println(app.getAbsolutePath());        
    }
    public static void main(String[] args) throws Exception{
        t5();
    }
}