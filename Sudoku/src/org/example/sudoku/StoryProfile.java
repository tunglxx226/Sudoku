package org.example.sudoku;

public class StoryProfile 
{
	private int level;
	private String introVideoPath[];
	
	public StoryProfile()
	{
		this(0);
	}
	public StoryProfile(int newLevel)
	{
		/*introVideoPath[0] = "android.resource://" + "org.example.sudoku" + "/" + R.raw.intro;
		introVideoPath[1] = "android.resource://" + "org.example.sudoku" + "/" + R.raw.intro;
		introVideoPath[2] = "android.resource://" + "org.example.sudoku" + "/" + R.raw.intro;
		introVideoPath[3] = "android.resource://" + "org.example.sudoku" + "/" + R.raw.intro;
		introVideoPath[4] = "android.resource://" + "org.example.sudoku" + "/" + R.raw.intro;*/
		level = newLevel;
	}
	public int getLevel()
	{
		return level;
	}
	
	public String getIntro()
	{
		return introVideoPath[level];
	}
	
	public void setLevel(int newLevel)
	{
		level = newLevel;
	}
	
	public void levelUp()
	{
		level += 1;		
	}

}
