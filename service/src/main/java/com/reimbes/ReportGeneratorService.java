package com.reimbes;

public interface ReportGeneratorService {
    byte[] getReport(ReimsUser user, long start, long end) throws Exception;
}
