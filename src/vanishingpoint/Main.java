package vanishingpoint;

import org.opencv.core.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("Vanishing Point Detection: \n");

        // load openCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        new VanishingPoint().run(args);
    }
}
