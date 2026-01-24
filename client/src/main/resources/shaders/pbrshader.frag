#version 450 core
out vec4 FragColor;
in vec2 TexCoords;
in vec3 WorldPos;
in vec3 Normal;

const float PI = 3.14159265359;

uniform sampler2D albedoMap;
uniform sampler2D normalMap;
uniform sampler2D metallicMap;
uniform sampler2D roughnessMap;
uniform sampler2D aoMap;

uniform vec3 camPos;

float trowbridgeReitz(float n, float m, float roughness) {
    float a2 = roughness * roughness;
    float NdotM = max(dot(n, m), 0.0);
    float NdotM2 = NdotM * NdotM;

    float nom = a2;
    float denom = (NdotM2 * (a2 - 1.0f) + 1.0f);
    denom = PI * denom * denom;

    return nom / denom;
}

float deltaGGXCorrelated(float NdotX, float roughness) {
    float a2 = roughness * roughness;
    float r2 = NdotX * NdotX;
    return (-1 + sqrt(1.0 + a2 * (1.0 - r2) / r2)) * 0.5;
}

float G(vec3 N, vec3 L, vec3 V, float roughness) {
    float NdotL = max(dot(N, L), 0.0);
    float NdotV = max(dot(N, V), 0.0);
    float deltaL = deltaGGXCorrelated(NdotL, roughness);
    float deltaV = deltaGGXCorrelated(NdotV, roughness);
    return 1.0 / (1.0 + deltaL + deltaV);
}

vec3 fresnelSchlick(float cosTheata, vec3 F0) {
    return F0 + (1 - F0) * pow(1 - cosTheata, 5.0);
}

void main() {
    vec3 N = normalize(Normal);
    vec3 V = normalize(camPos - WorldPos);

    vec3 albedo = pow(texture(albedoMap, TexCoords), 2.2);


    vec3 F0 = vec3(0.04);
    F0 = mix(F0, albedo, )
}