package com.jfixby.tool.eclipse.dep;

import java.io.IOException;

import com.jfixby.cmns.api.collections.Collection;
import com.jfixby.cmns.api.collections.Collections;
import com.jfixby.cmns.api.collections.List;
import com.jfixby.cmns.api.collections.Set;
import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.util.JUtils;

public class EclipseProjectDependencies {

	private String project_name;
	private File project_physical_location;

	public EclipseProjectDependencies(File project_physical_location) {
		this.project_physical_location = project_physical_location;
		this.project_name = project_physical_location.getName();
	}

	public static EclipseProjectDependencies extractFromClassPathFile(File project_physical_location)
			throws IOException {
		// desktop_project_folder.listChildren().print();
		File classpath_file = project_physical_location.child(".classpath");
		String data = classpath_file.readToString();
		// L.d("classpath", data);
		List<String> deps_list = JUtils.split(data, "<classpathentry");
		EclipseProjectDependencies dep = new EclipseProjectDependencies(project_physical_location);
		for (int i = 0; i < deps_list.size(); i++) {
			String element = deps_list.getElementAt(i);
			{
				String jar_path = getJarPath(element);
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

	final Set<String> source_folders = Collections.newSet();
	final Set<String> projects = Collections.newSet();
	final Set<String> jars = Collections.newSet();

	private void addSourceFolderDependencyString(String path_string) {
		source_folders.add(path_string);
	}

	private void addProjectDependencyString(String path_string) {
		projects.add(path_string);
	}

	private void addJarDependencyString(String path_string) {
		jars.add(path_string);
	}

	private static String getJarPath(String element) {
		String prefix = "kind=\"lib\" path=\"";
		int begin_index = element.indexOf(prefix, 0);
		if (begin_index < 0) {
			return null;
		}

		begin_index = begin_index + prefix.length();
		int end_index = element.indexOf("\"", begin_index);
		if (end_index < 0) {
			return null;
		}
		String path = element.substring(begin_index, end_index);
		return path;
	}

	private static String getSrcPath(String element) {
		String prefix = "kind=\"src\" path=\"";
		int begin_index = element.indexOf(prefix, 0);
		if (begin_index < 0) {
			return null;
		}

		begin_index = begin_index + prefix.length();
		int end_index = element.indexOf("\"", begin_index);
		if (end_index < 0) {
			return null;
		}
		String path = element.substring(begin_index, end_index);
		return path;
	}

	public void print() {
		L.d("---[" + project_name + "]---------------------");
		source_folders.print("source folders");
		projects.print("projects");
		jars.print("jars");
	}

	public Collection<String> getProjectsList() {
		return this.projects;
	}

}
