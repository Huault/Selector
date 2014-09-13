package com.jahia.huault.selector;

import java.util.List;
import java.util.Map;

import javax.jcr.NodeIterator;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.servlet.http.HttpServletRequest;

import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.services.content.JCRNodeIteratorWrapper;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRPropertyWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.JCRTemplate;
import org.jahia.services.content.JCRValueWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;

public class SelectorAction extends Action{

	JCRTemplate jcrTemplate;
	
	 public void setJcrTemplate(JCRTemplate jcrTemplate) {
		 this.jcrTemplate = jcrTemplate;
}
	
	@Override
	public ActionResult doExecute(HttpServletRequest req,
			RenderContext renderContext, Resource resource,
			JCRSessionWrapper session, Map<String, List<String>> parameters,
			URLResolver urlResolver) throws Exception {
			
			System.out.println("DEBUG 1");
			
			JCRNodeWrapper node = resource.getNode();
			
			QueryManager q = session.getWorkspace().getQueryManager();
			String sql = "select * from [jmix:selectable]" ;
            QueryResult qr = q.createQuery(sql, Query.JCR_SQL2).execute();
            NodeIterator ni = qr.getNodes();
            while (ni.hasNext()) {
                JCRNodeWrapper content = (JCRNodeWrapper) ni.nextNode();
                System.out.println("Debug 2 : " + content);
                if (content.hasProperty("selectors")){
                	
                	JCRPropertyWrapper selectors = (JCRPropertyWrapper) content.getProperty("selectors");
                	JCRValueWrapper[] iter = selectors.getValues();
                	for (int i = 0; i < iter.length; i++) {
                		JCRValueWrapper selector = iter[i];
                		System.out.println("Debug 3 : " + selector.getNode().getIdentifier() + " : " + node.getIdentifier());
						if (selector.getNode().getIdentifier().equals(node.getIdentifier())){

							JCRNodeIteratorWrapper rules = content.getNodes();
							System.out.println("Debug 4 " + rules.getSize());
							while (rules.hasNext()) {
								JCRNodeWrapper rule = (JCRNodeWrapper) rules.next();
								
								System.out.println("Debug 5 " + rule);
								if (rule.isNodeType("jnt:rule")){
									JCRNodeWrapper questionRule = (JCRNodeWrapper) rule.getProperty("question_rule").getNode();
									System.out.println("Debug 6 " + questionRule);
									JCRNodeIteratorWrapper nodes = node.getNodes();
									while (nodes.hasNext()) {
										JCRNodeWrapper questionSelector = (JCRNodeWrapper) nodes.next();
										String response = parameters.get(questionSelector.getName()).get(0);
										System.out.println("Debug  " +questionSelector.getName() + " : " + response);
										if (questionRule.getIdentifier().equals(questionSelector.getIdentifier())){
											String ruleValue = rule.getPropertyAsString("value");
											System.out.println("Debug compare   " +ruleValue + " to " + response);
											if (ruleValue.equals(response)){
												System.out.println("MATCH !!!");
											}
										}
									}
								}
							}
						}
					}
                }
            }
			
		return new ActionResult(ActionResult.OK.getResultCode());
	}
	
	
	
	

}
