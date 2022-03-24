import com.google.gson.Gson;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.apache.http.util.EntityUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build();

        HttpGet request = new HttpGet("https://api.nasa.gov/planetary/apod?api_key=hXvge3DXWoBkkqLnPCHzwGbC26OqDIWecWJiaIRP");
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            Gson gson = new Gson();
            Map map = gson.fromJson(EntityUtils.toString(response.getEntity()), Map.class);
            String url = map.get("url").toString();

            try (CloseableHttpResponse response2 = httpClient.execute(new HttpGet(url))) {
                if (response2.getEntity()!= null) {
                    try (FileOutputStream outstream = new FileOutputStream(url.substring( url.lastIndexOf('/')+1))) {
                        response2.getEntity().writeTo(outstream);
                    }
                }
            }
        }
    }
}