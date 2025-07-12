package cc.nekocc.cyanchatroom.model.service;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public class HttpService
{
    private String base_url_;

    public void setBaseUrl(String base_url)
    {
        this.base_url_ = base_url;
    }

    public CompletableFuture<Boolean> uploadFile(String upload_path, File file)
    {
        if (base_url_ == null || base_url_.isBlank())
        {
            return CompletableFuture.failedFuture(new IllegalStateException("Unset base URL for HTTP service."));
        }

        return CompletableFuture.supplyAsync(() ->
        {
            try (CloseableHttpClient http_client = HttpClients.createDefault())
            {
                HttpPost http_post = new HttpPost(base_url_ + upload_path);

                HttpEntity multipart_entity = MultipartEntityBuilder.create()
                        .addPart("file", new FileBody(file))
                        .build();

                http_post.setEntity(multipart_entity);

                return http_client.execute(http_post, response ->
                {
                    System.out.println("Upload response: " + response.getCode());

                    EntityUtils.consume(response.getEntity());
                    return response.getCode() == 200;
                });
            } catch (Exception e)
            {
                e.printStackTrace();
                return false;
            }
        });
    }
}
