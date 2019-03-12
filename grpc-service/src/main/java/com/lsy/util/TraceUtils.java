package com.lsy.util;

import io.opentracing.util.GlobalTracer;

/**
 * Created by lsy on 2019/3/12.
 */
public class TraceUtils {
        public static void registerTracer(String serviceName) {
            io.jaegertracing.Configuration config = new io.jaegertracing.Configuration(serviceName);
            io.jaegertracing.Configuration.SenderConfiguration sender = new io.jaegertracing.Configuration.SenderConfiguration();
            /**
             *  从https://tracing-analysis.console.aliyun.com/ 获取jaegerd的网关（Endpoint）
             *  第一次运行时，请设置当前用户的对应的网关
             */
            sender.withEndpoint("http://tracing-analysis-dc-hz.aliyuncs.com/adapt_abcdefg@123456_abcdefg@123456/api/traces");
            config.withSampler(new io.jaegertracing.Configuration.SamplerConfiguration().withType("const").withParam(1));

            config.withReporter(new io.jaegertracing.Configuration.ReporterConfiguration().withSender(sender).withMaxQueueSize(10000));
            GlobalTracer.register(config.getTracer());

    }

}
