package com.RuinedEngine.entity;

import java.io.File;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.Assimp;
import org.lwjgl.system.MemoryStack;

import com.RuinedEngine.utils.Consts;
import com.RuinedEngine.utils.TextureCache;

public class ModelLoader {
	private ModelLoader() {
		//Utility class
	}
	public static Model loadModel(String modelId, String modelPath, TextureCache textureCache) {
		return loadModel(modelId, modelPath, textureCache, Assimp.aiProcess_GenSmoothNormals | Assimp.aiProcess_JoinIdenticalVertices |
				Assimp.aiProcess_Triangulate | Assimp.aiProcess_FixInfacingNormals | Assimp.aiProcess_CalcTangentSpace | Assimp.aiProcess_LimitBoneWeights |
				Assimp.aiProcess_PreTransformVertices);
	}
	public static Model loadModel(String modelId, String modelPath, TextureCache textureCache, int flags) {
		File file = new File(modelPath);
		if(!file.exists()) {
			throw new RuntimeException("Model file [" + modelPath + "] not found");
		}
		String modelDir = file.getParent();
		AIScene aiScene = Assimp.aiImportFile(modelPath, flags);
		if(aiScene == null) {
			throw new RuntimeException("Error loading model [modelPath: " + modelPath + "]");
		}
		int numMaterials = aiScene.mNumMaterials();
		List<Material> materialList = new ArrayList<>();
		for(int i = 0; i < numMaterials; i++) {
			AIMaterial aiMaterial = AIMaterial.create(aiScene.mMaterials().get(i));
			materialList.add(processMaterial(aiMaterial, modelDir, textureCache));
		}
		int numMeshes = aiScene.mNumMeshes();
		PointerBuffer aiMeshes = aiScene.mMeshes();
		Material defaultMaterial = new Material();
		for(int i = 0; i < numMeshes; i++) {
			AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
			Mesh mesh = processMesh(aiMesh);
			int materialIdx = aiMesh.mMaterialIndex();
			Material material;
			if(materialIdx >= 0 && materialIdx < materialList.size()) {
				material = materialList.get(materialIdx);
			}else {
				material = defaultMaterial;
			}
			material.getMeshList().add(mesh);
		}
		if(!defaultMaterial.getMeshList().isEmpty()) {
			materialList.add(defaultMaterial);
		}
		return new Model(modelId, materialList);
	}
	private static Material processMaterial(AIMaterial aiMaterial, String modelDir, TextureCache textureCache) {
		Material material = new Material();
		try(MemoryStack stack = MemoryStack.stackPush()){
			AIColor4D color = AIColor4D.create();
			int result = Assimp.aiGetMaterialColor(aiMaterial, Assimp.AI_MATKEY_COLOR_AMBIENT, Assimp.aiTextureType_NONE, 0, color);
			if(result == Assimp.aiReturn_SUCCESS) {
				material.setAmbientColor(new Vector4f(color.r(), color.g(), color.b(), color.a()));
			}
			result = Assimp.aiGetMaterialColor(aiMaterial, Assimp.AI_MATKEY_COLOR_DIFFUSE, Assimp.aiTextureType_NONE, 0, color);
			if(result == Assimp.aiReturn_SUCCESS) {
				material.setDiffuseColor(new Vector4f(color.r(), color.g(), color.b(), color.a()));
			}
			result = Assimp.aiGetMaterialColor(aiMaterial, Assimp.AI_MATKEY_COLOR_SPECULAR, Assimp.aiTextureType_NONE, 0, color);
			if(result == Assimp.aiReturn_SUCCESS) {
				material.setSpecularColor(new Vector4f(color.r(), color.g(), color.b(), color.a()));
			}
			
			float reflectance = 0.0f;
			float[] shininessFactor = new float[] {0.0f};
			int[] pMax = new int[] {1};
			result = Assimp.aiGetMaterialFloatArray(aiMaterial, Assimp.AI_MATKEY_SHININESS_STRENGTH, Assimp.aiTextureType_NONE, 0, shininessFactor, pMax);
			if(result != Assimp.aiReturn_SUCCESS) {
				reflectance = shininessFactor[0];
			}
			material.setReflectance(reflectance);
			
			AIString aiTexturePath = AIString.calloc(stack);
			Assimp.aiGetMaterialTexture(aiMaterial, Assimp.aiTextureType_DIFFUSE, 0, aiTexturePath, (IntBuffer) null,null,null,null,null,null);
			String texturePath = aiTexturePath.dataString();
			if(texturePath != null && texturePath.length() > 0) {
				material.setTexturePath(modelDir + File.separator + new File(texturePath).getName());
				textureCache.createTexture(material.getTexturePath());
				material.setDiffuseColor(Consts.DEFAULT_COLOR);
			}
			return material;
		}
	}
	private static Mesh processMesh(AIMesh aiMesh) {
		float[] vertices = processVertices(aiMesh);
		float[] normals = processNormals(aiMesh);
		float[] textCoords = processTextCoords(aiMesh);
		int[] indices = processIndices(aiMesh);
		if(textCoords.length == 0) {
			int numElements = (vertices.length / 3) * 2;
			textCoords = new float[numElements];
		}
		return new Mesh(vertices, normals, textCoords, indices);
	}
	private static float[] processNormals(AIMesh aiMesh) {
		AIVector3D.Buffer buffer = aiMesh.mNormals();
		float[] data = new float[buffer.remaining() * 3];
		int pos = 0;
		while(buffer.remaining() > 0) {
			AIVector3D normal = buffer.get();
			data[pos++] = normal.x();
			data[pos++] = normal.y();
			data[pos++] = normal.z();
		}
		return data;
	}
	private static int[] processIndices(AIMesh aiMesh) {
		List<Integer> indices = new ArrayList<>();
		int numFaces = aiMesh.mNumFaces();
		AIFace.Buffer aiFaces = aiMesh.mFaces();
		for(int i = 0; i < numFaces; i++) {
			AIFace aiFace = aiFaces.get(i);
			IntBuffer buffer = aiFace.mIndices();
			while(buffer.remaining() > 0) {
				indices.add(buffer.get());
			}
		}
		return indices.stream().mapToInt(Integer::intValue).toArray();
	}
	private static float[] processTextCoords(AIMesh aiMesh) {
		AIVector3D.Buffer buffer = aiMesh.mTextureCoords(0);
		if(buffer == null) {
			return new float[]{};
		}
		float[] data = new float[buffer.remaining() * 2];
		int pos = 0;
		while(buffer.remaining() > 0) {
			AIVector3D textCoord = buffer.get();
			data[pos++] = textCoord.x();
			data[pos++] = 1 - textCoord.y();
		}
		return data;
	}
	private static float[] processVertices(AIMesh aiMesh) {
		AIVector3D.Buffer buffer = aiMesh.mVertices();
		float[] data = new float[buffer.remaining() * 3];
		int pos = 0;
		while(buffer.remaining() > 0) {
			AIVector3D textCoord = buffer.get();
			data[pos++] = textCoord.x();
			data[pos++] = textCoord.y();
			data[pos++] = textCoord.z();
		}
		return data;
	}
}
