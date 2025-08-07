package nl.melonstudios.bmnw.computer.melonscript;

public abstract class MelonScriptVM {
    public abstract byte getByte(int address);
    public abstract boolean getBoolean(int address);
    public abstract short getShort(int address);
    public abstract int getInt(int address);
    public abstract long getLong(int address);
    public abstract float getFloat(int address);
    public abstract double getDouble(int address);

    public abstract void setByte(int address, byte value);
    public abstract void setBoolean(int address, boolean value);
    public abstract void setShort(int address, short value);
    public abstract void setInt(int address, int value);
    public abstract void setLong(int address, long value);
    public abstract float setFloat(int address, float value);
    public abstract double setDouble(int address, double value);

    private int pointer = 0;
    public void jmp(int location) {
        this.pointer = location;
    }
}
