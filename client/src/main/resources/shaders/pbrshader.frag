#version 450 core
out vec4 FragColor;
in vec2 UV;
in vec3 WorldPos;
in vec3 Normal;
in vec4 Tangent;

const float PI = 3.14159265359;

uniform vec3 cam_pos;
uniform sampler2D albedoMap;
uniform sampler2D normalMap;
uniform sampler2D metallicRoughnessMap;
uniform sampler2D aoMap;
uniform int uHasNormalMap;
uniform int uHasAlbedoTexture;
uniform int uHasMetallicRoughnessTexture;
uniform int uHasTangent;
uniform int uHasUVs;
uniform int uHasNormals;

uniform vec4 uBaseColorFactor;
uniform float uMetallicFactor;
uniform float uRoughness;

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
    float metallic = uMetallicFactor;
    float roughness = uRoughness;
    vec4 albedo = uBaseColorFactor;
    vec3 N;

    if (uHasUVs != 0 && uHasAlbedoTexture) {
        albedo *= texture(albedoMap, UV);
    }

    if (uHasUVs != 0 && uHasMetallicRoughnessTexture !=0) {
        vec4 mr = texture(metallicRoughnessMap, UV);
        roughness *= mr.g;
        metallic *= mr.b;
    }

    if (uHasNormals != 0) {
        N = normalize(Normal);
    } else {
        vec3 dpdx = dFdx(WorldPos);
        vec3 dpdy = dFdy(WorldPos);
        N = normalize(cross(dpdx, dpdy));
    }

    if (uHasNormals != 0 && uHasUVs != 0 && uHasNormalMap != 0 && uHasTangent) {
        vec3 tangentNormal = texture(normalMap, UV).xyz * 2.0 - 1.0;
        vec3 T = normalize(Tangent.xyz);
        vec3 B = Tangent.w * normalize(cross(N, T));
        mat3 TBN = mat3(T, B, N);
        N = normalize(TBN * tangentNormal);
    }

    vec3 V = normalize(cam_pos - WorldPos);

    vec3 F0 = vec3(0.04);
    F0 = mix(F0, albedo.rgb, metallic);
}