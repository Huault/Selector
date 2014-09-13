package com.jahia.huault.selector;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRPropertyWrapper;
import org.jahia.services.content.JCRValueWrapper;
import org.jahia.services.content.nodetypes.ExtendedPropertyDefinition;
import org.jahia.services.content.nodetypes.initializers.ChoiceListValue;
import org.jahia.services.content.nodetypes.initializers.ModuleChoiceListInitializer;

public class QuestionChoiceList implements ModuleChoiceListInitializer {

	String key ;
	
	@Override
	public List<ChoiceListValue> getChoiceListValues(ExtendedPropertyDefinition epd, String param, List<ChoiceListValue> values, Locale locale, Map<String, Object> context) {
		
		JCRNodeWrapper content = (JCRNodeWrapper) context.get("contextParent");
		System.out.println("DEBUG 1 " + content);
		if (content == null) {
			 content = (JCRNodeWrapper) context.get("contextNode");
	    }
		
		System.out.println("DEBUG 2 " + content);
        final ArrayList<ChoiceListValue> listValues = new ArrayList<ChoiceListValue>();
        
        try {
        	while (!content.hasProperty("selectors") && (content.getParent() != null)){
    			content = content.getParent();
    		}
			
			System.out.println("Debug 2 : " + content);
	        if (content.hasProperty("selectors")){
	         	
	         	JCRPropertyWrapper selectors = (JCRPropertyWrapper) content.getProperty("selectors");
	         	JCRValueWrapper[] iter = selectors.getValues();
	         	for (int i = 0; i < iter.length; i++) {
	         		JCRValueWrapper selector = iter[i];
	         		NodeIterator nodeIterator = selector.getNode().getNodes();
	    			while (nodeIterator.hasNext()) {
	    				JCRNodeWrapper question = (JCRNodeWrapper) nodeIterator.next();
	    				if (question.isNodeType("jmix:question")) {
	    					String displayName = question.getDisplayableName();
	    					listValues.add(new ChoiceListValue(displayName, question.getIdentifier()));
	    				}
	    				
	    			}
	         		
	         	}
	         }
			
			
			
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		

		return listValues;
	}

	@Override
	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public String getKey() {
		return this.key;
	}

}
