#version 330 core

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aNorm;
layout (location = 2) in vec2 aTexCoords;

out vec2 TexCoords;

uniform mat4 uProj;
uniform mat4 uView;
uniform mat4 uModel;

void main() {
    TexCoords = aTexCoords;
    gl_Position = uProj  * uView * uModel * vec4(aPos, 1.0);
}