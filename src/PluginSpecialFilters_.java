import ij.plugin.PlugIn;
import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;

import java.awt.AWTEvent;
import ij.IJ;
import ij.process.ImageProcessor;

public class PluginSpecialFilters_ implements PlugIn{	
	private GenericDialog gui;
	private ImagePlus image;
	private ImageProcessor processorManipulate, processorOriginal;	
	
	public void run(String arg) {		
		image = IJ.getImage();
		processorManipulate = image.getProcessor();
		processorOriginal = image.duplicate().getProcessor();	
		if (image.getType() != ImagePlus.GRAY8) {
			IJ.error("In order to run this plugin, the image must be Type GRAY8");
		}
		else this.imageChange();		
	}	

	public String showGUIReturnMethod(){
		String[] methods = {"passa-baixa","passa-alta","borda"};
		this.gui = new GenericDialog("Linear Filters");		
		gui.addRadioButtonGroup("Available methods", methods, 3, 1, methods[0]);		
		gui.showDialog();		
		if (gui.wasOKed()) {			
			return gui.getNextRadioButton();
		}
		else return null;		
	}
	
	public void imageChange() {
		float[][] kernelPassaBaixa = {{0.11f,0.11f,0.11f},{0.11f,0.11f,0.11f},{0.11f,0.11f,0.11f}};
		float[][] kernelPassaAlta = {{-1,-1,-1},{-1,8,-1},{-1,-1,-1}};
		float[][] kernelBorda = {{1,1,1},{1,-2,-1},{1,-1,-1}}; //noroeste
		String method = this.showGUIReturnMethod();
		switch (method) {
		case "passa-baixa":
			imageFilter(kernelPassaBaixa);
			break;
		case "passa-alta":
			imageFilter(kernelPassaAlta);
			break;
		case "borda":
			imageFilter(kernelBorda);
			break;
		default:
			IJ.log("Plugin cancelled");
			break;			
		}
		image.updateAndDraw();
	}
	
	public void imageFilter(float[][] kernel) {
		int width = image.getWidth(), height = image.getHeight();
		double sum;
		int[][] sourcePixels = new int[3][3];
		for (int x = 1; x < width-1; x++) {
			for (int y = 1; y < height-1; y++) {
				sum = 0;
				for (int i=0;i<3;i++)
					for (int j=0;j<3;j++) {
						sourcePixels[i][j] = processorOriginal.getPixel(x-1+i, y-1+j);
						sum += (double) (sourcePixels[i][j]*kernel[i][j]);
					}				
				processorManipulate.putPixel(x, y, pixelValidation((int) sum));				
			}
		}		
	}
	
	private int pixelValidation(int pixel) {
		if (pixel > 255) {
			return 255;
		}
		else if (pixel < 0) {
			return 0;
		}
		else return pixel;
	}
	
}
	