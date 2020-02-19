package fr.theorozier.procgen.common.world.position;

public abstract class AbsBlockPosition implements BlockPositioned {

    @Override
    public AbsBlockPosition asBlockPos() {
        return this;
    }

    @Override
    public AbsSectionPosition asSectionPos() {
        return new ImmutableSectionPosition(this);
    }

    @Override
    public int hashCode() {
        int result = 31 + this.getX();
        result = 31 * result + this.getY();
        result = 31 * result + this.getZ();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BlockPositioned)) return false;
        BlockPositioned that = (BlockPositioned) obj;
        return this.getX() == that.getX() && this.getY() == that.getY() && this.getZ() == that.getZ();
    }

    @Override
    public String toString() {
        return "<" + this.getX() + "/" + this.getY() + "/" + this.getZ() + ">";
    }

}
