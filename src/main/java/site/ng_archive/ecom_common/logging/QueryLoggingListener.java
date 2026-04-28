package site.ng_archive.ecom_common.logging;

import io.r2dbc.proxy.core.Binding;
import io.r2dbc.proxy.core.Bindings;
import io.r2dbc.proxy.core.BoundValue;
import io.r2dbc.proxy.core.QueryExecutionInfo;
import io.r2dbc.proxy.core.QueryInfo;
import io.r2dbc.proxy.listener.ProxyExecutionListener;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class QueryLoggingListener implements ProxyExecutionListener {

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
            log.trace("{\"sql\":\"{}\",\"duration\":\"{}ms\"}", sql, millis);
        } else {
            String error = truncateError(execInfo.getThrowable());
            log.trace("{\"sql\":\"{}\",\"duration\":\"{}ms\",\"error\":\"{}\"}", sql, millis, error);
        }
    }

    // $1, $2... (PostgreSQL 위치 기반) 또는 :name (이름 기반) 치환
    private String substitute(String sql, Bindings bindings) {
        for (Binding b : bindings.getIndexBindings()) {
            int idx = (Integer) b.getKey();
            sql = sql.replace("$" + (idx + 1), toLiteral(b.getBoundValue()));
        }
        for (Binding b : bindings.getNamedBindings()) {
            sql = sql.replace(":" + b.getKey(), toLiteral(b.getBoundValue()));
        }
        return sql;
    }

    private String truncateError(Throwable t) {
        if (t == null) return "unknown";
        String msg = t.getMessage();
        if (msg == null) return t.getClass().getSimpleName();
        int idx = msg.indexOf("; SQL statement:");
        return idx > 0 ? msg.substring(0, idx) : msg;
    }

    private String toLiteral(BoundValue bv) {
        if (bv.isNull()) return "NULL";
        Object val = bv.getValue();
        if (val instanceof String || val instanceof Enum<?>) {
            return "'" + val + "'";
        }
        return String.valueOf(val);
    }
}