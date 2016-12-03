
package com.jfixby.tool.eclipse.dep;

import java.io.IOException;

import com.jfixby.cmns.api.collections.Collection;
import com.jfixby.cmns.api.collections.Collections;
import com.jfixby.cmns.api.collections.List;
import com.jfixby.cmns.api.collections.Map;
import com.jfixby.cmns.api.err.Err;
import com.jfixby.cmns.api.file.ChildrenList;
import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.file.LocalFileSystem;
import com.jfixby.cmns.api.log.L;

public class EclipseWorkSpaceSettings {

	private final File workspace_folder;

	public EclipseWorkSpaceSettings (final File workspace_folder) {
		this.workspace_folder = workspace_folder;
	}

	public static EclipseWorkSpaceSettings readWorkspaceSettings (final File workspace_folder) throws IOException {
		final File projects_folder = workspace_folder.child(".metadata").child(".plugins").child("org.eclipse.core.resources")
			.child(".projects");

		final EclipseWorkSpaceSettings result = new EclipseWorkSpaceSettings(workspace_folder);
		final ChildrenList projects_list = projects_folder.listDirectChildren();
		for (int i = 0; i < projects_list.size(); i++) {
			final File element = projects_list.getElementAt(i);
			final File location_file = element.child(".location");
			final String data = location_file.readToString();
			final String prefix = "URI//file:/";
			int begin = data.indexOf(prefix);
			if (begin == -1) {
				continue;
			}
			begin = data.indexOf(prefix) + prefix.length();
			final char endchar = 0;
			final int end = data.indexOf(endchar, begin);
			final String location_path = data.substring(begin, end);
			// L.d("location_path", location_path);

			// URI uri = new URI(location_path);
			final String path = java.net.URLDecoder.decode(location_path, "UTF-8");
			// L.d("path", path);

			final File project_path = LocalFileSystem.newFile(path);
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

	private void addProjectLocation (final File project_path) throws IOException {
		final EclipseProjectInfo info = new EclipseProjectInfo(project_path);
		this.project_location.put(info.getProjectName(), info);
	}

	public void print () {
		L.d("workspace", this.workspace_folder);
		this.project_location.print("projects");
	}

	public EclipseProjectInfo getProjectInfo (final String core_project_name) {
		final EclipseProjectInfo info = this.project_location.get(core_project_name);
		if (info == null) {
			this.print();
			Err.reportError("Project info not found: " + core_project_name);
		}
		return info;
	}

	public Collection<EclipseProjectInfo> toEclipseProjectInfoList (final Collection<String> projectsList) {
		final List<EclipseProjectInfo> list = Collections.newList();
		for (final String projectName : projectsList) {
			final EclipseProjectInfo info = this.getProjectInfo(projectName);
			list.add(info);
		}
		return list;
	}

}
