package org.example.sudoku;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class PuzzleView extends View {
	private static final String TAG = "Sudoku";
	private final Game game;
	private static final String SELX = "selX";
	private static final String SELY = "selY";
	private static final String VIEW_STATE = "viewState";
	//private static final int ID = 42;

	public PuzzleView(Context context) {
		super(context);
		this.game = (Game) context;
		setFocusable(true);
		setFocusableInTouchMode(true);
		//setId(ID);
		
	}

	public PuzzleView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		setFocusable(true);
		setFocusableInTouchMode(true);
		
	}

	public PuzzleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.game = (Game) context;
		setFocusable(true);
		setFocusableInTouchMode(true);
		//setId(ID);
		
		// real work here
	}

	// ...

	private float width; // width of one tile
	private float height; // height of one tile
	private int selX; // X index of selection
	private int selY; // Y index of selection

	private final Rect selRect = new Rect();

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		width = w / 9f;
		height = h / 9f;
		getRect(selX, selY, selRect);
		Log.d(TAG, "onSizeChanged: width " + width + ", height " + height);
		super.onSizeChanged(w, h, oldw, oldh);
	}

	private void getRect(int x, int y, Rect rect) {
		rect.set((int) (x * width), (int) (y * height),
				(int) (x * width + width), (int) (y * height + height));
	}

	@Override
	protected void onDraw(Canvas canvas) {

		// Draw the numbers
		// Define color and style for numbers
		Paint foreground = new Paint(Paint.ANTI_ALIAS_FLAG);
		foreground.setColor(getResources().getColor(R.color.puzzle_foreground));
		foreground.setStyle(Style.FILL);
		foreground.setTextSize(height * 0.75f);
		foreground.setTextScaleX(width / height * 1.5f);
		foreground.setTextAlign(Paint.Align.CENTER);
		// Draw the numbers in the center of the title
		FontMetrics fm = foreground.getFontMetrics();
		// Centering in X: use alignment (and X at midpoint)
		float x = width / 2f;
		// centering in Y: measure ascent/descent first
		float y = height / 2f - (fm.ascent + fm.descent) / 2f;
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				canvas.drawText(this.game.getTileString(i, j), i * width + x, j
						* height + y, foreground);
			}
		}
		
		// Draw the selection
		Log.d(TAG, "selRect=" + selRect);
		Paint selected = new Paint();
		selected.setColor(getResources().getColor(R.color.puzzle_selected));
		canvas.drawRect(selRect, selected); 
	}

	@Override
	// Handling input
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d(TAG, "onKeyDown: keycode=" + keyCode + ", event=" + event);
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_UP:
			select(selX, selY - 1);
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			select(selX, selY + 1);
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			select(selX - 1, selY);
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			select(selX + 1, selY);
			break;
		
		default:
			return super.onKeyDown(keyCode, event);
		}
		return true;
	}

	private void select(int x, int y) {
		invalidate(selRect);
		selX = Math.min(Math.max(x, 0), 8);
		selY = Math.min(Math.max(y, 0), 8);
		getRect(selX, selY, selRect);
		invalidate(selRect);
	}

	// Get the coordinate of the selected tile
	private int[] selectCordinate(int x, int y) {
		invalidate(selRect);
		int[] cordinate = new int[2];
		selX = Math.min(Math.max(x, 0), 8);
		selY = Math.min(Math.max(y, 0), 8);

		cordinate[0] = selX;
		cordinate[1] = selY;

		getRect(selX, selY, selRect);
		invalidate(selRect);
		return cordinate;
	}

	// Handle input in touch mode
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() != MotionEvent.ACTION_DOWN) {
			return super.onTouchEvent(event);
		}

		// Allow player to choose the tile that defined by game
		// But only allow the user to modify the tiles that are blank
		int[] selected = new int[2];
		selected = selectCordinate((int) (event.getX() / width),
				(int) (event.getY() / height));

		int[] predefined = new int[81];
		// Get the tile that are not blank (predefined by game)
		predefined = game.getPredefinedTileFromPuzzle();
		// Check if the selected tile is whether predefined or not
		if (predefined[selected[1] * 9 + selected[0]] == 1) {
			return true;
		}
		game.showKeypadOrError(selX, selY);
		return true;
	}

	public void setSelectedTile(int X, int Y, int tile) {
		game.setTile(X, Y, tile);
		invalidate();
		// any further code handling invalid input number goes here
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		Parcelable p = super.onSaveInstanceState();
		Log.d(TAG, "onSaveInstanceState");
		Bundle bundle = new Bundle();
		bundle.putInt(SELX, selX);
		bundle.putInt(SELY, selY);
		bundle.putParcelable(VIEW_STATE, p);
		return bundle;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		Log.d(TAG, "onRestoreInstanceState");
		Bundle bundle = (Bundle) state;
		select(bundle.getInt(SELX), bundle.getInt(SELY));
		super.onRestoreInstanceState(bundle.getParcelable(VIEW_STATE));
		return;
	}

}