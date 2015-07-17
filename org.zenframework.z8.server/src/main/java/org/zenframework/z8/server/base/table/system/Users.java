package org.zenframework.z8.server.base.table.system;

import java.util.LinkedHashMap;

import org.zenframework.z8.server.base.query.Query;
import org.zenframework.z8.server.base.table.Table;
import org.zenframework.z8.server.base.table.value.BoolField;
import org.zenframework.z8.server.base.table.value.IField;
import org.zenframework.z8.server.base.table.value.Link;
import org.zenframework.z8.server.base.table.value.StringField;
import org.zenframework.z8.server.base.table.value.TextField;
import org.zenframework.z8.server.engine.IAuthorityCenter;
import org.zenframework.z8.server.resources.Resources;
import org.zenframework.z8.server.runtime.IObject;
import org.zenframework.z8.server.runtime.RCollection;
import org.zenframework.z8.server.runtime.RLinkedHashMap;
import org.zenframework.z8.server.security.BuiltinUsers;
import org.zenframework.z8.server.security.IUser;
import org.zenframework.z8.server.security.SecurityGroup;
import org.zenframework.z8.server.types.bool;
import org.zenframework.z8.server.types.exception;
import org.zenframework.z8.server.types.guid;
import org.zenframework.z8.server.types.primary;
import org.zenframework.z8.server.types.string;

public class Users extends Table {
    final static public String TableName = "SystemUsers";

    static public final guid System = BuiltinUsers.System.guid();
    static public final guid Administrator = BuiltinUsers.Administrator.guid();
    
    static public class names {
        public final static String Password = "Password";
        public final static String SecurityGroup = "SecurityGroup";
        public final static String SecurityGroups = "SecurityGroups";
        public final static String Blocked = "Blocked";
        public final static String Phone = "Phone";
        public final static String Email = "Email";
        public final static String Settings = "Settings";
        public final static String Supervisor = "Supervisor";
    }

    static public class strings {
        public final static String Title = "Users.title";
        public final static String Name = "Users.name";
        public final static String Description = "Users.description";
        public final static String Password = "Users.password";
        public final static String SecurityGroup = "Users.securityGroup";
        public final static String Blocked = "Users.blocked";
        public final static String Phone = "Users.phone";
        public final static String Email = "Users.email";
        public final static String Settings = "Users.settings";

        public final static String DefaultName = "Users.name.default";
    }

    public static class CLASS<T extends Users> extends Table.CLASS<T> {
        public CLASS() {
            this(null);
        }

        public CLASS(IObject container) {
            super(container);
            setJavaClass(Users.class);
            setName(TableName);
            setDisplayName(Resources.get(Users.strings.Title));
        }

        @Override
        public Object newObject(IObject container) {
            return new Users(container);
        }
    }

    public SecurityGroups.CLASS<SecurityGroups> securityGroups = new SecurityGroups.CLASS<SecurityGroups>(this);

    public Link.CLASS<Link> securityGroup = new Link.CLASS<Link>(this);

    public PasswordField.CLASS<PasswordField> password = new PasswordField.CLASS<PasswordField>(this);
    public StringField.CLASS<StringField> phone = new StringField.CLASS<StringField>(this);
    public StringField.CLASS<StringField> email = new StringField.CLASS<StringField>(this);
    public BoolField.CLASS<BoolField> blocked = new BoolField.CLASS<BoolField>(this);
    public TextField.CLASS<TextField> settings = new TextField.CLASS<TextField>(this);

    public static class PasswordField extends StringField {
        public static class CLASS<T extends PasswordField> extends StringField.CLASS<T> {
            public CLASS(IObject container) {
                super(container);
                setJavaClass(PasswordField.class);
                setName(names.Password);
                setDisplayName(Resources.get(Users.strings.Password));
                setIndex("password");
            }

            @Override
            public Object newObject(IObject container) {
                return new PasswordField(container);
            }
        }

        public boolean mask = true;

        public PasswordField(IObject container) {
            super(container);
        }

        public String getPassword() {
            return super.get().string().get();
        }

        @Override
        public primary get() {
            return !mask || changed() ? super.get() : new string("*******");
        }
    }

    public static class NameField extends StringField {
        public static class CLASS<T extends NameField> extends StringField.CLASS<T> {
            public CLASS(IObject container) {
                super(container);
                setJavaClass(NameField.class);
            }

            @Override
            public Object newObject(IObject container) {
                return new NameField(container);
            }
        }

        public NameField(IObject container) {
            super(container);
        }

        @Override
        public primary getDefault() {
            Users users = (Users)getContainer();
            if(users.recordId.get().guid().equals(guid.NULL))
                return new string("");
            String userName = Resources.get(Users.strings.DefaultName) + getSequencer().next();

            return new string(userName);
        }
    }

