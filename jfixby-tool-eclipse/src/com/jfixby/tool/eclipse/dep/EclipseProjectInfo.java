package com.jfixby.tool.eclipse.dep;

import java.io.IOException;

import com.jfixby.cmns.api.err.Err;
import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.log.L;

public class EclipseProjectInfo {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((project_name == null) ? 0 : project_name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EclipseProjectInfo other = (EclipseProjectInfo) obj;
		if (project_name == null) {
			if (other.project_name != null)
				return false;
		} else if (!project_name.equals(other.project_name))
			return false;
		return true;
	}

	private File project_path;
	private String folder_name;
	private String project_name;
	private EclipseProjectDependencies project_dependencies;

	public EclipseProjectInfo(File project_path) throws IOException {
		this.project_path = project_path;

		File project_file = project_path.child(".project");
		if (project_file == null) {
			Err.reportError("Project info not found: " + project_path);
		}
		folder_name = project_path.getName();
		String data = project_file.readToString();
		String NAME_OPEN = "<name>";
		String NAME_CLOSE = "</name>";
		int open = data.indexOf(NAME_OPEN) + NAME_OPEN.length();
		int close = data.indexOf(NAME_CLOSE);
		project_name = data.substring(open, close);
		try {
			project_dependencies = EclipseProjectDependencies.extractFromClassPathFile(project_path);
		} catch (IOException e) {
			L.e(e.getMessage().toString());
			// e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "" + project_name + " " + project_path + "]";
	}

	public String getProjectName() {
		return this.project_name;
	}

	public EclipseProjectDependencies getDependencies() {
		return this.project_dependencies;
	}

}
