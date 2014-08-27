/**
 * @time 2014年8月27日
 * @author sunke
 */
package com.cffex.nogc.memory;

/**
 * @author sunke
 * @ClassName Pojo
 * @Description: 测试用pojo 
 */
public class Pojo {
	private int age;
	private String name;
	private long id;
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	/**
	 * @param age
	 * @param name
	 * @param id
	 */
	public Pojo(int age, String name, long id) {
		super();
		this.age = age;
		this.name = name;
		this.id = id;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "id:"+id+" name:"+name+" age:"+age;
	}
	/**
	 * 
	 */
	public Pojo() {
		super();
	}
	
}
