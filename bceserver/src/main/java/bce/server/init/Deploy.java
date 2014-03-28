package bce.server.init;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

/**
 * 工具类，暂时用于导出JPA标准下的Hibernate数据库
 *
 * @author robins
 *
 */
public class Deploy {

    /**
     * 导出数据库
     */
    public static void exportDB() {
        Configuration cfg = new Configuration().configure();
        SchemaExport export = new SchemaExport(cfg);
        export.create(true, true);
    }

    public static void main(String[] args) {
        exportDB();
    }
}
