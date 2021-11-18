package dev.theskidster.dshade.main;

import dev.theskidster.dshade.graphics.Color;
import dev.theskidster.jlogger.JLogger;
import dev.theskidster.shadercore.GLProgram;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import org.joml.Matrix4f;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import static org.lwjgl.stb.STBTruetype.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

/**
 * Nov 17, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
final class Font {
    
    private final int FLOATS_PER_GLYPH = 24;
    
    final int size;
    private final int texHandle;
    private final int vao = glGenVertexArrays();
    private final int vbo = glGenBuffers();
    
    private final float SCALE = 1.5f;
    
    final String filename;
    final Matrix4f projMatrix = new Matrix4f();
    
    private static final Map<Character, Glyph> glyphs = new HashMap<>();
    
    private final class Glyph {
        int advance;
        int width;
        int height;
        int bearingX;
        int bearingY;
        
        float s0;
        float s1;
        float t0;
        float t1;
    }
    
    Font(String filename, int size) {
        this.filename = filename;
        this.size     = size;
        
        texHandle = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texHandle);
        
        try(InputStream file = Font.class.getResourceAsStream(App.ASSETS_PATH + filename)) {
            try(MemoryStack stack = MemoryStack.stackPush()) {
                byte[] data = file.readAllBytes();
                
                ByteBuffer fontBuf = MemoryUtil.memAlloc(data.length).put(data).flip();
                STBTTFontinfo info = STBTTFontinfo.malloc(stack);
                
                if(!stbtt_InitFont(info, fontBuf)) {
                    throw new IllegalStateException("Failed to parse font information.");
                }
                
                String charset = " !\"#$%&\'()*+,-./" +
                                 "0123456789:;<=>?"   +
                                 "@ABCDEFGHIJKLMNO"   +
                                 "PQRSTUVWXYZ[\\]^_"  + 
                                 "`abcdefghijklmno"   +
                                 "pqrstuvwxyz{|}~";
                
                int bitmapSizeInPixels = 128;
                int exitStatus         = -1;
                int extraCells         = -1;
                int bitmapWidth        = 0;
                int bitmapHeight       = 0;
                
                ByteBuffer imageBuf = MemoryUtil.memAlloc(bitmapWidth * bitmapHeight);
                STBTTBakedChar.Buffer bakedCharBuf = STBTTBakedChar.malloc(charset.length());

                /*
                Continuously generate a bitmap image until its large enough to
                contain every glyph in the font. 
                */
                while(exitStatus <= 0) {
                    bitmapWidth  = Math.round(bitmapSizeInPixels * SCALE);
                    bitmapHeight = Math.round(bitmapSizeInPixels * SCALE);
                    imageBuf     = MemoryUtil.memAlloc(bitmapWidth * bitmapHeight);

                    bakedCharBuf = STBTTBakedChar.malloc(charset.length());

                    extraCells = stbtt_BakeFontBitmap(fontBuf, size * SCALE, imageBuf, bitmapWidth, bitmapHeight, 32, bakedCharBuf);
                    exitStatus = Math.abs(extraCells) - charset.length();

                    if(extraCells > 0) break;

                    MemoryUtil.memFree(bakedCharBuf);
                    MemoryUtil.memFree(imageBuf);

                    bitmapSizeInPixels += 16;
                }
                
                for(int i = 0; i < charset.length(); i++) {
                    STBTTAlignedQuad quad = STBTTAlignedQuad.callocStack(stack);
                    
                    FloatBuffer xPosBuf  = MemoryUtil.memAllocFloat(1);
                    FloatBuffer yPosBuf  = MemoryUtil.memAllocFloat(1);
                    IntBuffer advanceBuf = MemoryUtil.memAllocInt(1);
                    IntBuffer bearingBuf = MemoryUtil.memAllocInt(1);
                    
                    stbtt_GetBakedQuad(bakedCharBuf, bitmapWidth, bitmapHeight, i, xPosBuf, yPosBuf, quad, true);
                    stbtt_GetGlyphHMetrics(info, i, advanceBuf, bearingBuf);
                    
                    Glyph glyph = new Glyph();
                    STBTTBakedChar bakedChar = bakedCharBuf.get(i);
                    
                    glyph.advance  = (int) bakedChar.xadvance();
                    glyph.width    = (bakedChar.x1() - bakedChar.x0());
                    glyph.height   = (bakedChar.y1() - bakedChar.y0());
                    glyph.bearingX = (int) bakedChar.xoff();
                    glyph.bearingY = (int) (-bakedChar.yoff() - glyph.height);
                    glyph.s0       = quad.s0();
                    glyph.s1       = quad.s1();
                    glyph.t0       = quad.t0();
                    glyph.t1       = quad.t1();
                    
                    glyphs.put(charset.charAt(i), glyph);
                    
                    MemoryUtil.memFree(xPosBuf);
                    MemoryUtil.memFree(yPosBuf);
                }
                
                MemoryUtil.memFree(bakedCharBuf);
                
                glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, bitmapWidth, bitmapHeight, 0, GL_ALPHA, GL_UNSIGNED_BYTE, imageBuf);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
                
                MemoryUtil.memFree(imageBuf);
                MemoryUtil.memFree(fontBuf);
            }
            
            glBindVertexArray(vao);
            
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, FLOATS_PER_GLYPH * Float.BYTES, GL_DYNAMIC_DRAW);
            
            glVertexAttribPointer(0, 2, GL_FLOAT, false, (4 * Float.BYTES), 0);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, (4 * Float.BYTES), (2 * Float.BYTES));
            
            glEnableVertexAttribArray(0);
            glEnableVertexAttribArray(1);
            
        } catch(Exception e) {
            JLogger.setModule("core");
            JLogger.logSevere("Failed to load font \"" + filename + "\"", e);
        }
    }
    
    public void drawString(String text, float xPos, float yPos, Color color, GLProgram uiProgram) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        
        glBindVertexArray(vao);
        glBindTexture(GL_TEXTURE_2D, texHandle);
        
        uiProgram.setUniform("uType", 0);
        uiProgram.setUniform("uColor", color.asVec3());
        
        for(char c : text.toCharArray()) {
            Glyph g = glyphs.get(c);
            
            float x  = xPos + g.bearingX;
            float y  = yPos + g.bearingY;
            float w  = glyphs.get(c).width;
            float h  = glyphs.get(c).height;
            float s0 = glyphs.get(c).s0;
            float s1 = glyphs.get(c).s1;
            float t0 = glyphs.get(c).t0;
            float t1 = glyphs.get(c).t1;
            
            try(MemoryStack stack = MemoryStack.stackPush()) {
                FloatBuffer vertexBuf = stack.mallocFloat(FLOATS_PER_GLYPH);
                
                //(vec2 position), (vec2 texCoords)
                vertexBuf.put(x)    .put(y)        .put(s0).put(t1);
                vertexBuf.put(x)    .put(y + h)    .put(s0).put(t0);
                vertexBuf.put(x + w).put(y + h)    .put(s1).put(t0);
                vertexBuf.put(x)    .put(y)        .put(s0).put(t1);
                vertexBuf.put(x + w).put(y + h)    .put(s1).put(t0);
                vertexBuf.put(x + w).put(y)        .put(s1).put(t1);
                
                vertexBuf.flip();
                
                glBindBuffer(GL_ARRAY_BUFFER, vbo);
                glBufferSubData(GL_ARRAY_BUFFER, 0, vertexBuf);
            }
            
            glDrawArrays(GL_TRIANGLES, 0, 6);
            xPos += g.advance;
        }
        
        glDisable(GL_BLEND);
        App.checkGLError();
    }
    
    public static int getLengthInPixels(String text) {
        int length = 0;
        for(char c : text.toCharArray()) length += glyphs.get(c).advance;
        
        return length;
    }

}