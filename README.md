# Computer Vision:
"Implementation of vanishing point detection in images through Hough Transform"
Riccardo Crescenti, A.Y. 2020/2021

## Main goal
This program is able to detect a vanishing point of an image, the point in a photo, drawing or painting where parallel lines seem to meet at a distance.

## OpenCV
I used OpenCV library: please download OpenCV-4.5.0 version
https://opencv.org/releases/

The user can type some parameters in order to customize the results
1. Image path
2. Low threshold Canny
3. High threshold Canny
4. Hough threshold


If there are no parameters inserted by the user, these are the default ones:
src/strada_2.png    100 200 110

## Recommended threshold values associated to the specific image:

- src/corridoio.png   100 200 100
- src/strada_1.jpg    100 200 140
- src/strada_2.png    100 200 110
- src/strada_3.jpg    100 200 110
- src/rotaie.png      100 200 140
- src/staccionata.png 100 200 140
