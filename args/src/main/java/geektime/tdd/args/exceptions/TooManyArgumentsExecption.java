package geektime.tdd.args.exceptions;

public class TooManyArgumentsExecption extends RuntimeException{
    private String option = "";
    public TooManyArgumentsExecption(String value) {
        this.option = value;
    }

    public String getOption() {
        return this.option;
    }
}
