package vanishingpoint;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;



public class VanishingPoint {

    public void run(String[] args) {
        // Dichiarazione variabili
        Mat cdst = new Mat();

        int lowThresholdCanny;
        int highThresholdCanny;
        int houghThreshold;

        int DEFAULT_LTC = 100;
        int DEFAULT_HTC = 200;
        int DEFAULT_HOUGH = 110;

        String defaultImage = "src/strada_2.png";
        String imagePath;

        /*
         *   Check parametri inseriti da utente:
         *   primo parametro: image path
         *   secondo parametro: soglia Low Canny edge detector
         *   terzo parametro: soglia High Canny edge detector
         *   quarto parametro: soglia Hough Transform
         */

        int nArgs = args.length;

        // in base al numero di parametri inseriti da utente assegno i valori soglia
        switch (nArgs){
            case 1:
                imagePath = args[0];
                lowThresholdCanny = DEFAULT_LTC;
                highThresholdCanny = DEFAULT_HTC;
                houghThreshold = DEFAULT_HOUGH;
                break;

            case 2:
                imagePath = args[0];
                try {
                    Integer.parseInt(args[1]);
                } catch (NumberFormatException e){
                    System.err.println("Incorrect threshold values");
                    return;
                }
                lowThresholdCanny = Integer.parseInt(args[1]);
                highThresholdCanny = DEFAULT_HTC;
                houghThreshold = DEFAULT_HOUGH;
                break;

            case 3:
                imagePath = args[0];
                try {
                    Integer.parseInt(args[1]);
                    Integer.parseInt(args[2]);
                } catch (NumberFormatException e){
                    System.err.println("Incorrect threshold values");
                    return;
                }
                lowThresholdCanny = Integer.parseInt(args[1]);
                highThresholdCanny = Integer.parseInt(args[2]);
                houghThreshold = DEFAULT_HOUGH;
                break;

            case 4:
                imagePath = args[0];
                try {
                    Integer.parseInt(args[1]);
                    Integer.parseInt(args[2]);
                    Integer.parseInt(args[3]);
                } catch (NumberFormatException e){
                    System.err.println("Incorrect threshold values");
                    return;
                }
                lowThresholdCanny = Integer.parseInt(args[1]);
                highThresholdCanny = Integer.parseInt(args[2]);
                houghThreshold = Integer.parseInt(args[3]);
                break;

            default:
                imagePath = defaultImage;
                lowThresholdCanny = DEFAULT_LTC;
                highThresholdCanny = DEFAULT_HTC;
                houghThreshold = DEFAULT_HOUGH;
        }



        // Carico l'immagine e la converto in scala di grigi
        Mat src = Imgcodecs.imread(imagePath, Imgcodecs.IMREAD_GRAYSCALE);

        // Controllo se l'immagine è stata caricata correttamente
        if (src.empty()) {
            System.out.println("Error opening image! Wrong path");
            System.exit(-1);
        }
        System.out.println("-Image: " + imagePath);
        System.out.println("-Image Parameters:\n\tHeight: "+src.rows() +"\tWidth: "+ src.cols());
        System.out.println("-Threshold Parameters:\n " + "\tLow Threshold Canny \t" + lowThresholdCanny);
        System.out.println("\tHigh Threshold Canny \t" +highThresholdCanny);
        System.out.println("\tHough Threshold \t" +houghThreshold);

        Mat srcBlur= new Mat();
        Mat cannyBlurred= new Mat();
        Size BLUR_SIZE= new Size(3,3);

        // filtro smoothing e Canny
        Imgproc.blur(src, srcBlur, BLUR_SIZE);
        Imgproc.Canny(srcBlur, cannyBlurred, lowThresholdCanny, highThresholdCanny, 3, true);

        // Copy edges to the images that will display the results in BGR
        Imgproc.cvtColor(src, cdst, Imgproc.COLOR_GRAY2BGR);

        // Applico la trasformata Hough e salvo i risultati nella variabile lines
        Mat lines = new Mat();
        Imgproc.HoughLines(cannyBlurred, lines, 1, Math.PI/180, houghThreshold);

        // parametersRhoTheta salva i parametri rho e theta delle rette
        // intersections salva i punti di intersezioni tra le rette
        ArrayList<Point> parametersRhoTheta = new ArrayList<>();
        ArrayList<Point> intersections = new ArrayList<>();

        draw_lines(cdst, lines, parametersRhoTheta);
        draw_intersections(cdst, intersections, parametersRhoTheta);

        System.out.println("-Intersections detected: " + intersections.size());
        /*for (Point p:intersections) {
            System.out.println("coordinate intersezione: "+p);
        }*/

        draw_vanishingPoint(cdst,intersections);

        HighGui.imshow("Greyscale Image", src);

        HighGui.imshow("Canny Edge Detector", cannyBlurred);

        HighGui.imshow("Vanishing Point", cdst);

        HighGui.waitKey();
        System.exit(0);
    }



