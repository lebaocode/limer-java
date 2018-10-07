package com.lebaor.utils;

import java.sql.ResultSet;
import java.util.LinkedList;

import org.json.JSONObject;

import com.lebaor.dbutils.ResultSetHandler;
import com.lebaor.limer.data.User;
import com.lebaor.limer.web.data.WebBook;

public class DBJavaGenUtil {
	public static void gen1(String[] lines, String className)throws Exception {
		LinkedList<String> varNameList = new LinkedList<String>();
		LinkedList<String> columnNameList = new LinkedList<String>();
		LinkedList<String> typeList = new LinkedList<String>();
		
		for (String line : lines) {
			if (line.trim().length() == 0) continue;
			int index = line.indexOf("//");
			if (index != -1) {
				line = line.substring(0, index);
			}
			
			String[] arr = line.trim().split(" +");
			String type = arr[0].trim();
			typeList.add(type);
			
			String varName = arr[1].trim();
			if (varName.endsWith(";")) varName = varName.substring(0, varName.length() - 1);
			varNameList.add(varName);
			
			
			String columnName = varName;
			if (varName.equals("id")) {
				columnNameList.add(columnName);
				System.out.println("\"  `id` bigint(20) NOT NULL auto_increment,\\r\\n\" +");
				continue;
			} 
				
			for (int i = 0; i < columnName.length(); i++) {
				char ch = columnName.charAt(i);
				if (ch >= 'A' && ch <= 'Z') {
					columnName = columnName.substring(0, i) + "_" + columnName.substring(i,i+1).toLowerCase() + columnName.substring(i+1);
					break;
				}
			}
			
			for (int i = 0; i < columnName.length(); i++) {
				char ch = columnName.charAt(i);
				if (ch >= 'A' && ch <= 'Z') {
					columnName = columnName.substring(0, i) + "_" + columnName.substring(i).toLowerCase();
					break;
				}
			}
			

			String dbType = "";
			String defaultValue = "NULL";
			if (type.equals("int")) {
				dbType = "smallint(2)";
				defaultValue = "0";
			} else if (type.equals("String")) {
				dbType = "varchar(255)";
			} else if (type.equals("long")) {
				if (varName.endsWith("Time")) {
					dbType = "datetime";
				} else {
					dbType = "bigint(20)";
					defaultValue = "0";
				}
			}  else if (type.equals("long")) {
				dbType = "bigint(20)";
			} 
			columnNameList.add(columnName);
			System.out.println("				\"  `"+ columnName +"` "+ dbType +" default "+ defaultValue +",\\r\\n\" +");
			
			
		}
		
		System.out.println();
		int n = 0;
		for (String s : columnNameList) {
			if (n < columnNameList.size() - 1)	System.out.print("\""+ s+"\", ");
			else System.out.print("\""+ s+"\"");
			n++;
			if (n % 4 == 0) {
				System.out.print("\n");
			}
		}
		System.out.println();
		System.out.println();
		
		n = 0;
		for (String s : varNameList) {
			String fs = "o.get"+ s.substring(0,1).toUpperCase()+ s.substring(1) +"()";
			if (s.endsWith("Time")) {
				fs = "TextUtil.formatTime(" + fs + ")";
			}
			
			if (n < varNameList.size() - 1)	 fs = fs + ",";
			n++;
			System.out.println(fs);
		}
		System.out.println();
		
		//User u = new User();
		//u.setMobile(rs.getString(2));
		//java.sql.Timestamp d = rs.getTimestamp(8);
		//u.setRegisterTime(d != null ? d.getTime() : 0);
		n = 0;
		System.out.println("java.sql.Timestamp d;");
		for (String s : typeList) {
			if (n == 0) {
				System.out.println(className + " o = new " + className + "();");
			}
			String v = varNameList.get(n);
			String fs = s;
			if (s.equals("int")) {
				fs = "Int";
			} else if(s.equals("long")) {
				fs = "Long";
			}
			if (v.endsWith("Time")) {
				System.out.println("d = rs.getTimestamp("+ (n+1) +");");
				System.out.println("o.set"+ v.substring(0,1).toUpperCase()+ v.substring(1) +"(d != null ? d.getTime() : 0);");
			} else {
				System.out.println("o.set" + v.substring(0,1).toUpperCase()+ v.substring(1) +"(rs.get"+ fs  +"("+ (n+1) +"));" );
			}
			n++;
		}
		System.out.println("return o;");
		System.out.println();
		
		
		System.out.println("\tpublic String toJSON() {\n" + 
				"		try {\n"+
				"			JSONObject o = new JSONObject();");
		
		n=0;
		for (String s : varNameList) {
			System.out.println("\t\t\to.put(\""+ s +"\", "+ s +");");
		}
		
		System.out.println(
				"			return o.toString();\n" +
				"		} catch (Exception e) {\n" + 
				"			return \"{error: 'format error.'}\";\n" + 
				"		}\n" + 
				"		\n" + 
				"	}\n");
		
		System.out.println("\tpublic static "+ className +" parseJSON(String s) {\n" + 
				"		try {\n" + 
				"			return parseJSON(new JSONObject(s));\n" + 
				"		} catch (Exception e) {\n" + 
				"			return null;\n" + 
				"		}\n" + 
				"	}\n");
		System.out.println();
		
		System.out.println("\tpublic static "+ className +" parseJSON(JSONObject o) {\n" + 
				"		try {\n" + 
				"			"+ className +" n = new "+ className +"();");
		n = 0;
		for (String s : varNameList) {
			String type = typeList.get(n);
			System.out.println("\t\t\tn."+ s +" = o.get"+ type.substring(0,1).toUpperCase() + type.substring(1) +"(\""+ s +"\");");
			n++;
		}
		System.out.println(
				"			return n;\n" + 
				"		} catch (Exception e) {\n" + 
				"			return null;\n" + 
				"		}\n" + 
				"	}\n");
		
		System.out.println();
		System.out.println("\tpublic String toString() {\n" + 
				"		return toJSON();\n" + 
				"	}");
		System.out.println();
		
		
	}
	
