package com.sparrow.rpc.namesrv.dto;

import java.util.List;

/**
 * @author chengwei_shen
 * @date 2022/7/20 13:52
 **/
public class JsonMetaInfo {
    List<MetaInfo> metaInfos;

    public List<MetaInfo> getMetaInfos() {
        return metaInfos;
    }

    public void setMetaInfos(List<MetaInfo> metaInfos) {
        this.metaInfos = metaInfos;
    }
}
