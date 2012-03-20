package org.example.sudoku;

import android.app.Activity;
import android.os.Bundle;

public class FinishScreenNewVer extends Activity
{
	 @Override
		protected void onCreate(Bundle savedInstanceState) {
	        // Be sure to call the super class.
	        super.onCreate(savedInstanceState);
	        
	        // See assets/res/any/layout/translucent_background.xml for this
	        // view layout definition, which is being set here as
	        // the content of our screen.
	        setContentView(R.layout.finish_screen);
	    }
}
