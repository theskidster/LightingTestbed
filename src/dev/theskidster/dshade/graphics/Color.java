package dev.theskidster.dshade.graphics;

import org.joml.Vector3f;

/**
 * Nov 17, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public class Color {

    //XJGE 2 palette
    public static final Color WHITE   = new Color(1);
    public static final Color SILVER  = new Color(0.753f);
    public static final Color GRAY    = new Color(0.38f);
    public static final Color BLACK   = new Color(0);
    public static final Color RED     = new Color(255, 0, 0);
    public static final Color ORANGE  = new Color(255, 153, 0);
    public static final Color YELLOW  = new Color(255, 255, 0);
    public static final Color LIME    = new Color(0, 255, 0);
    public static final Color GREEN   = new Color(0, 153, 0);
    public static final Color TEAL    = new Color(0, 153, 153);
    public static final Color CYAN    = new Color(0, 255, 255);
    public static final Color BLUE    = new Color(0, 0, 255);
    public static final Color PURPLE  = new Color(153, 51, 204);
    public static final Color MAGENTA = new Color(255, 0, 255);
    public static final Color PINK    = new Color(255, 153, 204);
    public static final Color BROWN   = new Color(102, 51, 0);
    
    public final float r;
    public final float g;
    public final float b;
    
    private final Vector3f conversion;
    
    private Color(float scalar) {
        r = g = b = scalar;
        conversion = new Vector3f(scalar);
    }
    
    private Color(int r, int g, int b) {
        this.r = (r / 255f);
        this.g = (g / 255f);
        this.b = (b / 255f);
        
        conversion = new Vector3f(this.r, this.g, this.b);
    }
    
    public static Color create(int r, int g, int b) {
        return new Color(r, g, b);
    }
    
    public Vector3f asVec3() {
        return conversion;
    }
    
}
