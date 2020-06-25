package com.vandenberg.johnsfirstproject;

public class Shark {

	public static void main(String[] args) {
		System.out.println("Hello Johnny Shark!");
		System.out.println("Sharks are awesome!  They eat your face!");

		long num1 = 59569;
		long num2 = 6000000069l;
		long sum = num1 + num2;
		System.out.println("The sum of the numbers is: " + sum);

		long halfSum = sum / 2;
		System.out.println("Half of the sum is: " + halfSum);

		long newSum = sum + halfSum;
		System.out.println("The new sum is: " + newSum);

		long lastSum = newSum + sum + halfSum;
		System.out.println("The last sum is: " + lastSum);
	}

}
