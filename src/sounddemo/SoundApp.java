package sounddemo;
import java.applet.Applet;
import java.applet.AudioClip;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import quicksound.Sound;

public class SoundApp extends Application {
	
	// save a reference to the MediaPlayer so it will not get garbage collected
	// once the start method is done. This is technically not needed in this demo
	// since there are button handlers that save references to musicPlayer
	// through lambda expressions, however if you don't have any such references
	// your music will stop are some random time possibly very soon after your
	// application starts (depending on the memory your application is using)
	MediaPlayer musicPlayer;

	// Load WAV file
	// AudioClip works well for short sounds that need to be played a lot.
	// It will not work for long sounds, such as a music mp3
	AudioClip coinSound = Applet.newAudioClip(getClass().getClassLoader().getResource("resources/smw_coin.wav"));
	
	
	// Sound is a custom class I made for playing quick sounds in succession without
	// the annoying lag when first played. It basically initializes the sound system
	// when the program starts by playing a silent clip that wakes it up so all future
	// sounds will be immediate. It also saves multiple copies of the sound as Clip
	// objects so it can play the same sound very fast with no interrupt.
	// FYI, simply using this class once to load a sound will actually wake up
	// the sound system for all other sound classes too.
	// If you comment out all reference to quickSound, you will notice
	// the next time you press the Coin button there will be a noticable lag
	// before the sound is played
	Sound quickSound = new Sound("resources/smw_coin.wav");
	

	public static void main(String[] args) {
		launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Sound Demo JavaFX");
		
		// MediaPlayer is good for longer sounds like background music mp3s
		Media longMusic = new Media(getClass().getClassLoader().getResource("resources/Heroic Demise (New).mp3").toURI().toString());
		musicPlayer =  new MediaPlayer(longMusic);
		musicPlayer.setCycleCount(1); // plays once
		//musicPlayer.setCycleCount(Integer.MAX_VALUE); // loops forever
		
		HBox root = new HBox();
		
		// try spam clicking this button really fast. How many clicks per second
		// can you do? Notice if it is pressed too fast, the sound will cut off the
		// first attempt and keep starting over. If you require super fast
		// repeated sounds, you will want to use the custom Sound class.
		Button coinBtn = new Button("Coin");
		coinBtn.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				coinSound.play();
			}
		});
		root.getChildren().add(coinBtn);
		
		// try spam clicking this button really fast. How many clicks per second
		// can you do? Notice it will play the sound flawlessly very fast without
		// interuption
		Button spamCoinBtn = new Button("Spam Coin");
		spamCoinBtn.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				quickSound.play();
			}
		});
		root.getChildren().add(spamCoinBtn);
		
		Button startMusicBtn = new Button("Play Music");
		startMusicBtn.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				// start over at the beginning (needed if media finished playing)
				musicPlayer.seek(Duration.ZERO);
				musicPlayer.play();
			}
		});
		root.getChildren().add(startMusicBtn);
		
		ToggleButton loopMusicBtn = new ToggleButton("Turn Repeat On");
		loopMusicBtn.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				// toggle between repeat and no repeat
				if (musicPlayer.getCycleCount() == Integer.MAX_VALUE) {
					musicPlayer.setCycleCount(1);
					loopMusicBtn.setText("Turn Repeat On");
				} else {
					musicPlayer.setCycleCount(Integer.MAX_VALUE);
					loopMusicBtn.setText("Turn Repeat Off");
				}
			}
		});
		root.getChildren().add(loopMusicBtn);
		
		Button stopMusicBtn = new Button("Stop Music");
		stopMusicBtn.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				// Stop play (not the same as pausing)
				musicPlayer.stop();
			}
		});
		root.getChildren().add(stopMusicBtn);
		
		Button pauseMusicBtn = new Button("Pause Music");
		pauseMusicBtn.setMinWidth(100);
		pauseMusicBtn.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				// pause the music player if paused, otherwise resume
				if (pauseMusicBtn.getText().equals("Pause Music")) {
					musicPlayer.pause();
					pauseMusicBtn.setText("Resume Music");
				} else {
					musicPlayer.play(); // it will start where it left off
					pauseMusicBtn.setText("Pause Music");
				}
			}
		});
		root.getChildren().add(pauseMusicBtn);
		
		Button forward10MusicBtn = new Button("Forward 10 sec");
		forward10MusicBtn.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				// skip ahead 10 seconds
				Duration currentTime = musicPlayer.getCurrentTime();
				Duration tenSec = Duration.seconds(10);
				currentTime.add(tenSec);
				musicPlayer.seek(currentTime.add(tenSec));
			}
		});
		root.getChildren().add(forward10MusicBtn);
		
		Button forwardMusicBtn = new Button("Forward 5 min");
		forwardMusicBtn.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				// skip forward 5 minutes (useful for testing loop since it effectively finishes the song)
				musicPlayer.seek(Duration.minutes(5));
			}
		});
		root.getChildren().add(forwardMusicBtn);
		
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
	}

}
