
package com.jfixby.tool.eclipse.dep;

import java.io.IOException;

import com.jfixby.cmns.api.err.Err;
import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.log.L;

public class EclipseProjectInfo {

	@Override
	public int hashCode () {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.project_name == null) ? 0 : this.project_name.hashCode());
		return result;
	}

	@Override
	public boolean equals (final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final EclipseProjectInfo other = (EclipseProjectInfo)obj;
		if (this.project_name == null) {
			if (other.project_name != null) {
				return false;
			}
		} else if (!this.project_name.equals(other.project_name)) {
			return false;
		}
		return true;
	}

	private final File project_path;
	private final String folder_name;
	private final String project_name;
	private EclipseProjectDependencies project_dependencies;

	public EclipseProjectInfo (final File project_path) throws IOException {
		this.project_path = project_path;

		final File project_file = project_path.child(".project");
		if (project_file == null) {
			Err.reportError("Project info not found: " + project_path);
		}
		this.folder_name = project_path.getName();
		final String data = project_file.readToString();
		final String NAME_OPEN = "<name>";
		final String NAME_CLOSE = "</name>";
		final int open = data.indexOf(NAME_OPEN) + NAME_OPEN.length();
		final int close = data.indexOf(NAME_CLOSE);
		this.project_name = data.substring(open, close);
		try {
			this.project_dependencies = EclipseProjectDependencies.extractFromClassPathFile(project_path);
		} catch (final IOException e) {
			L.e(e.getMessage().toString());
			// e.printStackTrace();
		}
	}

	@Override
	public String toString () {
		return "<" + this.project_name + "> " + this.project_path + "";
	}

	public String getProjectName () {
		return this.project_name;
	}

	public EclipseProjectDependencies getDependencies () {
		return this.project_dependencies;
	}

	public File getProjectPath () {
		return this.project_path;
	}

}
