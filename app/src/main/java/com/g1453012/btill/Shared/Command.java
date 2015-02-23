package btill.terminal.bluetooth;

public enum Command {
    REQUEST_MENU,
    MAKE_ORDER,
    SETTLE_BILL;

    public static Command toCommand(String value) {
        if (value == null) {
            throw new InvalidCommand(value);
        }
        return Command.valueOf(value.toUpperCase());
    }

    static class InvalidCommand extends RuntimeException {
        public InvalidCommand(String value) {
            super(value + " is an invalid command.");
        }
    }
}
