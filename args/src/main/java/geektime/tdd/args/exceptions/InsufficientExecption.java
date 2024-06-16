package geektime.tdd.args.exceptions;

public class InsufficientExecption extends RuntimeException{
    private String option = "";
    public InsufficientExecption(String value) {
        this.option = value;
    }

    public String getOption() {
        return this.option;
    }
}
