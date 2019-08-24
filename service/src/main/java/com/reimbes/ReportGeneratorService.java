package com.reimbes;

public interface ReportGeneratorService {
    byte[] getReport(ReimsUser user, Long start, Long end) throws Exception;
}
