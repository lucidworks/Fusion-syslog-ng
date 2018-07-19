package org.syslog_ng.fusion;

import org.syslog_ng.LogDestination;
import org.syslog_ng.options.BooleanOptionDecorator;
import org.syslog_ng.options.IntegerOptionDecorator;
import org.syslog_ng.options.InvalidOptionException;
import org.syslog_ng.options.Option;
import org.syslog_ng.options.Options;
import org.syslog_ng.options.RequiredOptionDecorator;
import org.syslog_ng.options.StringOption;
import org.syslog_ng.options.TemplateOption;

public class FusionDestinationOptions {
  public static String TEMPLATE = "template";
  public static String TEMPLATE_DEFAULT = "$(format-json --scope rfc5424)";
  public static String NODES = "nodes";
  public static String USERNAME = "username";
  public static String PASSWORD = "password";
  public static String PIPELINE = "pipeline";
  public static String COLLECTION = "collection";
  public static String BATCH_SIZE = "batch";
  public static String BATCH_SIZE_DEFAULT = "25";
  public static String SSL = "ssl";
  public static String ASYNC = "async";
  public static String IS_4X = "4x";


  private Options options;

  public FusionDestinationOptions(LogDestination owner) {
    options = new Options();

    options.put(new TemplateOption(owner.getConfigHandle(), new RequiredOptionDecorator(new StringOption(owner, TEMPLATE, TEMPLATE_DEFAULT))));
    options.put(new RequiredOptionDecorator(new StringOption(owner, NODES)));
    options.put(new RequiredOptionDecorator(new StringOption(owner, USERNAME)));
    options.put(new RequiredOptionDecorator(new StringOption(owner, PASSWORD)));
    options.put(new RequiredOptionDecorator(new StringOption(owner, PIPELINE)));
    options.put(new RequiredOptionDecorator(new StringOption(owner, COLLECTION)));
    options.put(new RequiredOptionDecorator(new IntegerOptionDecorator(new StringOption(owner, BATCH_SIZE))));
    options.put(new RequiredOptionDecorator(new BooleanOptionDecorator(new StringOption(owner, SSL))));
    options.put(new RequiredOptionDecorator(new BooleanOptionDecorator(new StringOption(owner, ASYNC))));
    options.put(new BooleanOptionDecorator(new StringOption(owner, IS_4X, "true")));
  }

  public void init() throws InvalidOptionException {
    options.validate();
  }

  public void deinit() {
    options.deinit();
  }

  public Option getOption(String name) {
    return options.get(name);
  }

  public TemplateOption getTemplate() {
    return options.getTemplateOption(TEMPLATE);
  }

  public String getNodes() {
    return options.get(NODES).getValue();
  }

  public String getUsername() {
    return options.get(USERNAME).getValue();
  }

  public String getPassword() {
    return options.get(PASSWORD).getValue();
  }

  public String getPipeline() {
    return options.get(PIPELINE).getValue();
  }

  public String getCollection() {
    return options.get(COLLECTION).getValue();
  }

  public int getBatchSize() {
    return options.get(BATCH_SIZE).getValueAsInteger();
  }

  public boolean getSsl() { return options.get(SSL).getValueAsBoolean(); }

  public boolean getAsync() { return options.get(ASYNC).getValueAsBoolean(); }

  public boolean is4x() { return options.get(IS_4X).getValueAsBoolean();}
}
