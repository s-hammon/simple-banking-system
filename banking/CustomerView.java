package banking;

public class CustomerView {

    public void mainMenu() {
        System.out.print(
                "1. Create an account\n" +
                "2. Log into account\n" +
                "0. Exit\n");
    }

    public void message(String message) { System.out.println(message); }

    public void accountMenu() {
        System.out.println();
        System.out.print(
                "1. Balance\n" +
                "2. Log out\n" +
                "0. Exit\n");
    }
}
