#include <jni.h>
#include <string>
#include "log/ApkcoreLog.h"
#include "shader/ShaderUtils.h"

static float VERTEXES[] = {
        -1.0f, 1.0f,
        -1.0f, -1.0f,
        1.0f, -1.0f,
        1.0f, 1.0f,
};

// 后置摄像头使用的纹理坐标
static float TEXTURE_BACK[] = {
        0.0f, 1.0f,
        1.0f, 1.0f,
        1.0f, 0.0f,
        0.0f, 0.0f,
};

// 前置摄像头使用的纹理坐标
static float TEXTURE_FRONT[] = {
        1.0f, 1.0f,
        0.0f, 1.0f,
        0.0f, 0.0f,
        1.0f, 0.0f,
};

static GLbyte VERTEX_ORDER[] = {0, 1, 2, 3};
AAssetManager *g_pAssetManager = NULL;
GLuint program;
GLuint textureId;
GLuint mPositionHandle = 0;
GLuint mTextureHandle = 1;

extern "C"
JNIEXPORT jint JNICALL
Java_com_apkcore_cameraglsurface_natives_CameraNativeDrawer_getOesTexture(JNIEnv *env,
                                                                          jobject thiz) {
    textureId = getOesTexture();
    return textureId;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_apkcore_cameraglsurface_natives_CameraNativeDrawer_registerAssetManager(JNIEnv *env,
                                                                                 jobject thiz,
                                                                                 jobject asset_manager) {
    if (asset_manager) {
        g_pAssetManager = AAssetManager_fromJava(env, asset_manager);
    } else {
        LOGE("assetManager is null!")
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_apkcore_cameraglsurface_natives_CameraNativeDrawer_surfaceCreate(JNIEnv *env,
                                                                          jobject thiz) {
    char *vertexShaderSource = readAssetFile("vertex_shader.glsl", g_pAssetManager);
    char *fragmentShaderSource = readAssetFile("fragment_shader.glsl", g_pAssetManager);
    program = createProgram(vertexShaderSource, fragmentShaderSource);
    if (program == GL_NONE) {
        LOGE("gl init faild!");
    }
//    vPosition = glGetAttribLocation(program, "vPosition");
//    LOGD("vPosition: %d", vPosition);
    clear(0.0f, 0.0f, 0.0f, 1.0f); // 背景颜色设置为黑色 RGBA (range: 0.0 ~ 1.0)
}

extern "C"
JNIEXPORT void JNICALL
Java_com_apkcore_cameraglsurface_natives_CameraNativeDrawer_surfaceChange(JNIEnv *env, jobject thiz,
                                                                          jint width, jint height) {
    glViewport(width, height);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_apkcore_cameraglsurface_natives_CameraNativeDrawer_surfaceDraw(JNIEnv *env, jobject thiz,
                                                                        jboolean is_front_camera) {
    glUseProgram(program);
    glEnable(GL_CULL_FACE);
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_EXTERNAL_OES, textureId);

    glEnableVertexAttribArray(mPositionHandle);
    glVertexAttribPointer(mPositionHandle, 2, GL_FLOAT, GL_FALSE, 2 * 4, VERTEXES);

    glEnableVertexAttribArray(mTextureHandle);
    if (is_front_camera) {
        glVertexAttribPointer(mTextureHandle, 2, GL_FLOAT, GL_FALSE, 2 * 4, TEXTURE_FRONT);
    } else {
        glVertexAttribPointer(mTextureHandle, 2, GL_FLOAT, GL_FALSE, 2 * 4, TEXTURE_BACK);
    }
    glDrawElements(GL_TRIANGLE_FAN, 4, GL_UNSIGNED_BYTE, VERTEX_ORDER);
    glDisableVertexAttribArray(mPositionHandle);
    glDisableVertexAttribArray(mTextureHandle);
}