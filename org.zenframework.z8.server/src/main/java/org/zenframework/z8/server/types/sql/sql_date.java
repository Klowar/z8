package org.zenframework.z8.server.types.sql;

import org.zenframework.z8.server.db.sql.SqlConst;
import org.zenframework.z8.server.db.sql.SqlToken;
import org.zenframework.z8.server.db.sql.expressions.Add;
import org.zenframework.z8.server.db.sql.expressions.Group;
import org.zenframework.z8.server.db.sql.expressions.Operation;
import org.zenframework.z8.server.db.sql.expressions.Rel;
import org.zenframework.z8.server.db.sql.functions.Count;
import org.zenframework.z8.server.db.sql.functions.Max;
import org.zenframework.z8.server.db.sql.functions.Min;
import org.zenframework.z8.server.db.sql.functions.conversion.DateToChar;
import org.zenframework.z8.server.db.sql.functions.datetime.AddDay;
import org.zenframework.z8.server.db.sql.functions.datetime.AddHour;
import org.zenframework.z8.server.db.sql.functions.datetime.AddMinute;
import org.zenframework.z8.server.db.sql.functions.datetime.AddMonth;
import org.zenframework.z8.server.db.sql.functions.datetime.AddQuarter;
import org.zenframework.z8.server.db.sql.functions.datetime.AddSecond;
import org.zenframework.z8.server.db.sql.functions.datetime.AddYear;
import org.zenframework.z8.server.db.sql.functions.datetime.Day;
import org.zenframework.z8.server.db.sql.functions.datetime.Month;
import org.zenframework.z8.server.db.sql.functions.datetime.Quarter;
import org.zenframework.z8.server.db.sql.functions.datetime.SetDate;
import org.zenframework.z8.server.db.sql.functions.datetime.TruncMonth;
import org.zenframework.z8.server.db.sql.functions.datetime.TruncQuarter;
import org.zenframework.z8.server.db.sql.functions.datetime.TruncYear;
import org.zenframework.z8.server.db.sql.functions.datetime.WeekDay;
import org.zenframework.z8.server.db.sql.functions.datetime.Year;
import org.zenframework.z8.server.db.sql.functions.datetime.YearDay;
import org.zenframework.z8.server.types.date;

public class sql_date extends sql_primary {
    public sql_date() {
        super(new SqlConst(new date()));
    }

    public sql_date(date value) {
        super(new SqlConst(value));
    }

    public sql_date(SqlToken token) {
        super(token);
    }

    public sql_integer z8_quarter() {
        return new sql_integer(new Quarter(this));
    }

    public sql_integer z8_year() {
        return new sql_integer(new Year(this));
    }

    public sql_integer z8_month() {
        return new sql_integer(new Month(this));
    }

    public sql_integer z8_day() {
        return new sql_integer(new Day(this));
    }

    public sql_integer z8_weekDay() {
        return new sql_integer(new WeekDay(this));
    }

    public sql_integer z8_yearDay() {
        return new sql_integer(new YearDay(this));
    }

    public void z8_set(sql_integer year, sql_integer month, sql_integer day) {
        setToken(new SetDate(year, month, day));
    }

    public sql_date z8_truncQuarter() {
        return new sql_date(new TruncQuarter(this));
    }

    public sql_date z8_truncYear() {
        return new sql_date(new TruncYear(this));
    }

    public sql_date z8_truncMonth() {
        return new sql_date(new TruncMonth(this));
    }

    public sql_date z8_addQuarter(sql_integer count) {
        return new sql_date(new AddQuarter(this, count));
    }

    public sql_date z8_addYear(sql_integer count) {
        return new sql_date(new AddYear(this, count));
    }

    public sql_date z8_addMonth(sql_integer count) {
        return new sql_date(new AddMonth(this, count));
    }

    public sql_date z8_addDay(sql_integer count) {
        return new sql_date(new AddDay(this, count));
    }

    public sql_datetime z8_addHour(sql_integer count) {
        return new sql_datetime(new AddHour(this, count));
    }

    public sql_datetime z8_addMinute(sql_integer count) {
        return new sql_datetime(new AddMinute(this, count));
    }

    public sql_datetime z8_addSecond(sql_integer count) {
        return new sql_datetime(new AddSecond(this, count));
    }

    public sql_date z8_max() {
        return new sql_date(new Max(this));
    }

    public sql_date z8_min() {
        return new sql_date(new Min(this));
    }

    public sql_integer z8_count() {
        return new sql_integer(new Count(this));
    }

    public sql_date operatorPriority() {
        return new sql_date(new Group(this));
    }

    public sql_datetime sql_datetime() {
        return new sql_datetime(this);
    }

    @Override
    public sql_string z8_toString() {
        return new sql_string(new DateToChar(this));
    }

    public sql_datetime operatorAdd(sql_datespan value) {
        return new sql_datetime(new Add(this, Operation.Add, value));
    }

    public sql_datetime operatorSub(sql_datespan value) {
        return new sql_datetime(new Add(this, Operation.Sub, value));
    }

    public sql_datespan operatorSub(sql_date value) {
        return new sql_datespan(new Add(this, Operation.Sub, value));
    }

    public sql_datespan operatorSub(sql_datetime value) {
        return new sql_datespan(new Add(this, Operation.Sub, value));
    }

    public sql_bool operatorLess(SqlToken value) {
        return new sql_bool(new Rel(this, Operation.LT, value));
    }

    public sql_bool operatorMore(SqlToken value) {
        return new sql_bool(new Rel(this, Operation.GT, value));
    }

    public sql_bool operatorLessEqu(SqlToken value) {
        return new sql_bool(new Rel(this, Operation.LE, value));
    }

    public sql_bool operatorMoreEqu(SqlToken value) {
        return new sql_bool(new Rel(this, Operation.GE, value));
    }

    public sql_bool operatorEqu(SqlToken value) {
        return new sql_bool(new Rel(this, Operation.Eq, value));
    }

    public sql_bool operatorNotEqu(SqlToken value) {
        return new sql_bool(new Rel(this, Operation.NotEq, value));
    }
}
