package model.access;

import model.Service;
import model.User;

public class MatrixEntry {
    private User user;
    private Service service;

    public MatrixEntry(User user, Service service) {
	this.user = user;
	this.service = service;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @return the service
     */
    public Service getService() {
        return service;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((service == null) ? 0 : service.hashCode());
	result = prime * result + ((user == null) ? 0 : user.hashCode());
	return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
	if (this == obj) return true;
	if (obj == null) return false;
	if (getClass() != obj.getClass()) return false;
	MatrixEntry other = (MatrixEntry) obj;
	if (service == null) {
	    if (other.service != null) return false;
	} else if (!service.equals(other.service)) return false;
	if (user == null) {
	    if (other.user != null) return false;
	} else if (!user.equals(other.user)) return false;
	return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "[" + user + "], [" + service + "]";
    }
       
}
