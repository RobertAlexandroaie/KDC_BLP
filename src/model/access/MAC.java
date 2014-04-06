/**
 * 
 */
package model.access;

import java.util.HashMap;

import model.Service;
import model.User;

/**
 * @author Robert
 * 
 */
public class MAC {
    private HashMap<MatrixEntry, Permission> accessMatrix;

    public MAC() {
	accessMatrix = new HashMap<MatrixEntry, Permission>();
    }

    /**
     * @return the accessMatrix
     */
    public HashMap<MatrixEntry, Permission> getAccessMatrix() {
	return accessMatrix;
    }

    private Permission getPermissionOf(MatrixEntry matrixEntry) {
	if (accessMatrix != null) {
	    return accessMatrix.get(matrixEntry);
	} else {
	    return null;
	}
    }

    private Permission getPermissionOf(User user, Service service) {
	return getPermissionOf(new MatrixEntry(user, service));
    }

    public boolean isPermitted(User user, Service service) {
	Permission perm = getPermissionOf(user, service);
	if (perm != null) {
	    // no read-up
	    if (perm.isRead() && user.getClearance().compareTo(service.getClearance()) < 0) {
		return false;
	    }
	    // no write-down
	    if (perm.isWrite() && user.getClearance().compareTo(service.getClearance()) > 0) {
		return false;
	    }

	    return true;
	} else {
	    return false;
	}
    }

    public boolean isPermitted(MatrixEntry matrixEntry) {
	return isPermitted(matrixEntry.getUser(), matrixEntry.getService());
    }

    public Permission add(User user, Service service, String permission) {
	return add(user, service, new Permission(permission));
    }

    public Permission add(User user, Service service, Permission permission) {
	return accessMatrix.put(new MatrixEntry(user, service), permission);
    }
}
