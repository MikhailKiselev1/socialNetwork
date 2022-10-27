package org.javaproteam27.socialnetwork.util;

import com.dropbox.core.*;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.oauth.DbxRefreshResult;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class DropBox {


    private static String dropboxAppKey = "rr6vbi7z7brhld3";
    private static String dropboxAppSecret = "dguh74c9giceow7";
    private static String refreshToken = "ZdM_cscUFxQAAAAAAAAAAaYgNW47F-G6ZADxwhWouthi_NbOtYP0h68pY9V3FIOu";
    private static String dropboxPath = "dropbox/javaproteams_27";
    private String photo = "";

    public static String getRefreshToken() throws DbxException, IOException {
        try {
            DbxCredential credential = new DbxCredential(
                    "", System.currentTimeMillis(), refreshToken, dropboxAppKey, dropboxAppSecret);
            DbxRequestConfig config = new DbxRequestConfig(dropboxAppKey);
            DbxRefreshResult refreshResult = credential.refresh(config);
            refreshToken = refreshResult.getAccessToken();

            DbxAppInfo appInfo = new DbxAppInfo(dropboxAppKey, dropboxAppSecret);
            DbxWebAuth webAuth = new DbxWebAuth(config, appInfo);
            DbxAuthFinish authFinish = webAuth.finishFromCode(refreshToken);
            System.out.println(authFinish.getAccessToken());
        } catch (Exception ignored) {}

        return refreshToken;
    }

    public static String dropBoxUploadImages(MultipartFile image) throws DbxException, IOException {
        Optional<String> originalName = Optional.ofNullable(image.getOriginalFilename());
        String imageName = "/" + UUID.randomUUID() + "." + originalName.orElse("").split("\\.")[1];

        DbxClientV2 client = getClient();
        try (InputStream in = image.getInputStream()) {
            FileMetadata metadata = client.files().uploadBuilder(imageName)
                    .uploadAndFinish(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageName;
    }

    public String getLinkImages(String imageName)  {
        try {
            photo = getClient().files().getTemporaryLink(imageName).getLink();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return photo;
    }

    public static DbxClientV2 getClient() throws IOException, DbxException {
        String token = getRefreshToken();
        DbxRequestConfig config = DbxRequestConfig.newBuilder(dropboxPath).build();
        return new DbxClientV2(config, token);
    }


}
