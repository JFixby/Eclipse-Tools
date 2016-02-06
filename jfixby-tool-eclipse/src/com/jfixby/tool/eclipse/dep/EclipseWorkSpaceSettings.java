package com.jfixby.tool.eclipse.dep;

import java.io.IOException;

import com.jfixby.cmns.api.collections.Collections;
import com.jfixby.cmns.api.collections.Map;
import com.jfixby.cmns.api.file.ChildrenList;
import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.file.LocalFileSystem;
import com.jfixby.cmns.api.log.L;

public class EclipseWorkSpaceSettings {

	private File workspace_folder;

	public EclipseWorkSpaceSettings(File workspace_folder) {
		this.workspace_folder = workspace_folder;
	}

	public static EclipseWorkSpaceSettings readWorkspaceSettings(File workspace_folder) throws IOException {
		File projects_folder = workspace_folder.child(".metadata").child(".plugins").child("org.eclipse.core.resources")
				.child(".projects");

		EclipseWorkSpaceSettings result = new EclipseWorkSpaceSettings(workspace_folder);
		ChildrenList projects_list = projects_folder.listChildren();
		for (int i = 0; i < projects_list.size(); i++) {
			File element = projects_list.getElementAt(i);
			File location_file = element.child(".location");
			String data = location_file.readToString();
			String prefix = "URI//file:/";
			int begin = data.indexOf(prefix);
			if (begin == -1) {
				continue;
			}
			begin = data.indexOf(prefix) + prefix.length();
			char endchar = 0;
			int end = data.indexOf(endchar, begin);
			String location_path = data.substring(begin, end);
			// L.d("location_path", location_path);

			// URI uri = new URI(location_path);
			String path = java.net.URLDecoder.decode(location_path, "UTF-8");
			// L.d("path", path);

			File project_path = LocalFileSystem.newFile(path);
			// L.d("project_path.exists() " + project_path,
			// project_path.exists());
			if (project_path.exists()) {
				result.addProjectLocation(project_path);
			} else {
				L.d("NOT FOUND", project_path);
			}
		}

		return result;
	}

	final Map<String, EclipseProjectInfo> project_location = Collections.newMap();

	private void addProjectLocation(File project_path) throws IOException {
		EclipseProjectInfo info = new EclipseProjectInfo(project_path);
		project_location.put(info.getProjectName(), info);
	}

	public void print() {
		L.d("workspace", this.workspace_folder);
		this.project_location.print("projects");
	}

	public EclipseProjectInfo getProjectInfo(String core_project_name) {
		EclipseProjectInfo info = this.project_location.get(core_project_name);
		if (info == null) {
			this.print();
			throw new Error("Project info not found: " + core_project_name);
		}
		return info;
	}

}
