package sample;

public class Pixel {
    private int height, width;

    public Pixel(int width, int height){
        this.height = height;
        this.width = width;
    }

    @Override
    public boolean equals(Object obj) {
        return this.width == ((Pixel)obj).getWidth() && this.height == ((Pixel)obj).getHeight();
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
