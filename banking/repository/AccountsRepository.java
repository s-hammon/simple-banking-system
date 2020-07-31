package banking.repository;

import banking.Accounts;

import java.util.Optional;

public interface AccountsRepository {
    Optional<Accounts> createAccount();

    Optional<Accounts> findAccount(String cardNumber, String pinNumber);

    Optional<Accounts> findAccount(String cardNumber);

    Optional<Accounts> updateAccount(Accounts account);

    boolean deleteAccount(Accounts accounts);
}