    public Users() {
        this(null);
    }

    public Users(IObject container) {
        super(container);

        name = new NameField.CLASS<NameField>(this);
    }

    @Override
    public void constructor2() {
        super.constructor2();

        securityGroups.setIndex("securityGroups");
        name.setDisplayName(Resources.get(Users.strings.Name));
        name.setGendb_updatable(false);
        
        description.setDisplayName(Resources.get(strings.Description));

        securityGroup.setName(names.SecurityGroup);
        securityGroup.setIndex("securityGroup");
        securityGroup.setDisplayName(Resources.get(strings.SecurityGroup));

        phone.setName(names.Phone);
        phone.setIndex("phone");
        phone.setDisplayName(Resources.get(strings.Phone));

        email.setName(names.Email);
        email.setIndex("email");
        email.setDisplayName(Resources.get(strings.Email));

        blocked.setName(names.Blocked);
        blocked.setIndex("blocked");
        blocked.setDisplayName(Resources.get(strings.Blocked));

        settings.setName(names.Settings);
        settings.setIndex("settings");
        settings.setDisplayName(Resources.get(strings.Settings));

        id.get().visible = new bool(false);
        id1.get().visible = new bool(false);
        settings.get().visible = new bool(false);

        name.get().length.set(IAuthorityCenter.MaxLoginLength);
        name.get().unique.set(true);

        securityGroup.get().setDefault(SecurityGroup.Users.guid());
        securityGroup.get().operatorAssign(securityGroups);

        password.get().length.set(IAuthorityCenter.MaxPasswordLength);
        password.setExportable(false);

        phone.get().length.set(128);
        email.get().length.set(128);

        registerDataField(securityGroup);
        registerDataField(password);
        registerDataField(phone);
        registerDataField(email);
        registerDataField(blocked);
        registerDataField(settings);

        queries.add(securityGroups);

        links.add(securityGroup);

        {
            LinkedHashMap<IField, primary> record = new LinkedHashMap<IField, primary>();
            record.put(name.get(), new string(Resources.get("BuiltinUsers.System.name")));
            record.put(description.get(), new string(Resources.get("BuiltinUsers.System.description")));
            record.put(securityGroup.get(), SecurityGroup.Users.guid());
            addRecord(BuiltinUsers.System.guid(), record);
        }
        {
            LinkedHashMap<IField, primary> record = new LinkedHashMap<IField, primary>();
            record.put(name.get(), new string(Resources.get("BuiltinUsers.Administrator.name")));
            record.put(description.get(), new string(Resources.get("BuiltinUsers.Administrator.description")));
            record.put(securityGroup.get(), SecurityGroup.Administrators.guid());
            addRecord(BuiltinUsers.Administrator.guid(), record);
        }
    }

    @Override
    public void z8_beforeCreate(Query.CLASS<? extends Query> query, guid recordId, guid parentId,
            Query.CLASS<? extends Query> model, guid modelRecordId) {
        super.z8_beforeCreate(query, recordId, parentId, model, modelRecordId);
        // TODO В чём глубокий смысл данной проверки ???
        if(guid.NULL.equals(recordId) || BuiltinUsers.System.guid().equals(recordId)
                || BuiltinUsers.Administrator.guid().equals(recordId)) {
            return;
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void z8_beforeUpdate(Query.CLASS<? extends Query> query, guid recordId, RCollection changedFields,
            Query.CLASS<? extends Query> model, guid modelRecordId) {
        super.z8_beforeUpdate(query, recordId, changedFields, model, modelRecordId);

        if(changedFields.isEmpty())
            return;

        if((BuiltinUsers.Administrator.guid().equals(recordId) || BuiltinUsers.System.guid().equals(recordId)) && securityGroup.get().changed()) {
            throw new exception("Unable to change the security group of the builtin user.");
        }
    }

    @Override
    public void z8_beforeDestroy(Query.CLASS<? extends Query> query, guid recordId, Query.CLASS<? extends Query> model,
            guid modelRecordId) {
        super.z8_beforeDestroy(query, recordId, model, modelRecordId);

        if(BuiltinUsers.Administrator.guid().equals(recordId) || BuiltinUsers.System.guid().equals(recordId))
            throw new exception("Unable to delete builtin user !");
    }
    
    public boolean getExtraParameters(IUser user, RLinkedHashMap<string, primary> parameters) {
        return z8_getParameters(user.id(), new string(user.name()), parameters).get();
    }
    
    public bool z8_getParameters(guid id, string name, RLinkedHashMap<string, primary> parameters) {
        return new bool(true);
    }
}
