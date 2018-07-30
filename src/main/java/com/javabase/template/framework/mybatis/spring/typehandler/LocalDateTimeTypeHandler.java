package com.javabase.template.framework.mybatis.spring.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.ibatis.type.JdbcType;
import org.joda.time.LocalDateTime;

/**
 * MyBatis Type Handler
 *
 * org.joda.time.LocalDateTime - DB Date
 *
 * [설정방법]
 * sqlSessionFactory 빈에 typeHandlersPackage 프라퍼티등으로 설정
 *
 * @author Cheong SungHyun <hashmap27@gmail.com>
 */
public class LocalDateTimeTypeHandler extends CommonBaseTypeHandler<LocalDateTime> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, LocalDateTime parameter, JdbcType jdbcType) throws SQLException {
        if(parameter == null) {
            ps.setTimestamp(i, null);
        } else {
            ps.setTimestamp(i, new Timestamp(parameter.toDateTime().getMillis()));
        }
    }

    @Override
    public LocalDateTime getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Timestamp timeStamp = rs.getTimestamp(columnName);
        if(timeStamp == null) {
            return null;
        }
        return new LocalDateTime(timeStamp);
    }

    @Override
    public LocalDateTime getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Timestamp timeStamp = rs.getTimestamp(columnIndex);
        if(timeStamp == null) {
            return null;
        }
        return new LocalDateTime(timeStamp);
    }

    @Override
    public LocalDateTime getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Timestamp timeStamp = cs.getTimestamp(columnIndex);
        if(timeStamp == null) {
            return null;
        }
        return new LocalDateTime(timeStamp);
    }

}
