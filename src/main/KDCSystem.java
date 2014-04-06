package main;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.crypto.KeyGenerator;

import model.Clearance;
import model.Server;
import model.Service;
import model.User;
import model.access.MAC;
import model.access.Permission;
import model.services.ServiceSvc;
import model.services.UserSvc;
import util.Constants;

public class KDCSystem {
    private MAC mac;

    private HashMap<User, Key> KUTList;
    private HashMap<Service, Key> KSTList;

    private List<User> users;
    private List<Service> services;

    public KDCSystem(Server server) {
	mac = new MAC();

	KUTList = new HashMap<>();
	KSTList = new HashMap<>();

	users = new ArrayList<User>();
	services = new ArrayList<Service>();

	initMAC();
	server.setSystem(this);
    }

    private void addUsers() {
	Key key;
	try {
	    User user = new User("Robert", Clearance.U);
	    key = KeyGenerator.getInstance(Constants.DES3).generateKey();
	    KUTList.put(user, key);
	    users.add(user);

	    user = new User("Alex", Clearance.C);
	    key = KeyGenerator.getInstance(Constants.DES3).generateKey();
	    KUTList.put(user, key);
	    users.add(user);

	    user = new User("Claudiu", Clearance.S);
	    key = KeyGenerator.getInstance(Constants.DES3).generateKey();
	    KUTList.put(user, key);
	    users.add(user);

	    user = new User("Adi", Clearance.TS);
	    key = KeyGenerator.getInstance(Constants.DES3).generateKey();
	    KUTList.put(user, key);
	    users.add(user);

	    user = new User("Florin", Clearance.U);
	    key = KeyGenerator.getInstance(Constants.DES3).generateKey();
	    KUTList.put(user, key);
	    users.add(user);

	    user = new User("NickFury", Clearance.SHIELD);
	    key = KeyGenerator.getInstance(Constants.DES3).generateKey();
	    KUTList.put(user, key);
	    users.add(user);
	} catch (NoSuchAlgorithmException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

    private void addServices() {
	Key key;
	try {
	    Service service = new Service("Service1", Clearance.U);
	    key = KeyGenerator.getInstance(Constants.DES3).generateKey();
	    KSTList.put(service, key);
	    services.add(service);

	    service = new Service("Service2", Clearance.C);
	    key = KeyGenerator.getInstance(Constants.DES3).generateKey();
	    KSTList.put(service, key);
	    services.add(service);

	    service = new Service("Service3", Clearance.S);
	    key = KeyGenerator.getInstance(Constants.DES3).generateKey();
	    KSTList.put(service, key);
	    services.add(service);

	    service = new Service("Service4", Clearance.TS);
	    key = KeyGenerator.getInstance(Constants.DES3).generateKey();
	    KSTList.put(service, key);
	    services.add(service);

	    service = new Service("DeployHulk", Clearance.SHIELD);
	    key = KeyGenerator.getInstance(Constants.DES3).generateKey();
	    KSTList.put(service, key);
	    services.add(service);
	} catch (NoSuchAlgorithmException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

    private void initMAC() {
	addUsers();
	addServices();
	Random howMany = new Random();
	Random whatPermission = new Random();
	ArrayList<String> permissions = new ArrayList<>();
	permissions.add("r");
	permissions.add("w");
	permissions.add("e");
	for (User user : users) {
	    for (Service service : services) {
		Permission permission = new Permission();
		for (int i = 1; i < howMany.nextInt(3) + 1; i++) {
		    permission.set(permissions.get(whatPermission.nextInt(3)));
		}
		mac.add(user, service, permission);
	    }
	}
    }

    public Key serviceKeyByServiceName(Service service) {
	return KSTList.get(service);
    }

    public Key serviceKeyByServiceName(String name) {
	return serviceKeyByServiceName(new Service(name));
    }

    public Key userKeyByUserName(User user) {
	return KUTList.get(user);
    }

    public Key userKeyByUserName(String name) {
	return userKeyByUserName(new User(name));
    }

    public boolean containsUser(String user) {
	return users.contains(new User(user));
    }

    public boolean containsService(String service) {
	return services.contains(new Service(service));
    }

    public boolean isPermitted(String user, String service) {
	return mac.isPermitted(new User(user), new Service(service));
    }

    public void userAccessService(String userName, String serviceName) {
	Key KUT = userKeyByUserName(userName);

	UserSvc userSvc = new UserSvc(KUT, serviceName, userName, this);

	new Thread(userSvc).start();

    }
}
