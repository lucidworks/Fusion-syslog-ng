This is example code for sending logs from Syslog-ng to Fusion.  It is not officially supported by Lucidworks.

## syslog-ng Setup

The binary versions of syslog-ng provided in public repos are extremely outdated. For this reason, it is best to build from source. You can follow the instructions below.

- https://syslog-ng.com/documents/html/syslog-ng-ose-latest-guides/en/syslog-ng-ose-guide-admin/html/compiling-syslog-ng.html
- https://legacy.gitbook.com/book/syslog-ng/getting-started/details

## Plugin Setup

Add the following to your `syslog.conf` file(s). This file is commonly located at ` /etc/syslog-ng/syslog-ng.conf`. After you have updated configuration file, you will need to restart syslog-ng.

Listen for log messages on tcp port 6001
```
source n_src {
    network(transport(tcp) port(601));
};
```

Route log messages to the Fusion plugin. 
```
destination d_fusion {
  java(
    class_path("/usr/lib/syslog-ng/3.14.1/java-modules/fusion-syslog-ng-0.1.jar")
    class_name("org.syslog_ng.fusion.FusionDestination")
    option("pipeline", "syslog")
    option("collection", "syslog")
    option("nodes", "fusion1:8764,fusion2:8764")
    option("username", "admin")
    option("password", "password")
    option("batch", "1000")
    option("ssl", "false")
    option("async", "false")
    option("4x", "false")
    log-fifo-size(1000)   
  );
};
```

Link the source and destinations and enable [flow-control](https://syslog-ng.com/documents/html/syslog-ng-ose-latest-guides/en/syslog-ng-ose-guide-admin/html/concepts-flow-control.html).
```
log { 
    source(n_src); 
    destination(d_fusion); 
    flags(flow-control); 
};
```

## Testing
Generate some log messages to test your new configuration.
```
loggen --size 300 --rate 10000 --interval 10 127.0.0.1 601
```

## Notes

There is an alternative Fusion client called `FusionPipelineClient` that is not currently being used.