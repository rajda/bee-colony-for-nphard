package com.github.rajda;

import java.util.Scanner;

public class test {

	private static final int[] numberCustomers = new int[8];
	private static int[] numberLinks = new int[8];
	private static final int iterationsNumber = 1000;
	private static final int optimizationCyclesNumber = 30;

//	 private static final int bandwith = 155;
//	 private static final int lowerLimit = 3;
//	 private static final int upperLimit = 7;

	private static final int bandwith = 622;
	private static final int lowerLimit = 11;
	private static final int upperLimit = 17;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
//		numberCustomers[0] = 15;
//		numberLinks[0] = 41;
//		numberCustomers[1] = 15;
//		numberLinks[1] = 60;
//		numberCustomers[2] = 25;
//		numberLinks[2] = 54;
//		numberCustomers[3] = 25;
//		numberLinks[3] = 61;
//		numberCustomers[4] = 30;
//		numberLinks[4] = 57;
//		numberCustomers[5] = 30;
//		numberLinks[5] = 76;
//		numberCustomers[6] = 50;
//		numberLinks[6] = 85;
//		numberCustomers[7] = 50;
//		numberLinks[7] = 98;

		numberCustomers[0] = 15;
		numberLinks[0] = 43;
		numberCustomers[1] = 15;
		numberLinks[1] = 76;
		numberCustomers[2] = 25;
		numberLinks[2] = 57;
		numberCustomers[3] = 25;
		numberLinks[3] = 86;
		numberCustomers[4] = 30;
		numberLinks[4] = 72;
		numberCustomers[5] = 30;
		numberLinks[5] = 78;
		numberCustomers[6] = 50;
		numberLinks[6] = 110;
		numberCustomers[7] = 50;
		numberLinks[7] = 143;
		
		int howManyDifferentParameters = numberCustomers.length;
		for(int i=0; i<howManyDifferentParameters; i++){
			
			if(numberCustomers[i]==0)
				continue;
			
			int maxNumberLinks = numberCustomers[i] * (numberCustomers[i] - 1) / 2;

			if (numberLinks[i] > maxNumberLinks || numberLinks[i] < 1) {
				Scanner in = new Scanner(System.in);
				while (numberLinks[i] > maxNumberLinks || numberLinks[i] < 1) {
					System.out.print("Podaj liczb� ��czy pomi�dzy u�ytkownikami (1<=x<=" + maxNumberLinks + "): ");
					numberLinks[i] = Integer.parseInt(in.nextLine());
				}
				in.close();
			}
			
		BeeAlgorithmInIDP idp = new BeeAlgorithmInIDP(numberCustomers[i], numberLinks[i], bandwith, lowerLimit, upperLimit, iterationsNumber, optimizationCyclesNumber);
		idp.mainSteps();
		}
	}
}
