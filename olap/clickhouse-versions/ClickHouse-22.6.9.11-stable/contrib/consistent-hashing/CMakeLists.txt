add_library(_consistent_hashing consistent_hashing.cpp popcount.cpp)
target_include_directories(_consistent_hashing SYSTEM PUBLIC ${CMAKE_CURRENT_SOURCE_DIR})
add_library(ch_contrib::consistent_hashing ALIAS _consistent_hashing)
