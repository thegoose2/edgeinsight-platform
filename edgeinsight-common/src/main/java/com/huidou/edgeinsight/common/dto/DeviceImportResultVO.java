package com.huidou.edgeinsight.common.dto;

import java.util.List;

public class DeviceImportResultVO {
    private int successCount;
    private int failCount;
    private List<DeviceImportError> errors;

    public int getSuccessCount() { return successCount; }
    public void setSuccessCount(int successCount) { this.successCount = successCount; }
    public int getFailCount() { return failCount; }
    public void setFailCount(int failCount) { this.failCount = failCount; }
    public List<DeviceImportError> getErrors() { return errors; }
    public void setErrors(List<DeviceImportError> errors) { this.errors = errors; }

    public static class DeviceImportError {
        private int row;
        private String reason;

        public int getRow() { return row; }
        public void setRow(int row) { this.row = row; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
}