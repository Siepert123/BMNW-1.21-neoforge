package nl.melonstudios.bmnw.item.subtype;

import java.util.Objects;

public class FireMarbleSubtype {
    private final Integer type;
    private final boolean charged;
    public FireMarbleSubtype(Integer type, boolean charged) {
        this.type = type;
        this.charged = charged;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FireMarbleSubtype subtype) {
            return Objects.equals(subtype.type, this.type) && subtype.charged == this.charged;
        }
        return false;
    }

    @Override
    public int hashCode() {
        final boolean nullType = this.type == null;
        int hash = 0;
        if (nullType) hash |= 0b10000;
        else hash |= this.type & 0b111;
        if (charged) hash |= 0b1000;
        return hash;
    }

    @Override
    public String toString() {
        return String.format("FireMarble[type=%s,charged=%s]", this.type == null ? "null" : this.type, this.charged);
    }
}
