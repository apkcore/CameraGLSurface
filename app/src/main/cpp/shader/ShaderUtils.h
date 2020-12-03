//
// Created by Lenovo on 2020/11/30.
//

#ifndef OPENGLTRIANGLE_SHADERUTILS_H
#define OPENGLTRIANGLE_SHADERUTILS_H

#include <stdlib.h>
#include <android/asset_manager_jni.h>
#include "GLES3/gl3.h"
#include <GLES2/gl2ext.h>
#include "../log/ApkcoreLog.h"

static void clear(float red, float green, float blue, float alpha) {
    glClearColor(red, green, blue, alpha);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
}

static void glViewport(int width, int height) {
    glViewport(0, 0, width, height);
}

static GLuint getOesTexture() {
    GLuint textures;
    glGenTextures(1, &textures);
    glBindTexture(GL_TEXTURE_EXTERNAL_OES, textures);
    glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    return textures;
}

static GLuint loadShader(GLenum shaderType, const char *shaderSource) {
    int shader = glCreateShader(shaderType);
    if (shader == GL_NONE) {
        LOGE("create shader failed! shaderType: %d", shaderType);
        return GL_NONE;
    }
    glShaderSource(shader, 1, &shaderSource, 0);
    glCompileShader(shader);
    GLint compiled;
    glGetShaderiv(shader, GL_COMPILE_STATUS, &compiled);
    if (compiled == GL_FALSE) { // compile failed
        GLint len = 0;
        glGetShaderiv(shader, GL_INFO_LOG_LENGTH, &len);
        if (len > 1) {
            char *log = static_cast<char *>(malloc(sizeof(char) * len));
            glGetShaderInfoLog(shader, len, NULL, log);
            LOGE("Error compiling shader: %s", log);
            free(log);
        }
        glDeleteShader(shader); // delete shader
        return GL_NONE;
    }
    return shader;
}

static GLuint createProgram(const char *vertexSource, const char *fragmentSource) {
    int vertexShader = loadShader(GL_VERTEX_SHADER, vertexSource);
    if (vertexShader == GL_NONE) {
        LOGE("load vertex shader failed! ");
        return GL_NONE;
    }
    int fragmentShader = loadShader(GL_FRAGMENT_SHADER, fragmentSource);
    if (fragmentShader == GL_NONE) {
        LOGE("load fragment shader failed! ");
        return GL_NONE;
    }
    int program = glCreateProgram();
    if (program == GL_NONE) {
        LOGE("create program failed! ");
        return GL_NONE;
    }
    glAttachShader(program, vertexShader);
    glAttachShader(program, fragmentShader);
    // 把着色器对象链接到程序对象以后，记得删除着色器对象，我们不再需要它们了
    glDeleteShader(vertexShader);
    glDeleteShader(fragmentShader);
    glLinkProgram(program);
    GLint linked;
    glGetProgramiv(program, GL_LINK_STATUS, &linked);
    if (linked == GL_FALSE) { // link failed
        GLint len = 0;
        glGetProgramiv(program, GL_INFO_LOG_LENGTH, &len);
        if (len > 1) {
            char *log = static_cast<char *>(malloc(sizeof(char) * len));
            glGetProgramInfoLog(program, len, NULL, log);
            LOGE("Error link program: %s", log);
            free(log);
        }
        glDeleteProgram(program); // delete program
        return GL_NONE;
    }
    return program;
}

static char *readAssetFile(const char *filename, AAssetManager *mgr) {
    if (mgr == NULL) {
        LOGE("pAssetManager is null!");
        return NULL;
    }
    AAsset *pAsset = AAssetManager_open(mgr, filename, AASSET_MODE_UNKNOWN);
    off_t len = AAsset_getLength(pAsset);
    char *pBuffer = (char *) malloc(len + 1);
    pBuffer[len] = '\0';
    int numByte = AAsset_read(pAsset, pBuffer, len);
    LOGD("numByte: %d, len: %d", numByte, len);
    AAsset_close(pAsset);
    return pBuffer;
}

#endif //OPENGLTRIANGLE_SHADERUTILS_H
