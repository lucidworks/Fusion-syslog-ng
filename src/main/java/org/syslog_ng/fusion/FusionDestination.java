package org.syslog_ng.fusion;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.syslog_ng.LogMessage;
import org.syslog_ng.LogTemplate;
import org.syslog_ng.StructuredLogDestination;
import org.syslog_ng.fusion.client.FusionClient;
import org.syslog_ng.logging.SyslogNgInternalLogger;
import org.syslog_ng.options.InvalidOptionException;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class FusionDestination extends StructuredLogDestination {
  private Logger logger;
  private FusionDestinationOptions options;
  private FusionClient client;
//  private FusionPipelineClient pipelineClient;
  private ArrayList<String> buffer;
  private String pipelinePath;

  public FusionDestination(long handle) throws MalformedURLException {
    super(handle);
    buffer = new ArrayList<String>();
    logger = Logger.getRootLogger();
    SyslogNgInternalLogger.register(logger);
    options = new FusionDestinationOptions(this);
    client = new FusionClient(this.options.getNodes(), this.options.getUsername(), this.options.getPassword(), this.options.getSsl(), this.options.getAsync(), this.options.is4x());

//    pipelineClient = new FusionPipelineClient(this.options.getNodes(), this.options.getUsername(), this.options.getPassword(), "native");
//    pipelinePath = "/api/index-pipelines/" + options.getPipeline() + "/collections/" + options.getCollection() + "/index";
  }

  @Override
  protected boolean send(LogMessage message) {
    String resolvedMessage = options.getTemplate().getResolvedString(message, getTemplateOptionsHandle(), LogTemplate.LTZ_SEND);

    try {

      if (buffer.size() >= options.getBatchSize()) {
//        logger.info("Flushing " + buffer.size() + " messages");
        String batch = "[" + StringUtils.join(buffer, ",") + "]";
        List docs = buffer.subList(0, options.getBatchSize()-1);
        for (int i = 0; i < options.getBatchSize(); i++) {
          buffer.remove(0);
        }

        //pipelineClient.postBatchToPipeline(pipelinePath, docs);
        client.write(batch, options.getPipeline(), options.getCollection());
      }

      buffer.add(resolvedMessage);

    } catch (Exception e) {
      logger.error(e);
      return false;
    }

    return true;
  }

  @Override
  protected boolean init() {
    try {
      this.options.init();
    } catch (InvalidOptionException e) {
      logger.error(e);
      return false;
    }

    return true;
  }

  @Override
  protected boolean open() {
    return true;
  }

  @Override
  protected boolean isOpened() {
    return true;
  }

  @Override
  protected String getNameByUniqOptions() {
    return String.format("Fusion-%s-%s", options.getNodes(), options.getPipeline());
  }


  @Override
  protected void deinit() {
    this.options.deinit();
    this.client.close();
//    this.pipelineClient.shutdown();
  }

  @Override
  protected void close() {
    if (buffer.size() > 0) {
      String batch = "[" + StringUtils.join(buffer, ",") + "]";
      try {
        client.write(batch, options.getPipeline(), options.getCollection());
//        pipelineClient.postBatchToPipeline(pipelinePath, buffer);
      } catch (Exception e) {
        logger.error(e);
      }
    }
  }
}
