package Renderer;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.stb.STBImage.*;

import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

public class Texture {
	public int id, width, height;
	public String filename;
	
	public Texture() {}
	
	public Texture(String filename) {
		this.filename = filename;
		try {
			this.load_texture();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void bind() {
		GL30.glActiveTexture(GL30.GL_TEXTURE0);
		GL30.glBindTexture(GL30.GL_TEXTURE_2D, this.id);
	}
	
	private void load_texture() throws Exception {
		ByteBuffer buf;
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			IntBuffer channels = stack.mallocInt(1);
			
			buf = stbi_load(this.filename, w, h, channels, 4);
			if (buf == null) {
				throw new Exception("[texture] cannot load " + this.filename + ", " + stbi_failure_reason());
			}
			
			this.width = w.get();
			this.height = h.get();
		}
		
		this.id = GL30.glGenTextures();
		
		GL30.glBindTexture(GL30.GL_TEXTURE_2D, this.id);
		GL30.glPixelStorei(GL30.GL_UNPACK_ALIGNMENT, 1);
		GL30.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_RGBA, this.width, this.height, 0, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, buf);
		GL30.glGenerateMipmap(GL30.GL_TEXTURE_2D);
		
		//GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_REPEAT);
		//GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_REPEAT);
		GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_REPEAT);
		GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_REPEAT);
		GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_NEAREST);
		GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_NEAREST);

		
		
		System.out.println("[textures] texture " + this.filename + " loaded : w" + this.width + ", " + this.height);
		
		stbi_image_free(buf);
	}
	
	protected void finalize() {
		GL30.glDeleteTextures(this.id);
	}
}
