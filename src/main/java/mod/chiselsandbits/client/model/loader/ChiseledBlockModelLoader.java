package mod.chiselsandbits.client.model.loader;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import io.github.fabricators_of_create.porting_lib.models.geometry.IGeometryLoader;
import mod.chiselsandbits.client.model.ChiseledBlockModel;

public final class ChiseledBlockModelLoader implements IGeometryLoader<ChiseledBlockModel> {

    private static final ChiseledBlockModelLoader INSTANCE = new ChiseledBlockModelLoader();

    public static ChiseledBlockModelLoader getInstance() {
        return INSTANCE;
    }

    private ChiseledBlockModelLoader() {}

    @Override
    public ChiseledBlockModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext)
            throws JsonParseException {
        return new ChiseledBlockModel();
    }
}
