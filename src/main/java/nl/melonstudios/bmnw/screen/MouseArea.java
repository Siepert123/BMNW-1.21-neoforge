package nl.melonstudios.bmnw.screen;

public record MouseArea(int x, int y, int w, int h) {
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MouseArea area) {
            return area.x == this.x && area.y == this.y && area.w == this.w && area.h == this.h;
        }
        return false;
    }

    public boolean isInArea(int mouseX, int mouseY) {
        return mouseX >= this.x && mouseY >= this.y && mouseX < this.x+this.w && mouseY < this.y+this.h;
    }
}
