package org.example.sudoku;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

public class FinishScreenNewVer extends Activity
{
	@Override
    protected void onCreate(Bundle icicle) {
        // Be sure to call the super class.
        super.onCreate(icicle);

        // Have the system blur any windows behind this one.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        
        // See assets/res/any/layout/translucent_background.xml for this
        // view layout definition, which is being set here as
        // the content of our screen.
        setContentView(R.layout.finish_screen);
    }
}
