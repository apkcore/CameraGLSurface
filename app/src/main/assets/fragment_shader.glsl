#version 300 es
#extension GL_OES_EGL_image_external : require
precision mediump float;
in vec2 textureCoordinate;
uniform samplerExternalOES s_texture;
out vec4 fragColor;
void main() {
    fragColor = texture(s_texture, textureCoordinate);
}
//#version 100
//#extension GL_OES_EGL_image_external : require
//precision mediump float;
//varying vec2 textureCoordinate;
//uniform samplerExternalOES s_texture;
//
//void main() {
//    gl_FragColor = texture2D(s_texture, textureCoordinate);
//}