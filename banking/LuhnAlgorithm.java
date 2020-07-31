package banking;

public class LuhnAlgorithm {

    public static int getControlNumber(String number) {
        int sum = 0;
        for (int i = 0; i < number.length(); i++) {
            int digit = Character.getNumericValue(number.charAt(i));
            if (i % 2 ==0) digit *=2;
            if (digit > 9) digit -= 9;

            sum += digit;
        }

        return sum;
    }

    public static int getCheckSum(String number) {
        final int checkSum = 10 - getControlNumber(number) % 10;
        return checkSum % 10;
    }

    public static boolean isCorrectNumber(String cardNumber) {
        return getControlNumber(cardNumber) % 10 == 0;
    }
}
