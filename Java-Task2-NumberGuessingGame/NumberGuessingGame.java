
import java.util.Random;
import java.util.Scanner;

public class NumberGuessingGame {

    static Scanner sc = new Scanner(System.in);
    static Random random = new Random();

    static int round = 1;
    static int totalWins = 0;
    static int totalLosses = 0;

    public static void main(String[] args) {

        System.out.println("================================");
        System.out.println("     NUMBER GUESSING GAME");
        System.out.println("================================");

        boolean playAgain = true;

        while (playAgain) {

            System.out.println("\nChoose Difficulty:");
            System.out.println("1. Easy   (1-50, 10 attempts)");
            System.out.println("2. Medium (1-100, 7 attempts)");
            System.out.println("3. Hard   (1-200, 5 attempts)");

            int choice = readChoice();

            int maxNumber;
            int maxAttempts;

            switch (choice) {
                case 1:
                    maxNumber = 50;
                    maxAttempts = 10;
                    break;
                case 2:
                    maxNumber = 100;
                    maxAttempts = 7;
                    break;
                default:
                    maxNumber = 200;
                    maxAttempts = 5;
            }

            int secretNumber = random.nextInt(maxNumber) + 1;
            int attempts = 0;
            boolean guessed = false;

            System.out.println("\nRound " + round);
            System.out.println("Guess a number between 1 and " + maxNumber);
            System.out.println("Maximum attempts: " + maxAttempts);

            while (attempts < maxAttempts) {

                System.out.print("\nEnter your guess: ");

                int guess = readGuess();

                if (guess < 1 || guess > maxNumber) {
                    System.out.println("Enter a number between 1 and " + maxNumber);
                    continue;
                }

                attempts++;

                System.out.println("Attempts used: " + attempts
                        + "/" + maxAttempts);

                if (guess > secretNumber) {
                    System.out.println("Too High!");
                } else if (guess < secretNumber) {
                    System.out.println("Too Low!");
                } else {
                    System.out.println("Correct! You guessed the number!");
                    guessed = true;
                    totalWins++;
                    break;
                }
            }

            if (!guessed) {
                System.out.println("\nYou Lost!");
                System.out.println("The correct number was: " + secretNumber);
                totalLosses++;
            }

            System.out.println("\n---------- ROUND SUMMARY ----------");
            if (guessed) {
                System.out.println("Round " + round
                        + " - guessed in " + attempts + " attempts");
            } else {
                System.out.println("Round " + round
                        + " - failed after " + attempts + " attempts");
            }

            System.out.println("Total Wins: " + totalWins);
            System.out.println("Total Losses: " + totalLosses);

            System.out.print("\nDo you want to play again? (yes/no): ");
            String answer = sc.next().trim();

            playAgain = answer.equalsIgnoreCase("yes");

            if (playAgain) {
                round++;
            }
        }

        System.out.println("\n================================");
        System.out.println("       THANK YOU FOR PLAYING!");
        System.out.println("================================");
        System.out.println("Total Rounds: " + (totalWins + totalLosses));
        System.out.println("Total Wins: " + totalWins);
        System.out.println("Total Losses: " + totalLosses);

        sc.close();
    }

    static int readChoice() {
        while (true) {
            System.out.print("Enter your choice (1-3): ");

            if (sc.hasNextInt()) {
                int choice = sc.nextInt();

                if (choice >= 1 && choice <= 3) {
                    return choice;
                }
            } else {
                sc.next();
            }

            System.out.println("Invalid choice! Please enter 1, 2, or 3.");
        }
    }

    static int readGuess() {
        while (true) {
            if (sc.hasNextInt()) {
                return sc.nextInt();
            }

            System.out.print("Invalid input! Enter a valid number: ");
            sc.next();
        }
    }
}
