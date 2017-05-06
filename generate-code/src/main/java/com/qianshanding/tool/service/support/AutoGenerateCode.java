package com.qianshanding.tool.service.support;

import com.qianshanding.tool.db.entity.DataBase;
import com.qianshanding.tool.db.entity.Table;
import com.qianshanding.tool.entity.CodeBO;
import com.qianshanding.tool.factory.CodeFactory;
import com.qianshanding.tool.factory.ConnectionFactory;
import com.qianshanding.tool.utils.TableUtils;
import com.qianshanding.tool.utils.ZipHelper;
import freemarker.template.Configuration;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.qianshanding.tool.utils.Constants.*;

/**
 * Created by fish on 2016/10/17.
 */
public class AutoGenerateCode {
    private List<Table> tables = null;
    private Configuration configuration = null;
    private CodeBO gb;
    private String sourcePath;

    public String generate(CodeBO generateBO, String basePath) throws Exception {
        this.gb = generateBO;
        Connection conn = ConnectionFactory.getConnection(gb.getDriverClassName(), gb.getUrl()
                , gb.getUserName(), gb.getPassWord());
        String zipFilePath = null;
        try {
            tables = TableUtils.getTables(conn, gb.getAuthor(), gb.getTableNames());
            configuration = CodeFactory.getConfiguration();
            String fileName = generateRandomDir();
            sourcePath = basePath + "/" + fileName + "/";
            List<String> tableList = new ArrayList<String>();
            List<String> tableMlList = new ArrayList<String>();
            for (Table table : tables) {
                table.setDaoPackage(gb.getDaoPackage());
                table.setDoPackage(gb.getDoPackage());
                table.setTransferPackage(gb.getTransferPackage());
                table.setBoPackage(gb.getBoPackage());
                table.setAuthor(gb.getAuthor());
                if (gb.isCreateBo()) {
                    outBO(table);
                }
                if (gb.isCreateTransfer()) {
                    outTransfer(table);
                }
                //@TODO 自动生成Service
//                if (gb.isCreateService()) {
//                    outService(table);
//                }
                if (gb.isCreateDal()) {
                    outDO(table);
                    outMapperXml(table);
//                    outDaoClass(table);
                    outMapperClass(table);
                    tableList.add(table.getClassName_Java());
                    tableMlList.add(table.getClassName_ml());
                }
            }
            if (gb.isCreateDal()) {
                DataBase dataBase = new DataBase();
                dataBase.setTableList(tableList);
                dataBase.setTableMlList(tableMlList);
                dataBase.setMybatisXmlPath(gb.getMybatisXmlPath());
                dataBase.setDoPackage(gb.getDoPackage());
                outSpringMybatis(gb);
                outMybatisConfig(dataBase);
            }
            zipFilePath = basePath + "/" + fileName + ".zip";
            //压缩成zip
            ZipHelper.zip(sourcePath, zipFilePath);
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);
        }
        return zipFilePath;
    }

    /**
     * 生成DO对象
     *
     * @param table
     */
    public void outDO(Table table) {
        String packageName = gb.getDoPackage();
        packageName = packageName.replace(".", "/");
        String filePath = sourcePath + packageName;
        CodeFactory.dataSourceOut(configuration, "do.ftl", table, table.getClassName_Java() + DO_SUFFIX, filePath);
    }

    /**
     * 生成Dao类
     *
     * @param table
     */
    public void outDaoClass(Table table) {
        String packageName = gb.getDaoPackage();
        packageName = packageName.replace(".", "/");
        String filePath = sourcePath + packageName;
        CodeFactory.dataSourceOut(configuration, "dao_class.ftl", table, table.getClassName_Java() + DAO_SUFFIX, filePath);
    }

    /**
     * 生成Mapper类
     *
     * @param table
     */
    public void outMapperClass(Table table) {
        String packageName = gb.getDaoPackage();
        packageName = packageName.replace(".", "/");
        String filePath = sourcePath + packageName;
        CodeFactory.dataSourceOut(configuration, "mapper_interface.ftl", table, table.getClassName_Java() + MAPPER_SUFFIX, filePath);
    }

    /**
     * 生成mybatis xml
     *
     * @param table
     */
    public void outMapperXml(Table table) {
        String filePath = sourcePath + "/resources/" + gb.getMybatisXmlPath();
        CodeFactory.dataSourceOut(configuration, "mapper_xml.ftl", table, "sqlmap-" + table.getClassName_ml() + ".xml", filePath);
    }


    /**
     * 生成BO
     *
     * @param table
     */
    public void outBO(Table table) {
        String packageName = gb.getBoPackage();
        packageName = packageName.replace(".", "/");
        String filePath = sourcePath + "/" + packageName;
        CodeFactory.dataSourceOut(configuration, "bo.ftl", table, table.getClassName_Java() + BO_SUFFIX, filePath);
    }

    /**
     * 生成Transfer
     *
     * @param table
     */
    public void outTransfer(Table table) {
        String packageName = gb.getTransferPackage();
        packageName = packageName.replace(".", "/");
        String filePath = sourcePath + packageName;
        CodeFactory.dataSourceOut(configuration, "transfer.ftl", table, table.getClassName_Java() + TRANSFER_SUFFIX, filePath);
    }

    /**
     * 生成mybatis-config.ftl
     *
     * @param gb
     */
    public void outSpringMybatis(CodeBO gb) {
        String filePath = sourcePath + "resources";
        CodeFactory.dataSourceOut(configuration, "spring_mybatis.ftl", gb, "spring-mybatis.xml", filePath);
    }

    /**
     * 生成spring-mybatis.ftl
     *
     * @param dataBase
     */
    public void outMybatisConfig(DataBase dataBase) {
        String filePath = sourcePath + "resources";
        CodeFactory.dataSourceOut(configuration, "mybatis_config.ftl", dataBase, "mybatis-config.xml", filePath);
    }

    private String generateRandomDir() {
        return String.valueOf(System.nanoTime()) + (new Random().nextInt(900) + 100);
    }
}
