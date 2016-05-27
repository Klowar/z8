package org.zenframework.z8.server.base.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.zenframework.z8.server.json.Json;
import org.zenframework.z8.server.json.parser.JsonArray;
import org.zenframework.z8.server.json.parser.JsonObject;
import org.zenframework.z8.server.resources.Resources;
import org.zenframework.z8.server.runtime.IObject;
import org.zenframework.z8.server.runtime.OBJECT;
import org.zenframework.z8.server.runtime.RCollection;
import org.zenframework.z8.server.types.datetime;
import org.zenframework.z8.server.types.guid;
import org.zenframework.z8.server.types.string;
import org.zenframework.z8.server.utils.IOUtils;

public class FileInfo extends OBJECT implements Serializable {

	private static final long serialVersionUID = -4474455212423780540L;
	private static final byte FILE_INFO_VERSION = 1;

	private static final String PROP_DEFAULT_READ = "z8.serialization.FileInfo.defaultReadObject";
	private static final String PROP_DEFAULT_WRITE = "z8.serialization.FileInfo.defaultWriteObject";

	private static final boolean DEFAULT_READ = Boolean.parseBoolean(System.getProperty(PROP_DEFAULT_READ));
	private static final boolean DEFAULT_WRITE = Boolean.parseBoolean(System.getProperty(PROP_DEFAULT_WRITE));

	public static boolean isDefaultRead() {
		return DEFAULT_READ;
	}

	public static boolean isDefaultWrite() {
		return DEFAULT_WRITE;
	}

	public string instanceId = new string();
	public string name = new string();
	public string path = new string();
	public string type = new string();
	public datetime time = new datetime();
	public guid id = new guid();

	public transient FileItem file;
	public transient Status status = Status.LOCAL;

	public transient JsonObject json;

	public static enum Status {

		LOCAL("Files.status.local", ""), REMOTE("Files.status.remote", "remote"), REQUEST_SENT("Files.status.requestSent",
				"requestSent");

		private final String id;
		private final String value;

		private Status(String id, String value) {
			this.id = id;
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public String getText() {
			return Resources.get(id);
		}

		public static Status getStatus(String value) {
			for (Status status : values()) {
				if (status.value.equals(value))
					return status;
			}
			return LOCAL;
		}

	}

	public static class CLASS<T extends FileInfo> extends OBJECT.CLASS<T> {
		public CLASS() {
			this(null);
		}

		public CLASS(IObject container) {
			super(container);
			setJavaClass(FileInfo.class);
			setAttribute(Native, FileInfo.class.getCanonicalName());
		}

		@Override
		public Object newObject(IObject container) {
			return new FileInfo(container);
		}
	}

	public FileInfo() {
		super();
	}

	public FileInfo(String id) {
		super();
		this.id = new guid(id);
	}

	public FileInfo(IObject container) {
		super(container);
	}

	public FileInfo(FileItem file) throws IOException {
		this(file, null, null);
	}

	public FileInfo(FileItem file, String instanceId, String path) {
		super();
		this.instanceId = new string(instanceId);
		this.path = new string(path);
		this.name = new string(file.getName());
		this.file = file;
	}

	public FileInfo(guid id, String name, String instanceId, String path) {
		super();
		this.id = id;
		this.instanceId = new string(instanceId);
		this.path = new string(path);
		this.name = new string(name);
	}

	protected FileInfo(JsonObject json) {
		super();
		set(json);
	}

	public void set(FileInfo fileInfo) {
		this.instanceId = fileInfo.instanceId;
		this.name = fileInfo.name;
		this.path = fileInfo.path;
		this.type = fileInfo.type;
		this.time = fileInfo.time;
		this.id = fileInfo.id;
		this.file = fileInfo.file;
		this.status = fileInfo.status;
		this.json = fileInfo.json;
	}

	protected void set(JsonObject json) {
		path = new string(json.getString(json.has(Json.file) ? Json.file : Json.path));
		name = new string(json.has(Json.name) ? json.getString(Json.name) : "");
		time = new datetime(json.has(Json.time) ? json.getString(Json.time) : "");
		type = new string(json.has(Json.type) ? json.getString(Json.type) : "");
		id = new guid(json.has(Json.id) ? json.getString(Json.id) : "");
		instanceId = new string(json.has(Json.instanceId) ? json.getString(Json.instanceId) : "");

		this.json = json;
	}

	public static List<FileInfo> parseArray(String json) {
		List<FileInfo> result = new ArrayList<FileInfo>();

		if (!json.isEmpty()) {
			JsonArray array = new JsonArray(json);

			for (int i = 0; i < array.length(); i++)
				result.add(parse(array.getJsonObject(i)));
		}
		return result;
	}

	public static String toJson(Collection<FileInfo> fileInfos) {
		JsonArray array = new JsonArray();

		for (FileInfo file : fileInfos)
			array.add(file.toJsonObject());

		return array.toString();
	}

	public static FileInfo parse(JsonObject json) {
		return new FileInfo(json);
	}

	public JsonObject toJsonObject() {
		if (json == null) {
			json = new JsonObject();
			json.put(Json.name, name);
			// json.put(Json.time, time);
			json.put(Json.type, type);
			json.put(Json.path, path);
			json.put(Json.id, id);
			json.put(Json.instanceId, instanceId);
		}
		return json;
	}

	public static RCollection<FileInfo.CLASS<? extends FileInfo>> z8_parse(string json) {
		RCollection<FileInfo.CLASS<? extends FileInfo>> result = new RCollection<FileInfo.CLASS<? extends FileInfo>>();

		JsonArray array = new JsonArray(json.get());

		for (int index = 0; index < array.length(); index++) {
			JsonObject object = array.getJsonObject(index);

			FileInfo.CLASS<FileInfo> fileInfo = new FileInfo.CLASS<FileInfo>();
			fileInfo.get().set(object);

			result.add(fileInfo);
		}
		return result;
	}

	static public string z8_toJson(RCollection<FileInfo.CLASS<? extends FileInfo>> classes) {
		return new string(toJson(CLASS.asList(classes)));
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}

	@Override
	public boolean equals(Object object) {
		return object instanceof FileInfo && id != null && id.equals(((FileInfo) object).id);
	}

	@Override
	public String toString() {
		return toJsonObject().toString();
	}

	public InputStream getInputStream() {
		try {
			return file == null ? null : file.getInputStream();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public OutputStream getOutputStream() {
		try {
			return file == null ? null : file.getOutputStream();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void writeObject(ObjectOutputStream outputStream) throws IOException {
		outputStream.defaultWriteObject();
		if (DEFAULT_WRITE)
			return;

		outputStream.writeByte(FILE_INFO_VERSION);
		outputStream.writeBoolean(file != null);

		if (file != null) {
			InputStream inputStream = file.getInputStream();

			try {
				IOUtils.copy(inputStream, outputStream, false);
			} finally {
				IOUtils.closeQuietly(inputStream);
			}
		}
	}

	private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
		inputStream.defaultReadObject();
		if (DEFAULT_READ)
			return;

		// Read FileInfo version - for future use
		@SuppressWarnings("unused")
		byte version = inputStream.readByte();

		if (inputStream.readBoolean()) {
			file = FilesFactory.createFileItem(name.get());

			OutputStream outputStream = file.getOutputStream();

			try {
				IOUtils.copy(inputStream, outputStream, false);
			} finally {
				IOUtils.closeQuietly(outputStream);
			}
		}
	}

}
