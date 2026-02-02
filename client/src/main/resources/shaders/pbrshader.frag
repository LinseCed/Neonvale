#version 330 core
out vec4 FragColor;
in vec2 UV;
in vec3 WorldPos;
in vec3 Normal;
in vec4 Tangent;

const float PI = 3.14159265359;

uniform sampler2D albedoMap;
uniform sampler2D normalMap;
uniform sampler2D metallicRoughnessMap;

uniform vec4 uBaseColorFactor;
uniform float uMetallicFactor;
uniform float uRoughness;

uniform vec3 camPos;

uniform vec3 uLightPosition;
uniform vec3 uLightRadiance;

float trowbridgeReitz(vec3 n, vec3 m, float roughness) {
    float a = roughness * roughness;
    float a2 = a * a;
    float NdotM = max(dot(n, m), 0.0);
    float NdotM2 = NdotM * NdotM;

    float nom = a2;
    float denom = (NdotM2 * (a2 - 1.0f) + 1.0f);
    denom = PI * denom * denom;

    return nom / denom;
}

float deltaGGXCorrelated(float NdotX, float roughness) {
    float a = roughness * roughness;
    float a2 = a * a;
    NdotX = max(NdotX, 0.0000001);
    float r2 = NdotX * NdotX;
    return (-1 + sqrt(1.0 + a2 * (1.0 - r2) / r2)) * 0.5;
}

float Geometry(vec3 N, vec3 L, vec3 V, float roughness) {
    float NdotL = max(dot(N, L), 0.0);
    float NdotV = max(dot(N, V), 0.0);
    float deltaL = deltaGGXCorrelated(NdotL, roughness);
    float deltaV = deltaGGXCorrelated(NdotV, roughness);
    return 1.0 / (1.0 + deltaL + deltaV);
}

vec3 fresnelSchlick(float cosTheata, vec3 F0) {
    return F0 + (1 - F0) * pow(clamp(1 - cosTheata, 0.0, 1.0), 5.0);
}

void main() {
    float metallic = uMetallicFactor;
    float roughness = uRoughness;
    vec4 albedo = uBaseColorFactor;
    vec3 N;

    albedo *= texture(albedoMap, UV);

    vec4 mr = texture(metallicRoughnessMap, UV);
    roughness *= mr.g;
    roughness = clamp(roughness, 0.04, 1.0);
    metallic *= mr.b;

    N = normalize(Normal);

    vec3 tangentNormal = texture(normalMap, UV).xyz * 2.0 - 1.0;
    vec3 T = normalize(Tangent.xyz);
    T = normalize(T - N * dot(N, T));
    vec3 B = normalize(cross(N, T) * Tangent.w);
    mat3 TBN = mat3(T, B, N);
    N = normalize(TBN * tangentNormal);
    vec3 V = normalize(camPos - WorldPos);

    vec3 F0 = vec3(0.04);
    F0 = mix(F0, albedo.rgb, metallic);

    vec3 LO = vec3(0.0);

    vec3 L = normalize(uLightPosition - WorldPos);
    vec3 H = normalize(V + L);
    float distance = length(uLightPosition - WorldPos);
    float attenuation = 1.0 / max((distance * distance), 0.0000001);
    vec3 radiance = uLightRadiance * attenuation;

    float NDF = trowbridgeReitz(N, H, roughness);
    float G = Geometry(N, L, V, roughness);
    vec3 F = fresnelSchlick(max(dot(H, V), 0.0), F0);

    vec3 numerator = NDF * G * F;
    float denominator = 4.0 * max(dot(N, V), 0.0) * max(dot(N, L), 0.0) + 0.000001;
    vec3 specular = numerator / denominator;

    vec3 kD = vec3(1.0) - F;

    kD *= 1.0 - metallic;

    float NdotL = max(dot(N, L), 0.0);

    LO += (kD * vec3(albedo) / PI + specular) * radiance * NdotL;

    vec3 ambient = vec3(0.03) * vec3(albedo);

    vec3 color = ambient + LO;

    color = pow(color, vec3(1.0/2.2));

    FragColor = vec4(color, 1.0);
}