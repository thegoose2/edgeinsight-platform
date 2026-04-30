package com.huidou.edgeinsight.core.cache;

import lombok.Data;

import java.util.Set;

@Data
public class ResolvedContext {

    private Long deviceId;
    private Long deviceTypeId;
    private String parserId;
    private String protocol;
    private Set<String> validPointCodes;
    private String lifecycleStatus;
    private int gracePeriodSecs;
}
