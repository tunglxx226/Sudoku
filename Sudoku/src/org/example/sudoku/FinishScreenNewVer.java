package org.example.sudoku;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class FinishScreenNewVer extends Activity implements OnClickListener
{
	 @Override
		protected void onCreate(Bundle savedInstanceState) {
	        // Be sure to call the super class.
	        super.onCreate(savedInstanceState);
	        
	        // See assets/res/any/layout/translucent_background.xml for this
	        // view layout definition, which is being set here as
	        // the content of our screen.
	        setContentView(R.layout.finish_screen);
	        View menuButton = findViewById(R.id.menu_button);
	        menuButton.setOnClickListener(this);
	        View replayButton = findViewById(R.id.replay_button);
	        replayButton.setOnClickListener(this);
	    }

	 public void onClick(View v) 
	    {
	    	switch (v.getId())
	    	{
	    		case R.id.menu_button:
	    			Intent intent = new Intent(FinishScreenNewVer.this, Sudoku.class);
	    			startActivity(intent);
	    			break;
		    	case R.id.replay_button:
		    		openNewGameDialog();
		    		break;
		    	// More buttons go here (if any) ...
	    	}
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
	    								startGame(i);
	    							}
	    						})
	    			.show();
	    }
	    
	    private void startGame(int i)
	    {
	    	//Start game here...
	    	Intent intent = new Intent(this,Game.class);
	    	intent.putExtra(Game.KEY_DIFFICULTY, i);
	    	startActivity(intent);
	    	finish();
	    }
		
		protected void onResume()
	    {
	    	super.onResume();
	    	Music.play(this, R.raw.congrat);
	    }
	    
	    protected void onPause()
	    {
	    	super.onPause();
	    	Music.stop(this);
	    }
}
