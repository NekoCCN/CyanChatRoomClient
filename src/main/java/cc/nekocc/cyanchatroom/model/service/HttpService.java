package cc.nekocc.cyanchatroom.model.service;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
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

    public CompletableFuture<Boolean> downloadFile(String file_id, File save_location)
    {
        if (base_url_ == null || base_url_.isBlank())
        {
            return CompletableFuture.failedFuture(new IllegalStateException("HTTP服务的基础URL尚未设置。"));
        }

        String download_url = base_url_ + "/api/files/download/" + file_id;

        return CompletableFuture.supplyAsync(() ->
        {
            try (CloseableHttpClient http_client = HttpClients.createDefault())
            {
                HttpGet http_get = new HttpGet(download_url);
                return http_client.execute(http_get, response ->
                {
                    int status_code = response.getCode();
                    System.out.println("[HTTP] 下载响应状态码: " + status_code);
                    if (status_code == 200)
                    {
                        HttpEntity entity = response.getEntity();
                        if (entity != null)
                        {
                            try (InputStream in_stream = entity.getContent();
                                 FileOutputStream out_stream = new FileOutputStream(save_location))
                            {
                                in_stream.transferTo(out_stream);
                                return true;
                            }
                        }
                    }
                    return false;
                });
            } catch (Exception e)
            {
                e.printStackTrace();
                return false;
            }
        });
    }
}
