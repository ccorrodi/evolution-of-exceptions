package ch.unibe.inf.scg_seminar_exceptions;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

public class HierarchieEntry implements Iterator<HierarchieEntry>{
	
	private File file;
	private Node node;
	private String className;
	private HierarchieEntry parent;

	public HierarchieEntry(Node node, File file) {
		this.node = node;
		this.className = ((ClassOrInterfaceDeclaration)node).getName();
		this.file = file;
	}
	
	public HierarchieEntry(String className) {
		this.className = className;
	}
	
	public Node getNode() {
		return node;
	}

	public void setNode(ClassOrInterfaceDeclaration node) {
		this.node = node;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public HierarchieEntry getParent() {

		if(parent == null){
			try{
				if(!((ClassOrInterfaceDeclaration)node).getExtends().isEmpty()){
					return new HierarchieEntry(((ClassOrInterfaceDeclaration)node).getExtends().get(0).toString());
				}
			} catch (Exception e){
				return null;
			}

			return null;
		}
		return parent;
	}

	public void setParent(HierarchieEntry parent) {
		this.parent = parent;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	@Override
	public boolean hasNext() {
		if(getParent()==null){
			return false;
		} else {
			return true;
		}
	}

	@Override
	public HierarchieEntry next() {
		return getParent();
	}
}
