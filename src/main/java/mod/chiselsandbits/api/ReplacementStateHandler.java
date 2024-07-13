package mod.chiselsandbits.api;

public final class ReplacementStateHandler {

    private static final ReplacementStateHandler INSTANCE = new ReplacementStateHandler();

    public static ReplacementStateHandler getInstance() {
        return INSTANCE;
    }

    private boolean isReplacing;

    private ReplacementStateHandler() {}

    public boolean isReplacing() {
        return isReplacing;
    }

    public void setReplacing(final boolean replacing) {
        isReplacing = replacing;
    }
}
