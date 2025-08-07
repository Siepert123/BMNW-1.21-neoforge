package nl.melonstudios.bmnw.weapon.missile.guidance;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import nl.melonstudios.bmnw.weapon.missile.entity.CustomizableMissileEntity;
import nl.melonstudios.bmnw.computer.internals.ByteArray;

import java.io.DataInputStream;
import java.io.IOException;

public class GuidanceComputer {
    private GuidanceComputer(CustomizableMissileEntity missile) {
        this.missile = missile;
    }
    public static GuidanceComputer create(CustomizableMissileEntity missile) {
        return new GuidanceComputer(missile);
    }

    private final CustomizableMissileEntity missile;
    private boolean hasCrashed = false;
    private GuidanceProgram program = GuidanceProgram.PRECOMPILED_GUIDANCE_PROGRAMS[0];

    public void setMemorySize(int bytes) {
        this.memory = new byte[bytes];
    }
    private byte[] memory = new byte[128];

    public void tick() {
        if (this.hasCrashed) return;
        try {
            this.program.doTheGuidanceProgramThings(this.missile, this);
        } catch (Throwable e) {
            this.hasCrashed = true;
        }
    }

    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        if (this.hasCrashed) {
            nbt.putBoolean("hasCrashed", true);
        } else {
            nbt.putInt("programID", this.program.getHardcodedID());
            nbt.putByteArray("memory", this.memory);
            if (this.program.getHardcodedID() < 0) {
                CompiledGuidanceProgram compiled = (CompiledGuidanceProgram) this.program;
                nbt.put("CompiledProgram", compiled.serialize(new CompoundTag()));
            }
        }
        return nbt;
    }
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.getBoolean("hasCrashed")) {
            this.hasCrashed = true;
        } else {
            int programID = nbt.getInt("programID");
            if (programID < 0) {
                this.program = CompiledGuidanceProgram.createFromNBT(nbt.getCompound("CompiledProgram"));
                if (this.program == null) {
                    this.hasCrashed = true;
                    this.program = GuidanceProgram.PRECOMPILED_GUIDANCE_PROGRAMS[0];
                }
            } else {
                if (programID < GuidanceProgram.PRECOMPILED_GUIDANCE_PROGRAMS.length) {
                    this.program = GuidanceProgram.PRECOMPILED_GUIDANCE_PROGRAMS[programID];
                } else this.program = GuidanceProgram.PRECOMPILED_GUIDANCE_PROGRAMS[0];
            }
            this.memory = nbt.getByteArray("memory");
        }
    }

    public void writeBuf(ByteBuf buf) {
        buf.writeBoolean(this.hasCrashed);
        if (this.hasCrashed) return;
        buf.writeInt(this.program.getHardcodedID());
        if (this.program.getHardcodedID() < 0) {
            CompiledGuidanceProgram compiled = (CompiledGuidanceProgram) this.program;
            try {
                NbtIo.writeCompressed(compiled.serialize(new CompoundTag()), new ByteBufOutputStream(buf));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void readBuf(ByteBuf buf) {
        if (buf.readBoolean()) {
            this.hasCrashed = true;
            return;
        }
        int programID = buf.readInt();
        if (programID < 0) {
            try {
                this.program = CompiledGuidanceProgram.createFromNBT(NbtIo.readCompressed(new ByteBufInputStream(buf),
                        NbtAccounter.unlimitedHeap()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (this.program == null) {
                this.hasCrashed = true;
                this.program = GuidanceProgram.PRECOMPILED_GUIDANCE_PROGRAMS[0];
            }
        } else this.program = GuidanceProgram.PRECOMPILED_GUIDANCE_PROGRAMS[programID];
    }

    //data accessors
    public byte getByte(int add) {
        return this.memory[add];
    }
    public boolean getBoolean(int add) {
        return ByteArray.getBoolean(this.memory, add);
    }
    public short getShort(int add) {
        return ByteArray.getShort(this.memory, add);
    }
    public int getInt(int add) {
        return ByteArray.getInt(this.memory, add);
    }
    public long getLong(int add) {
        return ByteArray.getLong(this.memory, add);
    }
    public float getFloat(int add) {
        return ByteArray.getFloatRaw(this.memory, add);
    }
    public double getDouble(int add) {
        return ByteArray.getDoubleRaw(this.memory, add);
    }

    public void setByte(int add, byte value) {
        this.memory[add] = value;
    }
    public void setBoolean(int add, boolean value) {
        ByteArray.setBoolean(this.memory, add, value);
    }
    public void setShort(int add, short value) {
        ByteArray.setShort(this.memory, add, value);
    }
    public void setInt(int add, int value) {
        ByteArray.setInt(this.memory, add, value);
    }
    public void setLong(int add, long value) {
        ByteArray.setLong(this.memory, add, value);
    }
    public void setFloat(int add, float value) {
        ByteArray.setFloatRaw(this.memory, add, value);
    }
    public void setDouble(int add, double value) {
        ByteArray.setDouble(this.memory, add, value);
    }
}
