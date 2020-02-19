package fr.theorozier.procgen.common.world.position;

public abstract class AbsSectionPosition implements SectionPositioned {

    @Override
    public AbsSectionPosition asSectionPos() {
        return this;
    }

    @Override
    public int hashCode() {
        int result = 31 + this.getX();
        result = 31 * result + this.getZ();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof SectionPositioned)) return false;
        SectionPositioned that = (SectionPositioned) obj;
        return this.getX() == that.getX() && this.getZ() == that.getZ();
    }

    @Override
    public String toString() {
        return "<" + this.getX() + "/" + this.getZ() + ">";
    }

}
