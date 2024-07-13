package mod.chiselsandbits.core.api;

import java.util.HashMap;
import java.util.Map;
import mod.chiselsandbits.client.ModConflictContext;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.core.Log;
import net.minecraftforge.fml.InterModComms;

public class IMCHandler {

    private final Map<String, IMCMessageHandler> processors = new HashMap<String, IMCMessageHandler>();

    public IMCHandler() {
        processors.put("forcestatecompatibility", new IMCHandlerForceState());
        processors.put("ignoreblocklogic", new IMCHandlerIgnoreLogic());
        processors.put("materialequivilancy", new IMCHandlerMaterialEquivilancy());

        for (ModConflictContext conflictContext : ModConflictContext.values()) {
            processors.put(conflictContext.getName(), new IMCHandlerKeyBinding());
        }

        processors.put("initkeybindingannotations", new IMCHandlerKeyBindingAnnotations());
    }

    public void handleIMCEvent() {
        InterModComms.getMessages(ChiselsAndBits.MODID).forEach(this::executeIMC);
    }

    private void executeIMC(final InterModComms.IMCMessage message) {
        final IMCMessageHandler handler = processors.get(message.getMethod());

        if (handler != null) {
            handler.excuteIMC(message);
        } else {
            Log.logError(
                    "Invalid IMC: " + message.getMethod() + " from " + message.getSenderModId(),
                    new RuntimeException("Invalid IMC Type."));
        }
    }
}
