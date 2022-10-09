#ifdef GL_ES
#define LOWP lowp
#define MED mediump
#define HIGH highp
precision mediump float;
#else
#define MED
#define LOWP
#define HIGH
#endif

#ifdef emissiveTextureFlag
varying MED vec2 v_emissiveUV;
#endif

#ifdef emissiveTextureFlag
uniform sampler2D u_emissiveTexture;
#endif

////////////////////////////////////////////////////////////////////////////////////
////////// POSITION ATTRIBUTE - FRAGMENT
////////////////////////////////////////////////////////////////////////////////////
#define nop() {}

varying vec4 v_position;
vec4 g_position = vec4(0.0, 0.0, 0.0, 1.0);
#define pullPosition() (g_position = v_position)

////////////////////////////////////////////////////////////////////////////////////
////////// COLOR ATTRIBUTE - FRAGMENT
///////////////////////////////////////////////////////////////////////////////////
varying vec4 v_color;
vec4 g_color = vec4(1.0, 1.0, 1.0, 1.0);
#define pullColor()	(g_color = v_color)

////////////////////////////////////////////////////////////////////////////////////
////////// NORMAL ATTRIBUTE - FRAGMENT
///////////////////////////////////////////////////////////////////////////////////
varying vec3 v_normal;
vec3 g_normal = vec3(0.0, 0.0, 1.0);
#define pullNormal() (g_normal = v_normal)

////////////////////////////////////////////////////////////////////////////////////
////////// BINORMAL ATTRIBUTE - FRAGMENT
///////////////////////////////////////////////////////////////////////////////////
varying vec3 v_binormal;
vec3 g_binormal = vec3(0.0, 0.0, 1.0);
#define pullBinormal() (g_binormal = v_binormal)

////////////////////////////////////////////////////////////////////////////////////
////////// TANGENT ATTRIBUTE - FRAGMENT
///////////////////////////////////////////////////////////////////////////////////
varying vec3 v_tangent;
vec3 g_tangent = vec3(1.0, 0.0, 0.0);
#define pullTangent() (g_tangent = v_tangent)

////////////////////////////////////////////////////////////////////////////////////
////////// TEXCOORD0 ATTRIBUTE - FRAGMENT
///////////////////////////////////////////////////////////////////////////////////
varying vec2 v_texCoord0;
vec2 g_texCoord0 = vec2(0.0, 0.0);
#define pullTexCoord0() (g_texCoord0 = v_texCoord0)


// Uniforms which are always available
uniform mat4 u_projViewTrans;

uniform mat4 u_worldTrans;

uniform vec4 u_cameraPosition;

uniform mat3 u_normalMatrix;

// Other uniforms
#ifdef blendedFlag
uniform float u_opacity;
#else
const float u_opacity = 1.0;
#endif

#ifdef alphaTestFlag
uniform float u_alphaTest;
#else
const float u_alphaTest = 0.0;
#endif

#ifdef shininessFlag
uniform float u_shininess;
#else
const float u_shininess = 20.0;
#endif


#ifdef diffuseColorFlag
uniform vec4 u_diffuseColor;
#endif

#ifdef diffuseTextureFlag
uniform sampler2D u_diffuseTexture;
#endif

#ifdef specularColorFlag
uniform vec4 u_specularColor;
#endif

#ifdef specularTextureFlag
uniform sampler2D u_specularTexture;
#endif

#ifdef normalTextureFlag
uniform sampler2D u_normalTexture;
#endif

#if defined(diffuseTextureFlag) || defined(specularTextureFlag)
#define textureFlag
#endif

#if defined(specularTextureFlag) || defined(specularColorFlag)
#define specularFlag
#endif

#if defined(specularFlag) || defined(fogFlag)
#define cameraPositionFlag
#endif

#ifdef shadowMapFlag
uniform sampler2D u_shadowTexture;
uniform float u_shadowPCFOffset;
varying vec3 v_shadowMapUv;
#define separateAmbientFlag

float getShadowness(vec2 offset)
{
	const vec4 bitShifts = vec4(1.0, 1.0 / 255.0, 1.0 / 65025.0, 1.0 / 160581375.0);
	return step(v_shadowMapUv.z, dot(texture2D(u_shadowTexture, v_shadowMapUv.xy + offset), bitShifts));//+(1.0/255.0));	
}

float getShadow() 
{
	return (//getShadowness(vec2(0,0)) + 
			getShadowness(vec2(u_shadowPCFOffset, u_shadowPCFOffset)) +
			getShadowness(vec2(-u_shadowPCFOffset, u_shadowPCFOffset)) +
			getShadowness(vec2(u_shadowPCFOffset, -u_shadowPCFOffset)) +
			getShadowness(vec2(-u_shadowPCFOffset, -u_shadowPCFOffset))) * 0.25;
}
#endif //shadowMapFlag

#if defined(diffuseTextureFlag) && defined(diffuseColorFlag)
#define fetchColorDiffuseTD(texCoord, defaultValue) texture2D(u_diffuseTexture, texCoord) * u_diffuseColor
#elif defined(diffuseTextureFlag)
#define fetchColorDiffuseTD(texCoord, defaultValue) texture2D(u_diffuseTexture, texCoord)
#elif defined(diffuseColorFlag)
#define fetchColorDiffuseTD(texCoord, defaultValue) u_diffuseColor
#else
#define fetchColorDiffuseTD(texCoord, defaultValue) (defaultValue)
#endif


