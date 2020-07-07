package com.dept.video.server.parser;

import com.dept.video.server.common.MessagesUtility;
import com.dept.video.server.exception.QueryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.expression.spel.SpelNode;
import org.springframework.expression.spel.ast.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class QueryParser {

    private static final String CONTAINS_PREFIX = "*";
    private static final String CONTAINS_REGEX_PREFIX = "\\*";

    private final List<String> segmentElements = Stream.of(":>=", ":<=", ":>", ":<", ":!", "\\(", "\\)", ":", "AND", "OR", "NOT").
            collect(Collectors.toList());
    private final List<String> groupingOperators = Stream.of("\\(", "\\)", "AND", "OR", "NOT").collect(Collectors.toList());
    private final List<String> groupingOperatorsOpen = Stream.of("(").collect(Collectors.toList());
    private final List<String> groupingOperatorsClose = Stream.of(")").collect(Collectors.toList());

    @Autowired
    private MessagesUtility messagesUtility;

    public Query buildQuery(String queryElasticsearch) throws QueryException {
        List<String> segments = splitAndKeep(queryElasticsearch, segmentElements);
        SpelNode node = generateExpression(segments);
        return new BasicQuery(generateQuery(node));
    }

    private boolean isNumericOrBoolean(String str) {
        return isNumeric(str) || isBoolean(str);
    }

    private boolean isNumeric(String str) {
        return str.trim().matches("-?\\d+(\\.\\d+)?");
    }

    private boolean isBoolean(String str) {
        return str.trim().matches("^true|false$");
    }

    private String generateQuery(SpelNode expression) {
        return generateQuery(expression, false);
    }

    private String generateQuery(SpelNode expression, boolean isNot) {
        StringBuilder stringBuilder = new StringBuilder();
        if (expression == null || expression instanceof NullLiteral) {
            stringBuilder.append("{}");
        } else if (expression instanceof OpOr || expression instanceof OpAnd) {
            operationOrAndParcer(expression, isNot, stringBuilder);
        } else if (expression instanceof OpEQ) {
            operationEqParcer(expression, isNot, stringBuilder);
        } else if (expression instanceof OpNE) {
            operationNeParcer(expression, stringBuilder);
        } else if (expression instanceof OpGE) {
            stringBuilder.append(getMongoDbFunction(expression, "$gte"));
        } else if (expression instanceof OpLE) {
            stringBuilder.append(getMongoDbFunction(expression, "$lte"));
        } else if (expression instanceof OpGT) {
            stringBuilder.append(getMongoDbFunction(expression, "$gt"));
        } else if (expression instanceof OpLT) {
            stringBuilder.append(getMongoDbFunction(expression, "$lt"));
        }
        return stringBuilder.toString();
    }

    private void operationOrAndParcer(SpelNode expression, boolean isNot, StringBuilder stringBuilder) {
        List<String> subQuery = new ArrayList<>();
        for (int i = 0; i < expression.getChildCount(); i++) {
            subQuery.add(generateQuery(expression.getChild(i), isNot));
        }
        stringBuilder.append(expression instanceof OpOr ? "{ $or: [" : "{ $and: [");
        stringBuilder.append(subQuery.stream().collect(Collectors.joining(",")));
        stringBuilder.append("] }");
    }

    private void operationNeParcer(SpelNode expression, StringBuilder stringBuilder) {
        if (expression.getChildCount() == 2) {
            if (expression.getChild(1).toStringAST().equals("*")) {
                String fieldName = expression.getChild(0).toStringAST();
                String empty = new StringBuilder().append("{  $or:  [     {").append(fieldName).append(": { $exists: false  }}, {").append(fieldName).append(": { $exists: true, $in: [[],null,'',{}] }}  ] }").toString();
                stringBuilder.append(empty);
            } else {
                stringBuilder.append(getMongoDbFunction(expression, "$ne"));
            }
        } else {
            List<String> subQuery = new ArrayList<>();
            for (int i = 0; i < expression.getChildCount(); i++) {
                subQuery.add(generateQuery(expression.getChild(i), true));
            }
            stringBuilder.append(subQuery.stream().collect(Collectors.joining(",")));
        }
    }

    private void operationEqParcer(SpelNode expression, boolean isNot, StringBuilder stringBuilder) {
        String firstChild = expression.getChild(0).toStringAST();
        if ("_id".equals(firstChild) || "id".equals(firstChild)) {
            stringBuilder.append(getMongoDbFunctionValue(firstChild, expression.getChild(1).toStringAST(), "$eq", true, true, null, isNot));
        } else {
            if (firstChild.charAt(0) == '!') {
                stringBuilder.append(getMongoDbFunction(firstChild.replace("!", ""), prepareMongoQueryValue(expression.getChild(1).toStringAST()), "$ne", null, isNot));
            } else {
                stringBuilder.append(getMongoDbFunction(firstChild, prepareMongoQueryValue(expression.getChild(1).toStringAST()), "$regex", "im", isNot));
            }
        }
    }

    private String getMongoDbFunction(SpelNode expression, String function) {
        return getMongoDbFunction(expression, function, false);
    }

    private String getMongoDbFunction(SpelNode expression, String function, boolean isNot) {
        String firstChild = expression.getChildCount() > 0 ? expression.getChild(0).toStringAST() : null;
        String secondChild = expression.getChildCount() > 1 ? expression.getChild(1).toStringAST() : null;
        return getMongoDbFunction(firstChild, secondChild, function, null, isNot);
    }

    private String getMongoDbFunction(String firstChild, String secondChild, String function, String options, boolean isNot) {
        if (!isNot && secondChild != null && isNumericOrBoolean(secondChild.replace("'", ""))) {
            String alternativeFunction = function;
            String alternativeOptions = options;
            if ("$regex".equals(function)) {
                alternativeFunction = "$eq";
                alternativeOptions = "";
            }
            String value1 = getMongoDbFunctionValue(firstChild, secondChild, function, true, true, options, isNot);
            String value2 = getMongoDbFunctionValue(firstChild, secondChild.replace("'", ""), alternativeFunction, false, true, alternativeOptions, isNot);
            return String.format("{$or: [%s,%s] }", value1, value2);
        } else {
            return getMongoDbFunctionValue(firstChild, secondChild, function, true, true, options, isNot);
        }
    }

    private String getMongoDbFunctionValue(String firstChild, String secondChild, String function, boolean scapeValue, boolean scapeProperties, String options, boolean isNot) {
        StringBuilder stringBuilder = new StringBuilder(100);
        stringBuilder.append("{");
        if (scapeProperties) {
            stringBuilder.append("'").append(firstChild).append("':");
        } else {
            stringBuilder.append(firstChild).append(":");
        }
        if (isNot) {
            stringBuilder.append("{ $not: ");
        }
        boolean emptyFunction = StringUtils.isEmpty(function);
        if (!emptyFunction) {
            stringBuilder.append("{ ").append(function).append(": ");
        }
        if (scapeValue) {
            stringBuilder.append("'").append(secondChild.replace("'", "")).append("'");
        } else {
            stringBuilder.append(secondChild);
        }
        if (!StringUtils.isEmpty(options)) {
            stringBuilder.append(", $options: '").append(options).append("'");
        }
        stringBuilder.append("} ");
        if (isNot) {
            stringBuilder.append("}");
        }
        if (!emptyFunction) {
            stringBuilder.append("}");
        }
        return stringBuilder.toString();
    }

    private String prepareMongoQueryValue(final String value) {
        String response = value;
        if (!StringUtils.isEmpty(response)) {
            boolean startsWith = response.endsWith(CONTAINS_PREFIX);
            boolean endsWith = response.startsWith(CONTAINS_PREFIX);
            if (startsWith && endsWith) {
                response = response.replaceFirst(CONTAINS_REGEX_PREFIX, "");
                response = replaceLastString(response, CONTAINS_PREFIX, "");
            } else if (startsWith) {
                response = replaceLastString(response, CONTAINS_PREFIX, "");
                response = "^" + response;
            } else if (endsWith) {
                response = response.replaceFirst(CONTAINS_REGEX_PREFIX, "");
                response = response + "$";
            } else if (!isNumericOrBoolean(response)) {
                response = "^" + response + "$";
            }
        }
        return response;
    }

    private String replaceLastString(String value, String regex, String replacement) {
        int ind = value.lastIndexOf(regex);
        if (ind >= 0) {
            value = new StringBuilder(value).replace(ind, ind + regex.length(), replacement).toString();
        }
        return value;
    }

    private List<String> splitAndKeep(String input, List<String> regex) {
        ArrayList<String> result = new ArrayList<>();
        List<String> previousSeparators = new LinkedList<>();
        for (String separator : regex) {
            previousSeparators.add(separator);
            if (result.isEmpty()) {
                result.addAll(splitAndKeep(input, separator));
            } else {
                ArrayList<String> partialResult = new ArrayList<>();
                for (String s : result) {
                    if (previousSeparators.contains(s)) {
                        partialResult.add(s);
                    } else {
                        partialResult.addAll(splitAndKeep(s, separator));
                    }
                }
                result = partialResult;
            }
        }
        return result;
    }

    private List<String> splitAndKeep(String input, String regex) {
        ArrayList<String> result = new ArrayList<>();
        if (!StringUtils.isEmpty(input)) {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(input);
            int pos = 0;
            while (m.find()) {
                if (pos != m.start()) {
                    String substring = input.substring(pos, m.start());
                    if (!substring.trim().isEmpty()) {
                        result.add(substring);
                    }
                }
                String substring = input.substring(m.start(), m.end());
                if (!substring.trim().isEmpty()) {
                    result.add(substring);
                }
                pos = m.end();
            }
            if (pos < input.length()) {
                String substring = input.substring(pos);
                if (!substring.trim().isEmpty()) {
                    result.add(substring);
                }
            }
        }
        return result;
    }

    private SpelNode generateExpressionByType(Object fragment, int pos) throws QueryException {
        SpelNode node;
        if (fragment instanceof List) {
            node = generateExpressionFromGropedList((List) fragment, 0, ((List) fragment).size());
        } else {
            node = new PropertyOrFieldReference(false, String.valueOf(fragment).trim(), pos + 1);
        }
        return node;
    }

    private SpelNode generateExpression(List<String> segments) throws QueryException {
        if (segments != null && !segments.isEmpty()) {
            ArrayList valuesListFinal = new ArrayList();
            try {
                generateExpressionList(segments, 0, segments.size(), valuesListFinal);
                return generateExpressionFromGropedList(valuesListFinal, 0, valuesListFinal.size());
            } catch (IndexOutOfBoundsException e) {
                throw new QueryException(e.getMessage(), e);
            }
        } else {
            return null;
        }
    }

    private SpelNode generateExpressionFromGropedList(List fragments, int initPos, int endPos) throws QueryException {
        if (fragments.get(initPos).equals("NOT")) {
            SpelNode node1 = generateExpressionFromGropedList(fragments, initPos + 1, endPos);
            return new OpNE(initPos, (SpelNodeImpl) node1);
        } else if (endPos > initPos + 1) {
            Object operator = fragments.get(initPos + 1);
            if (groupingOperators.contains(operator)) {
                SpelNodeImpl node1 = (SpelNodeImpl) generateExpressionByType(fragments.get(initPos), initPos);
                SpelNodeImpl node2 = (SpelNodeImpl) generateExpressionFromGropedList(fragments, initPos + 2, endPos);
                return generateSpelOperator(operator, node1, node2, initPos + 1 + endPos);
            } else {
                SpelNodeImpl node1 = (SpelNodeImpl) generateExpressionByType(fragments.get(initPos), initPos);
                SpelNodeImpl node2 = (SpelNodeImpl) generateExpressionByType(fragments.get(initPos + 2), initPos + 2);
                SpelNode mainNode = generateSpelOperator(operator, node1, node2, initPos + 1 + initPos + 2);
                if (initPos + 3 == endPos) {
                    return mainNode;
                } else {
                    return generateExpressionFromGropedList(fragments, initPos + 3, endPos, (SpelNodeImpl) mainNode);
                }
            }
        } else {
            return generateExpressionByType(fragments.get(initPos), initPos);
        }
    }

    private SpelNode generateSpelOperator(Object fragment, SpelNodeImpl node1, SpelNodeImpl node2, int pos) throws QueryException {
        SpelNode spelNode = null;
        if (fragment instanceof String) {
            switch (((String) fragment).toUpperCase()) {
                case "AND":
                    spelNode = new OpAnd(pos, node1, node2);
                    break;
                case "OR":
                    spelNode = new OpOr(pos, node1, node2);
                    break;
                case ":>":
                    spelNode = new OpGE(pos, node1, node2);
                    break;
                case ":>=":
                    spelNode = new OpGT(pos, node1, node2);
                    break;
                case ":<":
                    spelNode = new OpLE(pos, node1, node2);
                    break;
                case ":<=":
                    spelNode = new OpLT(pos, node1, node2);
                    break;
                case ":!":
                    spelNode = new OpNE(pos, node1, node2);
                    break;
                case "NOT":
                    spelNode = new OpNE(pos, node1, node2);
                    break;
                case ":":
                    spelNode = new OpEQ(pos, node1, node2);
                    break;
                default:
                    throw new QueryException(messagesUtility.getMessage("query.error.bad.format"));
            }
        }
        return spelNode;
    }

    private SpelNode generateExpressionFromGropedList(List fragments, int initPos, int endPos, SpelNodeImpl node1) throws QueryException {
        if (endPos > initPos) {
            Object operator = fragments.get(initPos);
            if (groupingOperators.contains(operator)) {
                SpelNodeImpl node2 = (SpelNodeImpl) generateExpressionFromGropedList(fragments, initPos + 1, endPos);
                return generateSpelOperator(operator, node1, node2, initPos + endPos);
            } else {
                SpelNodeImpl node2 = (SpelNodeImpl) generateExpressionByType(fragments.get(initPos + 1), initPos + 1);
                SpelNode mainNode = generateSpelOperator(operator, node1, node2, initPos + initPos + 2);
                if (initPos + 2 == endPos) {
                    return mainNode;
                } else {
                    return generateExpressionFromGropedList(fragments, initPos + 2, endPos, (SpelNodeImpl) mainNode);
                }
            }
        } else {
            return generateExpressionByType(fragments.get(initPos), initPos);
        }
    }

    private int generateExpressionList(List<String> segments, int initPos, int endPos, List valuesList) {
        for (int i = initPos; i < endPos && i < segments.size(); i++) {
            String segment = segments.get(i);
            if (groupingOperatorsOpen.contains(segment)) {
                List subValues = new ArrayList();
                i = generateExpressionList(segments, i + 1, endPos, subValues);
                valuesList.add(subValues);
            } else if (groupingOperatorsClose.contains(segment)) {
                return i;
            } else {
                valuesList.add(segment);
            }
        }
        return endPos;
    }
}
