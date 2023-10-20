package com.RuinedEngine.utils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

public class ImageParser {
    public ByteBuffer get_image() {
        return image;
    }

    public int get_width() {
        return width;
    }

    public int get_heigh() {
        return heigh;
    }

    private ByteBuffer image;
    private int width, heigh;

    ImageParser(int width, int heigh, ByteBuffer image) {
        this.image = image;
        this.heigh = heigh;
        this.width = width;
    }
    public static ImageParser load_image(String path) {
        ByteBuffer image;
        int width, heigh;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer comp = stack.mallocInt(1);
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);

            image = STBImage.stbi_load(path, w, h, comp, 4);
            if (image == null) {
                throw new RuntimeException("Could not load icon resources. From path: " + path);
            }
            width = w.get();
            heigh = h.get();
        }
        return new ImageParser(width, heigh, image);
    }
}
