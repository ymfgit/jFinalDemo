package dao.model;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;

/**
 * 表映射
 * @Author: yaomeifa
 * @Date: 2018/11/26
 */
public class MappingKit {
    public static void mapping(ActiveRecordPlugin arp) {
        arp.addMapping("blog", "id", Blog.class);
    }
}
