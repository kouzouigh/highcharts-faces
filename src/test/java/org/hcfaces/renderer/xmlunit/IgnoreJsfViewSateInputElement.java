package org.hcfaces.renderer.xmlunit;

import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceConstants;
import org.custommonkey.xmlunit.DifferenceListener;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

public class IgnoreJsfViewSateInputElement implements DifferenceListener {

	public int differenceFound(Difference difference) {
		if( difference.getId() == DifferenceConstants.ATTR_VALUE_ID ) {
			Attr attribute = getCurrentAttribute(difference);
			if( isAttributeOfJsfViewStateInputElement(attribute) ) {
				return  RETURN_IGNORE_DIFFERENCE_NODES_SIMILAR;
			}
		}
		return RETURN_ACCEPT_DIFFERENCE;
	};
	
	public boolean isAttributeOfJsfViewStateInputElement(Attr attribute) {
		return "value".equals(attribute.getName()) && 
			   "javax.faces.ViewState".equals(attribute.getOwnerElement().getAttribute("id"));
	}
	
	public Attr getCurrentAttribute(Difference difference) {
		return (Attr) difference.getControlNodeDetail().getNode();
	}

	@Override
	public void skippedComparison(Node node, Node node1) {
		
	}
}
