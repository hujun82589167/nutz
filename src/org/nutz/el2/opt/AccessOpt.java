package org.nutz.el2.opt;

import java.util.Queue;

import org.nutz.el2.obj.IdentifierObj;

/**
 * 访问符:'.'
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class AccessOpt extends AbstractOpt {
	private Object left;
	private Object right;
	
	

	public int fetchPriority() {

		return 1;
	}

	public void wrap(Queue<Object> operand) {
		this.right = operand.poll();
		this.left = operand.poll();
	}

	public Object calculate() {
		if(left instanceof AccessOpt){
			left = ((AccessOpt) left).fetchVar();
		}
		return new Object[]{left, right};
	}
	
	/**
	 * 执行方法
	 * @param var 方法参数
	 */
	public void runMethod(Object... var){
		
	}
	/**
	 * 取得变得的值
	 * @return 
	 */
	public Object fetchVar(){
		if(left instanceof AccessOpt){
			return ((AccessOpt)left).fetchVar();
		}
		if(left instanceof IdentifierObj){
			return ((IdentifierObj) left).fetchVal();
		}
		//@ JKTODO 添加属性读取
		//@ JKTODO 添加包引用
		return left;
	}

	public OptEnum fetchSelf() {
		return OptEnum.ACCESS;
	}

}
