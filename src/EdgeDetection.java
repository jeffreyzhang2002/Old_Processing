import processing.core.PApplet;
import processing.core.PImage;
import processing.video.*;

import java.util.Arrays;

public class EdgeDetection extends PApplet
{
    private Capture video;
    private PImage frame;

    public static void main(String[] args)
    {
        System.out.println("Start");
        PApplet.main("EdgeDetection");
    }

    public void settings()
    {
        size(640, 480);
    }

    public void setup()
    {
        System.out.println("Running Setup");
        String[] cameras = new String[0];
        while(cameras.length == 0) {
            cameras = Capture.list();
            System.out.println("Here: " + Arrays.toString(cameras));
        }
        video = new Capture(this, cameras[0]);
        video.start();
    }



    public void draw() {
        if(video.available())
            video.read();

        loadFrame();
        image(blur(frame), 0 , 0);

    }

    public PImage convolution(PImage img, int[][] mat, double scale)
    {
        int total = 0;
        for(int i = 0; i < mat.length; i++)
            for(int j = 0; j < mat[0].length; j++)
                total += mat[i][j];


        PImage output = createImage(img.width, img.height, RGB);
        output.loadPixels();
        for(int i = 0; i < img.width; i++)
            for(int j = 0; j < img.height; j++) {

                int sum = 0;

                for (int k = 0; k < 3; k++)
                    for (int l = 0; l < 3; l++) {
                        int xpos = i - 1 + k;
                        int ypos = j - 1 + l;
                        sum += (xpos < 0 || xpos >= img.width || ypos < 1 || ypos >= img.height)?
                                0 : img.pixels[xpos + ypos * img.width] * mat[k][l];
                    }
                output.pixels[i + j * output.width] = color( (float)(sum / total * scale));
            }

        return output;
    }

    public PImage blur(PImage input)
    {
        int[][] kernel = new int[][]{{1,2,1},{2,4,2},{1,2,1}};

        PImage img = createImage(input.width, input.height, RGB);
        input.loadPixels();
        img.loadPixels();
        for (int x = 1; x < video.width - 1; x++)
        {
            for(int y = 1; y < video.height - 1; y++)
            {
                int sum = 0;
                    sum += input.pixels[location(x - 1, y - 1, img)] * kernel[0][0];
                    sum += input.pixels[location(x - 1, y, img)] * kernel[0][1];
                    sum += input.pixels[location(x - 1, y + 1, img)] * kernel[0][2];
                    sum += input.pixels[location(x, y - 1, img)] * kernel[1][0];
                    sum += input.pixels[location(x, y, img)] * kernel[1][1];
                    sum += input.pixels[location(x, y + 1, img)] * kernel[1][2];
                    sum += input.pixels[location(x + 1, y - 1, img)] * kernel[2][0];
                    sum += input.pixels[location(x + 1, y, img)] * kernel[2][1];
                    sum += input.pixels[location(x + 1, y + 1, img)] * kernel[2][2];


                int avg = sum/16;

                img.pixels[location(x, y, img)] = color(avg,avg,avg);
            }
        }
        return img;
    }

    public PImage sobel(PImage input)
    {
        int[][][] kernel = new int[][][]{{{1,2,1},{0,0,0},{-1,-2,-1}},{{1,0,-1},{2,0,-2},{1,0,-1}}};

        PImage img = createImage(input.width, input.height, RGB);
        input.loadPixels();
        img.loadPixels();
        for (int x = 1; x < video.width - 1; x++)
        {
            for(int y = 1; y < video.height - 1; y++)
            {
                int sum = 0;
                for(int i = 0; i < 2; i++) {
                    sum += input.pixels[location(x - 1, y - 1, img)] * kernel[i][0][0];
                    sum += input.pixels[location(x - 1, y, img)] * kernel[i][0][1];
                    sum += input.pixels[location(x - 1, y + 1, img)] * kernel[i][0][2];
                    sum += input.pixels[location(x, y - 1, img)] * kernel[i][1][0];
                    sum += input.pixels[location(x, y, img)] * kernel[i][1][1];
                    sum += input.pixels[location(x, y + 1, img)] * kernel[i][1][2];
                    sum += input.pixels[location(x + 1, y - 1, img)] * kernel[i][2][0];
                    sum += input.pixels[location(x + 1, y, img)] * kernel[i][2][1];
                    sum += input.pixels[location(x + 1, y + 1, img)] * kernel[i][2][2];
                }

                int avg = sum/18;

                img.pixels[location(x, y, img)] = color(avg,avg,avg);
            }
        }
        return img;
    }

    public int location(int x, int y, PImage img)
    { return x + y * img.width; }

    public void loadFrame()
    {
        frame = createImage(video.width, video.height, RGB);
        video.loadPixels();
        frame.loadPixels();
        for (int x = 0; x < video.width; x++) {
            for (int y = 0; y < video.height; y++) {
                int loc = x + y * video.width;
                float r = red(video.pixels[loc]);
                float g = green(video.pixels[loc]);
                float b = blue(video.pixels[loc]);

                r = constrain(r, 0, 255);
                g = constrain(g, 0, 255);
                b = constrain(b, 0, 255);

                frame.pixels[loc] = color(r, g, b);
            }
        }
        frame.updatePixels();
    }

    public void keyPressed()
    {
        switch(key)
        {
            case 'q':
                System.out.println("|===== Camera Data =====|");
                System.out.println("Width: " + video.width);
                System.out.println("Height: " + video.height);
        }
    }
}