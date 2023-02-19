package git.dimitrikvirik.userapi.utils;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.*;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageUtils {

	public static void rescale(String filename, int maxWidth, int compression, MultipartFile file) throws IOException {
		BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
		int originalWidth = bufferedImage.getWidth();
		int originalHeight = bufferedImage.getHeight();
		int type = bufferedImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : bufferedImage.getType();

		double wH = (double) originalWidth / (double) originalHeight;

		int width = Math.min(originalWidth, maxWidth);
		int height = (int) (width / wH);

		if (bufferedImage.getColorModel().hasAlpha()) {
			type = BufferedImage.TYPE_INT_RGB;
		}

		BufferedImage resizedImage = new BufferedImage(width, height, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(bufferedImage, 0, 0, width, height, null);
		g.dispose();
		g.setComposite(AlphaComposite.Src);

		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if (filename.contains("png")) {
			compressByPng(resizedImage, filename, compression);
			return;
		}

		compress(filename, compression, resizedImage);
	}

	private static void compress(String filename, int compression, BufferedImage bi) throws IOException {
		ImageWriter jpegWriter = ImageIO.getImageWritersByFormatName("jpeg").next();

		ImageWriteParam param = jpegWriter.getDefaultWriteParam();
		param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		param.setCompressionQuality(0.1f * compression);

		try (FileImageOutputStream output = new FileImageOutputStream(new File(filename))) {
			jpegWriter.setOutput(output);
			jpegWriter.write(null, new IIOImage(bi, null, null), param);
		} finally {
			jpegWriter.dispose();
		}
	}
	private static void compressByPng(BufferedImage pngImage, String fileName, int compression) throws IOException {
		try (ImageOutputStream out = ImageIO.createImageOutputStream(new File(fileName))) {
			ImageTypeSpecifier type = ImageTypeSpecifier.createFromRenderedImage(pngImage);
			ImageWriter writer = ImageIO.getImageWriters(type, "png").next();
			ImageWriteParam param = writer.getDefaultWriteParam();

			if (param.canWriteCompressed()) {
				param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				param.setCompressionQuality(0.1f * compression);
			}

			writer.setOutput(out);
			writer.write(null, new IIOImage(pngImage, null, null), param);
			writer.dispose();
		}
	}

}
