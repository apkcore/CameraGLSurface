//
// Created by Lenovo on 2020/11/30.
//
#ifndef NATIVEOPENGLDEMO_WLANDROIDLOG_H
#define NATIVEOPENGLDEMO_WLANDROIDLOG_H

#include "android/log.h"

#define LOGD(FORMAT, ...) __android_log_print(ANDROID_LOG_DEBUG,"apkcore-ndk",FORMAT,##__VA_ARGS__);
#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,"apkcore-ndk",FORMAT,##__VA_ARGS__);
#endif //NATIVEOPENGLDEMO_WLANDROIDLOG_H