package banking.repository;

import banking.Accounts;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class InMemoryDatabase implements AccountsRepository {
    private static final Logger log = Logger.getLogger(InMemoryDatabase.class.getName());
    private static long lastAccountNumber = 1;
    private final List<Accounts> accounts;

    public InMemoryDatabase(List<Accounts> accounts) {
        this.accounts = accounts;
        log.warning("Used InMemory Database. The data will not be saved.");
    }

    @Override
    public Optional<Accounts> createAccount() {
        Accounts account = new Accounts(lastAccountNumber++);
        accounts.add(account);
        return Optional.of(account);
    }

    @Override
    public Optional<Accounts> findAccount(String creditCardNumber, String pinNumber) {
        log.info(() -> "Log in account #" + creditCardNumber);
        return findAccount(creditCardNumber).filter(account -> account.getPinNumber().equals(pinNumber));
    }

    @Override
    public Optional<Accounts> findAccount(String creditCardNumber) {
        log.info(() -> "Searching for account #" + creditCardNumber);
        return accounts.stream()
                .filter(account -> account.getCardNumber().equals(creditCardNumber))
                .findFirst();
    }

    @Override
    public Optional<Accounts> updateAccount(Accounts account) {
        return Optional.ofNullable(account);
    }

    @Override
    public boolean deleteAccount(Accounts account) {
        return accounts.remove(account);
    }

}