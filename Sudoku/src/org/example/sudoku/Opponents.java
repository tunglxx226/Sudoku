package org.example.sudoku;

public class Opponents 
{
	 private Skills[][] skills = new Skills[3][3];
	 private String name;
	 private int ID;
	 
	 public Opponents(int ID, String[] skillsName)
	 {
		 switch(ID)
		 {
		 case 0:
			 name = "Hulijing";
			 break;
		 case 1:
			 name = "Au Co";
			 break;
		 }
		 for (int i = 0; i < 3; i++)
		 {
			 this.skills[ID][i] = new Skills(skillsName[i]);
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
}
