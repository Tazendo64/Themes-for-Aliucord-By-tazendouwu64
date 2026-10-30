app/src/main/java/com/miplugin/waddledeetheme/

import android.content.Context;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.transition.Fade;
import android.view.View;
import android.widget.Toast;
import androidx.fragment.app.FragmentManager;

import com.aliucord.Logger;
import com.aliucord.Utils;
import com.aliucord.api.CommandsAPI;
import com.aliucord.entities.CommandOption;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.PrePatch;
import com.discord.models.commands.ApplicationCommandOptionType;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WaddleDeeTheme extends Plugin {
    private static final Logger logger = new Logger("WaddleDeeTheme");

    @Override
    public void start(Context context) {
        commands.registerCommand(
            "waddledee",
            "Configura el tema avanzado inspirado en Waddle Dee",
            Collections.emptyList(),
            ctx -> {
                applyAdvancedTheme(context);
                return new CommandsAPI.CommandResult("Refrescando la configuración estática de Waddle Dee... ☂️", null, false);
            }
        );

        commands.registerCommand(
            "waddledebug",
            "Prueba los efectos de sonido del tema de Waddle Dee individualmente",
            Arrays.asList(
                new CommandOption(
                    ApplicationCommandOptionType.STRING, 
                    "sonido", 
                    "Selecciona: enviar, recibir, conectar, timbre", 
                    true, 
                    Collections.emptyList()
                )
            ),
            ctx -> {
                String seleccion = ctx.getRequiredString("sonido").toLowerCase().trim();
                String archivoSound = "";

                switch (seleccion) {
                    case "enviar": archivoSound = "waddle_send.mp3"; break;
                    case "recibir": archivoSound = "waddle_receive.mp3"; break;
                    case "conectar": archivoSound = "waddle_call_start.mp3"; break;
                    case "timbre": archivoSound = "waddle_ringtone.mp3"; break;
                    default:
                        return new CommandsAPI.CommandResult("❌ Opción inválida. Usa: enviar, recibir, conectar o timbre.", null, false);
                }

                File sFile = new File(context.getExternalFilesDir(null), "Aliucord/sounds/" + archivoSound);
                if (!sFile.exists()) {
                    return new CommandsAPI.CommandResult("⚠️ El archivo '" + archivoSound + "' no se encuentra en Aliucord/sounds/.", null, false);
                }

                playThemeSound(context, archivoSound);
                return new CommandsAPI.CommandResult("🎵 Reproduciendo efecto de prueba: " + seleccion, null, false);
            }
        );

        applyAdvancedTheme(context);
    }

    private void applyAdvancedTheme(Context context) {
        setupTransitions();

        boolean recursosListos = validateAndPrepareResources(context);

        if (recursosListos) {
            setupFont(context);
            setupAllSounds(context);
        } else {
            logger.warn("Ejecutando en modo seguro de respaldo. Interfaz activa sin multimedia local.");
        }
    }

    private boolean validateAndPrepareResources(Context context) {
        File aliucordDir = new File(context.getExternalFilesDir(null), "Aliucord");
        List<String> missingFiles = new ArrayList<>();

        File fontsDir = new File(aliucordDir, "fonts");
        File soundsDir = new File(aliucordDir, "sounds");
        
        if (!fontsDir.exists()) fontsDir.mkdirs();
        if (!soundsDir.exists()) soundsDir.mkdirs();

        if (!new File(fontsDir, "WaddleFont.ttf").exists()) {
            missingFiles.add("Fuentes/WaddleFont.ttf");
        }

        String[] requiredSounds = {"waddle_send.mp3", "waddle_receive.mp3", "waddle_call_start.mp3", "waddle_ringtone.mp3"};
        for (String sound : requiredSounds) {
            if (!new File(soundsDir, sound).exists()) {
                missingFiles.add("Sounds/" + sound);
            }
        }

        if (!missingFiles.isEmpty()) {
            Utils.mainThread.post(() -> {
                StringBuilder sb = new StringBuilder("⚠️ Recursos de Waddle Dee incompletos:\n");
                for (int i = 0; i < Math.min(missingFiles.size(), 2); i++) {
                    sb.append("- ").append(missingFiles.get(i)).append("\n");
                }
                if (missingFiles.size() > 2) sb.append("... y ").append(missingFiles.size() - 2).append(" más.");
                Toast.makeText(context, sb.toString(), Toast.LENGTH_LONG).show();
            });
            return false;
        }
        return true;
    }

    private void setupFont(Context context) {
        try {
            File fontFile = new File(context.getExternalFilesDir(null), "Aliucord/fonts/WaddleFont.ttf");
            Typeface customTypeface = Typeface.createFromFile(fontFile);
            patcher.patch(View.class.getDeclaredMethod("setTypeface", Typeface.class), 
                new PrePatch(callFrame -> callFrame.args = new Object[]{customTypeface})
            );
        } catch (Exception e) { logger.error("Error al inyectar tipografía", e); }
    }

    private void setupTransitions() {
        try {
            patcher.patch(FragmentManager.class.getDeclaredMethod("beginTransaction"),
                new PrePatch(callFrame -> {
                    Fade fade = new Fade();
                    fade.setDuration(250);
                })
            );

            patcher.patch(com.google.android.material.bottomsheet.BottomSheetDialogFragment.class.getDeclaredMethod("onViewCreated", View.class, android.os.Bundle.class),
                new PrePatch(callFrame -> {
                    View view = (View) callFrame.args;
                    if (view != null) {
                        view.setAlpha(0f);
                        view.setTranslationY(100f);
                        view.animate()
                            .alpha(1f)
                            .translationY(0f)
                            .setDuration(350)
                            .setInterpolator(new android.view.animation.OvershootInterpolator(1.2f))
                            .start();
                    }
                })
            );
        } catch (Exception e) { logger.error("Error en transiciones", e); }
    }

    private void playThemeSound(Context context, String fileName) {
        try {
            File soundFile = new File(context.getExternalFilesDir(null), "Aliucord/sounds/" + fileName);
            MediaPlayer mp = new MediaPlayer();
            mp.setDataSource(soundFile.getPath());
            mp.setOnCompletionListener(MediaPlayer::release);
            mp.prepare();
            mp.start();
        } catch (Exception e) { logger.error("Fallo al reproducir audio: " + fileName, e); }
    }

    private void setupAllSounds(Context context) {
        try {
            patcher.patch(com.discord.stores.StoreMessages.class.getDeclaredMethod("sendMessage", long.class, com.discord.models.message.Message.class),
                new PrePatch(callFrame -> playThemeSound(context, "waddle_send.mp3"))
            );
        } catch (Exception e) { logger.error("Error patch: enviar", e); }

        try {
            patcher.patch(com.discord.stores.StoreMessages.class.getDeclaredMethod("handleMessageCreate", com.discord.models.message.Message.class),
                new PrePatch(callFrame -> {
                    com.discord.models.message.Message msg = (com.discord.models.message.Message) callFrame.args;
                    if (msg != null && msg.getAuthor().getId() != com.discord.stores.StoreUser.getSelf().getId()) {
                        playThemeSound(context, "waddle_receive.mp3");
                    }
                })
            );
        } catch (Exception e) { logger.error("Error patch: recibir", e); }

        try {
            patcher.patch(com.discord.stores.StoreVoiceChannels.class.getDeclaredMethod("connectToVoiceChannel", long.class, boolean.class, boolean.class),
                new PrePatch(callFrame -> playThemeSound(context, "waddle_call_start.mp3"))
            );
        } catch (Exception e) { logger.error("Error patch: llamar", e); }

        try {
            patcher.patch(com.discord.utilities.voip.VoipNotificationManager.class.getDeclaredMethod("showIncomingCallNotification", Context.class, com.discord.models.user.User.class, long.class),
                new PrePatch(callFrame -> playThemeSound(context, "waddle_ringtone.mp3"))
            );
        } catch (Exception e) { logger.error("Error patch: ringtone", e); }
    }

    @Override
    public void stop(Context context) {
        commands.unregisterCommand("waddledee");
        commands.unregisterCommand("waddledebug");
        patcher.unpatchAll();
    }
}
