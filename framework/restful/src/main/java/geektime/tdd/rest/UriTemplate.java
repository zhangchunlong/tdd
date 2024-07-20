package geektime.tdd.rest;

import java.util.*;
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

class PathTemplate implements UriTemplate{

    private final Pattern pattern;
    private PathVariables pathVariables = new PathVariables();
    private int variableGroupStartFrom;

    private static String group(String pattern) {
        return "(" + pattern + ")";
    }

    public PathTemplate(String template) {
        pattern = Pattern.compile(group(pathVariables.template(template)) + "(/.*)?");
        variableGroupStartFrom = 2;
    }

    @Override
    public Optional<MatchResult> match(String path) {
        Matcher matcher = pattern.matcher(path);
        if(!matcher.matches()) return Optional.empty();

        return Optional.of(new PathMatchResult(matcher, pathVariables));
    }

    class PathVariables implements Comparable<PathVariables>{
        private static final String LeftBracket = "\\{";
        private static final String RightBracket = "}";
        private static final String VariableName = "\\w[\\w\\.-]*";
        private static final String NonBrackets = "[^\\{}]+";

        private static Pattern variable = Pattern.compile(LeftBracket + group(VariableName) +
                group(":" + group(NonBrackets)) + "?" + RightBracket);
        private static final int variableNameGroup = 1;
        private static final int variablePatternGroup = 3;

        private static final String defaultVariablePattern = "([^/]+?)";

        private final List<String> variables = new ArrayList<>();
        public Integer specificPatternCount = 0;

        private String template(String template) {
            return variable.matcher(template).replaceAll(this::replace);
        }

        private String replace(java.util.regex.MatchResult result) {
            String variableName = result.group(variableNameGroup);
            String pattern = result.group(variablePatternGroup);

            if(variables.contains(variableName))
                throw new IllegalArgumentException("duplicate variable" + variableName);
            variables.add(variableName);
            if(pattern != null) {
                specificPatternCount ++;
                return group(pattern);
            }
            return defaultVariablePattern;
        }

        public Map<String, String> extract(Matcher matcher) {
            Map<String, String> parameters = new HashMap<>();
            for(int i = 0; i < variables.size(); i++) {
                parameters.put(variables.get(i), matcher.group(variableGroupStartFrom + i));
            }
            return parameters;
        }

        @Override
        public int compareTo(PathVariables o) {
            if(variables.size() > o.variables.size()) return -1;
            if(variables.size() < o.variables.size()) return 1;
            return Integer.compare(o.specificPatternCount, specificPatternCount);
        }
    }

    class PathMatchResult implements MatchResult {
        private  PathVariables variables;
        private int matchLiteralCount;
        private Matcher matcher;
        private Map<String, String> parameters;

        public PathMatchResult(Matcher matcher, PathVariables variables) {
            this.matcher = matcher;
            this.variables = variables;
            this.parameters = variables.extract(matcher);
            this.matchLiteralCount = matcher.group(1).length() - parameters.values().stream().map(String::length).reduce(0, Integer::sum);
        }

        @Override
        public String getMatched() {
            return matcher.group(1);
        }

        @Override
        public String getRemaining() {
            return matcher.group(matcher.groupCount());
        }

        @Override
        public Map<String, String> getMatchedParameters() {
            return parameters;
        }

        @Override
        public int compareTo(MatchResult o) {
            PathMatchResult result = (PathMatchResult) o;
            if(matchLiteralCount > result.matchLiteralCount) return -1;
            if(matchLiteralCount < result.matchLiteralCount) return 1;

            return variables.compareTo(result.variables);
        }
    }
}
