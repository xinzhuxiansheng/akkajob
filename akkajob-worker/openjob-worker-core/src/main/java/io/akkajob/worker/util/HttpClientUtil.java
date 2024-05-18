package io.akkajob.worker.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.akkajob.common.constant.StatusEnum;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Objects;

public class HttpClientUtil {

    /**
     * Http client
     */
    private static final CloseableHttpClient HTTP_CLIENT;

    static {
        HTTP_CLIENT = HttpClients.createDefault();
    }

    /**
     * Get
     *
     * @param requestConfig requestConfig
     * @param url           url
     * @param typeReference typeReference
     * @param <T>           t
     * @return T
     * @throws IOException IOException
     */
    public static <T> T get(RequestConfig requestConfig, String url, TypeReference<T> typeReference) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        try {
            response = HTTP_CLIENT.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String body = EntityUtils.toString(entity, "UTF-8");
            int statusCode = response.getStatusLine().getStatusCode();
            if (!StatusEnum.SUCCESS.getStatus().equals(statusCode)) {
                throw new RuntimeException(String.format("Get failed! status=%d %s", statusCode, body));
            }

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(body, typeReference);
        } finally {
            if (Objects.nonNull(response)) {
                response.close();
            }
        }
    }
}
