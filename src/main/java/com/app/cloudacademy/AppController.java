package com.app.cloudacademy;

import java.io.IOException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.opencensus.common.Scope;
import io.opencensus.exporter.trace.stackdriver.StackdriverTraceConfiguration;
import io.opencensus.exporter.trace.stackdriver.StackdriverTraceExporter;
import io.opencensus.trace.Tracer;
import io.opencensus.trace.Tracing;
import io.opencensus.trace.samplers.Samplers;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigInteger;

@RestController
public class AppController  {

  @Autowired
  private ApplicationContext context;

  private static final Tracer tracer = Tracing.getTracer();

  /*  Add the Trace Exporter Code here */
  static {
    try {
      System.out.println("Trace Exporter Registered");
      StackdriverTraceExporter.createAndRegister(
        StackdriverTraceConfiguration.builder()
        .build());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @RequestMapping("/start")
  public static String start() throws InterruptedException {

    try (Scope scope = tracer.spanBuilder("TraceSpan").setSampler(Samplers.alwaysSample()).startScopedSpan()) {
      tracer.getCurrentSpan().addAnnotation("Thread Sleep 2000ms Created");

      /* Add the Sleep Timer here */
      Thread.sleep(2000);

      tracer.getCurrentSpan().addAnnotation("Performing BigInteger Multiplications");
      
      BigInteger fact = BigInteger.valueOf(10000000);
      for (int i = 1; i <= 10000000; i++) {
        fact = fact.multiply(BigInteger.valueOf(i));
      }

      tracer.getCurrentSpan().addAnnotation("Finished");

    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    return "API Triggered";
  }

  @RequestMapping("/shut")
  public String close() {
    SpringApplication.exit(context);
    return "Application Shutting Down";
  }


}
