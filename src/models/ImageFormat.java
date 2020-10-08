package models;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class ImageFormat {
    public static String keyToBase64(String key) {
        Regions clientRegion = Regions.US_EAST_1;
        String bucketName = "nightlife-images";
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
        S3Object o;
        try {
            o = s3.getObject(bucketName, key);
        }
        catch (AmazonS3Exception e) {
            return "";
        }
        String base64 = "";
        try {
            BufferedImage imgBuf = ImageIO.read(o.getObjectContent());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(imgBuf, "PNG", out);
            byte[] bytes = out.toByteArray();
            base64 = new String(Base64.getEncoder().encode(bytes), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return base64;
    }
}
