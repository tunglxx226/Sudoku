package org.example.sudoku;

public class StoryProfile 
{
	private int level;
	private String introVideoPath;
	public StoryProfile(int newLevel, String newIntroVideoPath)
	{
		level = newLevel;
		introVideoPath = newIntroVideoPath;
	}
	public int getLevel()
	{
		return level;
	}
	
	public String getIntro()
	{
		return introVideoPath;
	}
	
	public void setLevel(int newLevel)
	{
		level = newLevel;
	}
	
	public void setIntro(String videoPath)
	{
		introVideoPath = videoPath;
	}

}
