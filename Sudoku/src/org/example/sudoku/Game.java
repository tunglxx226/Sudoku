package org.example.sudoku;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Game extends Activity
{
	private static final String TAG = "Game.java";
	public static final String KEY_DIFFICULTY = "org.example.sudoku.difficulty";
	public static final int DIFFICULTY_EASY = 0;
	public static final int DIFFICULTY_MEDIUM = 1;
	public static final int DIFFICULTY_HARD = 2;
	public static final int DIFFICULTY_CONTINUE = -1;
	
	private StopWatch stopwatch;
	private static final String keyTime = "time";
	private long atTime = 0;
	private Bundle bundle;
	// Continue or not
	public static boolean cont = false;
	
	// Neu game hoan thanh roi thi no se dung isFinish duoc, nhu the 
	
	private static final String key = "puzzle";
	private final String easyPuzzle =
			"362581479914237856785694231" +
			"170462583823759614546813927" +
			"431925768657148392298376140";
	private final String mediumPuzzle =
			"650000070000506000014000005" +
			"007009000002314700000700800" +
			"500000630000201000030000097";
	private final String hardPuzzle =
			"009000000080605020501078000" +
			"000000700706040102004000000" +
			"000720903090301080000000600";
	
	private int puzzle[] = new int[9*9];
	
	public PuzzleView puzzleView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
		bundle = savedInstanceState;
		//initialize puzzleView
		puzzleView = new PuzzleView(this);
		puzzleView.setFocusableInTouchMode(true);
		puzzleView.setFocusable(true);
		
		Log.d(TAG, "onCreate");
		
		cont = false;
		stopwatch = new StopWatch();
		int diff = getIntent().getIntExtra(KEY_DIFFICULTY, DIFFICULTY_EASY);
		if (savedInstanceState != null)
		{
			puzzle = fromPuzzleString(savedInstanceState.getString(key));
			atTime = bundle.getLong(keyTime);
			stopwatch.startAt(atTime);
		}
		else
		{
			puzzle = getPuzzle(diff);
			stopwatch.start();
		}
		calculateUsedTiles();		
		
		//defines tile that was already filled by the game
		//----ma nguon them boi Tunglxx--------------------------------------------
		//----chuc nang: khong cho phep sua cac o da co san trong de bai-----------
		calculateUsedTilesIndex(question);
		//-------------------------------------------------------------------------
		// If game is not finished then continue loading puzzleView
		if (!isFinish())
		{
			
			setContentView(R.layout.gameview);
			View v = getLayoutInflater().inflate(R.layout.gameview, null);
//			puzzleView = (PuzzleView) v.findViewById(R.id.puzzleId);
			if (puzzleView == null)
			{
				Log.e(TAG, "onCreate: puzzleView is NULL");
			}
			cont = true;
			LinearLayout mLayout1 = (LinearLayout) findViewById(R.id.linearlayouttop);
			LinearLayout mLayout2 = (LinearLayout) findViewById(R.id.linearlayoutbottom);
		}
		// If game is finished, set cont to false and finish the game
		else 
		{
			cont = false;
			Game.this.finish();
		}
	}
	
	// ...
	public void showKeypadOrError(int x, int y)
	{
		int tiles[] = getUsedTiles(x,y);
		if (tiles.length == 9)
		{
			Toast toast = Toast.makeText(this, R.string.no_moves_label, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}else
		{
			Log.d(TAG, "showKeypad: used=" + toPuzzleString(tiles));
			Dialog v = new Keypad(this, this.puzzleView, x, y);
			v.show();
		}
	}
	
	public boolean setTileIfValid(int x, int y, int value)
	{
		Log.i(TAG, "setTileIfValid: stepping on");
		int tiles[] = getUsedTiles(x,y);
		if (value != 0)
		{
			for (int tile : tiles)
			{
				if (tile == value)
				{
					Log.e(TAG, "setTileIfValid: failed on setTile, tile = " + tile + " value = " + value);
					return false;
				}
			}
		}
		setTile(x,y,value);
		calculateUsedTiles();
		return true;
	}
	
	private final int used[][][] = new int[9][9][];
	
	protected int[] getUsedTiles(int x, int y)
	{
		return used[x][y];
	}
	
	private int[] calculateUsedTiles(int x,int y)
	{
		int c[] = new int[9];
		//horizontal
		for (int i = 0; i < 9; i++)
		{
			if (i == y)
			{
				continue;
			}
			int t = getTile(x,i);
			if (t != 0)
			{
				c[t - 1] = t;
			}
		}
		//vertical
		for (int i = 0; i < 9; i++)
		{
			if (i == x)
			{
				continue;
			}
			int t = getTile(i, y);
			if (t != 0)
			{
				c[t - 1] = t;
			}
		}
		
		// same cell block
		int startx = (x / 3) * 3;
		int starty = (y / 3) * 3;
		for (int i = startx; i < startx + 3; i++) 
		{
			for (int j = starty; j < starty + 3; j++) 
			{
				if (i == x && j == y)
					continue;
				int t = getTile(i, j);
				if (t != 0)
					c[t - 1] = t;
			}
		}
		
		// compress
		int nused = 0;
		for (int t : c)
		{
			if (t != 0)
			{
				nused++;
			}
		}
		int c1[] = new int[nused];
		nused = 0;
		for (int t : c)
		{
			if (t != 0)
			{
				c1[nused++] = t;
			}
		}
		return c1;
	}
	
	private void calculateUsedTiles()
	{
		for (int x = 0; x < 9; x++)
		{
			for (int y = 0; y < 9; y++)
			{
				used[x][y] = calculateUsedTiles(x, y);
			}
		}
	}
	
	private int[] getPuzzle(int diff)
	{
		String puz;
		// TODO: Continue last game
		switch (diff) 
		{
			case DIFFICULTY_CONTINUE:
				puz = getPreferences(MODE_PRIVATE).getString(key, easyPuzzle);
				break;
			case DIFFICULTY_HARD:
				puz = hardPuzzle;
				break;
			case DIFFICULTY_MEDIUM:
				puz = mediumPuzzle;
				break;
			case DIFFICULTY_EASY:
			default:
				puz = easyPuzzle;
				break;
		}
		return fromPuzzleString(puz);
	}
	
	static private String toPuzzleString(int[] puz)
	{
		StringBuilder buf = new StringBuilder();
		for (int element : puz)
		{
			buf.append(element);
		}
		return buf.toString();
	}

	static protected int[] fromPuzzleString(String string)
	{
		int[] puz = new int[string.length()];
		for (int i = 0; i < puz.length; i++)
		{
			puz[i] = string.charAt(i) - '0';
		}
		return puz;
	}

	private int getTile(int x, int y)
	{
		return puzzle[y*9 + x];
	}
	public void setTile(int x, int y, int value)
	{
		puzzle[y*9 + x] = value;
	}
	protected String getTileString(int x, int y)
	{
		int v = getTile(x, y);
		if (v == 0)
		{
			return "";
		}
		else
		{
			return String.valueOf(v);
		}
	}
	//--ma nguon them boi Tunglx-----------------------------------------------
	//----chuc nang: khong cho phep sua cac o da co san trong de bai-----------
	
	protected final int question[][] = new int [81][2];
	protected int usedNum = 0;
	private void calculateUsedTilesIndex(int[][] c) // Ham nay duoc sua de co the tinh toan cac index
													// cua cac o da su dung.
	{
		int k = 0;
		for (int i = 0; i < 9; i++)
		{
			for (int j = 0; j < 9; j++)
			{
				final int t = getTile(i, j);
				if (t != 0)
				{
					c[k][0] = i;
					c[k][1] = j;
					k++;
				}
			}
		}
		usedNum = k; // dem so luong o da duoc su dung, phuc vu cho ham` isFinish()
	}
	
	protected int[][] getUsedIndex()
	{
		return question;
	}
	//-------------------------------------------------------------------------
	
	
	// Ma nguon them boi Tunglx------------------------------------------------
	// Xac dinh khi nao thi het game (tat ca cac o deu duoc dien.)
	protected boolean isFinish()
	{
		int[][] c = new int[81][2];
		calculateUsedTilesIndex(c);
		if (usedNum == 81)
		{
			stopwatch.stop();
			return true;
		}
		return false;
	}
	
	protected void callFinishScreen()
	{
		cont = false;
		stopwatch.stop();
		confirmExit();
	}
	
	protected void finishGame()
	{
		cont = false;
		stopwatch.stop();
		finish();
	}
	
	protected void onResume()
    {
		super.onResume();
		Log.d(TAG, "onResume");
		Music.play(this, R.raw.game);
		atTime = 0;
		if (bundle != null)
		{
			atTime = bundle.getLong(keyTime);
		}
		stopwatch.startAt(atTime);
	}
    
    protected void onPause()
    {
    	super.onPause();
    	Log.d(TAG, "onPause");
    	Music.stop(this);
    	stopwatch.stop();
    	getPreferences(MODE_PRIVATE).edit().putString(key, toPuzzleString(puzzle)).commit();
    	getPreferences(MODE_PRIVATE).edit().putLong(keyTime, stopwatch.getElapsedTimeSecs()).commit();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
    	stopwatch.stop();
    	outState.putString(key, toPuzzleString(puzzle));
    	outState.putLong(keyTime, stopwatch.getElapsedTime());
    	super.onSaveInstanceState(outState);
    }
    
    // March 28th: Congratulation skill
  //Confirm exit
    private void confirmExit()
    {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage(R.string.congratscreen_title)
    		   .setCancelable(false)
    		   .setPositiveButton(R.string.menu_label, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int id) {
					// TODO Auto-generated method stub
					Game.this.finish();
				}
			})
			   .setNegativeButton(R.string.replay_label, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int id) {
					// TODO Auto-generated method stub
					openNewGameDialog();
					dialog.cancel();
					
				}
			});
    	builder.create();
    	builder.show();
    }
    
    private void openNewGameDialog()
    {
    	new AlertDialog.Builder(this)
    			.setTitle(R.string.new_game_title)
    			.setItems(R.array.difficulty,
    					new DialogInterface.OnClickListener()
    						{
    							public void onClick(DialogInterface dialoginterface, int i)
    							{
    								Game.this.finishGame();
    								startGame(i);
    								
    							}
    						})
    			.show();
    }
    
    private void startGame(int i)
    {
    	Log.d(TAG, "clicked on " + i);
    	//Start game here...
    	Intent intent = new Intent(Game.this,Game.class);
    	intent.putExtra(Game.KEY_DIFFICULTY, i);
    	startActivity(intent);
    }
    //-----------------------------------------------------------------------
    public String getElapsedTime() 
    {
    	long temp = stopwatch.getElapsedTimeSecs();
    	int hour = (int) Math.floor(temp / 3600);
    	
    	int minute = (int) Math.floor((temp - (3600 * hour))/60);
    	
    	int second = (int) (temp - minute * 60 - hour * 3600); 
        return hour + ":" + minute + ":" + second;
    }
    
}
