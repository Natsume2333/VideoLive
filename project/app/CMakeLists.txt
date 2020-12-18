cmake_minimum_required(VERSION 3.4.1)

set(CMAKE_CXX_STANDARD 11)

set(SOURCE_DIR ${PROJECT_SOURCE_DIR}/src/main/cpp)

aux_source_directory(${SOURCE_DIR} src)
aux_source_directory(${SOURCE_DIR}/agora agora)

add_library(
        apm-plugin-video-preprocessing

        SHARED

        ${src}
        ${agora}
)


target_link_libraries(apm-plugin-video-preprocessing
        log
        )