package org.example.sudoku;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class Game extends Activity {
	private static final String TAG = "Game.java";
	public static final String KEY_DIFFICULTY = "org.example.sudoku.difficulty";
	public static final int DIFFICULTY_EASY = 0;
	public static final int DIFFICULTY_MEDIUM = 1;
	public static final int DIFFICULTY_HARD = 2;
	public static final int DIFFICULTY_CONTINUE = -1;

	private static final int BLANK = 0;
	private static final int DEFINED = 1;

	private StopWatch stopwatch;
	private static final String keyTime = "time";
	private long atTime = 0;
	private Bundle bundle;
	// Continue or not
	public static boolean cont = false;

	// Game profile statistic
	private int level;
	private String introVideoPath;
	public static boolean storymode = false;

	// Keys for onPause()
	private static final String key = "puzzle";
	private static final String keyPredefined = "predefined";
	private static final String keyBlankTile = "blank_tiles";
	private static final String keyInvalidMove = "invalid_moves";

	private final String easyPuzzle = "362581479914237856785694231"
			+ "170462583823759614546813927" + "431925768657148392298376140";
	private final String mediumPuzzle = "650000070000506000014000005"
			+ "007009000002314700000700800" + "500000630000201000030000097";
	private final String hardPuzzle = "009000000080605020501078000"
			+ "000000700706040102004000000" + "000720903090301080000000600";

	private int puzzle[] = new int[9 * 9];
	// Use to store the predefined tile by game
	private int predefined[] = new int[9 * 9];
	// Used to track the the remaining blank tiles
	private int blank_tiles = 0;
	// Used to track the number of invalid moves of game
	private int invalid_moves = 0;

	public PuzzleView puzzleView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		bundle = savedInstanceState;
		// initialize puzzleView
		/*
		 * puzzleView = new PuzzleView(this);
		 * puzzleView.setFocusableInTouchMode(true);
		 * puzzleView.setFocusable(true);
		 */

		Log.d(TAG, "onCreate");

		cont = false;
		stopwatch = new StopWatch();
		int diff = getIntent().getIntExtra(KEY_DIFFICULTY, DIFFICULTY_EASY);
		if (savedInstanceState != null) {
			puzzle = fromPuzzleString(savedInstanceState.getString(key));
			predefined = fromPuzzleString(savedInstanceState
					.getString(keyPredefined));

			blank_tiles = savedInstanceState.getInt(keyBlankTile);
			invalid_moves = savedInstanceState.getInt(keyInvalidMove);

			atTime = bundle.getLong(keyTime);
			stopwatch.startAt(atTime);
		} else {
			puzzle = getPuzzle(diff);
			stopwatch.start();
		}

		// -------------------------------------------------------------------------
		// If game is not finished then continue loading puzzleView
		if (!isFinished()) {

			setContentView(R.layout.gameview);
			cont = true;
			puzzleView = (PuzzleView) findViewById(R.id.puzzleId);
		}
		// If game is finished, set cont to false and finish the game
		else {
			cont = false;
			Game.this.finish();
		}
	}

	// Get or set level and intro movies
	public int getLevel() {
		return level;
	}

	public String getIntro() {
		return introVideoPath;
	}

	public void setLevel(int newLevel) {
		level = newLevel;
	}

	public void setIntro(String videoPath) {
		introVideoPath = videoPath;
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
			puz = getPreferences(MODE_PRIVATE).getString(key, easyPuzzle);
			predefined = fromPuzzleString(getPreferences(MODE_PRIVATE)
					.getString(keyPredefined, easyPuzzle));
			blank_tiles = getPreferences(MODE_PRIVATE).getInt(keyBlankTile, 0);
			invalid_moves = getPreferences(MODE_PRIVATE).getInt(keyInvalidMove,
					0);
			break;
		case DIFFICULTY_HARD:
			puz = hardPuzzle;
			getPredefinedTileFromPuzzle(puz, predefined);
			this.initialBlankTile(puz);
			break;
		case DIFFICULTY_MEDIUM:
			puz = mediumPuzzle;
			getPredefinedTileFromPuzzle(puz, predefined);
			this.initialBlankTile(puz);
			break;
		case DIFFICULTY_EASY:
		default:
			puz = easyPuzzle;
			getPredefinedTileFromPuzzle(puz, predefined);
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
	public void setTile(int x, int y, int value) {
		if (puzzle[y * 9 + x] == 0) {
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
		}
		puzzle[y * 9 + x] = value;
	}

	protected String getTileString(int x, int y) {
		int v = getTile(x, y);
		if (v == 0) {
			return "";
		} else {
			return String.valueOf(v);
		}
	}

	protected void finishGame() {
		cont = false;
//		stopwatch.stop();
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
		getPreferences(MODE_PRIVATE).edit().putInt(keyBlankTile, blank_tiles)
				.commit();
		getPreferences(MODE_PRIVATE).edit()
				.putInt(keyInvalidMove, invalid_moves).commit();

		getPreferences(MODE_PRIVATE).edit()
				.putLong(keyTime, stopwatch.getElapsedTimeSecs()).commit();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		stopwatch.stop();
		outState.putString(key, toPuzzleString(puzzle));
		outState.putString(keyPredefined, toPuzzleString(predefined));
		outState.putInt(keyBlankTile, blank_tiles);
		outState.putInt(keyInvalidMove, invalid_moves);
		outState.putLong(keyTime, stopwatch.getElapsedTime());
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
								Game.this.finishGame();
							}
						})
				.setNegativeButton(R.string.replay_label,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int id) {
								// TODO Auto-generated method stub
								openNewGameDialog();
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
								Game.this.finishGame();
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

	// Check finish-able property
	public boolean isFinishable() {
		// return FINISH_ABLE;
		if (invalid_moves == 0) {
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
			this.confirmExit();
		}
	}
}
