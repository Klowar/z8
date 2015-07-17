package org.zenframework.z8.server.ie;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.zenframework.z8.ie.xml.ExportEntry;
import org.zenframework.z8.ie.xml.ExportEntry.Records;
import org.zenframework.z8.ie.xml.ObjectFactory;
import org.zenframework.z8.server.base.file.FileInfo;
import org.zenframework.z8.server.base.query.Query;
import org.zenframework.z8.server.base.table.value.AttachmentField;
import org.zenframework.z8.server.base.table.value.Field;
import org.zenframework.z8.server.logs.Trace;
import org.zenframework.z8.server.security.BuiltinUsers;
import org.zenframework.z8.server.types.guid;
import org.zenframework.z8.server.types.primary;

public class IeUtil {

    public static final String XML_ENCODING = "utf-8";

    private static final String RECORD_ID = "recordId";
    private static final JAXBContext JAXB_CONTEXT;

    private static final Collection<String> TO_STRING_FIELDS = Arrays.asList("createdAt", "modifiedAt", "createdBy",
            "modifiedBy", "id", "id1", "name", "description", "locked", "attachments");

    static {
        try {
            JAXB_CONTEXT = JAXBContext.newInstance(ObjectFactory.class.getPackage().getName());
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    private IeUtil() {/* Hide constructor*/}

    public static String toString(ExportEntry.Records.Record record) {
        StringBuilder str = new StringBuilder(1024);
        str.append(record.getTable()).append(" [").append("recordId: ").append(record.getRecordId());
        for (String id : TO_STRING_FIELDS) {
            ExportEntry.Records.Record.Field field = findField(record, id);
            if (field != null) {
                str.append(", ").append(id).append(": ").append(field.getValue());
            }
        }
        str.append(']');
        return str.toString();
    }

    public static ExportEntry.Records.Record tableToRecord(Query recordSet, ImportPolicy policy, boolean exportAttachments) {
        Records.Record record = new Records.Record();
        record.setTable(recordSet.classId());
        record.setRecordId(recordSet.recordId().toString());
        if (policy != null) {
            record.setPolicy(policy.name());
        }
        for (Field f : recordSet.getPrimaryFields()) {
            if (!RECORD_ID.equals(f.id()) && f.exportable()
                    && (exportAttachments || !(AttachmentField.class.isAssignableFrom(f.getClass())))) {
                String value = f.get().toString();
                if (!value.equals("")) {
                    Records.Record.Field field = new Records.Record.Field();
                    field.setId(f.id());
                    field.setValue(value);
                    record.getField().add(field);
                }
            }
        }
        return record;
    }

    public static void fillTableRecord(Query recordSet, ExportEntry.Records.Record record) {
        for (ExportEntry.Records.Record.Field xmlField : record.getField()) {
            Field field = recordSet.getFieldById(xmlField.getId());
            if (field != null) {
                field.set(primary.create(field.type(), xmlField.getValue()));
            } else {
                Trace.logEvent("WARNING: Incorrect record format. Table '" + recordSet.classId() + "' has no field '"
                        + xmlField.getId() + "'");
            }
        }
    }

    public static ExportEntry.Files.File fileInfoToFile(FileInfo fileInfo, ImportPolicy policy) {
        ExportEntry.Files.File file = new ExportEntry.Files.File();
        file.setName(fileInfo.name.get());
        file.setPath(fileInfo.path.get());
        file.setId(fileInfo.id.toString());
        if (policy != null) {
            file.setPolicy(policy.name());
        }
        return file;
    }

    public static FileInfo fileToFileInfo(ExportEntry.Files.File file) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.name.set(file.getName());
        fileInfo.path.set(file.getPath());
        fileInfo.id.set(file.getId());
        return fileInfo;
    }

    public static Collection<ExportEntry.Files.File> fileInfosToFiles(Collection<FileInfo> fileInfos, ImportPolicy policy) {
        Collection<ExportEntry.Files.File> files = new ArrayList<ExportEntry.Files.File>(fileInfos.size());
        for (FileInfo fileInfo : fileInfos) {
            files.add(fileInfoToFile(fileInfo, policy));
        }
        return files;
    }

    public static Collection<FileInfo> filesToFileInfos(Collection<ExportEntry.Files.File> files) {
        Collection<FileInfo> fileInfos = new ArrayList<FileInfo>(files.size());
        for (ExportEntry.Files.File file : files) {
            fileInfos.add(fileToFileInfo(file));
        }
        return fileInfos;
    }

    /*public static boolean isExportable(StringField.CLASS<? extends StringField> fieldClass) {
        if (fieldClass != null && fieldClass.exportable()) {
            StringField field = fieldClass.get();
            return !field.isNull() && field.string().get().length() > 0;
        } else {
            return false;
        }
    }

    public static boolean isExportable(GuidField.CLASS<? extends GuidField> fieldClass) {
        if (fieldClass != null && fieldClass.exportable()) {
            GuidField field = fieldClass.get();
            return field != null && !field.isNull() && field.exportable();
        } else {
            return false;
        }
    }

    public static boolean isExportable(DatetimeField.CLASS<? extends DatetimeField> fieldClass) {
        if (fieldClass != null && fieldClass.exportable()) {
            DatetimeField field = fieldClass.get();
            return field != null && !field.isNull() && field.exportable();
        } else {
            return false;
        }
    }

    public static boolean isExportable(BoolExpression.CLASS<? extends BoolExpression> fieldClass) {
        if (fieldClass != null && fieldClass.exportable()) {
            BoolExpression field = fieldClass.get();
            return field != null && !field.isNull() && field.exportable();
        } else {
            return false;
        }
    }*/

    public static ExportEntry.Records.Record.Field findField(ExportEntry.Records.Record record, String id) {
        for (ExportEntry.Records.Record.Field field : record.getField()) {
            if (field.getId() != null && field.getId().equals(id)) {
                return field;
            }
        }
        return null;
    }

    public static boolean isBuiltinRecord(guid recordId) {
        return guid.NULL.equals(recordId) || BuiltinUsers.System.guid().equals(recordId)
                || BuiltinUsers.Administrator.guid().equals(recordId);
    }

    public static void marshalExportEntry(ExportEntry entry, Writer out) throws JAXBException, UnsupportedEncodingException {
        Marshaller marshaller = getMarshaller(JAXB_CONTEXT);
        marshaller.marshal(entry, out);
    }

    public static String marshalExportEntry(ExportEntry entry) throws JAXBException, UnsupportedEncodingException {
        StringWriter out = new StringWriter();
        marshalExportEntry(entry, out);
        return out.toString();
    }

    public static ExportEntry unmarshalExportEntry(Reader in) throws JAXBException {
        Unmarshaller unmarshaller = getUnmarshaller(JAXB_CONTEXT);
        Object result = unmarshaller.unmarshal(in);
        if (result instanceof JAXBElement) {
            result = ((JAXBElement<?>) result).getValue();
        }
        if (result instanceof ExportEntry) {
            return (ExportEntry) result;
        } else {
            throw new JAXBException("Incorrect ExportEntry class: " + result.getClass());
        }
    }

    public static ExportEntry unmarshalExportEntry(String str) throws JAXBException {
        return unmarshalExportEntry(new StringReader(str));
    }

    public static Marshaller getMarshaller(JAXBContext jaxbContext) throws JAXBException {
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_ENCODING, XML_ENCODING);
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        return marshaller;
    }

    public static Unmarshaller getUnmarshaller(JAXBContext jaxbContext) throws JAXBException {
        return jaxbContext.createUnmarshaller();
    }

}
