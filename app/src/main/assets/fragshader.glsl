uniform sampler2D texture;

varying vec2 textureCoords;

void main() {
    gl_FragColor = texture2D(texture, textureCoords);
}