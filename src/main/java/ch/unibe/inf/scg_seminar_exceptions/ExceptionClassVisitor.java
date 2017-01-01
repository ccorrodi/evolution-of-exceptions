package ch.unibe.inf.scg_seminar_exceptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class ExceptionClassVisitor {
	public static ArrayList<ExceptionClass> listClassesDerivedFromException(File projectDir) {

		ArrayList<ExceptionClass> exceptionClasses = new ArrayList<ExceptionClass>();

		JavaExceptionNames jen = new JavaExceptionNames();
		ArrayList<String> checkedExceptionNames = jen.getExceptionNamesFrom("checked_exceptions.txt");
		for (String name : checkedExceptionNames) {
			exceptionClasses.add(new ExceptionClass(name, true, Scope.STANDARD));
		}

		ArrayList<String> uncheckedExceptionNames = jen.getExceptionNamesFrom("unchecked_exceptions.txt");
		for (String name : uncheckedExceptionNames) {
			exceptionClasses.add(new ExceptionClass(name, false, Scope.STANDARD));
		}


		ArrayList<HierarchieEntry> classTree = new ArrayList<HierarchieEntry>();
		DatabaseManager dbManager = DatabaseManager.getInstance();

		new FileExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
			try {
				new VoidVisitorAdapter<Object>() {
					@Override
					public void visit(ClassOrInterfaceDeclaration n, Object arg) {
						super.visit(n, arg);
						classTree.add(new HierarchieEntry(n, file));
					}
				}.visit(JavaParser.parse(file), null);
			} catch (Exception e) {
				Util.logException(e);
				dbManager.addParserException(path, e.toString(), "exception class", "");
			}
		}).explore(projectDir);

		System.out.println(classTree.size());

		for (HierarchieEntry entry : classTree) {
			try {
				List<ClassOrInterfaceType> extendTypes = ((ClassOrInterfaceDeclaration) entry.getNode()).getExtends();

				for (ClassOrInterfaceType coit : extendTypes) {
					classTree.stream().filter(o -> o.getClassName().equals(coit.getName())).forEach(
							o -> {
								entry.setParent(o);
							}
					);
				}

			} catch (Exception e) {
				Util.logException(e);
				dbManager.addParserException("build tree", e.toString(), "exception class", "");
			}
		}

		for (HierarchieEntry hierarchieEntry : classTree) {
			HierarchieEntry tmp = hierarchieEntry;
			ArrayList<HierarchieEntry> pathHistory = new ArrayList<>();

			while (tmp.hasNext()) {
				if (pathHistory.contains(tmp)) {
					break; // loop detected
				} else {
					pathHistory.add(tmp);
				}
				tmp = tmp.next();
				if (uncheckedExceptionNames.contains(tmp.getClassName())) {
					exceptionClasses.add(new ExceptionClass(hierarchieEntry.getClassName(), false, Scope.CUSTOM));
					dbManager.addExceptionClass(hierarchieEntry.getFile().getPath(), hierarchieEntry.getClassName(), "unchecked", hierarchieEntry.getNode().toString().replaceAll("\0", ""));
					//System.out.println("Unchecked Exception: " + hierarchieEntry.getClassName());
					break;
				} else if (checkedExceptionNames.contains(tmp.getClassName())) {
					exceptionClasses.add(new ExceptionClass(hierarchieEntry.getClassName(), true, Scope.CUSTOM));
					dbManager.addExceptionClass(hierarchieEntry.getFile().getPath(), hierarchieEntry.getClassName(), "checked", hierarchieEntry.getNode().toString().replaceAll("\0", ""));
					//System.out.println("Checked Exception: " + hierarchieEntry.getClassName());
					break;
				}
			}
		}
		return exceptionClasses;
	}
}
