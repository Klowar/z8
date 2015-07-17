package org.zenframework.z8.server.db.sql.functions.conversion;

import java.util.Collection;

import org.zenframework.z8.server.base.table.value.IValue;
import org.zenframework.z8.server.db.DatabaseVendor;
import org.zenframework.z8.server.db.FieldType;
import org.zenframework.z8.server.db.sql.FormatOptions;
import org.zenframework.z8.server.db.sql.SqlToken;
import org.zenframework.z8.server.exceptions.db.UnknownDatabaseException;

public class StrToInt extends SqlToken {
    private SqlToken param1;

    public StrToInt(SqlToken p1) {
        param1 = p1;
    }

    @Override
    public void collectFields(Collection<IValue> fields) {
        param1.collectFields(fields);
    }

    @Override
    public String format(DatabaseVendor vendor, FormatOptions options, boolean logicalContext) {
        switch(vendor) {
        case Oracle:
            return "TO_NUMBER(" + param1.format(vendor, options) + ")";
        case SqlServer:
            return "CONVERT(int, " + param1.format(vendor, options) + ")";
        default:
            throw new UnknownDatabaseException();
        }
    }

    @Override
    public FieldType type() {
        return FieldType.Integer;
    }
}
