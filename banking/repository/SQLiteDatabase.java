package banking.repository;

import banking.Accounts;

import java.sql.*;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLiteDatabase implements AccountsRepository {
    private static final Logger log = Logger.getLogger(SQLiteDatabase.class.getName());
    private static final String SQL_ADD_ACCOUNT = "INSERT INTO card (number, pin) VALUES (?, ?)";
    private static final String SQL_FIND_ACCOUNT = "SELECT number, pin, balance FROM card WHERE number = ?";
    private static final String SQL_UPDATE_ACCOUNT = "UPDATE card SET balance = ? WHERE number = ?";
    private static final String SQL_DELETE_ACCOUNT = "DELETE FROM card WHERE number = ?";

    private final String databaseName;
    private final String url;

    public SQLiteDatabase(final String databaseName) {
        this.databaseName = databaseName;
        url = "jdbc:sqlite:C:" + databaseName;
        log.info("Used SQLite3 Database with url=" + url);
        createNewTable(url);
    }

    public static void createNewTable(String url) {

        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS card (\n"
                + "	id INTEGER PRIMARY KEY,\n"
                + "	number TEXT NOT NULL UNIQUE CHECK ( length(number) = 16),\n"
                + "	pin TEXT NOT NULL CHECK ( length(pin) = 4 ),\n"
                + " balance INTEGER NOT NULL DEFAULT 0 CHECK ( balance >= 0 )"
                + ");";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Optional<Accounts> createAccount() {
        final var account = new Accounts(generateAccountId());
        log.info(() -> "Create Account #" + account.getCardNumber());

        try (final var connection = DriverManager.getConnection(url);
             final var sql = connection.prepareStatement(SQL_ADD_ACCOUNT)) {

            sql.setString(1, account.getCardNumber());
            sql.setString(2, account.getPinNumber());
            sql.executeUpdate();

            log.info(() -> String.format("Saved to database: Card: %s Pin: %s Balance: %d",
                    account.getCardNumber(), account.getPinNumber(), account.getBalance()));

            return Optional.of(account);
        } catch (SQLException e) {
            log.log(Level.WARNING, "Can't add account to " + databaseName, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Accounts> findAccount(final String creditCardNumber, final String pinNumber) {
        log.info(() -> "Log in account #" + creditCardNumber);
        return findAccount(creditCardNumber).filter(a -> a.getPinNumber().equals(pinNumber));
    }

    @Override
    public Optional<Accounts> findAccount(String creditCardNumber) {
        log.info(() -> "Searching for account #" + creditCardNumber);

        try (var connection = DriverManager.getConnection(url);
             var sql = connection.prepareStatement(SQL_FIND_ACCOUNT)) {
            sql.setString(1, creditCardNumber);
            var resultSet = sql.executeQuery();
            if (!resultSet.next()) {
                return Optional.empty();          }
            var bankAccount = Accounts.builder()
                    .setCard(resultSet.getString("number"))
                    .setPin(resultSet.getString("pin"))
                    .setBalance(resultSet.getInt("balance"))
                    .build();
            return Optional.of(bankAccount);
        } catch (SQLException e) {
            log.log(Level.WARNING, "Can't connect to " + databaseName, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Accounts> updateAccount(Accounts account) {
        log.info(() -> "Update Account #" + account.getCardNumber());

        try (var connection = DriverManager.getConnection(url);
             var sql = connection.prepareStatement(SQL_UPDATE_ACCOUNT)) {

            sql.setLong(1, account.getBalance());
            sql.setString(2, account.getCardNumber());
            sql.executeUpdate();

            log.info(() -> String.format("Updated card# %s with balance: %d",
                    account.getCardNumber(), account.getBalance()));

            return Optional.of(account);
        } catch (SQLException e) {
            log.log(Level.WARNING, "Can't add account to " + databaseName, e);
        }
        return Optional.empty();

    }

    @Override
    public boolean deleteAccount(Accounts account) {
        log.info(() -> "Delete Account #" + account.getCardNumber());

        try (var connection = DriverManager.getConnection(url);
             var sql = connection.prepareStatement(SQL_DELETE_ACCOUNT)) {

            sql.setString(1, account.getCardNumber());
            sql.executeUpdate();
            log.info(() -> "Deleted card# %s " + account.getCardNumber());
            return true;
        } catch (SQLException e) {
            log.log(Level.WARNING, "Can't add account to " + databaseName, e);
        }
        return false;
    }

    private static long generateAccountId() {
        return ThreadLocalRandom.current().nextLong(100000000L, 999999999L);
    }
}