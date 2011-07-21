package org.nutz.el.opt.custom;

import java.util.List;

import org.nutz.el.opt.RunMethod;
import org.nutz.plugin.Plugin;

/**
 * 取小
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class Min implements RunMethod, Plugin{
	public Object run(List<Object> param) {
		if(param.size() == 1){
			return param.get(0);
		}
		if(param.size() >= 2){
			Number n1 = (Number) param.get(0);
			Number n2 = (Number) param.get(1);
			if(n1 == null){
				return n2;
			}
			if(n2 == null){
				return n1;
			}
			if(n1 instanceof Double || n2 instanceof Double){
				return Math.min(n1.doubleValue(), n2.doubleValue());
			}
			if(n1 instanceof Float || n2 instanceof Float){
				return Math.min(n1.floatValue(), n2.floatValue());
			}
			if(n1 instanceof Long || n2 instanceof Long){
				return Math.min(n1.longValue(), n2.longValue());
			}
			return Math.min(n1.intValue(), n2.intValue());
		}
		return null;
	}

    public boolean canWork() {
        return true;
    }

    public String fetchSelf() {
        return "min";
    }

}
