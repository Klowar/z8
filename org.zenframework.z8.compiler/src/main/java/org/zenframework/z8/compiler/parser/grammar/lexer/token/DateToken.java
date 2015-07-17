package org.zenframework.z8.compiler.parser.grammar.lexer.token;

import org.zenframework.z8.compiler.core.IPosition;
import org.zenframework.z8.compiler.parser.type.Primary;
import org.zenframework.z8.compiler.util.Date;

public class DateToken extends ConstantToken {
    private Date value;

    public DateToken() {}

    public DateToken(Date value, IPosition position) {
        super(position);
        this.value = value;
    }

    public Date getValue() {
        return value;
    }

    @Override
    public String format(boolean forCodeGeneration) {
        return '"' + value.toString() + '"';
    }

    @Override
    public String getTypeName() {
        return Primary.Date;
    }

    @Override
    public String getSqlTypeName() {
        return Primary.SqlDate;
    }
}