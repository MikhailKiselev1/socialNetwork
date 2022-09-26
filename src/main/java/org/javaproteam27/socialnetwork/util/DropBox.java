package org.javaproteam27.socialnetwork.util;

import com.dropbox.core.*;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.oauth.DbxRefreshResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Locale;

@Component
@Slf4j
@RequiredArgsConstructor
public class DropBox {


    private static String dropboxAppKey = "rr6vbi7z7brhld3";
    private static String dropboxAppSecret = "dguh74c9giceow7";
    private static String refreshToken = "ZdM_cscUFxQAAAAAAAAAAaYgNW47F-G6ZADxwhWouthi_NbOtYP0h68pY9V3FIOu";

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


}
