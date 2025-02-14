package nl.melonstudios.bmnw.misc;

public class DoubleHolderThing {
    public double value;

    public DoubleHolderThing(double v) {
        this.value = v;
    }

    public void multiply(double v) {
        value *= v;
    }
}