    private void draw_vanishingPoint(Mat cdst, ArrayList<Point> intersections) {
        if (intersections.size()==1){
            Imgproc.circle(cdst, intersections.get(0), 15, new Scalar(0, 255, 0), 2, 8, 0);
        }

        else if (intersections.size()>1){

            Imgproc.circle(cdst, compute_vanishing_point(intersections), 15, new Scalar(0, 255, 0), 2, 8, 0);

        } else {

            System.out.println("There are no intersections! Please decrease threshold values");
            System.exit(-1);

        }
    }


    private void draw_intersections(Mat cdst, ArrayList<Point> intersections, ArrayList<Point> parametersRhoTheta) {

        for(int i=0; i<parametersRhoTheta.size(); i++) {
            for (int j = i + 1; j < parametersRhoTheta.size(); j++) {

                if (i != j) {
                    if (parametersRhoTheta.get(i).y != parametersRhoTheta.get(j).y) {

                        Point intersectionPoint = compute_intersections(parametersRhoTheta.get(i).x, parametersRhoTheta.get(i).y, parametersRhoTheta.get(j).x, parametersRhoTheta.get(j).y);
                        intersections.add(intersectionPoint);
                        Imgproc.circle(cdst, intersectionPoint, 2, new Scalar(255, 0, 0), 2, 8, 0);

                    }
                }
            }
        }

    }


    private void draw_lines(Mat cdst, Mat lines, ArrayList<Point> parametersRhoTheta) {

        for (int x = 0; x < lines.rows(); x++) {

            // valori di theta in radianti, rho in pixel
            double rho = lines.get(x, 0)[0];
            double theta = lines.get(x, 0)[1];

            // vengono escluse rette verticali o orizzontali
            if(theta>0.1 && theta<1.50 || theta>1.65 && theta<3.05) {
                double a = Math.cos(theta), b = Math.sin(theta);
                double x0 = a * rho, y0 = b * rho;

                int LINE_LENGTH = 1000;
                // moltiplicazione per 1000 (LINE_LENGTH) per definire gli "estremi delle rette" pt1, pt2 da disegnare

                Point pt1 = new Point(Math.round(x0 + LINE_LENGTH * (-b)), Math.round(y0 + LINE_LENGTH * (a)));
                Point pt2 = new Point(Math.round(x0 - LINE_LENGTH * (-b)), Math.round(y0 - LINE_LENGTH * (a)));

                // metodo per disegnare le rette
                Imgproc.line(cdst, pt1, pt2, new Scalar(0, 0, 255), 1, Imgproc.LINE_4, 0); //Imgproc.LINE_AA

                //System.out.println("rho " + rho);
                //System.out.println("theta " + theta);
                Point p = new Point(rho, theta);
                parametersRhoTheta.add(p);
            }
        }
    }


    // metodo che calcola il punto (x,y) di intersezione tra due rette parametrizzate da rho e theta
    // tramite risoluzione di Cramer
    public Point compute_intersections(double rho1, double theta1, double rho2, double theta2) {
        Point pt= new Point();

        double cosT1 = Math.cos(theta1); //a
        double sinT1 = Math.sin(theta1); //b
        double cosT2 = Math.cos(theta2); //c
        double sinT2 = Math.sin(theta2); //d

        double determinant = cosT1*sinT2 - sinT1*cosT2;

        // verifica che il determinante sia diverso da zero, in quel caso calcolo il punto di intersezione
        if(determinant!=0){
            pt.x = (sinT2*rho1 - sinT1*rho2) / determinant;
            pt.y = (-cosT2*rho1 + cosT1*rho2) / determinant;
        }
        // System.out.println("r1 "+ rho1+"t1 "+theta1+"r2 "+rho2+"t2 "+theta2);
        return pt;
    }



    // metodo che stima la posizione del punto di fuga tramite criterio di distanza minima
    public Point compute_vanishing_point(ArrayList<Point> list){

        Point vanishingP = new Point(list.get(0).x,list.get(0).y);
        double minDist= Math.sqrt(Math.pow(list.get(0).x-list.get(1).x,2) + Math.pow(list.get(0).y - list.get(1).y,2));
        double distanza = minDist;

        double[] sommadistanze = new double[list.size()];
        double sommaminima;

        //System.out.println(minDist);

        for(int i=0; i<list.size(); i++)
            for (int j =0; j<list.size(); j++){
                distanza= Math.sqrt(Math.pow(list.get(i).x-list.get(j).x,2) + Math.pow(list.get(i).y - list.get(j).y,2));
                sommadistanze[i]+=distanza;
            }
        //System.out.println("vettore somma distanze: ");
        sommaminima=sommadistanze[0];

        for (int i=0;i<sommadistanze.length-1;i++){
            //System.out.println(sommadistanze[i] +"  " +sommadistanze.length);
            if (sommadistanze[i]<sommaminima){
                sommaminima=sommadistanze[i];
                vanishingP= list.get(i);
            }
        }

        System.out.println("\n-Coordinate of the Vanishing Point " + vanishingP);
        return vanishingP;
    }

}
