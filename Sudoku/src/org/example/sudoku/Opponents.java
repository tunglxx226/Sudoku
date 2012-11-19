package org.example.sudoku;

import android.util.Log;

public class Opponents 
{
	public static final String TAG = "OPPONENTS";
	// Skills' names
	//HULIJING:
	public static final String hulijing1 = "QUẪY ĐUÔI.";
	public static final String hulijing2 = "MUÔN HÌNH VẠN TRẠNG.";
	public static final String hulijing3 = "BÌNH ĐỊA.";
	public static final int[] RATE_HULIJING = {26, 11, 1};
	//--------------------------------------------------
	//AUCO:
	public static final String auco1 = "MẮT THẦN.";
	public static final String auco2 = "CÁNH ĐỒNG BẤT TẬN.";
	public static final String auco3 = "MẦM SỐNG.";
	public static final int[] RATE_AUCO = {30, 13, 3};
	//------------------------------------------------
	
	
	
	// Opponents' attributes
	 private Skills[][] skills = new Skills[10][3]; // 10 must be changed, since it's the number of levels
	 private String name;
	 private int ID;
	 private int[] skillRate;
	 
	 public Opponents(int ID)
	 {
		 String[] skillName = new String[3];
		 switch(ID)
		 {
		 case 0:
			 name = "Hulijing";
			 skillName[0] = hulijing1;
			 skillName[1] = hulijing2;
			 skillName[2] = hulijing3;
			 skillRate = RATE_HULIJING;
			 break;
		 case 1:
			 name = "Au Co";
			 skillName[0] = auco1;
			 skillName[1] = auco2;
			 skillName[2] = auco3;
			 skillRate = RATE_AUCO;
			 break;
		default:
			name = "nothing";
			 skillName[0] = "nothing1";
			 skillName[1] = "nothing2";
			 skillName[2] = "nothing3";
		 }
		 for (int i = 0; i < 3; i++)
		 {
			 this.skills[ID][i] = new Skills(skillName[i]);
			 Log.d(TAG, "i = " + Integer.toString(i));
		 }
		 this.ID = ID;
		 
	 }
	 
	 public String getName()
	 {
		 return name;
	 }
	 
	 public Skills getSkill(int index)
	 {
		 return skills[ID][index];
	 }
	 
	 public int[] getSkillRate()
	 {
		 return skillRate;
	 }
}
