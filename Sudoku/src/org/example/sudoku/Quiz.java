package org.example.sudoku;

import java.io.InputStream;
import java.util.Random;

public class Quiz {
	public static final int DIFFICULTY_EASY = 0;
	public static final int DIFFICULTY_MEDIUM = 1;
	public static final int DIFFICULTY_HARD = 2;

	public static final int MISSING_NUMBER_EASY = 30;
	public static final int MISSING_NUMBER_MEDIUM = 45;
	public static final int MISSING_NUMBER_HARD = 60;

	private String defResult;
	private String quiz;
	private int missingNumber;

	InputStream is;

	public Quiz(String aQuiz) {
		defResult = aQuiz;
		quiz = aQuiz;

	}

	public void preparation(int difficulty) {
		switch (difficulty) {
		case DIFFICULTY_EASY:
			missingNumber = MISSING_NUMBER_EASY;
			break;
		case DIFFICULTY_MEDIUM:
			missingNumber = MISSING_NUMBER_MEDIUM;
			break;
		case DIFFICULTY_HARD:
			missingNumber = MISSING_NUMBER_HARD;
			break;
		default:
			break;
		}
		generate(missingNumber);

	}

	public void generate(int missingNumber) {
		int randomPosition[] = new int[missingNumber];
		Random rand = new Random();
		StringBuilder stringBuilder = new StringBuilder(quiz);

		for (int i = 0; i < missingNumber; i++) {
			//do {
				randomPosition[i] = rand.nextInt(81);
			//} while (checkDuplicate(randomPosition, i));
		}
		for (int i = 0; i < missingNumber; i++) {
			stringBuilder.setCharAt(randomPosition[i], '0');
		}
		quiz = stringBuilder.toString();
	}

	
	/*
	 * This causes the system to be freezed due to too many loop
	public boolean checkDuplicate(int randomPostion[], int position) {
		for (int i = 0; i < position; i++) {
			if (randomPostion[position] == randomPostion[i])
				return false;

		}
		return true;
	}*/

	public String getQuiz() {
		return quiz;
	}

	public String getDefResult() {
		return defResult;
	}

}
