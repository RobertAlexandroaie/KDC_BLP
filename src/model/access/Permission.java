package model.access;

public class Permission {
    private boolean read;
    private boolean write;
    private boolean exec;

    public Permission() {
	read = false;
	write = false;
	exec = false;
    }

    public Permission(String... permissions) {
	this();
	for (String permission : permissions) {
	    set(permission);
	}
    }

    /**
     * @return the read
     */
    public boolean isRead() {
	return read;
    }

    /**
     * @param read
     *            the read to set
     */
    public void setRead(boolean read) {
	this.read = read;
    }

    /**
     * @return the write
     */
    public boolean isWrite() {
	return write;
    }

    /**
     * @param write
     *            the write to set
     */
    public void setWrite(boolean write) {
	this.write = write;
    }

    /**
     * @return the exec
     */
    public boolean isExec() {
	return exec;
    }

    /**
     * @param exec
     *            the exec to set
     */
    public void setExec(boolean exec) {
	this.exec = exec;
    }

    public void set(String right) {
	switch (right.toLowerCase()) {
	case "read":
	case "r":
	    read = true;
	    break;
	case "write":
	case "w":
	    write = true;
	    break;
	case "exec":
	case "e":
	    exec = true;
	    break;
	}
    }
}
