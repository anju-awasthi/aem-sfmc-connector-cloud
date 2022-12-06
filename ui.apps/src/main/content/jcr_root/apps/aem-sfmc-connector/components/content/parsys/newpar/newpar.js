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

var global = this;

use(function () {
    
    var _retrieveAttribute = function (attrName, defaultValue) {
        var value = defaultValue;
        if (global.request
                && global.request.getAttribute
                && global.request.getAttribute(attrName)) {
            value = global.request.getAttribute(attrName);
        }
        
        return value;
    }
    
    var _getEditContext = function () {
        var editContext = undefined;
        var componentContext = _retrieveAttribute("com.day.cq.wcm.componentcontext");
        if (componentContext) {
            editContext = componentContext.getEditContext();
        }
        
        return editContext;
    }
    
    var editContext = _getEditContext();
    
    if (editContext
            && editContext.getParent
            && editContext.getParent() != null) {
        var parentContext = editContext.getParent();
        
        var curRes = editContext.getParent().getAttribute("currentResource");
        if (curRes != null) {
            var prev = global.Packages.com.day.text.Text.getName(curRes.getPath());
            editContext.getEditConfig().setInsertBehavior("before " + prev);
        }
    }
    
    return {};
});