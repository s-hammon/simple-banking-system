package banking;

import banking.repository.AccountsRepository;

import java.util.Scanner;
import java.util.logging.Logger;

public class Controller implements Runnable {
    private final CustomerView view = new CustomerView();
    private final AccountsRepository repository;
    private final Scanner sc = new Scanner(System.in);

    private static final Logger log = Logger.getLogger(Controller.class.getName());

    public Controller(AccountsRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run() {
        String command;
        do {
            view.mainMenu();
            command =sc.nextLine();
            switch (command) {
                case "1":
                    createAccount();
                    break;
                case "2":
                    loginAccount();
                    break;
                case "0":
                    view.message("\nBye!");
                    break;
                default:
                    view.message("Invalid command!");
            }
        } while (!"0".equals(command));

        sc.close();
    }

    private void loginMenu(Accounts account) {
        log.info("User successfully logged in.");
        view.message("You have successfully logged in!");
        
        while (true) {
            view.accountMenu();
            String command = sc.nextLine();
            switch (command) {
                case "1":
                    view.message("\nBalance: " + account.getBalance());
                    break;
                case "2":
                    view.message("\nEnter income:");
                    account.addIncome(Long.parseLong(sc.nextLine()));
                    repository.updateAccount(account)
                            .ifPresentOrElse(
                                    a -> view.message("Income was added!"),
                                    () -> view.message("Account was not updated.")
                            );
                    break;
                case "3":
                    transfer(account);
                    break;
                case "4":
                    if (repository.deleteAccount(account)) {
                        view.message("The account has been closed!");
                        return;
                    }
                    break;
                case "5":
                    view.message("You have successfully logged out!\n");
                    return;
                case "0":
                    view.message("\nBye!");
                    System.exit(0);
                    break;
                default:
                    view.message("Invalid command!");
            }
        }
    }

    private void wrongAccount() {
        log.warning("Wrong card number or PIN!");
        view.message("Wrong card number or PIN!");
    }

    private void loginAccount() {
        log.info("Log into account");

        view.message("\nEnter your card number:");
        String cardInput = sc.nextLine();
        view.message("Enter your PIN:");
        String pinInput = sc.nextLine();

        repository.findAccount(cardInput, pinInput)
                .ifPresentOrElse(this::loginMenu, this::wrongAccount);
    }

    private void createAccount() {
        log.info("1. Create an account");
        repository.createAccount()
                .ifPresentOrElse(this::printCardInfo,
                        () -> view.message("Couldn't create an account."));
    }

    private void printCardInfo(Accounts card) {
        view.message("Your card has been created\n" +
                "Your card number:\n" + card.getCardNumber() +
                "\nYour card PIN:\n" + card.getPinNumber());
    }

    private void transfer(Accounts account) {
        view.message("Transfer");
        view.message("Enter card number:");

        var cardInput = sc.nextLine();
        if (!LuhnAlgorithm.isCorrectNumber(cardInput)) {
            view.message("Wrong card number. Please try again.");
            return;
        }

        var recipient = repository.findAccount(cardInput);
        if (recipient.isEmpty()) {
            view.message("Card does not exist.");
            return;
        }

        view.message("Enter the amount of money you want to transfer:");
        long money = Long.parseLong(sc.nextLine());
        if (money > account.getBalance()) {
            view.message("Not enough money!");
            return;
        }

        account.addIncome(-money);
        recipient.get().addIncome(money);
        repository.updateAccount(account);
        repository.updateAccount(recipient.get());
        view.message("Success!");
    }
}
