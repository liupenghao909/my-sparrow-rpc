package com.sparrow.rpc.namesrv.impl;

import com.sparrow.rpc.api.NameService;
import com.sparrow.rpc.namesrv.JsonUtil;
import com.sparrow.rpc.namesrv.dto.JsonMetaInfo;
import com.sparrow.rpc.namesrv.dto.MetaInfo;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * 一个基于Json文件的NameServer，将服务与地址的关系记录持久化到Json中，
 * @author chengwei_shen
 * @date 2022/7/12 20:49
 **/
@Slf4j
public class FileNameService implements NameService {
    private static final Logger logger = LoggerFactory.getLogger(FileNameService.class);
    private static File metaDataFile;

    static {
        /*
        System.getProperty("java.io.tmpdir")：获取操作系统的缓存临时目录
         */
        File tmpDirFile = new File(System.getProperty("java.io.tmpdir"));
        File file = new File(tmpDirFile, "sparrow_rpc_service.json");
        metaDataFile = new File(file.toURI());
    }

    /**
     * 服务注册
     * @param serviceSign
     * @param uri
     */
    @Override
    public void registerServer(String serviceSign, URI uri) {
        logger.info("Service {} register,uri:{}", serviceSign, uri.toString());
        MetaInfo metaInfo = new MetaInfo();
        metaInfo.setServiceSign(serviceSign);
        metaInfo.setUri(uri);
        // 先从存在注册信息的json文件中读出存储的数据
        String jsonMeta = JsonUtil.readJson(metaDataFile);
        // 将新的服务信息添加到原来的服务信息列表中
        try {
            JsonMetaInfo jsonMetaInfo;
            if (jsonMeta.isEmpty()) {
                jsonMetaInfo = new JsonMetaInfo();
                List<MetaInfo> metaInfos = new ArrayList<>();
                metaInfos.add(metaInfo);
                jsonMetaInfo.setMetaInfos(metaInfos);
            } else {
                jsonMetaInfo = JsonUtil.readValue(jsonMeta, JsonMetaInfo.class);
//                jsonMetaInfo.getMetaInfos().forEach(metaInfo1 -> {
//                    if (metaInfo1.equals(metaInfo)) {
//                        throw new RuntimeException("Duplicate serviceSign");
//                    }
//                });
                jsonMetaInfo.getMetaInfos().add(metaInfo);
            }
            // 将添加后的服务列表存回到json文件中
            JsonUtil.writeJson(metaDataFile, jsonMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unregisterServer(String serviceSign, URI uri) {
    }

    /**
     * 服务发现
     * 从注册信息列表中找到第一个与服务签名相同的信息就返回其元信息
     * @param serviceSign
     * @return
     */
    @Override
    public URI seekService(String serviceSign) {
        if (serviceSign.isEmpty()) {
            return null;
        }
        try {
            String jsonMeta = JsonUtil.readJson(metaDataFile);
            JsonMetaInfo jsonMetaInfo = JsonUtil.readValue(jsonMeta, JsonMetaInfo.class);
            for (MetaInfo metaInfo : jsonMetaInfo.getMetaInfos()) {
                if (metaInfo.getServiceSign().equals(serviceSign)) {
                    return metaInfo.getUri();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