#define fetchColorDiffuseD(defaultValue) fetchColorDiffuseTD(g_texCoord0, defaultValue)
#define fetchColorDiffuse() fetchColorDiffuseD(vec4(1.0))

#if defined(diffuseTextureFlag) || defined(diffuseColorFlag)
#define applyColorDiffuse(baseColor) ((baseColor) * fetchColorDiffuse())
#else
#define applyColorDiffuse(baseColor) (baseColor)
#endif


#if defined(specularTextureFlag) && defined(specularColorFlag)
#define fetchColorSpecularTD(texCoord, defaultValue) (texture2D(u_specularTexture, texCoord).rgb * u_specularColor.rgb)
#elif defined(specularTextureFlag)
#define fetchColorSpecularTD(texCoord, defaultValue) texture2D(u_specularTexture, texCoord).rgb
#elif defined(specularColorFlag)
#define fetchColorSpecularTD(texCoord, defaultValue) u_specularColor.rgb
#else
#define fetchColorSpecularTD(texCoord, defaultValue) (defaultValue)
#endif


#define fetchColorSpecularD(defaultValue) fetchColorSpecularTD(g_texCoord0, defaultValue)
#define fetchColorSpecular() fetchColorSpecularD(vec3(0.0))

#if defined(specularTextureFlag) || defined(specularColorFlag)
#define applyColorSpecular(intensity) ((intensity) * fetchColorSpecular())
#define addColorSpecular(baseColor, intensity)	((baseColor) + applyColorSpecular(intensity))
#else
#define applyColorSpecular(intensity) (vec3(0.0))
#define addColorSpecular(baseColor, intensity)	(baseColor)
#endif

varying vec3 v_lightDir;
varying vec3 v_lightCol;
varying vec3 v_viewDir;
#ifdef environmentCubemapFlag
varying vec3 v_reflect;
#endif

#ifdef environmentCubemapFlag
uniform samplerCube u_environmentCubemap;
#endif

#ifdef reflectionColorFlag
uniform vec4 u_reflectionColor;
#endif

varying vec3 v_ambientLight;

#define saturate(x) clamp( x, 0.0, 1.0 )

void main() {
	#if defined(emissiveTextureFlag)
		vec4 emissive = texture2D(u_emissiveTexture, v_emissiveUV);
	#endif

	g_color = v_color ;//pullColor(); does not work on Android
	g_texCoord0 = v_texCoord0 ;//pullTexCoord0(); does not work on Android
	
	#if defined(diffuseTextureFlag) || defined(diffuseColorFlag)
	vec4 diffuse = g_color * fetchColorDiffuseD(vec4(1.0)); //applyColorDiffuse(g_color); does not work on Android
	#else
	vec4 diffuse = g_color;
	#endif
	
	#if defined(specularTextureFlag) || defined(specularColorFlag)
	vec3 specular = fetchColorSpecularD(vec3(0.0)); //fetchColorSpecular();  does not work on Android
	#else
	vec4 specular = vec4(0.0);
	#endif
	
	#ifdef normalTextureFlag
	vec4 N = vec4(normalize(texture2D(u_normalTexture, g_texCoord0).xyz * 2.0 - 1.0), 1.0);

	#ifdef environmentCubemapFlag
	vec3 reflectDir = normalize(v_reflect + (vec3(0.0, 0.0, 1.0) - N.xyz));
	#endif
	#else
	vec4 N = vec4(0.0, 0.0, 1.0, 1.0);
	#ifdef environmentCubemapFlag
	vec3 reflectDir = normalize(v_reflect);
	#endif
	#endif
	
	vec3 L = normalize(v_lightDir);
	vec3 V = normalize(v_viewDir);
	vec3 H = normalize(L + V);
	float NL = dot(N.xyz, L);
	float NH = max(0.0, dot(N.xyz, H));
	
	float specOpacity = 1.0; //(1.0 - diffuse.w);
	float spec = min(1.0, pow(NH, 10.0) * specOpacity);
	float selfShadow = saturate(4.0 * NL);
	//
	#ifdef environmentCubemapFlag
	vec3 environment = textureCube(u_environmentCubemap, reflectDir).rgb;
	specular *= environment;
	#ifdef reflectionColorFlag
	diffuse.rgb = saturate(vec3(1.0) - u_reflectionColor.rgb) * diffuse.rgb + environment * u_reflectionColor.rgb;
	#endif
	#endif
	
	#ifdef shadowMapFlag
	gl_FragColor = vec4(saturate((v_lightCol * diffuse.rgb) * NL * getShadow()), diffuse.w);
	#else
	gl_FragColor = vec4(saturate((v_lightCol * diffuse.rgb) * NL), diffuse.w);
	#endif
	gl_FragColor.rgb += v_ambientLight * diffuse.rgb;
	gl_FragColor.rgb += (selfShadow * spec) * specular.rgb * v_lightCol;
	
	#if defined(emissiveTextureFlag)
		gl_FragColor.r += emissive.r * emissive.a;
		gl_FragColor.g += emissive.g * emissive.a;
		gl_FragColor.b += emissive.b * emissive.a;
	#endif
}