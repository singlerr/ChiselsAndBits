package mod.chiselsandbits.chiseledblock;

public class MaterialType {

    private final String name;
    private final String type;

    public MaterialType(final String n, final String t) {
        name = n;
        type = t;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
