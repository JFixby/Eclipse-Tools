package com.jfixby.tool.eclipse.dep;



import java.io.IOException;

import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.log.L;

public class EclipseProjectDependencies {

	public static EclipseProjectDependencies extractFromClassPathFile(File desktop_project_folder) throws IOException {
		desktop_project_folder.listChildren().print();
		File classpath_file = desktop_project_folder.child(".classpath");
		String data = classpath_file.readToString();
		L.d("classpath", data);

		EclipseProjectDependencies dep = new EclipseProjectDependencies();
		return dep;
	}

}
