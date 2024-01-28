package com.schacherbauer;

import liquibase.database.Database;
import liquibase.database.core.HsqlDatabase;
import liquibase.database.core.OracleDatabase;
import liquibase.datatype.DataTypeInfo;
import liquibase.datatype.DatabaseDataType;
import liquibase.datatype.LiquibaseDataType;
import liquibase.datatype.core.VarcharType;
import liquibase.util.StringUtil;

@DataTypeInfo(
    name = "varchar",
    aliases = {"java.sql.Types.VARCHAR", "java.lang.String", "varchar2", "character varying"},
    minParameters = 0,
    maxParameters = 1,
    priority = LiquibaseDataType.PRIORITY_DATABASE)
public class OracleVarcharType extends VarcharType {

  private static final String ORACLE_CHAR_SUFFIX = " char";
  private static final String VARCHAR2 = "VARCHAR2(%s)";

  private static boolean isOracle(Database db) {
    return db instanceof OracleDatabase
        || (db instanceof HsqlDatabase hs && hs.isUsingOracleSyntax());
  }

  /**
   * @param db the {@link Database} for which the native data type is to be constructed
   * @return If oracle or oracle syntax are used append " char" to the size of the varchar2-type.
   *     Otherwise, the size of the created database column is interpreted as byte sized, which is
   *     different to all other db-vendors where the size is interpreted as chars.
   */
  @Override
  public DatabaseDataType toDatabaseDataType(Database db) {
    Object[] parameters = getParameters();
    if (isOracle(db) && parameters != null && parameters.length == 1) {
      if (parameters[0] instanceof String s && StringUtil.isNumeric(s)) {
        var sizeInChars = s + ORACLE_CHAR_SUFFIX;
        return new DatabaseDataType(VARCHAR2.formatted(sizeInChars));
      }
    }
    return super.toDatabaseDataType(db);
  }
}
