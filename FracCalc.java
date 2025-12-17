// Ronit Agarwal
// Period 6
// Mrs. Abuja
// Fraction Calculator Project

import java.util.*;

// This program takes in two fractions and an operator, then performs the
// appropriate calculation (addition, subtraction, multiplication, or division).
// It simplifies the resulting fraction and outputs the final reduced form back to the user.
public class FracCalc {

   // It is best if we have only one console object for input
   public static Scanner console = new Scanner(System.in);
   
   // This main method will loop through user input and then call the
   // correct method to execute the user's request for help, test, or
   // the mathematical operation on fractions. or, quit.
   // DO NOT CHANGE THIS METHOD!!
   public static void main(String[] args) {
   
      // initialize to false so that we start our loop
      boolean done = false;
      
      // When the user types in "quit", we are done.
      while (!done) {
         // prompt the user for input
         String input = getInput();
         
         // special case the "quit" command
         if (input.equalsIgnoreCase("quit")) {
            done = true;
         } else if (!UnitTestRunner.processCommand(input, FracCalc::processCommand)) {
        	   // We allowed the UnitTestRunner to handle the command first.
            // If the UnitTestRunner didn't handled the command, process normally.
            String result = processCommand(input);
            
            // print the result of processing the command
            System.out.println(result);
         }
      }
      
      System.out.println("Goodbye!");
      console.close();
   }

   // Prompt the user with a simple, "Enter: " and get the line of input.
   // Return the full line that the user typed in.
   public static String getInput() {

      System.out.print("Enter: ");
      return console.nextLine();

   }
   
   // processCommand will process every user command except for "quit".
   // It will return the String that should be printed to the console.
   // This method won't print anything.
   // DO NOT CHANGE THIS METHOD!!!
   public static String processCommand(String input) {

      if (input.equalsIgnoreCase("help")) {
         return provideHelp();
      }
      
      // if the command is not "help", it should be an expression.
      // Of course, this is only if the user is being nice.
      return processExpression(input);
   }
   
   // Lots work for this project is handled in here.
   // Of course, this method will call LOTS of helper methods
   // so that this method can be shorter.
   // This will calculate the expression and RETURN the answer.
   // This will NOT print anything!
   // Input: an expression to be evaluated
   //    Examples: 
   //        1/2 + 1/2
   //        2_1/4 - 0_1/8
   //        1_1/8 * 2
   // Return: the fully reduced mathematical result of the expression
   //    Value is returned as a String. Results using above examples:
   //        1
   //        2 1/8
   //        2 1/4
   public static String processExpression(String input) {

      //Find where first space is in input
      int firstSpace = input.indexOf(" ");
      //Initialize second space variable
      int secondSpace = -1;
      
      //Using first space, find where the second space is now
      for (int i = firstSpace + 1; i < input.length(); i++) {
        if (input.charAt(i) == ' ') {
            secondSpace = i;
            break;
         }
      }

      //Find the operator and the second number and the first number
      String op = extractOperator(input, firstSpace, secondSpace);
      String second = extractSecondNumber(input, secondSpace);
      String first = input.substring(0, firstSpace);

      //Parse the first and second numbers into whole, numerator, denominator
      int whole1 = parseWhole(first);
      int num1 = parseNumerator(first);
      int den1 = parseDenominator(first);

      int whole2 = parseWhole(second);
      int num2 = parseNumerator(second);
      int den2 = parseDenominator(second); 

      //Divison by 0 warning (when either denominator is 0)
      if (den1 == 0 || den2 == 0){
         return "Error: Division by zero";
      }

      //Convert both numbers to improper fractions
      num1 = convertToImproper(whole1, num1, den1);
      num2 = convertToImproper(whole2, num2, den2);

      //Divison by 0 warning (When user is trying to divide a fraction and the 2nd numerator is equal to 0, 
      //meaning reciprocal has 0 in the denominator)
      if (op.equals("/") && num2 == 0) {
         return "Error: Division by zero";
      }

      //Declare Variables to track numerator and denominator
      int resultNum = 0;
      int resultDen = 1;

      //Perform the operation based on the operator
      if (op.equals("+")){
         //Addition
         resultNum = num1 * den2 + num2 * den1;
         resultDen = den1 * den2;
      }
      else if (op.equals("-")){
         //Subtraction
         resultNum = num1 * den2 - num2 * den1;
         resultDen = den1 * den2;
      }
      else if (op.equals("*")){
         //Multiplication
         resultNum = num1 * num2;
         resultDen = den1 * den2;
      }
      else if (op.equals("/")){
         //Division (multiply by reciprocal)
         resultNum = num1 * den2;
         resultDen = den1 * num2;
      }

      //Finds what factor to reduce the answer by and reduces it
      int gcdValue = gcd(resultNum, resultDen);
      resultNum /= gcdValue;
      resultDen /= gcdValue;

      //Noramlizes the fraction so denominator is always positive
      if (resultDen < 0) {
         resultDen = -resultDen;
         resultNum = -resultNum;
      }

      //Formats and returns the result
      return formatResult(resultNum, resultDen);
   }

   //Parse whole number from input String
   //input - full operand String
   //Return whole number as an integer
   public static int parseWhole(String input) {
      //Call Mixed Number function if Mixed Number
      if (input.contains("_")) {
         return extractWholeFromMixed(input);
      //Call Regular Fraction function if regular fraction
      } else if (input.contains("/")) {
         return 0;
      //Call Whole Number function if whole number
      } else {
         return extractWholeFromNumber(input);
      }
   }

