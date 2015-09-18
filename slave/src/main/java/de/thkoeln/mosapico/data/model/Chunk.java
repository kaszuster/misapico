package de.thkoeln.mosapico.data.model;

/**
 * Created by szuster on 14.09.2015.
 */
public class Chunk {

    private int x;
    private int y;
    private byte[] bytes;

    public Chunk() {
    }

    public Chunk(int x, int y, byte[] bytes) {
        this.x = x;
        this.y = y;
        this.bytes = bytes;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
