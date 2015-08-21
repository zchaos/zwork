package jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.succez.commons.util.io.IOUtils;

public class JDBCUtils {
	private static final String DRIVER_ORACLE = "oracle.jdbc.driver.OracleDriver";

	private static final String DRIVER_MYSQL = "com.mysql.jdbc.Driver";

	private static final String DRIVER_DB2 = "com.ibm.db2.jcc.DB2Driver";

	private static final String DRIVER_SQLSERVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

	private static final String DRIVER_SYBASE = "com.sybase.jdbc3.jdbc.SybDriver";

	private static final String DRIVER_VERTICA = "com.vertica.jdbc.Driver";

	private static final String DRIVER_DM = "dm.jdbc.driver.DmDriver";

	private static final String DRIVER_POSTGRESQL = "org.postgresql.Driver";

	private static final String DRIVER_HIVE = "org.apache.hive.jdbc.HiveDriver";

	private static final String[][] MAPPING = { { "oracle", DRIVER_ORACLE }, { "mysql", DRIVER_MYSQL },
			{ "db2", DRIVER_DB2 }, { "sqlserver", DRIVER_SQLSERVER }, { "sybase", DRIVER_SYBASE },
			{ "vertica", DRIVER_VERTICA }, { "dm", DRIVER_DM }, { "postgresql", DRIVER_POSTGRESQL },
			{ "hive", DRIVER_HIVE } };

	public static Properties getConf(String name) throws Exception {
		Properties prop = new Properties();
		InputStream in = JDBCUtils.class.getResourceAsStream("conf/" + name + ".conf");
		try {
			prop.load(in);
		}
		finally {
			in.close();
		}
		return prop;
	}

	public static Connection getConnection(String url, String username, String password) throws Exception {
		String driver = getDriver(url);
		Class.forName(driver);
		Connection con = DriverManager.getConnection(url, username, password);
		return con;
	}

	public static Connection getConnection(Properties prop) throws Exception {
		String url = prop.getProperty("jdbc.url");
		String user = prop.getProperty("jdbc.user");
		String password = prop.getProperty("jdbc.password");
		return getConnection(url, user, password);
	}

	public static List<List<Object>> getData(String confName, String tableName, String[] fields, String[][] condition)
			throws Exception {
		Properties prop = getConf(confName);
		Connection con = getConnection(prop);
		try {
			return getData(con, tableName, fields, condition);
		}
		finally {
			con.close();
		}
	}

	public static List<List<Object>> getData(Connection con, String tableName, String[] fields, String[][] condition)
			throws Exception {
		String sql = createSql(tableName, fields, condition);
		PreparedStatement ps = con.prepareStatement(sql);
		try {
			for (int i = 0, len = condition == null ? 0 : condition.length; i < len; i++) {
				ps.setString(i + 1, condition[i][1]);
			}
			ResultSet rs = ps.executeQuery();
			try {
				return getRowsData(rs);
			}
			finally {
				rs.close();
			}
		}
		finally {
			ps.close();
		}
	}

	private static List<List<Object>> getRowsData(ResultSet rs) throws Exception {
		ArrayList<List<Object>> rows = new ArrayList<List<Object>>();
		ResultSetMetaData meta = rs.getMetaData();
		while (rs.next()) {
			List<Object> row = getRowData(rs, meta);
			rows.add(row);
		}
		return rows;
	}

	private static List<Object> getRowData(ResultSet rs, ResultSetMetaData meta) throws Exception {
		ArrayList<Object> row = new ArrayList<Object>();
		int count = meta.getColumnCount() + 1;
		for (int i = 1; i < count; i++) {
			int type = meta.getColumnType(i);
			Object o = readData(rs, i, type);
			row.add(o);
		}
		return row;
	}

	private static Object readData(ResultSet rs, int index, int type) throws Exception {
		Object o = null;
		switch (type) {
			case Types.TINYINT:
			case Types.SMALLINT:
			case Types.INTEGER:
			case Types.BIGINT:
				o = rs.getInt(index);
				break;
			case Types.FLOAT:
				o = rs.getFloat(index);
				break;
			case Types.DOUBLE:
			case Types.NUMERIC:
			case Types.DECIMAL:
				o = rs.getDouble(index);
				break;
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
				o = rs.getString(index);
				break;
			case Types.DATE:
				o = rs.getDate(index);
				break;
			case Types.TIME:
				o = rs.getTime(index);
				break;
			case Types.TIMESTAMP:
				o = rs.getTimestamp(index);
				break;
			case Types.BLOB:
				InputStream in = rs.getBinaryStream(index);
				try {
					o = IOUtils.toByteArray(in);
				}
				finally {
					in.close();
				}
				break;
			case Types.CLOB:
				Reader r = rs.getCharacterStream(index);
				try {
					o = IOUtils.toString(r);
				}
				finally {
					r.close();
				}
				break;
			case Types.BOOLEAN:
				o = rs.getBoolean(index);
				break;
			case Types.REF:
			case Types.DATALINK:
			case Types.BIT:
			case Types.REAL:
			case Types.BINARY:
			case Types.VARBINARY:
			case Types.LONGVARBINARY:
			case Types.NULL:
			case Types.OTHER:
			case Types.JAVA_OBJECT:
			case Types.DISTINCT:
			case Types.STRUCT:
			case Types.ARRAY:
			default:
				o = rs.getObject(index);
		}
		return o;
	}

	public static String createSql(String tableName, String[] fields, String[][] condition) {
		StringBuilder sql = new StringBuilder();
		sql.append("select ");
		for (int i = 0, len = fields.length; i < len; i++) {
			if (i != 0) {
				sql.append(',');
			}
			sql.append(fields[i]);
		}
		sql.append(" from ").append(tableName);
		if (condition != null && condition.length != 0) {
			sql.append(" where ");
			for (int i = 0, len = condition.length; i < len; i++) {
				if (i != 0) {
					sql.append(" and ");
				}
				sql.append(condition[i][0]).append("=?");
			}
		}
		return sql.toString();
	}

	private static String getDriver(String url) {
		url = url.toLowerCase();
		String[][] m = MAPPING;
		int len = m.length;
		for (int i = 0; i < len; i++) {
			if (url.indexOf(m[i][0]) >= 0) {
				return m[i][1];
			}
		}
		return null;
	}
}
