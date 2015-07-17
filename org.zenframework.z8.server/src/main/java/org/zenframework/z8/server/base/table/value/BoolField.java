package org.zenframework.z8.server.base.table.value;

import org.zenframework.z8.server.db.DatabaseVendor;
import org.zenframework.z8.server.db.FieldType;
import org.zenframework.z8.server.db.sql.SqlField;
import org.zenframework.z8.server.runtime.IObject;
import org.zenframework.z8.server.types.bool;
import org.zenframework.z8.server.types.primary;
import org.zenframework.z8.server.types.sql.sql_bool;

public class BoolField extends Field {
    public static class CLASS<T extends BoolField> extends Field.CLASS<T> {
        public CLASS(IObject container) {
            super(container);
            setJavaClass(BoolField.class);
            setAttribute(Native, BoolField.class.getCanonicalName());
        }

        @Override
        public Object newObject(IObject container) {
            return new BoolField(container);
        }
    }

    public BoolField(IObject container) {
        super(container);
        setDefault(new bool(false));
        width.set(5);
        stretch.set(false);
        aggregation = Aggregation.Max;
    }

    @Override
    public FieldType type() {
        return FieldType.Boolean;
    }

    @Override
    public String sqlType(DatabaseVendor vendor) {
        String name = type().vendorType(vendor);

        if(vendor == DatabaseVendor.Oracle) {
            return name + "(1)";
        }
        return name;
    }

    public sql_bool sql_bool() {
        return new sql_bool(new SqlField(this));
    }

    @Override
    public primary get() {
        return z8_get();
    }

    public bool z8_get() {
        return (bool)internalGet();
    }

    public BoolField.CLASS<? extends BoolField> operatorAssign(bool value) {
        set(value);
        return (BoolField.CLASS<?>) this.getCLASS();
    }
}
