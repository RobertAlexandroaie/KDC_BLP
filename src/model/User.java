package model;

public class User {
    private String name;
    private Clearance clearance;

    public User(String name) {
	this.name = name;
	clearance = Clearance.U;
    }

    public User(String name, Clearance clearance) {
	this(name);
	this.clearance = clearance;
    }

    /**
     * @return the name
     */
    public String getName() {
	return name;
    }

    /**
     * @return the clearance
     */
    public Clearance getClearance() {
	return clearance;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((name == null) ? 0 : name.hashCode());
	return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
	if (this == obj) return true;
	if (obj == null) return false;
	if (getClass() != obj.getClass()) return false;
	User other = (User) obj;
	if (name == null) {
	    if (other.name != null) return false;
	} else if (!name.equals(other.name)) return false;
	return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return name + "[" + clearance + "]";
    }

}
