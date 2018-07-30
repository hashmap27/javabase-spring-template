package com.javabase.template.framework.mybatis.spring.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.ibatis.type.JdbcType;
import org.joda.time.DateTime;

/**
 * MyBatis Type Handler
 *
 * org.joda.time.DateTime - DB Date
 *
 * [설정방법]
 * sqlSessionFactory 빈에 typeHanderlPackage 프러퍼티등으로 설정
 *
 * @author Cheong SungHyun <hashmap27@gmail.com>
 */
public class DateTimeTypeHandler extends CommonBaseTypeHandler<DateTime> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, DateTime parameter, JdbcType jdbcType) throws SQLException {
        if(parameter == null) {
            ps.setTimestamp(i, null);
        } else {
            ps.setTimestamp(i, new Timestamp(parameter.toDateTime().getMillis()));
        }
    }

    @Override
    public DateTime getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Timestamp timeStamp = rs.getTimestamp(columnName);
        if(timeStamp == null) {
            return null;
        }
        return new DateTime(timeStamp);
    }

    @Override
    public DateTime getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Timestamp timeStamp = rs.getTimestamp(columnIndex);
        if(timeStamp == null) {
            return null;
        }
        return new DateTime(timeStamp);
    }

    @Override
    public DateTime getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Timestamp timeStamp = cs.getTimestamp(columnIndex);
        if (timeStamp == null) {
            return null;
        }
        return new DateTime(timeStamp);
    }

}
