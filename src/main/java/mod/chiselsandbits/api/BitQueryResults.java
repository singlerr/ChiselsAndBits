package mod.chiselsandbits.api;

public class BitQueryResults {
    public BitQueryResults(final int air, final int solid, final int fluid) {
        this.empty = air;
        this.solid = solid;
        this.fluid = fluid;
        total = air + solid + fluid;
    }

    public final int total;
    public final int empty;
    public final int solid;
    public final int fluid;
}
