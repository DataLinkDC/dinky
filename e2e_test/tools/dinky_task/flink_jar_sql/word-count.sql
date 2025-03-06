set 'execution.checkpointing.interval' = '5s';
EXECUTE JAR WITH (
'uri'='rs:/opt/flink/examples/streaming/WordCount.jar',
'main-class'='org.apache.flink.streaming.examples.wordcount.WordCount',
'args'='',
'allowNonRestoredState'='false'
);
