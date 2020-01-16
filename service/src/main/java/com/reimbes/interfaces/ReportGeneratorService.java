package com.reimbes.interfaces;

import com.reimbes.ReimsUser;

public interface ReportGeneratorService {
    byte[] getReport(ReimsUser user, Long start, Long end, String reimbursementType) throws Exception;
}
