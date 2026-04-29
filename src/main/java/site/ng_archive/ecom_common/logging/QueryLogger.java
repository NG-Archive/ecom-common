package site.ng_archive.ecom_common.logging;

import io.r2dbc.proxy.core.Binding;
import io.r2dbc.proxy.core.Bindings;
import io.r2dbc.proxy.core.BoundValue;
import io.r2dbc.proxy.core.QueryExecutionInfo;
import io.r2dbc.proxy.core.QueryInfo;
import io.r2dbc.proxy.listener.ProxyExecutionListener;
import com.fasterxml.jackson.core.io.JsonStringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j(topic = "QueryLogger")
public class QueryLogger implements ProxyExecutionListener {

    private static final Set<String> SENSITIVE_KEYS = Set.of(
            "password"
    );

    private static final Pattern SENSITIVE_COLUMN_PATTERN = Pattern.compile(
            "(?i)\\b(" + String.join("|", SENSITIVE_KEYS.stream().map(Pattern::quote).toList()) + ")"
            + "\\s*=\\s*(?:'[^']*'|[^,\\s)]+)"
    );

    @Override
    public void afterQuery(QueryExecutionInfo execInfo) {
        if (!log.isDebugEnabled()) return;
        if (execInfo.getValueStore().get("logged") != null) return;
        execInfo.getValueStore().put("logged", Boolean.TRUE);

        List<String> sqls = new ArrayList<>();
        for (QueryInfo queryInfo : execInfo.getQueries()) {
            String sql = queryInfo.getQuery().replaceAll("\\s+", " ").trim();
            List<Bindings> bindingsList = queryInfo.getBindingsList();
            if (bindingsList.isEmpty()) {
                sqls.add(sql);
            } else {
                for (Bindings bindings : bindingsList) {
                    sqls.add(substitute(sql, bindings));
                }
            }
        }

        long millis = execInfo.getExecuteDuration().toMillis();
        String sql = String.join("; ", sqls);

        if (execInfo.isSuccess()) {
            log.trace("{\"duration\":\"{}ms\",\"sql\":\"{}\"}", millis, escapeJson(sql));
        } else {
            String error = truncateError(execInfo.getThrowable());
            log.trace("{\"duration\":\"{}ms\",\"sql\":\"{}\",\"error\":\"{}\"}", millis, escapeJson(sql), escapeJson(error));
        }
    }

    private String substitute(String sql, Bindings bindings) {
        for (Binding b : bindings.getIndexBindings()) {
            int idx = (Integer) b.getKey();
            sql = sql.replaceAll("\\$" + (idx + 1) + "(?!\\d)",
                    Matcher.quoteReplacement(toLiteral(b.getBoundValue())));
        }
        for (Binding b : bindings.getNamedBindings()) {
            String key = String.valueOf(b.getKey());
            String literal = isSensitive(key) ? "'***'" : toLiteral(b.getBoundValue());
            sql = sql.replaceAll(":" + Pattern.quote(key) + "(?!\\w)",
                    Matcher.quoteReplacement(literal));
        }
        return SENSITIVE_COLUMN_PATTERN.matcher(sql)
                .replaceAll(m -> m.group(1) + " = '***'");
    }

    private boolean isSensitive(String key) {
        String lower = key.toLowerCase();
        return SENSITIVE_KEYS.stream().anyMatch(lower::contains);
    }

    private String truncateError(Throwable t) {
        if (t == null) return "unknown";
        String msg = t.getMessage();
        if (msg == null) return t.getClass().getSimpleName();
        int idx = msg.indexOf("; SQL statement:");
        return idx > 0 ? msg.substring(0, idx) : msg;
    }

    private String escapeJson(String value) {
        if (value == null) return "null";
        return new String(JsonStringEncoder.getInstance().quoteAsString(value));
    }

    private String toLiteral(BoundValue bv) {
        if (bv.isNull()) return "NULL";
        Object val = bv.getValue();
        if (val instanceof String || val instanceof Enum<?>) {
            String escaped = String.valueOf(val).replace("'", "''");
            return "'" + escaped + "'";
        }
        return String.valueOf(val);
    }
}