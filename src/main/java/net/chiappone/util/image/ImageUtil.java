package net.chiappone.util.image;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;
import java.io.*;
import java.util.Iterator;

/**
 * @author Kurtis Chiappone
 */
public class ImageUtil {

    public static void draw( Image img, Graphics g, ImageObserver observer, int x, int y ) throws Exception {

        g.drawImage( toBufferedImage( img ), x, y, observer );

    }

    public static boolean hasAlpha( Image image ) throws InterruptedException {

        if ( image instanceof BufferedImage ) {

            BufferedImage bi = (BufferedImage) image;
            return bi.getColorModel().hasAlpha();

        }

        PixelGrabber pg = new PixelGrabber( image, 0, 0, 1, 1, false );
        pg.grabPixels();

        ColorModel cm = pg.getColorModel();
        return cm.hasAlpha();

    }

    public static void save( Image image, File file, String formatName ) throws Exception {

        FileOutputStream os = new FileOutputStream( file );
        ImageIO.write( toBufferedImage( image ), formatName, os );

    }

    public static BufferedImage toBufferedImage( Image image ) throws Exception {

        if ( image instanceof BufferedImage ) {
            return (BufferedImage) image;
        }

        image = new ImageIcon( image ).getImage();

        boolean hasAlpha = hasAlpha( image );

        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

        int transparency = Transparency.OPAQUE;

        if ( hasAlpha ) {

            transparency = Transparency.BITMASK;

        }

        GraphicsDevice gs = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gs.getDefaultConfiguration();
        bimage = gc.createCompatibleImage( image.getWidth( null ), image.getHeight( null ), transparency );

        if ( bimage == null ) {

            int type = BufferedImage.TYPE_INT_RGB;

            if ( hasAlpha ) {

                type = BufferedImage.TYPE_INT_ARGB;

            }

            bimage = new BufferedImage( image.getWidth( null ), image.getHeight( null ), type );

        }

        Graphics g = bimage.createGraphics();
        g.drawImage( image, 0, 0, null );
        g.dispose();

        return bimage;
    }

    public static byte[] toByteArray( Image image ) throws Exception {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write( toBufferedImage( image ), "jpg", bos );
        return bos.toByteArray();

    }

    public static Image toImage( byte[] bytes ) throws IOException {

        ByteArrayInputStream bis = new ByteArrayInputStream( bytes );
        Iterator<?> readers = ImageIO.getImageReadersByFormatName( "jpg" );
        ImageReader reader = (ImageReader) readers.next();
        ImageInputStream iis = ImageIO.createImageInputStream( bis );
        reader.setInput( iis, true );
        ImageReadParam param = reader.getDefaultReadParam();

        return reader.read( 0, param );

    }

}
