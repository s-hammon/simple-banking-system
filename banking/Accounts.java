package banking;

import java.util.Random;

import static java.lang.String.format;

public class Accounts {
    private static final Random rand = new Random();

    private static final String BIN = "400000";

    private final String cardNumber;
    private String pinNumber;
    private long balance;

    public Accounts(long id) {
        var checksum = LuhnAlgorithm.getCheckSum(BIN + format("%09d", id));

        cardNumber = format("%s%09d%d", BIN, id, checksum);
        pinNumber = format("%04d", newPin());
        balance = 0;
    }

    public Accounts(String card, String pin, long balance) {
        this.cardNumber = card;
        this.pinNumber = pin;
        this.balance = balance;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getPinNumber() {
        return pinNumber;
    }

    public long getBalance() { return balance; }

    public void addIncome(long income) {
        balance += income;
    }

    public static AccountBuilderCard builder() {
        return card -> pin -> balance -> () -> new Accounts(card, pin, balance);
    }

    public interface AccountBuilderCard {
        AccountBuilderPin setCard(String cardNumber);
    }

    public interface AccountBuilderPin {
        AccountBuilderBalance setPin(String pin);
    }

    public interface AccountBuilderBalance {
        AccountBuilder setBalance(long balance);
    }

    public interface AccountBuilder {
        Accounts build();
    }

    private static int newPin() {
        return rand.nextInt(10000);
    }
}
