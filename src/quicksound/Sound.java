package quicksound;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;

/**
 * <p>This is a simple sound class that can play sounds in some formats (like wav).
 * It cannot play files in mp3 format.
 * It will play sounds immediately and can play the same sound multiple
 * times in a row without interruption. Most sound classes in comparison
 * lag the first time you play a sound and can't handle playing the same
 * sound many times in a row quickly, so this is a great solution for playing
 * short sounds many times in a row. For looping a long music track, you should
 * use another option such as MediaPlayer.</p>
 * 
 * <p>Note that merely loading a single sound using this class will wake up
 * the sound system and remove the lag from the first sound played even if it
 * is played with another class such as AudioClip. If you want to compare the
 * performance of another sound class to this one, you should make sure
 * this class is not used anywhere in your code while testing the other class.</p>
 * 
 * <p>If you want to initialize the sound with multiple channels, you can do so
 * in the second constructor. This could make sense if you already know
 * you will be playing this sound many times in extremely quick succession, but
 * most of the time it is not needed because whenever a sound is told to play
 * and there isn't a channel available, a new channel will be created automatically.
 * 
 * Example Usage:
<pre>
    // initialize the sound named <b>soundFile.wav</b> in the package named <b>resources</b>
    Sound mySound = new Sound("resources/soundFile.wav");
    // to play the sound later in the code...
    mySound.play();

  
    // Initialize a sound with 10 channels ready to play quickly in a row
    Sound multiSound = new Sound("resources/laserSound.wav", 10);
    // to play the sound later in the code...
    multiSound.play();
</pre>
 */
public class Sound {
    private Clip[] clips;
    private int clipIndex;
    private String fileName;
    
    static {
        init();
    }

    /**
     * <p>Initialize a Sound from a file at the given path</p>
     * 
	 * Example path: "resources/laserSound.wav"
	 * 
     * @param resourcePath the path to the sound file
     */
    public Sound(String resourcePath) {
        this(resourcePath, 1);
    }

    /**
     * <p>Initialize a Sound from a file at the given path
     * with the given number of channels preloaded.</p>
     * 
	 * Example path: "resources/laserSound.wav"
	 * 
     * @param resourcePath the path to the sound file
     * @param numChannels the number of channels
     */
    public Sound(String resourcePath, int numChannels) {
        this.fileName = resourcePath;
        if (numChannels < 0) numChannels = 0;
        clips = new Clip[numChannels];
        for (int i = 0; i < clips.length; i++) {
            clips[i] = getClip(resourcePath);
        }
        clipIndex = 0;
    }
    
    
    private static Clip getClip(String resourcePath) {
        try {
            URL url = Sound.class.getClassLoader().getResource(resourcePath);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            audioIn.close();
            return clip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
            return null;
        }
    }       

    /**
     * Play the sound.
     */
    public void play() {
        Clip clip = clips[clipIndex];
        if (clip.isRunning()) {
            Clip[] bc = new Clip[clips.length + 1];
            System.arraycopy(clips, 0, bc, 0, clips.length);
            bc[bc.length - 1] = getClip(fileName);
            clips = bc;
            clipIndex = clips.length - 1;
            clip = clips[clipIndex];
        }
        clip.setFramePosition(0);
        clip.start();
        if (clipIndex < clips.length - 1) clipIndex++;
        else clipIndex = 0;
    }
    
    /**
     * Stop the sound.
     */
    public void stop() {
        for (Clip c : clips) {
            c.stop();
        }
    }
    
    /**
     * Close the clips only when finished with them
     * because the sounds won't be able to be played
     * again if you close them.
     */
    public void close() {
        for (Clip c : clips) {
            c.close();
        }
    }
    
    /**
     * Create a silent sound clip
     */
    private static Clip getSilentClip() {
        byte[] silenceBytes = {0, 0};
        ByteArrayInputStream stream = new ByteArrayInputStream(silenceBytes);
        AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100.0f, 16, 1, 2, 44100.0f, false);
        AudioInputStream audioIn = new AudioInputStream(stream, format, 1);
        Clip clip = null;
        try {
            clip = AudioSystem.getClip();
            clip.open(audioIn);
            audioIn.close();
        } catch (LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
        return clip;
    }

    /**
     * Load a silent sound and play it to get the sound system initialized
     */
    private static void init() {
        Clip c = getSilentClip();
        c.addLineListener( e -> {
            if (e.getType().equals(LineEvent.Type.STOP)) {
                c.close();
            }
        });
        c.start();
    }
}