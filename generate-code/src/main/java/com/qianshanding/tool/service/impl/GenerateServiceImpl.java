package com.qianshanding.tool.service.impl;

import com.qianshanding.tool.entity.CodeBO;
import com.qianshanding.tool.entity.ProjectBO;
import com.qianshanding.tool.service.GenerateService;
import com.qianshanding.tool.service.support.AutoGenerateCode;
import com.qianshanding.tool.utils.CmdUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 自动生成代码
 *
 * @author fish
 */
@Service("generateService")
public class GenerateServiceImpl implements GenerateService {

    @Override
    public String generateCode(CodeBO generateBO, String basePath) throws Exception {
        AutoGenerateCode gc = new AutoGenerateCode();
        return gc.generate(generateBO, basePath);
    }

    @Override
    public String generateProject(ProjectBO projectBO, String basePath) throws Exception {
        String cdCmd = "cd " + basePath;
        String generateProjectCmd = "mvn archetype:generate -DgroupId=" + projectBO.getGroupId() + " -DartifactId="
                + projectBO.getArtifactId() + " -Dversion=" + projectBO.getVersion() + " -DarchetypeGroupId="
                + projectBO.getArchetypeGroupId() + " -DarchetypeArtifactId="
                + projectBO.getArchetypeArtifactId() + "-DarchetypeVersion="
                + projectBO.getArchetypeVersion() + "-DinteractiveMode=false -DarchetypeCatalog=internal";
        List<String> cmdList = new ArrayList<String>();
        cmdList.add(cdCmd);
        CmdUtils.test();
//        cmdList.add(generateProjectCmd);
//        Boolean boo = CmdUtils.exec(cmdList);
        return null;
    }
}
