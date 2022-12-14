/*  ADOBE CONFIDENTIAL
  __________________

   Copyright 2014 Adobe Systems Incorporated
   All Rights Reserved.

  NOTICE:  All information contained herein is, and remains
  the property of Adobe Systems Incorporated and its suppliers,
  if any.  The intellectual and technical concepts contained
  herein are proprietary to Adobe Systems Incorporated and its
  suppliers and are protected by trade secret or copyright law.
  Dissemination of this information or reproduction of this material
  is strictly forbidden unless prior written permission is obtained
  from Adobe Systems Incorporated.
*/

"use strict";

/**
 * Parsys sightly foundation component JS backing script
 */
use(["/libs/wcm/foundation/components/utils/ParagraphSystem.js"], function (ParagraphSystem) {
    var newParResourceType = granite.resource.resourceType + "/newpar";
    
    if(wcmmode.disabled) {
        request.setAttribute(Packages.com.day.cq.wcm.api.components.ComponentContext.BYPASS_COMPONENT_HANDLING_ON_INCLUDE_ATTRIBUTE, true); 
    }
    
    var parsys = new ParagraphSystem(granite.resource, newParResourceType, false);
    
    return {
        renderInfo: parsys.getParagraphs()
    };
});
