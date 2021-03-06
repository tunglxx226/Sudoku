package org.example.sudoku;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

public class Game extends Activity implements OnClickListener {
	private static final String TAG = "Game.java";
	public static final String KEY_DIFFICULTY = "org.example.sudoku.difficulty";

	public static final int DIFFICULTY_EASY = 0;
	public static final int DIFFICULTY_MEDIUM = 1;
	public static final int DIFFICULTY_HARD = 2;
	public static final int DIFFICULTY_CONTINUE = -1;

	private static final int BLANK = 0;
	private static final int DEFINED = 1;

	// Background:
	private static final int backgroundid[] = { R.drawable.hulijingwp,
			R.drawable.aucowp, R.drawable.gamebackground };

	// Quiz
	private InputStream is;
	BufferedReader bufferedReader;
	int fileLength;
	private Quiz quiz;

	// Opponents' IDs
	Opponents opponent;
	public static final int HULIJING = 0;
	public static final int AU_CO = 1;
	/** CAUTION: The opponents' IDs is also the index of the appropriate level **/
	// Skills' names
	// HULIJING:
	public static final String hulijing1 = "QUẪY ĐUÔI.";
	public static final String hulijing2 = "MUÔN HÌNH VẠN TRẠNG.";
	public static final String hulijing3 = "BÌNH ĐỊA.";
	// --------------------------------------------------
	// AUCO:
	public static final String auco1 = "MẮT THẦN.";
	public static final String auco2 = "CÁNH ĐỒNG BẤT TẬN.";
	public static final String auco3 = "MẦM SỐNG.";
	// ------------------------------------------------

	private StopWatch stopwatch = new StopWatch();
	private static final String keyTime = "time";
	private long atTime = 0;
	private Bundle bundle;
	// Continue or not
	public static boolean cont = false;

	// Game profile statistic
	public static int level;
	public static boolean storymode = false;
	public static StoryProfile storyProfile = new StoryProfile();

	// Keys for onPause()
	private static final String key = "puzzle";
	private static final String keyPredefined = "predefined";
	private static final String keyBlankTile = "blank_tiles";
	private static final String keyInvalidMove = "invalid_moves";
	private static final String keyOrigin = "originalPuzzle";
	private static final String keyLevel = "level";

	private int puzzle[] = new int[9 * 9];
	// Used to store the state of the original puzzle
	private int originalPuzzle[] = new int[9 * 9];
	// Use to store the predefined tile by game
	private int predefined[] = new int[9 * 9];
	// Used to track the the remaining blank tiles
	private int blank_tiles = 0;
	// Used to track the number of invalid moves of game
	private int invalid_moves = 0;

