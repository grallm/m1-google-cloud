package utils;

import com.google.appengine.api.appidentity.AppIdentityService;
import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.auth.Credentials;
import com.google.auth.appengine.AppEngineCredentials;
import com.google.cloud.datastore.DatastoreOptions;
import endpoints.repackaged.com.google.api.client.auth.oauth2.Credential;

public class DatastoreUtils {


    void getDatastore() {
        AppIdentityService appIdentityService = AppIdentityServiceFactory.getAppIdentityService();


        Credentials credentials = com.google.auth.appengine.AppEngineCredentials.newBuilder()
                .setScopes()
                .setAppIdentityService()
                .build();

        DatastoreOptions options = DatastoreOptions.newBuilder()
                .setCredentials(credentials)
                .setProjectId("tinycrash")
                .namespace(NAMESPACE)
                .build();
        Datastore datastore = options.service();
    }
}
