package com.jfixby.tool.eclipse.dep;

import java.io.IOException;

import com.jfixby.cmns.api.collections.Collections;
import com.jfixby.cmns.api.collections.List;
import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.util.JUtils;

public class EclipseProjectDependencies {

	private String project_name;

	public EclipseProjectDependencies(String project_name) {
		this.project_name = project_name;
	}

	public static EclipseProjectDependencies extractFromClassPathFile(File desktop_project_folder) throws IOException {
		// desktop_project_folder.listChildren().print();
		File classpath_file = desktop_project_folder.child(".classpath");
		String data = classpath_file.readToString();
		// L.d("classpath", data);
		List<String> deps_list = JUtils.split(data, "<classpathentry");
		EclipseProjectDependencies dep = new EclipseProjectDependencies(desktop_project_folder.getName());
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

	final List<String> source_folders = Collections.newList();
	final List<String> projects = Collections.newList();
	final List<String> jars = Collections.newList();

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

	public List<String> getProjectsList() {
		return this.projects;
	}

}
