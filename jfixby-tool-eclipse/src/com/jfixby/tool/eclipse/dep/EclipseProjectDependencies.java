
package com.jfixby.tool.eclipse.dep;

import java.io.IOException;

import com.jfixby.scarabei.api.collections.Collection;
import com.jfixby.scarabei.api.collections.Collections;
import com.jfixby.scarabei.api.collections.List;
import com.jfixby.scarabei.api.collections.Set;
import com.jfixby.scarabei.api.file.File;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.api.util.JUtils;

public class EclipseProjectDependencies {

	private final String project_name;
	private final File project_physical_location;

	final Set<String> source_folders = Collections.newSet();
	final Set<String> projects = Collections.newSet();
	final Set<String> jars = Collections.newSet();

	public EclipseProjectDependencies (final File project_physical_location) {
		this.project_physical_location = project_physical_location;
		this.project_name = project_physical_location.getName();
	}

	public static EclipseProjectDependencies extractFromClassPathFile (final File project_physical_location) throws IOException {
		// desktop_project_folder.listChildren().print();
		final EclipseProjectDependencies dep = new EclipseProjectDependencies(project_physical_location);
		final File classpath_file = project_physical_location.child(".classpath");
		if (!classpath_file.exists()) {
			L.e("File not found", classpath_file);
			return dep;
		}
		final String data = classpath_file.readToString();
		// L.d("classpath", data);
		final List<String> deps_list = JUtils.split(data, "<classpathentry");

		for (int i = 0; i < deps_list.size(); i++) {
			final String element = deps_list.getElementAt(i);
			{
				final String jar_path = getJarPath(element);
				if (jar_path != null) {
					// L.d("jar_path", jar_path);
					dep.addJarDependencyString(jar_path);
					continue;
				}
			}
			String src_path = getSrcPath(element);
			if (src_path != null) {
				if (src_path.charAt(0) == '/') {
					// L.d("project_path", src_path);
					src_path = src_path.replaceFirst("/", "");
					dep.addProjectDependencyString(src_path);
				} else {
					// L.d("src_path", src_path);
					dep.addSourceFolderDependencyString(src_path);
				}
			}
		}

		dep.source_folders.sort();
		dep.projects.sort();
		dep.jars.sort();

		return dep;
	}

	private void addSourceFolderDependencyString (final String path_string) {
		this.source_folders.add(path_string);
	}

	private void addProjectDependencyString (final String path_string) {
		this.projects.add(path_string);
	}

	private void addJarDependencyString (final String path_string) {
		this.jars.add(path_string);
	}

	private static String getJarPath (final String element) {
		final String prefix = "kind=\"lib\" path=\"";
		int begin_index = element.indexOf(prefix, 0);
		if (begin_index < 0) {
			return null;
		}

		begin_index = begin_index + prefix.length();
		final int end_index = element.indexOf("\"", begin_index);
		if (end_index < 0) {
			return null;
		}
		final String path = element.substring(begin_index, end_index);
		return path;
	}

	private static String getSrcPath (final String element) {
		final String prefix = "kind=\"src\" path=\"";
		int begin_index = element.indexOf(prefix, 0);
		if (begin_index < 0) {
			return null;
		}

		begin_index = begin_index + prefix.length();
		final int end_index = element.indexOf("\"", begin_index);
		if (end_index < 0) {
			return null;
		}
		final String path = element.substring(begin_index, end_index);
		return path;
	}

	public void print () {
		L.d("---[" + this.project_name + "]---------------------");
		if (this.source_folders.size() > 0) {
			this.source_folders.print("source folders");
		}
		if (this.projects.size() > 0) {
			this.projects.print("projects");
		}
		if (this.jars.size() > 0) {
			this.jars.print("jars");
		}
	}

	public Collection<String> getProjectsList () {
		return this.projects;
	}

	public Collection<String> getSourceFoldersList () {
		return this.source_folders;
	}

}
