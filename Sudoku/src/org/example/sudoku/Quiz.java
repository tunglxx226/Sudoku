package org.example.sudoku;

import java.io.InputStream;
import java.util.Random;

import android.util.Log;

public class Quiz {
	public static final int DIFFICULTY_EASY = 0;
	public static final int DIFFICULTY_MEDIUM = 1;
	public static final int DIFFICULTY_HARD = 2;

	// public static final int MISSING_NUMBER_EASY = 50;
	// public static final int MISSING_NUMBER_MEDIUM = 60;
	// public static final int MISSING_NUMBER_HARD = 70;

	public static final int LEAP_EASY = 5;
	public static final int LEAP_MEDIUM = 4;
	public static final int LEAP_HARD = 3;

	private String defResult;
	private String quiz;
	//private int missingNumber;
	private int leap;

	InputStream is;

	public Quiz(String aQuiz) {
		defResult = aQuiz;
		quiz = aQuiz;

	}

	public void preparation(int difficulty) {
		switch (difficulty) {
		case DIFFICULTY_EASY:
			// missingNumber = MISSING_NUMBER_EASY;
			leap = LEAP_EASY;
			break;
		case DIFFICULTY_MEDIUM:
			// missingNumber = MISSING_NUMBER_MEDIUM;
			leap = LEAP_MEDIUM;
			break;
		case DIFFICULTY_HARD:
			// missingNumber = MISSING_NUMBER_HARD;
			leap = LEAP_HARD;
			break;
		default:
			break;
		}
		generate(leap);

	}

	public void generate(int leap) {
		// int randomPosition[] = new int[missingNumber];
		int randomPosition[] = new int[81];
		int missingNo = 0;
		int i = 1; // index
		int step = 0;

		Random rand = new Random();
		StringBuilder stringBuilder = new StringBuilder(quiz);

		randomPosition[0] = rand.nextInt(leap);

		missingNo++;

		while (randomPosition[i - 1] < 81) {
			step = rand.nextInt(leap);
			if (step == 0) {
				step = 1;
			}

			randomPosition[i] = randomPosition[i - 1] + step;
			if (randomPosition[i] >= 81) {
				randomPosition[i] = 80;
				break;
			}
			i++;
			missingNo++;
		}

		// }
		for (int j = 1; j < missingNo; j++) {
			stringBuilder.setCharAt(randomPosition[j], '0');
		}
		quiz = stringBuilder.toString();
	}

	/*
	 * This causes the system to be freezed due to too many loop public boolean
	 * checkDuplicate(int randomPostion[], int position) { for (int i = 0; i <
	 * position; i++) { if (randomPostion[position] == randomPostion[i]) return
	 * false;
	 * 
	 * } return true; }
	 */

	public String getQuiz() {
		return quiz;
	}

	public String getDefResult() {
		return defResult;
	}

}
