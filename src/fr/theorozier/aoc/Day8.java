package fr.theorozier.aoc;

import java.io.IOException;

public class Day8 {
	
	private static class SpaceImageFormat {
		
		private final int width, height;
		private final int size;
		private final SpaceImageLayer[] layers;
		
		SpaceImageFormat(int width, int height, SpaceImageLayer[] layers) {
			
			this.width = width;
			this.height = height;
			this.size = width * height;
			this.layers = layers;
			
		}
		
		public PixelColor[] computeImage() {
		
			PixelColor[] pixels = new PixelColor[this.size];
			char value;
			
			for (int i = 0; i < this.size; ++i) {
				
				for (SpaceImageLayer layer : this.layers) {
					
					value = layer.getPixelAt(i);
					
					if (value == '2')
						continue;
					
					switch (value) {
						case '0':
							pixels[i] = PixelColor.BLACK;
							break;
						case '1':
							pixels[i] = PixelColor.WHITE;
							break;
					}
					
					break;
					
				}
			
			}
			
			return pixels;
		
		}
		
	}
	
	private static class SpaceImageLayer {
		
		private final String data;
		
		SpaceImageLayer(String data) {
			
			this.data = data;
			
		}
		
		public int getCharCount(char c) {
			return (int) this.data.chars().filter(ch -> ch == c).count();
		}
		
		public char getPixelAt(int idx) {
			return this.data.charAt(idx);
		}
		
	}
	
	private enum PixelColor {
		BLACK, WHITE;
	}
	
	public static void main(String[] args) throws IOException {
	
		String content = Utils.getFileContent("day8input");
	
		int imageWidth = 25;
		int imageHeight = 6;
		
		int imageSize = imageWidth * imageHeight;
		
		int size = content.length() / imageSize;
		SpaceImageLayer[] layers = new SpaceImageLayer[size];
		
		int minZeroCount = Integer.MAX_VALUE;
		int minZeroLayerIdx = -1;
		
		String raw;
		int zeroCount = 0;
		
		for (int i = 0, ri; i < size; ++i) {
			
			ri = i * imageSize;
			raw = content.substring(ri, ri + imageSize);
			
			layers[i] = new SpaceImageLayer(raw);
			zeroCount = layers[i].getCharCount('0');
			
			if (zeroCount < minZeroCount) {
				
				minZeroCount = zeroCount;
				minZeroLayerIdx = i;
				
			}
			
		}
		
		if (minZeroLayerIdx != -1) {
			
			SpaceImageLayer layer = layers[minZeroLayerIdx];
			int answer = layer.getCharCount('1') * layer.getCharCount('2');
			
			System.out.println("Min zero count : " + zeroCount);
			System.out.println("Answer : " + answer);
			
		}
		
		SpaceImageFormat image = new SpaceImageFormat(imageWidth, imageHeight, layers);
		PixelColor[] computed = image.computeImage();
		
		int l = 0;
		for (PixelColor pixelColor : computed) {
			
			if (pixelColor == PixelColor.BLACK) {
				System.out.print(' ');
			} else {
				System.out.print('█');
			}
			
			if (++l == imageWidth) {
				System.out.println();
				l = 0;
			}
			
		}
		
		
	}
	
}
