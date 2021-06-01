package extention;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;

import java.text.SimpleDateFormat;
import java.util.*;

public class BeanArgumentResolver implements WebArgumentResolver {

	@Override
	@SuppressWarnings("rawtypes")
	public Object resolveArgument(MethodParameter param, NativeWebRequest request){
		RequestBean requestBean = param.getParameterAnnotation(RequestBean.class);
		try{
			if (requestBean != null) {
				String _param = requestBean.value();
				if (_param.equals("_def_param_name")) {
					_param = param.getParameterName();
				}
				Class clazz = param.getParameterType();
				Object object = clazz.newInstance();
				HashMap<String, String[]> paramsMap = new HashMap<String, String[]>();
				Iterator<String> itor = request.getParameterNames();
				while (itor.hasNext()) {
					String webParam = itor.next();
					String[] webValue = request.getParameterValues(webParam);
					List<String> webValueList = new ArrayList<String>();
					for(int i = 0;i<webValue.length;i++){
						if(webValue[i]!=null&&!"".equals(webValue[i])){
							webValueList.add(webValue[i]);
						}
					}
					if (webParam.startsWith(_param)&&!webValueList.isEmpty()) {
						paramsMap.put(webParam, webValueList.toArray(new String[webValueList.size()]));
					}
				}
				BeanWrapper obj = new BeanWrapperImpl(object);
				obj.registerCustomEditor(Date.class, null, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), true));
				System.out.println(obj.findCustomEditor(Date.class, null).getAsText());

				for (String propName : paramsMap.keySet()) {
					Object propVals = paramsMap.get(propName);
					String[] props = propName.split("\\.");
					if (props.length == 2) {
						obj.setPropertyValue(props[1], propVals);
					} else if (props.length == 3) {
						Object tmpObj = obj.getPropertyValue(props[1]);
						if (tmpObj == null)
							obj.setPropertyValue(props[1], obj.getPropertyType(props[1]).newInstance());
						obj.setPropertyValue(props[1] + "." + props[2], propVals);
					}

				}
				return object;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return WebArgumentResolver.UNRESOLVED;
	}

}
