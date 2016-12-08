package com.lxz.quasar.demo2;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.httpclient.FiberHttpClientBuilder;
import co.paralleluniverse.fibers.okhttp.FiberOkHttpClient;
import co.paralleluniverse.strands.concurrent.CountDownLatch;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by xiaolezheng on 16/12/7.
 */
@Slf4j
public class HttpClientTest {

    public static void main(String[] args) throws Exception {
        //httpClientTest();
        okHttpClientTest();
    }

    private static void okHttpClientTest() throws Exception {
        OkHttpClient client = new FiberOkHttpClient();
        int count = 2000;
        for (int i = 0; i < count; i++) {
            Request request = new Request.Builder()
                    .url("https://www.baidu.com")
                    .build();
            Response response = client.newCall(request).execute();
            int httpCode = response.code();
            log.info("code: {}", httpCode);
        }
    }

    private static void httpClientTest() throws InterruptedException, IOException {
        final CloseableHttpClient client = FiberHttpClientBuilder.
                create(Runtime.getRuntime().availableProcessors() * 2). // use 2 io threads
                setMaxConnPerRoute(1000).
                setMaxConnTotal(3000).setKeepAliveStrategy((response, context) -> 1000 * 60).build();

        log.info("*******************************************************");
        log.info("*******************************************************");
        log.info("*******************************************************");

        int count = 0;
        int concurrencyLevel = 2000;
        final CountDownLatch cdl = new CountDownLatch(concurrencyLevel);
        for (int i = 0; i < concurrencyLevel; i++) {
            //TimeUnit.MILLISECONDS.sleep(1);
            count++;

            new Fiber<Void>(() -> {
                try {
                    HttpGet request = new HttpGet("https://www.baidu.com");
                    HttpResponse httpResponse = client.execute(request);
                    int httpCode = httpResponse.getStatusLine().getStatusCode();
                    log.info("code: {}", httpCode);
                    if (httpCode == 200) {
                        HttpEntity entity = httpResponse.getEntity();
                        //log.info("{}", EntityUtils.toString(entity));
                        EntityUtils.consume(entity);
                    } else {
                        //log.error("error: {}", httpCode);
                    }

                } catch (Exception e) {
                    log.error("", e);
                } finally {
                    cdl.countDown();
                }

            }).start();
        }

        log.info("count: {}", count);
        cdl.await(100, TimeUnit.SECONDS);
        client.close();
    }


}
