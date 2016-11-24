package com.rldevel;

public class PatternConsole {

	private static String[] m_args = null;
	
	public static void main(String[] args) {
		m_args = args;
		init();
	}
	
	private static void init(){
		if (m_args[0].compareToIgnoreCase("create")==0){
			create();
		}
	}
	
	private static boolean create(){
		if(m_args[1].startsWith("-")){
			if(m_args[1].toCharArray()[1]=='f'){
				//full [DAO] + [Repository] + [BackingBean] + [View]
				return createFull();
			}else if (m_args[1].toCharArray()[1]=='m'){
				//minimun [BackingBean]
			}
		}
	}
	
	private static boolean createFull(){
		CreateTask crt = new Createtask();
		return cret.execute();
	}

}
