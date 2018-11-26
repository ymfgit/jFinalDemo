package utils;

import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.sql.SqlKit;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.Template;
import com.jfinal.template.source.ClassPathSourceFactory;
import dao.model.MappingKit;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 连接数据库
 * @Author: yaomeifa
 * @Date: 2018/11/26
 */
public class DataBaseUtil {
    private static class InstanceHolder {
        private static final DataBaseUtil INSTANCE = new DataBaseUtil();
    }

    public static DataBaseUtil getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private static final Logger LOG = Logger.getLogger(DataBaseUtil.class);

    public void initDataBase() {
        String url = PropKit.get("jdbc.url").trim();
        String name = PropKit.get("jdbc.name").trim();
        String pwd = PropKit.get("jdbc.pwd").trim();
        String dbName = PropKit.get("jdbc.db").trim();
        DruidPlugin druid = getConnection(url, name, pwd);
        druid.start();
        Statement statement = null;
        try {
            statement = druid.getDataSource().getConnection().createStatement();
            // 创建db
            boolean createResult = statement.execute(
                    "CREATE DATABASE IF NOT EXISTS " + dbName + " CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_bin'");
            if (!createResult) {
                LOG.error("创建DB库失败");
            }
            LOG.info("创建DB库完成");
            statement.close();
            druid.stop();

            // 创建表
            druid = getConnection(url + dbName, name, pwd);
            druid.start();
            ActiveRecordPlugin arp = new ActiveRecordPlugin("mysql", druid);
            arp.getEngine().setSourceFactory(new ClassPathSourceFactory());
            arp.addSqlTemplate("init.sql");
            // sql热加载，正式部署时关闭
            arp.setDevMode(true);
            arp.setShowSql(true);
            arp.start();
            SqlKit sqlKit = arp.getSqlKit();
            Set<Map.Entry<String, Template>> templates = sqlKit.getSqlMapEntrySet();
            for (Map.Entry<String, Template> temp : templates) {
                String tableSql = temp.getValue().renderToString(new HashMap<>());
                if (StringUtils.isEmpty(tableSql)) {
                    LOG.error("请检查数据库配置文件");
                }
                Db.update(tableSql);
            }
            LOG.info("创建表完成");
            arp.stop();

            // 映射
            MappingKit.mapping(arp);
            arp.addSqlTemplate("sql.sql");
            arp.start();
            LOG.info("*****数据库初始化完成！*****");
        } catch (SQLException e) {
            LOG.error("数据库初始化失败", e);
        }
    }

    private DruidPlugin getConnection(String url, String name, String pwd) {
        return new DruidPlugin(url, name, pwd);
    }
}
