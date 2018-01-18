# cmake version

cmake_minimum_required(VERSION 3.4.1)

# 支持 -std=gnu++11

set(CMAKE_VERBOSE_MAKEFILE on)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")

# opencv import libs

set(pathToOpenCV ./src/main/jni/include)
include_directories( ${pathToOpenCV} )
add_library( lib_opencv SHARED IMPORTED )
set_target_properties( lib_opencv PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/src/main/OpenCVLib/${ANDROID_ABI}/libopencv_java.so )

# build application's shared lib