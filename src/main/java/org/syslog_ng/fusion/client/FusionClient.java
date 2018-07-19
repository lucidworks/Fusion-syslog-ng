package org.syslog_ng.fusion.client;

import org.apache.log4j.Logger;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import static org.asynchttpclient.Dsl.asyncHttpClient;

public class FusionClient {
  protected Logger logger = Logger.getRootLogger();
  private AsyncHttpClient client;

  private String authorization;
  private String[] nodes;
  private boolean ssl;
  private boolean async;
  private boolean is4x;

  public FusionClient(String nodes, String username, String password, boolean ssl, boolean async, boolean is4x) {
    this.nodes = nodes.split(",");
    this.authorization = "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
    this.client = asyncHttpClient();
    this.ssl = ssl;
    this.async = async;
    this.is4x = is4x;
  }

  public void write(String payload, String indexPipeline, String collection) throws ExecutionException, InterruptedException {
    int nodeIndex = 0;
    if (nodes.length > 1) {
      nodeIndex = getRandomNumberInRange(0, this.nodes.length - 1);
    }

    String protocol = "http";
    if(ssl) {
      protocol = "https";
    }
    String url = protocol + "://" + this.nodes[nodeIndex] + "/api/" + (is4x ? "" : "apollo/") + "index-pipelines/" + indexPipeline + "/collections/" + collection + "/index?_cookie=false&echo=false";

    ListenableFuture<Response> fut = client
        .preparePost(url)
        .setHeader("Authorization", authorization)
        .setHeader("Content-Type", "application/json")
        .setBody(payload)
        .execute();


    if(!async) {
      Response response = fut.get();
      logger.info(response.getStatusCode());
    }

  }


  public void close() {
    try {
      this.client.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static int getRandomNumberInRange(int min, int max) {

    if (min >= max) {
      throw new IllegalArgumentException("max must be greater than min");
    }

    Random r = new Random();
    return r.nextInt((max - min) + 1) + min;
  }
}
