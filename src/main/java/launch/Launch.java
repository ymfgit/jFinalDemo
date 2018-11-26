package launch;

import com.jfinal.config.*;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.render.ViewType;
import com.jfinal.template.Engine;
import common.Constant;
import utils.DataBaseUtil;

/**
 * 项目启动入口
 * @Author: yaomeifa
 * @Date: 2018/11/26
 */
public class Launch extends JFinalConfig {
    @Override
    public void configConstant(Constants me) {
        //允许inject注入
        me.setInjectDependency(true);
        me.setDevMode(true);
        me.setError404View(Constant.DEFULT_REDIRECT_URL);
        me.setError500View(Constant.DEFULT_REDIRECT_URL);
        me.setViewType(ViewType.FREE_MARKER);
    }

    // 配置路由
    @Override
    public void configRoute(Routes me) {
        //设置初始页面
        me.setBaseViewPath("/");
    }

    @Override
    public void configEngine(Engine me) {

    }

    @Override
    public void configPlugin(Plugins me) {
        PropKit.use("database.properties");
        // 初始化数据库
        DataBaseUtil.getInstance().initDataBase();
        // 缓存插件
        EhCachePlugin ehCache = new EhCachePlugin();
        me.add(ehCache);

    }

    @Override
    public void configInterceptor(Interceptors me) {

    }

    @Override
    public void configHandler(Handlers me) {

    }

    /**
     * jfinal 3.5启动
     */
    public static void main(String[] args) {
        JFinal.start("src/main/webapp", 8080, "/", 5);
    }
}
