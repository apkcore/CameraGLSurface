package com.apkcore.cameraglsurface.natives;

import android.content.res.AssetManager;

public class CameraNativeDrawer {
    static {
        System.loadLibrary("native-lib");
    }
    public native int getOesTexture();

    public native void registerAssetManager(AssetManager assetManager);

    public native void surfaceCreate();

    public native void surfaceChange(int width, int height);

    public native void surfaceDraw(boolean isFrontCamera);
}
