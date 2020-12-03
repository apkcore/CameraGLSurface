package com.apkcore.cameraglsurface.util;

import android.content.res.Resources;
import android.opengl.GLES11Ext;
import android.opengl.GLES30;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

public class ShaderUtils {
    private static final String TAG = "ShaderUtils";

    public static void clear(float red, float green, float blue, float alpha) {
        GLES30.glClearColor(red, green, blue, alpha);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
    }

    public static void glViewport(int width, int height) {
        GLES30.glViewport(0, 0, width, height);
    }

    public static int getOesTexture() {
        int[] texture = new int[1];
        GLES30.glGenTextures(1, texture, 0);
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES30.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
        return texture[0];
    }

    private static int loadShader(int shaderType, String shaderSource) {
        int shader = GLES30.glCreateShader(shaderType);
        if (shader == GLES30.GL_NONE) {
            Log.e(TAG, "create shared failed! type: " + shaderType);
            return GLES30.GL_NONE;
        }
        GLES30.glShaderSource(shader, shaderSource);
        GLES30.glCompileShader(shader);
        // 4. check compile status
        int[] compiled = new int[1];
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == GLES30.GL_FALSE) { // compile failed
            Log.e(TAG, "Error compiling shader. type: " + shaderType + ":");
            Log.e(TAG, GLES30.glGetShaderInfoLog(shader));
            GLES30.glDeleteShader(shader); // delete shader
            shader = GLES30.GL_NONE;
        }
        return shader;
    }

    //https://learnopengl-cn.github.io/01%20Getting%20started/04%20Hello%20Triangle/#_3
    //对了，在把着色器对象链接到程序对象以后，记得删除着色器对象，我们不再需要它们了：
    //glDeleteShader(vertexShader);
    //glDeleteShader(fragmentShader);
    public static int createProgram(String vertexSource, String fragmentSource) {
        int vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == GLES30.GL_NONE) {
            Log.e(TAG, "load vertex shader failed! ");
            return GLES30.GL_NONE;
        }
        int fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentSource);
        if (fragmentShader == GLES30.GL_NONE) {
            Log.e(TAG, "load fragment shader failed! ");
            return GLES30.GL_NONE;
        }
        int program = GLES30.glCreateProgram();
        if (program == GLES30.GL_NONE) {
            Log.e(TAG, "create program failed! ");
            return GLES30.GL_NONE;
        }
        GLES30.glAttachShader(program, vertexShader);
        GLES30.glAttachShader(program, fragmentShader);
        GLES30.glDeleteShader(vertexShader);
        GLES30.glDeleteShader(fragmentShader);
        GLES30.glLinkProgram(program);
        int[] linkStatus = new int[1];
        GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] == GLES30.GL_FALSE) { // link failed
            Log.e(TAG, "Error link program: ");
            Log.e(TAG, GLES30.glGetProgramInfoLog(program));
            GLES30.glDeleteProgram(program); // delete program
            return GLES30.GL_NONE;
        }
        return program;
    }

    public static String loadFromAssets(String fileName, Resources resources) {
        String result = null;
        try {
            InputStream is = resources.getAssets().open(fileName);
            int length = is.available();
            byte[] data = new byte[length];
            is.read(data);
            is.close();
            result = new String(data, "UTF-8");
            result.replaceAll("\\r\\n", "\\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}