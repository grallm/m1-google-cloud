package endpoint;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.tools.cloudstorage.*;
import com.google.cloud.storage.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Base64;


@Api(name = "instaCrash", version = "v1",
        audiences = "616939906371-cnpc0ocrc71ae3hbann3glgiktfgregk.apps.googleusercontent.com",
        clientIds = "616939906371-cnpc0ocrc71ae3hbann3glgiktfgregk.apps.googleusercontent.com",
        namespace = @ApiNamespace(ownerDomain = "tinycrash.ew.r.appspot.com", ownerName = "tinycrash.ew.r.appspot.com", packagePath = ""))
public class UploadEndpoint {

    private final GcsService gcsService = GcsServiceFactory.createGcsService(new RetryParams.Builder()
            .initialRetryDelayMillis(10)
            .retryMaxAttempts(10)
            .totalRetryPeriodMillis(15000)
            .build());

    public String uploadFile(String content, String fileName) throws IOException {

        String bucketName = "tinycrash.appspot.com";

        System.out.println(content.substring(0, 100));
        byte[] decodedString = Base64.getDecoder().decode(content);
        Image img = ImagesServiceFactory.makeImage(decodedString);

        String imageURL = "https://storage.cloud.google.com/" + bucketName + "/" + fileName + ".jpeg";

        gcsService.createOrReplace(
                new GcsFilename(bucketName, fileName + ".jpeg"),
                new GcsFileOptions.Builder().mimeType("image/jpg").build(),
                ByteBuffer.wrap(img.getImageData()));

        System.out.println("File " + content + " uploaded to bucket " + bucketName + " as " + fileName + " link to asset " + imageURL);

        return imageURL;
    }
}

