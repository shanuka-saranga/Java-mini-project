package com.fot.system.view.dashboard.admin.manageNotices;

import com.fot.system.view.dashboard.admin.shared.BaseAdminTablePanel;

public class NoticeTablePanel extends BaseAdminTablePanel {

    public NoticeTablePanel() {
        super(new String[]{"ID", "Title", "Audience", "Priority", "Status", "Published", "Expires"});
    }
}
