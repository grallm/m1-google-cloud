package endpoint;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.*;
import org.apache.commons.io.input.ReaderInputStream;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;


@Api(name = "instaCrash", version = "v1",
        audiences = "616939906371-cnpc0ocrc71ae3hbann3glgiktfgregk.apps.googleusercontent.com",
        clientIds = "616939906371-cnpc0ocrc71ae3hbann3glgiktfgregk.apps.googleusercontent.com",
        namespace = @ApiNamespace(ownerDomain = "tinycrash.ew.r.appspot.com", ownerName = "tinycrash.ew.r.appspot.com", packagePath = ""))
public class UploadEndpoint {

//    private final GcsService gcsService = GcsServiceFactory.createGcsService(new RetryParams.Builder()
//            .initialRetryDelayMillis(10)
//            .retryMaxAttempts(10)
//            .totalRetryPeriodMillis(15000)
//            .build());

    public String uploadFile(String content, String fileName) throws IOException {
        String bucketName = "tinycrash-img";
        String projectId = "tinycrash";
        String jsonPath = "src/main/java/endpoint/tinycrash-0a72d5e26ea0.json";
        File jsonFile = new File(jsonPath);

        StorageOptions storageOptions = StorageOptions.newBuilder()
                .setProjectId(projectId)
                .setCredentials(GoogleCredentials.getApplicationDefault())
                .build();
        Storage storage = storageOptions.getService();

        byte[] decodedImg = new byte[0];
        String encodedImg = "";
        System.out.println("CONTENT SUB : " + content.substring(0, 100));


        String partSeparator = ",";
        if (content.contains(partSeparator)) {
            encodedImg = content.split(partSeparator)[1];
            decodedImg = Base64.getDecoder().decode(encodedImg.getBytes(UTF_8));
        }

        Image img = ImagesServiceFactory.makeImage(decodedImg);

        //String imageURL = "https://storage.cloud.google.com/" + bucketName + "/" + fileName + ".jpeg";

        //storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("image/jpeg").build();
        Blob blob = storage.create(blobInfo, img.getImageData());


//        gcsService.createOrReplace(
//                new GcsFilename(bucketName, fileName + ".jpeg"),
//                new GcsFileOptions.Builder().mimeType("image/jpg").build(),
//                ByteBuffer.wrap(img.getImageData()));

        System.out.println("File uploaded to bucket " + bucketName + " as " + fileName + " link to asset " + blob.getMediaLink());

        return blob.getMediaLink();
    }
}

