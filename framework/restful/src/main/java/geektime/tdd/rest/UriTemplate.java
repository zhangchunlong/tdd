package geektime.tdd.rest;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

interface UriTemplate {
    //@Path("{id}") /1/orders
    interface MatchResult extends Comparable<MatchResult> {
        String getMatched(); // /1

        String getRemaining(); // /orders

        Map<String, String> getMatchedParameters(); // id -> 1
    }

    Optional<MatchResult> match(String path);
}

class UriTemplateString implements UriTemplate{
    private static Pattern variable = Pattern.compile("\\{\\w[\\w\\.-]*\\}");
    private Pattern pattern;

    public UriTemplateString(String template) {

        pattern = Pattern.compile("(" + variable(template) + ")" + "(/.*)?");

    }

    private String variable(String template) {
        return variable.matcher(template).replaceAll("([^/]+?)");
    }

    @Override
    public Optional<MatchResult> match(String path) {
        Matcher matcher = pattern.matcher(path);
        if(!matcher.matches()) return Optional.empty();
        int count = matcher.groupCount();

        return Optional.of(new MatchResult() {
            @Override
            public String getMatched() {
                return matcher.group(1);
            }

            @Override
            public String getRemaining() {
                return matcher.group(count);
            }

            @Override
            public Map<String, String> getMatchedParameters() {
                return null;
            }

            @Override
            public int compareTo(MatchResult o) {
                return 0;
            }
        });
    }
}