   //Parse numerator from input String
   //input - full operand String
   //Return numerator as an integer
   public static int parseNumerator(String input) {
      //Call Mixed Number function if Mixed Number
      if (input.contains("_")) {
         return extractNumeratorFromMixed(input);
      //Call Regular Fraction function if regular fraction
      } else if (input.contains("/")) {
         return extractNumeratorFromFraction(input);
      } else {
         return 0;
      }
   }

   //Parse denominator from input String
   //input - full operand String
   //Return denominator as an integer
   public static int parseDenominator(String input) {
      //Call Mixed Number function if Mixed Number
      if (input.contains("_")) {
         return extractDenominatorFromMixed(input);
      //Call Regular Fraction function if regular fraction
      } else if (input.contains("/")) {
         return extractDenominatorFromFraction(input);
      } else {
         return 1;
      }
   }

   //Convert mixed number to improper fraction
   //whole - whole number part of mixed number
   //num - numerator of mixed number
   //den - denominator of mixed number
   //Return numerator of improper fraction
   public static int convertToImproper(int whole, int num, int den) {
      //Calculate improper fraction numerator based on whole number sign
      if (whole < 0) {
         return whole * den - num;
      } else {
         return whole * den + num;
      }
   }  

   //Format the result into a String
   //num - numerator of result
   //den - denominator of result
   //Return formatted result as a String
   public static String formatResult(int num, int den) {

      //If numerator is 0, return 0
      if (num == 0){
         return "0";
      }

      //Calculate whole number and remainder
      int whole = num / den;
      int remainder = num % den;

      //Make remainder positive
      if (remainder < 0) {
         remainder = -remainder;
      }

      //Format result based on whole number and remainder
      if (remainder == 0) {
         return "" + whole;
      }

      //If whole number is 0, only return fraction part
      if (whole == 0) {
         if (num < 0) {
            return "-" + remainder + "/" + den;
         } else {
            return remainder + "/" + den;
         }
      }

      //Return mixed number format
      return whole + " " + remainder + "/" + den;
   }

   //Extract operator from String
   //input - full expression String
   //firstSpace - index of first Space Character
   //secondSpace - index of second Space Character
   //Return operator as a String
   public static String extractOperator(String input, int firstSpace, int secondSpace){
      //Extract operator using substring between first and second space
      return input.substring(firstSpace + 1, secondSpace);
   }

   //Extract second operand from String
   //input - full expression String
   //secondSpace - index of second Space Character
   //Return second operand as a String
   public static String extractSecondNumber(String input, int secondSpace){
      //Extract second number using substring after second space
      return input.substring(secondSpace + 1);
   }

   //Extract whole number from mixed number String
   //second - second operand String in mixed number format
   //Return whole number part as an integer
   public static int extractWholeFromMixed(String second){
      //Find underscore index and extract whole number part
      int underscore = second.indexOf("_");
      return Integer.parseInt(second.substring(0, underscore));
   }

   //Extract numerator from mixed number String
   //second - second operand String in mixed number format
   //Return numerator part as an integer
   public static int extractNumeratorFromMixed(String second){
      //Find underscore and slash index and extract numerator part
      int underscore = second.indexOf("_");
      int slash = second.indexOf("/");
      return Integer.parseInt(second.substring(underscore + 1, slash));
   }

   //Extract denominator from mixed number String
   //second - second operand String in mixed number format
   //Return denominator part as an integer
   public static int extractDenominatorFromMixed(String second){
      //Find slash index and extract denominator part
      int slash = second.indexOf("/");
      return Integer.parseInt(second.substring(slash + 1));
   }

   //Extract numerator from simple fraction String
   //second - second operand String in fraction format
   //Return numerator as an integer
   public static int extractNumeratorFromFraction(String second){
      //Find slash index and extract numerator part
      int slash = second.indexOf("/");
      return Integer.parseInt(second.substring(0, slash));
   }

   //Extract denominator from simple fraction String
   //second - second operand String in fraction format
   //Return denominator as an integer
   public static int extractDenominatorFromFraction(String second){
      int slash = second.indexOf("/");
      return Integer.parseInt(second.substring(slash + 1));
   }

   //Extract whole number from whole number String
   //second - second operand String as a whole number
   //Return whole number as an integer
   public static int extractWholeFromNumber(String second){
      //Parse whole number String to integer
      return Integer.parseInt(second);
   }

   //Calculate the greatest common divisor of two integers
   //using the Euclidean algorithm https://www.geeksforgeeks.org/dsa/euclidean-algorithms-basic-and-extended/
   //a - first integer
   //b - second integer
   //Return greatest common divisor of a and b
   public static int gcd(int a, int b) {
      if (b == 0) {
         return Math.abs(a);
      }
      return gcd(b, a % b);
   }
   
   // Returns a string that is helpful to the user about how
   // to use the program. These are instructions to the user.
   public static String provideHelp() {
      // Prints out help text for the user
      String help = "\nFraction Calculator Help:\n";
      help += "Enter mathematical expressions with two numbers and an operator (+, -, *, /)\n";
      help += "Make sure to put underscores between whole numbers and fractions for mixed numbers.\n";
      help += "Examples: 1/2 + 1/4, 3 * 2_1/2, 4/5 - 1/3\n";
      help += "Commands: 'help' - show this help, 'quit' - exit program";
      return help;
   }
}

