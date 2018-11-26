package dao.model;

import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.generator.Generator;
import com.jfinal.plugin.druid.DruidPlugin;

import javax.sql.DataSource;

/**
 * 在数据库表有任何变动时，运行一下 main 方法，极速响应变化进行代码重构
 * @Author: yaomeifa
 * @Date: 2018/11/26
 */
public class ModelGenerator {
    private static DruidPlugin createDruidPlugin() {
        return new DruidPlugin("jdbc:mysql://bdm266490436.my3w.com/bdm266490436_db?characterEncoding=utf8", "bdm266490436", "Copy0623");
    }

    public static DataSource getDataSource() {
        DruidPlugin druidPlugin = createDruidPlugin();
        druidPlugin.start();
        return druidPlugin.getDataSource();
    }

    static String package_ = "dao.model";

    public static void main(String[] args) {
        // base model 所使用的包名
        String baseModelPackageName = package_ + ".base";
        // base model 文件保存路径
        System.out.println(package_.replaceAll("\\.", "/"));
        String baseModelOutputDir = PathKit.getRootClassPath() + "/../../src/main/java/"
                + package_.replaceAll("\\.", "/") + "/base";
        System.out.println(baseModelOutputDir);
        // model 所使用的包名 (MappingKit 默认使用的包名)
        String modelPackageName = package_;
        // model 文件保存路径 (MappingKit 与 DataDictionary 文件默认保存路径)
        String modelOutputDir = baseModelOutputDir + "/..";
        Generator gernerator = new Generator(getDataSource(), baseModelPackageName, baseModelOutputDir,
                modelPackageName, modelOutputDir);
        gernerator.setGenerateDaoInModel(true);
        gernerator.setGenerateDataDictionary(true);
//        gernerator.setRemovedTableNamePrefixes("t_");
        gernerator.generate();
    }
}
