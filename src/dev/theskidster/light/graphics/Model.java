package dev.theskidster.light.graphics;

import dev.theskidster.jlogger.JLogger;
import dev.theskidster.light.main.App;
import dev.theskidster.shadercore.GLProgram;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.joml.Matrix3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIFile;
import org.lwjgl.assimp.AIFileCloseProc;
import org.lwjgl.assimp.AIFileCloseProcI;
import org.lwjgl.assimp.AIFileIO;
import org.lwjgl.assimp.AIFileOpenProc;
import org.lwjgl.assimp.AIFileOpenProcI;
import org.lwjgl.assimp.AIFileReadProc;
import org.lwjgl.assimp.AIFileReadProcI;
import org.lwjgl.assimp.AIFileSeek;
import org.lwjgl.assimp.AIFileSeekI;
import org.lwjgl.assimp.AIFileTellProc;
import org.lwjgl.assimp.AIFileTellProcI;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.Assimp;
import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL14.GL_MIRRORED_REPEAT;
import org.lwjgl.system.MemoryUtil;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Nov 19, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public class Model {
    
    public static final int MAX_TEXTURES = 8;
    
    public Color color = Color.WHITE;
    public final String filename;
    private AIScene aiScene;    
    private final Matrix3f normal = new Matrix3f();

    public List<Mesh> meshes = new ArrayList<>();
    private Texture[] textures;
    
    public Model(String filename, int aiArgs) {
        this.filename = filename;
        
        try(InputStream file = Model.class.getResourceAsStream(App.ASSETS_PATH + filename)) {
            loadModel(file, aiArgs);
        } catch(Exception e) {
            JLogger.setModule("graphics");
            JLogger.logWarning("Failed to load model \"" + filename + "\"", e);
            JLogger.setModule(null);
        }
    }
    
    public Model(String filename) {
        this(filename, 
             aiProcess_JoinIdenticalVertices | 
             aiProcess_Triangulate | 
             aiProcess_LimitBoneWeights | 
             aiProcess_FixInfacingNormals);
    }
    
    private void loadModel(InputStream file, int aiArgs) throws Exception {
        byte[] data = file.readAllBytes();
        
        ByteBuffer modelBuf = MemoryUtil.memAlloc(data.length).put(data).flip();
        AIFileIO aiFileIO   = AIFileIO.create();
        AIFile aiFile       = AIFile.create();
        
        AIFileOpenProcI openProcedure = new AIFileOpenProc() {
            @Override
            public long invoke(long pFileIO, long fileName, long openMode) {
                AIFileReadProcI readProcedure = new AIFileReadProc() {
                    @Override
                    public long invoke(long pFile, long pBuffer, long size, long count) {
                        long numBytes = Math.min(modelBuf.remaining(), size * count);
                        MemoryUtil.memCopy(MemoryUtil.memAddress(modelBuf) + modelBuf.position(), pBuffer, numBytes);

                        return numBytes;
                    }
                };

                AIFileSeekI seekProcedure = new AIFileSeek() {
                    @Override
                    public int invoke(long pFile, long offset, int origin) {
                        switch(origin) {
                            case Assimp.aiOrigin_CUR -> modelBuf.position(modelBuf.position() + (int) offset);
                            case Assimp.aiOrigin_SET -> modelBuf.position((int) offset);
                            case Assimp.aiOrigin_END -> modelBuf.position(modelBuf.limit() + (int) offset);
                        }

                        return 0;
                    }
                };

                AIFileTellProcI tellProcedure = new AIFileTellProc() {
                    @Override
                    public long invoke(long pFile) { return modelBuf.limit(); }
                };

                aiFile.ReadProc(readProcedure);
                aiFile.SeekProc(seekProcedure);
                aiFile.FileSizeProc(tellProcedure);

                return aiFile.address();
            }
        };
        
        AIFileCloseProcI closeProcedure = new AIFileCloseProc() {
            @Override
            public void invoke(long pFileIO, long pFile) {}
        };
        
        aiFileIO.set(openProcedure, closeProcedure, NULL);
        
        aiScene = aiImportFileEx((App.ASSETS_PATH + filename), aiArgs, aiFileIO);
        
        if(aiScene == null) throw new IllegalStateException(aiGetErrorString());
        
        MemoryUtil.memFree(modelBuf);
        
        parseMeshData(aiScene.mMeshes());
        parseTextureData(aiScene.mMaterials());
    }
    
    private void parseMeshData(PointerBuffer meshBuf) throws Exception {
        for(int i = 0; i < aiScene.mNumMeshes(); i++) {
            AIMesh aiMesh = AIMesh.create(meshBuf.get(i));
            meshes.add(new Mesh(aiMesh));
        }
        
        meshes = Collections.unmodifiableList(meshes);
    }
    
    private void parseTextureData(PointerBuffer materialBuf) throws Exception {
        if(aiScene.mNumMaterials() > MAX_TEXTURES) {
            textures = new Texture[MAX_TEXTURES];
            JLogger.setModule("graphics");
            JLogger.logWarning(
                    "Invalid number of textures. Limit of " + MAX_TEXTURES + 
                    " permitted, found " + aiScene.mNumMaterials(), 
                    null);
            JLogger.setModule(null);
        } else {
            textures = new Texture[aiScene.mNumMaterials()];
        }
        
        for(int i = 0; i < textures.length; i++) {
            AIMaterial aiMaterial = AIMaterial.create(materialBuf.get(i));
            
            AIString aiFilename = AIString.calloc();
            Assimp.aiGetMaterialTexture(aiMaterial, aiTextureType_DIFFUSE, 0, aiFilename, (IntBuffer) null, null, null, null, null, null);
            
            textures[i] = new Texture(aiFilename.dataString());
            
            aiFilename.free();
            
            glBindTexture(GL_TEXTURE_2D, textures[i].handle);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_MIRRORED_REPEAT);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_MIRRORED_REPEAT);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glBindTexture(GL_TEXTURE_2D, 0);
        }
    }
    
    public void delocalizeNormal() {
        meshes.forEach(mesh -> normal.set(mesh.modelMatrix.invert()));
    }
    
    public void translation(float x, float y, float z) {
        meshes.forEach(mesh -> mesh.modelMatrix.translation(x, y, z));
    }
    
    public void rotateX(float angle) {
        meshes.forEach(mesh -> mesh.modelMatrix.rotateX((float) Math.toRadians(angle)));
    }
    
    public void rotateY(float angle) {
        meshes.forEach(mesh -> mesh.modelMatrix.rotateY((float) Math.toRadians(angle)));
    }
    
    public void rotateZ(float angle) {
        meshes.forEach(mesh -> mesh.modelMatrix.rotateZ((float) Math.toRadians(angle)));
    }
    
    public void scale(float factor) {
        meshes.forEach(mesh -> mesh.modelMatrix.scale(factor));
    }
    
    public void render(GLProgram sceneProgram) {
        meshes.forEach(mesh -> {
            
        });
        
        App.checkGLError();
    }
    
}