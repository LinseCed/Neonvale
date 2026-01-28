#version 330 core

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aNormal;
layout (location = 2) in vec2 aUV;
layout (location = 3) in vec4 aTangent;

uniform mat4 uProj;
uniform mat4 uView;
uniform mat4 uModel;
uniform mat3 uNormalMatrix;

out vec3 Normal;
out vec3 WorldPos;
out vec2 UV;
out vec4 Tangent;

void main(){
    UV = aUV;
    vec3 T = normalize(uNormalMatrix * aTangent.xyz);
    Tangent = vec4(T, aTangent.w);
    WorldPos = vec3(uModel * vec4(aPos, 1.0));
    Normal = normalize(uNormalMatrix * aNormal);
    gl_Position = uProj * uView * uModel * vec4(aPos, 1.0);
}