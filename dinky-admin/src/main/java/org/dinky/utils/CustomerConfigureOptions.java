package org.dinky.utils;

import org.apache.flink.configuration.ConfigOption;

import static org.apache.flink.configuration.ConfigOptions.key;

public class CustomerConfigureOptions {
    public static final ConfigOption<String> REST_TARGET_DIRECTORY =
            key("rest.target-directory")
                    .stringType()
                    .noDefaultValue()
                    .withDescription(
                            "for set savepoint target directory");

    public static final ConfigOption<Boolean> REST_CANCEL_JOB =
            key("rest.cancel-job")
                    .booleanType()
                    .defaultValue(false)
                    .withDescription(
                            "for cancel job when trigger savepoint");

    public static final ConfigOption<String> REST_TRIGGER_ID =
            key("rest.triggerId")
                    .stringType()
                    .noDefaultValue()
                    .withDescription(
                            "for trigger savepoint");

    public static final ConfigOption<String> REST_FORMAT_TYPE =
            key("rest.formatType")
                    .stringType()
                    .defaultValue("DEFAULT")
                    .withDescription(
                            "for savepoint format type");
}
