package org.example.sudoku;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

public class Sudoku extends Activity implements OnClickListener {

	private static final String TAG = "Sudoku";

	// private StoryProfile storyProfile = new StoryProfile();
	boolean doneVideo = false;
	private View continueButton;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.main);
		getWindow().setFormat(PixelFormat.TRANSLUCENT);

		// Show video at the beginning
		Intent i = new Intent(Sudoku.this, VideoviewActivity.class);
		i.putExtra(VideoviewActivity.setVIDEO, VideoviewActivity.SUDOKU);
		startActivity(i);
		// ------------------------------------------

		// Set up click listeners for all the buttons
		continueButton = findViewById(R.id.continue_button);
		continueButton.setOnClickListener(this);
		this.setStateContinueButton(Game.cont);
		View storyButton = findViewById(R.id.story_button);
		storyButton.setOnClickListener(this);
		View newButton = findViewById(R.id.new_button);
		newButton.setOnClickListener(this);
		View exitButton = findViewById(R.id.exit_button);
		exitButton.setOnClickListener(this);

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.story_button:
			Game.storymode = true;
			startGame(Game.DIFFICULTY_EASY);
			break;
		case R.id.new_button:
			Game.storymode = false;
			openNewGameDialog();
			break;
		case R.id.exit_button:
			confirmExit();
			break;
		case R.id.continue_button:
			startGame(Game.DIFFICULTY_CONTINUE);
			break;
		// More buttons go here (if any) ...
		}
	}

	// Confirm exit
	private void confirmExit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.confirm_exit)
				.setCancelable(false)
				.setPositiveButton(R.string.exit_yes_label,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int id) {
								// TODO Auto-generated method stub
								Sudoku.this.finish();
							}
						})
				.setNegativeButton(R.string.exit_no_label,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int id) {
								// TODO Auto-generated method stub
								dialog.cancel();
							}
						});
		builder.create();
		builder.show();
	}

	private void openNewGameDialog() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.new_game_title)
				.setItems(R.array.difficulty,
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialoginterface, int i) {
								startGame(i);
							}
						}).show();
	}

	private void startGame(int i) {
		Log.d(TAG, "clicked on " + i);
		// Start game here...
		Intent intent = new Intent(Sudoku.this, Game.class);
		intent.putExtra(Game.KEY_DIFFICULTY, i);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.settings:
			startActivity(new Intent(this, Prefs.class));
			return true;
		}
		return false;
	}

	protected void onResume() {

		super.onResume();
		this.setStateContinueButton(Game.cont);
		Music.play(this, R.raw.menu);
	}

	protected void onPause() {
		super.onPause();
		Music.stop(this);
	}

	private void setStateContinueButton(boolean state) {
		continueButton.setEnabled(state);
	}
}