	public static void main(String[] args) throws Exception {
		String s = "long id;\n" + 
				"	\n" + 
				"	long limerBookId;//具体哪本书\n" + 
				"	long borrowRecordId;//哪次借阅相关的物流\n" + 
				"	String goodsName;//物流商品名称\n" + 
				"	\n" + 
				"	long fromUserId;\n" + 
				"	String fromUserName;\n" + 
				"	String fromUserAddress;\n" + 
				"	String fromMobile;\n" + 
				"	\n" + 
				"	long toUserId;\n" + 
				"	String toUserName;\n" + 
				"	String toUserAddress;\n" + 
				"	String toMobile;\n" + 
				"	\n" + 
				"	int status;//订单状态\n" + 
				"	String statusDesc;\n" + 
				"	\n" + 
				"	int price;//运费（分）\n" + 
				"	\n" + 
				"	int logisCompany;//物流公司\n" + 
				"	String logisOrderId;//物流公司订单编号\n" + 
				"	\n" + 
				"	long createTime;\n" + 
				"	long lastUpdateTime;\n" + 
				"	String orderDetail;//订单详细流程";
		String className = "Logistics";
		
		String[] lines = s.split("\n+");
		gen1(lines, className);
		
		/**
		sql = " CREATE INDEX index_"+ TABLENAME +"_mobile" + 
		" ON "+ TABLENAME +" (mobile)";
		dbUtils.executeSql(sql, null);
		
		public User getUserByWxOpenId(String wxOpenId) {
			String sql = "SELECT * FROM " + TABLENAME + " " 
					+ " WHERE wx_open_id=? ";
			return (User)dbUtils.executeQuery(sql, new Object[]{wxOpenId}, new ResultSetHandler(){
				public Object handle(ResultSet rs, Object[] params) throws Exception {
					while (rs.next()) {
						User u = readOneRow(rs);
						return u;
					}
					return null;
				}
			});
		}
		
		 */
	}
	
}
