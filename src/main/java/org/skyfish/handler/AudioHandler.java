package org.skyfish.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.skyfish.util.*;

import javax.sound.sampled.*;
import java.io.File;
import java.util.concurrent.TimeUnit;

public class AudioHandler {

    private final Minecraft mc = Minecraft.getMinecraft();
    private boolean minecraftSoundEnabled = false;
    private final Clock delayBetweenPings = new Clock();
    private int numSounds = 15;
    private float soundBeforeChange = 0;

    public void setSoundBeforeChange(float soundBeforeChange) {
        this.soundBeforeChange = soundBeforeChange;
    }

    public void resetSound() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
            return;
        }

        minecraftSoundEnabled = false;
        mc.gameSettings.setSoundLevel(SoundCategory.MASTER, soundBeforeChange);
    }

    private static Clip clip;

    public void playSound() {
        Multithreading.runAsync(() -> {
            try {
                AudioInputStream inputStream = AudioSystem.getAudioInputStream(getClass().getResource("/skyfish/failsafe_notif.wav"));
                if (inputStream == null) {
                    LogUtils.sendError("Failed to load sound file!");
                    return;
                }
                clip = AudioSystem.getClip();
                clip.open(inputStream);
                FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float volumePercentage = 100f;
                float dB = (float) (Math.log(volumePercentage) / Math.log(10.0) * 20.0);
                volume.setValue(dB);
                clip.start();
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                });
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        });
    }

    public boolean isSoundPlaying() {
        return (clip != null && clip.isRunning()) || minecraftSoundEnabled;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null || !Config.getInstance().FAILSAFE_PLAY_SOUND || !minecraftSoundEnabled) return;
        if (delayBetweenPings.isScheduled() && !delayBetweenPings.passed()) return;
        if (numSounds <= 0) {
            minecraftSoundEnabled = false;
            mc.gameSettings.setSoundLevel(SoundCategory.MASTER, soundBeforeChange);
            return;
        }

        delayBetweenPings.schedule(100);
        numSounds--;
    }

    private static AudioHandler instance;
    public static AudioHandler getInstance() {
        if (instance == null) {
            instance = new AudioHandler();
        }
        return instance;
    }
    
}