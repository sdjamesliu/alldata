add_executable(clickhouse-zookeeper-cli zookeeper-cli.cpp)
target_link_libraries(clickhouse-zookeeper-cli PRIVATE clickhouse_common_zookeeper_no_log)
