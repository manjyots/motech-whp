package org.motechproject.whp.user.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.motechproject.whp.common.exception.WHPErrorCode;
import org.motechproject.whp.common.exception.WHPRuntimeException;
import org.motechproject.dao.BusinessIdNotUniqueException;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.whp.user.domain.CmfAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllCmfAdmins extends MotechBaseRepository<CmfAdmin> {

    @Autowired
    public AllCmfAdmins(@Qualifier("whpDbConnector") CouchDbConnector dbCouchDbConnector) {
        super(CmfAdmin.class, dbCouchDbConnector);
    }

    @GenerateView
    public CmfAdmin findByUserId(String userId) {
        if (userId == null)
            return null;
        ViewQuery find_by_user_id = createQuery("by_userId").key(userId.toLowerCase()).includeDocs(true);
        return singleResult(db.queryView(find_by_user_id, CmfAdmin.class));
    }

    public void addOrReplace(CmfAdmin cmfAdmin) {
        try {
            addOrReplace(cmfAdmin, "userId", cmfAdmin.getUserId());
        } catch (BusinessIdNotUniqueException e) {
            throw new WHPRuntimeException(WHPErrorCode.DUPLICATE_PROVIDER_ID);
        }
    }

    @View(name = "list_staffname_sorted", map = "function(doc) {if (doc.type ==='CmfAdmin') {emit(doc.staffName, doc._id);}}")
    public List<CmfAdmin> list() {
        ViewQuery q = createQuery("list_staffname_sorted").includeDocs(true);
        return db.queryView(q, CmfAdmin.class);
    }

    public void updateDetails(CmfAdmin cmfAdmin, String staffName, String location, String email, String department) {
        cmfAdmin.setStaffName(staffName);
        cmfAdmin.setLocationId(location);
        cmfAdmin.setEmail(email);
        cmfAdmin.setDepartment(department);
        update(cmfAdmin);
    }
}
