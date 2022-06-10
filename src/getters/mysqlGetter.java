package getters;

import byteview.byteview;

import java.sql.*;



public class mysqlGetter implements IGetter {
    static String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
     String DB_URL;
     String USER;
     String PASS;
     byteview ret;

    public mysqlGetter(String database, String user, String password) {
        this.DB_URL = "jdbc:mysql://localhost:3306/" + database + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        this.USER = user;
        this.PASS = password;
    }


    @Override
    public byteview get(String key) { //
        Connection conn = null;
        Statement stmt = null;
        try {
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);

            // 打开链接
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            // 执行查询
            stmt = conn.createStatement();

            String[] tableTargetCondition = key.split(":");
            String table = tableTargetCondition[0];
            String targets = tableTargetCondition[1];
            String[] splitTarget = targets.split(",");
            String condition = tableTargetCondition[2];

            String sql;
            sql = "SELECT " + targets + " FROM " + table + " WHERE " + condition;
            ResultSet rs = stmt.executeQuery(sql);

            String ret = "";
            while (rs.next()) {
                String temp = "";
                for (String target : splitTarget){
                    temp += rs.getString(target);
                    temp += ",";
                }
                temp = temp.substring(0, temp.length()-1);
                ret += temp;
            }
            this.ret = new byteview(ret);
            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
                try {
                    if (conn != null) conn.close();
                } catch (SQLException se) {
                    se.printStackTrace();
                }
            }
            return this.ret;
        }
    }
}