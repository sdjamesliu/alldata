add_executable (config-processor config-processor.cpp)
target_link_libraries(config-processor PRIVATE clickhouse_common_config_no_zookeeper_log)
