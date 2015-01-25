import java.awt.event.*;
import java.awt.*;
import java.applet.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import java.util.*;
class Utility
{
	public static ArrayList<File> getFileList(String directory, String ext, String prefix)
    {
        ArrayList<File> list = new ArrayList<File>();
        File folder = new File(directory);
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
          if (listOfFiles[i].isFile() && listOfFiles[i].getName().indexOf(ext) != -1 &&listOfFiles[i].getName().indexOf(prefix) != -1 ) {
              String filePath  = directory + "/" + listOfFiles[i].getName();
              list.add(new File(filePath));
              
          }
        }
        Collections.sort(list, new StringLengthComparator());
        return list;
    }

    public static double[][] readDepthImage(File f)
    {
        double[][] depth = new double[320][240];
        Scanner fromFile = OpenFile.openToRead(f);
        while(fromFile.hasNext())
        {
            String temp = fromFile.nextLine();
            int x = Integer.parseInt(temp.substring(0,temp.indexOf(",")));
            int y = Integer.parseInt(temp.substring(temp.indexOf(",")+1, temp.lastIndexOf(",")));
            double z = Double.parseDouble(temp.substring(temp.lastIndexOf(",")+1,temp.length()));
            depth[x][y] = z;
        }
        return depth;
    }

    public static BufferedImage loadImage(File jpgFile)
    {
        BufferedImage img = null;
         try {
             img = ImageIO.read(jpgFile);
             return img;
        } catch (IOException e) {
            return null;
        }
    }

    public static BufferedImage d2ArrToBufferedImage(double[][] depth)
    {
        BufferedImage img = new BufferedImage(depth.length,depth[0].length,BufferedImage.TYPE_INT_RGB);
        for(int x = 0; x<depth.length; x++)
        {
            for(int y = 0; y<depth[0].length; y++)
            {
                int color = (int)(255 * depth[x][y]/1000.0);
                img.setRGB(x,y,color);
            }
        }
        return img;
    }

    public static void goToSleep()
    {
         try {
                Thread.sleep(50);
            }catch(InterruptedException ex){}   
    }

    public static void d2ArrToCSV(double[][] depthImage, String fileName)
    {
        PrintWriter outFile = OpenFile.openToWrite(fileName);  
        for(int x = 0; x<depthImage.length;x++)
        {
            for(int y =0; y<depthImage[x].length;y++)
            {
                if(depthImage[x][y] != 0.0)
                {
                    outFile.println(y + "," + x + "," + depthImage[x][y]);
                }
            }
        }
        outFile.flush();
        outFile.close();

    }

    public static int getFileNumber(String fileName)
    {
       int x = fileName.lastIndexOf("_");
       int y = fileName.lastIndexOf(".");
       int a = Integer.parseInt(fileName.substring(x+1,y));
       return a;
    }

    public static double[][] subtractBackground(double[][] backgroundImage, double[][] handsImage)
    {
        double[][] difference = new double[320][240];
        double[][] foreground = new double[320][240];
     //   img2 = depthImageToBufferedImage(handsImage);
        for(int a = 0; a<handsImage.length;a++)
        {
            for(int b = 0; b<handsImage[a].length; b++)
            {
                if(handsImage[a][b] != 0 && backgroundImage[a][b] != 0) {
                    difference[a][b] = Math.abs(handsImage[a][b] - backgroundImage[a][b]);
                }
                if(difference[a][b] < 100 || handsImage[a][b] > 505)
                {
                     difference[a][b] = 0;
                }
                if(difference[a][b] != 0)
                {
                    foreground[a][b] = handsImage[a][b];
                }
            }

        }
        return foreground;
       
    } 

	public static double[][][] bufferedImagetoArray3D(BufferedImage b) {
			double[][][] rtn = new double[b.getHeight()][b.getWidth()][3];
			for(int y=0; y < b.getHeight(); y++) {
				for(int x=0; x < b.getWidth(); x++) {
					Color c = new Color(b.getRGB(x,y));
					rtn[y][x][0] = c.getRed();
					rtn[y][x][0] = c.getGreen();
					rtn[y][x][0] = c.getBlue();					
				}
			}
			return rtn;
	}
	
	public static BufferedImage array3DToBufferedImage(double[][][] arr) {
		BufferedImage bi = new BufferedImage(arr[0].length, arr.length, BufferedImage.TYPE_INT_ARGB);
		for(int y=0; y < arr.length; y++) {
			for(int x=0; x < arr[y].length; x++) {
				int r = (int) arr[y][x][0];
				int g = (int) arr[y][x][1];
				int b = (int) arr[y][x][2];
				int rgb = ((r & 0x0ff) << 16) | ((g & 0x0ff) << 8) | (b & 0x0ff);
				bi.setRGB(x,y,rgb);
			}
		}
		return bi;
	} 
	
	public static void writeImage(BufferedImage bi, String file) {
		try {
	    	File outputfile = new File(file);
	    	ImageIO.write(bi, "png", outputfile);
		} 
		catch (IOException e) {
	    	e.printStackTrace();
		}
	}
	
	public static void writeImage(double[][][] image, String file) {
		BufferedImage bi = array3DToBufferedImage(image);
		writeImage(bi,file);
	}
	
}