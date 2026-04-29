package site.ng_archive.ecom_common.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2dbc.proxy.core.Binding;
import io.r2dbc.proxy.core.Bindings;
import io.r2dbc.proxy.core.QueryExecutionInfo;
import io.r2dbc.proxy.listener.ProxyExecutionListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.ng_archive.ecom_common.error.ErrorMessageUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
public class QueryLogger implements ProxyExecutionListener {

    private final ObjectMapper mapper;

    @Override
    public void afterQuery(QueryExecutionInfo execInfo) {
        if(isLoggedThenMark(execInfo)) {
            return;
        }

        try {
            List<String> boundQueries = execInfo.getQueries().stream()
                .flatMap(queryInfo -> {
                    String originalSql = queryInfo.getQuery();
                    List<Bindings> bindingsList = queryInfo.getBindingsList();

                    // 1. 바인딩 정보가 없으면 원본 SQL만 정리해서 반환
                    if (bindingsList == null || bindingsList.isEmpty()) {
                        return Stream.of(cleanSql(originalSql));
                    }

                    // 2. 각 바인딩 세트를 ?를 실제 값으로 치환
                    return bindingsList.stream().map(binding -> {
                        String replacedSql = replaceBindings(originalSql, binding.getIndexBindings());
                        return cleanSql(replacedSql);
                    });
                })
                .collect(Collectors.toList());

            // 요청하신 JSON 구조 생성
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("queries", boundQueries);
            result.put("elapsedTime", execInfo.getExecuteDuration().toMillis() + "ms");
            result.put("success", execInfo.isSuccess());

            log.trace(mapper.writeValueAsString(result));

        } catch (Exception e) {
            log.error(ErrorMessageUtil.buildErrorJson(e));
        }
    }

    private String cleanSql(String sql) {
        return sql.replaceAll("\\s+", " ").trim();
    }

    private String formatValue(Object value) {
        if (value == null) return "NULL";
        if (value instanceof String) return "'" + value + "'";
        return String.valueOf(value);
    }

    private String replaceBindings(String sql, SortedSet<Binding> bindings) {
        String resultSql = sql;
        for (var binding : bindings) {
            String value = formatValue(binding.getBoundValue().getValue());
            resultSql = resultSql.replaceFirst("\\?", Matcher.quoteReplacement(value));
        }
        return resultSql;
    }

    private static boolean isLoggedThenMark(QueryExecutionInfo execInfo) {
        if (execInfo.getValueStore().get("logged") != null) return true;
        execInfo.getValueStore().put("logged", Boolean.TRUE);
        return false;
    }
}