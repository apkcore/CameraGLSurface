package com.apkcore.cameraglsurface.camera;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import com.apkcore.cameraglsurface.util.ShaderUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class CameraDrawer {

    private static final float[] VERTEXES = {
            -1.0f, 1.0f,
            -1.0f, -1.0f,
            1.0f, -1.0f,
            1.0f, 1.0f,
    };

    // 后置摄像头使用的纹理坐标
    private static final float[] TEXTURE_BACK = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
    };

    // 前置摄像头使用的纹理坐标
    private static final float[] TEXTURE_FRONT = {
            1.0f, 1.0f,
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
    };
    private static final byte[] VERTEX_ORDER = {0, 1, 2, 3};
    private final int mProgram;
    private final int mPositionHandle;
    private final int mTextureHandle;
    private FloatBuffer mVertexBuffer;
    private FloatBuffer mBackTextureBuffer;
    private FloatBuffer mFrontTextureBuffer;
    private ByteBuffer mDrawListBuffer;

    public CameraDrawer(Context context) {
        mVertexBuffer = ByteBuffer.allocateDirect(VERTEXES.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mVertexBuffer.put(VERTEXES).position(0);
        mBackTextureBuffer = ByteBuffer.allocateDirect(TEXTURE_BACK.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mBackTextureBuffer.put(TEXTURE_BACK).position(0);
        mFrontTextureBuffer = ByteBuffer.allocateDirect(TEXTURE_FRONT.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mFrontTextureBuffer.put(TEXTURE_FRONT).position(0);

        mDrawListBuffer = ByteBuffer.allocateDirect(VERTEX_ORDER.length)
                .order(ByteOrder.nativeOrder());
        mDrawListBuffer.put(VERTEX_ORDER).position(0);
        String vertexShader = ShaderUtils.loadFromAssets("vertex_shader.glsl", context.getResources());
        String fragmentShader = ShaderUtils.loadFromAssets("fragment_shader.glsl", context.getResources());

        mProgram = ShaderUtils.createProgram(vertexShader, fragmentShader);
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        mTextureHandle = GLES20.glGetAttribLocation(mProgram, "inputTextureCoordinate");
    }

    public void draw(int textureId, boolean isFrontCamera) {
        GLES20.glUseProgram(mProgram);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);

        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, mVertexBuffer);

        GLES20.glEnableVertexAttribArray(mTextureHandle);
        if (isFrontCamera) {
            GLES20.glVertexAttribPointer(mTextureHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, mFrontTextureBuffer);
        } else {
            GLES20.glVertexAttribPointer(mTextureHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, mBackTextureBuffer);
        }
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_FAN, VERTEX_ORDER.length, GLES20.GL_UNSIGNED_BYTE, mDrawListBuffer);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTextureHandle);
    }

}
