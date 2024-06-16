package geektime.tdd.args.exceptions;

public class IllegalValueException extends RuntimeException {
    private String option = "";
    private String value = "";
    public IllegalValueException(String option, String value) {
        this.option = option;
        this.value = value;
    }

    public String getOption() {
        return this.option;
    }
    public String getValue() {
        return this.value;
    }
}
