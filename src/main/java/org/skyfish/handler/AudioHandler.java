package org.skyfish.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.skyfish.util.*;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
                InputStream audioSrc = getClass().getResourceAsStream("/skyfish/metal_pipe.wav");

                if (audioSrc == null) {
                    LogUtils.sendError("[Audio Manager] Failed to load sound file!");
                    return;
                }

                InputStream bufferedIn = new BufferedInputStream(audioSrc);
                AudioInputStream inputStream = AudioSystem.getAudioInputStream(bufferedIn);

                clip = AudioSystem.getClip();
                clip.open(inputStream);

                FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float volumePercentage = 100 / 100f;
                float dB = (float) (Math.log(volumePercentage) / Math.log(10.0) * 20.0);
                volume.setValue(dB);

                clip.start();
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                        try {
                            inputStream.close();
                            bufferedIn.close();
                            audioSrc.close();
                        } catch (IOException e) {
                            System.err.println("Error closing streams: " + e.getMessage());
                        }
                    }
                });
            } catch (Exception e) {
                System.err.println("Audio playback error: " + e.getMessage());
                e.printStackTrace();
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

    public void initialize() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    private static AudioHandler instance;
    public static AudioHandler getInstance() {
        if (instance == null) {
            instance = new AudioHandler();
        }
        return instance;
    }
    
}
