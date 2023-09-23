package com.chestnut.cms.word.properties;

import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;

import com.chestnut.common.utils.JacksonUtils;
import com.chestnut.common.utils.StringUtils;
import com.chestnut.contentcore.core.IProperty;
import com.chestnut.contentcore.util.ConfigPropertyUtils;

/**
 * 应用热词分组IDs
 */
@Component(IProperty.BEAN_NAME_PREFIX + HotWordGroupsProperty.ID)
public class HotWordGroupsProperty implements IProperty {

	public final static String ID = "HotWordGroups";
	
	static UseType[] UseTypes = new UseType[] { UseType.Site, UseType.Catalog };
	
	private static final String[] DEFAULT_VALUE = {};
	
	@Override
	public UseType[] getUseTypes() {
		return UseTypes;
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getName() {
		return "热词分组";
	}
	
	@Override
	public String[] defaultValue() {
		return DEFAULT_VALUE;
	}
	
	@Override
	public String[] getPropValue(Map<String, String> configProps) {
		String string = MapUtils.getString(configProps, getId());
		if (StringUtils.isNotEmpty(string)) {
			return JacksonUtils.from(string, String[].class);
		}
		return defaultValue();
	}
	
	public static String[] getHotWordGroupCodes(Map<String, String> firstProps, Map<String, String> secondProps) {
		String propValue = ConfigPropertyUtils.getStringValue(ID, firstProps, secondProps);
		if (StringUtils.isNotEmpty(propValue)) {
			return JacksonUtils.from(propValue, String[].class);
		}
		return DEFAULT_VALUE;
	}
}