	public PuzzleView puzzleView;
	public View gameView;
	public TextView skillTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.gameview);
		skillTextView = (TextView) findViewById(R.id.skilltextview);
		// Input Stream:
		is = getResources().openRawResource(R.raw.quiz);
		bufferedReader = new BufferedReader(new InputStreamReader(is));
		String line[] = new String[100];
		try {
			int i = 0;
			while (bufferedReader.readLine() != null) {
				line[i] = bufferedReader.readLine();
				i++;
			}
			fileLength = i;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		gameView = findViewById(R.id.gameviewlayout);
		Random rand = new Random();
		quiz = new Quiz(line[rand.nextInt(fileLength - 1)]);
		// Show video at the beginning
		if (storymode == true) {
			Intent i = new Intent(Game.this, VideoviewActivity.class);
			i.putExtra(VideoviewActivity.setVIDEO, VideoviewActivity.GAME);
			startActivity(i);
			gameView.setBackgroundDrawable(getResources().getDrawable(
					backgroundid[0]));
		}
		// ---------------------------------------------------

		bundle = savedInstanceState;

		Log.d(TAG, "onCreate");

		cont = false;
		int diff = getIntent().getIntExtra(KEY_DIFFICULTY, DIFFICULTY_EASY);

		// When continue:
		if (savedInstanceState != null) {
			puzzle = fromPuzzleString(savedInstanceState.getString(key));
			predefined = fromPuzzleString(savedInstanceState
					.getString(keyPredefined));
			originalPuzzle = fromPuzzleString(savedInstanceState
					.getString(keyOrigin));

			level = savedInstanceState.getInt(keyLevel);

			blank_tiles = savedInstanceState.getInt(keyBlankTile);
			invalid_moves = savedInstanceState.getInt(keyInvalidMove);

			atTime = bundle.getLong(keyTime);
			stopwatch.startAt(atTime);
			if (storymode == true) {
				storyProfile = new StoryProfile(level);
				gameView.setBackgroundDrawable(getResources().getDrawable(
						backgroundid[level]));
			}
			Log.d(TAG, "Level: " + Integer.toString(level));
		}
		// if saveInstanceState == null (not continue)
		else {
			puzzle = getPuzzle(diff);
			level = storyProfile.getLevel();
			if (storymode == true) {
				storyProfile = new StoryProfile(level);
			}
			stopwatch.start();
			Log.d(TAG, "Level: " + Integer.toString(level));
		}

		// Set up story mode
		if (storymode == true) {
			storyProfile = new StoryProfile(level);

			opponent = new Opponents(level);
		}
		// --------------------------------------------------------

		// -------------------------------------------------------------------------
		// If game is not finished then continue loading puzzleView
		if (!isFinished()) {

			cont = true;
			puzzleView = (PuzzleView) findViewById(R.id.puzzleId);

		}
		// If game is finished, set cont to false and finish the game
		else {
			cont = false;
			Game.this.finish();
		}

		// Clear function implementation
		View clearButton = findViewById(R.id.clear_button);
		clearButton.setOnClickListener(this);
		View restartButton = findViewById(R.id.restart_button);
		restartButton.setOnClickListener(this);
		View undoButton = findViewById(R.id.undo_button);
		undoButton.setOnClickListener(this);
		View pauseButton = findViewById(R.id.pause_button);
		pauseButton.setOnClickListener(this);
		TextView stopwatchView = (TextView) findViewById(R.id.stopwatch);
		stopwatchView.setText(getElapsedTime());
		Log.d(TAG, getElapsedTime());

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.restart_button:
			this.confirmRestart();
			break;
		case R.id.clear_button:
			this.confirmClear();
			break;
		case R.id.undo_button:
			Toast toast3 = Toast.makeText(getApplicationContext(),
					"Undo button", Toast.LENGTH_SHORT);
			toast3.show();
			break;
		case R.id.pause_button:
			Toast toast = Toast.makeText(getApplicationContext(),
					"Pause button", Toast.LENGTH_SHORT);
			toast.show();
			break;
		}
	}

	// -------------Implement buttons--------------------

	// -------------Clear Button
	private void clearPuzzle() {
		invalid_moves = 0;
		puzzle = this.copyArray(originalPuzzle);
		this.initialBlankTile(toPuzzleString(originalPuzzle));
		this.puzzleView.invalidate();
	}

	// -------------Restart button
	private void restartGame() {
		if (storymode == false) {
			this.openNewGameDialog();
			stopwatch = new StopWatch();
		} else {
			this.finishGame(true);
			stopwatch = new StopWatch();
		}

	}

	// Get or set level and intro movies
	public int getLevel() {
		return level;
	}

	public void setLevel(int newLevel) {
		level = newLevel;
	}

	// ...
	public void showKeypadOrError(int x, int y) {
		Dialog v = new Keypad(this, this.puzzleView, x, y);
		v.show();
	}

	private int[] getPuzzle(int diff) {
		String puz;
		// TODO: Continue last game
		switch (diff) {
		case DIFFICULTY_CONTINUE:
			quiz.preparation(DIFFICULTY_EASY);
			puz = getPreferences(MODE_PRIVATE).getString(key, quiz.getQuiz());
			predefined = fromPuzzleString(getPreferences(MODE_PRIVATE)
					.getString(keyPredefined, quiz.getQuiz()));
			originalPuzzle = fromPuzzleString(getPreferences(MODE_PRIVATE)
					.getString(keyOrigin, quiz.getQuiz()));
			blank_tiles = getPreferences(MODE_PRIVATE).getInt(keyBlankTile, 0);
			invalid_moves = getPreferences(MODE_PRIVATE).getInt(keyInvalidMove,
					0);
			break;
		case DIFFICULTY_HARD:
			quiz.preparation(DIFFICULTY_HARD);
			puz = quiz.getQuiz();
			getPredefinedTileFromPuzzle(puz, predefined);
			this.storeOriginalState(puz);
			this.initialBlankTile(puz);
			break;
		case DIFFICULTY_MEDIUM:
			quiz.preparation(DIFFICULTY_MEDIUM);
			puz = quiz.getQuiz();
			getPredefinedTileFromPuzzle(puz, predefined);
			this.storeOriginalState(puz);
			this.initialBlankTile(puz);
			break;
		case DIFFICULTY_EASY:
		default:
			quiz.preparation(DIFFICULTY_EASY);
			puz = quiz.getQuiz();
			getPredefinedTileFromPuzzle(puz, predefined);
			this.storeOriginalState(puz);
			this.initialBlankTile(puz);
			break;
		}
		return fromPuzzleString(puz);
	}

	static private String toPuzzleString(int[] puz) {
		StringBuilder buf = new StringBuilder();
		for (int element : puz) {
			buf.append(element);
		}
		return buf.toString();
	}

	static protected int[] fromPuzzleString(String string) {
		int[] puz = new int[string.length()];
		for (int i = 0; i < puz.length; i++) {
			puz[i] = string.charAt(i) - '0';
		}
		return puz;
	}

	private int getTile(int x, int y) {
		return puzzle[y * 9 + x];
	}

	// Update to decrease the number of blank tiles if the current tile is blank
	// Also check for valid move right inside this function
	// NEW: boolean skills added: to consider if this is skill-generate action
	// or user-generate action
	// skills == true: user-generate
	// skills == false: skill-generate (automatic)
	public void setTile(int x, int y, int value, boolean skills) {
		if (puzzle[y * 9 + x] == 0) {
			if (value != 0)
				this.decreaseBlankTile();
			if (!this.checkValidMove(x, y, value)) {
				this.increaseInvalidMove();
			}

		} else // Already filled before
		{
			// On user update move
			if (!this.checkValidMove(x, y, puzzle[y * 9 + x])) {
				// User change from invalid move to valid move
				if (this.checkValidMove(x, y, value)) {
					this.decreaseInvalidMove();
				}
			} else {
				// User change from valid move to invalid move
				if (!this.checkValidMove(x, y, value)) {
					this.increaseInvalidMove();
				}
			}
			if (value == 0) {
				this.increaseBlankTile();
			}
		}
		puzzle[y * 9 + x] = value;
		if (storymode == true && skills == true) {
			int[] skillRate = new int[3];
			skillRate = opponent.getSkillRate();
			skillsGenerate(skillRate[2], skillRate[1], skillRate[0]);
		}
		Log.d(TAG, "invalid moves: " + Integer.toString(invalid_moves)
				+ " + blank tiles: " + Integer.toString(blank_tiles));
		Toast.makeText(
				getApplicationContext(),
				"invalid moves: " + Integer.toString(invalid_moves)
						+ " + blank tiles: " + Integer.toString(blank_tiles),
				Toast.LENGTH_SHORT).show();
	}

	protected String getTileString(int x, int y) {
		int v = getTile(x, y);
		if (v == 0) {
			return "";
		} else {
			return String.valueOf(v);
		}
	}

	protected void finishGame(boolean isRestart) {
		cont = false;
		stopwatch.stop();
		if (storymode == true) {
			if (!isRestart) {
				storyProfile.levelUp();
			}
			level = storyProfile.getLevel();
			Intent i = new Intent(this, Game.class);
			startActivity(i);
		}
		finish();
	}

	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		Music.play(this, R.raw.game);
		atTime = 0;
		if (bundle != null) {
			atTime = bundle.getLong(keyTime);
		}
		stopwatch.startAt(atTime);
	}

	protected void onPause() {
		super.onPause();
		Log.d(TAG, "onPause");
		Music.stop(this);
		stopwatch.stop();
		getPreferences(MODE_PRIVATE).edit()
				.putString(key, toPuzzleString(puzzle)).commit();
		getPreferences(MODE_PRIVATE).edit()
				.putString(keyPredefined, toPuzzleString(predefined)).commit();
		getPreferences(MODE_PRIVATE).edit()
				.putString(keyOrigin, toPuzzleString(originalPuzzle)).commit();
		getPreferences(MODE_PRIVATE).edit().putInt(keyBlankTile, blank_tiles)
				.commit();
		getPreferences(MODE_PRIVATE).edit()
				.putInt(keyInvalidMove, invalid_moves).commit();
		getPreferences(MODE_PRIVATE).edit()
				.putInt(keyLevel, storyProfile.getLevel()).commit();

		getPreferences(MODE_PRIVATE).edit()
				.putLong(keyTime, stopwatch.getElapsedTimeSecs()).commit();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		stopwatch.stop();
		outState.putString(key, toPuzzleString(puzzle));
		outState.putString(keyPredefined, toPuzzleString(predefined));
		outState.putString(keyOrigin, toPuzzleString(originalPuzzle));
		outState.putInt(keyBlankTile, blank_tiles);
		outState.putInt(keyInvalidMove, invalid_moves);
		outState.putLong(keyTime, stopwatch.getElapsedTime());
		outState.putInt(keyLevel, storyProfile.getLevel());

		super.onSaveInstanceState(outState);
	}

	// March 28th: Congratulation skill
	// Confirm exit
	private void confirmExit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.congratscreen_title)
				.setCancelable(false)
				.setPositiveButton(R.string.menu_label,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int id) {
								// TODO Auto-generated method stub
								Game.this.finishGame(false);
							}
						})
				.setNegativeButton(R.string.replay_label,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int id) {
								// TODO Auto-generated method stub
								openNewGameDialog();
								dialog.cancel();
								// bug here: crash when user cancel
								// openNewGameDialog()
								// wait to fix
							}
						});
		builder.create();
		builder.show();
	}

	private void openNewGameDialog() {
		new AlertDialog.Builder(this)
				.setCancelable(false)
				.setTitle(R.string.new_game_title)
				.setItems(R.array.difficulty,
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialoginterface, int i) {
								Game.this.finishGame(false);
								startGame(i);

							}
						}).show();
	}

	private void startGame(int i) {
		Log.d(TAG, "clicked on " + i);
		// Start game here...
		Intent intent = new Intent(Game.this, Game.class);
		intent.putExtra(Game.KEY_DIFFICULTY, i);
		startActivity(intent);
	}

	// -----------------------------------------------------------------------
	public String getElapsedTime() {
		long temp = stopwatch.getElapsedTimeSecs();
		int hour = (int) Math.floor(temp / 3600);

		int minute = (int) Math.floor((temp - (3600 * hour)) / 60);

		int second = (int) (temp - minute * 60 - hour * 3600);
		return hour + ":" + minute + ":" + second;
	}

	// Get the tiles that are predefined by game (which are not blank when the
	// game start)
	private void getPredefinedTileFromPuzzle(String puz, int[] predefined) {

		int[] puzzleTmp = new int[9 * 9];
		puzzleTmp = fromPuzzleString(puz);

		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				final int tmp = puzzleTmp[j * 9 + i];
				if (tmp == 0) {
					predefined[j * 9 + i] = BLANK;
				} else {
					predefined[j * 9 + i] = DEFINED;
				}
			}
		}
	}

	public int[] getPredefinedTileFromPuzzle() {
		return this.predefined;
	}

	// -----------------------------------------------------------
	// -----------New implementation of finish()------------------

	// Finish game if there are no more tile and all tiles are valid
	public boolean isFinished() {
		return (blank_tiles == 0 && invalid_moves == 0);
	}

	// Check duplicate number horizontally
	// Return true if valid
	// Return false if exist a duplicate number horizontally
	private boolean checkHorizontal(int X, int Y, int t) {
		for (int i = 0; i < 9; i++) {
			if ((i == X) || (puzzle[Y * 9 + i] == 0)) {
				continue;
			} else {
				if (puzzle[Y * 9 + i] == t) {
					return false;
				}
			}
		}
		return true;
	}

	// Check duplicate number vertically
	// Return true if valid
	// Return false if exist a duplicate number vertically
	private boolean checkVertical(int X, int Y, int t) {
		for (int i = 0; i < 9; i++) {
			if ((i == Y) || (puzzle[i * 9 + X] == 0)) {
				continue;
			} else {
				if (puzzle[i * 9 + X] == t) {
					return false;
				}
			}
		}
		return true;
	}

	// Check duplicate number on the same cell block
	// Return true if valid
	// Return false if exist a duplicate number in this cell block
	private boolean checkSameCellBlock(int X, int Y, int t) {
		int startX = (X / 3) * 3;
		int startY = (Y / 3) * 3;
		for (int i = startX; i < startX + 3; i++) {
			for (int j = startY; j < startY + 3; j++) {
				if (((i == X) && (j == Y)) || (puzzle[j * 9 + i] == 0)) {
					continue;
				} else {
					if (puzzle[j * 9 + i] == t) {
						return false;
					}
				}
			}
		}
		return true;
	}

	// Check valid move
	public boolean checkValidMove(int X, int Y, int t) {
		if (this.checkHorizontal(X, Y, t) && this.checkVertical(X, Y, t)
				&& this.checkSameCellBlock(X, Y, t)) {
			return true;
		}
		return false;
	}

	// Increase the number of valid moves
	private void increaseInvalidMove() {
		++invalid_moves;
	}

	// Decrease the number of invalid moves
	private void decreaseInvalidMove() {
		--invalid_moves;
	}

	// Decrease the number of blank tiles
	private void decreaseBlankTile() {
		--blank_tiles;
	}

	// Increase the number of blank tiles
	private void increaseBlankTile() {
		++blank_tiles;
	}

	// Set the number of blank tiles
	private void setBlankTile(int num) {
		blank_tiles = num;
	}

	// Get the initial blank tile from puzzle
	public void initialBlankTile(String puz) {
		int numBlank = 0;
		for (int i = 0; i < puz.length(); i++) {
			final char c = puz.charAt(i);
			if (c == '0') {
				++numBlank;
			}
		}
		setBlankTile(numBlank);
	}

	// Check to see if the game is finish
	public void checkFinishGame() {
		if (this.isFinished()) {
			if (storymode == true && storyProfile != null) {
				String message = getResources().getString(R.string.level) + " "
						+ Integer.toString(level + 1) + " "
						+ getResources().getString(R.string.complete)
						+ " with " + opponent.getName() + " "
						+ opponent.getSkill(0).getName() + " "
						+ opponent.getSkill(1).getName() + " "
						+ opponent.getSkill(2).getName() + " ";

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(message)
						.setCancelable(false)
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int id) {
										storyProfile.levelUp();
										Intent i = new Intent(Game.this,
												Game.class);
										startActivity(i);
										finish();
									}
								});
				builder.create();
				builder.show();
				return;
			}
			this.confirmExit();
		}
	}

	// Store the original state of puzzle
	private void storeOriginalState(String puz) {
		this.originalPuzzle = fromPuzzleString(puz);
	}

	// Copy array
	private int[] copyArray(int[] origin) {
		int index = origin.length;
		int[] tmp = new int[index];
		for (int i = 0; i < index; i++) {
			tmp[i] = origin[i];
		}
		return tmp;
	}

	// Confirm before clear all filled-in tiles
	// Confirm exit
	private void confirmClear() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.confirm_clear)
				.setCancelable(false)
				.setPositiveButton(R.string.exit_yes_label,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int id) {
								// TODO Auto-generated method stub
								clearPuzzle();
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

	// Confirm before terminal current game and start another one game
	private void confirmRestart() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.confirm_restart)
				.setCancelable(false)
				.setPositiveButton(R.string.exit_yes_label,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int id) {
								// TODO Auto-generated method stub
								restartGame();
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

	// Random skills generate mechanism
	private void skillsGenerate(int rateSkill2, int rateSkill1, int rateSkill0) {
		Random srand = new Random();
		boolean skillInvoked = false;
		Typeface tf = Typeface
				.createFromAsset(getAssets(), "fonts/merscrb.ttf");// remember: the font's name extension must be lower case
		skillTextView.setTypeface(tf);
		// Skill or not.
		int i = srand.nextInt(100);
		/**
		 * CAUTION: for skill[], the index 0 must be the one with the least
		 * effect
		 **/
		if (i < rateSkill2) {
			Toast toast = Toast.makeText(getApplicationContext(), opponent
					.getSkill(2).getName(), Toast.LENGTH_SHORT);
			skillEffects(2);
			toast.show();
			skillTextView.setText(opponent.getSkill(2).getName());
			skillInvoked = true;
		} else if (i < rateSkill1) {
			Toast toast = Toast.makeText(getApplicationContext(), opponent
					.getSkill(1).getName(), Toast.LENGTH_SHORT);
			skillEffects(1);
			skillTextView.setText(opponent.getSkill(1).getName());
			toast.show();
			skillInvoked = true;
		}

		else if (i < rateSkill0) {
			Toast toast = Toast.makeText(getApplicationContext(), opponent
					.getSkill(0).getName(), Toast.LENGTH_SHORT);
			skillEffects(0);
			skillTextView.setText(opponent.getSkill(0).getName());
			toast.show();
			skillInvoked = true;
		} else {
			Toast toast = Toast.makeText(getApplicationContext(),
					"No Skill invoked", Toast.LENGTH_SHORT);
			toast.show();
			skillInvoked = false;
		}
		if (skillInvoked) {
			skillTextView.setVisibility(View.VISIBLE);
			skillTextView.startAnimation(AnimationUtils.loadAnimation(this,
					R.anim.shake));
			skillTextView.setVisibility(View.INVISIBLE);
			skillInvoked = false;
		}
	}

	// Automatically delete a number of random tiles of the puzzle:
	private void autoDelRand() {

		int randX = 0;
		int randY = 0;

		SecureRandom srand = new SecureRandom();
		int[] predefined = new int[81];
		// Get the tile that are not blank (predefined by game)
		predefined = getPredefinedTileFromPuzzle();

		while (predefined[randY * 9 + randX] != 0) {
			randX = srand.nextInt(9);
			randY = srand.nextInt(9);
			continue;
		}
		Toast.makeText(getApplicationContext(),
				Integer.toString(randX) + Integer.toString(randY),
				Toast.LENGTH_SHORT).show();
		setTile(randX, randY, 0, false);

	}

	// Automatically change a number of random tiles of the puzzle:
	private void autoChangeRand() {

		int randX = 0;
		int randY = 0;

		SecureRandom srand = new SecureRandom();
		int[] predefined = new int[81];
		// Get the tile that are not blank (predefined by game)
		predefined = getPredefinedTileFromPuzzle();

		while (predefined[randY * 9 + randX] != 0) {
			randX = srand.nextInt(9);
			randY = srand.nextInt(9);
			continue;
		}
		int randValue = srand.nextInt(9);
		Toast.makeText(getApplicationContext(),
				Integer.toString(randX) + Integer.toString(randY),
				Toast.LENGTH_SHORT).show();
		setTile(randX, randY, randValue, false);
	}

	// Skills take effects:
	private void skillEffects(int i) {
		if (opponent.getSkill(i).getName() == hulijing1) {
			puzzleView.startAnimation(AnimationUtils.loadAnimation(this,
					R.anim.shake));
			autoDelRand();
			return;
		} else if (opponent.getSkill(i).getName() == hulijing2) {
			puzzleView.startAnimation(AnimationUtils.loadAnimation(this,
					R.anim.fadein));

			autoChangeRand();
			return;
		} else if (opponent.getSkill(i).getName() == hulijing3) {
			puzzleView.startAnimation(AnimationUtils.loadAnimation(this,
					R.anim.blink));
			clearPuzzle();
			return;
		} else if (opponent.getSkill(i).getName() == auco1) {
			return;
		} else if (opponent.getSkill(i).getName() == auco2) {
			return;
		} else if (opponent.getSkill(i).getName() == auco3) {
			restartGame();
			return;
		}
	}

}
