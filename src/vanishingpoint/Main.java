package vanishingpoint;

import org.opencv.core.*;

public class Main {

    public static void main(String[] args) {
        // write your code here
        System.out.println("hello World");
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        //new HoughCirclesRun().run(args);
        //new HoughLinesRun().run(args);
        new VanishingPoint().run(args);

    }
}
